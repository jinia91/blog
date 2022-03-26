package myblog.blog.category.adapter.imcomming;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;

import java.util.List;
import java.util.Objects;


/*
    - List 순환으로 유효성 검사하게하는 커스텀 Validator
*/
@Component
@RequiredArgsConstructor
public class CategoryListValidator implements Validator {

    private final SpringValidatorAdapter springValidatorAdapter;

    @Override
    public boolean supports(Class<?> clazz) {
        return List.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        for(Object object : (List)target){
            springValidatorAdapter.validate(object,errors);
        }
        if (errors.hasErrors()) {
            throw new InvalidCategoryRequestException(Objects.requireNonNull(errors.getFieldError()).getDefaultMessage());
        }
    }
}

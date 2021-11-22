package myblog.blog;

import myblog.blog.category.domain.Category;
import myblog.blog.category.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BlogApplicationTests {

	@Autowired
	CategoryService categoryService;



}

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

	@Test
	void contextLoads() {

		String str = null;
		Long 테스트_부모 = categoryService.createNewCategory("테스트 부모", str);
		categoryService.createNewCategory("테스트 자식","테스트 부모");
	}

}

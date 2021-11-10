package myblog.blog.category.repository;

import myblog.blog.category.dto.CategoryCountForRepository;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface NaCategoryRepository {

    @Select("select ifnull(f.title,'total') as title,ifnull(tier,0) as tier, count\n" +
            "from \n" +
            "(select ifnull(child,parent) as title, count\n" +
            "from\n" +
            "(select c.title 'parent', b.title as 'child' , count(*) as 'count'\n" +
            "from article a\n" +
            "join category b on (a.category_id = b.category_id)\n" +
            "left join category c on (b.parents_id = c.category_id)\n" +
            "group by parent, child with rollup) d\n" +
            ") e\n" +
            "left join category f on (e.title = f.title)")
    List<CategoryCountForRepository> getCategoryCount();

}

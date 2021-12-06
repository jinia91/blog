package myblog.blog.category.repository;

import myblog.blog.category.dto.CategoryNormalDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface NaCategoryRepository {

    /*
        - 카테고리별 아티클 갯수 통계 쿼리
    */
    @Select("select ifnull(f.title,'total') as title, ifnull(tier,0) as tier, ifnull(f.category_id, 0) as id, ifnull(count,0) as count, ifnull(f.p_sort_num, 0) as pOrder, ifnull(f.c_sort_num, 0) as cOrder\n" +
            "            from \n" +
            "            (select ifnull(ifnull(b.title, c.title),'total') as title, count(*) as 'count'\n" +
            "            from article a\n" +
            "            join category b on (a.category_id = b.category_id)\n" +
            "            left join category c on (b.parents_id = c.category_id)\n" +
            "            group by c.title, b.title with rollup) e\n" +
            "            right join category f on (e.title = f.title)\n" +
            "            order by pOrder, cOrder ")
    List<CategoryNormalDto> getCategoryCount();

}

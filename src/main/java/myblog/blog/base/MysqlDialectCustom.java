package myblog.blog.base;

import org.hibernate.dialect.MySQL57Dialect;
import org.hibernate.dialect.MySQL8Dialect;
import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.type.StandardBasicTypes;


/*
    - FTS 지원을 위해 JPA에 SQL에 MYSQL 문법 추가 등록
*/
public class MysqlDialectCustom extends MySQL8Dialect {

    public MysqlDialectCustom(){
        super();

        registerFunction(
                "match", new SQLFunctionTemplate(StandardBasicTypes.DOUBLE, "match(?1) against (?2 in boolean mode)"
                ));
    }
}

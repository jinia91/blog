package myblog.blog.base.config;

import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class AppConfig {

    /*
        - DTO <-> 엔티티 매퍼 빈등록
    */
    @Bean
    public ModelMapper modelMapper(){
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
                .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE)
                .setFieldMatchingEnabled(true);
        return modelMapper;
    }

    /*
        - HTML -> 마크다운 파싱 & 렌더러 빈등록
    */
    @Bean
    public Parser parser(){
        return Parser.builder()
            .extensions(List.of(TablesExtension.create()))
            .build();
    }

    @Bean
    public HtmlRenderer htmlRenderer(){
        return HtmlRenderer.builder()
            .extensions(List.of(TablesExtension.create()))
            .build();
    }
}

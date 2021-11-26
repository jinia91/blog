package myblog.blog.base.config;

import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class AppConfig {

    @Bean
    public ModelMapper modelMapper(){ return new ModelMapper();}

    @Bean
    public Parser parser(){return Parser.builder()
            .extensions(Arrays.asList(TablesExtension.create()))
            .build();}

    @Bean
    public HtmlRenderer htmlRenderer(){return HtmlRenderer.builder()
            .extensions(Arrays.asList(TablesExtension.create()))
            .build();}


}

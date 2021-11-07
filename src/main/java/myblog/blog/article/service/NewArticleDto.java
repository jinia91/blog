package myblog.blog.article.service;

import myblog.blog.member.doamin.Member;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

public class NewArticleDto {

    @NotBlank
    private String title;
    @NotBlank
    private String content;
    private String toc;
    @NotBlank
    private Long memberId;

}

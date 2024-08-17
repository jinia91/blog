package kr.co.jiniaslog.blog.usecase.article

import kr.co.jiniaslog.blog.domain.article.ArticleId
import kr.co.jiniaslog.blog.domain.tag.TagName

/**
 * 나는 어떤 태그라도 게시글에 추가할 수 있다
 *
 * - 태그 생성 여부와 상관없이 태그 이름이 주어지면 게시글에 태그를 추가하는 유즈케이스
 * - 태그가 없을경우 생성한다
 *
 */
interface IAddAnyTagInArticle {
    fun handle(command: Command): Info

    data class Command(
        val tagName: TagName,
        val articleId: ArticleId,
    )

    data class Info(
        val articleId: ArticleId,
    )
}

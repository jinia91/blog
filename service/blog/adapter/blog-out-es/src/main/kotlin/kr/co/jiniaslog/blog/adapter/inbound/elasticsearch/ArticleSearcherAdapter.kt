package kr.co.jiniaslog.blog.adapter.inbound.elasticsearch

import co.elastic.clients.elasticsearch._types.query_dsl.TextQueryType
import kr.co.jiniaslog.blog.domain.article.PublishedArticleVo
import kr.co.jiniaslog.blog.outbound.ArticleSearcher
import org.springframework.data.domain.PageRequest
import org.springframework.data.elasticsearch.client.elc.NativeQuery
import org.springframework.data.elasticsearch.core.ElasticsearchOperations
import org.springframework.stereotype.Component

@Component
class ArticleSearcherAdapter(
    private val elasticsearchOperations: ElasticsearchOperations
) : ArticleSearcher {
    /**
     *
     * Published 상태의 아티클을 키워드로 검색
     *
     * 검색전략은 title을 2배 가중치로 주고 contents도 검색하여 둘중 하나라도 포함되어 있으면 검색결과로 반환
     *
     * 역인덱싱 방식은 한글 형태소 분석기를 사용하여 검색
     *
     * 페이지네이션은 일단 기본 50개로 제한
     *
     */
    override fun searchPublishedArticlesByKeyword(keyword: String): List<PublishedArticleVo> {
        val query = NativeQuery.builder()
            .withQuery { q ->
                q.bool { b ->
                    b.must { m ->
                        m.match { mm -> mm.query("PUBLISHED").field("STATUS") }
                    }
                    b.must { m ->
                        m.multiMatch { mm ->
                            mm.query(keyword)
                                .fields("TITLE^2", "CONTENTS")
                                .type(TextQueryType.MostFields)
                        }
                    }
                }
            }
            .withPageable(PageRequest.of(0, 30))
            .build()

        val result = elasticsearchOperations.search(query, PublishedArticleDocument::class.java)
        return result.map { it.content.toArticleVo() }.toList()
    }
}

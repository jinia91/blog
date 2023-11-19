package kr.co.jiniaslog.blog.adapter.out.rdb.articleview

import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kr.co.jiniaslog.blog.domain.article.ArticleId
import kr.co.jiniaslog.blog.domain.articleview.ArticleView
import kr.co.jiniaslog.blog.domain.articleview.ArticleViewRepository
import org.springframework.stereotype.Repository

@Repository
class ArticleViewRepositoryAdapter(
    private val articleViewRdbRepository: ArticleViewRdbRepository,
) : ArticleViewRepository {
    override suspend fun findById(id: ArticleId): ArticleView? {
        return articleViewRdbRepository.findById(id.value)?.toDomain()
    }

    override suspend fun findAll(): List<ArticleView> {
        return articleViewRdbRepository.findAll().map { it.toDomain() }.toList()
    }

    override suspend fun deleteById(id: ArticleId) {
        articleViewRdbRepository.deleteById(id.value)
    }

    override suspend fun save(entity: ArticleView): ArticleView {
        return articleViewRdbRepository.save(entity.toPM()).toDomain()
    }
}

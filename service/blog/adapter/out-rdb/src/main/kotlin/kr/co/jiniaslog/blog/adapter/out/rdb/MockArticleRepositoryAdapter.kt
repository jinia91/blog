package kr.co.jiniaslog.blog.adapter.out.rdb

import kr.co.jiniaslog.blog.domain.article.Article
import kr.co.jiniaslog.blog.domain.article.ArticleId
import kr.co.jiniaslog.blog.domain.article.ArticleRepository
import kr.co.jiniaslog.shared.core.annotation.PersistenceAdapter
import kr.co.jiniaslog.shared.core.domain.IdGenerator

@PersistenceAdapter
internal class MockArticleRepositoryAdapter(
    private val idGenerator: IdGenerator,
    private val repo: ArticleRdbRepository,
) : ArticleRepository {
    override suspend fun nextId(): ArticleId {
        idGenerator.generate().let {
            return ArticleId(it)
        }
    }

    override suspend fun findById(id: ArticleId): Article {
        TODO("Not yet implemented")
    }

    override suspend fun findAll(): List<Article>? {
        TODO("Not yet implemented")
    }

    override suspend fun deleteById(id: ArticleId) {
        TODO("Not yet implemented")
    }

    override suspend fun save(entity: Article): Article {
        repo.save(
            ArticlePm(
                id = entity.id.value,
            ),
        )
        return entity
    }
}

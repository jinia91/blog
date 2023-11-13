package kr.co.jiniaslog.blog.adapter.out.rdb.article

import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kr.co.jiniaslog.blog.domain.article.Article
import kr.co.jiniaslog.blog.domain.article.ArticleId
import kr.co.jiniaslog.blog.domain.article.ArticleRepository
import kr.co.jiniaslog.shared.adapter.out.rdb.isNew
import kr.co.jiniaslog.shared.core.annotation.PersistenceAdapter
import kr.co.jiniaslog.shared.core.domain.IdManager

@PersistenceAdapter
internal class ArticleRepositoryAdapter(
    private val articleRepo: ArticleRdbRepository,
    private val commitRepo: ArticleCommitRdbRepository,
) : ArticleRepository {
    override suspend fun nextId(): ArticleId {
        IdManager.generate().let {
            return ArticleId(it)
        }
    }

    override suspend fun findById(id: ArticleId): Article? {
        val articlePm = articleRepo.findById(id.value) ?: return null
        val commits = commitRepo.findAllByArticleId(id.value).map { it.toEntity() }.toMutableList()
        return articlePm.toEntity(commits)
    }

    override suspend fun findAll(): List<Article> {
        articleRepo.findAll().map {
            val commits = commitRepo.findAllByArticleId(it.id).map { it.toEntity() }.toMutableList()
            it.toEntity(commits)
        }.let {
            return it.toList()
        }
    }

    override suspend fun deleteById(id: ArticleId) {
        articleRepo.deleteById(id.value)
        commitRepo.deleteAllByArticleId(id.value)
    }

    override suspend fun save(entity: Article): Article {
        val persistedCommitEntity =
            entity.history
                .map {
                    if (it.isNew()) {
                        commitRepo.save(it.toPM(entity.id.value)).toEntity()
                    } else {
                        it
                    }
                }.toMutableList()
        val refreshedArticlePm = articleRepo.save(entity.toPM())
        return refreshedArticlePm.toEntity(persistedCommitEntity)
    }
}

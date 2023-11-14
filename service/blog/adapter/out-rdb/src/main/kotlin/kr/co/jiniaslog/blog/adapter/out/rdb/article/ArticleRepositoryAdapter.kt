package kr.co.jiniaslog.blog.adapter.out.rdb.article

import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kr.co.jiniaslog.blog.domain.article.Article
import kr.co.jiniaslog.blog.domain.article.ArticleId
import kr.co.jiniaslog.blog.domain.article.ArticleRepository
import kr.co.jiniaslog.shared.adapter.out.rdb.isNew
import kr.co.jiniaslog.shared.core.annotation.PersistenceAdapter
import kr.co.jiniaslog.shared.core.domain.FetchMode
import kr.co.jiniaslog.shared.core.domain.IdManager

@PersistenceAdapter
internal class ArticleRepositoryAdapter(
    private val articleRepo: ArticleRdbRepository,
    private val commitRepo: ArticleCommitRdbRepository,
    private val stagingRepo: ArticleStagingSnapShotRdbRepository,
    private val articleFactory: ArticleFactory,
) : ArticleRepository {
    override suspend fun nextId(): ArticleId {
        IdManager.generate().let {
            return ArticleId(it)
        }
    }

    override suspend fun findById(
        id: ArticleId,
        mode: FetchMode,
    ): Article? {
        when (mode) {
            FetchMode.NONE -> {
                return articleRepo.findById(id.value)?.let { articleFactory.assemble(articlePM = it) }
            }

            FetchMode.ALL -> {
                val article = articleRepo.findById(id.value) ?: return null
                return fetchedAllArticle(article)
            }
        }
    }

    override suspend fun findAll(mode: FetchMode): List<Article> {
        articleRepo.findAll().map {
            when (mode) {
                FetchMode.NONE -> {
                    articleFactory.assemble(articlePM = it)
                }
                FetchMode.ALL -> {
                    fetchedAllArticle(it)
                }
            }
        }.let {
            return it.toList()
        }
    }

    // todo : join 쿼리로 최적화
    private suspend fun fetchedAllArticle(article: ArticlePM): Article {
        val commits = commitRepo.findAllByArticleId(article.id).map { it.toEntity() }.toMutableList()
        val stagingSnapShot = stagingRepo.findById(article.id)?.toEntity()
        return articleFactory.assemble(
            articlePM = article,
            commits = commits,
            stagingSnapShot = stagingSnapShot,
        )
    }

    override suspend fun deleteById(id: ArticleId) {
        articleRepo.deleteById(id.value)
        commitRepo.deleteAllByArticleId(id.value)
        stagingRepo.deleteById(id.value)
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
        val stagingRepoEntity =
            entity.stagingSnapShot?.let {
                stagingRepo.save(it.toPM()).toEntity()
            }
        val refreshedArticlePm = articleRepo.save(entity.toPM())
        return articleFactory.assemble(
            articlePM = refreshedArticlePm,
            commits = persistedCommitEntity,
            stagingSnapShot = stagingRepoEntity,
        )
    }
}

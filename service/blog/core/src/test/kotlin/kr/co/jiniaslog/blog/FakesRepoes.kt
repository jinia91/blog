package kr.co.jiniaslog.blog

import kr.co.jiniaslog.blog.domain.article.Article
import kr.co.jiniaslog.blog.domain.article.ArticleId
import kr.co.jiniaslog.blog.domain.article.ArticleRepository
import kr.co.jiniaslog.blog.domain.articleview.ArticleView
import kr.co.jiniaslog.blog.domain.articleview.ArticleViewRepository
import kr.co.jiniaslog.blog.domain.category.Category
import kr.co.jiniaslog.blog.domain.category.CategoryId
import kr.co.jiniaslog.blog.domain.category.CategoryRepository
import kr.co.jiniaslog.blog.domain.writer.Writer
import kr.co.jiniaslog.blog.domain.writer.WriterId
import kr.co.jiniaslog.blog.domain.writer.WriterName
import kr.co.jiniaslog.blog.domain.writer.WriterProvider
import kr.co.jiniaslog.shared.core.domain.IdUtils

fun fakeArticleRepository() =
    object : ArticleRepository {
        private val map = mutableMapOf<ArticleId, Article>()

        override suspend fun nextId(): ArticleId {
            return ArticleId(IdUtils.generate())
        }

        override suspend fun findById(id: ArticleId): Article? {
            return map[id]
        }

        override suspend fun findAll(): List<Article> {
            return map.values.toList()
        }

        override suspend fun deleteById(id: ArticleId) {
            map.remove(id)
        }

        override suspend fun save(entity: Article): Article {
            map[entity.id] = entity
            return entity
        }
    }

fun fakeArticleViewRepository() =
    object : ArticleViewRepository {
        private val map = mutableMapOf<ArticleId, ArticleView>()

        override suspend fun nextId(): ArticleId {
            throw IllegalArgumentException("ArticleViewRepository.nextId() is not supported")
        }

        override suspend fun findById(id: ArticleId): ArticleView? {
            return map[id]
        }

        override suspend fun findAll(): List<ArticleView> {
            return map.values.toList()
        }

        override suspend fun deleteById(id: ArticleId) {
            map.remove(id)
        }

        override suspend fun save(entity: ArticleView): ArticleView {
            map[entity.id] = entity
            return entity
        }
    }

fun fakeCategoryRepository() =
    object : CategoryRepository {
        private val map = mutableMapOf<CategoryId, Category>()

        override suspend fun nextId(): CategoryId {
            return CategoryId(IdUtils.generate())
        }

        override suspend fun findById(id: CategoryId): Category? {
            return map[id]
        }

        override suspend fun findAll(): List<Category> {
            return map.values.toList()
        }

        override suspend fun deleteById(id: CategoryId) {
            map.remove(id)
        }

        override suspend fun save(entity: Category): Category {
            map[entity.id] = entity
            return entity
        }
    }

fun fakeWriterProvider() =
    object : WriterProvider {
        override suspend fun getWriter(writerId: WriterId): Writer? {
            return Writer(
                id = writerId,
                name = WriterName("jinia"),
            )
        }

        override suspend fun isExist(writerId: WriterId): Boolean {
            return true
        }
    }

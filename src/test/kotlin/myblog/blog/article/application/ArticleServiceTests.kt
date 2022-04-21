//package myblog.blog.article.application
//
//import myblog.blog.article.application.port.incomming.TagUseCase
//import myblog.blog.article.application.port.outgoing.ArticleBackupRepositoryPort
//import myblog.blog.article.application.port.outgoing.ArticleRepositoryPort
//import myblog.blog.category.appliacation.port.incomming.CategoryUseCase
//import myblog.blog.member.application.port.incomming.MemberQueriesUseCase
//import org.junit.jupiter.api.extension.ExtendWith
//import org.mockito.InjectMocks
//import org.mockito.Mock
//import org.mockito.junit.jupiter.MockitoExtension
//
//@ExtendWith(MockitoExtension::class)
//class ArticleServiceTests {
//    @Mock lateinit var tagUseCase: TagUseCase
//    @Mock lateinit var categoryUseCase: CategoryUseCase
//    @Mock lateinit var memberQueriesUseCase: MemberQueriesUseCase
//    @Mock lateinit var articleRepositoryPort: ArticleRepositoryPort
//    @Mock lateinit var articleBackupRepositoryPort: ArticleBackupRepositoryPort
//    @InjectMocks lateinit var articleService: ArticleService
//
//    fun``(){
//
//    }
//
//}
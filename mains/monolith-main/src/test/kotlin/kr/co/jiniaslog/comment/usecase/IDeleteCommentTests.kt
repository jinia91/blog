// package kr.co.jiniaslog.comment.usecase
//
// import io.kotest.assertions.withClue
// import kr.co.jiniaslog.TestContainerAbstractSkeleton
// import kr.co.jiniaslog.comment.domain.CommentTestFixtures
// import kr.co.jiniaslog.comment.outbound.CommentRepository
// import org.junit.jupiter.api.Test
// import org.springframework.beans.factory.annotation.Autowired
//
// class IDeleteCommentTests : TestContainerAbstractSkeleton() {
//    @Autowired
//    private lateinit var sut : IDeleteComment
//
//    @Autowired
//    private lateinit var commentRepository: CommentRepository
//
//    @Test
//    fun `익명 유저는 패스워드가 일치하면 자신의 댓글을 삭제할 수 있다` () {
//        // given
//        val asIsComment = withClue("익명 유저 기존 댓글이 존재한다") {
//            commentRepository.save(
//                CommentTestFixtures.createAnonymousComment()
//            )
//        }
//        val command = IDeleteComment.Command(
//            asIsComment.entityId,
//            null,
//            asIsComment
//        )
//
//        // when
//
//        sut.handle()
//    }
// }

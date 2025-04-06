package kr.co.jiniaslog.comment.queries

import kr.co.jiniaslog.comment.domain.Comment
import kr.co.jiniaslog.comment.domain.CommentId
import kr.co.jiniaslog.comment.domain.CommentVo
import kr.co.jiniaslog.comment.outbound.CommentRepository
import org.springframework.stereotype.Component

@Component
class CommentQueries(
    private val commentRepository: CommentRepository,
) : CommentQueriesFacade {
    override fun handle(command: IGetHierarchyCommentsByRef.Command): IGetHierarchyCommentsByRef.Info {
        val flattenedComments = commentRepository.findByRefIdAndRefType(
            refId = command.refId,
            refType = command.refType
        )

        val hierarchy = buildHierarchy(flattenedComments)
        return IGetHierarchyCommentsByRef.Info(
            comments = hierarchy,
        )
    }

    private fun buildHierarchy(flatList: List<Comment>): List<CommentVo> {
        val voMap = mutableMapOf<CommentId, CommentVo>()
        val roots = mutableListOf<CommentVo>()

        for (comment in flatList) {
            voMap[comment.id] = CommentVo.from(comment)
        }

        for (comment in flatList) {
            val vo = voMap[comment.id] ?: continue
            val parentId = comment.parentId

            if (parentId != null) {
                val parentVo = voMap[parentId]
                parentVo?.children?.add(vo)
            } else {
                roots.add(vo)
            }
        }

        fun sortRecursively(comments: List<CommentVo>) {
            comments.sorted().forEach {
                sortRecursively(it.children)
            }
            (comments as MutableList).sort()
        }

        sortRecursively(roots)

        return roots
    }
}

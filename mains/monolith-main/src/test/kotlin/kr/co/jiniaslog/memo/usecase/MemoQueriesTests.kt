package kr.co.jiniaslog.memo.usecase

import kr.co.jiniaslog.TestContainerAbstractSkeleton
import kr.co.jiniaslog.memo.outbound.FolderRepository
import kr.co.jiniaslog.memo.outbound.MemoRepository
import kr.co.jiniaslog.memo.queries.MemoQueriesFacade
import org.springframework.beans.factory.annotation.Autowired

class MemoQueriesTests : TestContainerAbstractSkeleton() {
    @Autowired
    lateinit var sut: MemoQueriesFacade

    @Autowired
    lateinit var folderRepository: FolderRepository

    @Autowired
    lateinit var memoRepository: MemoRepository
}

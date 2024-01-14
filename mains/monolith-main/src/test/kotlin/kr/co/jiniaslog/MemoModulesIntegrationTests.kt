package kr.co.jiniaslog

import kr.co.jiniaslog.memo.outbound.FolderRepository
import kr.co.jiniaslog.memo.outbound.MemoRepository
import kr.co.jiniaslog.memo.usecase.AbstractFolderUseCaseTests
import kr.co.jiniaslog.memo.usecase.MemoAbstractUseCaseTest
import org.junit.jupiter.api.Nested
import org.springframework.beans.factory.annotation.Autowired

class MemoModulesIntegrationTests : TestContainerAbstractSkeleton() {
    @Nested
    inner class FolderUseCaseNeo4jTest
        @Autowired
        constructor(
            folderRepository: FolderRepository,
        ) : AbstractFolderUseCaseTests(
                folderRepository = folderRepository,
            )

    @Nested
    inner class MemoUseCaseNeo4jTest
        @Autowired
        constructor(
            memoRepository: MemoRepository,
            folderRepository: FolderRepository,
        ) : MemoAbstractUseCaseTest(
                memoRepository = memoRepository,
                folderRepository = folderRepository,
            )
}

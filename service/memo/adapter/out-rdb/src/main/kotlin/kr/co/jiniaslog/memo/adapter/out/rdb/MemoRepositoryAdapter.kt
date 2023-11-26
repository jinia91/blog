package kr.co.jiniaslog.memo.adapter.out.rdb

import kr.co.jiniaslog.memo.domain.memo.Memo
import kr.co.jiniaslog.memo.domain.memo.MemoId
import kr.co.jiniaslog.memo.domain.memo.MemoRepository
import kr.co.jiniaslog.shared.core.annotation.PersistenceAdapter
import kr.co.jiniaslog.shared.core.domain.IdGenerator
import kotlin.jvm.optionals.getOrNull

@PersistenceAdapter
internal class MemoRepositoryAdapter(
    private val memoPMRepository: MemoPMRepository,
    private val idGenerator: IdGenerator,
) : MemoRepository {
    override fun nextId(): MemoId {
        return MemoId(idGenerator.generate())
    }

    override fun findById(id: MemoId): Memo? {
        return memoPMRepository.findById(id.value).getOrNull()?.toDomain()
    }

    override fun findAll(): List<Memo> {
        return memoPMRepository.findAll().map { it.toDomain() }
    }

    override fun deleteById(id: MemoId) {
        memoPMRepository.deleteById(id.value)
    }

    override fun save(entity: Memo): Memo {
        return memoPMRepository.save(entity.toPm()).toDomain()
    }
}

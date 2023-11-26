package kr.co.jiniaslog.memo.domain.tag

import kr.co.jiniaslog.shared.core.domain.Repository

interface TagRepository : Repository<Tag, TagId> {
    fun findByName(name: TagName): Tag?
}

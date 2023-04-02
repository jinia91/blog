package kr.co.jiniaslog.shared.persistence.id

interface ServerIdAllocator {
    fun allocate(): Int
}

package kr.co.jiniaslog.shared.persistence.id

interface IdGenerator {
    fun generate(): Long
}

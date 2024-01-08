package kr.co.jiniaslog.user.adapter.out.mysql.auth

import org.springframework.data.jpa.repository.JpaRepository

interface TokenRepository : JpaRepository<TokenJpaEntity, Long>

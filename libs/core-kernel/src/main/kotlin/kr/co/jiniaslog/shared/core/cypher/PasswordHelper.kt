package kr.co.jiniaslog.shared.core.cypher

import at.favre.lib.crypto.bcrypt.BCrypt

object PasswordHelper {
    fun encode(value: String): String {
        val hashed = BCrypt.withDefaults().hashToString(12, value.toCharArray())
        return hashed
    }

    fun matches(value: String, hashedStr: String): Boolean {
        val result = BCrypt.verifyer().verify(value.toCharArray(), hashedStr)
        return result.verified
    }
}

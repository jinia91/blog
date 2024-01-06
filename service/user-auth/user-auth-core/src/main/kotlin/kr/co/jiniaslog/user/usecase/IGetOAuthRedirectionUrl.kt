package kr.co.jiniaslog.user.usecase

import kr.co.jiniaslog.user.domain.auth.Provider
import kr.co.jiniaslog.user.domain.auth.Url

interface IGetOAuthRedirectionUrl {
    fun handle(command: Command): Info

    data class Command(
        val provider: Provider,
    )

    data class Info(
        val url: Url,
    )
}

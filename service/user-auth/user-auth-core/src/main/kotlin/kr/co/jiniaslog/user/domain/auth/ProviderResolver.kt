package kr.co.jiniaslog.user.domain.auth

import kr.co.jiniaslog.shared.core.annotation.DomainService

@DomainService
class ProviderResolver(
    private val providerAdapters: List<ProviderAdapter>,
) {
    fun isSupported(provider: Provider): Boolean {
        return providerAdapters.any { it.provider == provider }
    }

    fun resolve(provider: Provider): ProviderAdapter {
        require(isSupported(provider)) { "not supported provider. provider: $provider" }
        return providerAdapters.find { it.provider == provider }!!
    }
}

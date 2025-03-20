package kr.co.jiniaslog.user.application.infra

import kr.co.jiniaslog.user.domain.auth.provider.Provider
import org.springframework.stereotype.Component

@Component
class ProviderResolver(
    private val providerClients: List<ProviderClient>,
) {
    fun isSupported(provider: Provider): Boolean {
        return providerClients.any { it.provider == provider }
    }

    fun resolve(provider: Provider): ProviderClient {
        require(isSupported(provider)) { "not supported provider. provider: $provider" }
        return providerClients.find { it.provider == provider }!!
    }
}

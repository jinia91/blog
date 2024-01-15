package kr.co.jiniaslog.user.fakes

import kr.co.jiniaslog.shared.core.annotation.CustomComponent
import kr.co.jiniaslog.user.application.infra.UserAuthTransactionHandler

@CustomComponent
class TestTransactionHandler : UserAuthTransactionHandler {
    override fun <T> runInRepeatableReadTransaction(supplier: () -> T): T {
        return supplier()
    }
}

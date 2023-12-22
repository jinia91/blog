package kr.co.jiniaslog.shared.log

import jakarta.servlet.http.HttpServletRequest

object IpGetHelper {
    fun getClientIP(request: HttpServletRequest): String? {
        var ip = request.getHeader("X-Forwarded-For")
        if (ip == null) {
            ip = request.getHeader("Proxy-Client-IP")
        }
        if (ip == null) {
            ip = request.getHeader("WL-Proxy-Client-IP")
        }
        if (ip == null) {
            ip = request.getHeader("HTTP_CLIENT_IP")
        }
        if (ip == null) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR")
        }
        if (ip == null) {
            ip = request.remoteAddr
        }
        return ip
    }
}

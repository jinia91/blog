package kr.co.jiniaslog.blog.domain.article

import kr.co.jiniaslog.shared.core.domain.ValidationException

class ArticleNotValidException(message: String) : ValidationException(message)

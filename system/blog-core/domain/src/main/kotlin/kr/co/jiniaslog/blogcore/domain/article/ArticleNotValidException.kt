package kr.co.jiniaslog.blogcore.domain.article

import kr.co.jiniaslog.shared.core.domain.ValidationException

class ArticleNotValidException(message: String) : ValidationException(message)

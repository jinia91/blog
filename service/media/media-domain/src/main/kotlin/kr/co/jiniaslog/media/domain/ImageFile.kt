package kr.co.jiniaslog.media.domain

import kr.co.jiniaslog.shared.core.domain.AggregateRoot

class ImageFile(
    override val entityId: ImageId,
    val rawImage: RawImage,
) : AggregateRoot<ImageId>()

package kr.co.jiniaslog.media.outbound.github

import kr.co.jiniaslog.media.domain.ImageFile
import kr.co.jiniaslog.media.domain.ImageUrl
import kr.co.jiniaslog.media.outbound.ImageUploader
import org.kohsuke.github.GitHubBuilder
import org.springframework.stereotype.Component

@Component
internal class GithubImageUploader(
    private val githubProperty: GithubProperty,
) : ImageUploader {
    override fun uploadImage(imageFile: ImageFile): ImageUrl {
        val gitHub = GitHubBuilder().withOAuthToken(githubProperty.gitToken).build()
        gitHub.getRepository(githubProperty.gitRepo)
            .createContent()
            .path("$REPOSITORY_IMAGE_PATH${imageFile.entityId.value}")
            .content(imageFile.rawImage.value)
            .message(COMMIT_MESSAGE)
            .branch(BRANCH)
            .commit()
        return ImageUrl("${githubProperty.rootUrl}${imageFile.entityId.value}?raw=true")
    }

    companion object {
        const val REPOSITORY_IMAGE_PATH = "img/"
        const val COMMIT_MESSAGE = "thumbnail"
        const val BRANCH = "main"
    }
}

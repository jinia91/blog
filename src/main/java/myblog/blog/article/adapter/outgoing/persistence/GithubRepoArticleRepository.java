package myblog.blog.article.adapter.outgoing.persistence;

import myblog.blog.article.adapter.outgoing.model.GithubExternalErrorException;
import myblog.blog.article.domain.Article;
import myblog.blog.shared.domain.ExternalErrorException;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class GithubRepoArticleRepository {

    @Value("${git.gitToken}")
    private String gitToken;
    @Value("${git.repo}")
    private String gitRepo;

    void pushArticleToGithub(Article article) {
        try {
            GitHub gitHub = new GitHubBuilder().withOAuthToken(gitToken).build();
            GHRepository repository = gitHub.getRepository(gitRepo);
            repository.createContent()
                    .path(article.getCategory().getParents().getTitle()+"/"+ article.getCategory().getTitle()+"/"+ article.getTitle()+".md")
                    .content(article.getContent())
                    .message("test")
                    .branch("main")
                    .commit();
        } catch (IOException e) {
            e.printStackTrace();
            throw new GithubExternalErrorException();
        }
    }

}

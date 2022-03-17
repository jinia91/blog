package myblog.blog.imgupload.service;

import lombok.RequiredArgsConstructor;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 깃헙 레포 이미지 업로드 전략 구현체
 */
//@RequiredArgsConstructor
//@Service
public class GithubRepoImgUploadStrategy implements ImgUploadStrategy {

    /*
    - 설정 파일로 잡아놓은 깃헙 이미지 레포지토리와 토큰
*/
    @Value("${git.gitToken}")
    private String gitToken;

    @Value("${git.imgRepo}")
    private String gitRepo;

    @Value("${git.imgUrl}")
    private String rootUrl;

    /*
        - 이미지 업로드 로직
            1. 깃허브 Repo에 이미지 업로드
            2. 업로드된 Url 반환
    */
    @Override
    public String uploadFile(MultipartFile multipartFile, String storeFileName) throws IOException {
        GitHub gitHub = new GitHubBuilder().withOAuthToken(gitToken).build();
        GHRepository repository = gitHub.getRepository(gitRepo);
        repository.createContent().path("img/"+storeFileName)
                .content(multipartFile.getBytes()).message("thumbnail").branch("main").commit();
        return rootUrl + storeFileName + "?raw=true";
    }
}

package myblog.blog.img.service;

import lombok.RequiredArgsConstructor;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UploadImgService {

    /*
        - 설정 파일로 잡아놓은 깃헙 이미지 레포지토리와 토큰
    */
    @Value("${git.gitToken}")
    private String gitToken;

    @Value("${git.imgRepo}")
    private String gitRepo;

    @Value("${git.imgUrl}")
    private String imgUrl;

    /*
        - 이미지 저장 로직
            1. 깃허브 Repo에 이미지 업로드
            2. 업로드된 Url 반환
    */
    public String storeImg(MultipartFile multipartFile) throws IOException {
        if (multipartFile.isEmpty()) {
            throw new IllegalArgumentException("이미지가 존재하지 않습니다.");
        }

        String storeFileName = createStoreFileName(multipartFile.getOriginalFilename());

        GitHub gitHub = new GitHubBuilder().withOAuthToken(gitToken).build();
        GHRepository repository = gitHub.getRepository(gitRepo);
        repository.createContent().path("img/"+storeFileName)
                .content(multipartFile.getBytes()).message("thumbnail").branch("main").commit();

        return imgUrl + storeFileName + "?raw=true";
    }

    /*
        - 이미지 중복 방지용 무작위 파일 이름 생성기
    */
    private String createStoreFileName(String originalFilename) {
        String ext = extractExt(originalFilename);
        String uuid = UUID.randomUUID().toString();
        return uuid + "." + ext;
    }

    /*
        - 파일 이름 추출
    */
    private String extractExt(String originalFilename) {
        int pos = originalFilename.lastIndexOf(".");
        return originalFilename.substring(pos + 1);
    }

}
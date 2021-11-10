package myblog.blog.img.service;

import lombok.RequiredArgsConstructor;
import myblog.blog.img.domain.UploadedImg;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UploadImgService {

    @Value("${git.gitToken}")
    private String gitToken;

    @Value("${git.imgRepo}")
    private String gitRepo;

    @Value("${git.imgUrl}")
    private String imgUrl;

    public String storeImg(MultipartFile multipartFile) throws IOException {
        if (multipartFile.isEmpty()) {
            throw new IllegalArgumentException("이미지가 존재하지 않습니다.");
        }

        GitHub gitHub = new GitHubBuilder().withOAuthToken(gitToken).build();
        GHRepository repository = gitHub.getRepository(gitRepo);

        String originalFilename = multipartFile.getOriginalFilename();
        String storeFileName = createStoreFileName(originalFilename);

        repository.createContent().path("img/"+storeFileName)
                .content(multipartFile.getBytes()).message("test").branch("main").commit();

        UploadedImg uploadedImg = new UploadedImg(originalFilename, storeFileName, imgUrl + storeFileName + "?raw=true");

        return uploadedImg.getUploadUrl();

    }

    private String createStoreFileName(String originalFilename) {
        String ext = extractExt(originalFilename);
        String uuid = UUID.randomUUID().toString();
        return uuid + "." + ext;
    }

    private String extractExt(String originalFilename) {
        int pos = originalFilename.lastIndexOf(".");
        return originalFilename.substring(pos + 1);
    }

}
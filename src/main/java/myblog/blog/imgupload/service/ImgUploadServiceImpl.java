package myblog.blog.imgupload.service;

import lombok.RequiredArgsConstructor;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class ImgUploadServiceImpl {

    private final ImgUploadStrategy imgUploadStrategy;

    public String storeImg(MultipartFile multipartFile) throws IOException {
        validateFile(multipartFile);
        String storeFileName = createStoreFileName(multipartFile.getOriginalFilename());
        return imgUploadStrategy.uploadFile(multipartFile, storeFileName);
    }

    private void validateFile(MultipartFile multipartFile) {
        if (multipartFile.isEmpty()) {
            throw new IllegalArgumentException("이미지가 존재하지 않습니다.");
        }
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
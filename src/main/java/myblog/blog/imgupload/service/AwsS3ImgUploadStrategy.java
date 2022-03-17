package myblog.blog.imgupload.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

/**
 * S3 이미지 업로드 전략 구현체
 */
@RequiredArgsConstructor
@Service
public class AwsS3ImgUploadStrategy implements ImgUploadStrategy {

    private final AmazonS3 amazonS3;
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Override
    public String uploadFile(MultipartFile file, String storeFileName) {
        ObjectMetadata metadata = createObjectMetadata(file);
        try(InputStream inputStream = file.getInputStream()) {
            amazonS3.putObject(new PutObjectRequest(bucket, storeFileName, inputStream, metadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
        } catch (IOException e) {
            throw new IllegalArgumentException("파일 업로드에 실패했습니다.");
        }
        return amazonS3.getUrl(bucket, storeFileName).toString();
    }

    /**
     * s3 api를 위한 dto 생성 메서드
     * @param file 업로드 요청된 이미지 파일
     * @return s3 api 요구 스펙 dto
     */
    private ObjectMetadata createObjectMetadata(MultipartFile file) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());
        return metadata;
    }
}

package myblog.blog.imgupload.adapter.outgoing;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import lombok.RequiredArgsConstructor;
import myblog.blog.imgupload.domain.ImageFile;
import myblog.blog.imgupload.service.port.outgoing.ImgUploadStrategyPort;
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
public class AwsS3ImgUploadStrategyAdapter implements ImgUploadStrategyPort {

    private final AmazonS3 amazonS3;
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public String uploadFile(ImageFile imageFile) {
        MultipartFile file = imageFile.getMultipartFile();
        ObjectMetadata metadata = createObjectMetadata(file);
        try(InputStream inputStream = file.getInputStream()) {
            amazonS3.putObject(new PutObjectRequest(bucket, imageFile.getStoredFileName(), inputStream, metadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
        } catch (IOException e) {
            throw new IllegalArgumentException("파일 업로드에 실패했습니다.");
        }
        return amazonS3.getUrl(bucket, imageFile.getStoredFileName()).toString();
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

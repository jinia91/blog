package myblog.blog.imgupload.service;

import lombok.RequiredArgsConstructor;
import myblog.blog.imgupload.domain.ImageFile;
import myblog.blog.imgupload.service.port.incomming.ImgUploadUseCase;
import myblog.blog.imgupload.service.port.outgoing.ImgUploadStrategyPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class ImgUploadService implements ImgUploadUseCase {

    private final ImgUploadStrategyPort imgUploadStrategyPort;

    @Override
    public String storeImg(MultipartFile multipartFile) {
        validateFile(multipartFile);
        ImageFile imageFile = ImageFile.from(multipartFile);
        return imgUploadStrategyPort.uploadFile(imageFile);
    }

    private void validateFile(MultipartFile multipartFile) {
        if (multipartFile.isEmpty()) {
            throw new IllegalArgumentException("이미지가 존재하지 않습니다.");
        }
    }
}
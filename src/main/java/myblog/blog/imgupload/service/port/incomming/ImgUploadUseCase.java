package myblog.blog.imgupload.service.port.incomming;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ImgUploadUseCase {
    String storeImg(MultipartFile img);
}

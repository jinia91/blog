package myblog.blog.imgupload.application.port.incomming;

import org.springframework.web.multipart.MultipartFile;

public interface ImgUploadUseCase {
    String storeImg(MultipartFile img);
}

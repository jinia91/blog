package myblog.blog.imgupload.adapter.incomming;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class UploadImgForm {
    private MultipartFile img;
}

package myblog.blog.img.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class UploadImgDto {

    private MultipartFile img;

}

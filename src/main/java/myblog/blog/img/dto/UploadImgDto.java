package myblog.blog.img.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

/*
    - 멀티파트 파일 래핑용 DTO
*/
@Getter
@Setter
public class UploadImgDto {
    private MultipartFile img;
}

package myblog.blog.img.controller;

import lombok.RequiredArgsConstructor;
import myblog.blog.img.dto.UploadImgDto;
import myblog.blog.img.service.UploadImgService;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class UploadImgController {

    private final UploadImgService uploadImgService;

    /*
        - 썸네일 업로드 요청
    */
    @PostMapping("/article/uploadImg")
    public @ResponseBody
    String imgUpload(@ModelAttribute UploadImgDto uploadImgDto) throws IOException {
        return uploadImgService.storeImg(uploadImgDto.getImg());
    }
}

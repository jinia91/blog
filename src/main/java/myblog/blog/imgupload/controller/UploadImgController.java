package myblog.blog.imgupload.controller;

import lombok.RequiredArgsConstructor;
import myblog.blog.imgupload.dto.UploadImgDto;
import myblog.blog.imgupload.service.ImgUploadServiceImpl;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class UploadImgController {

    private final ImgUploadServiceImpl imgUploadServiceImpl;

    /*
        - 썸네일 업로드 요청
    */
    @PostMapping("/article/uploadImg")
    public @ResponseBody
    String imgUpload(@ModelAttribute UploadImgDto uploadImgDto) throws IOException {
        return imgUploadServiceImpl.storeImg(uploadImgDto.getImg());
    }
}

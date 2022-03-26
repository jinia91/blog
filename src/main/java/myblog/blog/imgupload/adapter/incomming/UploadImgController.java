package myblog.blog.imgupload.adapter.incomming;

import myblog.blog.imgupload.application.port.incomming.ImgUploadUseCase;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UploadImgController {

    private final ImgUploadUseCase imgUploadUseCase;

    @PostMapping("/article/uploadImg")
    String imgUpload(@ModelAttribute UploadImgForm uploadImgForm){
        return imgUploadUseCase.storeImg(uploadImgForm.getImg());
    }
}

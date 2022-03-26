package myblog.blog.imgupload.adapter.incomming;

import myblog.blog.imgupload.service.port.incomming.ImgUploadUseCase;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class UploadImgController {

    private final ImgUploadUseCase imgUploadUseCase;

    @PostMapping("/article/uploadImg")
    public @ResponseBody
    String imgUpload(@ModelAttribute UploadImgForm uploadImgForm) throws IOException {
        return imgUploadUseCase.storeImg(uploadImgForm.getImg());
    }
}

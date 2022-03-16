package myblog.blog.imgupload.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ImgUploadService {
    List<String> storeFile(List<MultipartFile> imgList);
}

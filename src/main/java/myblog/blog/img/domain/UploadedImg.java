package myblog.blog.img.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
public class UploadedImg {

    private String uploadFileName;
    private String storeFileName;
    private String uploadUrl;

    public UploadedImg(String uploadFileName, String storeFileName, String uploadUrl) {
        this.uploadFileName = uploadFileName;
        this.storeFileName = storeFileName;
        this.uploadUrl = uploadUrl;
    }
}

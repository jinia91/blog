package myblog.blog.imgupload.application.port.outgoing;

import myblog.blog.imgupload.domain.ImageFile;

/**
 * 파일 업로드 전략패턴 추상화 인터페이스
 */
public interface ImgUploadStrategyPort {
    String uploadFile(ImageFile imageFile);
}

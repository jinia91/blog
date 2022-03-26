package myblog.blog.infra.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Value("${cloud.aws.credentials.accessKey}")
    private String AWS_ACCESS_KEY_ID;
    @Value("${cloud.aws.credentials.secretKey}")
    private String AWS_SECRET_ACCESS_KEY;

    /**
     * AWS S3 설정
     */
    @Bean
    public AmazonS3 S3() {
        AWSCredentials awsCredentials =
                new BasicAWSCredentials(AWS_ACCESS_KEY_ID, AWS_SECRET_ACCESS_KEY);
        return AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials)).withRegion(Regions.AP_NORTHEAST_2)
                .build();
    }
}

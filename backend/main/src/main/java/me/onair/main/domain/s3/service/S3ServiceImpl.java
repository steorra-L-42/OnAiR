package me.onair.main.domain.s3.service;

import static me.onair.main.global.error.ErrorCode.IMAGE_UPLOAD_FAILED;
import static me.onair.main.global.error.ErrorCode.INVALID_FILE_FORMAT;
import static me.onair.main.global.error.ErrorCode.MULTIPARTFILE_IS_NULL;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.onair.main.domain.s3.error.ImageUploadFailedException;
import me.onair.main.domain.s3.error.InvalidFileFormatException;
import me.onair.main.domain.s3.error.MultipartFileNullException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RequiredArgsConstructor
@Service
public class S3ServiceImpl implements S3Service {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucketName}")
    private String bucket;

    /**
     * S3 버킷에 파일을 등록하는 메소드
     *
     * @param multipartFile MultiFile
     * @return 파일 업로드 된 URL
     */
    public String uploadSingleFile(MultipartFile multipartFile) {

        if (multipartFile == null || multipartFile.isEmpty()) {
            throw new MultipartFileNullException(MULTIPARTFILE_IS_NULL);
        }
        
        String fileName = createFileName(multipartFile.getOriginalFilename());
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(multipartFile.getSize());
        metadata.setContentType(multipartFile.getContentType());

        try (InputStream inputStream = multipartFile.getInputStream()) {
            amazonS3.putObject(new PutObjectRequest(bucket, fileName, inputStream, metadata));
        } catch (IOException e) {
            throw new ImageUploadFailedException(IMAGE_UPLOAD_FAILED);
        }
        return amazonS3.getUrl(bucket, fileName).toString();
    }

    private String createFileName(String fileName) {
        return UUID.randomUUID().toString().concat(getFileExtension(fileName));
    }

    private String getFileExtension(String fileName) {
        try {
            return fileName.substring(fileName.lastIndexOf("."));
        } catch (StringIndexOutOfBoundsException se) {
            throw new InvalidFileFormatException(INVALID_FILE_FORMAT);
        }
    }
}

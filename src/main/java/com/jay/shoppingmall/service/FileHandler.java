package com.jay.shoppingmall.service;

import com.jay.shoppingmall.domain.image.Image;
import com.jay.shoppingmall.dto.ImageUpload;
import com.jay.shoppingmall.exception.FileException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class FileHandler {

    public List<Image> parseFileInfo(List<MultipartFile> files) {
        List<Image> imageList = new ArrayList<>();

        if(!CollectionUtils.isEmpty(files)) {
            //파일명을 업로드 한 날짜로 변환 후 저장
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            String currentDate = now.format(dateTimeFormatter);

            //절대 경로를 설정.
            //File.separator는 OS마다 다른 path 구분자를 처리하기 위함.
            String absolutPath = new File("").getAbsolutePath() + File.separator + File.separator;

            //파일 저장 세부 경로 설정.
            String path = "images" + File.separator + currentDate;
            File file = new File(path);

            //경로가 중복되지 않을 경우
            if (!file.exists()) {
                boolean wasSuccessful = file.mkdirs();

                if(!wasSuccessful) {
                    throw new FileException("Duplicated files");
                }
            }
            //모든 파일에 대해 처리
            for (MultipartFile multipartFile : files) {
                //파일의 확장자 추출
                String originalFileExtension;
                String contentType = multipartFile.getContentType();

                //확장자가 없다면 처리 중지
                if (ObjectUtils.isEmpty(contentType)) {
                    break;
                } else { //jpeg와 png인 사진 파일에 대해서만 처리
                    if (contentType.contains("image/jpeg"))
                        originalFileExtension = ".jpg";
                    else if (contentType.contains("image/png"))
                        originalFileExtension = ".png";
                    else
                        break;
                }
                //파일명은 나노초+확장자
                String fileName = System.nanoTime() + originalFileExtension;

                //DTO -> Image -> List<Image>
                ImageUpload imageUpload = ImageUpload.builder()
                        .originalFileName(multipartFile.getOriginalFilename())
                        .filePath(path + File.separator + fileName)
                        .fileSize(multipartFile.getSize())
                        .build();

//                Image image = imageUpload.toEntity();

                Image image = new Image(
                        imageUpload.getOriginalFileName(),
                        imageUpload.getFilePath(),
                        imageUpload.getFileSize()
                );

                imageList.add(image);

                //업로드 한 파일 데이터를 지정한 파일에 저장.
                file = new File(absolutPath + path + File.separator + fileName);
                try {
                    multipartFile.transferTo(file);
                } catch (IOException e) {
                    throw new FileException("File I/O failed");
                }

                file.setWritable(true);
                file.setReadable(true);

            }

        }

        return imageList;
    }
}

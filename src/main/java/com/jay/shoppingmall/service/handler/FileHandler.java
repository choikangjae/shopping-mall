package com.jay.shoppingmall.service.handler;

import com.jay.shoppingmall.domain.image.Image;
import com.jay.shoppingmall.domain.image.ImageRelation;
import com.jay.shoppingmall.domain.image.ImageRepository;
import com.jay.shoppingmall.domain.item.Item;
import com.jay.shoppingmall.dto.request.ImageUpload;
import com.jay.shoppingmall.exception.exceptions.FileException;
import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

@Service
@Transactional
@RequiredArgsConstructor
public class FileHandler {

    private final ImageRepository imageRepository;

    public Image saveImage(MultipartFile file) {

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String currentDate = now.format(dateTimeFormatter);


        String absolutePath = new File("").getAbsolutePath() + File.separator + File.separator;

        String path = "images" + File.separator + currentDate;
        File filePath = new File(path);

        if (!filePath.exists())
            filePath.mkdirs();

        if (file.getContentType() == null || file.getContentType().equals("image/jpeg") || file.getContentType().equals("image/png"))
            throw new FileException("확장자가 없거나 jpg, png가 아닙니다");

        String fileName = String.valueOf(System.nanoTime());

        Image image = Image.builder()
                .originalFileName(file.getOriginalFilename())
                .filePath(path + File.separator + fileName)
                .fileSize(file.getSize())
                .build();

        try {
            file.transferTo(new File(path));
        } catch (Exception e) {
            throw new FileException("File I/O failed");
        }

        return image;
    }

    //    public List<Image> parseFilesInfo(List<MultipartFile> files) {
//        int indexOfMainImage = 0;
//        List<Image> imageList = new ArrayList<>();
//
//        if(!CollectionUtils.isEmpty(files)) {
//            //폴더명을 업로드 한 날짜로 변환 후 저장
//            LocalDateTime now = LocalDateTime.now();
//            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
//            String currentDate = now.format(dateTimeFormatter);
//
//            //프로젝트 내에 저장하기 위해 절대 경로를 설정.
//            //File.separator는 OS마다 다른 path 구분자를 처리하기 위함.
//            String absolutePath = new File("").getAbsolutePath() + File.separator + File.separator;
//
//            //파일 저장 세부 경로 설정.
//            String path = "images" + File.separator + currentDate;
//            File file = new File(path);
//            File thumbnailFile = new File(path + File.separator + "thumbnails");
//
//            //폴더가 존재하지 않으면 만듬
//            //없는 폴더에 파일을 넣으려고 하면 I/O 익셉션 발생
//            if (!file.exists()) {
//                file.mkdirs();
//            }
//            if (!thumbnailFile.exists())
//                thumbnailFile.mkdirs();
//
//            //모든 파일에 대해 처리
//            for (MultipartFile multipartFile : files) {
//                //파일의 확장자 추출
//                String originalFileExtension;
//                String contentType = multipartFile.getContentType();
//
//                //확장자가 없다면 처리 중지
//                if (ObjectUtils.isEmpty(contentType)) {
//                    break;
//                } else { //jpeg와 png인 사진 파일에 대해서만 처리
//                    if (contentType.contains("image/jpeg"))
//                        originalFileExtension = ".jpg";
//                    else if (contentType.contains("image/png"))
//                        originalFileExtension = ".png";
//                    else
//                        break;
//                }
//                //파일명은 나노초+확장자
//                String fileName = System.nanoTime() + originalFileExtension;
//
//                //DTO -> Image -> List<Image>
//                ImageUpload imageUpload = ImageUpload.builder()
//                        .originalFileName(multipartFile.getOriginalFilename())
//                        .filePath(path + File.separator + fileName)
//                        .fileSize(multipartFile.getSize())
//                        .isMainImage(false)
//                        .build();
//
//                Image image = new Image(
//                        imageUpload.getOriginalFileName(),
//                        imageUpload.getFilePath(),
//                        imageUpload.getFileSize(),
//                        imageUpload.isMainImage()
//                );
//                imageList.add(image);
//
//                //업로드 한 파일 데이터를 지정한 파일에 저장.
//                file = new File(absolutePath + path + File.separator + fileName);
//                try {
//                    multipartFile.transferTo(file);
//                    Thumbnails.of(file)
//                            .size(100, 100)
//                            .toFile(thumbnailFile + File.separator + fileName);
//                } catch (IOException e) {
//                    throw new FileException("File I/O failed");
//                }
//
//            }
//        }
//        imageList.get(indexOfMainImage).setIsMainImage(true);
//        return imageList;
//    }
    public Image parseFilesInfo(MultipartFile multipartFile, ImageRelation imageRelation, Long foreignId) {

            //폴더명을 업로드 한 날짜로 변환 후 저장
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            String currentDate = now.format(dateTimeFormatter);

            //프로젝트 내에 저장하기 위해 절대 경로를 설정.
            //File.separator는 OS마다 다른 path 구분자를 처리하기 위함.
            String absolutePath = new File("").getAbsolutePath() + File.separator + File.separator;

            //파일 저장 세부 경로 설정.
            String path = "images" + File.separator + currentDate;
            File file = new File(path);
            File thumbnailFile = new File(path + File.separator + "thumbnails");

            //폴더가 존재하지 않으면 만듬
            //없는 폴더에 파일을 넣으려고 하면 I/O 익셉션 발생
            if (!file.exists()) {
                file.mkdirs();
            }
            if (!thumbnailFile.exists())
                thumbnailFile.mkdirs();

            //모든 파일에 대해 처리
                //파일의 확장자 추출
                String originalFileExtension = "";
                String contentType = multipartFile.getContentType();

                //확장자가 없다면 처리 중지
                    if (contentType.contains("image/jpeg"))
                        originalFileExtension = ".jpg";
                    else if (contentType.contains("image/png"))
                        originalFileExtension = ".png";

                String fileName = System.nanoTime() + "";
                        //확장자를 붙여 저장하는 것은 보안에 취약.
                        //+ originalFileExtension;

                Image image = Image.builder()
                        .originalFileName(multipartFile.getOriginalFilename())
                        .fileSize(multipartFile.getSize())
                        .filePath(path + File.separator + fileName)
                        .fileExtension(originalFileExtension)
                        .imageRelation(imageRelation)
                        .foreignId(foreignId)
                        .isMainImage(false)
                        .build();

                //업로드 한 파일 데이터를 지정한 파일에 저장.
                file = new File(absolutePath + path + File.separator + fileName);
                try {
                    multipartFile.transferTo(file);
//                    Thumbnails.of(file)
//                            .size(750, 500)
//                            .toFile(thumbnailFile + File.separator + fileName);
                } catch (IOException e) {
                    throw new FileException("File I/O failed");
                }
        return image;
    }

    public String getStringImage(Image image) {
        String absolutePath = new File("").getAbsolutePath() + File.separator;
        String path = absolutePath + image.getFilePath();

        String stringImage = null;
        try {
            InputStream inputStream = new FileInputStream(path);
            byte[] imageBytes = IOUtils.toByteArray(inputStream);
            stringImage = Base64.getEncoder().encodeToString(imageBytes);
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringImage;
    }


}

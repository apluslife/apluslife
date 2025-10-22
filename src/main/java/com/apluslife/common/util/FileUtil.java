package com.apluslife.common.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
public class FileUtil {

    @Value("${file.upload.path}")
    private String uploadPath;

    // 허용된 파일 확장자 정의
    private static final Set<String> ALLOWED_IMAGE_EXTENSIONS = new HashSet<>(Arrays.asList(
            "jpg", "jpeg", "png", "gif", "bmp", "webp", "svg"
    ));

    private static final Set<String> ALLOWED_DOCUMENT_EXTENSIONS = new HashSet<>(Arrays.asList(
            "pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx",
            "hwp", "txt", "csv", "odt", "ods", "odp"
    ));

    private static final Set<String> ALLOWED_ARCHIVE_EXTENSIONS = new HashSet<>(Arrays.asList(
            "zip", "rar", "7z", "tar", "gz", "alz", "egg"
    ));

    /**
     * 파일 저장
     */
    public Map<String, Object> saveFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("파일이 비어있습니다.");
        }

        // 원본 파일명
        String originalFileName = file.getOriginalFilename();
        if (originalFileName == null || originalFileName.isEmpty()) {
            throw new IllegalArgumentException("파일명이 올바르지 않습니다.");
        }

        // 파일 확장자 검증
        String extension = getFileExtension(originalFileName);
        String fileCategory = getFileCategory(extension);

        if (fileCategory == null) {
            throw new IllegalArgumentException("허용되지 않는 파일 형식입니다: " + extension);
        }

        // 저장될 파일명 생성 (UUID)
        String savedFileName = UUID.randomUUID().toString() + "." + extension;

        // 날짜별 폴더 생성 (예: 2025/10/21)
        String dateFolder = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String fullPath = uploadPath + File.separator + dateFolder;

        // 디렉토리 생성
        File directory = new File(fullPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // 파일 저장
        Path filePath = Paths.get(fullPath, savedFileName);
        Files.copy(file.getInputStream(), filePath);

        // 파일 정보 반환
        Map<String, Object> fileInfo = new HashMap<>();
        fileInfo.put("originalFileName", originalFileName);
        fileInfo.put("savedFileName", savedFileName);
        fileInfo.put("filePath", dateFolder + File.separator + savedFileName);
        fileInfo.put("fileSize", file.getSize());
        fileInfo.put("fileType", file.getContentType());
        fileInfo.put("fileCategory", fileCategory);

        return fileInfo;
    }

    /**
     * 파일 삭제
     */
    public boolean deleteFile(String filePath) {
        try {
            Path path = Paths.get(uploadPath, filePath);
            return Files.deleteIfExists(path);
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * 파일 확장자 추출
     */
    private String getFileExtension(String fileName) {
        int lastIndex = fileName.lastIndexOf('.');
        if (lastIndex > 0) {
            return fileName.substring(lastIndex + 1).toLowerCase();
        }
        return "";
    }

    /**
     * 파일 카테고리 판별
     */
    private String getFileCategory(String extension) {
        if (ALLOWED_IMAGE_EXTENSIONS.contains(extension)) {
            return "image";
        } else if (ALLOWED_DOCUMENT_EXTENSIONS.contains(extension)) {
            return "document";
        } else if (ALLOWED_ARCHIVE_EXTENSIONS.contains(extension)) {
            return "archive";
        }
        return null;  // 허용되지 않는 확장자
    }

    /**
     * 파일 크기를 읽기 쉬운 형태로 변환
     */
    public static String formatFileSize(long size) {
        if (size < 1024) {
            return size + " B";
        } else if (size < 1024 * 1024) {
            return String.format("%.2f KB", size / 1024.0);
        } else if (size < 1024 * 1024 * 1024) {
            return String.format("%.2f MB", size / (1024.0 * 1024.0));
        } else {
            return String.format("%.2f GB", size / (1024.0 * 1024.0 * 1024.0));
        }
    }

    /**
     * 허용된 파일 확장자인지 검증
     */
    public boolean isAllowedExtension(String fileName) {
        String extension = getFileExtension(fileName);
        return getFileCategory(extension) != null;
    }

    /**
     * 이미지 파일인지 확인
     */
    public boolean isImageFile(String fileName) {
        String extension = getFileExtension(fileName);
        return ALLOWED_IMAGE_EXTENSIONS.contains(extension);
    }
}

package com.apluslife.domain.announcement.service;

import com.apluslife.common.util.FileUtil;
import com.apluslife.domain.announcement.dto.AnnouncementDto;
import com.apluslife.domain.announcement.dto.AnnouncementRequest;
import com.apluslife.domain.announcement.entity.Announcement;
import com.apluslife.domain.announcement.entity.AnnouncementFile;
import com.apluslife.domain.announcement.repository.AnnouncementRepository;
import com.apluslife.domain.announcement.repository.AnnouncementFileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 공시자료 관리 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnnouncementService {

    private final AnnouncementRepository announcementRepository;
    private final AnnouncementFileRepository announcementFileRepository;
    private final FileUtil fileUtil;

    // 카테고리 ID 상수
    private static final Integer ANNOUNCEMENT_CATEGORY_IDX = 1;
    private static final String ANNOUNCEMENT_CATEGORY_NAME = "공시자료";

    /**
     * 공시자료 목록 조회 (페이징)
     */
    public Page<AnnouncementDto> getAnnouncementList(Pageable pageable) {
        Page<Announcement> announcements = announcementRepository.findAll(pageable);
        return announcements.map(AnnouncementDto::from);
    }

    /**
     * 공시자료 목록 조회 (정렬)
     */
    public List<AnnouncementDto> getAnnouncementListOrdered() {
        List<Announcement> announcements = announcementRepository.findAllOrderByLatest();
        return announcements.stream()
                .map(AnnouncementDto::from)
                .collect(Collectors.toList());
    }

    /**
     * 공시자료 상세 조회
     */
    public AnnouncementDto getAnnouncement(Integer idx) {
        Announcement announcement = announcementRepository.findById(idx)
                .orElseThrow(() -> new IllegalArgumentException("공시자료를 찾을 수 없습니다."));
        return AnnouncementDto.from(announcement);
    }

    /**
     * 공시자료 생성
     */
    @Transactional
    public Integer createAnnouncement(AnnouncementRequest request,
                                     MultipartFile pdfFile,
                                     MultipartFile wordFile,
                                     MultipartFile hwpFile) throws IOException {
        StringBuilder fileNameBuilder = new StringBuilder();
        StringBuilder filePathBuilder = new StringBuilder();

        // 파일 업로드 처리
        int fileCount = 0;
        if (pdfFile != null && !pdfFile.isEmpty()) {
            Map<String, Object> fileInfo = fileUtil.saveFile(pdfFile);
            fileNameBuilder.append("[PDF]").append(fileInfo.get("originalFileName"));
            filePathBuilder.append((String) fileInfo.get("filePath"));
            fileCount++;
        }

        if (wordFile != null && !wordFile.isEmpty()) {
            if (fileCount > 0) {
                fileNameBuilder.append(" | ");
                filePathBuilder.append(" | ");
            }
            Map<String, Object> fileInfo = fileUtil.saveFile(wordFile);
            fileNameBuilder.append("[WORD]").append(fileInfo.get("originalFileName"));
            filePathBuilder.append((String) fileInfo.get("filePath"));
            fileCount++;
        }

        if (hwpFile != null && !hwpFile.isEmpty()) {
            if (fileCount > 0) {
                fileNameBuilder.append(" | ");
                filePathBuilder.append(" | ");
            }
            Map<String, Object> fileInfo = fileUtil.saveFile(hwpFile);
            fileNameBuilder.append("[HWP]").append(fileInfo.get("originalFileName"));
            filePathBuilder.append((String) fileInfo.get("filePath"));
            fileCount++;
        }

        if (fileCount == 0) {
            throw new IllegalArgumentException("최소 하나의 파일을 업로드해야 합니다.");
        }

        // 공시자료 생성
        Announcement announcement = Announcement.builder()
                .title(request.getTitle())
                .fileName(fileNameBuilder.toString())
                .filePath(filePathBuilder.toString())
                .rDate(LocalDateTime.now())
                .uDate(LocalDateTime.now())
                .build();

        Announcement saved = announcementRepository.save(announcement);
        Integer announcementIdx = saved.getIdx();

        // 파일 정보를 aplus_파일업로드 테이블에 저장
        saveAnnouncementFiles(announcementIdx, pdfFile, wordFile, hwpFile);

        return announcementIdx;
    }

    /**
     * 공시자료 파일을 aplus_파일업로드 테이블에 저장
     */
    private void saveAnnouncementFiles(Integer boardIdx, MultipartFile... files) throws IOException {
        for (MultipartFile file : files) {
            if (file != null && !file.isEmpty()) {
                Map<String, Object> fileInfo = fileUtil.saveFile(file);
                String fileName = (String) fileInfo.get("originalFileName");
                String filePath = (String) fileInfo.get("filePath");

                AnnouncementFile announcementFile = AnnouncementFile.builder()
                        .boardIdx(boardIdx)
                        .fileName(fileName)
                        .filePath(filePath)
                        .categoryIdx(ANNOUNCEMENT_CATEGORY_IDX)
                        .categoryName(ANNOUNCEMENT_CATEGORY_NAME)
                        .build();

                announcementFileRepository.save(announcementFile);
            }
        }
    }

    /**
     * 공시자료 수정
     */
    @Transactional
    public void updateAnnouncement(Integer idx,
                                  AnnouncementRequest request,
                                  MultipartFile pdfFile,
                                  MultipartFile wordFile,
                                  MultipartFile hwpFile,
                                  Boolean replaceFiles) throws IOException {
        Announcement announcement = announcementRepository.findById(idx)
                .orElseThrow(() -> new IllegalArgumentException("공시자료를 찾을 수 없습니다."));

        // 기본 정보 수정
        announcement.setTitle(request.getTitle());
        announcement.setUDate(LocalDateTime.now());

        // 파일 교체 여부 확인
        if (replaceFiles != null && replaceFiles) {
            StringBuilder fileNameBuilder = new StringBuilder();
            StringBuilder filePathBuilder = new StringBuilder();

            int fileCount = 0;
            if (pdfFile != null && !pdfFile.isEmpty()) {
                Map<String, Object> fileInfo = fileUtil.saveFile(pdfFile);
                fileNameBuilder.append("[PDF]").append(fileInfo.get("originalFileName"));
                filePathBuilder.append((String) fileInfo.get("filePath"));
                fileCount++;
            }

            if (wordFile != null && !wordFile.isEmpty()) {
                if (fileCount > 0) {
                    fileNameBuilder.append(" | ");
                    filePathBuilder.append(" | ");
                }
                Map<String, Object> fileInfo = fileUtil.saveFile(wordFile);
                fileNameBuilder.append("[WORD]").append(fileInfo.get("originalFileName"));
                filePathBuilder.append((String) fileInfo.get("filePath"));
                fileCount++;
            }

            if (hwpFile != null && !hwpFile.isEmpty()) {
                if (fileCount > 0) {
                    fileNameBuilder.append(" | ");
                    filePathBuilder.append(" | ");
                }
                Map<String, Object> fileInfo = fileUtil.saveFile(hwpFile);
                fileNameBuilder.append("[HWP]").append(fileInfo.get("originalFileName"));
                filePathBuilder.append((String) fileInfo.get("filePath"));
                fileCount++;
            }

            if (fileCount > 0) {
                announcement.setFileName(fileNameBuilder.toString());
                announcement.setFilePath(filePathBuilder.toString());
            }
        }

        announcementRepository.save(announcement);
    }

    /**
     * 공시자료 삭제
     */
    @Transactional
    public void deleteAnnouncement(Integer idx) {
        Announcement announcement = announcementRepository.findById(idx)
                .orElseThrow(() -> new IllegalArgumentException("공시자료를 찾을 수 없습니다."));

        // 기존 파일 삭제
        if (announcement.getFilePath() != null && !announcement.getFilePath().isEmpty()) {
            String[] filePaths = announcement.getFilePath().split(" \\| ");
            for (String filePath : filePaths) {
                fileUtil.deleteFile(filePath);
            }
        }

        // aplus_파일업로드 테이블에서 관련 파일 정보 삭제
        announcementFileRepository.deleteByBoardIdx(idx);

        announcementRepository.deleteById(idx);
    }

    /**
     * 공시자료 검색
     */
    public Page<AnnouncementDto> searchAnnouncements(String keyword, Pageable pageable) {
        Page<Announcement> announcements = announcementRepository.findByTitleContainingIgnoreCase(keyword, pageable);
        return announcements.map(AnnouncementDto::from);
    }
}

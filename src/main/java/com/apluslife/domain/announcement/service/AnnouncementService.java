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
        // 최소 하나의 파일을 업로드해야 함
        if ((pdfFile == null || pdfFile.isEmpty()) &&
            (wordFile == null || wordFile.isEmpty()) &&
            (hwpFile == null || hwpFile.isEmpty())) {
            throw new IllegalArgumentException("최소 하나의 파일을 업로드해야 합니다.");
        }

        // 공시자료 생성 (aplus_gongsi 테이블에는 idx, title, rdate, udate만 저장)
        Announcement announcement = Announcement.builder()
                .title(request.getTitle())
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

        announcementRepository.save(announcement);

        // 파일 교체 여부 확인
        if (replaceFiles != null && replaceFiles) {
            // 기존 파일 삭제
            announcementFileRepository.deleteByBoardIdx(idx);

            // 새 파일 저장
            saveAnnouncementFiles(idx, pdfFile, wordFile, hwpFile);
        }
    }

    /**
     * 공시자료 삭제
     */
    @Transactional
    public void deleteAnnouncement(Integer idx) {
        announcementRepository.findById(idx)
                .orElseThrow(() -> new IllegalArgumentException("공시자료를 찾을 수 없습니다."));

        // aplus_파일업로드 테이블에서 관련 파일 삭제
        List<AnnouncementFile> files = announcementFileRepository.findByBoardIdx(idx);
        for (AnnouncementFile file : files) {
            fileUtil.deleteFile(file.getFilePath());
        }
        announcementFileRepository.deleteByBoardIdx(idx);

        // aplus_gongsi 테이블에서 공시자료 삭제
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

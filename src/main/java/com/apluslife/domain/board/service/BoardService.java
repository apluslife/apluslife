package com.apluslife.domain.board.service;

import com.apluslife.common.util.FileUtil;
import com.apluslife.domain.board.dto.BoardDto;
import com.apluslife.domain.board.dto.BoardFileDto;
import com.apluslife.domain.board.dto.BoardWriteRequest;
import com.apluslife.domain.board.entity.Board;
import com.apluslife.domain.board.entity.BoardFile;
import com.apluslife.domain.board.repository.BoardFileRepository;
import com.apluslife.domain.board.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardService {

    private final BoardRepository boardRepository;
    private final BoardFileRepository boardFileRepository;
    private final FileUtil fileUtil;

    /**
     * 게시글 목록 조회 (페이징)
     */
    public Page<BoardDto> getBoardList(String boardType, Pageable pageable) {
        Page<Board> boards = boardRepository.findByBoardTypeAndIsDeletedFalseOrderByIsNoticeDescCreatedDateDesc(
                boardType, pageable);
        return boards.map(BoardDto::from);
    }

    /**
     * 게시글 검색
     */
    public Page<BoardDto> searchBoards(String boardType, String searchType, String keyword, Pageable pageable) {
        Page<Board> boards;

        switch (searchType) {
            case "title":
                boards = boardRepository.searchByTitle(boardType, keyword, pageable);
                break;
            case "content":
                boards = boardRepository.searchByContent(boardType, keyword, pageable);
                break;
            case "writer":
                boards = boardRepository.searchByWriter(boardType, keyword, pageable);
                break;
            default:  // titleContent
                boards = boardRepository.searchByTitleOrContent(boardType, keyword, pageable);
                break;
        }

        return boards.map(BoardDto::from);
    }

    /**
     * 게시글 상세 조회 (조회수 증가)
     */
    @Transactional
    public BoardDto getBoard(Long idx) {
        Board board = boardRepository.findByIdxAndIsDeletedFalse(idx)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        board.increaseViewCount();

        return BoardDto.from(board);
    }

    /**
     * 게시글 상세 조회 (조회수 증가 없음 - 수정용)
     */
    public BoardDto getBoardForEdit(Long idx, String currentUserId) {
        Board board = boardRepository.findByIdxAndIsDeletedFalse(idx)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        // 작성자 본인만 수정 가능
        if (!board.getWriterId().equals(currentUserId)) {
            throw new IllegalArgumentException("수정 권한이 없습니다.");
        }

        return BoardDto.from(board);
    }

    /**
     * 게시글 작성
     */
    @Transactional
    public Long createBoard(BoardWriteRequest request, String writer, String writerId,
                           List<MultipartFile> files) throws IOException {
        // 게시글 생성
        Board board = Board.builder()
                .boardType(request.getBoardType())
                .title(request.getTitle())
                .content(request.getContent())
                .writer(writer)
                .writerId(writerId)
                .isNotice(request.getIsNotice())
                .build();

        Board savedBoard = boardRepository.save(board);

        // 파일 업로드 처리
        if (files != null && !files.isEmpty()) {
            uploadFiles(savedBoard, files);
        }

        return savedBoard.getIdx();
    }

    /**
     * 게시글 수정
     */
    @Transactional
    public void updateBoard(Long idx, BoardWriteRequest request, String currentUserId,
                           List<MultipartFile> newFiles, List<Long> deleteFileIds) throws IOException {
        Board board = boardRepository.findByIdxAndIsDeletedFalse(idx)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        // 작성자 본인만 수정 가능
        if (!board.getWriterId().equals(currentUserId)) {
            throw new IllegalArgumentException("수정 권한이 없습니다.");
        }

        // 게시글 내용 수정
        board.update(request.getTitle(), request.getContent(), request.getIsNotice());

        // 파일 삭제 처리
        if (deleteFileIds != null && !deleteFileIds.isEmpty()) {
            deleteFiles(deleteFileIds);
        }

        // 새 파일 업로드 처리
        if (newFiles != null && !newFiles.isEmpty()) {
            uploadFiles(board, newFiles);
        }
    }

    /**
     * 게시글 삭제 (논리 삭제)
     */
    @Transactional
    public void deleteBoard(Long idx, String currentUserId) {
        Board board = boardRepository.findByIdxAndIsDeletedFalse(idx)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        // 작성자 본인만 삭제 가능
        if (!board.getWriterId().equals(currentUserId)) {
            throw new IllegalArgumentException("삭제 권한이 없습니다.");
        }

        board.delete();

        // 첨부파일도 논리 삭제
        board.getFiles().forEach(BoardFile::delete);
    }

    /**
     * 파일 업로드 처리
     */
    private void uploadFiles(Board board, List<MultipartFile> files) throws IOException {
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                Map<String, Object> fileInfo = fileUtil.saveFile(file);

                BoardFile boardFile = BoardFile.builder()
                        .originalFileName((String) fileInfo.get("originalFileName"))
                        .savedFileName((String) fileInfo.get("savedFileName"))
                        .filePath((String) fileInfo.get("filePath"))
                        .fileSize((Long) fileInfo.get("fileSize"))
                        .fileType((String) fileInfo.get("fileType"))
                        .fileCategory((String) fileInfo.get("fileCategory"))
                        .build();

                board.addFile(boardFile);
                boardFileRepository.save(boardFile);
            }
        }
    }

    /**
     * 파일 삭제 처리
     */
    private void deleteFiles(List<Long> fileIds) {
        for (Long fileId : fileIds) {
            boardFileRepository.findById(fileId).ifPresent(file -> {
                file.delete();
                // 실제 파일 시스템에서도 삭제
                fileUtil.deleteFile(file.getFilePath());
            });
        }
    }

    /**
     * 게시글의 파일 목록 조회
     */
    public List<BoardFileDto> getBoardFiles(Long boardIdx) {
        List<BoardFile> files = boardFileRepository.findByBoardIdxAndIsDeletedFalseOrderByUploadDate(boardIdx);
        return files.stream()
                .map(BoardFileDto::from)
                .collect(Collectors.toList());
    }
}

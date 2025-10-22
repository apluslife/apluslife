package com.apluslife.web.controller;

import com.apluslife.domain.board.dto.BoardDto;
import com.apluslife.domain.board.dto.BoardWriteRequest;
import com.apluslife.domain.board.service.BoardService;
import com.apluslife.domain.member.entity.Member;
import com.apluslife.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/board")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;
    private final MemberRepository memberRepository;
    private final com.apluslife.domain.board.repository.BoardFileRepository boardFileRepository;

    @org.springframework.beans.factory.annotation.Value("${file.upload.path:c:/apluslife/uploads}")
    private String uploadPath;

    /**
     * 게시판 목록 페이지
     */
    @GetMapping("/{boardType}")
    public String list(@PathVariable String boardType,
                      @RequestParam(defaultValue = "0") int page,
                      @RequestParam(required = false) String searchType,
                      @RequestParam(required = false) String keyword,
                      Model model) {

        Pageable pageable = PageRequest.of(page, 10);
        Page<BoardDto> boards;

        if (keyword != null && !keyword.trim().isEmpty()) {
            boards = boardService.searchBoards(boardType, searchType, keyword, pageable);
            model.addAttribute("searchType", searchType);
            model.addAttribute("keyword", keyword);
        } else {
            boards = boardService.getBoardList(boardType, pageable);
        }

        model.addAttribute("boards", boards);
        model.addAttribute("boardType", boardType);
        model.addAttribute("boardTypeName", getBoardTypeName(boardType));

        return "board/list";
    }

    /**
     * 게시글 상세 페이지
     */
    @GetMapping("/{boardType}/{idx}")
    public String detail(@PathVariable String boardType,
                        @PathVariable Long idx,
                        Model model) {
        BoardDto board = boardService.getBoard(idx);

        model.addAttribute("board", board);
        model.addAttribute("boardType", boardType);
        model.addAttribute("boardTypeName", getBoardTypeName(boardType));

        // 현재 로그인한 사용자 정보
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            Member currentMember = memberRepository.findById(auth.getName()).orElse(null);
            if (currentMember != null) {
                model.addAttribute("currentUserId", currentMember.getId());
                model.addAttribute("isWriter", currentMember.getId().equals(board.getWriterId()));
            }
        }

        return "board/detail";
    }

    /**
     * 게시글 작성 페이지
     */
    @GetMapping("/{boardType}/write")
    public String writeForm(@PathVariable String boardType, Model model) {
        model.addAttribute("boardType", boardType);
        model.addAttribute("boardTypeName", getBoardTypeName(boardType));
        return "board/write";
    }

    /**
     * 게시글 작성 처리
     */
    @PostMapping("/{boardType}/write")
    public String write(@PathVariable String boardType,
                       @ModelAttribute BoardWriteRequest request,
                       @RequestParam(value = "files", required = false) List<MultipartFile> files,
                       RedirectAttributes redirectAttributes) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Member currentMember = memberRepository.findById(auth.getName())
                .orElseThrow(() -> new IllegalArgumentException("로그인이 필요합니다."));

        request.setBoardType(boardType);

        try {
            Long boardIdx = boardService.createBoard(request, currentMember.getName(),
                    currentMember.getId(), files);

            redirectAttributes.addFlashAttribute("message", "게시글이 작성되었습니다.");
            return "redirect:/board/" + boardType + "/" + boardIdx;

        } catch (IOException e) {
            log.error("파일 업로드 실패", e);
            redirectAttributes.addFlashAttribute("error", "파일 업로드에 실패했습니다.");
            return "redirect:/board/" + boardType + "/write";
        }
    }

    /**
     * 게시글 수정 페이지
     */
    @GetMapping("/{boardType}/{idx}/edit")
    public String editForm(@PathVariable String boardType,
                          @PathVariable Long idx,
                          Model model) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Member currentMember = memberRepository.findById(auth.getName())
                .orElseThrow(() -> new IllegalArgumentException("로그인이 필요합니다."));

        BoardDto board = boardService.getBoardForEdit(idx, currentMember.getId());

        model.addAttribute("board", board);
        model.addAttribute("boardType", boardType);
        model.addAttribute("boardTypeName", getBoardTypeName(boardType));

        return "board/edit";
    }

    /**
     * 게시글 수정 처리
     */
    @PostMapping("/{boardType}/{idx}/edit")
    public String edit(@PathVariable String boardType,
                      @PathVariable Long idx,
                      @ModelAttribute BoardWriteRequest request,
                      @RequestParam(value = "newFiles", required = false) List<MultipartFile> newFiles,
                      @RequestParam(value = "deleteFileIds", required = false) List<Long> deleteFileIds,
                      RedirectAttributes redirectAttributes) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Member currentMember = memberRepository.findById(auth.getName())
                .orElseThrow(() -> new IllegalArgumentException("로그인이 필요합니다."));

        request.setBoardType(boardType);

        try {
            boardService.updateBoard(idx, request, currentMember.getId(), newFiles, deleteFileIds);

            redirectAttributes.addFlashAttribute("message", "게시글이 수정되었습니다.");
            return "redirect:/board/" + boardType + "/" + idx;

        } catch (IOException e) {
            log.error("파일 업로드 실패", e);
            redirectAttributes.addFlashAttribute("error", "파일 업로드에 실패했습니다.");
            return "redirect:/board/" + boardType + "/" + idx + "/edit";
        }
    }

    /**
     * 게시글 삭제 처리
     */
    @PostMapping("/{boardType}/{idx}/delete")
    public String delete(@PathVariable String boardType,
                        @PathVariable Long idx,
                        RedirectAttributes redirectAttributes) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Member currentMember = memberRepository.findById(auth.getName())
                .orElseThrow(() -> new IllegalArgumentException("로그인이 필요합니다."));

        boardService.deleteBoard(idx, currentMember.getId());

        redirectAttributes.addFlashAttribute("message", "게시글이 삭제되었습니다.");
        return "redirect:/board/" + boardType;
    }

    /**
     * 파일 다운로드
     */
    @GetMapping("/file/download/{fileIdx}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long fileIdx) throws UnsupportedEncodingException {

        // 파일 정보 조회
        com.apluslife.domain.board.entity.BoardFile file = boardFileRepository.findById(fileIdx)
                .orElseThrow(() -> new IllegalArgumentException("파일을 찾을 수 없습니다."));

        // 실제 파일 경로
        Path filePath = Paths.get(uploadPath, file.getFilePath());
        Resource resource = new FileSystemResource(filePath);

        if (!resource.exists()) {
            throw new IllegalArgumentException("파일이 존재하지 않습니다.");
        }

        // 파일명 인코딩 (한글 파일명 지원)
        String encodedFileName = URLEncoder.encode(file.getOriginalFileName(), StandardCharsets.UTF_8.toString())
                .replaceAll("\\+", "%20");

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + encodedFileName + "\"")
                .body(resource);
    }

    /**
     * 게시판 유형명 반환
     */
    private String getBoardTypeName(String boardType) {
        switch (boardType) {
            case "notice":
                return "공지사항";
            case "free":
                return "자유게시판";
            case "qna":
                return "문의게시판";
            default:
                return "게시판";
        }
    }
}

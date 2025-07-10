package com.example.crudproject.controller;

import com.example.crudproject.dto.BoardRequestDto;
import com.example.crudproject.dto.BoardResponseDto;
import com.example.crudproject.service.BoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/boards")
@RequiredArgsConstructor
@Slf4j
public class BoardController {
    
    private final BoardService boardService;
    
    // 게시글 목록 페이지
    @GetMapping
    public String boardList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "content") String type,
            Model model) {
        
        Page<BoardResponseDto> boards;
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            boards = boardService.searchBoards(keyword.trim(), type, page, size);
            model.addAttribute("keyword", keyword);
            model.addAttribute("type", type);
        } else {
            boards = boardService.getAllBoards(page, size);
        }
        
        model.addAttribute("boards", boards);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", boards.getTotalPages());
        model.addAttribute("totalElements", boards.getTotalElements());
        
        return "boards/list";
    }
    
    // 게시글 상세 페이지
    @GetMapping("/{id}")
    public String boardDetail(@PathVariable Long id, Model model) {
        try {
            BoardResponseDto board = boardService.getBoardById(id);
            model.addAttribute("board", board);
            return "boards/detail";
        } catch (IllegalArgumentException e) {
            log.error("게시글 조회 실패: {}", e.getMessage());
            return "redirect:/boards";
        }
    }
    
    // 게시글 작성 폼 페이지
    @GetMapping("/new")
    public String boardForm(Model model) {
        model.addAttribute("board", new BoardRequestDto());
        return "boards/form";
    }
    
    // 게시글 생성
    @PostMapping("/new")
    public String createBoard(@ModelAttribute BoardRequestDto requestDto, RedirectAttributes redirectAttributes) {
        try {
            BoardResponseDto response = boardService.createBoard(requestDto);
            redirectAttributes.addFlashAttribute("message", "게시글이 성공적으로 작성되었습니다.");
            return "redirect:/boards/" + response.getId();
        } catch (IllegalArgumentException e) {
            log.error("게시글 생성 실패: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "게시글 작성에 실패했습니다: " + e.getMessage());
            return "redirect:/boards/new";
        }
    }
    
    // 게시글 수정 폼 페이지
    @GetMapping("/{id}/edit")
    public String boardEditForm(@PathVariable Long id, Model model) {
        try {
            BoardResponseDto board = boardService.getBoardById(id);
            BoardRequestDto requestDto = BoardRequestDto.builder()
                    .title(board.getTitle())
                    .author(board.getAuthor())
                    .content(board.getContent())
                    .build();
            model.addAttribute("board", requestDto);
            model.addAttribute("boardId", id);
            return "boards/edit";
        } catch (IllegalArgumentException e) {
            log.error("게시글 조회 실패: {}", e.getMessage());
            return "redirect:/boards";
        }
    }
    
    // 게시글 수정
    @PostMapping("/{id}/edit")
    public String updateBoard(@PathVariable Long id, @ModelAttribute BoardRequestDto requestDto, RedirectAttributes redirectAttributes) {
        try {
            boardService.updateBoard(id, requestDto);
            redirectAttributes.addFlashAttribute("message", "게시글이 성공적으로 수정되었습니다.");
            return "redirect:/boards/" + id;
        } catch (IllegalArgumentException e) {
            log.error("게시글 수정 실패: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "게시글 수정에 실패했습니다: " + e.getMessage());
            return "redirect:/boards/" + id + "/edit";
        }
    }
    
    // 게시글 삭제
    @PostMapping("/{id}/delete")
    public String deleteBoard(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            boardService.deleteBoard(id);
            redirectAttributes.addFlashAttribute("message", "게시글이 성공적으로 삭제되었습니다.");
            return "redirect:/boards";
        } catch (IllegalArgumentException e) {
            log.error("게시글 삭제 실패: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "게시글 삭제에 실패했습니다: " + e.getMessage());
            return "redirect:/boards/" + id;
        }
    }
    
    // 메인 페이지로 리다이렉트
    @GetMapping("/")
    public String home() {
        return "redirect:/boards";
    }
}

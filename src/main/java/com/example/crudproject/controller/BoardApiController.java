package com.example.crudproject.controller;

import com.example.crudproject.dto.BoardRequestDto;
import com.example.crudproject.dto.BoardResponseDto;
import com.example.crudproject.service.BoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class BoardApiController {
    
    private final BoardService boardService;
    
    // 게시글 생성
    @PostMapping
    public ResponseEntity<BoardResponseDto> createBoard(@RequestBody BoardRequestDto requestDto) {
        try {
            BoardResponseDto response = boardService.createBoard(requestDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            log.error("게시글 생성 실패: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 게시글 전체 조회 (페이징)
    @GetMapping
    public ResponseEntity<Page<BoardResponseDto>> getAllBoards(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Page<BoardResponseDto> boards = boardService.getAllBoards(page, size);
        return ResponseEntity.ok(boards);
    }
    
    // 게시글 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<BoardResponseDto> getBoardById(@PathVariable Long id) {
        try {
            BoardResponseDto response = boardService.getBoardById(id);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.error("게시글 조회 실패: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    
    // 게시글 수정
    @PutMapping("/{id}")
    public ResponseEntity<BoardResponseDto> updateBoard(
            @PathVariable Long id,
            @RequestBody BoardRequestDto requestDto) {
        try {
            BoardResponseDto response = boardService.updateBoard(id, requestDto);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.error("게시글 수정 실패: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 게시글 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBoard(@PathVariable Long id) {
        try {
            boardService.deleteBoard(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            log.error("게시글 삭제 실패: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    
    // 게시글 검색
    @GetMapping("/search")
    public ResponseEntity<Page<BoardResponseDto>> searchBoards(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "content") String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Page<BoardResponseDto> boards = boardService.searchBoards(keyword, type, page, size);
        return ResponseEntity.ok(boards);
    }
    
    // 최신 게시글 조회
    @GetMapping("/recent")
    public ResponseEntity<List<BoardResponseDto>> getRecentBoards() {
        List<BoardResponseDto> boards = boardService.getRecentBoards();
        return ResponseEntity.ok(boards);
    }
    
    // 인기 게시글 조회
    @GetMapping("/popular")
    public ResponseEntity<List<BoardResponseDto>> getPopularBoards() {
        List<BoardResponseDto> boards = boardService.getPopularBoards();
        return ResponseEntity.ok(boards);
    }
}

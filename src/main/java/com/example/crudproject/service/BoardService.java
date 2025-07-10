package com.example.crudproject.service;

import com.example.crudproject.dto.BoardRequestDto;
import com.example.crudproject.dto.BoardResponseDto;
import com.example.crudproject.entity.Board;
import com.example.crudproject.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class BoardService {
    
    private final BoardRepository boardRepository;
    
    // 게시글 생성
    public BoardResponseDto createBoard(BoardRequestDto requestDto) {
        if (!requestDto.isValid()) {
            throw new IllegalArgumentException("필수 입력값이 누락되었습니다.");
        }
        
        Board board = Board.builder()
                .title(requestDto.getTitle().trim())
                .author(requestDto.getAuthor().trim())
                .content(requestDto.getContent().trim())
                .build();
        
        Board savedBoard = boardRepository.save(board);
        log.info("게시글이 생성되었습니다. ID: {}", savedBoard.getId());
        
        return BoardResponseDto.from(savedBoard);
    }
    
    // 게시글 전체 조회 (페이징)
    @Transactional(readOnly = true)
    public Page<BoardResponseDto> getAllBoards(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Board> boards = boardRepository.findAll(pageable);
        
        return boards.map(BoardResponseDto::from);
    }
    
    // 게시글 상세 조회 (조회수 증가)
    public BoardResponseDto getBoardById(Long id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다. ID: " + id));
        
        board.increaseViewCount();
        Board updatedBoard = boardRepository.save(board);
        
        log.info("게시글 조회. ID: {}, 조회수: {}", id, updatedBoard.getViewCount());
        
        return BoardResponseDto.from(updatedBoard);
    }
    
    // 게시글 수정
    public BoardResponseDto updateBoard(Long id, BoardRequestDto requestDto) {
        if (!requestDto.isValid()) {
            throw new IllegalArgumentException("필수 입력값이 누락되었습니다.");
        }
        
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다. ID: " + id));
        
        board.setTitle(requestDto.getTitle().trim());
        board.setAuthor(requestDto.getAuthor().trim());
        board.setContent(requestDto.getContent().trim());
        
        Board updatedBoard = boardRepository.save(board);
        log.info("게시글이 수정되었습니다. ID: {}", id);
        
        return BoardResponseDto.from(updatedBoard);
    }
    
    // 게시글 삭제
    public void deleteBoard(Long id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다. ID: " + id));
        
        boardRepository.delete(board);
        log.info("게시글이 삭제되었습니다. ID: {}", id);
    }
    
    // 게시글 검색
    @Transactional(readOnly = true)
    public Page<BoardResponseDto> searchBoards(String keyword, String type, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Board> boards;
        
        switch (type.toLowerCase()) {
            case "title":
                boards = boardRepository.findByTitleContainingIgnoreCase(keyword, pageable);
                break;
            case "author":
                boards = boardRepository.findByAuthorContainingIgnoreCase(keyword, pageable);
                break;
            case "content":
                boards = boardRepository.findByTitleOrContentContaining(keyword, pageable);
                break;
            default:
                boards = boardRepository.findByTitleOrContentContaining(keyword, pageable);
        }
        
        return boards.map(BoardResponseDto::from);
    }
    
    // 최신 게시글 조회
    @Transactional(readOnly = true)
    public List<BoardResponseDto> getRecentBoards() {
        List<Board> boards = boardRepository.findTop5ByOrderByCreatedAtDesc();
        return boards.stream()
                .map(BoardResponseDto::from)
                .collect(Collectors.toList());
    }
    
    // 인기 게시글 조회
    @Transactional(readOnly = true)
    public List<BoardResponseDto> getPopularBoards() {
        List<Board> boards = boardRepository.findTop5ByOrderByViewCountDesc();
        return boards.stream()
                .map(BoardResponseDto::from)
                .collect(Collectors.toList());
    }
}

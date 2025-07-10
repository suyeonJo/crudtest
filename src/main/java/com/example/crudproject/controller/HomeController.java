package com.example.crudproject.controller;

import com.example.crudproject.dto.BoardResponseDto;
import com.example.crudproject.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {
    
    private final BoardService boardService;
    
    @GetMapping("/")
    public String home(Model model) {
        try {
            List<BoardResponseDto> recentBoards = boardService.getRecentBoards();
            List<BoardResponseDto> popularBoards = boardService.getPopularBoards();
            
            model.addAttribute("recentBoards", recentBoards);
            model.addAttribute("popularBoards", popularBoards);
        } catch (Exception e) {
            // 데이터베이스 연결 실패 시에도 페이지는 보여줌
            model.addAttribute("recentBoards", List.of());
            model.addAttribute("popularBoards", List.of());
        }
        
        return "index";
    }
}

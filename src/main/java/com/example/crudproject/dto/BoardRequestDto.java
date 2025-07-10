package com.example.crudproject.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardRequestDto {
    
    private String title;
    private String author;
    private String content;
    
    // 유효성 검증 메서드
    public boolean isValid() {
        return title != null && !title.trim().isEmpty() 
            && author != null && !author.trim().isEmpty()
            && content != null && !content.trim().isEmpty();
    }
}

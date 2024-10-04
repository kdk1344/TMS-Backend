package com.tms.backend.vo;

import org.springframework.web.util.UriComponentsBuilder;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class Criteria {
    private int page;
    private int perPageNum;
    private String userName; // 사용자명 검색
    private String authorityName; // 권한명 검색

    public Criteria() { // 사용자 데이터 용 페이지 정보
        this.page = 1;
        this.perPageNum = 10;
    }
    
    // 비즈니스 로직: 페이지 시작 인덱스를 계산하는 메서드는 유지
    public int getPageStart() {
        return (this.page - 1) * perPageNum;
    }
}
package com.tms.backend.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PageDTO {
    private int total; // 페이지 총 숫자
    private Criteria criteria; // 페이지 정보를 담은 Criteria

    public PageDTO(int total, Criteria criteria) { // 페이지 정보
        this.total = total;
        this.criteria = criteria;
    }
}
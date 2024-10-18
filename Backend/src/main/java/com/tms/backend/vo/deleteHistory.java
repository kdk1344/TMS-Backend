package com.tms.backend.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
public class deleteHistory {
	private Integer seq;                     // SEQ
    private String programId;            // 프로그램 ID
    private String programName;          // 프로그램명
    private String deletionHandler;      // 삭제처리자 ID
    private Date deletionDate;           // 삭제처리일 (YYYY-MM-DD)
    private String deletionReason;       // 삭제처리 사유
}

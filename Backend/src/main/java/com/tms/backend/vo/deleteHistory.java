package com.tms.backend.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
public class deleteHistory {
	private Integer seq;                     // SEQ
    private String programId;            // ���α׷� ID
    private String programName;          // ���α׷���
    private String deletionHandler;      // ����ó���� ID
    private Date deletionDate;           // ����ó���� (YYYY-MM-DD)
    private String deletionReason;       // ����ó�� ����
}

package com.tms.backend.vo;

import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
public class devProgress {
	private int seq;                  // SEQ
    private String majorCategory;     // 업무 대분류
    private String subCategory;       // 업무 중분류
    private String programType;       // 프로그램 구분
    private String programId;         // 프로그램 ID
    private String programName;       // 프로그램명
    private String className;         // 클래스명
    private String screenId;          // 화면 ID
    private String screenName;        // 화면명
    private String screenMenuPath;    // 화면메뉴경로
    private String priority;          // 우선순위 (공통코드)
    private String difficulty;        // 난이도 (공통코드)
    private int estimatedEffort;      // 예상 공수
    private String regStatus;         // 프로그램 상태 (공통코드)
    private String reqId;             // 요구사항 ID
    private String reqName;           // 요구사항명
    private String deletionHandler;   // 삭제처리자
    private String deletionDate;      // 삭제처리일 (YYYY-MM-DD)
    private String deletionReason;    // 삭제처리 사유
    private String developer;         // 개발자
    private String plannedStartDate;  // 시작 예정일 (YYYY-MM-DD)
    private String plannedEndDate;    // 완료 예정일 (YYYY-MM-DD)
    private String actualStartDate;   // 시작일 (YYYY-MM-DD)
    private String actualEndDate;     // 완료일 (YYYY-MM-DD)
    private String devProgAttachment; // 단테증적 첨부파일
    private String pl;                // PL
    private String plTestDate;        // PL 테스트 예정일 (YYYY-MM-DD)
    private String plConfirmDate;     // PL 테스트 완료일 (YYYY-MM-DD)
    private String plTestNotes;       // PL 테스트 의견
    private String custItMgr;         // IT 담당자
    private String custItTestDate;    // IT 테스트 예정일 (YYYY-MM-DD)
    private String custItConfirmDate; // IT 테스트 완료일 (YYYY-MM-DD)
    private String custItTestNotes;   // IT 테스트 의견
    private String custBusiMgr;       // 현업 담당자
    private String custBusiTestDate;  // 현업 테스트 예정일 (YYYY-MM-DD)
    private String custBusiConfirmDate;// 현업 테스트 완료일 (YYYY-MM-DD)
    private String custBusiTestNotes; // 현업 테스트 의견
    private String thirdPartyTestMgr; // 3자 테스트 담당자
    private String thirdPartyTestDate;// 3자 테스트 예정일 (YYYY-MM-DD)
    private String thirdPartyConfirmDate;// 3자 테스트 완료일 (YYYY-MM-DD)
    private String thirdPartyTestNotes;// 3자 테스트 의견
    private String devStatus;         // 개발 진행 상태 (공통코드)
    private String initRegDate;       // 최초 등록일시
    private String initRegistrar;     // 최초 등록자
    private String lastModifiedDate;  // 최종 변경일시
    private String lastModifier;      // 최종 변경자
    
    
	

}

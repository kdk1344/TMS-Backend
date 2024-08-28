package com.tms.backend.vo;

import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
public class devProgress {
	private int seq;                     // SEQ
    private String majorCategory;        // 업무 대분류
    private String subCategory;          // 업무 중분류
    private String minorCategory;        // 소분류
    private String programDetailType;    // 프로그램 구분 (상세)
    private String programType;          // 프로그램 구분 (유형)
    private String programId;            // 프로그램 ID
    private String programName;          // 프로그램명
    private String className;            // 클래스명
    private String screenId;             // 화면 ID
    private String screenName;           // 화면명
    private String screenMenuPath;       // 화면메뉴경로
    private String priority;             // 우선순위 (공통코드)
    private String difficulty;           // 난이도 (공통코드)
    private int estimatedEffort;         // 예상 공수
    private String programStatus;            // 프로그램 상태 (공통코드)
    private String reqId;                // 요구사항 ID
    private String deletionHandler;      // 삭제처리자 ID
    private String deletionDate;         // 삭제처리일 (YYYY-MM-DD)
    private String deletionReason;       // 삭제처리 사유
    private String developer;            // 개발자 ID
    private String plannedStartDate;     // 시작 예정일 (YYYY-MM-DD)
    private String plannedEndDate;       // 완료 예정일 (YYYY-MM-DD)
    private String actualStartDate;      // 시작일 (YYYY-MM-DD)
    private String actualEndDate;        // 완료일 (YYYY-MM-DD)
    private String devProgAttachment;    // 단테증적 첨부파일
    private String pl;                   // PL ID
    private String plTestScdDate;        // PL 테스트 예정일 (YYYY-MM-DD)
    private String plTestCmpDate;        // PL 테스트 완료일 (YYYY-MM-DD)
    private String plTestResult;         // PL 테스트 결과 (공통코드)
    private String plTestNotes;          // PL 테스트 의견
    private String itMgr;                // IT 담당자 ID
    private String itTestDate;           // IT 테스트 예정일 (YYYY-MM-DD)
    private String itConfirmDate;        // IT 테스트 완료일 (YYYY-MM-DD)
    private String itTestResult;         // IT 테스트 결과 (공통코드)
    private String itTestNotes;          // IT 테스트 의견
    private String busiMgr;              // 현업 담당자 ID
    private String busiTestDate;         // 현업 테스트 예정일 (YYYY-MM-DD)
    private String busiConfirmDate;      // 현업 테스트 완료일 (YYYY-MM-DD)
    private String busiTestResult;       // 현업 테스트 결과 (공통코드)
    private String busiTestNotes;        // 현업 테스트 의견
    private String thirdPartyTestMgr;    // 3자 테스트 담당자 ID
    private String thirdPartyTestDate;   // 3자 테스트 예정일 (YYYY-MM-DD)
    private String thirdPartyConfirmDate;// 3자 테스트 완료일 (YYYY-MM-DD)
    private String thirdTestResult;      // 제3자 테스트 결과 (공통코드)
    private String thirdPartyTestNotes;  // 제3자 테스트 의견
    private String devStatus;            // 개발 진행 상태 (공통코드)
    private String initRegDate;          // 최초 등록일시 (시스템 일/시/분/초)
    private String initRegistrar;        // 최초 등록자 ID
    private String lastModifiedDate;     // 최종 변경일시 (시스템 일/시/분/초)
    private String lastModifier;         // 최종 변경자 ID
    
    
	

}

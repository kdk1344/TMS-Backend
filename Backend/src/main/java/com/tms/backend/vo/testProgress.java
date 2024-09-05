package com.tms.backend.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
public class testProgress {
	private Integer seq;                           // 테스트 SEQ (PK)
    private String testStage;                      // 테스트단계
    private String majorCategory;                  // 업무대분류
    private String subCategory;                    // 업무중분류
    private String minorCategory;                  // 업무소분류
    private String testId;                         // 테스트ID
    private String testScenarioName;               // 테스트시나리오명
    private String testCaseName;                   // 테스트케이스명
    private String testStepName;                   // 테스트스텝명
    private String programType;                    // 프로그램구분
    private String programId;                      // 프로그램ID
    private String programName;                    // 프로그램명
    private String screenMenuPath;                 // 화면메뉴경로
    private String executeProcedure;               // 수행 절차
    private String inputData;                      // 입력 데이터
    private String expectedResult;                 // 예상 결과
    private String actualResult;                   // 실제 결과
    private String developer;                      // 개발자
    private String pl;                             // PL
    private String execCompanyMgr;                 // 수행사 담당자
    private Date execCompanyTestDate;              // 수행사 완료예정일
    private Date execCompanyConfirmDate;           // 수행사 완료일
    private String execCompanyTestResult;          // 수행사 테스트결과
    private String execCompanyTestNotes;           // 수행사 테스트의견
    private String thirdPartyTestMgr;              // 제3자 담당자
    private Date thirdPartyTestDate;               // 제3자 완료예정일
    private Date thirdPartyConfirmDate;            // 제3자 완료일
    private String thirdTestResult;                // 제3자 테스트결과
    private String thirdPartyTestNotes;            // 제3자 테스트의견
    private String itMgr;                          // 고객 IT 담당자
    private Date itTestDate;                       // 고객 IT 시작일
    private Date itConfirmDate;                    // 고객 IT 완료일
    private String itTestResult;                   // 고객 IT 테스트결과
    private String itTestNotes;                    // 고객 IT 테스트의견
    private String busiMgr;                        // 고객 현업 담당자
    private Date busiTestDate;                     // 고객 현업 시작일
    private Date busiConfirmDate;                  // 고객 현업 완료일
    private String busiTestResult;                 // 고객 현업 테스트결과
    private String busiTestNotes;                  // 고객 현업 테스트의견
    private String testStatus;                     // 테스트 진행 상태
    private LocalDateTime initRegDate;                      // 최초 등록일시
    private String initRegistrar;                  // 최초 등록자
    private LocalDateTime lastModifiedDate;                 // 최종 변경일시
    private String lastModifier;                   // 최종 변경자
    private String execCompanyAttachments;         // 수행사 증적 첨부
    private String thirdAttachments;               // 제3자 증적 첨부
    
    
	

}

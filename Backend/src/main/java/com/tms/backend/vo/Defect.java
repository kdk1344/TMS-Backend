package com.tms.backend.vo;

import lombok.Data;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class Defect {
    private Integer seq;                       // 결함SEQ
    private String testStage;                    // 테스트단계
    private String majorCategory;                // 업무대분류
    private String subCategory;                  // 업무중분류
    private String testId;                       // 테스트ID
    private String programType;                  // 프로그램구분
    private String programId;                    // 프로그램ID
    private String programName;                  // 프로그램명
    private String defectType;                   // 결함 유형
    private String defectSeverity;               // 결함 심각도
    private String defectDescription;            // 결함 내용
    private String defectRegistrar;              // 결함 등록자
    private Date defectDiscoveryDate;                  // 결함 등록일
    private String defectHandler;                // 결함 조치 담당자
    private Date defectScheduledDate;            // 결함 조치 예정일
    private Date defectCompletionDate;           // 결함 조치 완료일
    private String defectResolutionDetails;      // 결함 조치 내역
    private String pl;                            // PL
    private Date plConfirmDate;                  // PL 확인일
    private int originalDefectNumber;         // 기 발생 결함번호
    private String plDefectJudgeClass;           // PL결함판단구분
    private String plComments;                   // PL의견
    private Date defectRegConfirmDate;           // 결함 등록자 확인일
    private String defectRegistrarComment;       // 결함 등록자 의견
    private String defectStatus;                 // 결함 처리 상태
    private Date initCreatedDate;                // 최초 등록일시
    private String initCreater;                  // 최초 등록자
    private Date lastModifiedDate;               // 최종 변경일시
    private String lastModifier;                 // 최종 변경자
    private List<FileAttachment> defectAttachment;            // 결함 첨부파일
    private List<FileAttachment> defectFixAttachments;         // 결함 조치 첨부파일

    
}
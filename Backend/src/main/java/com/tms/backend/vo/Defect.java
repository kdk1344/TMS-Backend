package com.tms.backend.vo;

import lombok.Data;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class Defect {
	private Long defectSeq;                   // 결함 SEQ
    private String testCategory;              // 테스트단계
    private String testId;                    // 테스트ID
    private String programType;               // 프로그램구분 (공통코드)
    private String programId;                 // 프로그램ID
    private String programName;               // 프로그램명
    private String defectType;                // 결함 유형 (공통코드)
    private String defectSeverity;            // 결함 심각도 (공통코드)
    private String defectDescription;         // 결함 내용
    private String defectAttachment;          // 결함 첨부파일
    private String actualResult;              // 실제 결과
    private String defectRegistrar;           // 결함 등록자 (ID)
    private Date defectRegDate;               // 결함 등록일 (YYYY-MM-DD)
    private String defectHandler;             // 결함 조치 담당자 (ID)
    private Date defectScheduledDate;         // 결함 조치 예정일 (YYYY-MM-DD)
    private Date defectCompletionDate;        // 결함 조치 완료일 (YYYY-MM-DD)
    private String defectResolutionDetails;   // 결함 조치 내역
    private String projectLeader;             // PL (ID)
    private Date plConfirmDate;               // PL 확인일 (YYYY-MM-DD)
    private Long originalDefectNumber;        // 기 발생 결함번호
    private String plComments;                // PL의견
    private Date defectRegConfirmDate;        // 결함 등록자 확인일 (YYYY-MM-DD)
    private String defectStatus;              // 결함 처리 상태 (공통코드)
    private Date initCreatedDate;             // 최초 등록일시 (시스템 일/시/분/초)
    private String initCreater;               // 최초 등록자 (ID)
    private Date lastModifiedDate;            // 최종 변경일시 (시스템 일/시/분/초)
    private String lastModifier;              // 최종 변경자 (ID)

    // Getters and Setters
    public Long getDefectSeq() {
        return defectSeq;
    }

    public void setDefectSeq(Long defectSeq) {
        this.defectSeq = defectSeq;
    }

    public String getTestCategory() {
        return testCategory;
    }

    public void setTestCategory(String testCategory) {
        this.testCategory = testCategory;
    }

    public String getTestId() {
        return testId;
    }

    public void setTestId(String testId) {
        this.testId = testId;
    }

    public String getProgramType() {
        return programType;
    }

    public void setProgramType(String programType) {
        this.programType = programType;
    }

    public String getProgramId() {
        return programId;
    }

    public void setProgramId(String programId) {
        this.programId = programId;
    }

    public String getProgramName() {
        return programName;
    }

    public void setProgramName(String programName) {
        this.programName = programName;
    }

    public String getDefectType() {
        return defectType;
    }

    public void setDefectType(String defectType) {
        this.defectType = defectType;
    }

    public String getDefectSeverity() {
        return defectSeverity;
    }

    public void setDefectSeverity(String defectSeverity) {
        this.defectSeverity = defectSeverity;
    }

    public String getDefectDescription() {
        return defectDescription;
    }

    public void setDefectDescription(String defectDescription) {
        this.defectDescription = defectDescription;
    }

    public String getDefectAttachment() {
        return defectAttachment;
    }

    public void setDefectAttachment(String defectAttachment) {
        this.defectAttachment = defectAttachment;
    }

    public String getActualResult() {
        return actualResult;
    }

    public void setActualResult(String actualResult) {
        this.actualResult = actualResult;
    }

    public String getDefectRegistrar() {
        return defectRegistrar;
    }

    public void setDefectRegistrar(String defectRegistrar) {
        this.defectRegistrar = defectRegistrar;
    }

    public Date getDefectRegDate() {
        return defectRegDate;
    }

    public void setDefectRegDate(Date defectRegDate) {
        this.defectRegDate = defectRegDate;
    }

    public String getDefectHandler() {
        return defectHandler;
    }

    public void setDefectHandler(String defectHandler) {
        this.defectHandler = defectHandler;
    }

    public Date getDefectScheduledDate() {
        return defectScheduledDate;
    }

    public void setDefectScheduledDate(Date defectScheduledDate) {
        this.defectScheduledDate = defectScheduledDate;
    }

    public Date getDefectCompletionDate() {
        return defectCompletionDate;
    }

    public void setDefectCompletionDate(Date defectCompletionDate) {
        this.defectCompletionDate = defectCompletionDate;
    }

    public String getDefectResolutionDetails() {
        return defectResolutionDetails;
    }

    public void setDefectResolutionDetails(String defectResolutionDetails) {
        this.defectResolutionDetails = defectResolutionDetails;
    }

    public String getProjectLeader() {
        return projectLeader;
    }

    public void setProjectLeader(String projectLeader) {
        this.projectLeader = projectLeader;
    }

    public Date getPlConfirmDate() {
        return plConfirmDate;
    }

    public void setPlConfirmDate(Date plConfirmDate) {
        this.plConfirmDate = plConfirmDate;
    }

    public Long getOriginalDefectNumber() {
        return originalDefectNumber;
    }

    public void setOriginalDefectNumber(Long originalDefectNumber) {
        this.originalDefectNumber = originalDefectNumber;
    }

    public String getPlComments() {
        return plComments;
    }

    public void setPlComments(String plComments) {
        this.plComments = plComments;
    }

    public Date getDefectRegConfirmDate() {
        return defectRegConfirmDate;
    }

    public void setDefectRegConfirmDate(Date defectRegConfirmDate) {
        this.defectRegConfirmDate = defectRegConfirmDate;
    }

    public String getDefectStatus() {
        return defectStatus;
    }

    public void setDefectStatus(String defectStatus) {
        this.defectStatus = defectStatus;
    }

    public Date getInitCreatedDate() {
        return initCreatedDate;
    }

    public void setInitCreatedDate(Date initCreatedDate) {
        this.initCreatedDate = initCreatedDate;
    }

    public String getInitCreater() {
        return initCreater;
    }

    public void setInitCreater(String initCreater) {
        this.initCreater = initCreater;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getLastModifier() {
        return lastModifier;
    }

    public void setLastModifier(String lastModifier) {
        this.lastModifier = lastModifier;
    }
}
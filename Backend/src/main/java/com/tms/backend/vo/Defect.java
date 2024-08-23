package com.tms.backend.vo;

import lombok.Data;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class Defect {
	private Long defectSeq;                   // ���� SEQ
    private String testCategory;              // �׽�Ʈ�ܰ�
    private String testId;                    // �׽�ƮID
    private String programType;               // ���α׷����� (�����ڵ�)
    private String programId;                 // ���α׷�ID
    private String programName;               // ���α׷���
    private String defectType;                // ���� ���� (�����ڵ�)
    private String defectSeverity;            // ���� �ɰ��� (�����ڵ�)
    private String defectDescription;         // ���� ����
    private String defectAttachment;          // ���� ÷������
    private String actualResult;              // ���� ���
    private String defectRegistrar;           // ���� ����� (ID)
    private Date defectRegDate;               // ���� ����� (YYYY-MM-DD)
    private String defectHandler;             // ���� ��ġ ����� (ID)
    private Date defectScheduledDate;         // ���� ��ġ ������ (YYYY-MM-DD)
    private Date defectCompletionDate;        // ���� ��ġ �Ϸ��� (YYYY-MM-DD)
    private String defectResolutionDetails;   // ���� ��ġ ����
    private String projectLeader;             // PL (ID)
    private Date plConfirmDate;               // PL Ȯ���� (YYYY-MM-DD)
    private Long originalDefectNumber;        // �� �߻� ���Թ�ȣ
    private String plComments;                // PL�ǰ�
    private Date defectRegConfirmDate;        // ���� ����� Ȯ���� (YYYY-MM-DD)
    private String defectStatus;              // ���� ó�� ���� (�����ڵ�)
    private Date initCreatedDate;             // ���� ����Ͻ� (�ý��� ��/��/��/��)
    private String initCreater;               // ���� ����� (ID)
    private Date lastModifiedDate;            // ���� �����Ͻ� (�ý��� ��/��/��/��)
    private String lastModifier;              // ���� ������ (ID)

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
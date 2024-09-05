package com.tms.backend.vo;

import lombok.Data;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class Defect {
    private Integer seq;                       // ����SEQ
    private String testStage;                    // �׽�Ʈ�ܰ�
    private String majorCategory;                // ������з�
    private String subCategory;                  // �����ߺз�
    private String testId;                       // �׽�ƮID
    private String programType;                  // ���α׷�����
    private String programId;                    // ���α׷�ID
    private String programName;                  // ���α׷���
    private String defectType;                   // ���� ����
    private String defectSeverity;               // ���� �ɰ���
    private String defectDescription;            // ���� ����
    private String defectRegistrar;              // ���� �����
    private Date defectDiscoveryDate;                  // ���� �����
    private String defectHandler;                // ���� ��ġ �����
    private Date defectScheduledDate;            // ���� ��ġ ������
    private Date defectCompletionDate;           // ���� ��ġ �Ϸ���
    private String defectResolutionDetails;      // ���� ��ġ ����
    private String pl;                            // PL
    private Date plConfirmDate;                  // PL Ȯ����
    private int originalDefectNumber;         // �� �߻� ���Թ�ȣ
    private String plDefectJudgeClass;           // PL�����Ǵܱ���
    private String plComments;                   // PL�ǰ�
    private Date defectRegConfirmDate;           // ���� ����� Ȯ����
    private String defectRegistrarComment;       // ���� ����� �ǰ�
    private String defectStatus;                 // ���� ó�� ����
    private Date initCreatedDate;                // ���� ����Ͻ�
    private String initCreater;                  // ���� �����
    private Date lastModifiedDate;               // ���� �����Ͻ�
    private String lastModifier;                 // ���� ������
    private List<FileAttachment> defectAttachment;            // ���� ÷������
    private List<FileAttachment> defectFixAttachments;         // ���� ��ġ ÷������

    
}
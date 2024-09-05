package com.tms.backend.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
public class testProgress {
	private Integer seq;                           // �׽�Ʈ SEQ (PK)
    private String testStage;                      // �׽�Ʈ�ܰ�
    private String majorCategory;                  // ������з�
    private String subCategory;                    // �����ߺз�
    private String minorCategory;                  // �����Һз�
    private String testId;                         // �׽�ƮID
    private String testScenarioName;               // �׽�Ʈ�ó�������
    private String testCaseName;                   // �׽�Ʈ���̽���
    private String testStepName;                   // �׽�Ʈ���ܸ�
    private String programType;                    // ���α׷�����
    private String programId;                      // ���α׷�ID
    private String programName;                    // ���α׷���
    private String screenMenuPath;                 // ȭ��޴����
    private String executeProcedure;               // ���� ����
    private String inputData;                      // �Է� ������
    private String expectedResult;                 // ���� ���
    private String actualResult;                   // ���� ���
    private String developer;                      // ������
    private String pl;                             // PL
    private String execCompanyMgr;                 // ����� �����
    private Date execCompanyTestDate;              // ����� �ϷΌ����
    private Date execCompanyConfirmDate;           // ����� �Ϸ���
    private String execCompanyTestResult;          // ����� �׽�Ʈ���
    private String execCompanyTestNotes;           // ����� �׽�Ʈ�ǰ�
    private String thirdPartyTestMgr;              // ��3�� �����
    private Date thirdPartyTestDate;               // ��3�� �ϷΌ����
    private Date thirdPartyConfirmDate;            // ��3�� �Ϸ���
    private String thirdTestResult;                // ��3�� �׽�Ʈ���
    private String thirdPartyTestNotes;            // ��3�� �׽�Ʈ�ǰ�
    private String itMgr;                          // �� IT �����
    private Date itTestDate;                       // �� IT ������
    private Date itConfirmDate;                    // �� IT �Ϸ���
    private String itTestResult;                   // �� IT �׽�Ʈ���
    private String itTestNotes;                    // �� IT �׽�Ʈ�ǰ�
    private String busiMgr;                        // �� ���� �����
    private Date busiTestDate;                     // �� ���� ������
    private Date busiConfirmDate;                  // �� ���� �Ϸ���
    private String busiTestResult;                 // �� ���� �׽�Ʈ���
    private String busiTestNotes;                  // �� ���� �׽�Ʈ�ǰ�
    private String testStatus;                     // �׽�Ʈ ���� ����
    private LocalDateTime initRegDate;                      // ���� ����Ͻ�
    private String initRegistrar;                  // ���� �����
    private LocalDateTime lastModifiedDate;                 // ���� �����Ͻ�
    private String lastModifier;                   // ���� ������
    private String execCompanyAttachments;         // ����� ���� ÷��
    private String thirdAttachments;               // ��3�� ���� ÷��
    
    
	

}

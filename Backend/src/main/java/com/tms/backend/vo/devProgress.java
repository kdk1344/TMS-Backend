package com.tms.backend.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
public class devProgress {
	private int seq;                     // SEQ
    private String majorCategory;        // ���� ��з�
    private String subCategory;          // ���� �ߺз�
    private String minorCategory;        // �Һз�
    private String programDetailType;    // ���α׷� ���� (��)
    private String programType;          // ���α׷� ���� (����)
    private String programId;            // ���α׷� ID
    private String programName;          // ���α׷���
    private String className;            // Ŭ������
    private String screenId;             // ȭ�� ID
    private String screenName;           // ȭ���
    private String screenMenuPath;       // ȭ��޴����
    private String priority;             // �켱���� (�����ڵ�)
    private String difficulty;           // ���̵� (�����ڵ�)
    private int estimatedEffort;         // ���� ����
    private String programStatus;        // ���α׷� ���� (�����ڵ�)
    private String reqId;                // �䱸���� ID
    private String deletionHandler;      // ����ó���� ID
    private Date deletionDate;           // ����ó���� (YYYY-MM-DD)
    private String deletionReason;       // ����ó�� ����
    private String developer;            // ������ ID
    private Date plannedStartDate;       // ���� ������ (YYYY-MM-DD)
    private Date plannedEndDate;         // �Ϸ� ������ (YYYY-MM-DD)
    private Date actualStartDate;        // ������ (YYYY-MM-DD)
    private Date actualEndDate;          // �Ϸ��� (YYYY-MM-DD)
    private Date devtestendDate;         // ������ �����׽�Ʈ �Ϸ��� (YYYY-MM-DD)
    private List<FileAttachment> devProgAttachment;    // �������� ÷������
    private String pl;                   // PL ID
    private Date plTestScdDate;          // PL �׽�Ʈ ������ (YYYY-MM-DD)
    private Date plTestCmpDate;          // PL �׽�Ʈ �Ϸ��� (YYYY-MM-DD)
    private String plTestResult;         // PL �׽�Ʈ ��� (�����ڵ�)
    private String plTestNotes;          // PL �׽�Ʈ �ǰ�
    private String itMgr;                // IT ����� ID
    private Date itTestDate;             // IT �׽�Ʈ ������ (YYYY-MM-DD)
    private Date itConfirmDate;          // IT �׽�Ʈ �Ϸ��� (YYYY-MM-DD)
    private String itTestResult;         // IT �׽�Ʈ ��� (�����ڵ�)
    private String itTestNotes;          // IT �׽�Ʈ �ǰ�
    private String busiMgr;              // ���� ����� ID
    private Date busiTestDate;           // ���� �׽�Ʈ ������ (YYYY-MM-DD)
    private Date busiConfirmDate;        // ���� �׽�Ʈ �Ϸ��� (YYYY-MM-DD)
    private String busiTestResult;       // ���� �׽�Ʈ ��� (�����ڵ�)
    private String busiTestNotes;        // ���� �׽�Ʈ �ǰ�
    private String thirdPartyTestMgr;    // 3�� �׽�Ʈ ����� ID
    private Date thirdPartyTestDate;     // 3�� �׽�Ʈ ������ (YYYY-MM-DD)
    private Date thirdPartyConfirmDate;  // 3�� �׽�Ʈ �Ϸ��� (YYYY-MM-DD)
    private String thirdTestResult;      // ��3�� �׽�Ʈ ��� (�����ڵ�)
    private String thirdPartyTestNotes;  // ��3�� �׽�Ʈ �ǰ�
    private String devStatus;            // ���� ���� ���� (�����ڵ�)
    private LocalDateTime initRegDate;            // ���� ����Ͻ� (�ý��� ��/��/��/��)
    private String initRegistrar;        // ���� ����� ID
    private LocalDateTime lastModifiedDate;       // ���� �����Ͻ� (�ý��� ��/��/��/��)
    private String lastModifier;         // ���� ������ ID
    
    
	

}

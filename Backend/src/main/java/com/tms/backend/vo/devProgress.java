package com.tms.backend.vo;

import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
public class devProgress {
	private int seq;                  // SEQ
    private String majorCategory;     // ���� ��з�
    private String subCategory;       // ���� �ߺз�
    private String programType;       // ���α׷� ����
    private String programId;         // ���α׷� ID
    private String programName;       // ���α׷���
    private String className;         // Ŭ������
    private String screenId;          // ȭ�� ID
    private String screenName;        // ȭ���
    private String screenMenuPath;    // ȭ��޴����
    private String priority;          // �켱���� (�����ڵ�)
    private String difficulty;        // ���̵� (�����ڵ�)
    private int estimatedEffort;      // ���� ����
    private String regStatus;         // ���α׷� ���� (�����ڵ�)
    private String reqId;             // �䱸���� ID
    private String reqName;           // �䱸���׸�
    private String deletionHandler;   // ����ó����
    private String deletionDate;      // ����ó���� (YYYY-MM-DD)
    private String deletionReason;    // ����ó�� ����
    private String developer;         // ������
    private String plannedStartDate;  // ���� ������ (YYYY-MM-DD)
    private String plannedEndDate;    // �Ϸ� ������ (YYYY-MM-DD)
    private String actualStartDate;   // ������ (YYYY-MM-DD)
    private String actualEndDate;     // �Ϸ��� (YYYY-MM-DD)
    private String devProgAttachment; // �������� ÷������
    private String pl;                // PL
    private String plTestDate;        // PL �׽�Ʈ ������ (YYYY-MM-DD)
    private String plConfirmDate;     // PL �׽�Ʈ �Ϸ��� (YYYY-MM-DD)
    private String plTestNotes;       // PL �׽�Ʈ �ǰ�
    private String custItMgr;         // IT �����
    private String custItTestDate;    // IT �׽�Ʈ ������ (YYYY-MM-DD)
    private String custItConfirmDate; // IT �׽�Ʈ �Ϸ��� (YYYY-MM-DD)
    private String custItTestNotes;   // IT �׽�Ʈ �ǰ�
    private String custBusiMgr;       // ���� �����
    private String custBusiTestDate;  // ���� �׽�Ʈ ������ (YYYY-MM-DD)
    private String custBusiConfirmDate;// ���� �׽�Ʈ �Ϸ��� (YYYY-MM-DD)
    private String custBusiTestNotes; // ���� �׽�Ʈ �ǰ�
    private String thirdPartyTestMgr; // 3�� �׽�Ʈ �����
    private String thirdPartyTestDate;// 3�� �׽�Ʈ ������ (YYYY-MM-DD)
    private String thirdPartyConfirmDate;// 3�� �׽�Ʈ �Ϸ��� (YYYY-MM-DD)
    private String thirdPartyTestNotes;// 3�� �׽�Ʈ �ǰ�
    private String devStatus;         // ���� ���� ���� (�����ڵ�)
    private String initRegDate;       // ���� ����Ͻ�
    private String initRegistrar;     // ���� �����
    private String lastModifiedDate;  // ���� �����Ͻ�
    private String lastModifier;      // ���� ������
    
    
	

}

package com.tms.backend.vo;

import java.util.Date;

import lombok.Data;

@Data
public class FileAttachment {
	
	private Integer type; // ������ ���� �Խ���
    private Integer identifier; // ������ ���� �ĺ���
    private Integer seq; // ���������� SEQ�� ����
    private String storageLocation; // ���� ���� ���
    private String fileName; // ���ϸ�
    private Date createdDate; // ���� �����Ͻ�

}


package com.tms.backend.service;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

import javax.servlet.http.HttpSession;

import org.apache.poi.ss.formula.functions.T;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.tms.backend.controller.UserController;
import com.tms.backend.mapper.AdminMapper;
import com.tms.backend.mapper.DefectMapper;
import com.tms.backend.mapper.DevMapper;
import com.tms.backend.mapper.FileAttachmentMapper;
import com.tms.backend.mapper.UserMapper;
import com.tms.backend.vo.CommonCode;
import com.tms.backend.vo.Criteria;
import com.tms.backend.vo.Defect;
import com.tms.backend.vo.FileAttachment;
import com.tms.backend.vo.Notice;
import com.tms.backend.vo.User;
import com.tms.backend.vo.categoryCode;
import com.tms.backend.vo.devProgress;

import lombok.extern.log4j.Log4j;

@Service
@Log4j
public class FileService {
	
	@Autowired
    private AdminService adminService;
	
	// ÷������ ���� �� ����ǰ� ���Ǵ� �ڵ带 �����ϴ� Service
	
	//÷������ ���ε�
    public void handleFileUpload(MultipartFile[] files, String boardType, int identifier) {
        List<FileAttachment> attachments = new ArrayList<>();
        log.info(boardType + "÷�� üũ" + identifier);
        // boardType�� ���� boardTypeNumber ����
        Integer boardTypeNumber;
        switch (boardType) {
	        case "devProgress": // ���α׷� ���� ���� ��Ȳ ÷������
	            boardTypeNumber = 1;
	            break;
	        case "testProgress": //�׽�Ʈ �ó����� ÷������
	            boardTypeNumber = 21;
	            break;
	        case "testProgressThird": //�׽�Ʈ �ó����� ��3�� ÷������
	            boardTypeNumber = 22;
	            break;
	        case "defect": // ���� ÷������
	            boardTypeNumber = 31;
	            break;
	        case "defectFix": // ���� ��ġ ��� ÷������
	            boardTypeNumber = 32;
	            break;
	        case "nt": // �������� ÷������
	            boardTypeNumber = 4;
	            break;
            default:
                boardTypeNumber = 0; // �⺻�� (�� �� ���� boardType), ���� ����
                break;
        }
        
        for (MultipartFile file : files) {
	        if (file != null && !file.isEmpty()) {
	            try {
	            	String fileType = getFileType(file.getContentType()); // ���� Ÿ��
	                String storageLocation = getStorageLocation(fileType, file.getOriginalFilename()); // ���� ���� ���
	
	                File destinationFile = new File(storageLocation);
	                file.transferTo(destinationFile);
	                
	                // ÷������ ���̺� ����
	                FileAttachment attachment = new FileAttachment();
	                attachment.setIdentifier(identifier);
	                attachment.setType(boardTypeNumber);
	                attachment.setStorageLocation(storageLocation);
	                attachment.setFileName(file.getOriginalFilename());
	
	                attachments.add(attachment);
	            } catch (IOException e) {
	                log.error("File upload failed", e);
	            }
	        } else {
	        }
        }
        adminService.saveAttachments(attachments); // ÷������ ����
    }
    
    // ÷������ ���� ��� ����
    public String getStorageLocation(String fileType, String fileName) {
        String baseDir = "C:\\Users\\User\\Desktop\\TMS_DEV\\";
        String storageLocation;
        switch (fileType) {
        case "IMAGE":
            storageLocation = baseDir + "images/";
            break;
        case "DOCUMENT":
            storageLocation = baseDir + "documents/";
            break;
        default:
            storageLocation = baseDir + "others/";
            break;
            }

	    // ���丮�� �������� ������ ����
	    File directory = new File(storageLocation);
	    if (!directory.exists()) {
	        if (directory.mkdirs()) {
	            System.out.println("���丮�� �����Ǿ����ϴ�: " + storageLocation);
	        } else {
	            System.out.println("���丮 ������ �����߽��ϴ�: " + storageLocation);
	        }
	        }

    return storageLocation + fileName;
    }
    
    // ÷������ ���� ����
    private String getFileType(String contentType) {
        if (contentType != null && contentType.startsWith("image/")) {
            return "IMAGE";
        } else if (contentType != null && contentType.startsWith("application/pdf")) {
            return "DOCUMENT";
        } else {
            return "OTHER";
        }
    }
    
    // ÷������ ���ڵ�
    public String encodeFileName(String fileName) throws UnsupportedEncodingException {
        return URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
    }
	
    
    // �׼� ��ȿ��� Ȯ��
    public boolean isHeaderValid(Row headerRow, List<String> expectedHeaders) {
        for (int i = 0; i < expectedHeaders.size(); i++) {
            Cell cell = headerRow.getCell(i);
            if (cell == null || !cell.getStringCellValue().trim().equalsIgnoreCase(expectedHeaders.get(i))) {
                return false;
            }
        }
        return true;
    }
    
    // �׼� Cell�� String ��ȯ 
    public String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return null;
        }
        return cell.getCellType() == CellType.STRING ? cell.getStringCellValue() : cell.toString();
    }
    
    // �׼� Cell�� Num ��ȯ
    public double getCellValueAsNumeric(Cell cell) {
        if (cell == null) {
            return 0;
        }
        return cell.getCellType() == CellType.NUMERIC ? cell.getNumericCellValue() : Double.parseDouble(cell.toString());
    }
    
    // �׼� Cell�� Date ��ȯ
    public Date getCellValueAsDate(Cell cell) {
        if (cell == null) {
            return null;
        }

        if (cell.getCellType() == CellType.NUMERIC) {
            // ��¥�� ���� ���·� ����Ǿ� ���� ��, ��¥�� ��ȯ
            if (DateUtil.isCellDateFormatted(cell)) {
                return cell.getDateCellValue();
            } else {
                return null;
            }
        } else if (cell.getCellType() == CellType.STRING) {
            // ���ڿ��� �Ǿ� �ִ� ���, "YYYY-MM-DD" ���� ���� ó��
            String dateStr = cell.getStringCellValue();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            try {
				return dateFormat.parse(dateStr);
			} catch (ParseException e) {
				e.printStackTrace();
			}
        }

        return null; // �ٸ� ������ ���� null ��ȯ
    }
    
    // String Value�� �ʼ��� ��ȿ�� ����
    public void validateRequiredField(String fieldValue, String fieldName) {
        if (fieldValue == null || fieldValue.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + "��(��) �ʼ� �Է� �׸��Դϴ�.");
        }
    }
    
    // Date Value�� ��ȿ�� ����
    public void validateDate(Date dateValue, String dateName) {
        if (dateValue == null) {
            throw new IllegalArgumentException(dateName + "��(��) �ʼ� �Է� �׸��Դϴ�.");
        }
    }
    
    // �׽�Ʈ �Ϸ����� ������ �׽�Ʈ ��� null üũ
    public void TestEndDateNullCheck(Date dateValue, String ResultValue, String dataName) {
	    if ((dateValue != null) && 
	        	(ResultValue == null || ResultValue.trim().isEmpty())) {
	        	throw new IllegalArgumentException(dataName + " �����׽�Ʈ ���� ��� '����' ���� '����'���� ����Ͻñ� �ٶ��ϴ�.");
	        }
    }
    
    // �Ϸ����� ��ϵǾ��µ� �׽�Ʈ ����� �̵�ϵ� ��� �޼���
    public void EndDateResultCheck(Date dateValue, String ResultValue, String dataName) {
	    if ((dateValue != null) && 
	        	(ResultValue == null || ResultValue.trim().isEmpty())) {
	        	throw new IllegalArgumentException(dataName + " �׽�Ʈ�� �׽�Ʈ ��� �׸��� �Էµ��� �ʾҽ��ϴ�.");
	        }
    }
    
    //�׽�Ʈ �Ϸ��� üũ�ؼ� �׽�Ʈ ������ Null�� ��� �ڵ� ����
    public void setIfNullDate(Date DateValue1, Date DateValue2, Consumer<Date> setDate) {
        if (DateValue1 != null && DateValue2 == null) {
            setDate.accept(DateValue1);
        }
    }
    
}
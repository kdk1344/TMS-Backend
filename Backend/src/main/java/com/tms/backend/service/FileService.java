
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

    public void handleFileUpload(MultipartFile[] files, String boardType, int identifier) {
        List<FileAttachment> attachments = new ArrayList<>();
        log.info(boardType + "첨부 체크" + identifier);
        // boardType에 따라 boardTypeNumber 설정
        Integer boardTypeNumber;
        switch (boardType) {
	        case "devProgress":
	            boardTypeNumber = 1;
	            break;
	        case "testProgress":
	            boardTypeNumber = 21;
	            break;
	        case "testProgressThird":
	            boardTypeNumber = 22;
	            break;
	        case "defect":
	            boardTypeNumber = 31;
	            break;
	        case "defectFix":
	            boardTypeNumber = 32;
	            break;
	        case "nt":
	            boardTypeNumber = 4;
	            break;
            default:
                boardTypeNumber = 0; // 기본값 (알 수 없는 boardType)
                break;
        }
        
        for (MultipartFile file : files) {
	        if (file != null && !file.isEmpty()) {
	            try {
	            	String fileType = getFileType(file.getContentType());
	            	log.info(fileType);
	                String storageLocation = getStorageLocation(fileType, file.getOriginalFilename());
	                log.info(storageLocation);
	                
	
	                File destinationFile = new File(storageLocation);
	                file.transferTo(destinationFile);
	
	                FileAttachment attachment = new FileAttachment();
	                attachment.setIdentifier(identifier);
	                attachment.setType(boardTypeNumber);
	                attachment.setStorageLocation(storageLocation);
	                attachment.setFileName(file.getOriginalFilename());
	
	                attachments.add(attachment);
	
	                log.info("File attached: " + file.getOriginalFilename() + " stored at " + storageLocation);
	            } catch (IOException e) {
	                log.error("File upload failed", e);
	            }
	        } else {
	            log.info("No file attached");
	        }
        }

        adminService.saveAttachments(attachments);
    }
    
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

	    // 디렉토리가 존재하지 않으면 생성
	    File directory = new File(storageLocation);
	    if (!directory.exists()) {
	        if (directory.mkdirs()) {
	            System.out.println("디렉토리가 생성되었습니다: " + storageLocation);
	        } else {
	            System.out.println("디렉토리 생성에 실패했습니다: " + storageLocation);
	        }
	        }

    return storageLocation + fileName;
    }
    
    
    private String getFileType(String contentType) {
        if (contentType != null && contentType.startsWith("image/")) {
            return "IMAGE";
        } else if (contentType != null && contentType.startsWith("application/pdf")) {
            return "DOCUMENT";
        } else {
            return "OTHER";
        }
    }
    
    public String encodeFileName(String fileName) throws UnsupportedEncodingException {
        return URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
    }
	
    
    // 액셀 유효헤더 확인
    public boolean isHeaderValid(Row headerRow, List<String> expectedHeaders) {
        for (int i = 0; i < expectedHeaders.size(); i++) {
            Cell cell = headerRow.getCell(i);
            if (cell == null || !cell.getStringCellValue().trim().equalsIgnoreCase(expectedHeaders.get(i))) {
                return false;
            }
        }
        return true;
    }
    
    // 액셀 Cell값 String 변환 
    public String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return null;
        }
        return cell.getCellType() == CellType.STRING ? cell.getStringCellValue() : cell.toString();
    }
    
    // 액셀 Cell값 Num 변환
    public double getCellValueAsNumeric(Cell cell) {
        if (cell == null) {
            return 0;
        }
        return cell.getCellType() == CellType.NUMERIC ? cell.getNumericCellValue() : Double.parseDouble(cell.toString());
    }
    
    // 액셀 Cell값 Date 변환
    public Date getCellValueAsDate(Cell cell) {
        if (cell == null) {
            return null;
        }

        if (cell.getCellType() == CellType.NUMERIC) {
            // 날짜가 숫자 형태로 저장되어 있을 때, 날짜로 변환
            if (DateUtil.isCellDateFormatted(cell)) {
                return cell.getDateCellValue();
            } else {
                return null;
            }
        } else if (cell.getCellType() == CellType.STRING) {
            // 문자열로 되어 있는 경우, "YYYY-MM-DD" 형식 등을 처리
            String dateStr = cell.getStringCellValue();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            try {
				return dateFormat.parse(dateStr);
			} catch (ParseException e) {
				e.printStackTrace();
			}
        }

        return null; // 다른 유형의 셀은 null 반환
    }
    
    // String Value값 필수값 유효성 점검
    public void validateRequiredField(String fieldValue, String fieldName) {
        if (fieldValue == null || fieldValue.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + "은(는) 필수 입력 항목입니다.");
        }
    }
    
    // Date Value값 유효성 점검
    public void validateDate(Date dateValue, String dateName) {
        if (dateValue == null) {
            throw new IllegalArgumentException(dateName + "은(는) 필수 입력 항목입니다.");
        }
    }
    
    // 테스트 완료일이 있을때 테스트 결과 null 체크
    public void TestEndDateNullCheck(Date dateValue, String ResultValue, String dataName) {
	    if ((dateValue != null) && 
	        	(ResultValue == null || ResultValue.trim().isEmpty())) {
	        	throw new IllegalArgumentException(dataName + " 단위테스트 수행 결과 '성공' 인지 '실패'인지 등록하시기 바랍니다.");
	        }
    }
    
    // 완료일이 등록되었는데 테스트 결과가 미등록된 경우 메세지
    public void EndDateResultCheck(Date dateValue, String ResultValue, String dataName) {
	    if ((dateValue != null) && 
	        	(ResultValue == null || ResultValue.trim().isEmpty())) {
	        	throw new IllegalArgumentException(dataName + " 테스트의 테스트 결과 항목이 입력되지 않았습니다.");
	        }
    }
    
    //테스트 완료일 체크해서 테스트 예정일 Null일 경우 자동 셋팅
    public void setIfNullDate(Date DateValue1, Date DateValue2, Consumer<Date> setDate) {
        if (DateValue1 != null && DateValue2 == null) {
            setDate.accept(DateValue1);
        }
    }
	
	

    
    
}
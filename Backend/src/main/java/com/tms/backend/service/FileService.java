
package com.tms.backend.service;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.formula.functions.T;
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
        
        // boardType에 따라 boardTypeNumber 설정
        Integer boardTypeNumber;
        switch (boardType) {
            case "devProgress":
                boardTypeNumber = 1;
                break;
            case "testProgress":
                boardTypeNumber = 2;
                break;
            case "defect":
                boardTypeNumber = 3;
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
	
	
	

    
    
}
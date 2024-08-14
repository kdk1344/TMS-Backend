package com.tms.backend.controller;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.File;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tms.backend.service.AdminService;
import com.tms.backend.service.UserService;
import com.tms.backend.vo.Criteria;
import com.tms.backend.vo.FileAttachment;
import com.tms.backend.vo.Notice;
import com.tms.backend.vo.PageDTO;
import com.tms.backend.vo.User;

import lombok.extern.log4j.Log4j;

/**
 * Handles requests for the application home page.
 */
@Controller
@Log4j
@RequestMapping("/tms/*")
public class NoticeController {
	
	@Autowired
    private AdminService adminService;
	
	@Autowired
	private UserService userService;


    ////�������� Controller
    
 // ����¡ �� �˻��� ���� �������� ����� JSON���� ��ȯ
    @GetMapping(value = "api/notices", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Object> getNotices(@RequestParam(value = "postDate", required = false) String postDate,
                                          @RequestParam(value = "title", required = false) String title,
                                          @RequestParam(value = "content", required = false) String content,
                                          @RequestParam(value = "page", defaultValue = "1") int page,
                                          @RequestParam(value = "size", defaultValue = "10") int size) {
        List<Notice> notices = adminService.searchNotices(postDate, title, content, page, size);
        int totalNotices = adminService.getTotalNoticesCount(postDate, title, content);
        int totalPages = (int) Math.ceil((double) totalNotices / size);

        Map<String, Object> response = new HashMap<>();
        response.put("notices", notices);
        response.put("currentPage", page);
        response.put("totalPages", totalPages);
        response.put("totalNotices", totalNotices);

        return response;
    }
    
    
    @PostMapping(value = "api/ntwrite", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> createNotice(
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam("postDate") Date postDate,  // ����ڰ� �Է��� �Խ�����
            @RequestParam(value = "file", required = false) MultipartFile[] files) {

    	Notice notice = new Notice();
        notice.setTitle(title);
        notice.setContent(content);
        notice.setPostDate(postDate);
        
        adminService.createNotice(notice);
        handleFileUpload(files, notice);

//        List<FileAttachment> attachments = new ArrayList<>();
//        if (file != null && !file.isEmpty()) {
//            try {
//                // Step 2: ���� ���� ó��
//            	String storageLocation = "C:\\Users\\User\\Pictures\\" + file.getOriginalFilename();
//                File destinationFile = new File(storageLocation);
//                file.transferTo(destinationFile);
//
//                // Step 3: ���� ������ FileAttachment ��ü�� ����
//                FileAttachment attachment = new FileAttachment();
//                attachment.setIdentifier(notice.getSeq()); // �������� SEQ�� identifier�� ����
//                attachment.setType(getFileType(file.getContentType())); // ���� Ÿ�� ����
//                attachment.setStorageLocation(storageLocation);
//                attachment.setFileName(file.getOriginalFilename());
//
//                // FileAttachment ��ü�� attachments ����Ʈ�� �߰�
//                attachments.add(attachment);
//
//                log.info("File attached: " + file.getOriginalFilename());
//            } catch (IOException e) {
//                log.error("File upload failed", e);
//                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
//            }
//        } else {
//            log.info("No file attached");
//        }
//
//        // Step 4: ���� ���� ����
//        adminService.saveAttachments(attachments);

        // Prepare JSON response
        Map<String, Object> response = new HashMap<>();
        response.put("message", "�Խñ��� ���������� ��ϵǾ����ϴ�.");
        response.put("notice", notice);
        
        log.info(notice);

        return ResponseEntity.ok(response);
    }
    
    @PostMapping(value = "/ntupdate", produces = "application/json")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateNotice(
            @RequestParam("seq") Integer seq,
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam("postDate") Date postDate,
            @RequestParam(value = "file", required = false) MultipartFile[] files) {

        Notice notice = adminService.getNoticeById(seq);
        if (notice == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "�ش� ���������� �������� �ʽ��ϴ�.");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        notice.setTitle(title);
        notice.setContent(content);
        notice.setPostDate(postDate);

        adminService.updateNotice(notice);

        if (files != null && files.length > 0) {
            adminService.deleteAttachmentsByNoticeId(seq);
            handleFileUpload(files, notice);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("message", "�Խñ��� ���������� �����Ǿ����ϴ�.");
        response.put("notice", notice);


        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    private void handleFileUpload(MultipartFile[] files, Notice notice) {
        List<FileAttachment> attachments = new ArrayList<>();
        for (MultipartFile file : files) {
	        if (file != null && !file.isEmpty()) {
	            try {
	                String fileType = getFileType(file.getContentType());
	                String storageLocation = getStorageLocation(fileType, file.getOriginalFilename());
	
	                File destinationFile = new File(storageLocation);
	                file.transferTo(destinationFile);
	
	                FileAttachment attachment = new FileAttachment();
	                attachment.setIdentifier(notice.getSeq());
	                attachment.setType(fileType);
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
    
    private String getStorageLocation(String fileType, String fileName) {
        String baseDir = "C:\\Users\\User\\Desktop\\TMS_DEV\\";
        switch (fileType) {
            case "IMAGE":
                return baseDir + "images/" + fileName;
            case "DOCUMENT":
                return baseDir + "documents/" + fileName;
            default:
                return baseDir + "others/" + fileName;
        }
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
    
    
// �׽�Ʈ �뵵
 
    
    @GetMapping("/notice")
    public String getNotices(@RequestParam(value = "postDate", required = false) String postDate,
                             @RequestParam(value = "title", required = false) String title,
                             @RequestParam(value = "content", required = false) String content,
                             @RequestParam(value = "page", defaultValue = "1") int page,
                             @RequestParam(value = "size", defaultValue = "10") int size,
                             Model model) {
        List<Notice> notices = adminService.searchNotices(postDate, title, content, page, size);
        int totalNotices = adminService.getTotalNoticesCount(postDate, title, content);
        int totalPages = (int) Math.ceil((double) totalNotices / size);

        model.addAttribute("notices", notices);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalNotices", totalNotices);

        return "notice"; // JSP ������ ���
    }
    
    @PostMapping(value = "/ntwrite")
    public String createNotice(
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam("postDate") Date postDate,
            @RequestParam(value = "file", required = false) MultipartFile[] files,
            Model model) {

        Notice notice = new Notice();
        notice.setTitle(title);
        notice.setContent(content);
        notice.setPostDate(postDate);

        adminService.createNotice(notice);

        handleFileUpload(files, notice);

        model.addAttribute("message", "�Խñ��� ���������� ��ϵǾ����ϴ�.");
        model.addAttribute("notice", notice);

        return "notice";  // notice.jsp �������� �̵�
    }

    @PostMapping(value = "/ntupdate")
    public String updateNotice(
            @RequestParam("seq") Integer seq,
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam("postDate") Date postDate,
            @RequestParam(value = "file", required = false) MultipartFile[] files,
            Model model) {

        Notice notice = adminService.getNoticeById(seq);
        if (notice == null) {
            model.addAttribute("message", "�ش� ���������� �������� �ʽ��ϴ�.");
            return "error";  // ���� �������� �̵�
        }

        notice.setTitle(title);
        notice.setContent(content);
        notice.setPostDate(postDate);

        adminService.updateNotice(notice);

        if (files != null && files.length > 0) {
            adminService.deleteAttachmentsByNoticeId(seq);
            handleFileUpload(files, notice);
        }

        model.addAttribute("message", "�Խñ��� ���������� �����Ǿ����ϴ�.");
        model.addAttribute("notice", notice);

        return "notice";  // notice.jsp �������� �̵�
    }
    
    // �������� �󼼺��� ���
    @GetMapping("/ntdetail")
    public String getNoticeDetail(@RequestParam("seq") Integer seq, Model model) {
        Notice notice = adminService.getNoticeById(seq);
        if (notice == null) {
            model.addAttribute("message", "�ش� ���������� �������� �ʽ��ϴ�.");
            return "error";  // ���� �������� �̵�
        }
        
        log.info(notice);

        model.addAttribute("notice", notice);
        return "noticeDetail";  // noticeDetail.jsp �������� �̵�
    }
    
    
}

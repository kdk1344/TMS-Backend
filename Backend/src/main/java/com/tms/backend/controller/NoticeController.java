package com.tms.backend.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.net.http.HttpHeaders;
import java.nio.file.Files;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.File;
import java.io.FileInputStream;

import org.apache.log4j.lf5.util.Resource;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tms.backend.service.AdminService;
import com.tms.backend.service.FileService;
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
	
	@Autowired
	private FileService fileservice;


    ////�������� Controller
    
	 // ����¡ �� �˻��� ���� �������� ����� JSON���� ��ȯ
    @GetMapping(value = "api/notices", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Object> getNotices(@RequestParam(value = "startDate", required = false) String startDate,
                                          @RequestParam(value = "endDate", required = false) String endDate,
                                          @RequestParam(value = "title", required = false) String title,
                                          @RequestParam(value = "content", required = false) String content,
                                          @RequestParam(value = "page", defaultValue = "1") int page,
                                          @RequestParam(value = "size", defaultValue = "15") int size) {
    	// ��¥ ���� ���� (yyyy-MM-dd)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // ���� ��¥ ���
        LocalDate today = LocalDate.now();

        // startDate�� endDate �⺻�� ����: ���� ��¥�� 30�� ��
        LocalDate defaultEndDate = today;
        LocalDate defaultStartDate = today.minusDays(30);

        // �Ķ���Ͱ� ���� ��� �⺻�� ���
        if (startDate == null || startDate.isEmpty()) {
            startDate = defaultStartDate.format(formatter);
        }
        if (endDate == null || endDate.isEmpty()) {
            endDate = defaultEndDate.format(formatter);
        }
    	
        // �������� ��ȸ
        List<Notice> notices = adminService.searchNotices(startDate, endDate, title, content, page, size);
        int totalNotices = adminService.getTotalNoticesCount(startDate, endDate, title, content);
        int totalPages = (int) Math.ceil((double) totalNotices / size);

        // ���� ����
        Map<String, Object> response = new HashMap<>();
        response.put("notices", notices);
        response.put("currentPage", page);
        response.put("totalPages", totalPages);
        response.put("totalNotices", totalNotices);

        return response;
    }
    
    @PostMapping(value = "api/{boardType}write", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> createNotice(
            @RequestPart("notice") @Valid Notice notice,
            @RequestPart(value = "file", required = false) MultipartFile[] files,
            @PathVariable("boardType") String boardType,
            HttpServletRequest request) {
    	
//    	// ���ǿ��� authorityCode ��������
//        HttpSession session = request.getSession(false); // ������ ���ٸ� ���� ������ ����
//        if (session == null || session.getAttribute("authorityCode") == null) {
//            // ������ ���ų� authorityCode�� ������ 401 Unauthorized ��ȯ
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("message", "������ �����ϴ�. �α����ϼ���."));
//        }
//
//        Integer authorityCode = (Integer) session.getAttribute("authorityCode");
//
//        // ���� Ȯ��: 1, 2, 3�� ���Ѹ� ���
//        if (authorityCode != 1 && authorityCode != 2 && authorityCode != 3) {
//            // ������ ���� ��� 403 Forbidden ��ȯ
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Collections.singletonMap("message", "�� �۾��� ������ ������ �����ϴ�."));
//        }

        // �α׷� ������ Ȯ��
        log.info("Title: " + notice.getTitle());
        log.info("Content: " + notice.getContent());
        log.info("Post Date: " + notice.getPostDate());
        log.info("seq Date: " + notice.getSeq());

        // Notice ��ü�� �����ͺ��̽��� ����
        adminService.createNotice(notice);
        
        log.info("seq Date: " + notice.getSeq());
        
        // ���ο� ���� ���ε� ó��
        if (files != null && files.length > 0) {
            fileservice.handleFileUpload(files, boardType, notice.getSeq());
        }

        // ���� ����
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "�Խñ��� ���������� ��ϵǾ����ϴ�.");
        response.put("notice", notice);

        return ResponseEntity.ok(response);
    }
    
    @PostMapping(value = "api/{boardType}update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateNotice(
            @RequestPart("notice") @Valid Notice notice,  // JSON �������� �������� �����͸� ����
            @RequestPart(value = "file", required = false) MultipartFile[] files,  // ����
            @PathVariable("boardType") String boardType,
            HttpServletRequest request) {
    	
//    	// ���ǿ��� authorityCode ��������
//        HttpSession session = request.getSession(false); // ������ ���ٸ� ���� ������ ����
//        if (session == null || session.getAttribute("authorityCode") == null) {
//            // ������ ���ų� authorityCode�� ������ 401 Unauthorized ��ȯ
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("message", "������ �����ϴ�. �α����ϼ���."));
//        }
//
//        Integer authorityCode = (Integer) session.getAttribute("authorityCode");
//
//        // ���� Ȯ��: 1, 2, 3�� ���Ѹ� ���
//        if (authorityCode != 1 && authorityCode != 2 && authorityCode != 3) {
//            // ������ ���� ��� 403 Forbidden ��ȯ
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Collections.singletonMap("message", "�� �۾��� ������ ������ �����ϴ�."));
//        }

        Map<String, Object> response = new HashMap<>();
        
        // �������� ���� ���� Ȯ��
        Notice existingNotice = adminService.getNoticeById(notice.getSeq());
        if (existingNotice == null) {
            response.put("message", "�ش� ���������� �������� �ʽ��ϴ�.");
            return ResponseEntity.status(404).body(response);
        }

        // ���� �������� ������Ʈ
        existingNotice.setTitle(notice.getTitle());
        existingNotice.setContent(notice.getContent());
        existingNotice.setPostDate(notice.getPostDate());

        // �������׿� ��ϵ� ���� ÷������ ���� ����
        adminService.deleteAttachmentsByNoticeId(existingNotice.getSeq());

        // ���ο� ���� ���ε� ó��
        fileservice.handleFileUpload(files, boardType, existingNotice.getSeq());
        List<FileAttachment> attachments = adminService.getAttachments(existingNotice.getSeq());
        existingNotice.setAttachments(attachments);
        
        log.info("check "+existingNotice);

        // ������Ʈ�� ���������� ����
        adminService.updateNotice(existingNotice);
        

        // ���� ����
        response.put("status", "success");
        response.put("message", "�Խñ��� ���������� �����Ǿ����ϴ�.");
        response.put("notice", existingNotice);

        return ResponseEntity.ok(response);
    }
    
    @GetMapping(value = "api/ntdetail", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> getNoticeDetail(@RequestParam("seq") Integer seq) {
        Map<String, Object> response = new HashMap<>();
        Notice notice = adminService.getNoticeById(seq);

        if (notice == null) {
            response.put("message", "�ش� ���������� �������� �ʽ��ϴ�.");
            return ResponseEntity.status(404).body(response);
        }

        List<FileAttachment> attachments = adminService.getAttachments(seq);
        notice.setAttachments(attachments);

        response.put("notice", notice);
        response.put("attachments", attachments);

        return ResponseEntity.ok(response);
    }

    @GetMapping("api/downloadAttachment")
    public ResponseEntity<InputStreamResource> downloadAttachment(@RequestParam("seq") Integer seq) throws IOException {
    	// ÷������ ���� ��ȸ
    	FileAttachment attachment = adminService.getAttachmentById(seq);
        if (attachment == null) {
            return ResponseEntity.notFound().build();
        }
        // ���� ��� Ȯ��
        File file = new File(attachment.getStorageLocation());
        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }

        String encodedFileName = fileservice.encodeFileName(attachment.getFileName());
        
     // ������ MIME Ÿ�� Ȯ��
        String contentType = Files.probeContentType(file.toPath());
        if (contentType == null) {
            contentType = "application/octet-stream";  // MIME Ÿ���� ã�� ���� ��� �⺻ ���̳ʸ��� ó��
        }
        log.info(attachment.getStorageLocation());

        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"" + encodedFileName + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(file.length())
                .body(resource);
    }
    
    //JSON �����ۿ� ÷������ �ٿ�ε�
    @GetMapping("/downloadAttachment")
    public ResponseEntity<InputStreamResource> downloadAttachments(@RequestParam("seq") Integer seq) throws IOException {
        FileAttachment attachment = adminService.getAttachmentById(seq);
        if (attachment == null) {
            return ResponseEntity.notFound().build();
        }

        File file = new File(attachment.getStorageLocation());
        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }

        String encodedFileName = fileservice.encodeFileName(attachment.getFileName());

        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"" + encodedFileName + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(file.length())
                .body(resource);
    }
    
    @DeleteMapping(value = "api/ntdelete", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> deleteNotice(@RequestBody List<Integer> seqs,
            HttpServletRequest request) {
    	
//    	// ���ǿ��� authorityCode ��������
//        HttpSession session = request.getSession(false); // ������ ���ٸ� ���� ������ ����
//        if (session == null || session.getAttribute("authorityCode") == null) {
//            // ������ ���ų� authorityCode�� ������ 401 Unauthorized ��ȯ
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("message", "������ �����ϴ�. �α����ϼ���."));
//        }
//
//        Integer authorityCode = (Integer) session.getAttribute("authorityCode");
//
//        // ���� Ȯ��: 1, 2, 3�� ���Ѹ� ���
//        if (authorityCode != 1 && authorityCode != 2 && authorityCode != 3) {
//            // ������ ���� ��� 403 Forbidden ��ȯ
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Collections.singletonMap("message", "�� �۾��� ������ ������ �����ϴ�."));
//        }
        
        Map<String, Object> response = new HashMap<>();

        // �������� ���� ���� Ȯ��
        for (Integer seq : seqs) {
            if (adminService.getNoticeById(seq) == null) {
                response.put("message", "�Խñ� �� �ϳ��� �������� �ʽ��ϴ�. SEQ: " + seq);
                return ResponseEntity.status(404).body(response);
            }
        }

        // �������� ����
        for (Integer seq : seqs) {
            adminService.deleteNotice(seq);
        }
        
        response.put("status", "success");
        response.put("message", "���������� ���������� �����Ǿ����ϴ�.");
        return ResponseEntity.ok(response);
    }
    
    
// ������
 
    
    @GetMapping("/notice")
    public String getNotices(@RequestParam(value = "startDate", required = false) String startDate,
				            @RequestParam(value = "endDate", required = false) String endDate,
				            @RequestParam(value = "title", required = false) String title,
				            @RequestParam(value = "content", required = false) String content,
				            @RequestParam(value = "page", defaultValue = "1") int page,
				            @RequestParam(value = "size", defaultValue = "15") int size,
                             Model model) {
        return "notice"; // JSP ������ ���
    }
 
    
    // �������� �󼼺��� ���
    @GetMapping("/ntdetail")
    public String getNoticeDetail2(@RequestParam("seq") Integer seq, Model model) {
    	// �������� ��ȸ
        Notice notice = adminService.getNoticeById(seq);

        // ���������� �������� �ʴ� ��� ó��
        if (notice == null) {
            model.addAttribute("message", "�ش� ���������� �������� �ʽ��ϴ�.");
            return "error"; // ���� �������� �̵�
        }

        // ÷������ ����Ʈ ��ȸ �� ����
        List<FileAttachment> attachments = adminService.getAttachments(seq);
        log.info(attachments);
        log.info(seq);
        notice.setAttachments(attachments);
        log.info(notice);
        

        // �𵨿� �������� �� ÷������ ����Ʈ �߰�
        model.addAttribute("notice", notice);
        model.addAttribute("attachments", attachments);
    	
        return "noticeDetail";  // noticeDetail.jsp �������� �̵�
    }



    
    
    
}

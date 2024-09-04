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


    ////공지사항 Controller
    
	 // 페이징 및 검색을 통한 공지사항 목록을 JSON으로 반환
    @GetMapping(value = "api/notices", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Object> getNotices(@RequestParam(value = "startDate", required = false) String startDate,
                                          @RequestParam(value = "endDate", required = false) String endDate,
                                          @RequestParam(value = "title", required = false) String title,
                                          @RequestParam(value = "content", required = false) String content,
                                          @RequestParam(value = "page", defaultValue = "1") int page,
                                          @RequestParam(value = "size", defaultValue = "15") int size) {
    	// 날짜 형식 설정 (yyyy-MM-dd)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // 오늘 날짜 계산
        LocalDate today = LocalDate.now();

        // startDate와 endDate 기본값 설정: 오늘 날짜와 30일 전
        LocalDate defaultEndDate = today;
        LocalDate defaultStartDate = today.minusDays(30);

        // 파라미터가 없을 경우 기본값 사용
        if (startDate == null || startDate.isEmpty()) {
            startDate = defaultStartDate.format(formatter);
        }
        if (endDate == null || endDate.isEmpty()) {
            endDate = defaultEndDate.format(formatter);
        }
    	
        // 공지사항 조회
        List<Notice> notices = adminService.searchNotices(startDate, endDate, title, content, page, size);
        int totalNotices = adminService.getTotalNoticesCount(startDate, endDate, title, content);
        int totalPages = (int) Math.ceil((double) totalNotices / size);

        // 응답 생성
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
    	
//    	// 세션에서 authorityCode 가져오기
//        HttpSession session = request.getSession(false); // 세션이 없다면 새로 만들지 않음
//        if (session == null || session.getAttribute("authorityCode") == null) {
//            // 세션이 없거나 authorityCode가 없으면 401 Unauthorized 반환
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("message", "권한이 없습니다. 로그인하세요."));
//        }
//
//        Integer authorityCode = (Integer) session.getAttribute("authorityCode");
//
//        // 권한 확인: 1, 2, 3번 권한만 허용
//        if (authorityCode != 1 && authorityCode != 2 && authorityCode != 3) {
//            // 권한이 없는 경우 403 Forbidden 반환
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Collections.singletonMap("message", "이 작업을 수행할 권한이 없습니다."));
//        }

        // 로그로 데이터 확인
        log.info("Title: " + notice.getTitle());
        log.info("Content: " + notice.getContent());
        log.info("Post Date: " + notice.getPostDate());
        log.info("seq Date: " + notice.getSeq());

        // Notice 객체를 데이터베이스에 저장
        adminService.createNotice(notice);
        
        log.info("seq Date: " + notice.getSeq());
        
        // 새로운 파일 업로드 처리
        if (files != null && files.length > 0) {
            fileservice.handleFileUpload(files, boardType, notice.getSeq());
        }

        // 응답 생성
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "게시글이 성공적으로 등록되었습니다.");
        response.put("notice", notice);

        return ResponseEntity.ok(response);
    }
    
    @PostMapping(value = "api/{boardType}update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateNotice(
            @RequestPart("notice") @Valid Notice notice,  // JSON 형식으로 공지사항 데이터를 받음
            @RequestPart(value = "file", required = false) MultipartFile[] files,  // 파일
            @PathVariable("boardType") String boardType,
            HttpServletRequest request) {
    	
//    	// 세션에서 authorityCode 가져오기
//        HttpSession session = request.getSession(false); // 세션이 없다면 새로 만들지 않음
//        if (session == null || session.getAttribute("authorityCode") == null) {
//            // 세션이 없거나 authorityCode가 없으면 401 Unauthorized 반환
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("message", "권한이 없습니다. 로그인하세요."));
//        }
//
//        Integer authorityCode = (Integer) session.getAttribute("authorityCode");
//
//        // 권한 확인: 1, 2, 3번 권한만 허용
//        if (authorityCode != 1 && authorityCode != 2 && authorityCode != 3) {
//            // 권한이 없는 경우 403 Forbidden 반환
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Collections.singletonMap("message", "이 작업을 수행할 권한이 없습니다."));
//        }

        Map<String, Object> response = new HashMap<>();
        
        // 공지사항 존재 여부 확인
        Notice existingNotice = adminService.getNoticeById(notice.getSeq());
        if (existingNotice == null) {
            response.put("message", "해당 공지사항이 존재하지 않습니다.");
            return ResponseEntity.status(404).body(response);
        }

        // 기존 공지사항 업데이트
        existingNotice.setTitle(notice.getTitle());
        existingNotice.setContent(notice.getContent());
        existingNotice.setPostDate(notice.getPostDate());

        // 공지사항에 등록된 기존 첨부파일 전부 삭제
        adminService.deleteAttachmentsByNoticeId(existingNotice.getSeq());

        // 새로운 파일 업로드 처리
        fileservice.handleFileUpload(files, boardType, existingNotice.getSeq());
        List<FileAttachment> attachments = adminService.getAttachments(existingNotice.getSeq());
        existingNotice.setAttachments(attachments);
        
        log.info("check "+existingNotice);

        // 업데이트된 공지사항을 저장
        adminService.updateNotice(existingNotice);
        

        // 응답 생성
        response.put("status", "success");
        response.put("message", "게시글이 성공적으로 수정되었습니다.");
        response.put("notice", existingNotice);

        return ResponseEntity.ok(response);
    }
    
    @GetMapping(value = "api/ntdetail", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> getNoticeDetail(@RequestParam("seq") Integer seq) {
        Map<String, Object> response = new HashMap<>();
        Notice notice = adminService.getNoticeById(seq);

        if (notice == null) {
            response.put("message", "해당 공지사항이 존재하지 않습니다.");
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
    	// 첨부파일 정보 조회
    	FileAttachment attachment = adminService.getAttachmentById(seq);
        if (attachment == null) {
            return ResponseEntity.notFound().build();
        }
        // 파일 경로 확인
        File file = new File(attachment.getStorageLocation());
        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }

        String encodedFileName = fileservice.encodeFileName(attachment.getFileName());
        
     // 파일의 MIME 타입 확인
        String contentType = Files.probeContentType(file.toPath());
        if (contentType == null) {
            contentType = "application/octet-stream";  // MIME 타입을 찾지 못한 경우 기본 바이너리로 처리
        }
        log.info(attachment.getStorageLocation());

        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"" + encodedFileName + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(file.length())
                .body(resource);
    }
    
    //JSON 비전송용 첨부파일 다운로드
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
    	
//    	// 세션에서 authorityCode 가져오기
//        HttpSession session = request.getSession(false); // 세션이 없다면 새로 만들지 않음
//        if (session == null || session.getAttribute("authorityCode") == null) {
//            // 세션이 없거나 authorityCode가 없으면 401 Unauthorized 반환
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("message", "권한이 없습니다. 로그인하세요."));
//        }
//
//        Integer authorityCode = (Integer) session.getAttribute("authorityCode");
//
//        // 권한 확인: 1, 2, 3번 권한만 허용
//        if (authorityCode != 1 && authorityCode != 2 && authorityCode != 3) {
//            // 권한이 없는 경우 403 Forbidden 반환
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Collections.singletonMap("message", "이 작업을 수행할 권한이 없습니다."));
//        }
        
        Map<String, Object> response = new HashMap<>();

        // 공지사항 존재 여부 확인
        for (Integer seq : seqs) {
            if (adminService.getNoticeById(seq) == null) {
                response.put("message", "게시글 중 하나가 존재하지 않습니다. SEQ: " + seq);
                return ResponseEntity.status(404).body(response);
            }
        }

        // 공지사항 삭제
        for (Integer seq : seqs) {
            adminService.deleteNotice(seq);
        }
        
        response.put("status", "success");
        response.put("message", "공지사항이 성공적으로 삭제되었습니다.");
        return ResponseEntity.ok(response);
    }
    
    
// 페이지
 
    
    @GetMapping("/notice")
    public String getNotices(@RequestParam(value = "startDate", required = false) String startDate,
				            @RequestParam(value = "endDate", required = false) String endDate,
				            @RequestParam(value = "title", required = false) String title,
				            @RequestParam(value = "content", required = false) String content,
				            @RequestParam(value = "page", defaultValue = "1") int page,
				            @RequestParam(value = "size", defaultValue = "15") int size,
                             Model model) {
        return "notice"; // JSP 파일의 경로
    }
 
    
    // 공지사항 상세보기 기능
    @GetMapping("/ntdetail")
    public String getNoticeDetail2(@RequestParam("seq") Integer seq, Model model) {
    	// 공지사항 조회
        Notice notice = adminService.getNoticeById(seq);

        // 공지사항이 존재하지 않는 경우 처리
        if (notice == null) {
            model.addAttribute("message", "해당 공지사항이 존재하지 않습니다.");
            return "error"; // 에러 페이지로 이동
        }

        // 첨부파일 리스트 조회 및 설정
        List<FileAttachment> attachments = adminService.getAttachments(seq);
        log.info(attachments);
        log.info(seq);
        notice.setAttachments(attachments);
        log.info(notice);
        

        // 모델에 공지사항 및 첨부파일 리스트 추가
        model.addAttribute("notice", notice);
        model.addAttribute("attachments", attachments);
    	
        return "noticeDetail";  // noticeDetail.jsp 페이지로 이동
    }



    
    
    
}

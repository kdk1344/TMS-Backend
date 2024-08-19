package com.tms.backend.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.net.http.HttpHeaders;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
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


    ////공지사항 Controller
    
	 // 페이징 및 검색을 통한 공지사항 목록을 JSON으로 반환
    @GetMapping(value = "api/notices", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Object> getNotices(@RequestParam(value = "startDate", required = false) String startDate,
                                          @RequestParam(value = "endDate", required = false) String endDate,
                                          @RequestParam(value = "title", required = false) String title,
                                          @RequestParam(value = "content", required = false) String content,
                                          @RequestParam(value = "page", defaultValue = "1") int page,
                                          @RequestParam(value = "size", defaultValue = "10") int size) {
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
    
    @PostMapping(value = "api/ntwrite", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> createNotice(
            @RequestPart("notice") @Valid Notice notice,
            @RequestPart(value = "file", required = false) MultipartFile[] files) {

        // 로그로 데이터 확인
        log.info("Title: " + notice.getTitle());
        log.info("Content: " + notice.getContent());
        log.info("Post Date: " + notice.getPostDate());

        // Notice 객체를 데이터베이스에 저장
        adminService.createNotice(notice);
        
        // 새로운 파일 업로드 처리
        if (files != null && files.length > 0) {
            handleFileUpload(files, notice, "notice");
        }

        // 응답 생성
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "게시글이 성공적으로 등록되었습니다.");
        response.put("notice", notice);

        return ResponseEntity.ok(response);
    }
    
 // 유효성 검사 실패 시 발생하는 예외 처리
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> handleConstraintViolationException(ConstraintViolationException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Invalid input data");
        response.put("errors", ex.getConstraintViolations());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Invalid input data");

        // 각 필드의 오류 메시지를 담는 Map
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        response.put("errors", errors);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    
    
//    @PostMapping(value = "api/ntwrite", produces = MediaType.APPLICATION_JSON_VALUE)
//    @ResponseBody
//    public ResponseEntity<Map<String, Object>> createNotice(
//            @RequestParam(value = "title" , required = false) String title, // 필수 파라미터
//            @RequestParam(value = "content" , required = false) String content, // 필수 파라미터
//            @RequestParam("postDate") Date postDate,  // 자동 입력 일자
//            @RequestParam(value = "file", required = false) MultipartFile[] files) {
//    	
//    	// 로그로 데이터 확인
//        log.info("Title: " + title);
//        log.info("Content: " + content);
//        log.info("Post Date: " + postDate);
//
//    	Notice notice = new Notice();
//        notice.setTitle(title);
//        notice.setContent(content);
//        notice.setPostDate(postDate);
//        
//        adminService.createNotice(notice);
//        
//        //파일 업로드 처리
//        handleFileUpload(files, notice);
//
//        Map<String, Object> response = new HashMap<>();
//        response.put("message", "게시글이 성공적으로 등록되었습니다.");
//        response.put("notice", notice);
//        
//        log.info(notice);
//
//        return ResponseEntity.ok(response);
//    }
    
    @PostMapping(value = "api/{boardType}/ntupdate", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> updateNotice(
            @RequestParam("seq") Integer seq,
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam("postDate") Date postDate,
            @RequestParam(value = "file", required = false) MultipartFile[] files,
            @RequestParam(value = "deleteFileSeqs", required = false) List<Integer> deleteFileSeqs,
            @PathVariable("boardType") String boardType) {

        Map<String, Object> response = new HashMap<>();

        Notice notice = adminService.getNoticeById(seq);
        if (notice == null) {
            response.put("message", "해당 공지사항이 존재하지 않습니다.");
            return ResponseEntity.status(404).body(response);
        }

        notice.setTitle(title);
        notice.setContent(content);
        notice.setPostDate(postDate);

        adminService.updateNotice(notice);

        // 삭제할 파일이 있는 경우 삭제 처리
        if (deleteFileSeqs != null && !deleteFileSeqs.isEmpty()) {
            for (Integer fileSeq : deleteFileSeqs) {
                adminService.deleteAttachmentsByNoticeId(fileSeq);
            }
        }

        // 새로운 파일 업로드 처리
        if (files != null && files.length > 0) {
            handleFileUpload(files, notice, boardType);
        }

        response.put("message", "게시글이 성공적으로 수정되었습니다.");
        response.put("notice", notice);

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

    @GetMapping("/downloadAttachment")
    public ResponseEntity<InputStreamResource> downloadAttachment(@RequestParam("seq") Integer seq) throws IOException {
        FileAttachment attachment = adminService.getAttachmentById(seq);
        if (attachment == null) {
            return ResponseEntity.notFound().build();
        }

        File file = new File(attachment.getStorageLocation());
        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }

        String encodedFileName = encodeFileName(attachment.getFileName());

        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"" + encodedFileName + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(file.length())
                .body(resource);
    }
    
    @DeleteMapping(value = "api/ntdelete", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> deleteNotice(@RequestBody List<Integer> seqs) {
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
    
    
    
    private void handleFileUpload(MultipartFile[] files, Notice notice, String boardType) {
        List<FileAttachment> attachments = new ArrayList<>();
        for (MultipartFile file : files) {
	        if (file != null && !file.isEmpty()) {
	            try {
	            	String fileType = getFileType(file.getContentType());
	                String storageLocation = getStorageLocation(fileType, file.getOriginalFilename());
	                log.info(storageLocation);
	                
	
	                File destinationFile = new File(storageLocation);
	                file.transferTo(destinationFile);
	
	                FileAttachment attachment = new FileAttachment();
	                attachment.setIdentifier(notice.getSeq());
	                attachment.setType(boardType);
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
    
    private String encodeFileName(String fileName) throws UnsupportedEncodingException {
        return URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
    }
    
    
// 테스트 용도
 
    
    @GetMapping("/notice")
    public String getNotices(@RequestParam(value = "startDate", required = false) String startDate,
				            @RequestParam(value = "endDate", required = false) String endDate,
				            @RequestParam(value = "title", required = false) String title,
				            @RequestParam(value = "content", required = false) String content,
				            @RequestParam(value = "page", defaultValue = "1") int page,
				            @RequestParam(value = "size", defaultValue = "10") int size,
                             Model model) {
//        List<Notice> notices = adminService.searchNotices(postDate, title, content, page, size);
//        int totalNotices = adminService.getTotalNoticesCount(postDate, title, content);
//        int totalPages = (int) Math.ceil((double) totalNotices / size);
//
//        model.addAttribute("notices", notices);
//        model.addAttribute("currentPage", page);
//        model.addAttribute("totalPages", totalPages);
//        model.addAttribute("totalNotices", totalNotices);

        return "notice"; // JSP 파일의 경로
    }
    
//    @PostMapping(value = "/ntwrite")
//    public String createNotice(
//            @RequestParam("title") String title,
//            @RequestParam("content") String content,
//            @RequestParam("postDate") Date postDate,
//            @RequestParam(value = "file", required = false) MultipartFile[] files,
//            Model model) {
//
//        Notice notice = new Notice();
//        notice.setTitle(title);
//        notice.setContent(content);
//        notice.setPostDate(postDate);
//
//        adminService.createNotice(notice);
//
//        handleFileUpload(files, notice);
//
//        model.addAttribute("message", "게시글이 성공적으로 등록되었습니다.");
//        model.addAttribute("notice", notice);
//
//        return "notice";  // notice.jsp 페이지로 이동
//    }

//    @PostMapping(value = "/ntupdate")
//    public String updateNotice(
//            @RequestParam("seq") Integer seq,
//            @RequestParam("title") String title,
//            @RequestParam("content") String content,
//            @RequestParam("postDate") Date postDate,
//            @RequestParam(value = "file", required = false) MultipartFile[] files,
//            @RequestParam(value = "deleteFileSeqs", required = false) List<Integer> deleteFileSeqs,
//            Model model) {
//
//        Notice notice = adminService.getNoticeById(seq);
//        if (notice == null) {
//            model.addAttribute("message", "해당 공지사항이 존재하지 않습니다.");
//            return "error";  // 에러 페이지로 이동
//        }
//
//        notice.setTitle(title);
//        notice.setContent(content);
//        notice.setPostDate(postDate);
//
//        adminService.updateNotice(notice);
//        
//     // 삭제할 파일이 있는 경우 삭제 처리
//        if (deleteFileSeqs != null && !deleteFileSeqs.isEmpty()) {
//            for (Integer fileSeq : deleteFileSeqs) {
//                adminService.deleteAttachmentsByNoticeId(fileSeq);
//            }
//        }
//
//        if (files != null && files.length > 0) {
//            handleFileUpload(files, notice, boardType);
//        }
//
//        model.addAttribute("message", "게시글이 성공적으로 수정되었습니다.");
//        model.addAttribute("notice", notice);
//
//        return "notice";  // notice.jsp 페이지로 이동
//    }
    
    // 공지사항 상세보기 기능
    @GetMapping("/ntdetail")
    public String getNoticeDetail2(@RequestParam("seq") Integer seq, Model model) {
        Notice notice = adminService.getNoticeById(seq);
        if (notice == null) {
            model.addAttribute("message", "해당 공지사항이 존재하지 않습니다.");
            return "error";  // 에러 페이지로 이동
        }
        List<FileAttachment> attachments = adminService.getAttachments(seq);
        log.info(attachments);
        notice.setAttachments(attachments);
        
        log.info(notice);

        model.addAttribute("notice", notice);
        return "noticeDetail";  // noticeDetail.jsp 페이지로 이동
    }
    
    @PostMapping("/ntdelete")
    public String deleteNotice2(@RequestParam("seq") Integer seq, Model model) {
        // 공지사항 존재 여부 확인
        if (adminService.getNoticeById(seq) == null) {
            model.addAttribute("message", "해당 공지사항이 존재하지 않습니다.");
            return "error";  // 에러 페이지로 이동
        }

        // 공지사항 삭제
        adminService.deleteNotice(seq);

        // 성공 메시지와 함께 목록 페이지로 리다이렉트
        model.addAttribute("message", "공지사항이 성공적으로 삭제되었습니다.");
        return "redirect:/tms/notice";  // 공지사항 목록 페이지로 리다이렉트
    }
    



    
    
    
}

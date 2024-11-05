package com.tms.backend.controller;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.tms.backend.service.AdminService;
import com.tms.backend.service.UserService;
import com.tms.backend.vo.FileAttachment;
import com.tms.backend.vo.Notice;
import com.tms.backend.vo.User;

import lombok.extern.log4j.Log4j;

@Controller
@Log4j
@RequestMapping("/*")
public class UserController {
	
	@Autowired
    private UserService userService;
	
	@Autowired
	private AdminService adminService;
	
	// �α��� ������
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }
    
//    // �׽��� ������ �ο���
//    @GetMapping("api/testerNumber")
//    public Map<String, Object> getTesterNumber(){
//    	//�����, ��, ��Ÿ �׽��� �ο� ����
//    	Integer execNumber = adminService.getTesterNumber("�����");
//    	Integer customNumber = adminService.getTesterNumber("��");
//    	Integer etc = adminService.getTesterNumber("��Ÿ");
//    	
//    	// ���� ����
//	    Map<String, Object> response = new HashMap<>();
//	    response.put("execNumber", execNumber);
//	    response.put("customNumber", customNumber);
//	    response.put("etc", etc);
//      return response;
//    }
    
    // ���� ȭ�� ���� ������
    @GetMapping("/dashboard")
    public String getDashboard(@RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "content", required = false) String content,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size //������ �� ��µǴ� �������� ����
    		,Model model,
    		HttpSession session) {
    	List<Notice> noticeList = adminService.searchNotices(startDate, endDate, title, content, page, size); // ȭ�鿡 ��µǴ� �������� ����Ʈ
    	int totalNotices = adminService.getTotalNoticesCount(startDate, endDate, title, content); // �� �������� ����
        int totalPages = (int) Math.ceil((double) totalNotices / size); // �� �������� ������ ����
        
        log.info(totalNotices+" "+totalPages);
        log.info(adminService.searchNotices(null,null,null,null,1,10));
        
        // ���������̼� �׷� ���� (�� �׷쿡 10��������)
        int pageGroupSize = 10;
        int startPage = (page - 1) / pageGroupSize * pageGroupSize + 1;
        int endPage = Math.min(startPage + pageGroupSize - 1, totalPages);
        
        // ����, ���� ������ �׷��� �ִ��� Ȯ��
        boolean hasPrevPageGroup = startPage > 1;
        boolean hasNextPageGroup = endPage < totalPages;

        // ����, ���� �׷��� ���� ������
        int prevPageGroupStart = startPage - pageGroupSize;
        int nextPageGroupStart = startPage + pageGroupSize;
        
        Notice latestNotice = adminService.getLatestNotice(noticeList); // �� ȭ�鿡 ��µǴ� �ֱ� �������� ����
        if (latestNotice == null) {
            latestNotice = new Notice(); // �� Notice ��ü�� ����
            latestNotice.setTitle("�ֱ� ���������� �����ϴ�."); // �⺻ �޽��� ����
            latestNotice.setContent("");
            latestNotice.setSeq(77777);
        }
        List<FileAttachment> attachments = adminService.getAttachments(latestNotice.getSeq(),4); // �ֱ� �������� ÷������
        latestNotice.setAttachments(attachments);

        // �𵨿� ������ �߰�
        model.addAttribute("notices", noticeList);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalNotices", totalNotices);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("latestNotice", latestNotice);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("hasPrevPageGroup", hasPrevPageGroup);
        model.addAttribute("hasNextPageGroup", hasNextPageGroup);
        model.addAttribute("prevPageGroupStart", prevPageGroupStart);
        model.addAttribute("nextPageGroupStart", nextPageGroupStart);
                
        return  "dashboard";
    }
    
    @GetMapping(value="/getNotice" , produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getNotice(@RequestParam int seq) {
    	Map<String, Object> response = new HashMap<>();
    	Notice notice = adminService.getNoticeById(seq); // seq�� ���������� ã�� ���� �޼ҵ�
    	
    	// ���������� ������ ���
        response.put("seq", notice.getSeq());
        response.put("postDate", notice.getPostDate());
        response.put("title", notice.getTitle());
        response.put("content", notice.getContent());
        
        // ÷������ ����Ʈ �߰�
        List<FileAttachment> attachments = adminService.getAttachments(seq,4);
        response.put("attachments", attachments);

        return ResponseEntity.ok(response); // 200 OK ����
    }
    
    // ����ڰ� ���� ��й�ȣ ����
    @PostMapping(value = "api/pwChange", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> APIpwChange(HttpServletRequest request,
    		@RequestBody User user
    		) {
        Map<String, Object> response = new HashMap<>();

        HttpSession session = request.getSession(false); // false�� ������ ������ ���� ������ ������ �ǹ�
        String userID = (String) session.getAttribute("id"); // �α����� ID ��������
        User users = userService.getUserByUserID(userID); // ID�� ���� ������ ���� ����� ���� ��������
        log.info(users);
        users.setPassword(user.getPassword()); // ������ ��й�ȣ �Է�
        users.setPwChangeCnt(user.getPwChangeCnt()+1);
        log.info(users);
        boolean success = adminService.updateUser(users, request);
        if (success) {
            response.put("status", "success");
            response.put("message", "��й�ȣ ������ �Ϸ�Ǿ����ϴ�.");
            response.put("user", user); // ������Ʈ�� ����� ���� ��ȯ
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            response.put("status", "failure");
            response.put("message", "��й�ȣ ���� �߿� �����߽��ϴ�");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }
    
    
    
    // �α׾ƿ� ���
    @GetMapping(value = "api/logout", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> APIlogout(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();

        // ���� ��ȿȭ
        HttpSession session = request.getSession(false); // false�� ������ ������ ���� ������ ������ �ǹ�
        if (session != null) {
            session.invalidate(); // ���� ��ȿȭ
            response.put("status", "success");
            response.put("message", "�α׾ƿ� ����");
        } else {
            response.put("status", "error");
            response.put("message", "������ �������� �ʽ��ϴ�.");
        }

        return ResponseEntity.ok(response); // JSON ������ ���� ��ȯ
    }
    
    //������, �������̵� ���� ���� üũ
    @GetMapping(value = "api/checkSession", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> checkSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false); // ������ ���ٸ� ���� ������ ����
        Map<String, Object> response = new HashMap<>();
        
        if (session == null || session.getAttribute("id") == null) {
            response.put("status", "error");
            response.put("message", "������ �����ϴ�.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        // ���ǿ��� ID�� �̸�, ���� �ڵ� ��������
        String userID = (String) session.getAttribute("id");
        String userName = (String) session.getAttribute("name");
        Integer authorityCode = (Integer) session.getAttribute("authorityCode");
        // ���ǿ��� pwChange ��������
        Integer pwChange = (Integer) session.getAttribute("pwChange");

        // Ŭ���̾�Ʈ�� ����� ���� ����
        response.put("status", "success");
        response.put("userID", userID);
        response.put("userName", userName);
        response.put("authorityCode", authorityCode);
        
        return ResponseEntity.ok(response);
    }
    
    // �α��� ���
    @PostMapping(value = "api/login", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> loginRequest,
                                                     HttpServletRequest req,
                                                     HttpServletResponse res) throws Exception {
        
    	Map<String, Object> response = new HashMap<>();
        HttpSession session = req.getSession();
        
        // �α��� ���� �޾ƿ���
        String userID = loginRequest.get("userID");
        String password = loginRequest.get("password");
        
        //�α��� Ȯ��
        User check = userService.authenticateUser(userID, password);
     
        if (check != null) {            
            // ����� ID�� �̸�, ���� �ڵ带 ���ǿ� ����
            session.setAttribute("id", loginRequest.get("userID"));
	        session.setAttribute("name", check.getUserName());
            session.setAttribute("authorityCode", check.getAuthorityCode());
            
            // ���� ID�� ��Ű�� ����
            Cookie sessionCookie = new Cookie("TMSESSIONID", session.getId());
            sessionCookie.setHttpOnly(true);  // XSS ���� ����
            sessionCookie.setSecure(true);    // HTTPS������ ���
            sessionCookie.setPath("/");       // ���ø����̼� ��ü���� ��� ����
            sessionCookie.setMaxAge(60 * 30); // ��Ű ��ȿ �Ⱓ: 30��
            res.addCookie(sessionCookie);
            
            // ���� ���� ����
            response.put("status", "success");
            response.put("message", "�α��� ����");
            response.put("userID", loginRequest.get("userID"));
            response.put("pwChange", check.getPwChangeCnt());
            response.put("userName", check.getUserName());
            response.put("authorityCode", check.getAuthorityCode());
            
            return ResponseEntity.ok(response); // 200 OK
        } else {            
            // ���� ���� ����
            response.put("status", "error");
            response.put("message", "�߸��� ���̵� ��й�ȣ�� �Է��ϼ̽��ϴ�.");
            
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response); // 401 Unauthorized
        }
    }
    

}
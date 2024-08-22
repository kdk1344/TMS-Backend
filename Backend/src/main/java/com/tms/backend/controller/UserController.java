package com.tms.backend.controller;
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

/**
 * Handles requests for the application home page.
 */
@Controller
@Log4j
@RequestMapping("/tms/*")
public class UserController {
	
	@Autowired
    private UserService userService;
	
	@Autowired
	private AdminService adminService;
	// 12345

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }
    
    @GetMapping("/dashboard")
    public String getDashboard(@RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "content", required = false) String content,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "5") int size
    		,Model model,
    		HttpSession session) {
    	List<Notice> noticeList = adminService.searchNotices(startDate, endDate, title, content, page, size);
    	int totalNotices = adminService.getTotalNoticesCount(startDate, endDate, title, content);
        int totalPages = (int) Math.ceil((double) totalNotices / size);
        Notice latestNotice = adminService.getLatestNotice();
        
        List<FileAttachment> attachments = adminService.getAttachments(latestNotice.getSeq());
        latestNotice.setAttachments(attachments);

        model.addAttribute("notices", noticeList);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalNotices", totalNotices);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("latestNotice", latestNotice);
        
        log.info("Session ID after login: " + session.getId());
        
        return  "dashboard";
    }
    
    @GetMapping("/pdpc")
    public String PdpcPage() {
        return "pdpc";
    }
    
    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        // ���� ��ȿȭ
        HttpSession session = request.getSession(false); // false�� ������ ������ ���� ������ ������ �ǹ�
        if (session != null) {
            session.invalidate(); // ���� ��ȿȭ
        }

        // �α׾ƿ� �� �α��� �������� �����̷�Ʈ
        return "redirect:/tms/login";
    }
    
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

        // ���ǿ��� ID�� ���� �ڵ� ��������
        String userID = (String) session.getAttribute("id");
        Integer authorityCode = (Integer) session.getAttribute("authorityCode");

        // Ŭ���̾�Ʈ�� ����� ���� ����
        response.put("status", "success");
        response.put("userID", userID);
        response.put("authorityCode", authorityCode);
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
	public String login(@RequestParam(value="userID", required=false) String userID, HttpServletRequest req, @RequestParam("password") String password) throws Exception{
		HttpSession session = req.getSession();
		log.info(userID + " "+password);
		User check = userService.authenticateUser(userID, password);
		if(check != null) {
				log.info("�α��� ����");
		        // ����� ID ���ǿ� ����
		        session.setAttribute("id", check.getuserID());
		        // ����� ���� �ڵ� ���ǿ� ����
		        session.setAttribute("authorityCode", check.getauthorityCode());
		        // ��ú���� �����̷�Ʈ
				return "redirect:/tms/dashboard";}
		else {
				session.setAttribute("error", "Invalid userID or password.");
				log.info("failcheck2!!");
				return "redirect:/tms/login";
			}
		}
    
    @PostMapping(value = "api/login", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> loginRequest,
                                                     HttpServletRequest req,
                                                     HttpServletResponse res) throws Exception {
        
    	Map<String, Object> response = new HashMap<>();
        HttpSession session = req.getSession();
        
        String userID = loginRequest.get("userID");
        String password = loginRequest.get("password");

        log.info("�α��� �õ�: " + userID + " " + password);

        User check = userService.authenticateUser(userID, password);
        
        if (check != null) {
            log.info("�α��� ����");
            
            // ����� ID�� ���� �ڵ带 ���ǿ� ����
            session.setAttribute("id", loginRequest.get("userID"));
            session.setAttribute("authorityCode", check.getauthorityCode());
            
            log.info("Logged in user ID: " + loginRequest.get("userID"));
            
            log.info("Logged in user ID: " + check.getauthorityCode());
            
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
            response.put("authorityCode", check.getauthorityCode());
            
            return ResponseEntity.ok(response); // 200 OK
        } else {
            log.info("�α��� ����");
            
            // ���� ���� ����
            response.put("status", "error");
            response.put("message", "Invalid userID or password.");
            
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response); // 401 Unauthorized
        }
    }
    

}
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
        // 세션 무효화
        HttpSession session = request.getSession(false); // false는 세션이 없으면 새로 만들지 않음을 의미
        if (session != null) {
            session.invalidate(); // 세션 무효화
        }

        // 로그아웃 후 로그인 페이지로 리다이렉트
        return "redirect:/tms/login";
    }
    
    @GetMapping(value = "api/logout", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> APIlogout(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();

        // 세션 무효화
        HttpSession session = request.getSession(false); // false는 세션이 없으면 새로 만들지 않음을 의미
        if (session != null) {
            session.invalidate(); // 세션 무효화
            response.put("status", "success");
            response.put("message", "로그아웃 성공");
        } else {
            response.put("status", "error");
            response.put("message", "세션이 존재하지 않습니다.");
        }

        return ResponseEntity.ok(response); // JSON 형식의 응답 반환
    }
    
    @GetMapping(value = "api/checkSession", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> checkSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false); // 세션이 없다면 새로 만들지 않음
        Map<String, Object> response = new HashMap<>();
        
        if (session == null || session.getAttribute("id") == null) {
            response.put("status", "error");
            response.put("message", "권한이 없습니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        // 세션에서 ID와 권한 코드 가져오기
        String userID = (String) session.getAttribute("id");
        Integer authorityCode = (Integer) session.getAttribute("authorityCode");

        // 클라이언트에 사용자 정보 전송
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
				log.info("로그인 성공");
		        // 사용자 ID 세션에 저장
		        session.setAttribute("id", check.getuserID());
		        // 사용자 권한 코드 세션에 저장
		        session.setAttribute("authorityCode", check.getauthorityCode());
		        // 대시보드로 리다이렉트
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

        log.info("로그인 시도: " + userID + " " + password);

        User check = userService.authenticateUser(userID, password);
        
        if (check != null) {
            log.info("로그인 성공");
            
            // 사용자 ID와 권한 코드를 세션에 저장
            session.setAttribute("id", loginRequest.get("userID"));
            session.setAttribute("authorityCode", check.getauthorityCode());
            
            log.info("Logged in user ID: " + loginRequest.get("userID"));
            
            log.info("Logged in user ID: " + check.getauthorityCode());
            
            // 세션 ID를 쿠키로 저장
            Cookie sessionCookie = new Cookie("TMSESSIONID", session.getId());
            sessionCookie.setHttpOnly(true);  // XSS 공격 방지
            sessionCookie.setSecure(true);    // HTTPS에서만 사용
            sessionCookie.setPath("/");       // 애플리케이션 전체에서 사용 가능
            sessionCookie.setMaxAge(60 * 30); // 쿠키 유효 기간: 30분
            res.addCookie(sessionCookie);
            
            // 성공 응답 생성
            response.put("status", "success");
            response.put("message", "로그인 성공");
            response.put("userID", loginRequest.get("userID"));
            response.put("authorityCode", check.getauthorityCode());
            
            return ResponseEntity.ok(response); // 200 OK
        } else {
            log.info("로그인 실패");
            
            // 실패 응답 생성
            response.put("status", "error");
            response.put("message", "Invalid userID or password.");
            
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response); // 401 Unauthorized
        }
    }
    

}
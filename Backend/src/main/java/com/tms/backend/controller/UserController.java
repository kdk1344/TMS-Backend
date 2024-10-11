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
	
	// 로그인 페이지
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }
    
//    // 테스터 참여자 인원수
//    @GetMapping("api/testerNumber")
//    public Map<String, Object> getTesterNumber(){
//    	//수행사, 고객, 기타 테스터 인원 산출
//    	Integer execNumber = adminService.getTesterNumber("수행사");
//    	Integer customNumber = adminService.getTesterNumber("고객");
//    	Integer etc = adminService.getTesterNumber("기타");
//    	
//    	// 응답 생성
//	    Map<String, Object> response = new HashMap<>();
//	    response.put("execNumber", execNumber);
//	    response.put("customNumber", customNumber);
//	    response.put("etc", etc);
//      return response;
//    }
    
    // 메인 화면 진입 페이지
    @GetMapping("/dashboard")
    public String getDashboard(@RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "content", required = false) String content,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size //페이지 당 출력되는 공지사항 숫자
    		,Model model,
    		HttpSession session) {
    	List<Notice> noticeList = adminService.searchNotices(startDate, endDate, title, content, page, size); // 화면에 출력되는 공지사항 리스트
    	int totalNotices = adminService.getTotalNoticesCount(startDate, endDate, title, content); // 총 공지사항 숫자
        int totalPages = (int) Math.ceil((double) totalNotices / size); // 총 공지사항 페이지 숫자
        
        // 페이지네이션 그룹 설정 (한 그룹에 10페이지씩)
        int pageGroupSize = 10;
        int startPage = (page - 1) / pageGroupSize * pageGroupSize + 1;
        int endPage = Math.min(startPage + pageGroupSize - 1, totalPages);
        
        // 이전, 다음 페이지 그룹이 있는지 확인
        boolean hasPrevPageGroup = startPage > 1;
        boolean hasNextPageGroup = endPage < totalPages;

        // 이전, 다음 그룹의 시작 페이지
        int prevPageGroupStart = startPage - pageGroupSize;
        int nextPageGroupStart = startPage + pageGroupSize;
        
        Notice latestNotice = adminService.getLatestNotice(); // 윗 화면에 출력되는 최근 공지사항 내용
        List<FileAttachment> attachments = adminService.getAttachments(latestNotice.getSeq(),4); // 최근 공지사항 첨부파일
        latestNotice.setAttachments(attachments);

        // 모델에 데이터 추가
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
    
//    // 페이징 및 검색을 통한 공지사항 목록을 JSON으로 반환
//    @GetMapping(value = "api/dashboard", produces = MediaType.APPLICATION_JSON_VALUE)
//    @ResponseBody
//    public Map<String, Object> getNotices(@RequestParam(value = "startDate", required = false) String startDate,
//                                          @RequestParam(value = "endDate", required = false) String endDate,
//                                          @RequestParam(value = "title", required = false) String title,
//                                          @RequestParam(value = "content", required = false) String content,
//                                          @RequestParam(value = "page", defaultValue = "1") int page,
//                                          @RequestParam(value = "size", defaultValue = "10") int size) {
//    	
//        // 공지사항 조회
//        List<Notice> notices = adminService.searchNotices(startDate, endDate, title, content, page, size);
//        int totalNotices = adminService.getTotalNoticesCount(startDate, endDate, title, content);
//        int totalPages = (int) Math.ceil((double) totalNotices / size);
//        
//        //최근 공지글
//        Notice latestNotice = adminService.getLatestNotice();
//        List<FileAttachment> attachments = adminService.getAttachments(latestNotice.getSeq(),4);
//        latestNotice.setAttachments(attachments);
//
//        // 응답 생성
//        Map<String, Object> response = new HashMap<>();
//        response.put("notices", notices);
//        response.put("currentPage", page);
//        response.put("totalPages", totalPages);
//        response.put("totalNotices", totalNotices);
//        response.put("latestNotice", latestNotice);
//
//        return response;
//    }
    
    // 로그아웃 기능
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
    
    //유저명, 유저아이디를 담을 세션 체크
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

        // 세션에서 ID와 이름, 권한 코드 가져오기
        String userID = (String) session.getAttribute("id");
        String userName = (String) session.getAttribute("name");
        Integer authorityCode = (Integer) session.getAttribute("authorityCode");

        // 클라이언트에 사용자 정보 전송
        response.put("status", "success");
        response.put("userID", userID);
        response.put("userName", userName);
        response.put("authorityCode", authorityCode);
        
        return ResponseEntity.ok(response);
    }
    
    // 로그인 기능
    @PostMapping(value = "api/login", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> loginRequest,
                                                     HttpServletRequest req,
                                                     HttpServletResponse res) throws Exception {
        
    	Map<String, Object> response = new HashMap<>();
        HttpSession session = req.getSession();
        
        // 로그인 정보 받아오기
        String userID = loginRequest.get("userID");
        String password = loginRequest.get("password");
        
        //로그인 확인
        User check = userService.authenticateUser(userID, password);
        
        if (check != null) {            
            // 사용자 ID와 이름, 권한 코드를 세션에 저장
            session.setAttribute("id", loginRequest.get("userID"));
	        session.setAttribute("name", check.getUserName());
            session.setAttribute("authorityCode", check.getAuthorityCode());
            
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
            response.put("userName", check.getUserName());
            response.put("authorityCode", check.getAuthorityCode());
            
            return ResponseEntity.ok(response); // 200 OK
        } else {            
            // 실패 응답 생성
            response.put("status", "error");
            response.put("message", "Invalid userID or password.");
            
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response); // 401 Unauthorized
        }
    }
    

}
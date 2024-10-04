package com.tms.backend.controller;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tms.backend.exception.ForbiddenException;
import com.tms.backend.exception.UnauthorizedException;
import com.tms.backend.service.AdminService;
import com.tms.backend.service.FileService;
import com.tms.backend.service.UserService;
import com.tms.backend.vo.Criteria;
import com.tms.backend.vo.FileAttachment;
import com.tms.backend.vo.Notice;
import com.tms.backend.vo.PageDTO;
import com.tms.backend.vo.User;
import com.tms.backend.vo.categoryCode;

import lombok.extern.log4j.Log4j;

@Controller
@Log4j
@RequestMapping("/tms/*")
public class AdminController {
	
	@Autowired
    private AdminService adminService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private FileService fileService;
    
	//사용자 관리자 화면
    @GetMapping("/adminUser")
    public String UserPage(Criteria criteria, Model model) {
        return "adminUser";
    }
    
    //사용자 등록
    @PostMapping(value= "api/join", produces = "application/json")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> join(@RequestBody User user,
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
        // join 메서드에서 성공 시 예외를 발생시키지 않음
        try {
            adminService.join(user);  // 사용자 등록
            response.put("status", "success");
            response.put("message", "User successfully registered!");
            response.put("user", user); // 가입된 사용자 정보 반환
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            response.put("status", "failure");
            response.put("message", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("status", "error");
            response.put("message", "Error occurred while registering user.");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    //사용자 수정
    @PostMapping(value= "api/idmodify" , produces = "application/json")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> idModify(@RequestBody User user,
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
        try {
        	//사용자 수정
            boolean success = adminService.updateUser(user, request);
            if (success) {
                response.put("status", "success");
                response.put("message", "사용자 수정에 성공했습니다.");
                response.put("user", user); // 업데이트된 사용자 정보 반환
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                response.put("status", "failure");
                response.put("message", "사용자 수정 중에 실패했습니다");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.put("status", "error");
            response.put("message", "사용자 수정 중에 에러가 발생했습니다");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    //사용자 삭제
    @DeleteMapping(value= "api/deleteuser", produces = "application/json")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteUsers(@RequestBody List<String> IDList,
            HttpServletRequest request) {
    	
    	// 세션에서 authorityCode 가져오기
        HttpSession session = request.getSession(false); // 세션이 없다면 새로 만들지 않음
        if (session == null || session.getAttribute("authorityCode") == null) {
            // 세션이 없거나 authorityCode가 없으면 401 Unauthorized 반환
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("message", "권한이 없습니다. 로그인하세요."));
        }

        Integer authorityCode = (Integer) session.getAttribute("authorityCode");

        // 권한 확인: 1, 2, 3번 권한만 허용
        if (authorityCode != 1 && authorityCode != 2 && authorityCode != 3) {
            // 권한이 없는 경우 403 Forbidden 반환
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Collections.singletonMap("message", "이 작업을 수행할 권한이 없습니다."));
        }
        
    	Map<String, Object> response = new HashMap<>();
        try {
        	//사용자 삭제
            boolean success = adminService.deleteUser(IDList.toArray(new String[0]));

            if (success) {
                response.put("status", "success");
                response.put("message", "Users deleted successfully!");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                response.put("status", "failure");
                response.put("message", "Error deleting users.");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.put("status", "error");
            response.put("message", "Error occurred while deleting users.");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    //사용자 정보 가져오기
    @GetMapping(value = "api/users", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Object> getUserList(
        @RequestParam(value = "page", defaultValue = "1") int page,
        @RequestParam(value = "userName", required = false) String userName,
        @RequestParam(value = "authorityName", required = false) String authorityName) {
    	
    	//사용자 목록 정보를 가져오기 위한 파라미터 입력
        Criteria criteria = new Criteria();
        criteria.setPage(page);
        criteria.setPerPageNum(15);
        criteria.setuserName(userName);
        criteria.setauthorityName(authorityName);
        
        //사용자 목록 가져오기
        List<User> userList = adminService.getList(criteria);
        log.info(userList);
        int total = adminService.getTotal(criteria);
        int totalPages = (int) Math.ceil((double) total / criteria.getPerPageNum());

        Map<String, Object> response = new HashMap<String, Object>();
        response.put("userList", userList);
        response.put("currentPage", page);
        response.put("totalPages", totalPages);
        response.put("totalUsers", total);

        return response;
    }
    
    // 전체 사용자 정보를 엑셀로 다운로드
    @GetMapping("/downloadAll")
    public String downloadAllUsers(HttpServletResponse response,HttpServletRequest request) throws IOException {

    	// 세션에서 authorityCode 가져오기
        HttpSession session = request.getSession(false); // 세션이 없다면 새로 만들지 않음
        if (session == null || session.getAttribute("authorityCode") == null) {
            // 세션이 없거나 authorityCode가 없으면 예외 발생
        	return "redirect:/tms/login"; // 로그인 페이지로 리다이렉트
        }

        Integer authorityCode = (Integer) session.getAttribute("authorityCode");

        // 권한 확인: 1, 2, 3번 권한만 허용
        if (authorityCode != 1 && authorityCode != 2 && authorityCode != 3) {
        	log.info(authorityCode);
            // 권한이 없는 경우 예외 발생
        	return "redirect:/tms/adminUser"; // 관리자 페이지로 이동
        }
        //모든 유저 정보 가져오기
        List<User> userList = userService.getAllUser();
        exportToExcel(response, userList, "all_users.xlsx");
        return null;
    }
    
    // 액셀 파일 예시를 다운로드
    @GetMapping("/userexampleexcel")
    public void downloadExuser(HttpServletResponse response) throws IOException {
    	//사용자 정보 액셀 예시 파일
    	List<User> ExampleDEV = adminService.findAuthorityCode(99999);  	
    	exportToExcel(response, ExampleDEV, "example.xlsx");
    }


    // 조회된 사용자 정보를 엑셀로 다운로드
    @GetMapping("/downloadFiltered")
    public String downloadFilteredUsers(
            @RequestParam(value = "userName", required = false) String userName,
            @RequestParam(value = "authorityName", required = false) String authorityName,
            HttpServletResponse response,
            HttpServletRequest request) throws IOException {

    	// 세션에서 authorityCode 가져오기
        HttpSession session = request.getSession(false); // 세션이 없다면 새로 만들지 않음
        if (session == null || session.getAttribute("authorityCode") == null) {
            // 세션이 없거나 authorityCode가 없으면 예외 발생
        	return "redirect:/tms/login"; // 로그인 페이지로 리다이렉트
        }

        Integer authorityCode = (Integer) session.getAttribute("authorityCode");

        // 권한 확인: 1, 2, 3번 권한만 허용
        if (authorityCode != 1 && authorityCode != 2 && authorityCode != 3) {
            // 권한이 없는 경우 예외 발생
        	return "redirect:/tms/adminUser"; // 관리자 페이지로 이동
        }
        
        // 사용자 정보를 위한 파라미터 입력
        Criteria criteria = new Criteria();
        criteria.setuserName(userName);
        criteria.setauthorityName(authorityName);
        
        //필터링된 사용자 정보 가져오기
        List<User> filteredUserList = userService.getFilteredUsers(criteria);
        exportToExcel(response, filteredUserList, "filtered_users.xlsx");
        return null;
    }

    // 엑셀 파일로 데이터를 내보내는 메서드
    private void exportToExcel(HttpServletResponse response, List<User> userList, String fileName) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Users");
        String check = "";
        if (userList.isEmpty()) {
        	check = "no_value";
        }
        
        //액셀 Row 만들기
        Row headerRow = sheet.createRow(0);
        //컬럼 만들기
        headerRow.createCell(0).setCellValue("UserID");
        headerRow.createCell(1).setCellValue("userName");
        headerRow.createCell(2).setCellValue("Password");
        //사용자 예시 파일 생성을 위한 조건
        if (check != "") {
        	headerRow.createCell(3).setCellValue("AuthorityName");
        }
        //그외 액셀 파일 생성을 위한 조건
        else {
	        headerRow.createCell(3).setCellValue("AuthorityCode");
	        headerRow.createCell(4).setCellValue("AuthorityName");
        }
        
        int rowNum = 1;
        //사용자 정보 차례대로 입력
        for (User user : userList) {
            Row row = sheet.createRow(rowNum++);
            //사용자 정보 액셀에 입력
            row.createCell(0).setCellValue(user.getuserID());
            row.createCell(1).setCellValue(user.getuserName());
            row.createCell(2).setCellValue(user.getPassword());
            if (check != "") {
            	row.createCell(3).setCellValue(user.getauthorityName());
            }
            else {
	            row.createCell(3).setCellValue(user.getauthorityCode());
	            row.createCell(4).setCellValue(user.getauthorityName());
            }
        }

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
        try (OutputStream os = response.getOutputStream()) {
            workbook.write(os);
        }
        workbook.close();
    }
    
    //액셀 업로드 기능
    @PostMapping(value = "api/userupload", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> uploadExcelFile(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes,
            HttpServletRequest request) {
    	
    	// 세션에서 authorityCode 가져오기
        HttpSession session = request.getSession(false); // 세션이 없다면 새로 만들지 않음
        if (session == null || session.getAttribute("authorityCode") == null) {
            // 세션이 없거나 authorityCode가 없으면 401 Unauthorized 반환
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("message", "권한이 없습니다. 로그인하세요."));
        }

        Integer authorityCode = (Integer) session.getAttribute("authorityCode");

        // 권한 확인: 1, 2, 3번 권한만 허용
        if (authorityCode != 1 && authorityCode != 2 && authorityCode != 3) {
            // 권한이 없는 경우 403 Forbidden 반환
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Collections.singletonMap("message", "이 작업을 수행할 권한이 없습니다."));
        }
        
    	Map<String, Object> response = new HashMap<>();
        if (file.isEmpty()) {
        	response.put("status", "failure");
            response.put("message", "파일이 없습니다. 다시 시도해 주세요.");
        	return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        try (InputStream inputStream = file.getInputStream()) {
            Workbook workbook = WorkbookFactory.create(inputStream);
            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);
            
            // 예상하는 컬럼명 리스트
            List<String> expectedHeaders = Arrays.asList("userID", "userName", "Password", "AuthorityName");
            if (!fileService.isHeaderValid(headerRow, expectedHeaders)) { // 컬럼 비교
                response.put("status", "error");
                response.put("message", "헤더의 컬럼명이 올바르지 않습니다.");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            
            List<User> users = new ArrayList<User>();

            // 첫 번째 행은 헤더이므로 건너뜁니다.
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    User user = new User();
                    try {
                    	//업로드한 사용자 정보 입력
	                    user.setuserID(row.getCell(0).getStringCellValue());
	                    user.setuserName(row.getCell(1).getStringCellValue());
	                    if (row.getCell(2).getCellType() == CellType.NUMERIC ) {
	                    	user.setPassword(String.valueOf((int) row.getCell(2).getNumericCellValue()));
	                    }
	                    else{
	                    	user.setPassword(row.getCell(2).getStringCellValue());
	                    }
                        user.setauthorityName(row.getCell(3).getStringCellValue());
	                    adminService.join(user);
                    } catch (Exception e) {
                        // 형식 오류 또는 예상치 못한 컬럼 데이터 처리
                        e.printStackTrace();
                        response.put("status", "error");
                        response.put("message", "파일의 데이터 형식이 올바르지 않습니다: 행 " + (i + 1));
                        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                    }
                }
            }

            // 데이터베이스에 저장
            adminService.saveAll(users);
            response.put("status", "success");
            response.put("message", "파일 업로드가 성공적으로 완료되었습니다!");
            response.put("totalUploaded", users.size());

        } catch (DuplicateKeyException e) {
            // 중복된 키 오류 처리
            e.printStackTrace();
            response.put("status", "error");
            response.put("message", "중복된 사용자 ID가 발견되었습니다.");
            return new ResponseEntity<>(response, HttpStatus.CONFLICT); // 409 Conflict
        } catch (IOException e) {
            e.printStackTrace();
            response.put("status", "error");
            response.put("message", "파일 처리 중 오류가 발생했습니다. 다시 시도해 주세요.");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    

    
    



}
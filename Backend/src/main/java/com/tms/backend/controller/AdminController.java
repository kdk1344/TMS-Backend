package com.tms.backend.controller;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.tms.backend.service.AdminService;
import com.tms.backend.service.UserService;
import com.tms.backend.vo.Criteria;
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
public class AdminController {
	
	@Autowired
    private AdminService adminService;
	
	@Autowired
	private UserService userService;
    
    @GetMapping("/adminuser")
    public String UserPage(Criteria criteria, Model model) {
    	log.info(adminService.getList(criteria));
        model.addAttribute("userList", adminService.getList(criteria));
        model.addAttribute("pageDTO", new PageDTO(adminService.getTotal(criteria), criteria));
        return "adminuser";
    }
    
    
    @PostMapping("api/join")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> join(@RequestBody User user) {
        Map<String, Object> response = new HashMap<>();
        try {
            adminService.join(user);  // join 메서드에서 성공 시 예외를 발생시키지 않음
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
    
    @PostMapping("idmodify")
    public String IdModify(@ModelAttribute("user") User user, RedirectAttributes redirectAttributes) {
        boolean success = adminService.updateUser(user);
        if (success) {
            redirectAttributes.addFlashAttribute("message", "User updated successfully!");
        } else {
            redirectAttributes.addFlashAttribute("message", "Error updating user.");
        }
        return "redirect:/tms/adminuser";
    }
    
    @DeleteMapping("api/deleteuser")
    @ResponseBody
    public Map<String, String> deleteUsers(@RequestBody List<String> IDList) {
        boolean success = adminService.deleteUser(IDList.toArray(new String[0]));

        if (success) {
            return Map.of("message", "Users deleted successfully!");
        } else {
            return Map.of("message", "Error deleting users.");
        }
    }
    
    @GetMapping("test")
    public String TestPage(@RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "userName", required = false) String userName,
            @RequestParam(value = "authorityName", required = false) String authorityName,
            Model model) {

        Criteria criteria = new Criteria();
        criteria.setPage(page);
        criteria.setuserName(userName);
        criteria.setauthorityName(authorityName);

        List<User> userList = adminService.getList(criteria);
        int total = adminService.getTotal(criteria);
        log.info(userList);

        model.addAttribute("userList", userList);
        model.addAttribute("pageDTO", new PageDTO(total, criteria));
    	return "test";
    }
    
    @GetMapping(value = "api/users", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Object> getUserList(
        @RequestParam(value = "page", defaultValue = "1") int page,
        @RequestParam(value = "userName", required = false) String userName,
        @RequestParam(value = "authorityName", required = false) String authorityName) {

        Criteria criteria = new Criteria();
        criteria.setPage(page);
        criteria.setPerPageNum(10);
        criteria.setuserName(userName);
        criteria.setauthorityName(authorityName);

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
    public void downloadAllUsers(HttpServletResponse response) throws IOException {
        List<User> userList = userService.getAllUser();
        log.info("check");
        log.info(userList);
        exportToExcel(response, userList, "all_users.xlsx");
    }

    // 조회된 사용자 정보를 엑셀로 다운로드
    @GetMapping("/downloadFiltered")
    public void downloadFilteredUsers(
            @RequestParam(value = "userName", required = false) String userName,
            @RequestParam(value = "authorityName", required = false) String authorityName,
            HttpServletResponse response) throws IOException {

        Criteria criteria = new Criteria();
        criteria.setuserName(userName);
        criteria.setauthorityName(authorityName);

        List<User> filteredUserList = userService.getFilteredUsers(criteria);
        exportToExcel(response, filteredUserList, "filtered_users.xlsx");
    }

    // 엑셀 파일로 데이터를 내보내는 메서드
    private void exportToExcel(HttpServletResponse response, List<User> userList, String fileName) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Users");

        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("UserID");
        headerRow.createCell(1).setCellValue("userName");
        headerRow.createCell(2).setCellValue("Password");
        headerRow.createCell(3).setCellValue("AuthorityCode");
        headerRow.createCell(4).setCellValue("AuthorityName");

        int rowNum = 1;
        for (User user : userList) {
        	log.info(user);
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(user.getuserID());
            row.createCell(1).setCellValue(user.getuserName());
            row.createCell(2).setCellValue(user.getPassword());
            row.createCell(3).setCellValue(user.getauthorityCode());
            row.createCell(4).setCellValue(user.getauthorityName());
        }

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
        try (OutputStream os = response.getOutputStream()) {
            workbook.write(os);
        }
        workbook.close();
    }
    
    @PostMapping("/upload")
    public String uploadExcelFile(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "파일이 없습니다. 다시 시도해 주세요.");
            return "redirect:/tms/adminuser";
        }

        try (InputStream inputStream = file.getInputStream()) {
            Workbook workbook = WorkbookFactory.create(inputStream);
            Sheet sheet = workbook.getSheetAt(0);
            List<User> users = new ArrayList<User>();

            // 첫 번째 행은 헤더이므로 건너뜁니다.
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    User user = new User();
                    user.setuserID(row.getCell(0).getStringCellValue());
                    user.setuserName(row.getCell(1).getStringCellValue());
                    user.setPassword(row.getCell(2).getStringCellValue());
                    user.setauthorityCode((int)row.getCell(3).getNumericCellValue());
                    user.setauthorityName(row.getCell(4).getStringCellValue());
                    users.add(user);
                }
            }

            // 데이터베이스에 저장
            adminService.saveAll(users);
            redirectAttributes.addFlashAttribute("message", "파일 업로드가 성공적으로 완료되었습니다!");

        } catch (IOException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("message", "파일 처리 중 오류가 발생했습니다. 다시 시도해 주세요.");
        }

        return "redirect:/tms/adminuser";
    }
    
    
    ////공지사항 Controller
    
 // 페이징 및 검색을 통한 공지사항 목록을 JSON으로 반환
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

    // 특정 ID의 공지사항을 JSON 형식으로 반환
    @GetMapping(value = "api/notices/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Notice getNoticeById(@PathVariable("id") Long id) {
        return adminService.getNoticeById(id);
    }
    
    @GetMapping("/notice")
    public String getNotices(@RequestParam(value = "postDate", required = false) String postDate,
                             @RequestParam(value = "title", required = false) String title,
                             @RequestParam(value = "content", required = false) String content,
                             @RequestParam(value = "page", defaultValue = "1") int page,
                             @RequestParam(value = "size", defaultValue = "10") int size,
                             Model model) {
    	log.info(postDate);
        List<Notice> notices = adminService.searchNotices(postDate, title, content, page, size);
        int totalNotices = adminService.getTotalNoticesCount(postDate, title, content);
        int totalPages = (int) Math.ceil((double) totalNotices / size);

        model.addAttribute("notices", notices);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalNotices", totalNotices);

        return "notice"; // JSP 파일의 경로
    }



}
package com.tms.backend.controller;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.tms.backend.service.AdminService;
import com.tms.backend.service.UserService;
import com.tms.backend.vo.Criteria;
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
    
    @PostMapping("join")
    public String Join(User user) {
    	adminService.join(user);
    	return "redirect:/tms/adminuser";
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
    
    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable("id") String id, RedirectAttributes redirectAttributes) {
        boolean success = adminService.deleteUser(id);
        if (success) {
            redirectAttributes.addFlashAttribute("message", "User deleted successfully!");
        } else {
            redirectAttributes.addFlashAttribute("message", "Error deleting user.");
        }
        return "redirect:/tms/adminuser";
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
    
    @GetMapping(value="api/users", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<User> getUserList(
        @RequestParam(value = "page", defaultValue = "1") int page,
        @RequestParam(value = "userName", required = false) String userName,
        @RequestParam(value = "authorityName", required = false) String authorityName) {

    	Criteria criteria = new Criteria();
        criteria.setPage(page);
        criteria.setPerPageNum(10);
        criteria.setuserName(userName);
        criteria.setauthorityName(authorityName);

        return adminService.getList(criteria);
    }
    
//
//    @PostMapping("/upload")
//    public String uploadExcel(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
//        if (file.isEmpty()) {
//            redirectAttributes.addFlashAttribute("message", "Please select a file to upload");
//            return "redirect:/tms/adminuser";
//        }
//
//        try {
//            Workbook workbook = new XSSFWorkbook(file.getInputStream());
//            Sheet sheet = workbook.getSheetAt(0);
//            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
//                Row row = sheet.getRow(i);
//                User user = new User();
//                user.setuserID(row.getCell(0).getStringCellValue());
//                user.setuserName(row.getCell(1).getStringCellValue());
//                user.setPassword(row.getCell(2).getStringCellValue());
//                user.setauthorityCode((int) row.getCell(3).getNumericCellValue());
//                user.setauthorityName(row.getCell(4).getStringCellValue());
//                adminService.saveUser(user);
//            }
//            workbook.close();
//        } catch (IOException e) {
//            log.error("Failed to upload the Excel file", e);
//        }
//
//        redirectAttributes.addFlashAttribute("message", "File uploaded successfully");
//        return "redirect:/tms/adminuser";
//    }
    
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
            return "redirect:/tms/test";
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

        return "redirect:/tms/test";
    }

}
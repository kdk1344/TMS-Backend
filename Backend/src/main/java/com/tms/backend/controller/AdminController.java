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
    
    @DeleteMapping("/deleteuser")
    @ResponseBody
    public Map<String, String> deleteUsers(@RequestBody List<String> IDList) {
        boolean success = adminService.deleteUser(IDList.toArray(new String[0]));

        if (success) {
            return Map.of("message", "Users deleted successfully!");
        } else {
            return Map.of("message", "Error deleting users.");
        }
    }
    
//    @GetMapping("/delete")
//    public String deleteUsers(@RequestParam("IDList") String ids, RedirectAttributes redirectAttributes) {
//        String[] idArray = ids.split(","); // ��ǥ�� ���е� ID ���ڿ��� �迭�� ��ȯ
//        boolean success = adminService.deleteUser(idArray);
//        
//        if (success) {
//            redirectAttributes.addFlashAttribute("message", "Users deleted successfully!");
//        } else {
//            redirectAttributes.addFlashAttribute("message", "Error deleting users.");
//        }
//
//        return "redirect:/tms/adminuser";
//    }
    
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
    
    // ��ü ����� ������ ������ �ٿ�ε�
    @GetMapping("/downloadAll")
    public void downloadAllUsers(HttpServletResponse response) throws IOException {
        List<User> userList = userService.getAllUser();
        log.info("check");
        log.info(userList);
        exportToExcel(response, userList, "all_users.xlsx");
    }

    // ��ȸ�� ����� ������ ������ �ٿ�ε�
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

    // ���� ���Ϸ� �����͸� �������� �޼���
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
            redirectAttributes.addFlashAttribute("message", "������ �����ϴ�. �ٽ� �õ��� �ּ���.");
            return "redirect:/tms/adminuser";
        }

        try (InputStream inputStream = file.getInputStream()) {
            Workbook workbook = WorkbookFactory.create(inputStream);
            Sheet sheet = workbook.getSheetAt(0);
            List<User> users = new ArrayList<User>();

            // ù ��° ���� ����̹Ƿ� �ǳʶݴϴ�.
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

            // �����ͺ��̽��� ����
            adminService.saveAll(users);
            redirectAttributes.addFlashAttribute("message", "���� ���ε尡 ���������� �Ϸ�Ǿ����ϴ�!");

        } catch (IOException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("message", "���� ó�� �� ������ �߻��߽��ϴ�. �ٽ� �õ��� �ּ���.");
        }

        return "redirect:/tms/adminuser";
    }
    
    
    ////�������� Controller
    
    @GetMapping("/notice")
    public String getNotices(Model model) {
        List<Notice> notices = adminService.getAllNotices();
        model.addAttribute("notices", notices);
        return "notice";
    }
    
 // ��� ���������� JSON �������� ��ȯ
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Notice> getAllNotices() {
        return adminService.getAllNotices();
    }

    // Ư�� ID�� ���������� JSON �������� ��ȯ
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Notice getNoticeById(@PathVariable("id") Long id) {
        return adminService.getNoticeById(id);
    }

    // �������� ��� �������� �̵�
    @GetMapping("/list")
    public String showNoticeList(Model model) {
        List<Notice> notices = adminService.getAllNotices();
        model.addAttribute("notices", notices);
        return "notice/list"; // JSP ������ ���
    }

    // �������� ��� �� �������� �̵�
    @GetMapping("/new")
    public String showCreateNoticeForm(Model model) {
        model.addAttribute("notice", new Notice());
        return "notice/form"; // JSP ������ ���
    }

    // �� �������� ���
    @PostMapping
    public String createNotice(@ModelAttribute Notice notice) {
        adminService.createNotice(notice);
        return "redirect:/notices/list";
    }

    // �������� ���� �� �������� �̵�
    @GetMapping("/edit/{id}")
    public String showEditNoticeForm(@PathVariable("id") Long id, Model model) {
        Notice notice = adminService.getNoticeById(id);
        model.addAttribute("notice", notice);
        return "notice/edit"; // JSP ������ ���
    }

    // �������� ����
    @PostMapping("/edit/{id}")
    public String updateNotice(@PathVariable("id") Long id, @ModelAttribute Notice notice) {
        notice.setSeq(id);
        adminService.updateNotice(notice);
        return "redirect:/notices/list";
    }

    // �������� ����
    @PostMapping("/delete/{id}")
    public String deleteNotice(@PathVariable("id") Long id) {
        adminService.deleteNotice(id);
        return "redirect:/notices/list";
    }

}
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
    
	//����� ������ ȭ��
    @GetMapping("/adminUser")
    public String UserPage(Criteria criteria, Model model) {
        return "adminUser";
    }
    
    //����� ���
    @PostMapping(value= "api/join", produces = "application/json")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> join(@RequestBody User user,
            HttpServletRequest request) {
    	
//    	// ���ǿ��� authorityCode ��������
//        HttpSession session = request.getSession(false); // ������ ���ٸ� ���� ������ ����
//        if (session == null || session.getAttribute("authorityCode") == null) {
//            // ������ ���ų� authorityCode�� ������ 401 Unauthorized ��ȯ
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("message", "������ �����ϴ�. �α����ϼ���."));
//        }
//
//        Integer authorityCode = (Integer) session.getAttribute("authorityCode");
//
//        // ���� Ȯ��: 1, 2, 3�� ���Ѹ� ���
//        if (authorityCode != 1 && authorityCode != 2 && authorityCode != 3) {
//            // ������ ���� ��� 403 Forbidden ��ȯ
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Collections.singletonMap("message", "�� �۾��� ������ ������ �����ϴ�."));
//        }
        
        Map<String, Object> response = new HashMap<>();
        // join �޼��忡�� ���� �� ���ܸ� �߻���Ű�� ����
        try {
            adminService.join(user);  // ����� ���
            response.put("status", "success");
            response.put("message", "User successfully registered!");
            response.put("user", user); // ���Ե� ����� ���� ��ȯ
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
    
    //����� ����
    @PostMapping(value= "api/idmodify" , produces = "application/json")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> idModify(@RequestBody User user,
            HttpServletRequest request) {
    	
//    	// ���ǿ��� authorityCode ��������
//        HttpSession session = request.getSession(false); // ������ ���ٸ� ���� ������ ����
//        if (session == null || session.getAttribute("authorityCode") == null) {
//            // ������ ���ų� authorityCode�� ������ 401 Unauthorized ��ȯ
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("message", "������ �����ϴ�. �α����ϼ���."));
//        }
//
//        Integer authorityCode = (Integer) session.getAttribute("authorityCode");
//
//        // ���� Ȯ��: 1, 2, 3�� ���Ѹ� ���
//        if (authorityCode != 1 && authorityCode != 2 && authorityCode != 3) {
//            // ������ ���� ��� 403 Forbidden ��ȯ
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Collections.singletonMap("message", "�� �۾��� ������ ������ �����ϴ�."));
//        }
        
    	Map<String, Object> response = new HashMap<>();
        try {
        	//����� ����
            boolean success = adminService.updateUser(user, request);
            if (success) {
                response.put("status", "success");
                response.put("message", "����� ������ �����߽��ϴ�.");
                response.put("user", user); // ������Ʈ�� ����� ���� ��ȯ
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                response.put("status", "failure");
                response.put("message", "����� ���� �߿� �����߽��ϴ�");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.put("status", "error");
            response.put("message", "����� ���� �߿� ������ �߻��߽��ϴ�");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    //����� ����
    @DeleteMapping(value= "api/deleteuser", produces = "application/json")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteUsers(@RequestBody List<String> IDList,
            HttpServletRequest request) {
    	
    	// ���ǿ��� authorityCode ��������
        HttpSession session = request.getSession(false); // ������ ���ٸ� ���� ������ ����
        if (session == null || session.getAttribute("authorityCode") == null) {
            // ������ ���ų� authorityCode�� ������ 401 Unauthorized ��ȯ
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("message", "������ �����ϴ�. �α����ϼ���."));
        }

        Integer authorityCode = (Integer) session.getAttribute("authorityCode");

        // ���� Ȯ��: 1, 2, 3�� ���Ѹ� ���
        if (authorityCode != 1 && authorityCode != 2 && authorityCode != 3) {
            // ������ ���� ��� 403 Forbidden ��ȯ
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Collections.singletonMap("message", "�� �۾��� ������ ������ �����ϴ�."));
        }
        
    	Map<String, Object> response = new HashMap<>();
        try {
        	//����� ����
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
    
    //����� ���� ��������
    @GetMapping(value = "api/users", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Object> getUserList(
        @RequestParam(value = "page", defaultValue = "1") int page,
        @RequestParam(value = "userName", required = false) String userName,
        @RequestParam(value = "authorityName", required = false) String authorityName) {
    	
    	//����� ��� ������ �������� ���� �Ķ���� �Է�
        Criteria criteria = new Criteria();
        criteria.setPage(page);
        criteria.setPerPageNum(15);
        criteria.setuserName(userName);
        criteria.setauthorityName(authorityName);
        
        //����� ��� ��������
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
    public String downloadAllUsers(HttpServletResponse response,HttpServletRequest request) throws IOException {

    	// ���ǿ��� authorityCode ��������
        HttpSession session = request.getSession(false); // ������ ���ٸ� ���� ������ ����
        if (session == null || session.getAttribute("authorityCode") == null) {
            // ������ ���ų� authorityCode�� ������ ���� �߻�
        	return "redirect:/tms/login"; // �α��� �������� �����̷�Ʈ
        }

        Integer authorityCode = (Integer) session.getAttribute("authorityCode");

        // ���� Ȯ��: 1, 2, 3�� ���Ѹ� ���
        if (authorityCode != 1 && authorityCode != 2 && authorityCode != 3) {
        	log.info(authorityCode);
            // ������ ���� ��� ���� �߻�
        	return "redirect:/tms/adminUser"; // ������ �������� �̵�
        }
        //��� ���� ���� ��������
        List<User> userList = userService.getAllUser();
        exportToExcel(response, userList, "all_users.xlsx");
        return null;
    }
    
    // �׼� ���� ���ø� �ٿ�ε�
    @GetMapping("/userexampleexcel")
    public void downloadExuser(HttpServletResponse response) throws IOException {
    	//����� ���� �׼� ���� ����
    	List<User> ExampleDEV = adminService.findAuthorityCode(99999);  	
    	exportToExcel(response, ExampleDEV, "example.xlsx");
    }


    // ��ȸ�� ����� ������ ������ �ٿ�ε�
    @GetMapping("/downloadFiltered")
    public String downloadFilteredUsers(
            @RequestParam(value = "userName", required = false) String userName,
            @RequestParam(value = "authorityName", required = false) String authorityName,
            HttpServletResponse response,
            HttpServletRequest request) throws IOException {

    	// ���ǿ��� authorityCode ��������
        HttpSession session = request.getSession(false); // ������ ���ٸ� ���� ������ ����
        if (session == null || session.getAttribute("authorityCode") == null) {
            // ������ ���ų� authorityCode�� ������ ���� �߻�
        	return "redirect:/tms/login"; // �α��� �������� �����̷�Ʈ
        }

        Integer authorityCode = (Integer) session.getAttribute("authorityCode");

        // ���� Ȯ��: 1, 2, 3�� ���Ѹ� ���
        if (authorityCode != 1 && authorityCode != 2 && authorityCode != 3) {
            // ������ ���� ��� ���� �߻�
        	return "redirect:/tms/adminUser"; // ������ �������� �̵�
        }
        
        // ����� ������ ���� �Ķ���� �Է�
        Criteria criteria = new Criteria();
        criteria.setuserName(userName);
        criteria.setauthorityName(authorityName);
        
        //���͸��� ����� ���� ��������
        List<User> filteredUserList = userService.getFilteredUsers(criteria);
        exportToExcel(response, filteredUserList, "filtered_users.xlsx");
        return null;
    }

    // ���� ���Ϸ� �����͸� �������� �޼���
    private void exportToExcel(HttpServletResponse response, List<User> userList, String fileName) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Users");
        String check = "";
        if (userList.isEmpty()) {
        	check = "no_value";
        }
        
        //�׼� Row �����
        Row headerRow = sheet.createRow(0);
        //�÷� �����
        headerRow.createCell(0).setCellValue("UserID");
        headerRow.createCell(1).setCellValue("userName");
        headerRow.createCell(2).setCellValue("Password");
        //����� ���� ���� ������ ���� ����
        if (check != "") {
        	headerRow.createCell(3).setCellValue("AuthorityName");
        }
        //�׿� �׼� ���� ������ ���� ����
        else {
	        headerRow.createCell(3).setCellValue("AuthorityCode");
	        headerRow.createCell(4).setCellValue("AuthorityName");
        }
        
        int rowNum = 1;
        //����� ���� ���ʴ�� �Է�
        for (User user : userList) {
            Row row = sheet.createRow(rowNum++);
            //����� ���� �׼��� �Է�
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
    
    //�׼� ���ε� ���
    @PostMapping(value = "api/userupload", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> uploadExcelFile(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes,
            HttpServletRequest request) {
    	
    	// ���ǿ��� authorityCode ��������
        HttpSession session = request.getSession(false); // ������ ���ٸ� ���� ������ ����
        if (session == null || session.getAttribute("authorityCode") == null) {
            // ������ ���ų� authorityCode�� ������ 401 Unauthorized ��ȯ
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("message", "������ �����ϴ�. �α����ϼ���."));
        }

        Integer authorityCode = (Integer) session.getAttribute("authorityCode");

        // ���� Ȯ��: 1, 2, 3�� ���Ѹ� ���
        if (authorityCode != 1 && authorityCode != 2 && authorityCode != 3) {
            // ������ ���� ��� 403 Forbidden ��ȯ
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Collections.singletonMap("message", "�� �۾��� ������ ������ �����ϴ�."));
        }
        
    	Map<String, Object> response = new HashMap<>();
        if (file.isEmpty()) {
        	response.put("status", "failure");
            response.put("message", "������ �����ϴ�. �ٽ� �õ��� �ּ���.");
        	return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        try (InputStream inputStream = file.getInputStream()) {
            Workbook workbook = WorkbookFactory.create(inputStream);
            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);
            
            // �����ϴ� �÷��� ����Ʈ
            List<String> expectedHeaders = Arrays.asList("userID", "userName", "Password", "AuthorityName");
            if (!fileService.isHeaderValid(headerRow, expectedHeaders)) { // �÷� ��
                response.put("status", "error");
                response.put("message", "����� �÷����� �ùٸ��� �ʽ��ϴ�.");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            
            List<User> users = new ArrayList<User>();

            // ù ��° ���� ����̹Ƿ� �ǳʶݴϴ�.
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    User user = new User();
                    try {
                    	//���ε��� ����� ���� �Է�
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
                        // ���� ���� �Ǵ� ����ġ ���� �÷� ������ ó��
                        e.printStackTrace();
                        response.put("status", "error");
                        response.put("message", "������ ������ ������ �ùٸ��� �ʽ��ϴ�: �� " + (i + 1));
                        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                    }
                }
            }

            // �����ͺ��̽��� ����
            adminService.saveAll(users);
            response.put("status", "success");
            response.put("message", "���� ���ε尡 ���������� �Ϸ�Ǿ����ϴ�!");
            response.put("totalUploaded", users.size());

        } catch (DuplicateKeyException e) {
            // �ߺ��� Ű ���� ó��
            e.printStackTrace();
            response.put("status", "error");
            response.put("message", "�ߺ��� ����� ID�� �߰ߵǾ����ϴ�.");
            return new ResponseEntity<>(response, HttpStatus.CONFLICT); // 409 Conflict
        } catch (IOException e) {
            e.printStackTrace();
            response.put("status", "error");
            response.put("message", "���� ó�� �� ������ �߻��߽��ϴ�. �ٽ� �õ��� �ּ���.");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    

    
    



}
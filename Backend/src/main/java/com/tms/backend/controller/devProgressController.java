package com.tms.backend.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.tms.backend.service.AdminService;
import com.tms.backend.service.DevService;
import com.tms.backend.service.FileService;
import com.tms.backend.service.UserService;
import com.tms.backend.vo.CommonCode;
import com.tms.backend.vo.Criteria;
import com.tms.backend.vo.Notice;
import com.tms.backend.vo.PageDTO;
import com.tms.backend.vo.User;
import com.tms.backend.vo.categoryCode;
import com.tms.backend.vo.devProgress;

import java.text.ParseException;

import lombok.extern.log4j.Log4j;

@Controller
@Log4j
@RequestMapping("/tms/*")
public class devProgressController {
	
	@Autowired
    private DevService devservice;
	
	@Autowired
	private AdminService adminService;
	
	@Autowired
	private FileService fileservice;
	
	//����������� Controller
	
	@GetMapping("/devProgress")
	public String devProgressPage(
	        @RequestParam(value = "majorCategory", required = false) String majorCategory,
	        @RequestParam(value = "subCategory", required = false) String subCategory,
	        @RequestParam(value = "programType", required = false) String programType,
	        @RequestParam(value = "programName", required = false) String programName,
	        @RequestParam(value = "programId", required = false) String programId,
	        @RequestParam(value = "programStatus", required = false) String programStatus,
	        @RequestParam(value = "developer", required = false) String developer,
	        @RequestParam(value = "devStatus", required = false) String devStatus,
	        @RequestParam(value = "devStartDate", required = false) String devStartDate,
	        @RequestParam(value = "devEndDate", required = false) String devEndDate,
	        @RequestParam(value = "pl", required = false) String pl,
	        @RequestParam(value = "thirdPartyTestMgr", required = false) String thirdPartyTestMgr,
	        @RequestParam(value = "ItMgr", required = false) String ItMgr,
	        @RequestParam(value = "BusiMgr", required = false) String BusiMgr,
	        @RequestParam(value = "page", defaultValue = "1") int page,
	        @RequestParam(value = "size", defaultValue = "15") int size,
	        Model model) {
		
		log.info(programStatus);

	    // Service�� ���� �����͸� ��ȸ
	    List<devProgress> devProgressList = devservice.searchDevProgress(
	            majorCategory, subCategory, programType, programName,programId, programStatus, developer,
	            devStatus, devStartDate, devEndDate, pl, thirdPartyTestMgr, ItMgr, BusiMgr, page, size
	    );
	    


	    int totalDevProgress = devservice.getTotalDevProgressCount(
	            majorCategory, subCategory, programType, programName,programId, programStatus, developer,
	            devStatus, devStartDate, devEndDate, pl, thirdPartyTestMgr, ItMgr, BusiMgr
	    );

	    int totalPages = (int) Math.ceil((double) totalDevProgress / size);
	    
	    log.info(devProgressList);

	    // �𵨿� �����͸� �߰�
	    model.addAttribute("devProgressList", devProgressList);
	    model.addAttribute("currentPage", page);
	    model.addAttribute("totalPages", totalPages);
	    model.addAttribute("totalDevProgress", totalDevProgress);

	    return "devProgress"; // JSP �������� �̵�
	}
	
	//���� ���� ��Ȳ ��ȸ
	@GetMapping(value="api/devProgress" , produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<Map<String, Object>> APIdevProgressPage(
	        @RequestParam(value = "majorCategory", required = false) String majorCategory,
	        @RequestParam(value = "subCategory", required = false) String subCategory,
	        @RequestParam(value = "programType", required = false) String programType,
	        @RequestParam(value = "programName", required = false) String programName,
	        @RequestParam(value = "programId", required = false) String programId,
	        @RequestParam(value = "programStatus", required = false) String programStatus,
	        @RequestParam(value = "developer", required = false) String developer,
	        @RequestParam(value = "devStatus", required = false) String devStatus,
	        @RequestParam(value = "devStartDate", required = false) String devStartDate,
	        @RequestParam(value = "devEndDate", required = false) String devEndDate,
	        @RequestParam(value = "pl", required = false) String pl,
	        @RequestParam(value = "thirdPartyTestMgr", required = false) String thirdPartyTestMgr,
	        @RequestParam(value = "ItMgr", required = false) String ItMgr,
	        @RequestParam(value = "BusiMgr", required = false) String BusiMgr,
	        @RequestParam(value = "page", defaultValue = "1") int page,
	        @RequestParam(value = "size", defaultValue = "15") int size) {

	    // Service�� ���� �����͸� ��ȸ
	    List<devProgress> devProgressList = devservice.searchDevProgress(
	            majorCategory, subCategory, programType, programName, programId, programStatus, developer,
	            devStatus, devStartDate, devEndDate, pl, thirdPartyTestMgr, ItMgr, BusiMgr, page, size
	    );

	    int totalDevProgress = devservice.getTotalDevProgressCount(
	            majorCategory, subCategory, programType, programName, programId, programStatus, developer,
	            devStatus, devStartDate, devEndDate, pl, thirdPartyTestMgr, ItMgr, BusiMgr
	    );

	    int totalPages = (int) Math.ceil((double) totalDevProgress / size);
	    
	    List<User> developers = adminService.findAuthorityCode(5);
	    
	    // ���� ����
	    Map<String, Object> response = new HashMap<>();
	    response.put("devProgressList", devProgressList);
	    response.put("currentPage", page);
	    response.put("totalPages", totalPages);
	    response.put("totalDevProgress", totalDevProgress);
	    response.put("developers", developers);

	    return ResponseEntity.ok(response); // JSON���� ���� ��ȯ
	}
	
	//������ Ȯ��
	@GetMapping(value = "api/developer", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getDevUser() {
    	Map<String, Object> response = new HashMap<>();
        List<User> developer = adminService.findAuthorityCode(5);
        
        log.info("Ȯ����" + developer);

        // ���� ������ ����
        if (developer != null && !developer.isEmpty()) {
            response.put("status", "success");
            response.put("developer", developer);
        } else {
            response.put("status", "failure");
            response.put("message", "No developer found.");
            response.put("developer", developer);
        }

        // ��ȸ�� ����� ��ȯ
        return ResponseEntity.ok(response);
    }
	
//	//��з� Ȯ��
//	@GetMapping(value = "api/major", produces = MediaType.APPLICATION_JSON_VALUE)
//    @ResponseBody
//    public ResponseEntity<Map<String, Object>> getMajor() {
//    	Map<String, Object> response = new HashMap<>();
//    	List<categoryCode> Major = adminService.getParentCategoryCodes();
//
//        // ���� ������ ����
//        if (Major != null && !Major.isEmpty()) {
//            response.put("status", "success");
//            response.put("major", Major);
//        } else {
//            response.put("status", "failure");
//            response.put("message", "��з� ������ ã�� �� �����ϴ�");
//        }
//
//        // ��ȸ�� ����� ��ȯ
//        return ResponseEntity.ok(response);
//    }
	
	//���α׷� Ÿ�� Ȯ��
	@GetMapping(value = "api/programType", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getProgramType() {
    	Map<String, Object> response = new HashMap<>();
    	List<CommonCode> ProgramType= adminService.getCCCode("02");

        // ���� ������ ����
        if (ProgramType != null && !ProgramType.isEmpty()) {
            response.put("status", "success");
            response.put("programType", ProgramType);
        } else {
            response.put("status", "failure");
            response.put("message", "���α׷� Ÿ�� ������ ã�� �� �����ϴ�");
        }

        // ��ȸ�� ����� ��ȯ
        return ResponseEntity.ok(response);
    }
	
	//���α׷� �� ���� Ȯ��
	@GetMapping(value = "api/programDtype", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getProgramDType() {
    	Map<String, Object> response = new HashMap<>();
    	List<CommonCode> ProgramDType= adminService.getCCCode("03");

        // ���� ������ ����
        if (ProgramDType != null && !ProgramDType.isEmpty()) {
            response.put("status", "success");
            response.put("programDtype", ProgramDType);
        } else {
            response.put("status", "failure");
            response.put("message", "���α׷� �� ���� ������ ã�� �� �����ϴ�");
        }

        // ��ȸ�� ����� ��ȯ
        return ResponseEntity.ok(response);
    }
	
	//�켱�� �� ���̵� Ȯ��
	@GetMapping(value = "api/levels", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getlevels() {
    	Map<String, Object> response = new HashMap<>();
    	List<CommonCode> levels= adminService.getCCCode("04");

        // ���� ������ ����
        if (levels != null && !levels.isEmpty()) {
            response.put("status", "success");
            response.put("levels", levels);
        } else {
            response.put("status", "failure");
            response.put("message", "�켱�� �� ���̵� ������ ã�� �� �����ϴ�");
        }

        // ��ȸ�� ����� ��ȯ
        return ResponseEntity.ok(response);
    }
	
	//���α׷� ���� Ȯ��
	@GetMapping(value = "api/programStatus", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getProgramStatus() {
    	Map<String, Object> response = new HashMap<>();
    	List<CommonCode> ProgramStatus= adminService.getCCCode("05");

        // ���� ������ ����
        if (ProgramStatus != null && !ProgramStatus.isEmpty()) {
            response.put("status", "success");
            response.put("programStatus", ProgramStatus);
        } else {
            response.put("status", "failure");
            response.put("message", "���α׷� ���� ������ ã�� �� �����ϴ�");
        }

        // ��ȸ�� ����� ��ȯ
        return ResponseEntity.ok(response);
    }
	
	//����� ID Ȯ��
	@GetMapping(value = "api/user", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getUserIDLIST() {
    	Map<String, Object> response = new HashMap<>();
    	List<User> User= adminService.getUserList();

        // ���� ������ ����
        if (User != null && !User.isEmpty()) {
            response.put("status", "success");
            response.put("user", User);
        } else {
            response.put("status", "failure");
            response.put("message", "����� ������ ã�� �� �����ϴ�");
        }

        // ��ȸ�� ����� ��ȯ
        return ResponseEntity.ok(response);
    }
	
	//�켱�� �� ���̵� Ȯ��
	@GetMapping(value = "api/testResult", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getTestResult() {
    	Map<String, Object> response = new HashMap<>();
    	List<CommonCode> TestResult= adminService.getCCCode("07");

        // ���� ������ ����
        if (TestResult != null && !TestResult.isEmpty()) {
            response.put("status", "success");
            response.put("testResult", TestResult);
        } else {
            response.put("status", "failure");
            response.put("message", "�׽�Ʈ ����� ã�� �� �����ϴ�");
        }

        // ��ȸ�� ����� ��ȯ
        return ResponseEntity.ok(response);
    }
	
	//IT ����� Ȯ��
	@GetMapping(value = "api/itMgr", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getITMGR() {
    	Map<String, Object> response = new HashMap<>();
    	List<User> ITMGR= adminService.findAuthorityCode(7);

        // ���� ������ ����
        if (ITMGR != null && !ITMGR.isEmpty()) {
            response.put("status", "success");
            response.put("itMgr", ITMGR);
        } else {
            response.put("status", "failure");
            response.put("message", "IT ����� ������ ã�� �� �����ϴ�");
        }

        // ��ȸ�� ����� ��ȯ
        return ResponseEntity.ok(response);
    }
	
	//��� ����� Ȯ��
	@GetMapping(value = "api/busiMgr", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getBUSIMGR() {
    	Map<String, Object> response = new HashMap<>();
    	List<User> BUSIMGR= adminService.findAuthorityCode(8);

        // ���� ������ ����
        if (BUSIMGR != null && !BUSIMGR.isEmpty()) {
            response.put("status", "success");
            response.put("busiMgr", BUSIMGR);
        } else {
            response.put("status", "failure");
            response.put("message", "��� ����� ������ ã�� �� �����ϴ�");
        }

        // ��ȸ�� ����� ��ȯ
        return ResponseEntity.ok(response);
    }
	
	//���� ���� ���� Ȯ��
	@GetMapping(value = "api/devStatus", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getDevStatus() {
    	Map<String, Object> response = new HashMap<>();
    	List<CommonCode> devStatus= adminService.getCCCode("06");

        // ���� ������ ����
        if (devStatus != null && !devStatus.isEmpty()) {
            response.put("status", "success");
            response.put("devStatus", devStatus);
        } else {
            response.put("status", "failure");
            response.put("message", "��з� ������ ã�� �� �����ϴ�");
        }

        // ��ȸ�� ����� ��ȯ
        return ResponseEntity.ok(response);
    }
	
	@GetMapping("/devProgressReg")
	public String devProgressPage() {

	    return "devProgressReg"; // JSP �������� �̵�
	}
	
	//���� ���� ��Ȳ ��� ������
	@PostMapping(value="api/devProgressReg" , produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<Map<String, Object>> devProgressReg(HttpServletRequest request,
			@RequestPart("devProgress") devProgress devProgress,
			@RequestPart(value = "file", required = false) MultipartFile[] files) {
		HttpSession session = request.getSession(false); // ������ ���ٸ� ���� ������ ����
		if (session == null || session.getAttribute("authorityCode") == null) {
			// ������ ���ų� authorityCode�� ������ 401 Unauthorized ��ȯ
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("message", "������ �����ϴ�. �α����ϼ���."));
			}
		Map<String, Object> response = new HashMap<>();
		try {
			// �ʼ� �׸� üũ
	        if (devProgress.getMajorCategory() == null || devProgress.getMajorCategory().trim().isEmpty()) {
	            throw new IllegalArgumentException("���� ��з��� �ʼ� �Է� �׸��Դϴ�.");
	        }
	        if (devProgress.getSubCategory() == null || devProgress.getSubCategory().trim().isEmpty()) {
	            throw new IllegalArgumentException("���� �ߺз��� �ʼ� �Է� �׸��Դϴ�.");
	        }
	        if (devProgress.getProgramId() == null || devProgress.getProgramId().trim().isEmpty()) {
	            throw new IllegalArgumentException("���α׷� ID�� �ʼ� �Է� �׸��Դϴ�.");
	        }
	        if (devProgress.getProgramName() == null || devProgress.getProgramName().trim().isEmpty()) {
	            throw new IllegalArgumentException("���α׷����� �ʼ� �Է� �׸��Դϴ�.");
	        }
	        if (devProgress.getDeveloper() == null || devProgress.getDeveloper().trim().isEmpty()) {
	            throw new IllegalArgumentException("�����ڴ� �ʼ� �Է� �׸��Դϴ�.");
	        }
	        if (devProgress.getPriority() == null || devProgress.getPriority().trim().isEmpty()) {
	            throw new IllegalArgumentException("�켱������ �ʼ� �Է� �׸��Դϴ�.");
	        }
	        if (devProgress.getProgramStatus() == null || devProgress.getProgramStatus().trim().isEmpty()) {
	            throw new IllegalArgumentException("���α׷� ���´� �ʼ� �Է� �׸��Դϴ�.");
	        }
	        if (devProgress.getPriority() == null || devProgress.getPriority().trim().isEmpty()) {
	            throw new IllegalArgumentException("�켱������ �ʼ� �Է� �׸��Դϴ�.");
	        }
	        if ("����".equals(devProgress.getProgramStatus()) &&
	        	(devProgress.getDeletionReason() == null || devProgress.getDeletionReason().trim().isEmpty())) {
	        	throw new IllegalArgumentException("���α׷� ���°� '����'�� ��� ����ó������ �Է��� �ʼ��Դϴ�.");
	        }
	        // Date �ʵ� üũ
	        if (devProgress.getPlannedStartDate() == null) {
	            throw new IllegalArgumentException("���� �������� �ʼ� �Է� �׸��Դϴ�.");
	        }
	        if (devProgress.getPlannedEndDate() == null) {
	            throw new IllegalArgumentException("�Ϸ� �������� �ʼ� �Է� �׸��Դϴ�.");
	        }
	        // �ʿ��� ��� �ٸ� �ʼ� �׸� �߰��� üũ
			
        	devservice.insertdevProgress(devProgress);  // ���� ��Ȳ ���� ���� �߰�
        	
        	// ���ο� ���� ���ε� ó��
            if (files != null && files.length > 0) {
                fileservice.handleFileUpload(files, "devProgress", devProgress.getSeq());
            }
        	
            response.put("status", "success");
            response.put("message", "���� ���� ��Ȳ ������ ��ϵǾ����ϴ�");
            response.put("devProgress", devProgress);  // ��ϵ� ���� ��Ȳ ���� ���� ��ȯ
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            response.put("status", "failure");
            response.put("message", e.getMessage());  // ���� �޽����� response�� ���Խ�Ŵ
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("status", "error");
            response.put("message", "���� ���� ��Ȳ ���� ���� �߿� ������ �߻��߽��ϴ�.");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
	}
	
	@GetMapping("/devProgressEdit")
	public String devProgressEditPage() {

	    return "devProgressEdit"; // JSP �������� �̵�
	}
	
	//���� ���� ��Ȳ ���� ������
	@GetMapping(value="api/devProgressEditPage" , produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<Map<String, Object>> devProgressEditPage2(HttpServletRequest request,
			@RequestParam("seq") Integer seq) {
		HttpSession session = request.getSession(false); // ������ ���ٸ� ���� ������ ����
		if (session == null || session.getAttribute("authorityCode") == null) {
			// ������ ���ų� authorityCode�� ������ 401 Unauthorized ��ȯ
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("message", "������ �����ϴ�. �α����ϼ���."));
			}
//		Integer authorityCode = (Integer) session.getAttribute("authorityCode");
		
		Map<String, Object> response = new HashMap<>();
		devProgress DevProgressEdit = devservice.getDevById(seq);
		  
	    // ���� ����
		response.put("status", "success");
        response.put("message", "���� ���� ��Ȳ ���� ����.");
	    response.put("devProgressEdit", DevProgressEdit);
	    
	    return ResponseEntity.ok(response); // JSON���� ���� ��ȯ
	}
	
	//���� ���� ��Ȳ ����
    @PostMapping(value = "api/devProgressEdit", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> devProgressEdit(@RequestPart("devProgress") devProgress devProgress,  
    		@RequestPart(value = "file", required = false) MultipartFile[] files) {
        Map<String, Object> response = new HashMap<>();
        devProgress DevProgressEdit = devservice.getDevById(devProgress.getSeq());
        try {
        	// devProgress�� �ʵ带 DevProgressEdit�� ����
    	    BeanUtils.copyProperties(devProgress, DevProgressEdit);
    	    
    	    // �������׿� ��ϵ� ���� ÷������ ���� ����
            adminService.deleteAttachmentsByNoticeId(DevProgressEdit.getSeq());

            // ���ο� ���� ���ε� ó��
            fileservice.handleFileUpload(files, "devProgress", DevProgressEdit.getSeq());
            
            log.info("check "+devProgress);

            // ������Ʈ�� ���������� ����
            devservice.updatedevProgress(DevProgressEdit);

            // ���� ���� ����
            response.put("status", "success");
            response.put("message", "���� ���� ��Ȳ�� ���������� �����Ǿ����ϴ�.");
            response.put("DevProgressEdit", DevProgressEdit);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            response.put("status", "failure");
            response.put("message", "���α׷� ���߸�� ���� �߿� ���� �߻�");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("status", "error");
            response.put("message", "���α׷� ���߸�� ���� �߿� ���� �߻�");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
	
	
	//���� ���� ��Ȳ ����
	@DeleteMapping(value= "api/deletedev", produces = "application/json")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deletedev(@RequestBody List<Integer> seqs) {
        Map<String, Object> response = new HashMap<>();
        
        for (int seq : seqs) {
            devservice.deleteDevProgress(seq);
        }
        
        response.put("status", "success");
        response.put("message", "�����ڵ尡 ���������� �����Ǿ����ϴ�.");
        return ResponseEntity.ok(response);
    }
	
	// ��ü ���� ���� ��Ȳ ������ ������ �ٿ�ε�
    @GetMapping("/devdownloadAll")
    public void downloadAlldev(HttpServletResponse response) throws IOException {
        List<devProgress> DevCodeList = devservice.searchDevProgress(null, null, null, null, null, null, null, null, null, null, null, null, null, null, 1, Integer.MAX_VALUE);
        log.info(DevCodeList);
        devexportToExcel(response, DevCodeList, "all_dev_codes.xlsx");
    }
    
    // �׼� ���� ���ø� �ٿ�ε�
    @GetMapping("/devexampleexcel")
    public void downloadExdev(HttpServletResponse response) throws IOException {
    	List<devProgress> ExampleDEV = devservice.searchDevProgress("NO_VALUE", null, null, null, null, null, null, null, null, null, null, null, null, null, 1, Integer.MAX_VALUE);
    	devexportToExcel(response, ExampleDEV, "example.xlsx");
    }
    
    // ��ȸ�� ���� ���� ��Ȳ ������ ������ �ٿ�ε�
    @GetMapping("/devdownloadFiltered")
    public void downloadFiltereddev(
    		@RequestParam(value = "majorCategory", required = false) String majorCategory,
	        @RequestParam(value = "subCategory", required = false) String subCategory,
	        @RequestParam(value = "programType", required = false) String programType,
	        @RequestParam(value = "programName", required = false) String programName,
	        @RequestParam(value = "programId", required = false) String programId,
	        @RequestParam(value = "programStatus", required = false) String programStatus,
	        @RequestParam(value = "developer", required = false) String developer,
	        @RequestParam(value = "devStatus", required = false) String devStatus,
	        @RequestParam(value = "devStartDate", required = false) String devStartDate, //���߿Ϸ��� ������
	        @RequestParam(value = "devEndDate", required = false) String devEndDate, //���߿Ϸ��� �Ϸ���
	        @RequestParam(value = "pl", required = false) String pl,
	        @RequestParam(value = "thirdPartyTestMgr", required = false) String thirdPartyTestMgr,
	        @RequestParam(value = "ItMgr", required = false) String ItMgr,
	        @RequestParam(value = "BusiMgr", required = false) String BusiMgr,
	        @RequestParam(value = "page", defaultValue = "1") int page,
	        @RequestParam(value = "size", defaultValue = "15") int size,
            HttpServletResponse response) throws IOException {
    	
    	List<devProgress> filteredDevCodeList = devservice.searchDevProgress(
	            majorCategory, subCategory, programType, programName, programId, programStatus, developer,
	            devStatus, devStartDate, devEndDate, pl, thirdPartyTestMgr, ItMgr, BusiMgr, page, size
	    );

    	log.info("Ȯ����"+filteredDevCodeList);
        devexportToExcel(response, filteredDevCodeList, "filtered_dev_codes.xlsx");
    }

    // ���� ���Ϸ� �����͸� �������� �޼���
    private void devexportToExcel(HttpServletResponse response, List<devProgress> devProgressList, String fileName) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("DevProgress");
        String check = "";
        log.info("üũ��");
        if (devProgressList.isEmpty()) {
    		check = "no_value";
    		log.info(check);
    		}
        // ��� ����
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("SEQ");
        headerRow.createCell(1).setCellValue("MAJOR_CATEGORY");
        headerRow.createCell(2).setCellValue("SUB_CATEGORY");
        headerRow.createCell(3).setCellValue("MINOR_CATEGORY");
        headerRow.createCell(4).setCellValue("PROGRAM_DETAIL_TYPE");
        headerRow.createCell(5).setCellValue("PROGRAM_TYPE");
        headerRow.createCell(6).setCellValue("PROGRAM_ID");
        headerRow.createCell(7).setCellValue("PROGRAM_NAME");
        headerRow.createCell(8).setCellValue("CLASS_NAME");
        headerRow.createCell(9).setCellValue("SCREEN_ID");
        headerRow.createCell(10).setCellValue("SCREEN_NAME");
        headerRow.createCell(11).setCellValue("SCREEN_MENU_PATH");
        headerRow.createCell(12).setCellValue("PRIORITY");
        headerRow.createCell(13).setCellValue("DIFFICULTY");
        headerRow.createCell(14).setCellValue("ESTIMATED_EFFORT");
        headerRow.createCell(15).setCellValue("program_status");
        headerRow.createCell(16).setCellValue("REQ_ID");
        headerRow.createCell(17).setCellValue("DELETION_HANDLER");
        headerRow.createCell(18).setCellValue("DELETION_DATE");
        headerRow.createCell(19).setCellValue("DELETION_REASON");
        headerRow.createCell(20).setCellValue("DEVELOPER");
        headerRow.createCell(21).setCellValue("PLANNED_START_DATE");
        headerRow.createCell(22).setCellValue("PLANNED_END_DATE");
        headerRow.createCell(23).setCellValue("ACTUAL_START_DATE");
        headerRow.createCell(24).setCellValue("ACTUAL_END_DATE");
        headerRow.createCell(25).setCellValue("PL");
        headerRow.createCell(26).setCellValue("PL_TEST_SCD_DATE");
        headerRow.createCell(27).setCellValue("PL_TEST_CMP_DATE");
        headerRow.createCell(28).setCellValue("PL_TEST_RESULT");
        headerRow.createCell(29).setCellValue("PL_TEST_NOTES");
        headerRow.createCell(30).setCellValue("IT_MGR");
        headerRow.createCell(31).setCellValue("IT_TEST_DATE");
        headerRow.createCell(32).setCellValue("IT_CONFIRM_DATE");
        headerRow.createCell(33).setCellValue("IT_TEST_RESULT");
        headerRow.createCell(34).setCellValue("IT_TEST_NOTES");
        headerRow.createCell(35).setCellValue("BUSI_MGR");
        headerRow.createCell(36).setCellValue("BUSI_TEST_DATE");
        headerRow.createCell(37).setCellValue("BUSI_CONFIRM_DATE");
        headerRow.createCell(38).setCellValue("BUSI_TEST_RESULT");
        headerRow.createCell(39).setCellValue("BUSI_TEST_NOTES");
        headerRow.createCell(40).setCellValue("THIRD_PARTY_TEST_MGR");
        headerRow.createCell(41).setCellValue("THIRD_PARTY_TEST_DATE");
        headerRow.createCell(42).setCellValue("THIRD_PARTY_CONFIRM_DATE");
        headerRow.createCell(43).setCellValue("THIRD_TEST_RESULT");
        headerRow.createCell(44).setCellValue("THIRD_PARTY_TEST_NOTES");
        headerRow.createCell(45).setCellValue("DEV_STATUS");
        if (check != "") {
        	headerRow.createCell(46).setCellValue("INIT_REGISTRAR");
            headerRow.createCell(47).setCellValue("LAST_MODIFIER");
        }
        else {
	        headerRow.createCell(46).setCellValue("INIT_REG_DATE");
	        headerRow.createCell(47).setCellValue("INIT_REGISTRAR");
	        headerRow.createCell(48).setCellValue("LAST_MODIFIED_DATE");
	        headerRow.createCell(49).setCellValue("LAST_MODIFIER");
        }

        // ������ �� ����
        int rowNum = 1;
        for (devProgress devProgress : devProgressList) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(devProgress.getSeq());
            row.createCell(1).setCellValue(devProgress.getMajorCategory());
            row.createCell(2).setCellValue(devProgress.getSubCategory());
            row.createCell(3).setCellValue(devProgress.getMinorCategory());
            row.createCell(4).setCellValue(devProgress.getProgramDetailType());
            row.createCell(5).setCellValue(devProgress.getProgramType());
            row.createCell(6).setCellValue(devProgress.getProgramId());
            row.createCell(7).setCellValue(devProgress.getProgramName());
            row.createCell(8).setCellValue(devProgress.getClassName());
            row.createCell(9).setCellValue(devProgress.getScreenId());
            row.createCell(10).setCellValue(devProgress.getScreenName());
            row.createCell(11).setCellValue(devProgress.getScreenMenuPath());
            row.createCell(12).setCellValue(devProgress.getPriority());
            row.createCell(13).setCellValue(devProgress.getDifficulty());
            row.createCell(14).setCellValue(devProgress.getEstimatedEffort());
            row.createCell(15).setCellValue(devProgress.getProgramStatus());
            row.createCell(16).setCellValue(devProgress.getReqId());
            row.createCell(17).setCellValue(devProgress.getDeletionHandler());
            row.createCell(18).setCellValue(devProgress.getDeletionDate());
            row.createCell(19).setCellValue(devProgress.getDeletionReason());
            row.createCell(20).setCellValue(devProgress.getDeveloper());
            row.createCell(21).setCellValue(devProgress.getPlannedStartDate());
            row.createCell(22).setCellValue(devProgress.getPlannedEndDate());
            row.createCell(23).setCellValue(devProgress.getActualStartDate());
            row.createCell(24).setCellValue(devProgress.getActualEndDate());
            row.createCell(25).setCellValue(devProgress.getPl());
            row.createCell(26).setCellValue(devProgress.getPlTestScdDate());
            row.createCell(27).setCellValue(devProgress.getPlTestCmpDate());
            row.createCell(28).setCellValue(devProgress.getPlTestResult());
            row.createCell(29).setCellValue(devProgress.getPlTestNotes());
            row.createCell(30).setCellValue(devProgress.getItMgr());
            row.createCell(31).setCellValue(devProgress.getItTestDate());
            row.createCell(32).setCellValue(devProgress.getItConfirmDate());
            row.createCell(33).setCellValue(devProgress.getItTestResult());
            row.createCell(34).setCellValue(devProgress.getItTestNotes());
            row.createCell(35).setCellValue(devProgress.getBusiMgr());
            row.createCell(36).setCellValue(devProgress.getBusiTestDate());
            row.createCell(37).setCellValue(devProgress.getBusiConfirmDate());
            row.createCell(38).setCellValue(devProgress.getBusiTestResult());
            row.createCell(39).setCellValue(devProgress.getBusiTestNotes());
            row.createCell(40).setCellValue(devProgress.getThirdPartyTestMgr());
            row.createCell(41).setCellValue(devProgress.getThirdPartyTestDate());
            row.createCell(42).setCellValue(devProgress.getThirdPartyConfirmDate());
            row.createCell(43).setCellValue(devProgress.getThirdTestResult());
            row.createCell(44).setCellValue(devProgress.getThirdPartyTestNotes());
            row.createCell(45).setCellValue(devProgress.getDevStatus());
            if (check != "") {
	            row.createCell(46).setCellValue(devProgress.getInitRegistrar());
	            row.createCell(47).setCellValue(devProgress.getLastModifier());
            }
            else {
            	row.createCell(46).setCellValue(devProgress.getInitRegDate().toString());
	            row.createCell(47).setCellValue(devProgress.getInitRegistrar());
	            row.createCell(48).setCellValue(devProgress.getLastModifiedDate().toString());
	            row.createCell(49).setCellValue(devProgress.getLastModifier());
            }
        }
        // ���� ������ HTTP �������� ��������
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
        try (OutputStream os = response.getOutputStream()) {
            workbook.write(os);
        }
        workbook.close();
    }
  
    @PostMapping(value = "api/devupload", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> catuploadExcelFile(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
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
            List<String> expectedHeaders = Arrays.asList("SEQ", "MAJOR_CATEGORY", "SUB_CATEGORY", "MINOR_CATEGORY", 
            		"PROGRAM_DETAIL_TYPE", "PROGRAM_TYPE", "PROGRAM_ID", "PROGRAM_NAME", "CLASS_NAME", "SCREEN_ID", 
            		"SCREEN_NAME", "SCREEN_MENU_PATH", "PRIORITY", "DIFFICULTY", "ESTIMATED_EFFORT", "PROGRAM_STATUS", 
            		"REQ_ID", "DELETION_HANDLER", "DELETION_DATE", "DELETION_REASON", "DEVELOPER", "PLANNED_START_DATE", 
            		"PLANNED_END_DATE", "ACTUAL_START_DATE", "ACTUAL_END_DATE", "PL", "PL_TEST_SCD_DATE", 
            		"PL_TEST_CMP_DATE", "PL_TEST_RESULT", "PL_TEST_NOTES", "IT_MGR", "IT_TEST_DATE", "IT_CONFIRM_DATE", 
            		"IT_TEST_RESULT", "IT_TEST_NOTES", "BUSI_MGR", "BUSI_TEST_DATE", "BUSI_CONFIRM_DATE", "BUSI_TEST_RESULT", 
            		"BUSI_TEST_NOTES", "THIRD_PARTY_TEST_MGR", "THIRD_PARTY_TEST_DATE", "THIRD_PARTY_CONFIRM_DATE", 
            		"THIRD_TEST_RESULT", "THIRD_PARTY_TEST_NOTES", "DEV_STATUS", "INIT_REGISTRAR", "LAST_MODIFIER");
            if (!isHeaderValid4(headerRow, expectedHeaders)) {
                response.put("status", "error");
                response.put("message", "����� �÷����� �ùٸ��� �ʽ��ϴ�.");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            List<devProgress> devProgress = new ArrayList<>();

            // ù ��° ���� ����̹Ƿ� �ǳʶݴϴ�.
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    devProgress devprogress = new devProgress();

                    try {
                    	devprogress.setSeq((int) getCellValueAsNumeric(row.getCell(0)));
                        devprogress.setMajorCategory(getCellValueAsString(row.getCell(1)));
                        devprogress.setSubCategory(getCellValueAsString(row.getCell(2)));
                        devprogress.setMinorCategory(getCellValueAsString(row.getCell(3)));
                        devprogress.setProgramDetailType(getCellValueAsString(row.getCell(4)));
                        devprogress.setProgramType(getCellValueAsString(row.getCell(5)));
                        devprogress.setProgramId(getCellValueAsString(row.getCell(6)));
                        devprogress.setProgramName(getCellValueAsString(row.getCell(7)));
                        devprogress.setClassName(getCellValueAsString(row.getCell(8)));
                        devprogress.setScreenId(getCellValueAsString(row.getCell(9)));
                        devprogress.setScreenName(getCellValueAsString(row.getCell(10)));
                        devprogress.setScreenMenuPath(getCellValueAsString(row.getCell(11)));
                        devprogress.setPriority(getCellValueAsString(row.getCell(12)));
                        devprogress.setDifficulty(getCellValueAsString(row.getCell(13)));
                        devprogress.setEstimatedEffort((int) getCellValueAsNumeric(row.getCell(14)));
                        devprogress.setProgramStatus(getCellValueAsString(row.getCell(15)));
                        devprogress.setReqId(getCellValueAsString(row.getCell(16)));
                        devprogress.setDeletionHandler(getCellValueAsString(row.getCell(17)));
                        devprogress.setDeletionDate(getCellValueAsDate(row.getCell(18)));
                        devprogress.setDeletionReason(getCellValueAsString(row.getCell(19)));
                        devprogress.setDeveloper(getCellValueAsString(row.getCell(20)));
                        devprogress.setPlannedStartDate(getCellValueAsDate(row.getCell(21)));
                        devprogress.setPlannedEndDate(getCellValueAsDate(row.getCell(22)));
                        devprogress.setActualStartDate(getCellValueAsDate(row.getCell(23)));
                        devprogress.setActualEndDate(getCellValueAsDate(row.getCell(24)));
                        devprogress.setPl(getCellValueAsString(row.getCell(25)));
                        devprogress.setPlTestScdDate(getCellValueAsDate(row.getCell(26)));
                        devprogress.setPlTestCmpDate(getCellValueAsDate(row.getCell(27)));
                        devprogress.setPlTestResult(getCellValueAsString(row.getCell(28)));
                        devprogress.setPlTestNotes(getCellValueAsString(row.getCell(29)));
                        devprogress.setItMgr(getCellValueAsString(row.getCell(30)));
                        devprogress.setItTestDate(getCellValueAsDate(row.getCell(31)));
                        devprogress.setItConfirmDate(getCellValueAsDate(row.getCell(32)));
                        devprogress.setItTestResult(getCellValueAsString(row.getCell(33)));
                        devprogress.setItTestNotes(getCellValueAsString(row.getCell(34)));
                        devprogress.setBusiMgr(getCellValueAsString(row.getCell(35)));
                        devprogress.setBusiTestDate(getCellValueAsDate(row.getCell(36)));
                        devprogress.setBusiConfirmDate(getCellValueAsDate(row.getCell(37)));
                        devprogress.setBusiTestResult(getCellValueAsString(row.getCell(38)));
                        devprogress.setBusiTestNotes(getCellValueAsString(row.getCell(39)));
                        devprogress.setThirdPartyTestMgr(getCellValueAsString(row.getCell(40)));
                        devprogress.setThirdPartyTestDate(getCellValueAsDate(row.getCell(41)));
                        devprogress.setThirdPartyConfirmDate(getCellValueAsDate(row.getCell(42)));
                        devprogress.setThirdTestResult(getCellValueAsString(row.getCell(43)));
                        devprogress.setThirdPartyTestNotes(getCellValueAsString(row.getCell(44)));
                        devprogress.setDevStatus(getCellValueAsString(row.getCell(45)));
                        devprogress.setInitRegistrar(getCellValueAsString(row.getCell(46)));
                        devprogress.setLastModifier(getCellValueAsString(row.getCell(47)));
                        devProgress.add(devprogress);
                        log.info(devProgress);
                    } catch (Exception e) {
                        e.printStackTrace();
                        response.put("status", "error");
                        response.put("message", "������ ������ ������ �ùٸ��� �ʽ��ϴ�: �� " + (i + 1));
                        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                    }
                }
            }

            // �����ͺ��̽��� ����
            devservice.saveAllDevProgress(devProgress);
            response.put("status", "success");
            response.put("message", "���� ���ε尡 ���������� �Ϸ�Ǿ����ϴ�!");
            response.put("totalUploaded", devProgress.size());

        } catch (DuplicateKeyException e) {
            e.printStackTrace();
            response.put("status", "error");
            response.put("message", "�ߺ��� ���� ��Ȳ ������ �߰ߵǾ����ϴ�.");
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        } catch (IOException e) {
            e.printStackTrace();
            response.put("status", "error");
            response.put("message", "���� ó�� �� ������ �߻��߽��ϴ�. �ٽ� �õ��� �ּ���.");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    
    
    private boolean isHeaderValid4(Row headerRow, List<String> expectedHeaders) {
        for (int i = 0; i < expectedHeaders.size(); i++) {
            Cell cell = headerRow.getCell(i);
            if (cell == null || !cell.getStringCellValue().trim().equalsIgnoreCase(expectedHeaders.get(i))) {
                return false;
            }
        }
        return true;
    }
    
    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return null;
        }
        return cell.getCellType() == CellType.STRING ? cell.getStringCellValue() : cell.toString();
    }

    private double getCellValueAsNumeric(Cell cell) {
        if (cell == null) {
            return 0;
        }
        return cell.getCellType() == CellType.NUMERIC ? cell.getNumericCellValue() : Double.parseDouble(cell.toString());
    }
    
    private Date getCellValueAsDate(Cell cell) {
        if (cell == null) {
            return null;
        }

        if (cell.getCellType() == CellType.NUMERIC) {
            // ��¥�� ���� ���·� ����Ǿ� ���� ��, ��¥�� ��ȯ
            if (DateUtil.isCellDateFormatted(cell)) {
                return cell.getDateCellValue();
            } else {
                return null;
            }
        } else if (cell.getCellType() == CellType.STRING) {
            // ���ڿ��� �Ǿ� �ִ� ���, "YYYY-MM-DD" ���� ���� ó��
            String dateStr = cell.getStringCellValue();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            try {
				return dateFormat.parse(dateStr);
			} catch (ParseException e) {
				e.printStackTrace();
			}
        }

        return null; // �ٸ� ������ ���� null ��ȯ
    }
    
    
        
    
    
   
    
    



}

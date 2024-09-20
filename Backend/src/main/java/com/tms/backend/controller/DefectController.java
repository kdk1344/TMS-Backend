package com.tms.backend.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.tms.backend.service.AdminService;
import com.tms.backend.service.DefectService;
import com.tms.backend.service.DevService;
import com.tms.backend.service.FileService;
import com.tms.backend.vo.CommonCode;
import com.tms.backend.vo.Defect;
import com.tms.backend.vo.FileAttachment;
import com.tms.backend.vo.User;
import com.tms.backend.vo.devProgress;

import lombok.extern.log4j.Log4j;

@Controller
@Log4j
@RequestMapping("/tms/*")
public class DefectController {
	
	@Autowired
	private DefectService defectService;
	
	@Autowired
	private AdminService adminService;
	
	@Autowired
	private FileService fileservice;
	
	
	@GetMapping("/defect")
	public String defectStatusPage() {
	    

	    return "defect"; // defect.jsp�� �̵�
	}
	
	
	@GetMapping(value="api/defect" , produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<Map<String, Object>> APIdefectPage(HttpServletRequest request,
			@RequestParam(value = "testStage", required = false) String testStage,
            @RequestParam(value = "majorCategory", required = false) String majorCategory,
            @RequestParam(value = "subCategory", required = false) String subCategory,
            @RequestParam(value = "defectSeverity", required = false) String defectSeverity,
            @RequestParam(value = "seq", required = false) Integer seq,
            @RequestParam(value = "defectRegistrar", required = false) String defectRegistrar,
            @RequestParam(value = "defectHandler", required = false) String defectHandler,
            @RequestParam(value = "Pl", required = false) String pl,
            @RequestParam(value = "defectStatus", required = false) String defectStatus,
            @RequestParam(value = "programId", required = false) String programId,
            @RequestParam(value = "testId", required = false) String testId,
            @RequestParam(value = "programName", required = false) String programName,
            @RequestParam(value = "programType", required = false) String programType,
            @RequestParam(value = "page") int page,
            @RequestParam(value = "size") int size) {
		
//		HttpSession session = request.getSession(false); // ������ ���ٸ� ���� ������ ����
//		if (session == null || session.getAttribute("authorityCode") == null) {
//			// ������ ���ų� authorityCode�� ������ 401 Unauthorized ��ȯ
//			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("message", "������ �����ϴ�. �α����ϼ���."));
//			}
		Map<String, Object> response = new HashMap<>();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		
		log.info("�׽�Ʈ ��������"+testStage);

		majorCategory = adminService.getStageCodes("��", majorCategory);
		subCategory = adminService.getStageCodes("��", subCategory);
		if (defectSeverity != null && !defectSeverity.isEmpty()) {
			defectSeverity = adminService.getStageCCodes("14", defectSeverity);}
		if (defectStatus != null && !defectStatus.isEmpty()) {
			defectStatus = adminService.getStageCCodes("15", defectStatus);}
		if (testStage != null && !testStage.isEmpty()) {
			testStage = adminService.getStageCCodes("11", testStage);}
						
		// ���� ��� ��ȸ
		List<Defect> defects = defectService.searchDefects(testStage, majorCategory, subCategory, defectSeverity, seq, defectRegistrar, 
				defectHandler, pl,  defectStatus, programId, testId, programName, programType, page, size);
	    
	    // �� ���� �� ��ȸ
	    int totalDefects = defectService.getTotalDefectsCount(testStage, majorCategory, subCategory, defectSeverity, seq, defectRegistrar, 
	    		defectHandler, pl, defectStatus, programId, testId, programName, programType);
	    
	    // �� ������ �� ���
	    int totalPages = (int) Math.ceil((double) totalDefects / size);
	    	    
	    // ���� ����
	    response.put("defects", defects);
	    response.put("currentPage", page);
	    response.put("totalPages", totalPages);
	    response.put("totalDefects", totalDefects);

	    return ResponseEntity.ok(response); // JSON���� ���� ��ȯ

	}
	
	//���� ���� Ȯ��
	@GetMapping(value = "api/defectType", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getDefectType() {
    	Map<String, Object> response = new HashMap<>();
    	List<CommonCode> defectType= adminService.getCCCode("13");

        // ���� ������ ����
        if (defectType != null && !defectType.isEmpty()) {
            response.put("status", "success");
            response.put("defectType", defectType);
        } else {
            response.put("status", "failure");
            response.put("message", "���� ���� ������ ã�� �� �����ϴ�");
        }

        // ��ȸ�� ����� ��ȯ
        return ResponseEntity.ok(response);
    }
		
	//���� �ɰ��� Ȯ��
	@GetMapping(value = "api/defectSeverity", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getdefectSeverity() {
    	Map<String, Object> response = new HashMap<>();
    	List<CommonCode> defectSeverity= adminService.getCCCode("14");

        // ���� ������ ����
        if (defectSeverity != null && !defectSeverity.isEmpty()) {
            response.put("status", "success");
            response.put("defectSeverity", defectSeverity);
        } else {
            response.put("status", "failure");
            response.put("message", "���� �ɰ��� ������ ã�� �� �����ϴ�");
        }

        // ��ȸ�� ����� ��ȯ
        return ResponseEntity.ok(response);
    }
	
	//�׽�Ʈ �ܰ� ���� Ȯ��
	@GetMapping(value = "api/testStage", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> gettestStage() {
    	Map<String, Object> response = new HashMap<>();
    	List<CommonCode> testStage= adminService.getCCCode("11");

        // ���� ������ ����
        if (testStage != null && !testStage.isEmpty()) {
            response.put("status", "success");
            response.put("testStage", testStage);
        } else {
            response.put("status", "failure");
            response.put("message", "�׽�Ʈ �ܰ� ������ ã�� �� �����ϴ�");
        }

        // ��ȸ�� ����� ��ȯ
        return ResponseEntity.ok(response);
    }
		
	//���� ó�� ���� Ȯ��
	@GetMapping(value = "api/defectStatus", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getdefectStatus() {
    	Map<String, Object> response = new HashMap<>();
    	List<CommonCode> defectStatus= adminService.getCCCode("15");

        // ���� ������ ����
        if (defectStatus != null && !defectStatus.isEmpty()) {
            response.put("status", "success");
            response.put("defectStatus", defectStatus);
        } else {
            response.put("status", "failure");
            response.put("message", "���� ó�� ���� ������ ã�� �� �����ϴ�");
        }

        // ��ȸ�� ����� ��ȯ
        return ResponseEntity.ok(response);
    }
	
//	//���� ��ȣ ��ȸ
//	@GetMapping(value = "api/defectNumberList", produces = MediaType.APPLICATION_JSON_VALUE)
//    @ResponseBody
//    public ResponseEntity<Map<String, Object>> getdefectNumberList(@RequestParam(value = "testStage", required = false) String testStage,
//            @RequestParam(value = "testId", required = false) String testId,
//            @RequestParam(value = "programName", required = false) String programName,
//            @RequestParam(value = "programType", required = false) String programType) {
//    	Map<String, Object> response = new HashMap<>();
//    	List<Defect> defectNumberList= defectService.getdefectNumberList(testStage, testId, programName, programType);
//
//        // ���� ������ ����
//        if (defectNumberList != null && !defectNumberList.isEmpty()) {
//            response.put("status", "success");
//            response.put("defectNumberList", defectNumberList);
//        } else {
//            response.put("status", "failure");
//            response.put("message", "��߻� ���� ��ȣ ������ ã�� �� �����ϴ�");
//        }
//
//        // ��ȸ�� ����� ��ȯ
//        return ResponseEntity.ok(response);
//    }
	
	//���� ��� ������
	@GetMapping("/defectReg")
	public String defectRegPage() {
		return "defectReg";
	}
	
	//���� ���� ������
	@GetMapping("/defectEdit")
	public String defectEditPage() {
	    return "defectEdit"; // JSP �������� �̵�
	}
		
	//���� ���� ��Ȳ ����
    @PostMapping(value = "api/defectReg", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> defectReg(HttpServletRequest request,
			@RequestPart("defect") Defect defect,
			@RequestPart(value = "file", required = false) MultipartFile[] files,
			@RequestPart(value = "fixfile", required = false) MultipartFile[] fixfiles) {
		HttpSession session = request.getSession(false); // ������ ���ٸ� ���� ������ ����
		String UserID = (String) session.getAttribute("id");
		String UserName = (String) session.getAttribute("name");
		Map<String, Object> response = new HashMap<>();
		
		//�ڵ�� ������ �����͸� �ڵ������ ����
		defect.setMajorCategory(adminService.getStageCodes("��", defect.getMajorCategory()));
		defect.setSubCategory(adminService.getStageCodes("��", defect.getSubCategory()));
		defect.setDefectType(adminService.getStageCCodes("13", defect.getDefectType()));
		defect.setDefectSeverity(adminService.getStageCCodes("14", defect.getDefectSeverity()));
		defect.setDefectStatus(adminService.getStageCCodes("15", defect.getDefectStatus()));
		
		try {
			// �׽�Ʈ ID ������� ��� ���α׷� ID �Է�
			if(defect.getTestId() == null || defect.getTestId().trim().isEmpty()) {
				defect.setTestId(defect.getProgramId());
			}
			//�׽�Ʈ ���Ե����, ���� ���� ����� �α��� ID ����
			defect.setDefectRegistrar(UserName);
			defect.setInitCreater(UserName);
			defect.setLastModifier(UserName);
			// ��ġ�Ϸ���, PL Ȯ���Ͽ� ���� ���� ó�� ���� �ڵ� ����
			if(defect.getDefectCompletionDate() == null && defect.getPlConfirmDate() == null) {
				defect.setDefectStatus("��ϿϷ�");}
			if(defect.getDefectCompletionDate() != null && defect.getPlConfirmDate() == null) {
				defect.setDefectStatus("��ġ�Ϸ�");}
			if(defect.getDefectCompletionDate() != null && defect.getPlConfirmDate() != null && defect.getDefectRegConfirmDate() == null) {
				defect.setDefectStatus("PL Ȯ�οϷ�");}
			if(defect.getDefectCompletionDate() != null && defect.getPlConfirmDate() != null && defect.getDefectRegConfirmDate() != null) {
				defect.setDefectStatus("����� Ȯ�οϷ�");}
			
			// ���ۿ������� ���Ό���Ϻ��� ���� ��� ����
			if (defect.getDefectScheduledDate() != null && defect.getDefectCompletionDate() != null) {
			    if (defect.getDefectScheduledDate().after(defect.getDefectCompletionDate())) {
			    	log.info("Error!!!!!");
			        throw new IllegalArgumentException("���� ��ġ �������� �������� �����Ϻ��� �ռ��� �մϴ�.");
			    }
			}
			
        	// �ʼ� �׸� üũ
			validateRequiredField(defect.getMajorCategory(), "���� ��з�");
			validateRequiredField(defect.getSubCategory(), "���� �ߺз�");
			validateRequiredField(defect.getTestStage(), "�׽�Ʈ �ܰ�");
			validateRequiredField(defect.getTestId(), "�׽�ƮID");
			validateRequiredField(defect.getDefectType(), "��������");
			validateRequiredField(defect.getDefectSeverity(), "���Խɰ���");
			validateRequiredField(defect.getDefectDescription(), "���� ����");
			validateRequiredField(defect.getProgramId(), "���α׷�ID");
			validateRequiredField(defect.getDefectHandler(), "��ġ�����");
			validateRequiredField(defect.getPl(), "PL");
			if(defect.getDefectDiscoveryDate() == null) {
				throw new IllegalArgumentException("���Թ߻����� �ʼ� �Է� �׸��Դϴ�.");
			}
			//���� ���� ���
        	defectService.insertdefect(defect);
        	log.info("1÷��"+files.length);
        	log.info("2÷��"+fixfiles.length);
        	// ���ο� ���� ���ε� ó��
            if (files != null && files.length > 0) {
            	log.info("÷����");
                fileservice.handleFileUpload(files, "devfect", defect.getSeq());
            }
            if (fixfiles != null && fixfiles.length > 0) {
            	log.info("÷����");
                fileservice.handleFileUpload(fixfiles, "devfectFix", defect.getSeq());
            }
            List<FileAttachment> attachments = adminService.getAttachments(defect.getSeq(),31);
            defect.setDefectAttachment(attachments);
            List<FileAttachment> fixattachments = adminService.getAttachments(defect.getSeq(),32);
            defect.setDefectFixAttachments(fixattachments);
            
            log.info(defect);
        	
            response.put("status", "success");
            response.put("message", "���� ������ ��ϵǾ����ϴ�");
            response.put("defect", defect);  // ��ϵ� ���� ��Ȳ ���� ���� ��ȯ
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            response.put("status", "failure");
            response.put("message", e.getMessage());  // ���� �޽����� response�� ���Խ�Ŵ
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("status", "error");
            response.put("message", "���� ���� ��� �߿� ������ �߻��߽��ϴ�.");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
	}
		
    //���� ��Ȳ ���� ������
  	@GetMapping(value="api/defectDetail" , produces = MediaType.APPLICATION_JSON_VALUE)
  	@ResponseBody
  	public ResponseEntity<Map<String, Object>> defectEditPage2(HttpServletRequest request,
  			@RequestParam("seq") Integer seq) {
  		HttpSession session = request.getSession(false); // ������ ���ٸ� ���� ������ ����
  		if (session == null || session.getAttribute("authorityCode") == null) {
  			// ������ ���ų� authorityCode�� ������ 401 Unauthorized ��ȯ
  			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("message", "������ �����ϴ�. �α����ϼ���."));
  			}
//  		Integer authorityCode = (Integer) session.getAttribute("authorityCode");
  		
  		Map<String, Object> response = new HashMap<>();
  		
  		Defect defectDetail = defectService.getDefectById(seq);
  		
  		List<FileAttachment> attachments = adminService.getAttachments(seq, 31);
  		defectDetail.setDefectAttachment(attachments);
  		List<FileAttachment> fixAttachments = adminService.getAttachments(seq, 32);
  		defectDetail.setDefectFixAttachments(fixAttachments);

  	    // ���� ����
  		response.put("status", "success");
        response.put("message", "���� ���� ��Ȳ ���� ����.");
  	    response.put("defectDetail", defectDetail);
  	    response.put("attachments", attachments);
  	    response.put("fixAttachments", fixAttachments);
  	    
  	    return ResponseEntity.ok(response); // JSON���� ���� ��ȯ
  	}	
  	
  	//���� ���� ��Ȳ ����
    @PostMapping(value = "api/defectEdit", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> defectEdit(HttpServletRequest request,
			@RequestPart("defect") Defect defect,
			@RequestPart(value = "file", required = false) MultipartFile[] files,
			@RequestPart(value = "fixfile", required = false) MultipartFile[] fixfiles) {
		HttpSession session = request.getSession(false); // ������ ���ٸ� ���� ������ ����
//		if (session == null || session.getAttribute("authorityCode") == null) {
//			// ������ ���ų� authorityCode�� ������ 401 Unauthorized ��ȯ
//			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("message", "������ �����ϴ�. �α����ϼ���."));
//			}
		String UserID = (String) session.getAttribute("id");
		String UserName = (String) session.getAttribute("name");
		Map<String, Object> response = new HashMap<>();
		
		//�ڵ�� ������ �����͸� �ڵ������ ����
		defect.setMajorCategory(adminService.getStageCodes("��", defect.getMajorCategory()));
		defect.setSubCategory(adminService.getStageCodes("��", defect.getSubCategory()));
		defect.setDefectType(adminService.getStageCCodes("13", defect.getDefectType()));
		defect.setDefectSeverity(adminService.getStageCCodes("14", defect.getDefectSeverity()));
		defect.setDefectStatus(adminService.getStageCCodes("15", defect.getDefectStatus()));
				
		try {
			// �׽�Ʈ ID ������� ��� ���α׷� ID �Է�
			if(defect.getTestId() == null || defect.getTestId().trim().isEmpty()) {
				defect.setTestId(defect.getProgramId());
			}
			//���� ���� ����� �α��� ID ����
			defect.setDefectRegistrar(UserName);
			// ��ġ�Ϸ���, PL Ȯ���Ͽ� ���� ���� ó�� ���� �ڵ� ����
			if(defect.getDefectCompletionDate() == null && defect.getPlConfirmDate() == null) {
				defect.setDefectStatus("��ϿϷ�");}
			if(defect.getDefectCompletionDate() != null && defect.getPlConfirmDate() == null) {
				defect.setDefectStatus("��ġ�Ϸ�");}
			if(defect.getDefectCompletionDate() != null && defect.getPlConfirmDate() != null && defect.getDefectRegConfirmDate() == null) {
				defect.setDefectStatus("PL Ȯ�οϷ�");}
			if(defect.getDefectCompletionDate() != null && defect.getPlConfirmDate() != null && defect.getDefectRegConfirmDate() != null) {
				defect.setDefectStatus("����� Ȯ�οϷ�");}
			
			// ���ۿ������� ���Ό���Ϻ��� ���� ��� ����
			if (defect.getDefectScheduledDate() != null && defect.getDefectCompletionDate() != null) {
			    if (defect.getDefectScheduledDate().after(defect.getDefectCompletionDate())) {
			    	log.info("error!!!!");
			        throw new IllegalArgumentException("���� ��ġ �������� �������� �����Ϻ��� �ռ��� �մϴ�.");
			    }
			}
			
			//��ϵ� ���� ���� ��������
			Defect DefectEdit = defectService.getDefectById(defect.getSeq());
			// devProgress�� �ʵ带 DevProgressEdit�� ����
		    BeanUtils.copyProperties(defect, DefectEdit);
		    //���������� ����
			DefectEdit.setLastModifier(UserName);
		    // �������׿� ��ϵ� ���� ÷������ ���� ����
	        adminService.deleteAttachmentsByNoticeId(defect.getSeq(),31);
	        adminService.deleteAttachmentsByNoticeId(defect.getSeq(),32);
	        // ������Ʈ�� ���������� ����
	        defectService.updateDefect(DefectEdit);
	        
	        validateRequiredField(defect.getMajorCategory(), "���� ��з�");
			validateRequiredField(defect.getSubCategory(), "���� �ߺз�");
			validateRequiredField(defect.getTestStage(), "�׽�Ʈ �ܰ�");
			validateRequiredField(defect.getTestId(), "�׽�ƮID");
			validateRequiredField(defect.getDefectType(), "��������");
			validateRequiredField(defect.getDefectSeverity(), "���Խɰ���");
			validateRequiredField(defect.getDefectDescription(), "���� ����");
			validateRequiredField(defect.getProgramId(), "���α׷�ID");
			validateRequiredField(defect.getDefectHandler(), "��ġ�����");
			validateRequiredField(defect.getPl(), "PL");
			if(defect.getDefectDiscoveryDate() == null) {
				throw new IllegalArgumentException("���Թ߻����� �ʼ� �Է� �׸��Դϴ�.");
			}
        	
        	// ���ο� ���� ���ε� ó��
            if (files != null && files.length > 0) {
            	log.info("÷����");
            	fileservice.handleFileUpload(files, "defect", defect.getSeq());
            }
            if (fixfiles != null && fixfiles.length > 0) {
            	log.info("÷����");
            	fileservice.handleFileUpload(files, "defectFix", defect.getSeq());
            }
            List<FileAttachment> attachments = adminService.getAttachments(defect.getSeq(),31);
            defect.setDefectAttachment(attachments);
            List<FileAttachment> fixAttachments = adminService.getAttachments(defect.getSeq(),32);
            defect.setDefectFixAttachments(fixAttachments);
	
	        // ���� ���� ����
	        response.put("status", "success");
	        response.put("message", "���� ��Ȳ�� ���������� �����Ǿ����ϴ�.");
	        response.put("DefectEdit", DefectEdit);
	        return new ResponseEntity<>(response, HttpStatus.OK);
	    } catch (IllegalArgumentException e) {
	        response.put("status", "failure");
	        response.put("message", e.getMessage());
	        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	    } catch (Exception e) {
	        e.printStackTrace();
	        response.put("status", "error");
	        response.put("message", "���� ��Ȳ ���� �߿� ���� �߻��߽��ϴ�");
	        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}
		
	//���� ��Ȳ ����
	@DeleteMapping(value= "api/deleteDefect", produces = "application/json")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteDefect(@RequestBody List<Integer> seqs) {
        Map<String, Object> response = new HashMap<>();
        
        for (Integer seq : seqs) {
            defectService.deleteDefect(seq,31);
            defectService.deleteDefect(seq,32);
        }
        
        response.put("status", "success");
        response.put("message", "������ ���������� �����Ǿ����ϴ�.");
        return ResponseEntity.ok(response);
    }
	
	// ��ü ���� ���� ��Ȳ ������ ������ �ٿ�ε�
    @GetMapping("/defectdownloadAll")
    public void downloadAlldefect(HttpServletResponse response) throws IOException {
    	List<Defect> defects = defectService.searchAllDefects();
        log.info(defects);
        defectexportToExcel(response, defects, "all_defects_codes.xlsx");
    }
    
    // �׼� ���� ���ø� �ٿ�ε�
    @GetMapping("/defectsexampleexcel")
    public void downloadExdefects(HttpServletResponse response) throws IOException {
    	List<Defect> defects = defectService.searchDefects("no_value", null, null, null, 999999, null, null, null, null, null,null, null, null, 1, 15);  	
    	defectexportToExcel(response, defects, "example.xlsx");
    }
    
    // ��ȸ�� ���� ������ ������ �ٿ�ε�
    @GetMapping("/defectdownloadFiltered")
    public void downloadFiltereddefects(
    		@RequestParam(value = "testStage", required = false) String testStage,
            @RequestParam(value = "majorCategory", required = false) String majorCategory,
            @RequestParam(value = "subCategory", required = false) String subCategory,
            @RequestParam(value = "defectSeverity", required = false) String defectSeverity,
            @RequestParam(value = "seq", required = false) Integer seq,
            @RequestParam(value = "defectRegistrar", required = false) String defectRegistrar,
            @RequestParam(value = "defectHandler", required = false) String defectHandler,
            @RequestParam(value = "Pl", required = false) String pl,
            @RequestParam(value = "defectStatus", required = false) String defectStatus,
            @RequestParam(value = "programId", required = false) String programId,
            @RequestParam(value = "testId", required = false) String testId,
            @RequestParam(value = "programName", required = false) String programName,
            @RequestParam(value = "programType", required = false) String programType,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            HttpServletResponse response) throws IOException {
    	
    	majorCategory = adminService.getStageCodes("��", majorCategory);
		subCategory = adminService.getStageCodes("��", subCategory);
		if (defectSeverity != null && !defectSeverity.isEmpty()) {
			defectSeverity = adminService.getStageCCodes("14", defectSeverity);}
		if (defectStatus != null && !defectStatus.isEmpty()) {
			defectStatus = adminService.getStageCCodes("15", defectStatus);}
		if (testStage != null && !testStage.isEmpty()) {
			testStage = adminService.getStageCCodes("11", testStage);}
		
		// ���� ��� ��ȸ
	    List<Defect> defects = defectService.searchDefects(testStage, majorCategory, subCategory, defectSeverity, seq, defectRegistrar, 
				defectHandler, pl,  defectStatus, programId, testId, programName, programType, page, size);


    	log.info("Ȯ����"+defects);
    	defectexportToExcel(response, defects, "filtered_defects_codes.xlsx");
    }
    
    // ���� ���Ϸ� �����͸� �������� �޼���
    private void defectexportToExcel(HttpServletResponse response, List<Defect> Defects, String fileName) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Defect");
        String check = "";
        log.info("üũ��");
        if (Defects.isEmpty()) {
    		check = "no_value";
    		log.info(check);
    		}
        // ��� �̸� �迭
        String[] headers = {
        	    "SEQ", "TEST_STAGE", "MAJOR_CATEGORY", "SUB_CATEGORY", "TEST_ID", 
        	    "PROGRAM_TYPE", "PROGRAM_ID", "PROGRAM_NAME", "DEFECT_TYPE", 
        	    "DEFECT_SEVERITY", "DEFECT_DESCRIPTION", "DEFECT_REGISTRAR", "DEFECT_DISCOVERY_DATE", 
        	    "DEFECT_HANDLER", "DEFECT_SCHEDULED_DATE", "DEFECT_COMPLETION_DATE", 
        	    "DEFECT_RESOLUTION_DETAILS", "PL", "PL_CONFIRM_DATE", "ORIGINAL_DEFECT_NUMBER", 
        	    "PL_DEFECT_JUDGE_CLASS", "PL_COMMENTS", "DEFECT_REG_CONFIRM_DATE", 
        	    "DEFECT_REGISTRAR_COMMENT", "DEFECT_STATUS", "INIT_CREATED_DATE", 
        	    "INIT_CREATER", "LAST_MODIFIED_DATE", "LAST_MODIFIER"
        	};

        // ��� ����
        Row headerRow = sheet.createRow(0);
        if (!check.equals("")) {
        	// "SEQ"�� �����ϰ� ���� ������� ����
        	log.info("check"+check);
            for (int i = 1; i < headers.length-4; i++) {
                headerRow.createCell(i - 1).setCellValue(headers[i]);
            }
            headerRow.createCell(24).setCellValue("INIT_CREATER");
            headerRow.createCell(25).setCellValue("LAST_MODIFIER");
        }
        else {
        	// "SEQ"�� �����Ͽ� ��� ��� ����
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }
	        headerRow.createCell(25).setCellValue("INIT_CREATED_DATE");
	        headerRow.createCell(26).setCellValue("INIT_CREATER");
	        headerRow.createCell(27).setCellValue("LAST_MODIFIED_DATE");
	        headerRow.createCell(28).setCellValue("LAST_MODIFIER");
        }
        
        // ����� ������ ����
        String[] fields = {
        	    "getseq", "getTestStage", "getMajorCategory", "getSubCategory", 
        	    "getTestId", "getProgramType", "getProgramId", "getProgramName", 
        	    "getDefectType", "getDefectSeverity", "getDefectDescription", 
        	    "getDefectRegistrar", "getdefectDiscoveryDate", "getDefectHandler", 
        	    "getDefectScheduledDate", "getDefectCompletionDate", 
        	    "getDefectResolutionDetails", "getPl", "getPlConfirmDate", 
        	    "getOriginalDefectNumber", "getPlDefectJudgeClass", "getPlComments", 
        	    "getDefectRegConfirmDate", "getDefectRegistrarComment", 
        	    "getDefectStatus", "getInitCreatedDate", "getInitCreater", 
        	    "getLastModifiedDate", "getLastModifier"
        	};

        // ������ �� ����
        int rowNum = 1;
        for (Defect Defect : Defects) {
            Row row = sheet.createRow(rowNum++);
            for (int i = 0; i < fields.length; i++) {
                try {
                    Method method = Defect.getClass().getMethod(fields[i]);
                    Object value = method.invoke(Defect);
                    row.createCell(i).setCellValue(value != null ? value.toString() : "");
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
		
    
    @PostMapping(value = "api/defectupload", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> defectuploadExcelFile(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
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
            List<String> expectedHeaders = Arrays.asList("TEST_STAGE", "MAJOR_CATEGORY", "SUB_CATEGORY", "TEST_ID", 
            	    "PROGRAM_TYPE", "PROGRAM_ID", "PROGRAM_NAME", "DEFECT_TYPE", 
            	    "DEFECT_SEVERITY", "DEFECT_DESCRIPTION", "DEFECT_REGISTRAR", "DEFECT_DISCOVERY_DATE", 
            	    "DEFECT_HANDLER", "DEFECT_SCHEDULED_DATE", "DEFECT_COMPLETION_DATE", 
            	    "DEFECT_RESOLUTION_DETAILS", "PL", "PL_CONFIRM_DATE", "ORIGINAL_DEFECT_NUMBER", 
            	    "PL_DEFECT_JUDGE_CLASS", "PL_COMMENTS", "DEFECT_REG_CONFIRM_DATE", 
            	    "DEFECT_REGISTRAR_COMMENT", "DEFECT_STATUS", "INIT_CREATER", "LAST_MODIFIER");
            if (!isHeaderValid5(headerRow, expectedHeaders)) {
                response.put("status", "error");
                response.put("message", "����� �÷����� �ùٸ��� �ʽ��ϴ�.");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            List<Defect> defect = new ArrayList<>();
            
            // �ʵ�� �迭�� �����Ǵ� �� Ÿ�� �迭
            String[] fieldNames = {
            	    "testStage", "majorCategory", "subCategory", 
            	    "testId", "programType", "programId", "programName", 
            	    "defectType", "defectSeverity", "defectDescription", 
            	    "defectRegistrar", "defectDiscoveryDate", "defectHandler", 
            	    "defectScheduledDate", "defectCompletionDate", 
            	    "defectResolutionDetails", "pl", "plConfirmDate", 
            	    "originalDefectNumber", "plDefectJudgeClass", "plComments", 
            	    "defectRegConfirmDate", "defectRegistrarComment", 
            	    "defectStatus", "initCreater", "lastModifier"
            	};

            // ù ��° ���� ����̹Ƿ� �ǳʶݴϴ�.
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    Defect deFect = new Defect();

                    try {
                    	for (int j = 0; j < fieldNames.length; j++) {
                            Field field = Defect.class.getDeclaredField(fieldNames[j]);
                            field.setAccessible(true);

                            switch (field.getType().getSimpleName()) {
                            case "int":
                                field.set(deFect, (int) getCellValueAsNumeric2(row.getCell(j)));
                                break;
                            case "Date":
                                field.set(deFect, getCellValueAsDate2(row.getCell(j)));
                                break;
                            default:
                            	log.info(field.getType().getSimpleName());
                                field.set(deFect, getCellValueAsString2(row.getCell(j)));
                                break;
                        }
                        }
                    	defect.add(deFect);
                    } catch (Exception e) {
                        e.printStackTrace();
                        response.put("status", "error");
                        response.put("message", "�ùٸ��� ���� ���� �ԷµǾ����ϴ�.");
                        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                    }
                }
            }

            // �����ͺ��̽��� ����
            try {
                defectService.saveAllDefect(defect);
                response.put("status", "success");
                response.put("message", "���� ���ε尡 ���������� �Ϸ�Ǿ����ϴ�!");
                response.put("totalUploaded", defect.size());
            } catch (Exception e) {
                e.printStackTrace();
                response.put("status", "error");
                response.put("message", e.getMessage());
                return new ResponseEntity<>(response, HttpStatus.CONFLICT);
            }

        } catch (DuplicateKeyException e) {
            e.printStackTrace();
            response.put("status", "error");
            response.put("message", "�ߺ��� ���� ������ �߰ߵǾ����ϴ�.");
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        } catch (IOException e) {
            e.printStackTrace();
            response.put("status", "error");
            response.put("message", "���� ó�� �� ������ �߻��߽��ϴ�. �ٽ� �õ��� �ּ���.");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    
    // �׼� ��ȿ��� Ȯ��
    private boolean isHeaderValid5(Row headerRow, List<String> expectedHeaders) {
        for (int i = 0; i < expectedHeaders.size(); i++) {
            Cell cell = headerRow.getCell(i);
            if (cell == null || !cell.getStringCellValue().trim().equalsIgnoreCase(expectedHeaders.get(i))) {
                return false;
            }
        }
        return true;
    }
    
    // �׼� Cell�� String ��ȯ 
    private String getCellValueAsString2(Cell cell) {
        if (cell == null) {
            return null;
        }
        log.info(cell);
        return cell.getCellType() == CellType.STRING ? cell.getStringCellValue() : cell.toString();
    }
    
    // �׼� Cell�� Num ��ȯ
    private double getCellValueAsNumeric2(Cell cell) {
        if (cell == null) {
            return 0;
        }
        return cell.getCellType() == CellType.NUMERIC ? cell.getNumericCellValue() : Double.parseDouble(cell.toString());
    }
    
    // �׼� Cell�� Date ��ȯ
    private Date getCellValueAsDate2(Cell cell) {
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
    
    // String Value�� �ʼ��� ��ȿ�� ����
    private void validateRequiredField(String fieldValue, String fieldName) {
        if (fieldValue == null || fieldValue.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + "��(��) �ʼ� �Է� �׸��Դϴ�.");
        }
    }
}

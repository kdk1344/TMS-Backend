package com.tms.backend.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.tms.backend.vo.CommonCode;
import com.tms.backend.vo.Criteria;
import com.tms.backend.vo.Defect;
import com.tms.backend.vo.FileAttachment;
import com.tms.backend.vo.Notice;
import com.tms.backend.vo.User;
import com.tms.backend.vo.categoryCode;
import com.tms.backend.vo.devProgress;
import com.tms.backend.vo.testProgress;

import lombok.extern.log4j.Log4j;

@Mapper
public interface TestMapper {

	// �˻� ���Ϳ� ���� �׽�Ʈ �ó����� ������ ��ȸ
    public List<testProgress> searchTestProgress(
            @Param("testStage") String testStage,
            @Param("majorCategory") String majorCategory,
            @Param("subCategory") String subCategory,
            @Param("programType") String programType,
            @Param("testId") String testId,
            @Param("screenId") String screenId,
            @Param("screenName") String screenName,
            @Param("programName") String programName,
            @Param("programId") String programId,
            @Param("developer") String developer,
            @Param("testStatus") String testStatus,
            @Param("pl") String pl,
            @Param("ItMgr") String ItMgr,
            @Param("BusiMgr") String BusiMgr,
            @Param("execCompanyMgr") String execCompanyMgr,
            @Param("thirdPartyTestMgr") String thirdPartyTestMgr,
            @Param("offset") int offset,
            @Param("size") int size
    );
    // �׽�Ʈ ��Ȳ ����
    public void updatetestProgress(testProgress testProgress);
    // �׽�Ʈ ���� ������ �� ����
    public int getTotalTestCount(
            @Param("testStage") String testStage,
            @Param("majorCategory") String majorCategory,
            @Param("subCategory") String subCategory,
            @Param("programType") String programType,
            @Param("testId") String testId,
            @Param("screenId") String screenId,
            @Param("screenName") String screenName,
            @Param("programName") String programName,
            @Param("programId") String programId,
            @Param("developer") String developer,
            @Param("testStatus") String testStatus,
            @Param("pl") String pl,
            @Param("ItMgr") String ItMgr,
            @Param("BusiMgr") String BusiMgr,
            @Param("execCompanyMgr") String execCompanyMgr,
            @Param("thirdPartyTestMgr") String thirdPartyTestMgr
    );
    public void deleteTestProgress(int seq);//�׽�Ʈ ����
    public void inserttestProgress(testProgress testProgress); //�׽�Ʈ ����
    public List<devProgress> getscreenList(); //ȭ�� ���� ��������
    public String getProgramType(String programId); //���α׷� ���� ��������
    public testProgress getTestById(int seq); //�׽�Ʈ �ó����� ���� ��������
}

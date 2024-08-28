<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="ko">
  <head>
    <%@ include file="./common.jsp" %>
    <meta charset="UTF-8" />

    <title>프로그램 개발 진행 현황</title>
  </head>

  <body>
    <header class="header">
      <!-- 공통 헤더 정보 동적으로 삽입-->
    </header>

    <form id="searchForm" method="get" action="/tms/devProgress">
	    <div class="filter-row">
	        <div class="form-group">
	            <label for="majorCategory">업무 대분류</label>
	            <input type="text" id="majorCategory" name="majorCategory" value="${param.majorCategory}">
	        </div>
	        <div class="form-group">
	            <label for="subCategory">업무 중분류</label>
	            <input type="text" id="subCategory" name="subCategory" value="${param.subCategory}">
	        </div>
	        <div class="form-group">
	            <label for="programType">프로그램 구분</label>
	            <input type="text" id="programType" name="programType" value="${param.programType}">
	        </div>
	        <div class="form-group">
	            <label for="programName">프로그램명</label>
	            <input type="text" id="programName" name="programName" value="${param.programName}">
	        </div>
	        <div class="form-group">
	            <label for="pl">PL</label>
	            <input type="text" id="pl" name="pl" value="${param.pl}">
	        </div>
	        <!-- 더 많은 필터 옵션들 추가 -->
	    </div>
	    <button type="submit">조회</button>
	    <!-- 엑셀 다운로드 버튼 추가 -->
	    <a href="/tms/devdownloadAll">전체 공통코드 다운로드</a><br>
	    <!-- 검색된 결과 엑셀 다운로드 폼 -->
    </form>
    <form method="get" action="/tms/devdownloadFiltered">
        <input type="hidden" name="majorCategory" value="${param.majorCategory}" />
        <input type="hidden" name="subCategory" value="${param.subCategory}" />
        <input type="hidden" name="programType" value="${param.programType}" />
        <input type="hidden" name="programName" value="${param.programName}" />
        <input type="hidden" name="programId" value="${param.programId}" />
        <input type="hidden" name="regStatus" value="${param.regStatus}" />
        <input type="hidden" name="developer" value="${param.developer}" />
        <input type="hidden" name="devStatus" value="${param.devStatus}" />
        <input type="hidden" name="actualStartDate" value="${param.actualStartDate}" />
        <input type="hidden" name="actualEndDate" value="${param.actualEndDate}" />
        <input type="hidden" name="pl" value="${param.pl}" />
        <input type="hidden" name="thirdPartyTestMgr" value="${param.thirdPartyTestMgr}" />
        <input type="hidden" name="ItMgr" value="${param.ItMgr}" />
        <input type="hidden" name="BusiMgr" value="${param.BusiMgr}" />
        <input type="hidden" name="page" value="${param.page}" />
        <input type="hidden" name="size" value="${param.size}" />
        <button type="submit">검색된 결과 엑셀 다운로드</button>
    </form>
	
	<table>
	    <thead>
	        <tr>
	            <th>SEQ</th>
	            <th>업무 중분류</th>
	            <th>프로그램 구분</th>
	            <th>프로그램명</th>
	            <!-- 더 많은 컬럼들 -->
	        </tr>
	    </thead>
	    <tbody>
	        <c:forEach var="dev" items="${devProgressList}">
	            <tr>
	                <td>${dev.seq}</td>
	                <td>${dev.subCategory}</td>
	                <td>${dev.programType}</td>
	                <td>${dev.programName}</td>
	                <!-- 더 많은 컬럼들 -->
	            </tr>
	        </c:forEach>
	    </tbody>
	</table>
	
	<!-- 페이지네이션 -->
	<div>
	    <c:forEach var="i" begin="1" end="${totalPages}">
	        <c:choose>
	            <c:when test="${i == currentPage}">
	                <strong>${i}</strong>
	            </c:when>
	            <c:otherwise>
	                <a href="?page=${i}&size=${size}">${i}</a>
	            </c:otherwise>
	        </c:choose>
	    </c:forEach>
	</div>
  </body>
</html>

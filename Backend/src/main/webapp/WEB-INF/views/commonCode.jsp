<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <title>공통코드 목록</title>
    <style>
        table {
            width: 100%;
            border-collapse: collapse;
        }
        th, td {
            padding: 8px;
            text-align: left;
            border: 1px solid #ddd;
        }
        th {
            background-color: #f4f4f4;
        }
        a {
            margin: 0 5px;
        }
    </style>
</head>
<body>

<h1>공통코드 목록</h1>

<!-- 공통코드 검색 폼 -->
<form method="get" action="/tms/commonCode">
    <input type="text" name="parentCode" placeholder="ParentCode" value="${param.parentCode}" />
    <input type="text" name="code" placeholder="Code" value="${param.code}" />
    <input type="text" name="codeName" placeholder="CodeName" value="${param.codeName}" />
    <button type="submit">검색</button>
</form>

<!-- 공통코드 등록 폼 -->
<h2>공통코드 등록</h2>
<form method="post" action="/tms/api/ccwrite">
    <label for="parentCode">ParentCode:</label>
    <input type="text" id="parentCode" name="parentCode" required><br>
    
    <label for="code">Code:</label>
    <input type="text" id="code" name="code" required><br>
    
    <label for="codeName">CodeName:</label>
    <input type="text" id="codeName" name="codeName" required><br>
    
    <button type="submit">등록</button>
</form>

<!-- 공통코드 테이블 -->
<table>
    <thead>
        <tr>
            <th>ParentCode</th>
            <th>Code</th>
            <th>CodeName</th>
        </tr>
    </thead>
    <tbody>
        <c:forEach var="commonCode" items="${commonCodes}">
            <tr>
                <td>${commonCode.parentCode}</td>
                <td>${commonCode.code}</td>
                <td>${commonCode.codeName}</td>
            </tr>
        </c:forEach>
        <c:if test="${empty commonCodes}">
            <tr>
                <td colspan="3">조회된 공통코드가 없습니다.</td>
            </tr>
        </c:if>
    </tbody>
</table>

<!-- 페이징 처리 -->
<div>
    <c:forEach var="i" begin="1" end="${totalPages}">
        <c:choose>
            <c:when test="${i == currentPage}">
                <strong>${i}</strong>
            </c:when>
            <c:otherwise>
                <a href="?page=${i}&size=${size}&parentCode=${param.parentCode}&code=${param.code}&codeName=${param.codeName}">
                    ${i}
                </a>
            </c:otherwise>
        </c:choose>
    </c:forEach>
</div>

<!-- 엑셀 다운로드 -->
<h2>엑셀 다운로드</h2>
<a href="/tms/downloadAllcc">전체 공통코드 다운로드</a><br>
<a href="/tms/downloadFilteredcc?parentCode=${param.parentCode}&code=${param.code}&codeName=${param.codeName}">검색된 공통코드 다운로드</a>

</body>
</html>
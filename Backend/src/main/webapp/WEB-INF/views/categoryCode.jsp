<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>카테고리 코드 목록</title>
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
        .center {
            text-align: center;
        }
    </style>
</head>
<body>
<h1>카테고리 코드 목록</h1>

<!-- 검색 폼 -->
<form method="get" action="/tms/categoryCode">
    <input type="text" name="stageType" placeholder="StageType" value="${param.stageType}" />
    <input type="text" name="code" placeholder="Code" value="${param.code}" />
    <input type="text" name="codeName" placeholder="CodeName" value="${param.codeName}" />
    <button type="submit">검색</button>
</form>

<!-- 엑셀 다운로드 -->
<h2>엑셀 기능</h2>
<form method="get" action="/tms/downloadAllcat">
    <button type="submit">전체 엑셀 다운로드</button>
</form>
<form method="get" action="/tms/downloadFilteredcat">
    <input type="hidden" name="stageType" value="${param.stageType}" />
    <input type="hidden" name="code" value="${param.code}" />
    <input type="hidden" name="codeName" value="${param.codeName}" />
    <button type="submit">검색된 결과 엑셀 다운로드</button>
</form>

<!-- 엑셀 업로드 -->
<h2>엑셀 업로드</h2>
<form method="post" action="/tms/api/catupload" enctype="multipart/form-data">
    <input type="file" name="file" accept=".xlsx" />
    <button type="submit">업로드</button>
</form>

<!-- 카테고리 코드 테이블 -->
<table>
    <thead>
        <tr>
            <th>StageType</th>
            <th>Code</th>
            <th>CodeName</th>
            <th class="center">수정</th>
            <th class="center">삭제</th>
        </tr>
    </thead>
    <tbody>
        <c:forEach var="categoryCode" items="${categoryCodes}">
            <tr>
                <td>${categoryCode.stageType}</td>
                <td>${categoryCode.code}</td>
                <td>${categoryCode.codeName}</td>
                <td class="center">
                    <form action="/tms/catmodify" method="post">
                        <input type="hidden" name="code" value="${categoryCode.code}" />
                        <button type="submit">수정</button>
                    </form>
                </td>
                <td class="center">
                    <form action="/tms/deletecat" method="post">
                        <input type="hidden" name="code" value="${categoryCode.code}" />
                        <button type="submit">삭제</button>
                    </form>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty categoryCodes}">
            <tr>
                <td colspan="5" class="center">조회된 카테고리 코드가 없습니다.</td>
            </tr>
        </c:if>
    </tbody>
</table>

<!-- 페이징 링크 -->
<div>
    <c:forEach var="i" begin="1" end="${totalPages}">
        <c:choose>
            <c:when test="${i == currentPage}">
                <strong>${i}</strong>
            </c:when>
            <c:otherwise>
                <a href="?page=${i}&size=${param.size}&stageType=${param.stageType}&code=${param.code}&codeName=${param.codeName}">
                    ${i}
                </a>
            </c:otherwise>
        </c:choose>
    </c:forEach>
</div>

<!-- 등록 폼 -->
<h2>카테고리 코드 등록</h2>
<form action="/tms/catwrite" method="post">
    <label for="stageType">StageType:</label>
    <select id="stageType" name="stageType">
        <option value="대">대</option>
        <option value="중">중</option>
    </select><br/>

    <!-- 상위 대분류 코드 입력 필드 -->
    <label for="parentCode">Parent Code:</label>
    <input type="text" id="parentCode" name="parentCode" /><br/>

    <label for="code">Code:</label>
    <input type="text" id="code" name="code" required /><br/>
    
    <label for="codeName">CodeName:</label>
    <input type="text" id="codeName" name="codeName" required /><br/>
    
    <button type="submit">등록</button>
</form>

<!-- 상태 메시지 출력 -->
<c:if test="${not empty message}">
    <div class="status ${status}">
        ${message}
    </div>
</c:if>

</body>
</html>
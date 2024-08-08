<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="ko">
<head>
	<%@ include file="./common.jsp" %>
    <title>사용자 관리</title>
    <style>
        table {
            width: 100%;
            border-collapse: collapse;
        }
        th, td {
            border: 1px solid black;
            padding: 8px;
            text-align: left;
        }
        .buttons {
            margin-bottom: 20px;
        }
        .search-bar {
            margin-bottom: 20px;
        }
        .pagination a.active {
            background-color: #4CAF50;
            color: white;
            border: 1px solid #4CAF50;
        }
        .pagination a:hover:not(.active) {
            background-color: #ddd;
        }
    </style>
</head>
<body>
    <h1>사용자 관리</h1>

    <div class="search-bar">
        <form action="userList" method="get">
            <input type="text" name="username" placeholder="사용자명 검색" value="${param.username}">
            <select name="authorityName">
                <option value="">직무 선택</option>
                <option value="type1" ${param.roleName == 'type1' ? 'selected' : ''}>type1</option>
                <option value="type2" ${param.roleName == 'type2' ? 'selected' : ''}>type2</option>
                <option value="type3" ${param.roleName == 'type3' ? 'selected' : ''}>type3</option>
            </select>
            <button type="submit">검색</button>
        </form>
    </div>

    <div class="buttons">
        <!-- 전체 사용자 다운로드 -->
        <form action="downloadAll" method="get" style="display:inline;">
            <button type="submit">전체 사용자 다운로드</button>
        </form>

        <!-- 필터링된 사용자 다운로드 -->
        <form action="downloadFiltered" method="get" style="display:inline;">
            <input type="hidden" name="userName" value="${param.userName}">
            <input type="hidden" name="authorityName" value="${param.authorityName}">
            <button type="submit">조회된 사용자 다운로드</button>
        </form>
    </div>

    <table>
        <thead>
            <tr>
                <th>유저 ID</th>
                <th>사용자명</th>
                <th>직무명</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="user" items="${userList}">
                <tr>
                    <td>${user.userID}</td>
                    <td>${user.username}</td>
                    <td>${user.roleName}</td>
                </tr>
            </c:forEach>
        </tbody>
    </table>

    <div class="pagination">
        <c:if test="${pageDTO.total > 0}">
            <c:set var="totalPages" value="${(pageDTO.total / pageDTO.criteria.perPageNum) + (pageDTO.total % pageDTO.criteria.perPageNum > 0 ? 1 : 0)}" />
            <c:forEach begin="1" end="${totalPages}" var="i">
                <a href="?page=${i}&userName=${param.userName}&authorityName=${param.authorityName}" class="${pageDTO.criteria.page == i ? 'active' : ''}">${i}</a>
            </c:forEach>
        </c:if>
    </div>
</body>
</html>
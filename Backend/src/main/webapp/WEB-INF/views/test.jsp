<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <title>사용자 관리</title>
    <%@ include file="./common.jsp" %>
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
    </style>
</head>
<body>
    <h1>사용자 관리</h1>


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
                <a href="?page=${i}&filter=${param.filter}" class="${pageDTO.criteria.page == i ? 'active' : ''}">${i}</a>
            </c:forEach>
        </c:if>
    </div>
</body>
</html>
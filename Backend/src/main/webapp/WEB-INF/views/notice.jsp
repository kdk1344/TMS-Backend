<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>공지사항 목록</title>
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
<h1>공지사항 목록</h1>

<!-- 검색 폼 -->
<form method="get" action="/admin/notice">
    <input type="text" name="postDate" placeholder="게시일자" value="${param.postDate}" />
    <input type="text" name="title" placeholder="제목" value="${param.title}" />
    <input type="text" name="content" placeholder="내용" value="${param.content}" />
    <button type="submit">검색</button>
</form>

<!-- 공지사항 테이블 -->
<table>
    <thead>
        <tr>
            <th>게시일자</th>
            <th>제목</th>
            <th>내용</th>
            <th>첨부파일</th>
        </tr>
    </thead>
    <tbody>
        <c:forEach var="notice" items="${notices}">
            <tr>
                <td>${notice.postDate}</td>
                <td>${notice.title}</td>
                <td>${notice.content}</td>
                <td>
                    <c:forEach var="attachment" items="${notice.attachments}">
                        <a href="${attachment.storageLocation}">${attachment.fileName}</a><br/>
                    </c:forEach>
                </td>
            </tr>
        </c:forEach>
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
                <a href="?page=${i}&size=${param.size}&postDate=${param.postDate}&title=${param.title}&content=${param.content}">${i}</a>
            </c:otherwise>
        </c:choose>
    </c:forEach>
</div>

</body>
</html>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>공지사항 목록</title>
</head>
<body>
<h1>공지사항 목록</h1>

<form method="get" action="/admin/notices">
    <input type="text" name="postDate" placeholder="게시일자" value="${param.postDate}" />
    <input type="text" name="title" placeholder="제목" value="${param.title}" />
    <input type="text" name="content" placeholder="내용" value="${param.content}" />
    <button type="submit">검색</button>
</form>

<table border="1">
    <thead>
        <tr>
            <th>게시일자</th>
            <th>제목</th>
            <th>내용</th>
            <th>상세보기</th>
        </tr>
    </thead>
    <tbody>
        <c:forEach var="notice" items="${notices}">
            <tr>
                <td>${notice.postDate}</td>
                <td>${notice.title}</td>
                <td>${notice.content}</td>
                <td><a href="/admin/notices/${notice.seq}">상세보기</a></td>
            </tr>
        </c:forEach>
    </tbody>
</table>

<!-- 페이징 링크 -->
<div>
    <c:forEach var="i" begin="1" end="${totalPages}">
        <a href="?page=${i}&size=${param.size}&postDate=${param.postDate}&title=${param.title}&content=${param.content}">${i}</a>
    </c:forEach>
</div>

</body>
</html>
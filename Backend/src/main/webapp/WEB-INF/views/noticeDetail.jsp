<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>공지사항 상세보기</title>
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
    </style>
</head>
<body>
<h1>공지사항 상세보기</h1>

<!-- 공지사항 상세 정보 -->
<table>
    <tr>
        <th>게시일자</th>
        <td>${notice.postDate}</td>
    </tr>
    <tr>
        <th>제목</th>
        <td>${notice.title}</td>
    </tr>
    <tr>
        <th>내용</th>
        <td>${notice.content}</td>
    </tr>
    <tr>
        <th>첨부파일</th>
        <td>
            <c:forEach var="attachment" items="${notice.attachments}">
                <a href="${attachment.storageLocation}">${attachment.fileName}</a><br/>
            </c:forEach>
        </td>
    </tr>
</table>

<!-- 목록으로 돌아가기 -->
<a href="/tms/notice">목록으로 돌아가기</a>

</body>
</html>
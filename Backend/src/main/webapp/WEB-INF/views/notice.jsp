<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html>
<head>
    <title>Notices</title>
</head>
<body>
<h1>Notice Board</h1>

<table border="1">
    <tr>
        <th>SEQ</th>
        <th>Post Date</th>
        <th>Title</th>
        <th>Content</th>
        <th>Created Date</th>
        <th>Last Modified Date</th>
    </tr>
    <c:forEach var="post" items="${posts}">
        <tr>
            <td>${post.seq}</td>
            <td>${post.postDate}</td>
            <td>${post.title}</td>
            <td>${post.content}</td>
            <td>${post.createdDate}</td>
            <td>${post.lastModifiedDate}</td>
        </tr>
    </c:forEach>
</table>

</body>
</html>
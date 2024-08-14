<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>공지사항 상세보기 및 수정</title>
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
    <script>
        function markForDeletion(seq) {
            var deleteInput = document.createElement("input");
            deleteInput.type = "hidden";
            deleteInput.name = "deleteFileSeqs";
            deleteInput.value = seq;
            document.getElementById("editForm").appendChild(deleteInput);
            document.getElementById("attachment_" + seq).style.display = "none";
        }
    </script>
</head>
<body>
<h1>공지사항 상세보기 및 수정</h1>

<!-- 공지사항 수정 폼 -->
<div id="editFormContainer">
    <form id="editForm" method="post" action="/tms/ntupdate" enctype="multipart/form-data">
        <input type="hidden" name="seq" value="${notice.seq}">
        
        <label for="postDate">게시일자:</label>
        <input type="date" id="postDate" name="postDate" value="${notice.postDate}"><br>

        <label for="title">제목:</label>
        <input type="text" id="title" name="title" value="${notice.title}"><br>

        <label for="content">내용:</label>
        <textarea id="content" name="content">${notice.content}</textarea><br>

        <label for="file">첨부파일 추가:</label>
        <input type="file" id="file" name="file" multiple><br>

        <ul>
            <c:forEach var="attachment" items="${notice.attachments}">
                <li id="attachment_${attachment.seq}">
                    ${attachment.fileName}
                    <a href="/tms/downloadAttachment?seq=${attachment.seq}">[다운로드]</a>
                    <button type="button" onclick="markForDeletion(${attachment.seq})">[제거]</button>
                </li>
            </c:forEach>
        </ul>

        <button type="submit">수정 완료</button>
        <button type="button" onclick="window.location.href='/tms/notice'">취소</button>
    </form>
</div>

<!-- 목록으로 돌아가기 -->
<a href="/tms/notice">목록으로 돌아가기</a>

</body>
</html>
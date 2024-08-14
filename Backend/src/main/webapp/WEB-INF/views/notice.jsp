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

<form method="get" action="/tms/notice">
    <input type="text" name="postDate" placeholder="게시일자" value="${param.postDate}" />
    <input type="text" name="title" placeholder="제목" value="${param.title}" />
    <input type="text" name="content" placeholder="내용" value="${param.content}" />
    <button type="submit">검색</button>
</form>

<!-- 공지사항 등록/수정 폼 -->
<form id="noticeForm" method="post" action="/tms/ntwrite" enctype="multipart/form-data">
    <input type="hidden" id="seq" name="seq" value="">
    <input type="hidden" id="formAction" name="formAction" value="/tms/ntwrite">
    <label for="postDate">게시일자:</label>
    <input type="date" id="postDate" name="postDate"><br>
    <label for="title">제목:</label>
    <input type="text" id="title" name="title"><br>
    <label for="content">내용:</label>
    <textarea id="content" name="content"></textarea><br>
    <label for="file">첨부파일:</label>
    <input type="file" id="file" name="file" multiple onchange="renderFileList()"><br>
    <ul id="fileList"></ul>
    <button type="submit" id="formSubmit">등록</button>
</form>

<!-- 공지사항 테이블 -->
<table>
    <thead>
        <tr>
            <th>게시일자</th>
            <th>제목</th>
            <th>내용</th>
            <th>첨부파일</th>
            <th>상세보기</th>
            <th>관리</th>
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
                <td>
                    <a href="/tms/ntdetail?seq=${notice.seq}">상세보기</a>
                </td>
                <td>
                    <form action="/tms/ntdelete" method="post" style="display:inline;">
                        <input type="hidden" name="seq" value="${notice.seq}">
                        <button type="submit" onclick="return confirm('정말 삭제하시겠습니까?');">삭제</button>
                    </form>
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
<script>
        function openEditPopup(notice) {
            // 팝업 열기
            const popup = document.getElementById('editPopup');
            popup.style.display = 'block';

            // 폼에 데이터 채우기
            document.getElementById('editSeq').value = notice.seq;
            document.getElementById('editPostDate').value = notice.postDate;
            document.getElementById('editTitle').value = notice.title;
            document.getElementById('editContent').value = notice.content;

            // 첨부파일 목록 비우기
            document.getElementById('editFileList').innerHTML = '';

            // 기존 첨부파일 목록 표시
            notice.attachments.forEach(function(attachment, index) {
                const li = document.createElement('li');
                li.textContent = attachment.fileName;
                const removeButton = document.createElement('button');
                removeButton.textContent = '제거';
                removeButton.type = 'button';
                removeButton.onclick = function() {
                    removeFile(index);
                };
                li.appendChild(removeButton);
                document.getElementById('editFileList').appendChild(li);
            });
        }

        function closeEditPopup() {
            document.getElementById('editPopup').style.display = 'none';
        }

        function removeFile(fileIndex) {
            // 파일 제거 로직 (서버에서 기존 파일을 제거할 수도 있음)
            const fileList = document.getElementById('editFileList');
            fileList.removeChild(fileList.childNodes[fileIndex]);
        }
    </script>

</body>
</html>
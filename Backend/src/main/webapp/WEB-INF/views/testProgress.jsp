<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="ko">
  <head>
    <%@ include file="./common.jsp" %>
    <meta charset="UTF-8" />
    <link rel="stylesheet" type="text/css" href="../../resources/css/devProgress.css" />

    <title>TMS 개발진행 관리</title>
    <script type="module" src="../../resources/js/devProgress.js"></script>
  </head>

  <body>
    <h1>Test Progress</h1>

    <table border="1">
        <thead>
            <tr>
                <th>SEQ</th>
                <th>Test Stage</th>
                <th>Major Category</th>
                <th>Sub Category</th>
                <th>Program Type</th>
                <th>Test ID</th>
                <th>Program Name</th>
                <th>Developer</th>
                <th>PL</th>
                <th>Exec Company Manager</th>
                <th>Test Status</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="testProgress1" items="${testProgress}">
                <tr>
                    <td>${testProgress1.seq}</td>
                    <td>${testProgress1.testStage}</td>
                    <td>${testProgress1.majorCategory}</td>
                    <td>${testProgress1.subCategory}</td>
                    <td>${testProgress1.programType}</td>
                    <td>${testProgress1.testId}</td>
                    <td>${testProgress1.programName}</td>
                    <td>${testProgress1.developer}</td>
                    <td>${testProgress1.pl}</td>
                    <td>${testProgress1.execCompanyMgr}</td>
                    <td>${testProgress1.testStatus}</td>
                </tr>
            </c:forEach>
        </tbody>
    </table>

    <!-- Pagination -->
    <div class="pagination">
        <c:if test="${currentPage > 1}">
            <a href="?page=${currentPage - 1}">&laquo; Previous</a>
        </c:if>
        
        <c:forEach begin="1" end="${totalPages}" var="i">
            <c:choose>
                <c:when test="${i == currentPage}">
                    <span>${i}</span>
                </c:when>
                <c:otherwise>
                    <a href="?page=${i}">${i}</a>
                </c:otherwise>
            </c:choose>
        </c:forEach>
        
        <c:if test="${currentPage < totalPages}">
            <a href="?page=${currentPage + 1}">Next &raquo;</a>
        </c:if>
    </div>

</body>
</html>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="ko">
  <head>
    <%@ include file="./common.jsp" %>
    <meta charset="UTF-8" />
    <link rel="stylesheet" type="text/css" href="../../resources/css/commonCode.css" />

    <title>TMS 공통코드 관리</title>
    <script type="module" src="../../resources/js/commonCode.js"></script>
  </head>

  <body>
    <header class="header">
      <!-- 공통 헤더 정보 동적으로 삽입-->
    </header>

    <main class="content">
      <h1 class="page-title">공통코드 관리</h1>

      <!-- Register Modal -->
      <div id="commonCodeRegisterModal" class="modal">
        <!-- Modal content -->
        <div class="modal-content">
          <h2>공통코드 등록</h2>
          <form id="commonCodeRegisterForm" class="modal-form">
            <div class="modal-form-group">
              <label for="commonCodeID">아이디</label>
              <input type="text" id="commonCodeID" name="commonCodeID" placeholder="수정 시 변경불가" required />
            </div>

            <div class="modal-form-group">
              <label for="commonCodeName">공통코드명</label>
              <input type="text" id="commonCodeName" name="commonCodeName" required />
            </div>

            <div class="modal-form-group">
              <label for="password">비밀번호</label>
              <input type="password" id="password" name="password" required autocomplete />
            </div>

            <div class="modal-form-group">
              <label for="authorityName">직무</label>
              <select id="authorityName" name="authorityName">
                <option value="type1">직무 1</option>
                <option value="type2">직무 2</option>
                <option value="type3">직무 3</option>
                <option value="type4">직무 4</option>
              </select>
            </div>

            <div class="flex-box">
              <button type="submit">저장</button>
              <button type="button" id="closeCommonCodeRegisterModalButton">닫기</button>
            </div>
          </form>
        </div>
      </div>

      <!-- Edit Modal -->
      <div id="commonCodeEditModal" class="modal">
        <!-- Modal content -->
        <div class="modal-content">
          <h2>공통코드 수정</h2>
          <form id="commonCodeEditForm" class="modal-form">
            <div class="modal-form-group">
              <label for="commonCodeIDForEdit">아이디</label>
              <input type="text" id="commonCodeIDForEdit" name="commonCodeID" readonly required />
            </div>

            <div class="modal-form-group">
              <label for="commonCodeNameForEdit">공통코드명</label>
              <input type="text" id="commonCodeNameForEdit" name="commonCodeName" required />
            </div>

            <div class="modal-form-group">
              <label for="passwordForEdit">비밀번호</label>
              <input type="password" id="passwordForEdit" name="password" required autocomplete />
            </div>

            <div class="modal-form-group">
              <label for="authorityNameForEdit">직무</label>
              <select id="authorityNameForEdit" name="authorityName">
                <option value="type1">직무 1</option>
                <option value="type2">직무 2</option>
                <option value="type3">직무 3</option>
                <option value="type4">직무 4</option>
              </select>
            </div>

            <div class="flex-box">
              <button type="submit">저장</button>
              <button type="button" id="closeCommonCodeEditModalButton">닫기</button>
            </div>
          </form>
        </div>
      </div>

      <!-- File Download Modal -->
      <div id="commonCodeFileDownloadModal" class="modal">
        <!-- Modal content -->
        <div class="modal-content">
          <h2>엑셀 파일 다운로드</h2>
          <div class="flex-box">
            <form id="downloadAllCommonCodeForm" action="downloadAll" method="get">
              <button type="submit" id="downloadAllCommonCodeButton">전체 공통코드 목록 다운로드</button>
            </form>

            <form id="downloadFilteredCommonCodeForm" action="downloadFiltered" method="get" id="downloadFilteredForm">
              <!-- 숨겨진 input 필드 -->
              <input type="hidden" id="downloadcommonCodeName" name="commonCodeName" />
              <input type="hidden" id="downloadAuthorityName" name="authorityName" />

              <button type="submit" id="downloadFilteredCommonCodeButton">조회된 공통코드 목록 다운로드</button>
            </form>
          </div>
        </div>
      </div>

      <!-- 공통코드 조회 필터링 폼 -->
      <form id="commonCodeFilterForm">
        <div class="filter-container">
          <div class="form-group">
            <label for="parentCodeForFilter">상위코드</label>
            <select id="parentCodeForFilter" name="parentCode">
              <option value="">전체</option>
              <option value="type1">상위코드 1</option>
              <option value="type2">상위코드 2</option>
              <option value="type3">상위코드 3</option>
              <option value="type4">상위코드 4</option>
            </select>
          </div>

          <div class="form-group">
            <label for="codeForFilter">코드</label>
            <select id="codeForFilter" name="code">
              <option value="">전체</option>
              <option value="type1">코드 1</option>
              <option value="type2">코드 2</option>
              <option value="type3">코드 3</option>
              <option value="type4">코드 4</option>
            </select>
          </div>

          <div class="form-group">
            <label for="codeName">코드명</label>
            <input type="text" id="codeNameForFilter" name="codeName" placeholder="코드명을 입력하세요" />
          </div>
        </div>
        <div class="flex-box">
          <button type="submit" id="searchCommonCodeButton">조회</button>
          <button type="reset" id="resetButton">초기화</button>
        </div>
      </form>

      <!-- 버튼 그룹 -->
      <div class="button-group">
        <div class="flex-box">
          <button id="openCommonCodeRegisterModalButton">등록</button>
          <button id="deleteCommonCodeButton">삭제</button>
        </div>
        <div class="flex-box">
          <!-- 파일 선택 input -->
          <input type="file" id="uploadCommonCodeFileInput" name="file" accept=".xlsx, .xls" />

          <button id="uploadCommonCodeFileButton">파일 업로드</button>
          <button id="openCommonCodeFileDownloadModalButton">파일 다운로드</button>
        </div>
      </div>

      <!-- 공통코드 목록 테이블 -->
      <div class="table-container">
        <table id="commonCodeTable">
          <thead>
            <tr>
              <th><input type="checkbox" id="selectAllCommonCodeCheckbox" /></th>
              <th>상위코드</th>
              <th>상위코드명</th>
              <th>코드</th>
              <th>코드명</th>
            </tr>
          </thead>
          <tbody id="commonCodeTableBody">
            <!-- 공통코드 정보 동적으로 삽입-->
          </tbody>
        </table>
      </div>

      <!-- 페이지네이션 -->
      <div id="commonCodePagination" class="pagination">
        <!-- 페이지네이션 버튼 동적으로 삽입 -->
      </div>
    </main>
  </body>
</html>

<!-- 공통코드 검색 폼 -->
<!-- <form method="get" action="/tms/commonCode">
    <input type="text" name="parentCode" placeholder="ParentCode" value="${param.parentCode}" />
    <input type="text" name="code" placeholder="Code" value="${param.code}" />
    <input type="text" name="codeName" placeholder="CodeName" value="${param.codeName}" />
    <button type="submit">검색</button>
</form> -->

<!-- 공통코드 등록 폼 -->
<!-- <h2>공통코드 등록</h2>
<form method="post" action="/tms/api/ccwrite">
    <label for="parentCode">ParentCode:</label>
    <input type="text" id="parentCode" name="parentCode" required><br>
    
    <label for="code">Code:</label>
    <input type="text" id="code" name="code" required><br>
    
    <label for="codeName">CodeName:</label>
    <input type="text" id="codeName" name="codeName" required><br>
    
    <button type="submit">등록</button>
</form> -->

<!-- 공통코드 테이블 -->
<!-- <table>
    <thead>
        <tr>
            <th>ParentCode</th>
            <th>Code</th>
            <th>CodeName</th>
        </tr>
    </thead>
    <tbody>
        <c:forEach var="commonCode" items="${commonCodes}">
            <tr>
                <td>${commonCode.parentCode}</td>
                <td>${commonCode.code}</td>
                <td>${commonCode.codeName}</td>
            </tr>
        </c:forEach>
        <c:if test="${empty commonCodes}">
            <tr>
                <td colspan="3">조회된 공통코드가 없습니다.</td>
            </tr>
        </c:if>
    </tbody>
</table> -->

<!-- 페이징 처리 -->
<!-- <div>
    <c:forEach var="i" begin="1" end="${totalPages}">
        <c:choose>
            <c:when test="${i == currentPage}">
                <strong>${i}</strong>
            </c:when>
            <c:otherwise>
                <a href="?page=${i}&size=${size}&parentCode=${param.parentCode}&code=${param.code}&codeName=${param.codeName}">
                    ${i}
                </a>
            </c:otherwise>
        </c:choose>
    </c:forEach>
</div> -->

<!-- 엑셀 다운로드 -->
<!-- <h2>엑셀 다운로드</h2>
<a href="/tms/downloadAllcc">전체 공통코드 다운로드</a><br>
<a href="/tms/downloadFilteredcc?parentCode=${param.parentCode}&code=${param.code}&codeName=${param.codeName}">검색된 공통코드 다운로드</a>

</body>
</html> -->

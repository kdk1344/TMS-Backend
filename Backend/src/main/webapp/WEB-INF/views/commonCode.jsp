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
              <label for="parentCodeForRegister">상위코드</label>
              <select id="parentCodeForRegister" name="parentCode">
                <option value="00" selected>상위코드 등록</option>
                <!-- 상위코드 목록 동적 삽입 -->
              </select>
            </div>

            <div class="modal-form-group">
              <label for="codeForRegister">코드</label>
              <input
                type="text"
                id="codeForRegister"
                name="code"
                placeholder="숫자 2자리 입력(추후 변경불가)"
                required
                pattern="\d{2}"
                maxlength="2"
              />
            </div>

            <div class="modal-form-group">
              <label for="codeNameForRegister">코드명</label>
              <input
                type="text"
                id="codeNameForRegister"
                name="codeName"
                required
                placeholder="코드명을 입력해주세요"
              />
            </div>

            <div class="flex-box justify-end">
              <button type="button" id="closeCommonCodeRegisterModalButton" class="cancel-button">취소</button>
              <button type="submit" class="save-button">저장</button>
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
              <label for="parentCodeForEdit">상위코드</label>
              <span id="parentCodeForEdit"></span>
              <span id="parentCodeNameForEdit"></span>
            </div>

            <div class="modal-form-group">
              <label for="codeForEdit">코드</label>
              <span id="codeForEdit"></span>
            </div>

            <div class="modal-form-group">
              <label for="codeNameForEdit">코드명</label>
              <input type="text" id="codeNameForEdit" name="codeName" required />
            </div>

            <div class="flex-box justify-end">
              <button type="button" id="closeCommonCodeEditModalButton" class="cancel-button">취소</button>
              <button type="submit" class="save-button">저장</button>
            </div>
          </form>
        </div>
      </div>

      <!-- File Download Modal -->
      <div id="commonCodeFileDownloadModal" class="modal">
        <!-- Modal content -->
        <div class="modal-content">
          <h2>엑셀 다운로드</h2>
          <div class="flex-box">
            <form id="downloadAllCommonCodeForm" action="downloadAll" method="get">
              <button type="submit" id="downloadAllCommonCodeButton" class="excel-button">전체자료 다운로드</button>
            </form>

            <form id="downloadFilteredCommonCodeForm" action="downloadFiltered" method="get" id="downloadFilteredForm">
              <!-- 숨겨진 input 필드 -->
              <input type="hidden" id="downloadcommonCodeName" name="commonCodeName" />
              <input type="hidden" id="downloadAuthorityName" name="authorityName" />

              <button type="submit" id="downloadFilteredCommonCodeButton" class="excel-button">
                조회결과 다운로드
              </button>
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
              <option value="" selected>전체</option>
            </select>
          </div>

          <div class="form-group">
            <label for="codeForFilter">코드</label>
            <select id="codeForFilter" name="code">
              <option value="" selected>전체</option>
            </select>
          </div>

          <div class="form-group">
            <label for="codeNameForFilter">코드명</label>
            <input type="text" id="codeNameForFilter" name="codeName" placeholder="코드명을 입력하세요" />
          </div>
        </div>
        <div class="flex-box align-end">
          <button type="submit" id="searchCommonCodeButton">조회</button>
          <button type="reset" id="resetButton" class="reset-button">초기화</button>
        </div>
      </form>

      <!-- 버튼 그룹 -->
      <div class="button-group">
        <div class="flex-box">
          <button id="openCommonCodeRegisterModalButton">등록</button>
          <button id="deleteCommonCodeButton" class="delete-button">삭제</button>
        </div>
        <div class="flex-box">
          <!-- 파일 선택 input -->
          <input type="file" id="uploadCommonCodeFileInput" name="file" accept=".xlsx, .xls" />

          <button id="uploadCommonCodeFileButton" class="excel-button">엑셀 업로드</button>
          <button id="openCommonCodeFileDownloadModalButton" class="excel-button">엑셀 다운로드</button>
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

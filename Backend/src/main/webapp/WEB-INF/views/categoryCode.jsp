<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="ko">
  <head>
    <%@ include file="./common.jsp" %>
    <meta charset="UTF-8" />
    <link rel="stylesheet" type="text/css" href="../../resources/css/categoryCode.css" />

    <title>TMS 분류코드 관리</title>
    <script type="module" src="../../resources/js/categoryCode.js"></script>
  </head>

  <body>
    <header class="header">
      <!-- 공통 헤더 정보 동적으로 삽입-->
    </header>

    <main class="content">
      <h1 class="page-title">분류코드 관리</h1>

      <!-- Register Modal -->
      <div id="categoryCodeRegisterModal" class="modal">
        <!-- Modal content -->
        <div class="modal-content">
          <h2>분류코드 등록</h2>
          <form id="categoryCodeRegisterForm" class="modal-form">
            <div class="modal-form-group">
              <label for="stageTypeForRegister">구분</label>
              <div class="flex-box">
                <label>
                  <input type="radio" id="mainCategoryForRegister" name="stageType" value="대" checked />
                  대분류
                </label>
                <label>
                  <input type="radio" id="subCategoryForRegister" name="stageType" value="중" />
                  중분류
                </label>
              </div>
            </div>
            <div class="modal-form-group">
              <label for="parentCodeForRegister">대분류</label>
              <select id="parentCodeForRegister" name="parentCode" disabled>
                <option value="" selected disabled>대분류를 선택해주세요</option>
                <!-- 대분류 목록 동적 삽입 -->
              </select>
            </div>

            <div class="modal-form-group">
              <label for="codeForRegister">코드</label>
              <div class="code-display">
                <span id="partialCodeForRegister"></span>
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
              <button type="button" id="closeCategoryCodeRegisterModalButton" class="cancel-button">취소</button>
              <button type="submit" class="save-button">저장</button>
            </div>
          </form>
        </div>
      </div>

      <!-- Edit Modal -->
      <div id="categoryCodeEditModal" class="modal">
        <!-- Modal content -->
        <div class="modal-content">
          <h2>분류코드 수정</h2>
          <form id="categoryCodeEditForm" class="modal-form">
            <div class="modal-form-group">
              <label for="stageTypeForEdit">구분</label>
              <span id="stageTypeForEdit"></span>
            </div>

            <div class="modal-form-group">
              <label for="codeForEdit">코드</label>
              <span id="codeForEdit"></span>
            </div>

            <div class="modal-form-group">
              <label for="codeNameForEdit">코드명</label>
              <input type="text" id="codeNameForEdit" name="codeName" required placeholder="코드명을 입력해주세요" />
            </div>

            <div class="flex-box justify-end">
              <button type="button" id="closeCategoryCodeEditModalButton" class="cancel-button">취소</button>
              <button type="submit" class="save-button">저장</button>
            </div>
          </form>
        </div>
      </div>

      <!-- File Download Modal -->
      <div id="categoryCodeFileDownloadModal" class="modal">
        <!-- Modal content -->
        <div class="modal-content">
          <h2>엑셀 다운로드</h2>
          <div class="flex-box">
            <form id="downloadAllCategoryCodeForm" action="downloadAllcc" method="get">
              <button type="submit" id="downloadAllCategoryCodeButton" class="excel-button">
                <img src="../../resources/images/download_icon.png" />전체자료 다운로드
              </button>
            </form>

            <form
              id="downloadFilteredCategoryCodeForm"
              action="downloadFilteredcc"
              method="get"
              id="downloadFilteredForm"
            >
              <!-- 숨겨진 input 필드 -->
              <input type="hidden" id="parentCodeForDownload" name="parentCode" />
              <input type="hidden" id="codeForDownload" name="code" />
              <input type="hidden" id="codeNameForDownload" name="codeName" />

              <button type="submit" id="downloadFilteredCategoryCodeButton" class="excel-button">
                <img src="../../resources/images/download_icon.png" />
                조회결과 다운로드
              </button>
            </form>
          </div>
        </div>
      </div>

      <!-- 분류코드 조회 필터링 폼 -->
      <form id="categoryCodeFilterForm">
        <div class="filter-container">
          <div class="form-group">
            <label for="parentCodeForFilter">대분류</label>
            <select id="parentCodeForFilter" name="parentCode">
              <option value="" selected>전체</option>
            </select>
          </div>

          <div class="form-group">
            <label for="codeForFilter">중분류</label>
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
          <button type="submit" id="searchCategoryCodeButton">조회</button>
          <button type="reset" id="resetButton" class="reset-button">초기화</button>
        </div>
      </form>

      <!-- 버튼 그룹 -->
      <div class="button-group">
        <div class="flex-box">
          <button id="openCategoryCodeRegisterModalButton">등록</button>
          <button id="deleteCategoryCodeButton" class="delete-button">삭제</button>
        </div>
        <div class="flex-box">
          <!-- 파일 선택 input -->
          <input type="file" id="uploadCategoryCodeFileInput" name="file" accept=".xlsx, .xls" />

          <button id="uploadCategoryCodeFileButton" class="excel-button">
            <img src="../../resources/images/upload_icon.png" />엑셀 업로드
          </button>
          <button id="openCategoryCodeFileDownloadModalButton" class="excel-button">
            <img src="../../resources/images/download_icon.png" />엑셀 다운로드
          </button>
        </div>
      </div>

      <!-- 분류코드 목록 테이블 -->
      <div class="table-container">
        <table id="categoryCodeTable">
          <thead>
            <tr>
              <th><input type="checkbox" id="selectAllCategoryCodeCheckbox" /></th>
              <th class="th-stage-type">단계구분</th>
              <th class="th-code">코드</th>
              <th class="th-code-name">코드명</th>
            </tr>
          </thead>
          <tbody id="categoryCodeTableBody">
            <!-- 분류코드 정보 동적으로 삽입-->
          </tbody>
        </table>
      </div>

      <!-- 페이지네이션 -->
      <div id="categoryCodePagination" class="pagination">
        <!-- 페이지네이션 버튼 동적으로 삽입 -->
      </div>
    </main>
  </body>
</html>

<!-- 검색 폼 -->
<!-- <form method="get" action="/tms/categoryCode">
    <input type="text" name="stageType" placeholder="StageType" value="${param.stageType}" />
    <input type="text" name="code" placeholder="Code" value="${param.code}" />
    <input type="text" name="codeName" placeholder="CodeName" value="${param.codeName}" />
    <button type="submit">검색</button>
</form> -->

<!-- 엑셀 다운로드 -->
<!-- <h2>엑셀 기능</h2>
<form method="get" action="/tms/downloadAllcat">
    <button type="submit">전체 엑셀 다운로드</button>
</form>
<form method="get" action="/tms/downloadFilteredcat">
    <input type="hidden" name="stageType" value="${param.stageType}" />
    <input type="hidden" name="code" value="${param.code}" />
    <input type="hidden" name="codeName" value="${param.codeName}" />
    <button type="submit">검색된 결과 엑셀 다운로드</button>
</form> -->

<!-- 엑셀 업로드 -->
<!-- <h2>엑셀 업로드</h2>
<form method="post" action="/tms/api/catupload" enctype="multipart/form-data">
    <input type="file" name="file" accept=".xlsx" />
    <button type="submit">업로드</button>
</form> -->

<!-- 카테고리 코드 테이블 -->
<!-- <table>
    <thead>
        <tr>
            <th>StageType</th>
            <th>Code</th>
            <th>CodeName</th>
            <th class="center">수정</th>
            <th class="center">삭제</th>
        </tr>
    </thead>
    <tbody>
        <c:forEach var="categoryCode" items="${categoryCodes}">
            <tr>
                <td>${categoryCode.stageType}</td>
                <td>${categoryCode.code}</td>
                <td>${categoryCode.codeName}</td>
                <td class="center">
                    <form action="/tms/catmodify" method="post">
                        <input type="hidden" name="code" value="${categoryCode.code}" />
                        <button type="submit">수정</button>
                    </form>
                </td>
                <td class="center">
                    <form action="/tms/deletecat" method="post">
                        <input type="hidden" name="code" value="${categoryCode.code}" />
                        <button type="submit">삭제</button>
                    </form>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty categoryCodes}">
            <tr>
                <td colspan="5" class="center">조회된 카테고리 코드가 없습니다.</td>
            </tr>
        </c:if>
    </tbody>
</table> -->

<!-- 등록 폼 -->
<!-- <h2>카테고리 코드 등록</h2>
<form action="/tms/catwrite" method="post">
    <label for="stageType">StageType:</label>
    <select id="stageType" name="stageType">
        <option value="대">대</option>
        <option value="중">중</option>
    </select><br/>

 <label for="parentCode">Parent Code:</label>
    <input type="text" id="parentCode" name="parentCode" /><br/>

    <label for="code">Code:</label>
    <input type="text" id="code" name="code" required /><br/>
    
    <label for="codeName">CodeName:</label>
    <input type="text" id="codeName" name="codeName" required /><br/>
    
    <button type="submit">등록</button> 
 </form> -->

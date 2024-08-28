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
          <div class="download-box">
            <form id="downloadAllCategoryCodeForm" action="catdownloadAll" method="get" class="flex-box align-center">
              <button type="submit" id="downloadAllCategoryCodeButton" class="excel-button">
                <img src="../../resources/images/download_icon.png" />전체자료 다운로드
              </button>
              전체 데이터를 주별로 다운로드 받아 실적 집계로 활용
            </form>

            <form
              id="downloadFilteredCategoryCodeForm"
              action="catdownloadFiltered"
              method="get"
              id="downloadFilteredForm"
              class="flex-box align-center"
            >
              <!-- 숨겨진 input 필드 -->
              <input type="hidden" id="parentCodeForDownload" name="parentCode" />
              <input type="hidden" id="codeForDownload" name="code" />
              <input type="hidden" id="codeNameForDownload" name="codeName" />

              <button type="submit" id="downloadFilteredCategoryCodeButton" class="excel-button">
                <img src="../../resources/images/download_icon.png" />
                조회결과 다운로드
              </button>
              조회조건에 의한 결과만 다운로드
            </form>
          </div>

          <div class="flex-box justify-end">
            <button type="button" class="cancel-button" id="closeCategoryCodeFileDownloadModalButton">닫기</button>
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

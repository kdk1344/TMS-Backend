<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="ko">
  <head>
    <%@ include file="./common.jsp" %>
    <meta charset="UTF-8" />
    <link rel="stylesheet" type="text/css" href="../../resources/css/defect.css" />

    <title>TMS 결함진행 관리</title>
    <script type="module" src="../../resources/js/defect.js"></script>
  </head>

  <body>
    <header class="header">
      <!-- 공통 헤더 정보 동적으로 삽입-->
    </header>

    <main class="content">
      <h1 class="page-title">결함현황</h1>

      <!-- File Upload Modal -->
      <div id="defectFileUploadModal" class="modal">
        <!-- Modal content -->
        <div class="modal-content">
          <h2>엑셀 업로드</h2>
          <div class="excel-template-download-box">
            <pre>
엑셀로 업로드하여 다수의 목록을 한번에 입력할 수 있습니다. 
엑셀 양식에 데이터를 입력하여 업로드하세요. (파일 형식: xls, xlsx)</pre
            >
            <form id="downloadTemplateForm" action="defectsexampleexcel" method="get" class="flex-box align-center">
              <button type="submit" class="excel-button">
                <img src="../../resources/images/download_icon.png" />엑셀 양식 다운로드
              </button>
            </form>
          </div>

          <!-- 파일 선택 input -->
          <input type="file" id="uploadDefectFileInput" class="excel-upload-input" name="file" accept=".xlsx, .xls" />

          <div class="flex-box justify-end">
            <button type="button" class="cancel-button" id="closeDefectFileUploadModalButton">취소</button>
            <button type="button" class="save-button" id="uploadDefectFileButton">저장</button>
          </div>
        </div>
      </div>

      <!-- File Download Modal -->
      <div id="defectFileDownloadModal" class="modal">
        <!-- Modal content -->
        <div class="modal-content">
          <h2>엑셀 다운로드</h2>
          <div class="download-box">
            <form id="downloadAllDefectForm" action="defectdownloadAll" method="get" class="flex-box align-center">
              <button type="submit" id="downloadAllDefectButton" class="excel-button">
                <img src="../../resources/images/download_icon.png" />전체자료 다운로드
              </button>
              전체 데이터를 주별로 다운로드 받아 실적 집계로 활용
            </form>

            <form
              id="downloadFilteredDefectForm"
              action="defectdownloadFiltered"
              method="get"
              id="downloadFilteredForm"
              class="flex-box align-center"
            >
              <!-- 숨겨진 input 필드 -->
              <input type="hidden" id="testStageForDownload" name="testStage" />
              <input type="hidden" id="majorCategoryForDownload" name="majorCategory" />
              <input type="hidden" id="subCategoryForDownload" name="subCategory" />
              <input type="hidden" id="defectSeverityForDownload" name="defectSeverity" />
              <input type="hidden" id="defectRegistrarForDownload" name="defectRegistrar" />
              <input type="hidden" id="defectHandlerForDownload" name="defectHandler" />
              <input type="hidden" id="plForDownload" name="pl" />
              <input type="hidden" id="defectNumberForDownload" name="defectNumber" />
              <input type="hidden" id="defectStatusForDownload" name="defectStatus" />

              <button type="submit" id="downloadFilteredDefectButton" class="excel-button">
                <img src="../../resources/images/download_icon.png" />
                조회결과 다운로드
              </button>
              조회조건에 의한 결과만 다운로드
            </form>
          </div>

          <div class="flex-box justify-end">
            <button type="button" class="cancel-button" id="closeDefectFileDownloadModalButton">닫기</button>
          </div>
        </div>
      </div>

      <!-- 결함현황 조회 필터링 폼 -->
      <form id="defectFilterForm">
        <div class="filter-container">
          <div class="form-group">
            <label for="testStageForFilter">테스트 단계</label>
            <select id="testStageForFilter" name="testStage">
              <option value="" selected>전체</option>
            </select>
          </div>

          <div class="form-group">
            <label for="majorCategoryForFilter">업무 대분류</label>
            <select id="majorCategoryForFilter" name="majorCategory">
              <option value="" selected>전체</option>
            </select>
          </div>

          <div class="form-group">
            <label for="subCategoryForFilter">업무 중분류</label>
            <select id="subCategoryForFilter" name="subCategory">
              <option value="" selected>전체</option>
            </select>
          </div>

          <div class="form-group">
            <label for="defectSeverityForFilter">결함심각도</label>
            <select id="defectSeverityForFilter" name="defectSeverity">
              <option value="" selected>전체</option>
            </select>
          </div>

          <div class="form-group">
            <select id="defectRoleKeySelect" name="defectRoleKey">
              <option value="defectRegistrar" selected>결함등록자</option>
              <option value="defectHandler">결함조치자</option>
              <option value="pl">PL</option>
            </select>
            <input type="text" id="defectRoleValueInput" name="defectRoleValue" placeholder="이름" />
          </div>

          <div class="form-group">
            <label for="defectNumberForFilter">결함번호</label>
            <input type="text" id="defectNumberForFilter" name="defectNumber" pattern="\d{9}" maxlength="10" />
          </div>

          <div class="form-group">
            <label for="defectStatusForFilter">처리상태</label>
            <select id="defectStatusForFilter" name="defectStatus">
              <option value="" selected>전체</option>
            </select>
          </div>
        </div>

        <div class="flex-box justify-end">
          <button type="reset" id="resetButton" class="reset-button">초기화</button>
          <button type="submit" id="searchDefectButton">조회</button>
        </div>
      </form>

      <!-- 버튼 그룹 -->
      <div class="button-group">
        <div class="flex-box">
          <button id="goDefectRegisterPageButton">등록</button>
          <button id="deleteDefectButton" class="delete-button">삭제</button>
        </div>
        <div class="flex-box">
          <button id="openDefectFileUploadModalButton" class="excel-button">
            <img src="../../resources/images/upload_icon.png" />엑셀 업로드
          </button>
          <button id="openDefectFileDownloadModalButton" class="excel-button">
            <img src="../../resources/images/download_icon.png" />엑셀 다운로드
          </button>
        </div>
      </div>

      <!-- 개발진행 목록 테이블 -->
      <div class="table-container">
        <table id="defectTable">
          <thead>
            <tr>
              <th><input type="checkbox" id="selectAllDefectCheckbox" /></th>
              <th class="defect-number">결함번호</th>
              <th class="sub-category">업무 중분류</th>
              <th class="program-id">프로그램ID</th>
              <th class="program-name">프로그램명</th>
              <th class="defect-registrar">결함등록자</th>
              <th class="defect-reg-date">결함등록일</th>
              <th class="defect-severity">결함심각도</th>
              <th class="defect-description">결함내용</th>
              <th class="defect-handler">결함조치자</th>
              <th class="defect-scheduled-date">조치<br />예정일</th>
              <th class="defect-completion-date">결함<br />조치일</th>
              <th class="pl">PL</th>
              <th class="pl-confirm-date">PL<br />확인일</th>
              <th class="defect-reg-confirm-date">등록자<br />확인일</th>
              <th class="defect-status">처리상태</th>
            </tr>
          </thead>
          <tbody id="defectTableBody">
            <!-- 개발진행 정보 동적으로 삽입-->
          </tbody>
        </table>
      </div>

      <!-- 페이지네이션 -->
      <div id="defectPagination" class="pagination">
        <!-- 페이지네이션 버튼 동적으로 삽입 -->
      </div>
    </main>
  </body>
</html>

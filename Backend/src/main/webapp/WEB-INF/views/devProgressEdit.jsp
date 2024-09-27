<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="ko">
  <head>
    <%@ include file="./common.jsp" %>
    <meta charset="UTF-8" />
    <link rel="stylesheet" type="text/css" href="../../resources/css/devProgressEdit.css" />

    <title>TMS 개발진행 관리 - 프로그램 개발 수정</title>
    <script type="module" src="../../resources/js/devProgressEdit.js"></script>
  </head>

  <body>
    <header class="header">
      <!-- 공통 헤더 정보 동적으로 삽입-->
    </header>

    <main class="content">
      <h1 class="page-title">프로그램 개발 수정</h1>

      <!-- 결함수정 대상 검색 모달 -->
      <div id="defectToEditSearchModal" class="modal">
        <div class="modal-content">
          <div class="flex-box justify-between">
            <h2>수정대상 결함번호 조회</h2>
            <button type="button" id="closeDefectToEditSearchModalButton" class="modal-close-button">
              <img src="../../resources/images/close_icon.png" alt="닫기" />
            </button>
          </div>

          <!-- 결함 필터링 -->
          <form id="defectToEditFilterForm">
            <div class="form-group-row">
              <div class="form-group">
                <label for="testStageForDefectToEdit">테스트 단계</label>
                <select id="testStageForDefectToEdit" name="testStage">
                  <option value="" selected>전체</option>
                </select>
              </div>

              <div class="form-group">
                <label for="testIdForDefectToEdit">테스트ID</label>
                <input id="testIdForDefectToEdit" name="testId" />
              </div>

              <div class="form-group">
                <label for="programIdForDefectToEdit">프로그램ID</label>
                <input id="programIdForDefectToEdit" name="programId" />
              </div>

              <div class="form-group">
                <label for="programNameForDefectToEdit">프로그램명</label>
                <input id="programNameForDefectToEdit" name="programName" />
              </div>
            </div>

            <button>조회</button>
          </form>
          <div id="defectToEditTableWrapper" class="hidden-scroll">
            <table id="defectToEditTable">
              <thead>
                <tr>
                  <th>번호</th>
                  <th>테스트ID</th>
                  <th>결함번호</th>
                  <th>프로그램ID</th>
                  <th>프로그램명</th>
                  <th>결함등록자</th>
                  <th>결함유형</th>
                  <th>결함내용</th>
                </tr>
              </thead>
              <tbody id="defectToEditTableBody" class="hidden-scroll"></tbody>
            </table>
          </div>
        </div>
      </div>

      <form id="devProgressEditForm">
        <!-- 프로그램 개발 Fieldset -->
        <fieldset class="important-box">
          <div class="input-container">
            <div class="form-group-container">
              <div class="form-group">
                <label for="majorCategory">업무 대분류<span class="required-indicator">*</span></label>
                <select id="majorCategory" name="majorCategory" required>
                  <option value="" disabled selected>대분류를 선택해 주세요.</option>
                </select>
              </div>

              <div class="form-group">
                <label for="subCategory">업무 중분류<span class="required-indicator">*</span></label>
                <select id="subCategory" name="subCategory" required>
                  <option value="" disabled selected>중분류를 선택해 주세요.</option>
                </select>
              </div>

              <div class="form-group">
                <label for="minorCategory">업무 소분류</label>
                <input id="minorCategory" name="minorCategory" placeholder="소분류를 입력해 주세요." />
              </div>

              <div class="form-group">
                <label for="programType">프로그램 구분<span class="required-indicator">*</span></label>
                <select id="programType" name="programType" required>
                  <option value="" disabled selected>구분을 선택해 주세요.</option>
                </select>
              </div>

              <div class="form-group">
                <label for="programDetailType">프로그램 상세구분<span class="required-indicator">*</span></label>
                <select id="programDetailType" name="programDetailType" required>
                  <option value="" disabled selected>상세구분을 선택해 주세요.</option>
                </select>
              </div>
            </div>

            <div class="form-group-container">
              <div class="form-group">
                <label for="programId">프로그램 ID<span class="required-indicator">*</span></label>
                <input id="programId" name="programId" required placeholder="프로그램 ID를 입력해 주세요." />
              </div>

              <div class="form-group">
                <label for="programName">프로그램명<span class="required-indicator">*</span></label>
                <input id="programName" name="programName" required placeholder="프로그램명을 입력해 주세요." />
              </div>

              <div class="form-group">
                <label for="className">클래스명</label>
                <input id="className" name="className" placeholder="클래스명을 입력해 주세요." />
              </div>

              <div class="form-group">
                <label for="screenId">화면 ID</label>
                <input id="screenId" name="screenId" placeholder="화면 ID를 입력해 주세요." />
              </div>

              <div class="form-group">
                <label for="screenName">화면명</label>
                <input id="screenName" name="screenName" placeholder="화면명을 입력해 주세요." />
              </div>
            </div>

            <div class="form-group-container">
              <div class="form-group">
                <label for="developer">개발자<span class="required-indicator">*</span></label>
                <input id="developer" name="developer" required placeholder="이름을 입력해 주세요." />
              </div>

              <div class="form-group">
                <label for="priority">우선순위<span class="required-indicator">*</span></label>
                <select id="priority" name="priority" required>
                  <option value="" disabled selected>우선순위를 선택해 주세요.</option>
                </select>
              </div>

              <div class="form-group">
                <label for="difficulty">난이도<span class="required-indicator">*</span></label>
                <select id="difficulty" name="difficulty" required>
                  <option value="" disabled selected>난이도를 선택해 주세요.</option>
                </select>
              </div>

              <div class="form-group">
                <label for="programStatus">프로그램 상태<span class="required-indicator">*</span></label>
                <select id="programStatus" name="programStatus" required>
                  <option value="" disabled selected>상태를 선택해 주세요.</option>
                </select>
              </div>

              <div class="form-group">
                <label for="reqId">요구사항 ID</label>
                <input id="reqId" name="reqId" placeholder="요구사항 ID를 입력해 주세요." />
              </div>
            </div>

            <div class="form-group-container">
              <div class="form-group">
                <label for="deletionHandler">삭제처리자</label>
                <input id="deletionHandler" name="deletionHandler" placeholder="이름을 입력해 주세요." />
              </div>

              <div class="form-group">
                <label for="deletionDate">삭제처리일</label>
                <input id="deletionDate" name="deletionDate" type="date" />
              </div>

              <div class="form-group">
                <label for="deletionReason">삭제처리 사유</label>
                <input id="deletionReason" name="deletionReason" placeholder="사유를 입력해 주세요." />
              </div>

              <div class="small-form-group">
                <label for="plannedStartDate">개발착수예정일<span class="required-indicator">*</span></label>
                <input id="plannedStartDate" name="plannedStartDate" type="date" required />
                <span>-</span>
                <input id="plannedEndDate" name="plannedEndDate" type="date" required />
              </div>

              <div class="small-form-group">
                <label for="actualStartDate">개발종료일</label>
                <input id="actualStartDate" name="actualStartDate" type="date" />
                <span>-</span>
                <input id="actualEndDate" name="actualEndDate" type="date" />
              </div>
            </div>
          </div>
        </fieldset>

        <div class="file-box">
          <fieldset>
            <div class="file-box-top">
              <div class="flex-box">
                <legend>단위테스트 증적 첨부+</legend>
                <button type="button" class="file-button" id="fileSelectButton">파일 선택</button>
              </div>
              <div class="form-group">
                <label for="devTestEndDate">개발자 단위테스트 종료일</label>
                <input id="devTestEndDate" name="devtestendDate" type="date" />
              </div>
            </div>

            <div class="file-preview-container">
              <input type="file" id="fileInput" class="hidden" name="file" multiple />
              <div id="fileOutput" class="file-preview"></div>
            </div>
          </fieldset>
        </div>

        <div class="fieldset-container">
          <fieldset>
            <legend>▣ PL 단위테스트 결과</legend>
            <div class="input-container">
              <div class="form-group">
                <label for="pl">PL<span class="required-indicator">*</span></label>
                <input id="pl" name="pl" required placeholder="이름을 입력해 주세요." />
              </div>

              <div class="form-group">
                <label for="plTestScdDate">테스트 예정일</label>
                <input id="plTestScdDate" name="plTestScdDate" type="date" />
              </div>

              <div class="form-group">
                <label for="plTestCmpDate">테스트 완료일</label>
                <input id="plTestCmpDate" name="plTestCmpDate" type="date" />
              </div>

              <div class="form-group">
                <label for="plTestResult">테스트 결과</label>
                <select id="plTestResult" name="plTestResult">
                  <option value="" disabled selected>테스트 결과를 선택해 주세요.</option>
                </select>
              </div>

              <div class="form-group">
                <label for="plTestNotes">테스트 의견</label>
                <textarea
                  id="plTestNotes"
                  name="plTestNotes"
                  class="custom-scroll"
                  placeholder="테스트 의견을 입력해 주세요."
                ></textarea>
              </div>
            </div>
          </fieldset>

          <fieldset>
            <legend>▣ 제3자 기능테스트 결과</legend>
            <div class="input-container">
              <div class="form-group">
                <label for="thirdPartyTestMgr">테스트 담당자</label>
                <input id="thirdPartyTestMgr" name="thirdPartyTestMgr" placeholder="이름을 입력해 주세요." />
              </div>

              <div class="form-group">
                <label for="thirdPartyTestDate">테스트 예정일</label>
                <input id="thirdPartyTestDate" name="thirdPartyTestDate" type="date" />
              </div>

              <div class="form-group">
                <label for="thirdPartyConfirmDate">테스트 완료일</label>
                <input id="thirdPartyConfirmDate" name="thirdPartyConfirmDate" type="date" />
              </div>

              <div class="form-group">
                <label for="thirdTestResult">테스트 결과</label>
                <select id="thirdTestResult" name="thirdTestResult">
                  <option value="" disabled selected>테스트 결과를 선택해 주세요.</option>
                </select>
              </div>

              <div class="form-group">
                <label for="thirdPartyTestNotes">테스트 의견</label>
                <textarea
                  id="thirdPartyTestNotes"
                  name="thirdPartyTestNotes"
                  placeholder="테스트 의견을 입력해 주세요."
                  class="custom-scroll"
                ></textarea>
              </div>

              <div class="form-group">
                <label for="thirdPartyDefect">결함발생/조치완료</label>
                <input id="thirdPartyDefect" name="thirdPartyDefect" readonly disabled />
              </div>
            </div>
          </fieldset>

          <fieldset>
            <legend>▣ 고객 IT 단위테스트 결과</legend>
            <div class="input-container">
              <div class="form-group">
                <label for="itMgr">고객 IT 담당자</label>
                <input id="itMgr" name="itMgr" placeholder="이름을 입력해 주세요." />
              </div>

              <div class="form-group">
                <label for="itTestDate">테스트 예정일</label>
                <input id="itTestDate" name="itTestDate" type="date" />
              </div>

              <div class="form-group">
                <label for="itConfirmDate">테스트 완료일</label>
                <input id="itConfirmDate" name="itConfirmDate" type="date" />
              </div>

              <div class="form-group">
                <label for="itTestResult">테스트 결과</label>
                <select id="itTestResult" name="itTestResult">
                  <option value="" disabled selected>테스트 결과를 선택해 주세요.</option>
                </select>
              </div>

              <div class="form-group">
                <label for="itTestNotes">테스트 의견</label>
                <textarea
                  id="itTestNotes"
                  name="itTestNotes"
                  placeholder="테스트 의견을 입력해 주세요."
                  class="custom-scroll"
                ></textarea>
              </div>

              <div class="form-group">
                <label for="itDefect">결함발생/조치완료</label>
                <input id="itDefect" name="itDefect" readonly disabled />
              </div>
            </div>
          </fieldset>

          <fieldset>
            <legend>▣ 고객 현업 단위테스트 결과</legend>
            <div class="input-container">
              <div class="form-group">
                <label for="busiMgr">현업 담당자</label>
                <input id="busiMgr" name="busiMgr" placeholder="이름을 입력해 주세요." />
              </div>

              <div class="form-group">
                <label for="busiTestDate">테스트 예정일</label>
                <input id="busiTestDate" name="busiTestDate" type="date" />
              </div>

              <div class="form-group">
                <label for="busiConfirmDate">테스트 완료일</label>
                <input id="busiConfirmDate" name="busiConfirmDate" type="date" />
              </div>

              <div class="form-group">
                <label for="busiTestResult">테스트 결과</label>
                <select id="busiTestResult" name="busiTestResult">
                  <option value="" disabled selected>테스트 결과를 선택해 주세요.</option>
                </select>
              </div>

              <div class="form-group">
                <label for="busiTestNotes">테스트 의견</label>
                <textarea
                  id="busiTestNotes"
                  name="busiTestNotes"
                  placeholder="테스트 의견을 입력해 주세요."
                  class="custom-scroll"
                ></textarea>
              </div>

              <div class="form-group">
                <label for="busiDefect">결함발생/조치완료</label>
                <input id="busiDefect" name="busiDefect" readonly disabled />
              </div>
            </div>
          </fieldset>
        </div>

        <div class="input-container-row">
          <div class="form-group">
            <label for="devStatus">개발진행상태</label>
            <input id="devStatus" name="devStatus" readonly disabled />
          </div>

          <div class="form-group">
            <label for="lastModifiedDate">변경일자</label>
            <input id="lastModifiedDate" name="lastModifiedDate" readonly disabled />
          </div>

          <div class="form-group">
            <label for="lastModifier">변경자</label>
            <input id="lastModifier" name="lastModifier" readonly disabled />
          </div>
        </div>

        <div class="button-container">
          <button id="goDefectRegisterPageButton" class="defect-button" type="button">결함등록</button>
          <button id="openDefectToEditSearchModalButton" class="defect-button" type="button">결함수정</button>
          <button id="goBackButton" class="cancel-button" type="button">취소</button>
          <button class="save-button">저장</button>
        </div>
      </form>
    </main>
  </body>
</html>

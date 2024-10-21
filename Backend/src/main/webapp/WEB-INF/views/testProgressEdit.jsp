<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="ko">
  <head>
    <%@ include file="./common.jsp" %>
    <meta charset="UTF-8" />
    <link
      rel="stylesheet"
      type="text/css"
      href="${pageContext.request.contextPath}/resources/css/testProgressEdit.css"
    />

    <title>TMS 테스트진행관리 - 테스트 시나리오 수정</title>
    <script type="module" src="${pageContext.request.contextPath}/resources/js/testProgressEdit.js"></script>
  </head>

  <body>
    <header class="header">
      <!-- 공통 헤더 정보 동적으로 삽입-->
    </header>

    <main class="content">
      <h1 class="page-title">테스트 시나리오 수정</h1>

      <!-- 프로그램 검색 모달 -->
      <div id="programSearchModal" class="modal">
        <div class="modal-content">
          <div class="flex-box justify-between">
            <h2>프로그램 조회</h2>
            <button type="button" id="closeProgramSearchModalButton" class="modal-close-button">
              <img src="${pageContext.request.contextPath}/resources/images/close_icon.png" alt="닫기" />
            </button>
          </div>

          <!-- 프로그램 필터링 -->
          <form id="programFilterForm">
            <div class="form-group-row">
              <div class="form-group">
                <label for="programTypeForProgram">프로그램 구분</label>
                <select id="programTypeForProgram" name="programType">
                  <option value="" selected>전체</option>
                </select>
              </div>

              <div class="form-group">
                <label for="developerForProgram">개발자</label>
                <input id="developerForProgram" name="developer" />
              </div>

              <div class="form-group">
                <label for="programIdForProgram">프로그램ID</label>
                <input id="programIdForProgram" name="programId" />
              </div>

              <div class="form-group">
                <label for="programNameForProgram">프로그램명</label>
                <input id="programNameForProgram" name="programName" />
              </div>

              <div class="form-group">
                <label for="screenIdForProgram">화면ID</label>
                <input id="screenIdForProgram" name="screenId" />
              </div>
            </div>

            <button>조회</button>
          </form>
          <div id="programTableWrapper" class="hidden-scroll">
            <table id="programTable">
              <thead>
                <tr>
                  <th>번호</th>
                  <th>프로그램 구분</th>
                  <th>프로그램ID</th>
                  <th>프로그램명</th>
                  <th>상태</th>
                  <th>개발자</th>
                  <th>PL</th>
                </tr>
              </thead>
              <tbody id="programTableBody" class="hidden-scroll"></tbody>
            </table>
          </div>
        </div>
      </div>

      <!-- 화면 검색 모달 -->
      <div id="screenSearchModal" class="modal">
        <div class="modal-content">
          <div class="flex-box justify-between">
            <h2>화면 조회</h2>
            <button type="button" id="closeScreenSearchModalButton" class="modal-close-button">
              <img src="${pageContext.request.contextPath}/resources/images/close_icon.png" alt="닫기" />
            </button>
          </div>

          <div id="screenTableWrapper" class="hidden-scroll">
            <table id="screenTable">
              <thead>
                <tr>
                  <th>번호</th>
                  <th>화면ID</th>
                  <th>화면명</th>
                </tr>
              </thead>
              <tbody id="screenTableBody" class="hidden-scroll"></tbody>
            </table>
          </div>
        </div>
      </div>

      <!-- 결함수정 대상 검색 모달 -->
      <div id="editableDefectSearchModal" class="modal">
        <div class="modal-content">
          <div class="flex-box justify-between">
            <h2>수정대상 결함번호 조회</h2>
            <button type="button" id="closeEditableDefectSearchModalButton" class="modal-close-button">
              <img src="${pageContext.request.contextPath}/resources/images/close_icon.png" alt="닫기" />
            </button>
          </div>

          <!-- 결함 필터링 -->
          <form id="editableDefectFilterForm">
            <div class="form-group-row">
              <div class="form-group">
                <label for="testStageForEditableDefect">테스트 단계</label>
                <select id="testStageForEditableDefect" name="testStage" class="readonly">
                  <option value="" selected>전체</option>
                </select>
              </div>

              <div class="form-group">
                <label for="testIdForEditableDefect">테스트ID</label>
                <input id="testIdForEditableDefect" name="testId" readonly />
              </div>

              <div class="form-group">
                <label for="programIdForEditableDefect">프로그램ID</label>
                <input id="programIdForEditableDefect" name="programId" />
              </div>

              <div class="form-group">
                <label for="programNameForEditableDefect">프로그램명</label>
                <input id="programNameForEditableDefect" name="programName" />
              </div>
            </div>

            <button>조회</button>
          </form>
          <div id="editableDefectTableWrapper" class="hidden-scroll">
            <table id="editableDefectTable">
              <thead>
                <tr>
                  <th>번호</th>
                  <th>테스트 단계</th>
                  <th>테스트ID</th>
                  <th>결함번호</th>
                  <th>프로그램ID</th>
                  <th>프로그램명</th>
                  <th>결함등록자</th>
                  <th>결함유형</th>
                  <th>결함내용</th>
                </tr>
              </thead>
              <tbody id="editableDefectTableBody" class="hidden-scroll"></tbody>
            </table>
          </div>
        </div>
      </div>

      <form id="testProgressEditForm">
        <fieldset>
          <div class="form-group-container">
            <div class="form-group-row">
              <div class="form-group">
                <label for="testStage">테스트 단계<span class="required-indicator">*</span></label>
                <select id="testStage" name="testStage" required class="readonly"></select>
              </div>

              <div class="form-group">
                <label for="majorCategory">업무 대분류<span class="required-indicator">*</span></label>
                <select id="majorCategory" name="majorCategory" required class="readonly"></select>
              </div>

              <div class="form-group">
                <label for="subCategory">업무 중분류<span class="required-indicator">*</span></label>
                <select id="subCategory" name="subCategory" required class="readonly"></select>
              </div>

              <div class="form-group">
                <label for="minorCategory">업무 소분류</label>
                <input id="minorCategory" name="minorCategory" readonly />
              </div>
            </div>
          </div>
        </fieldset>

        <fieldset>
          <div class="legend-container">
            <legend>▣ 테스트 시나리오/케이스 설계 기본 정보</legend>
            <div class="form-group">
              <label for="testStatus">테스트 진행상태</label>
              <input id="testStatus" name="testStatus" readonly />
            </div>
          </div>

          <div class="form-group-container">
            <div class="form-group-row">
              <div class="form-group">
                <label for="testId">테스트ID<span class="required-indicator">*</span></label>
                <input id="testId" name="testId" required placeholder="테스트ID" tabindex="1" autofocus readonly />
              </div>

              <div class="form-group">
                <label for="testStepName">스텝명<span class="required-indicator">*</span></label>
                <input
                  id="testStepName"
                  name="testStepName"
                  required
                  placeholder="테스트 스텝명"
                  tabindex="4"
                  readonly
                />
              </div>

              <div class="form-group">
                <label for="programId">프로그램ID<span class="required-indicator">*</span></label>
                <div class="search-input-wrapper">
                  <input id="programId" name="programId" required readonly />

                  <button type="button" class="search-button" id="programSearchButton" disabled>
                    <img
                      src="${pageContext.request.contextPath}/resources/images/search_icon.png"
                      alt="프로그램 검색"
                    />
                  </button>
                </div>
              </div>

              <div class="form-group">
                <label for="pl">PL<span class="required-indicator">*</span></label>
                <input id="pl" name="pl" required readonly />
              </div>
            </div>

            <div class="form-group-row">
              <div class="form-group">
                <label for="testScenarioName">시나리오명<span class="required-indicator">*</span></label>
                <input
                  id="testScenarioName"
                  name="testScenarioName"
                  required
                  placeholder="테스트 시나리오명"
                  tabindex="2"
                  readonly
                />
              </div>

              <div class="form-group">
                <label for="screenId">화면ID<span class="required-indicator">*</span></label>
                <div class="search-input-wrapper">
                  <input id="screenId" name="screenId" required readonly />

                  <button type="button" class="search-button" id="screenSearchButton" disabled>
                    <img src="${pageContext.request.contextPath}/resources/images/search_icon.png" alt="화면 검색" />
                  </button>
                </div>
              </div>

              <div class="form-group">
                <label for="programName">프로그램명</label>
                <input id="programName" name="programName" readonly />
              </div>

              <div class="form-group">
                <label for="developer">개발자<span class="required-indicator">*</span></label>
                <input id="developer" name="developer" required readonly />
              </div>
            </div>

            <div class="form-group-row">
              <div class="form-group">
                <label for="testCaseName">케이스명<span class="required-indicator">*</span></label>
                <input
                  id="testCaseName"
                  name="testCaseName"
                  required
                  placeholder="테스트 케이스명"
                  tabindex="3"
                  readonly
                />
              </div>

              <div class="form-group">
                <label for="screenName">화면명</label>
                <input id="screenName" name="screenName" readonly />
              </div>

              <div class="form-group">
                <label for="screenMenuPath">화면경로</label>
                <input id="screenMenuPath" name="screenMenuPath" readonly />
              </div>

              <!-- @todo 현재 테스트 진핸현황 DB에 요구사항ID를 저장하는 컬럼 없으므로 화면엔 보이되 서버에 전송X, 나중에 사용하게 되면 disabled 제거 -->
              <div class="form-group">
                <label for="reqId">요구사항ID</label>
                <input id="reqId" name="reqId" readonly disabled />
              </div>
            </div>

            <div class="form-group-row">
              <div class="form-group">
                <label for="executeProcedure">수행절차<span class="required-indicator">*</span></label>
                <textarea
                  id="executeProcedure"
                  name="executeProcedure"
                  required
                  class="custom-scroll"
                  placeholder="테스트 수행절차를 입력해 주세요."
                  tabindex="5"
                  readonly
                ></textarea>
              </div>
            </div>

            <div class="form-group-row">
              <div class="form-group">
                <label for="preConditions">사전조건</label>
                <input id="preConditions" name="preConditions" placeholder="테스트 사전조건" tabindex="6" readonly />
              </div>

              <div class="form-group">
                <label for="inputData">입력 데이터<span class="required-indicator">*</span></label>
                <input
                  id="inputData"
                  name="inputData"
                  required
                  placeholder="테스트 입력 데이터"
                  tabindex="7"
                  readonly
                />
              </div>

              <div class="form-group">
                <label for="expectedResult">예상결과<span class="required-indicator">*</span></label>
                <input
                  id="expectedResult"
                  name="expectedResult"
                  required
                  placeholder="테스트 예상결과"
                  tabindex="8"
                  readonly
                />
              </div>

              <div class="form-group">
                <label for="actualResult">실제결과</label>
                <input id="actualResult" name="actualResult" placeholder="테스트 실제결과" tabindex="9" readonly />
              </div>
            </div>
          </div>
        </fieldset>

        <div class="fieldset-container">
          <fieldset>
            <div class="legend-container">
              <legend>▣ 수행사 테스트</legend>
              <div class="defect-box" id="execCompanyDefect" name="execCompanyDefect">결함0 / 조치0</div>
            </div>

            <div class="form-group-container">
              <div class="form-group">
                <label for="execCompanyMgr">수행사 테스터<span class="required-indicator">*</span></label>
                <input
                  id="execCompanyMgr"
                  name="execCompanyMgr"
                  required
                  placeholder="이름을 입력해 주세요."
                  tabindex="10"
                  readonly
                />
              </div>

              <div class="form-group">
                <label for="execCompanyTestDate">테스트 예정일</label>
                <input id="execCompanyTestDate" name="execCompanyTestDate" type="date" readonly />
              </div>

              <div class="form-group">
                <label for="execCompanyConfirmDate">테스트 완료일</label>
                <input id="execCompanyConfirmDate" name="execCompanyConfirmDate" type="date" readonly />
              </div>

              <div class="form-group">
                <label for="execCompanyTestResult">테스트 결과</label>
                <select id="execCompanyTestResult" name="execCompanyTestResult" class="readonly">
                  <option value="" disabled selected>테스트 결과를 선택해 주세요.</option>
                </select>
              </div>

              <div class="form-group">
                <label for="execCompanyTestNotes">테스트 의견</label>
                <textarea
                  id="execCompanyTestNotes"
                  name="execCompanyTestNotes"
                  class="custom-scroll"
                  placeholder="테스트 의견을 입력해 주세요."
                  tabindex="11"
                  readonly
                ></textarea>
              </div>
            </div>
          </fieldset>

          <fieldset>
            <div class="legend-container">
              <legend>▣ 제3자 테스트</legend>
              <div class="defect-box" id="thirdPartyDefect" name="thirdPartyDefect">결함0 / 조치0</div>
            </div>

            <div class="form-group-container">
              <div class="form-group">
                <label for="thirdPartyTestMgr">제3자 테스터</label>
                <input
                  id="thirdPartyTestMgr"
                  name="thirdPartyTestMgr"
                  placeholder="이름을 입력해 주세요."
                  tabindex="12"
                  readonly
                />
              </div>

              <div class="form-group">
                <label for="thirdPartyTestDate">테스트 예정일</label>
                <input id="thirdPartyTestDate" name="thirdPartyTestDate" type="date" readonly />
              </div>

              <div class="form-group">
                <label for="thirdPartyConfirmDate">테스트 완료일</label>
                <input id="thirdPartyConfirmDate" name="thirdPartyConfirmDate" type="date" readonly />
              </div>

              <div class="form-group">
                <label for="thirdTestResult">테스트 결과</label>
                <select id="thirdTestResult" name="thirdTestResult" class="readonly">
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
                  tabindex="13"
                  readonly
                ></textarea>
              </div>
            </div>
          </fieldset>

          <fieldset>
            <div class="legend-container">
              <legend>▣ 고객IT 테스트</legend>
              <div class="defect-box" id="itDefect" name="itDefect">결함0 / 조치0</div>
            </div>

            <div class="form-group-container">
              <div class="form-group">
                <label for="itMgr">고객 IT 담당자</label>
                <input id="itMgr" name="itMgr" placeholder="이름을 입력해 주세요." tabindex="14" readonly />
              </div>

              <div class="form-group">
                <label for="itTestDate">테스트 시작일</label>
                <input id="itTestDate" name="itTestDate" type="date" readonly />
              </div>

              <div class="form-group">
                <label for="itConfirmDate">테스트 완료일</label>
                <input id="itConfirmDate" name="itConfirmDate" type="date" readonly />
              </div>

              <div class="form-group">
                <label for="itTestResult">테스트 결과</label>
                <select id="itTestResult" name="itTestResult" class="readonly">
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
                  tabindex="15"
                  readonly
                ></textarea>
              </div>
            </div>
          </fieldset>

          <fieldset>
            <div class="legend-container">
              <legend>▣ 고객현업 테스트</legend>
              <div class="defect-box" id="busiDefect" name="busiDefect">결함0/조치0</div>
            </div>

            <div class="form-group-container">
              <div class="form-group">
                <label for="busiMgr">현업 담당자</label>
                <input id="busiMgr" name="busiMgr" placeholder="이름을 입력해 주세요." tabindex="16" readonly />
              </div>

              <div class="form-group">
                <label for="busiTestDate">테스트 시작일</label>
                <input id="busiTestDate" name="busiTestDate" type="date" readonly />
              </div>

              <div class="form-group">
                <label for="busiConfirmDate">테스트 완료일</label>
                <input id="busiConfirmDate" name="busiConfirmDate" type="date" readonly />
              </div>

              <div class="form-group">
                <label for="busiTestResult">테스트 결과</label>
                <select id="busiTestResult" name="busiTestResult" class="readonly">
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
                  tabindex="17"
                  readonly
                ></textarea>
              </div>
            </div>
          </fieldset>
        </div>

        <fieldset>
          <div class="form-group-container">
            <div class="form-group-row">
              <div class="form-group">
                <label for="execFileInput">수행사 증적</label>
                <div class="file-box">
                  <button type="button" class="file-button" id="execFileSelectButton" disabled>파일 선택</button>

                  <div class="file-preview-container">
                    <input type="file" id="execFileInput" class="hidden" name="execFile" multiple />
                    <div id="execFileOutput" class="file-preview custom-scroll"></div>
                  </div>
                </div>
              </div>
            </div>

            <div class="form-group-row">
              <div class="form-group">
                <label for="thirdFileInput">제3자 증적</label>
                <div class="file-box">
                  <button type="button" class="file-button" id="thirdFileSelectButton" disabled>파일 선택</button>

                  <div class="file-preview-container">
                    <input type="file" id="thirdFileInput" class="hidden" name="thirdFile" multiple />
                    <div id="thirdFileOutput" class="file-preview custom-scroll"></div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </fieldset>

        <div class="button-container">
          <button id="goDefectRegisterPageButton" class="defect-button" type="button">결함등록</button>
          <button id="openEditableDefectSearchModalButton" class="defect-button" type="button">결함수정</button>
          <button id="goBackButton" class="cancel-button" type="button">취소</button>
          <button class="save-button">저장</button>
        </div>
      </form>
    </main>
  </body>
</html>

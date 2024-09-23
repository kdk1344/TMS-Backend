import {
  tmsFetch,
  renderTMSHeader,
  setupPagination,
  convertDate,
  initializeSelect,
  getTestStageList,
  getMajorCategoryCodes,
  getSubCategoryCodes,
  getProgramTypes,
  getTestStatusList,
  openModal,
  closeModal,
  setupModalEventListeners,
  showSpinner,
  hideSpinner,
} from "./common.js";

let currentPage = 1;

// DOM 요소들
const testProgressTableBody = document.getElementById("testProgressTableBody");

const testProgressFilterForm = document.getElementById("testProgressFilterForm");
const majorCategorySelect = document.getElementById("majorCategoryForFilter");

const goTestProgressRegisterPageButton = document.getElementById("goTestProgressRegisterPageButton");
const selectAllTestProgressCheckbox = document.getElementById("selectAllTestProgressCheckbox");
const deleteTestProgressButton = document.getElementById("deleteTestProgressButton");

const uploadTestProgressFileButton = document.getElementById("uploadTestProgressFileButton");
const uploadTestProgressFileInput = document.getElementById("uploadTestProgressFileInput");

const openTestProgressFileUploadModalButton = document.getElementById("openTestProgressFileUploadModalButton");
const closeTestProgressFileUploadModalButton = document.getElementById("closeTestProgressFileUploadModalButton");
const openTestProgressFileDownloadModalButton = document.getElementById("openTestProgressFileDownloadModalButton");
const closeTestProgressFileDownloadModalButton = document.getElementById("closeTestProgressFileDownloadModalButton");

// 문서 로드 시 초기화
document.addEventListener("DOMContentLoaded", init);

// 모달 아이디
const MODAL_ID = {
  TEST_PROGRESS_EDIT: "testProgressEditModal",
  TEST_PROGRESS_FILE_UPLOAD: "testProgressFileUploadModal",
  TEST_PROGRESS_FILE_DOWNLOAD: "testProgressFileDownloadModal",
};

// 초기화 함수
function init() {
  renderTMSHeader();
  setupEventListeners();
  initializeFilterForm(); // 폼 메타 데이터 초기 로드
  renderTestProgress(); // 테스트 진행현황 목록 render
}

// 이벤트 핸들러 설정
function setupEventListeners() {
  // 테스트 진행현황 등록 페이지로 이동
  if (goTestProgressRegisterPageButton) {
    goTestProgressRegisterPageButton.addEventListener("click", () => {
      window.location.href = "/tms/testProgressReg";
    });
  }

  // 테스트 진행현황 목록 테이블 클릭 이벤트 핸들러
  if (testProgressTableBody) {
    testProgressTableBody.addEventListener("click", (event) => {
      const clickedElement = event.target;

      if (clickedElement.type === "checkbox") {
        return;
      }

      const row = event.target.closest("tr");

      if (row) {
        const checkbox = row.querySelector('input[type="checkbox"]'); // tr 내부의 첫 번째 체크박스 요소 선택
        const testProgressId = checkbox.value;
        window.location.href = `/tms/testProgressEdit?seq=${testProgressId}`;
      }
    });
  }

  // 테스트 진행현황 필터 폼 제출 및 리셋 이벤트 핸들러
  if (testProgressFilterForm) {
    testProgressFilterForm.addEventListener("submit", submitTestProgressFilter);
    testProgressFilterForm.addEventListener("reset", resetTestProgressFilter);
  }

  if (majorCategorySelect) {
    majorCategorySelect.addEventListener("change", () => initializeSubCategorySelect(majorCategorySelect.value));
  }

  // 전체 선택 체크박스 클릭 이벤트 핸들러
  if (selectAllTestProgressCheckbox) {
    selectAllTestProgressCheckbox.addEventListener("click", toggleAllCheckboxes);
  }

  // 테스트 진행현황 정보 삭제
  if (deleteTestProgressButton) {
    deleteTestProgressButton.addEventListener("click", deleteTestProgress);
  }

  // 엑셀 업로드 이벤트 핸들러
  if (openTestProgressFileUploadModalButton) {
    openTestProgressFileUploadModalButton.addEventListener("click", () =>
      openModal(MODAL_ID.TEST_PROGRESS_FILE_UPLOAD)
    );
  }

  if (closeTestProgressFileUploadModalButton) {
    closeTestProgressFileUploadModalButton.addEventListener("click", () =>
      closeModal(MODAL_ID.TEST_PROGRESS_FILE_UPLOAD)
    );
  }

  if (uploadTestProgressFileButton) {
    uploadTestProgressFileButton.addEventListener("click", uploadTestProgressFile);
  }

  // 엑셀 다운로드 이벤트 핸들러
  if (openTestProgressFileDownloadModalButton) {
    openTestProgressFileDownloadModalButton.addEventListener("click", () => {
      copyFilterValuesToDownloadForm(); // 필터링 값 복사
      openModal(MODAL_ID.TEST_PROGRESS_FILE_DOWNLOAD);
    });
  }

  if (closeTestProgressFileDownloadModalButton) {
    closeTestProgressFileDownloadModalButton.addEventListener("click", () =>
      closeModal(MODAL_ID.TEST_PROGRESS_FILE_DOWNLOAD)
    );
  }

  // 모달 외부 클릭 시 닫기 버튼 이벤트 핸들러
  setupModalEventListeners(Object.values(MODAL_ID));
}

// 테스트 진행현황 목록 테이블 렌더링
async function renderTestProgress(
  getTestProgressListProps = {
    page: 1,
    testStage: "",
    majorCategory: "",
    subCategory: "",
    programType: "",
    testId: "",
    programId: "",
    programName: "",
    developer: "",
    pl: "",
    execCompanyMgr: "",
    thirdPartyTestMgr: "",
    itMgr: "",
    busiMgr: "",
  }
) {
  const { testProgressList, totalPages } = await getTestProgressList(getTestProgressListProps);

  if (testProgressTableBody) {
    testProgressTableBody.innerHTML = "";

    testProgressList.forEach((testProgress) => {
      const {
        seq,
        subCategory,
        testId,
        testScenarioName,
        testCaseName,
        testStepName,
        screenId,
        screenName,
        programType,
        programId,
        programName,
        execCompanyTestDate,
        execCompanyConfirmDate,
        developer,
        pl,
        thirdPartyTestMgr,
        thirdPartyConfirmDate,
        itMgr,
        itConfirmDate,
        busiMgr,
        busiConfirmDate,
        testStatus,
      } = testProgress;

      const row = document.createElement("tr");

      row.innerHTML = `
        <td><input type="checkbox" name="testProgress" value="${seq}"></td>
        <td class="sub-category">${subCategory}</td>
        <td class="test-id">${testId}</td>
        <td class="test-scenario-name">${testScenarioName}</td>
        <td class="test-case-name">${testCaseName}</td>
        <td class="test-step-name">${testStepName}</td>
        <td class="screen-id">${screenId}</th>
        <td class="screen-name">${screenName}</td>
        <td class="program-type">${programType}</td>
        <td class="program-id">${programId}</td>
        <td class="program-name">${programName}</td>
        <td class="exec-company-test-date">${convertDate(execCompanyTestDate)}</td>
        <td class="exec-company-confirm-date">${convertDate(execCompanyConfirmDate)}</td>
        <td class="developer">${developer}</td>
        <td class="pl">${pl}</td>
        <td class="third-party-test-mgr">${thirdPartyTestMgr}</td>
        <td class="third-party-confirm-date">${convertDate(thirdPartyConfirmDate)}</td>
        <td class="it-mgr">${itMgr}</td>
        <td class="it-confirm-date">${convertDate(itConfirmDate)}</td>
        <td class="busi-mgr">${busiMgr}</td>
        <td class="busi-confirm-date">${convertDate(busiConfirmDate)}</td>
        <td class="test-status">${testStatus}</td>
      `;
      testProgressTableBody.appendChild(row);
    });

    setupPagination({ paginationElementId: "testProgressPagination", totalPages, currentPage, changePage });
  }
}

// 테스트 진행현황 필터링
function submitTestProgressFilter(event) {
  event.preventDefault(); // 폼 제출 기본 동작 방지

  // 페이지를 1로 초기화하고 테이블 렌더링
  currentPage = 1;

  const getTestProgressListProps = getCurrentFilterValues();

  renderTestProgress(getTestProgressListProps);
}

// 테스트 진행현황 필터 리셋
function resetTestProgressFilter() {
  this.reset(); // 폼 초기화
}

// 페이지 변경
function changePage(page) {
  currentPage = page;

  const getTestProgressListProps = getCurrentFilterValues();

  renderTestProgress(getTestProgressListProps);
}

function getCurrentFilterValues() {
  let getTestProgressListProps = { page: currentPage };

  const testKey = document.getElementById("testKeySelect").value;
  const testValue = document.getElementById("testValueInput").value.trim();

  getTestProgressListProps[testKey] = testValue;

  const testRoleKey = document.getElementById("testRoleKeySelect").value;
  const testRoleValue = document.getElementById("testRoleValueInput").value.trim();

  getTestProgressListProps[testRoleKey] = testRoleValue;

  const testStage = document.getElementById("testStageForFilter").value;
  const majorCategory = document.getElementById("majorCategoryForFilter").value;
  const subCategory = document.getElementById("subCategoryForFilter").value;
  const programType = document.getElementById("programTypeForFilter").value;
  const testStatus = document.getElementById("testStatusForFilter").value;

  getTestProgressListProps = {
    ...getTestProgressListProps,
    testStage,
    majorCategory,
    subCategory,
    programType,
    testStatus,
  };

  return getTestProgressListProps;
}

async function initializeFilterForm() {
  const [{ testStageList }, { majorCategoryCodes }, { programTypes }, { testStatusList }] = await Promise.all([
    getTestStageList(),
    getMajorCategoryCodes(),
    getProgramTypes(),
    getTestStatusList(),
  ]);

  const SELECT_ID = {
    TEST_STAGE: "testStageForFilter",
    MAJOR_CATEGORY: "majorCategoryForFilter",
    PROGRAM_TYPE: "programTypeForFilter",
    TEST_STATUS: "testStatusForFilter",
  };

  const SELECT_DATA = {
    [SELECT_ID.TEST_STAGE]: testStageList,
    [SELECT_ID.MAJOR_CATEGORY]: majorCategoryCodes,
    [SELECT_ID.PROGRAM_TYPE]: programTypes,
    [SELECT_ID.TEST_STATUS]: testStatusList,
  };

  Object.values(SELECT_ID).forEach((selectId) => initializeSelect(selectId, SELECT_DATA[selectId]));
}

async function initializeSubCategorySelect(selectedMajorCategoryCode) {
  const SUB_CATEGORY_SELECT_ID = "subCategoryForFilter";

  // 업무 대분류 '전체'를 선택한 경우
  if (selectedMajorCategoryCode === "") initializeSelect(SUB_CATEGORY_SELECT_ID, []);
  else {
    const { subCategoryCodes } = await getSubCategoryCodes(selectedMajorCategoryCode);

    initializeSelect(SUB_CATEGORY_SELECT_ID, subCategoryCodes);
  }
}

/** 테스트 진행현황 필터링 폼의 값을 다운로드 폼으로 복사하는 함수 */
function copyFilterValuesToDownloadForm() {
  // 조회 필터링 폼의 값을 가져옴
  const {
    testStage,
    majorCategory,
    subCategory,
    programType,
    testId = "",
    programId = "",
    programName = "",
    developer = "",
    pl = "",
    execCompanyMgr = "",
    thirdPartyTestMgr = "",
    itMgr = "",
    busiMgr = "",
    testStatus = "",
  } = getCurrentFilterValues();

  // 숨겨진 다운로드 폼의 input 필드에 값을 설정
  document.getElementById("testStageForDownload").value = testStage;
  document.getElementById("majorCategoryForDownload").value = majorCategory;
  document.getElementById("subCategoryForDownload").value = subCategory;
  document.getElementById("programTypeForDownload").value = programType;
  document.getElementById("testIdForDownload").value = testId;
  document.getElementById("programIdForDownload").value = programId;
  document.getElementById("programNameForDownload").value = programName;
  document.getElementById("developerForDownload").value = developer;
  document.getElementById("plForDownload").value = pl;
  document.getElementById("execCompanyMgrForDownload").value = execCompanyMgr;
  document.getElementById("thirdPartyTestMgrForDownload").value = thirdPartyTestMgr;
  document.getElementById("itMgrForDownload").value = itMgr;
  document.getElementById("busiMgrForDownload").value = busiMgr;
  document.getElementById("testStatusForDownload").value = testStatus;
}

// 체크박스 전체 선택/해제
function toggleAllCheckboxes() {
  const checkboxes = document.querySelectorAll('input[name="testProgress"]');
  checkboxes.forEach((checkbox) => (checkbox.checked = selectAllTestProgressCheckbox.checked));
}

// API 함수
// 테스트 진행현황 목록 조회
async function getTestProgressList(
  getTestProgressListProps = {
    page: 1,
    testStage,
    majorCategory,
    subCategory,
    programType,
    testId,
    programId,
    programName,
    developer,
    pl,
    execCompanyMgr,
    thirdPartyTestMgr,
    itMgr,
    busiMgr,
    testStatus,
  }
) {
  try {
    const query = new URLSearchParams(getTestProgressListProps).toString();
    const { testProgress: testProgressList, totalPages } = await tmsFetch(`/testProgress?${query}`);

    return { testProgressList, totalPages };
  } catch (error) {
    console.error(error.message, "테스트 진행현황 목록을 불러오지 못 했습니다.");
  }
}

// 테스트 진행현황 파일 업로드
async function uploadTestProgressFile() {
  if (uploadTestProgressFileInput.files.length <= 0) return;

  const formData = new FormData();

  // key 이름의 경우 서버와 협의
  formData.append("file", uploadTestProgressFileInput.files[0]);

  try {
    showSpinner();

    const response = await tmsFetch("/testProgressupload", {
      method: "POST",
      body: formData,
    });

    const success = response ? response.status === "success" : false;

    if (success) {
      alert("파일 업로드가 완료되었습니다.");
      location.reload(); // 페이지 새로고침
    }
  } catch (error) {
    alert(error.message + "\n다시 시도해주세요.");
  } finally {
    hideSpinner();
  }
}

// 테스트 진행현황 정보 삭제
async function deleteTestProgress() {
  try {
    const confirmed = confirm("테스트 정보를 삭제하시겠습니까?");

    if (!confirmed) return;

    const selectedTestProgressIds = Array.from(document.querySelectorAll('input[name="testProgress"]:checked')).map(
      (checkbox) => checkbox.value
    );

    if (selectedTestProgressIds.length === 0) {
      alert("삭제할 테스트 정보를 선택해주세요.");
      return;
    }

    showSpinner();

    const { status } = await tmsFetch("/deletetest", {
      method: "DELETE",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(selectedTestProgressIds),
    });

    const success = status === "success";

    if (success) {
      alert(`테스트 정보 삭제가 완료되었습니다.`);
      location.reload(); // 페이지 새로고침
    }
  } catch (error) {
    alert(error.message + "\n다시 시도해주세요.");
  } finally {
    hideSpinner();
  }
}

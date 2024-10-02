import {
  tmsFetch,
  renderTMSHeader,
  initializeSelect,
  getMajorCategoryCodes,
  getSubCategoryCodes,
  getTestStageList,
  getTestResultList,
  getProgramTypes,
  addFiles,
  updateFilePreview,
  showSpinner,
  hideSpinner,
  goBack,
  checkSession,
  openModal,
  closeModal,
  setupModalEventListeners,
  renderProgramList,
  submitProgramFilter,
  selectProgramFromTable,
  renderScreenList,
  selectScreenFromTable,
  AUTHORITY_CODE,
} from "./common.js";

/** @global */
const SELECT_ID = {
  TEST_STAGE: "testStage",
  MAJOR_CATEGORY: "majorCategory",
  SUB_CATEGORY: "subCategory",
  EXEC_COMPANY_TEST_RESULT: "execCompanyTestResult",
  THIRD_PARTY_TEST_RESULT: "thirdTestResult",
  IT_TEST_RESULT: "itTestResult",
  BUSI_TEST_RESULT: "busiTestResult",
  PROGRAM_TYPE_FOR_PROGRAM: "programTypeForPrgoram", // 프로그램 조회 모달 내부
};

// DOM 요소들
const testProgressEditForm = document.getElementById("testProgressEditForm");
const majorCategorySelect = document.getElementById("majorCategory");
const goBackButton = document.getElementById("goBackButton");

const programFilterForm = document.getElementById("programFilterForm");
const programSearchButton = document.getElementById("programSearchButton");
const closeProgramSearchModalButton = document.getElementById("closeProgramSearchModalButton");
const closeScreenSearchModalButton = document.getElementById("closeScreenSearchModalButton");

const execFileInput = document.getElementById("execFileInput");
const execFileSelectButton = document.getElementById("execFileSelectButton");

const thirdFileInput = document.getElementById("thirdFileInput");
const thirdFileSelectButton = document.getElementById("thirdFileSelectButton");

// 문서 로드 시 초기화
document.addEventListener("DOMContentLoaded", init);

// 초기화 함수
function init() {
  renderTMSHeader();
  initializeEditForm();
  initializePageByUser();
  setupEventListeners();
}

// 이벤트 핸들러 설정
function setupEventListeners() {
  // 업무 대분류 이벤트 핸들러
  if (majorCategorySelect) {
    majorCategorySelect.addEventListener("change", () => initializeSubCategorySelect(majorCategorySelect.value));
  }

  // 파일 선택 버튼 클릭 시 파일 입력 필드 클릭
  execFileSelectButton.addEventListener("click", () => {
    execFileInput.click();
  });

  thirdFileSelectButton.addEventListener("click", () => {
    thirdFileInput.click();
  });

  // 파일 입력 필드에서 파일이 선택되면 파일 목록 업데이트
  execFileInput.addEventListener("change", () => {
    const execFileInputId = "execFileInput";
    const execFileOutputId = "execFileOutput";

    addFiles(execFileInputId);
    updateFilePreview(execFileInputId, execFileOutputId);
  });

  thirdFileInput.addEventListener("change", () => {
    const thirdFileInputId = "thirdFileInput";
    const thirdFileOutputId = "thirdFileOutput";

    addFiles(thirdFileInputId);
    updateFilePreview(thirdFileInputId, thirdFileOutputId);
  });

  // 테스트 시나리오 수정
  testProgressEditForm.addEventListener("submit", register);

  // 뒤로가기
  goBackButton.addEventListener("click", () => goBack("수정을 취소하시겠습니까? 작성 중인 정보는 저장되지 않습니다."));

  // 화면 검색
  screenSearchButton.addEventListener("click", () => {
    renderScreenList();
    openModal("screenSearchModal");
  });

  closeScreenSearchModalButton.addEventListener("click", () => closeModal("screenSearchModal"));

  // 화면 목록 테이블 클릭
  screenTableBody.addEventListener("click", selectScreenFromTable);

  // 프로그램 검색
  programSearchButton.addEventListener("click", () => {
    renderProgramList();
    openModal("programSearchModal");
  });

  closeProgramSearchModalButton.addEventListener("click", () => closeModal("programSearchModal"));

  programFilterForm.addEventListener("submit", submitProgramFilter);

  // 프로그램 목록 테이블 클릭
  programTableBody.addEventListener("click", selectProgramFromTable);

  setupModalEventListeners(["screenSearchModal", "programSearchModal"]);
}

/** 사용자 정보에 따른 권한 설정 */
async function initializePageByUser() {
  const { userName, authorityCode } = await checkSession();
  const accessCode = new Set([AUTHORITY_CODE.ADMIN, AUTHORITY_CODE.PM, AUTHORITY_CODE.TEST_MGR]);
  const searchButtonIds = ["programSearchButton", "screenSearchButton"];
  const testSelectIds = ["thirdTestResult", "itTestResult", "busiTestResult"]; //제3자 테스트, 고객IT 테스트, 고객현업 테스트 셀렉트박스
  const selectIds = Object.values(SELECT_ID).filter((id) => !testSelectIds.includes(id));
  const inputIds = [
    "minorCategory",
    "testId",
    "testScenarioName",
    "testCaseName",
    "testStepName",
    "executeProcedure",
    "preConditions",
    "inputData",
    "expectedResult",
    "actualResult",
    "execCompanyMgr",
    "execCompanyTestDate",
    "execCompanyConfirmDate",
    "execCompanyTestNotes",
    "thirdPartyTestMgr",
    "itMgr",
    "busiMgr",
  ];

  // 관리자, PM, 테스트 관리자인 경우
  if (accessCode.has(authorityCode)) {
    // 전체 Select요소 readonly 클래스 해제
    selectIds.forEach((selectId) => {
      document.getElementById(selectId).classList.remove("readonly");
    });

    // 전체 Input요소 readonly 속성 해제
    inputIds.forEach((inputId) => {
      document.getElementById(inputId).removeAttribute("readonly");
    });

    // 검색 버튼 disabled 속성 해제
    searchButtonIds.forEach((buttonId) => {
      document.getElementById(buttonId).removeAttribute("disabled");
    });
  }

  // 수행사 테스터인 경우
  if (document.getElementById("execCompanyMgr").value === userName) {
    const inputIds = ["execCompanyTestDate", "execCompanyConfirmDate", "execCompanyTestNotes"];
    const selectIds = ["execCompanyTestResult"];
    const buttonIds = ["execFileSelectButton"];

    selectIds.forEach((selectId) => {
      document.getElementById(selectId).classList.remove("readonly");
    });

    inputIds.forEach((inputId) => {
      document.getElementById(inputId).removeAttribute("readonly");
    });

    buttonIds.forEach((buttonId) => {
      document.getElementById(buttonId).removeAttribute("disabled");
    });
  }

  // 제3자 테스터인 경우
  if (document.getElementById("thirdPartyTestMgr").value === userName) {
    const inputIds = ["thirdPartyTestMgr", "thirdPartyTestDate", "thirdPartyConfirmDate", "thirdPartyTestNotes"];
    const selectIds = ["thirdTestResult"];
    const buttonIds = ["thirdFileSelectButton"];

    selectIds.forEach((selectId) => {
      document.getElementById(selectId).classList.remove("readonly");
    });

    inputIds.forEach((inputId) => {
      document.getElementById(inputId).removeAttribute("readonly");
    });

    buttonIds.forEach((buttonId) => {
      document.getElementById(buttonId).removeAttribute("disabled");
    });
  }

  // 고객IT 담당자인 경우
  if (document.getElementById("itMgr").value === userName) {
    const inputIds = ["itMgr", "itTestDate", "itConfirmDate", "itTestNotes"];
    const selectIds = ["itTestResult"];

    selectIds.forEach((selectId) => {
      document.getElementById(selectId).classList.remove("readonly");
    });

    inputIds.forEach((inputId) => {
      document.getElementById(inputId).removeAttribute("readonly");
    });
  }

  // 고객현업 담당자인 경우
  if (document.getElementById("busiMgr").value === userName) {
    const inputIds = ["busiMgr", "busiTestDate", "busiConfirmDate", "busiTestNotes"];
    const selectIds = ["busiTestResult"];

    selectIds.forEach((selectId) => {
      document.getElementById(selectId).classList.remove("readonly");
    });

    inputIds.forEach((inputId) => {
      document.getElementById(inputId).removeAttribute("readonly");
    });
  }
}

/**  수정 폼 초기화 함수 */
async function initializeEditForm() {
  // 모든 비동기 호출을 병렬로 실행
  const [
    { testStageList },
    { majorCategoryCodes },
    { testResultList },
    { programTypes },
    { testProgressDetail, execAttachments, thirdAttachments, defectCounts },
  ] = await Promise.all([
    getTestStageList(),
    getMajorCategoryCodes(),
    getTestResultList(),
    getProgramTypes(),
    getTestProgressDetail(),
  ]);

  const SELECT_DATA = {
    [SELECT_ID.TEST_STAGE]: testStageList,
    [SELECT_ID.MAJOR_CATEGORY]: majorCategoryCodes,
    [SELECT_ID.EXEC_COMPANY_TEST_RESULT]: testResultList,
    [SELECT_ID.THIRD_PARTY_TEST_RESULT]: testResultList,
    [SELECT_ID.IT_TEST_RESULT]: testResultList,
    [SELECT_ID.BUSI_TEST_RESULT]: testResultList,
    [SELECT_ID.PROGRAM_TYPE_FOR_PROGRAM]: programTypes,
  };

  Object.values(SELECT_ID).forEach((selectId) => initializeSelect(selectId, SELECT_DATA[selectId]));

  await initializeSubCategorySelect(majorCategoryCodes[0]?.code);
  await fillTestProgressEditFormValues({ ...testProgressDetail, execAttachments, thirdAttachments, defectCounts });
}

async function initializeSubCategorySelect(selectedMajorCategoryCode = "") {
  // 업무 대분류 '전체'를 선택한 경우 중분류 리셋
  if (selectedMajorCategoryCode === "") initializeSelect(SELECT_ID.SUB_CATEGORY, []);
  else {
    const { subCategoryCodes } = await getSubCategoryCodes(selectedMajorCategoryCode);

    initializeSelect(SELECT_ID.SUB_CATEGORY, subCategoryCodes);
  }
}

async function fillTestProgressEditFormValues(data) {}

// 테스트 시나리오 상세 조회
async function getTestProgressDetail() {
  try {
    const testProgressId = new URLSearchParams(window.location.search).get("seq");
    const query = new URLSearchParams({ seq: testProgressId }).toString();

    const {
      testProgress: testProgressDetail,
      execAttachments,
      thirdAttachments,
      defectCounts,
    } = await tmsFetch(`/testProgressDetail?${query}`);

    return { testProgressDetail, execAttachments, thirdAttachments, defectCounts };
  } catch (error) {
    console.error(error.message, `테스트 시나리오(${testProgressId})를 불러오지 못 했습니다.`);
  }
}

// 테스트 시나리오 수정
async function register(event) {
  event.preventDefault(); // 폼 제출 기본 동작 방지

  const confirmed = confirm("테스트 시나리오를 수정하시겠습니까?");
  if (!confirmed) return;

  // 기존 FormData 객체 생성
  const formData = new FormData(event.target);

  // 새로운 FormData 객체 생성
  const newFormData = new FormData();

  // 파일 추출 (다중 파일 지원)
  const execFiles = formData.getAll("execFile"); // 다중 파일을 배열로 가져오기
  const thirdFiles = formData.getAll("thirdFile"); // 다중 파일을 배열로 가져오기

  // 파일을 제외한 나머지 데이터 추출
  const defectData = {};

  formData.forEach((value, key) => {
    if (key !== "execFile" && key !== "thirdFile") {
      defectData[key] = value;
    }
  });

  // notice 데이터 추가 (JSON 형태로 묶기)
  newFormData.append("testProgress", new Blob([JSON.stringify(defectData)], { type: "application/json" }));

  // 파일 추가
  execFiles.forEach((file) => {
    newFormData.append("execFile", file); // Blob으로 감싸지 않고 File 객체 그대로 추가
  });

  thirdFiles.forEach((file) => {
    newFormData.append("thirdFile", file);
  });

  showSpinner();

  try {
    const { status } = await tmsFetch("/testProgressEdit", {
      method: "POST",
      body: newFormData,
    });

    const success = status === "success";

    if (success) {
      alert(`테스트 시나리오 수정이 완료되었습니다.`);
      event.target.reset(); // 폼 초기화
      window.location.href = "/tms/testProgress"; // 테스트 진행현황 목록으로 이동
    }
  } catch (error) {
    alert(error.message + "\n다시 시도해주세요.");
  } finally {
    hideSpinner();
  }
}

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
  setTestIdIfUnitTest,
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
const testProgressRegisterForm = document.getElementById("testProgressRegisterForm");
const majorCategorySelect = document.getElementById("majorCategory");
const testStageSelect = document.getElementById("testStage");
const goBackButton = document.getElementById("goBackButton");

const programFilterForm = document.getElementById("programFilterForm");
const programSearchButton = document.getElementById("programSearchButton");
const closeProgramSearchModalButton = document.getElementById("closeProgramSearchModalButton");

const execFileInput = document.getElementById("execFileInput");
const execFileSelectButton = document.getElementById("execFileSelectButton");

const thirdFileInput = document.getElementById("thirdFileInput");
const thirdFileSelectButton = document.getElementById("thirdFileSelectButton");

// 문서 로드 시 초기화
document.addEventListener("DOMContentLoaded", init);

// 초기화 함수
function init() {
  checkAccessOrGoBack();
  renderTMSHeader();
  initializeRegisterForm();
  setupEventListeners();
}

// 이벤트 핸들러 설정
function setupEventListeners() {
  // 업무 대분류 이벤트 핸들러
  if (majorCategorySelect) {
    majorCategorySelect.addEventListener("change", () => initializeSubCategorySelect(majorCategorySelect.value));
  }

  // 테스트 단계 선택 이벤트 핸들러
  if (testStageSelect) {
    testStageSelect.addEventListener("change", (e) => {
      setTestIdIfUnitTest(e.target.options[e.target.selectedIndex].textContent);
    });
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

  // 테스트 시나리오 등록
  testProgressRegisterForm.addEventListener("submit", register);

  // 뒤로가기
  goBackButton.addEventListener("click", () => goBack("등록을 취소하시겠습니까? 작성 중인 정보는 저장되지 않습니다."));

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

async function checkAccessOrGoBack() {
  const { authorityCode } = await checkSession();
  const accessCode = new Set([AUTHORITY_CODE.ADMIN, AUTHORITY_CODE.PM, AUTHORITY_CODE.TEST_MGR]);

  if (!accessCode.has(authorityCode))
    goBack("테스트 시나리오를 등록할 수 있는 권한이 없습니다. 필요 시 관리자에게 문의하세요.");
}

/**  등록 폼 초기화 함수 */
async function initializeRegisterForm() {
  // 모든 비동기 호출을 병렬로 실행
  const [{ testStageList }, { majorCategoryCodes }, { testResultList }, { programTypes }] = await Promise.all([
    getTestStageList(),
    getMajorCategoryCodes(),
    getTestResultList(),
    getProgramTypes(),
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
}

async function initializeSubCategorySelect(selectedMajorCategoryCode = "") {
  // 업무 대분류 '전체'를 선택한 경우 중분류 리셋
  if (selectedMajorCategoryCode === "") initializeSelect(SELECT_ID.SUB_CATEGORY, []);
  else {
    const { subCategoryCodes } = await getSubCategoryCodes(selectedMajorCategoryCode);

    initializeSelect(SELECT_ID.SUB_CATEGORY, subCategoryCodes);
  }
}

// 테스트 시나리오 등록
async function register(event) {
  event.preventDefault(); // 폼 제출 기본 동작 방지

  const confirmed = confirm("테스트 시나리오를 등록하시겠습니까?");
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
    const { status } = await tmsFetch("/testProgressReg", {
      method: "POST",
      body: newFormData,
    });

    const success = status === "success";

    if (success) {
      alert(`테스트 시나리오 등록이 완료되었습니다.`);
      event.target.reset(); // 폼 초기화
      window.location.href = "/tms/testProgress"; // 결함 목록으로 이동
    }
  } catch (error) {
    alert(error.message + "\n다시 시도해주세요.");
  } finally {
    hideSpinner();
  }
}

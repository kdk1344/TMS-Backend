import {
  tmsFetch,
  renderTMSHeader,
  initializeSelect,
  getMajorCategoryCodes,
  getSubCategoryCodes,
  getProgramTypes,
  getProgramDetailTypes,
  getLevels,
  getProgramStatusList,
  getTestResultList,
  addFiles,
  updateFilePreview,
  showSpinner,
  hideSpinner,
} from "./common.js";

/** @global */
const SELECT_ID = {
  MAJOR_CATEGORY: "majorCategory",
  SUB_CATEGORY: "subCategory",
  PROGRAM_TYPE: "programType",
  PROGRAM_DETAIL_TYPE: "programDetailType",
  PROGRAM_NAME: "programName",
  PRIORITY: "priority",
  DIFFICULTY: "difficulty",
  PROGRAM_STATUS: "programStatus",
  PL_TEST_RESULT: "plTestResult",
  THIRD_PARTY_TEST_RESULT: "thirdTestResult",
  IT_TEST_RESULT: "itTestResult",
  BUSI_TEST_RESULT: "busiTestResult",
  DEV_STATUS: "devStatus",
};

// DOM 요소들
const devProgressRegisterForm = document.getElementById("devProgressRegisterForm");
const majorCategorySelect = document.getElementById("majorCategory");
const goBackButton = document.getElementById("goBackButton");

const fileInput = document.getElementById("fileInput");
const fileSelectButton = document.getElementById("fileSelectButton");

// 문서 로드 시 초기화
document.addEventListener("DOMContentLoaded", init);

// 초기화 함수
function init() {
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

  // 파일 선택 버튼 클릭 시 파일 입력 필드 클릭
  fileSelectButton.addEventListener("click", () => {
    fileInput.click();
  });

  // 파일 입력 필드에서 파일이 선택되면 파일 목록 업데이트
  fileInput.addEventListener("change", () => {
    const fileInputId = "fileInput";
    const fileOutputId = "fileOutput";

    addFiles(fileInputId);
    updateFilePreview(fileInputId, fileOutputId);
  });

  // 프로그램 개발 정보 등록
  devProgressRegisterForm.addEventListener("submit", register);

  // 뒤로가기
  goBackButton.addEventListener("click", goBack);
}

/**  등록 폼 초기화 함수 */
async function initializeRegisterForm() {
  // 모든 비동기 호출을 병렬로 실행
  const [
    { majorCategoryCodes },
    { programTypes },
    { programDetailTypes },
    { levels },
    { programStatusList },
    { testResultList },
  ] = await Promise.all([
    getMajorCategoryCodes(),
    getProgramTypes(),
    getProgramDetailTypes(),
    getLevels(),
    getProgramStatusList(),
    getTestResultList(),
  ]);

  const SELECT_DATA = {
    [SELECT_ID.MAJOR_CATEGORY]: majorCategoryCodes,
    [SELECT_ID.PROGRAM_TYPE]: programTypes,
    [SELECT_ID.PROGRAM_DETAIL_TYPE]: programDetailTypes,
    [SELECT_ID.PRIORITY]: levels,
    [SELECT_ID.DIFFICULTY]: levels,
    [SELECT_ID.PROGRAM_STATUS]: programStatusList,
    [SELECT_ID.PL_TEST_RESULT]: testResultList,
    [SELECT_ID.THIRD_PARTY_TEST_RESULT]: testResultList,
    [SELECT_ID.IT_TEST_RESULT]: testResultList,
    [SELECT_ID.BUSI_TEST_RESULT]: testResultList,
  };

  Object.values(SELECT_ID).forEach((selectId) => initializeSelect(selectId, SELECT_DATA[selectId]));
}

async function initializeSubCategorySelect(selectedMajorCategoryCode) {
  // 업무 대분류 '전체'를 선택한 경우 중분류 리셋
  if (selectedMajorCategoryCode === "") initializeSelect(SELECT_ID.SUB_CATEGORY, []);
  else {
    const { subCategoryCodes } = await getSubCategoryCodes(selectedMajorCategoryCode);

    initializeSelect(SELECT_ID.SUB_CATEGORY, subCategoryCodes);
  }
}

// 프로그램 개발 등록
async function register(event) {
  event.preventDefault(); // 폼 제출 기본 동작 방지

  const confirmed = confirm("프로그램 개발 정보를 등록하시겠습니까?");
  if (!confirmed) return;

  // 기존 FormData 객체 생성
  const formData = new FormData(event.target);

  // 새로운 FormData 객체 생성
  const newFormData = new FormData();

  // 파일 추출 (다중 파일 지원)
  const files = formData.getAll("file"); // 다중 파일을 배열로 가져오기

  // 파일을 제외한 나머지 데이터 추출
  const devProgressData = {};

  formData.forEach((value, key) => {
    if (key !== "file") {
      devProgressData[key] = value;
    }
  });

  // notice 데이터 추가 (JSON 형태로 묶기)
  newFormData.append("devProgress", new Blob([JSON.stringify(devProgressData)], { type: "application/json" }));

  // 파일 추가
  files.forEach((file) => {
    newFormData.append("file", file); // Blob으로 감싸지 않고 File 객체 그대로 추가
  });

  showSpinner();

  try {
    const { status } = await tmsFetch("/devProgressReg", {
      method: "POST",
      body: newFormData,
    });

    const success = status === "success";

    if (success) {
      alert(`프로그램 개발 정보 등록이 완료되었습니다.`);
      event.target.reset(); // 폼 초기화
      window.location.href = "/tms/devProgress"; // 프로그램 개발 목록으로 이동
    }
  } catch (error) {
    alert(error.message + "\n다시 시도해주세요.");
  } finally {
    hideSpinner();
  }
}

function goBack() {
  const confirmed = confirm("등록을 취소하시겠습니까? 작성 중인 정보는 저장되지 않습니다.");

  if (!confirmed) return;

  window.history.back();
}

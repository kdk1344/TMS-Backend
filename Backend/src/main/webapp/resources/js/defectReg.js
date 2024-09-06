import {
  tmsFetch,
  renderTMSHeader,
  initializeSelect,
  getMajorCategoryCodes,
  getSubCategoryCodes,
  getTestStageList,
  getDefectTypeList,
  getDefectSeverityList,
  getDefectStatusList,
  getProgramTypes,
  addFiles,
  updateFilePreview,
  showSpinner,
  hideSpinner,
  goBack,
  checkSession,
  getCurrentDate,
  openModal,
  closeModal,
  setupModalEventListeners,
  getReferer,
} from "./common.js";

/** @global */
const SELECT_ID = {
  MAJOR_CATEGORY: "majorCategory",
  SUB_CATEGORY: "subCategory",
  TEST_STAGE: "testStage",
  DEFECT_TYPE: "defectType",
  DEFECT_SEVERITY: "defectSeverity",
  DEFECT_STATUS: "defectStatus",
  PROGRAM_TYPE_FOR_PROGRAM: "programTypeForPrgoram",
};

// 이전 페이지
const referer = getReferer(document.referrer);

// DOM 요소들
const defectRegisterForm = document.getElementById("defectRegisterForm");
const majorCategorySelect = document.getElementById("majorCategory");
const goBackButton = document.getElementById("goBackButton");

const programSearchButton = document.getElementById("programSearchButton");
const closeProgramSearchModalButton = document.getElementById("closeProgramSearchModalButton");

const defectNumberSearchButton = document.getElementById("defectNumberSearchButton");
const closeDefectNumberSearchModalButton = document.getElementById("closeDefectNumberSearchModalButton");

const defectFileInput = document.getElementById("defectFileInput");
const defectFileSelectButton = document.getElementById("defectFileSelectButton");

const defectFixFileInput = document.getElementById("defectFixFileInput");
const defectFixFileSelectButton = document.getElementById("defectFixFileSelectButton");

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
  defectFileSelectButton.addEventListener("click", () => {
    defectFileInput.click();
  });

  defectFixFileSelectButton.addEventListener("click", () => {
    defectFixFileInput.click();
  });

  // 파일 입력 필드에서 파일이 선택되면 파일 목록 업데이트
  defectFileInput.addEventListener("change", () => {
    const defectFileInputId = "defectFileInput";
    const defectFileOutputId = "defectFileOutput";

    addFiles(defectFileInputId);
    updateFilePreview(defectFileInputId, defectFileOutputId);
  });

  defectFixFileInput.addEventListener("change", () => {
    const defectFixFileInputId = "defectFixFileInput";
    const defectFixFileOutputId = "defectFixFileOutput";

    addFiles(defectFixFileInputId);
    updateFilePreview(defectFixFileInputId, defectFixFileOutputId);
  });

  // 프로그램 개발 정보 등록
  defectRegisterForm.addEventListener("submit", register);

  // 뒤로가기
  goBackButton.addEventListener("click", () => goBack("등록을 취소하시겠습니까? 작성 중인 정보는 저장되지 않습니다."));

  // 프로그램 검색
  programSearchButton.addEventListener("click", () => openModal("programSearchModal"));
  closeProgramSearchModalButton.addEventListener("click", () => closeModal("programSearchModal"));

  // 기발생 결함번호 검색
  defectNumberSearchButton.addEventListener("click", () => {
    if (checkBeforeDefectNumberSearching()) openModal("defectNumberSearchModal");
  });

  closeDefectNumberSearchModalButton.addEventListener("click", () => closeModal("defectNumberSearchModal"));

  setupModalEventListeners(["defectNumberSearchModal", "programSearchModal"]);
}

/**  등록 폼 초기화 함수 */
async function initializeRegisterForm() {
  // 모든 비동기 호출을 병렬로 실행
  const [
    { majorCategoryCodes },
    { testStageList },
    { defectTypeList },
    { defectSeverityList },
    { defectStatusList },
    { userID },
    { programTypes },
  ] = await Promise.all([
    getMajorCategoryCodes(),
    getTestStageList(),
    getDefectTypeList(),
    getDefectSeverityList(),
    getDefectStatusList(),
    checkSession(),
    getProgramTypes(),
  ]);

  const SELECT_DATA = {
    [SELECT_ID.MAJOR_CATEGORY]: majorCategoryCodes,
    [SELECT_ID.TEST_STAGE]: testStageList,
    [SELECT_ID.DEFECT_TYPE]: defectTypeList,
    [SELECT_ID.DEFECT_SEVERITY]: defectSeverityList,
    [SELECT_ID.DEFECT_STATUS]: defectStatusList,
    [SELECT_ID.PROGRAM_TYPE_FOR_PROGRAM]: programTypes,
  };

  Object.values(SELECT_ID).forEach((selectId) => initializeSelect(selectId, SELECT_DATA[selectId]));

  document.getElementById("defectRegistrar").value = userID;
  document.getElementById("defectDiscoveryDate").value = getCurrentDate();

  await initializeSubCategorySelect(majorCategoryCodes[0]?.code);
}

async function initializeSubCategorySelect(selectedMajorCategoryCode) {
  // 업무 대분류 '전체'를 선택한 경우 중분류 리셋
  if (selectedMajorCategoryCode === "") initializeSelect(SELECT_ID.SUB_CATEGORY, []);
  else {
    const { subCategoryCodes } = await getSubCategoryCodes(selectedMajorCategoryCode);

    initializeSelect(SELECT_ID.SUB_CATEGORY, subCategoryCodes);
  }
}

function checkBeforeDefectNumberSearching() {
  const programId = document.getElementById("programId").value;
  const programName = document.getElementById("programName").value;

  if (programId === "" || programName === "") {
    alert("먼저 프로그램ID와 프로그램명을 입력해 주세요.");
    return false;
  }

  const programIdBox = document.getElementById("programIdBox");
  const programNameBox = document.getElementById("programNameBox");

  programIdBox.textContent = programId;
  programNameBox.textContent = programName;

  return true;
}

// 결함 등록
async function register(event) {
  event.preventDefault(); // 폼 제출 기본 동작 방지

  const confirmed = confirm("결함 정보를 등록하시겠습니까?");
  if (!confirmed) return;

  // 기존 FormData 객체 생성
  const formData = new FormData(event.target);

  // 새로운 FormData 객체 생성
  const newFormData = new FormData();

  // 파일 추출 (다중 파일 지원)
  const files = formData.getAll("defectAttachments"); // 다중 파일을 배열로 가져오기
  const fixFiles = formData.getAll("defectFixAttachments"); // 다중 파일을 배열로 가져오기

  // 파일을 제외한 나머지 데이터 추출
  const defectData = {};

  formData.forEach((value, key) => {
    if (key !== "defectAttachments" && key !== "defectFixAttachments") {
      defectData[key] = value;
    }
  });

  // notice 데이터 추가 (JSON 형태로 묶기)
  newFormData.append("defect", new Blob([JSON.stringify(defectData)], { type: "application/json" }));

  // 파일 추가
  files.forEach((file) => {
    newFormData.append("defectAttachments", file); // Blob으로 감싸지 않고 File 객체 그대로 추가
  });

  fixFiles.forEach((file) => {
    newFormData.append("defectFixAttachments", file);
  });

  showSpinner();

  try {
    const { status } = await tmsFetch("/defectReg", {
      method: "POST",
      body: newFormData,
    });

    const success = status === "success";

    if (success) {
      alert(`결함 정보 등록이 완료되었습니다.`);
      event.target.reset(); // 폼 초기화
      window.location.href = "/tms/defect"; // 결함 목록으로 이동
    }
  } catch (error) {
    alert(error.message + "\n다시 시도해주세요.");
  } finally {
    hideSpinner();
  }
}

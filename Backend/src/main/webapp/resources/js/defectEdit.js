import {
  tmsFetch,
  renderTMSHeader,
  initializeSelect,
  getMajorCategoryCodes,
  getSubCategoryCodes,
  getTestStageList,
  getDefectTypeList,
  getDefectSeverityList,
  getProgramTypes,
  addFiles,
  updateFilePreview,
  loadFilesToInput,
  showSpinner,
  hideSpinner,
  goBack,
  checkSession,
  getCurrentDate,
  openModal,
  closeModal,
  setupModalEventListeners,
  getReferer,
  REFERER,
  setTestIdIfUnitTest,
  setSelectValueByText,
  convertDate,
  renderProgramList,
  submitProgramFilter,
  selectProgramFromTable,
  renderDefectNumberList,
  selectOriginalDefectNumberFromTable,
} from "./common.js";

/** @global */
const SELECT_ID = {
  MAJOR_CATEGORY: "majorCategory",
  SUB_CATEGORY: "subCategory",
  TEST_STAGE: "testStage",
  DEFECT_TYPE: "defectType",
  DEFECT_SEVERITY: "defectSeverity",
  PROGRAM_TYPE_FOR_PROGRAM: "programTypeForPrgoram",
};

// DOM 요소들
const defectEditForm = document.getElementById("defectEditForm");
const majorCategorySelect = document.getElementById("majorCategory");
const testStageSelect = document.getElementById("testStage");
const defectRegistrarInfo = document.getElementById("defectRegistrarInfo");
const goBackButton = document.getElementById("goBackButton");

const programFilterForm = document.getElementById("programFilterForm");
const programSearchButton = document.getElementById("programSearchButton");
const closeProgramSearchModalButton = document.getElementById("closeProgramSearchModalButton");
const programTableBody = document.getElementById("programTableBody");

const defectNumberSearchButton = document.getElementById("defectNumberSearchButton");
const closeDefectNumberSearchModalButton = document.getElementById("closeDefectNumberSearchModalButton");
const defectNumberTableBody = document.getElementById("defectNumberTableBody");

const defectFileInput = document.getElementById("defectFileInput");
const defectFileSelectButton = document.getElementById("defectFileSelectButton");

const defectFixFileInput = document.getElementById("defectFixFileInput");
const defectFixFileSelectButton = document.getElementById("defectFixFileSelectButton");

// 문서 로드 시 초기화
document.addEventListener("DOMContentLoaded", init);

// 초기화 함수
function init() {
  renderTMSHeader();
  initializeEditForm();
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

  // 프로그램 개발 정보 수정
  defectEditForm.addEventListener("submit", edit);

  // 뒤로가기
  goBackButton.addEventListener("click", () => goBack("수정을 취소하시겠습니까? 작성 중인 정보는 저장되지 않습니다."));

  // 프로그램 검색
  programSearchButton.addEventListener("click", () => {
    renderProgramList();
    openModal("programSearchModal");
  });

  closeProgramSearchModalButton.addEventListener("click", () => closeModal("programSearchModal"));

  programFilterForm.addEventListener("submit", submitProgramFilter);

  // 프로그램 목록 테이블 클릭
  programTableBody.addEventListener("click", selectProgramFromTable);

  // 기발생 결함번호 검색
  defectNumberSearchButton.addEventListener("click", () => {
    if (checkBeforeDefectNumberSearching()) {
      renderDefectNumberList();
      openModal("defectNumberSearchModal");
    }
  });

  // 기발생 결함번호 목록 테이블 클릭
  defectNumberTableBody.addEventListener("click", selectOriginalDefectNumberFromTable);

  // PL
  const plRadioButtons = document.querySelectorAll('input[name="plDefectJudgeClass"]');

  // 각 radio 버튼에 change 이벤트 리스너 추가
  plRadioButtons.forEach((radio) => {
    radio.addEventListener("change", function () {
      if (this.value === "결함수용") {
        defectRegistrarInfo.textContent =
          "※ [ 유의사항 ] 결함조치결과 재테스트 후 이상이 없는 경우 “수정자 확인일” 입력 - 재결함 발생한 경우 결함내용과 첨부파일에 추가";
      } else if (this.value === "결함아님") {
        defectRegistrarInfo.textContent =
          "※ [ 유의사항 ] PL의 결함아님 의견을 수용하는 경우 “수정자 확인일을”을 입력하시고, 수용하지 않는 경우는 PL에게 결함 조치요청하시기 바랍니다.";
      }
    });
  });

  closeDefectNumberSearchModalButton.addEventListener("click", () => closeModal("defectNumberSearchModal"));

  setupModalEventListeners(["defectNumberSearchModal", "programSearchModal"]);
}

/**  수정 폼 초기화 함수 */
async function initializeEditForm() {
  // 모든 비동기 호출을 병렬로 실행
  const [
    { majorCategoryCodes },
    { testStageList },
    { defectTypeList },
    { defectSeverityList },
    { userName, authorityCode },
    { programTypes },
    { defectDetail, attachments, fixAttachments },
  ] = await Promise.all([
    getMajorCategoryCodes(),
    getTestStageList(),
    getDefectTypeList(),
    getDefectSeverityList(),
    checkSession(),
    getProgramTypes(),
    getDefectDetail(),
  ]);

  const SELECT_DATA = {
    [SELECT_ID.MAJOR_CATEGORY]: majorCategoryCodes,
    [SELECT_ID.TEST_STAGE]: testStageList,
    [SELECT_ID.DEFECT_TYPE]: defectTypeList,
    [SELECT_ID.DEFECT_SEVERITY]: defectSeverityList,
    [SELECT_ID.PROGRAM_TYPE_FOR_PROGRAM]: programTypes,
  };

  Object.values(SELECT_ID).forEach((selectId) => initializeSelect(selectId, SELECT_DATA[selectId]));

  document.getElementById("defectRegistrar").value = userName;
  document.getElementById("defectDiscoveryDate").value = getCurrentDate();
  document.getElementById("defectRegistrarInfo").textContent =
    "※ [ 유의사항 ] 결함조치결과 재테스트 후 이상이 없는 경우 “수정자 확인일” 입력 - 재결함 발생한 경우 결함내용과 첨부파일에 추가";

  await initializePageByReferer();
  await fillDefectEditFormValues({ ...defectDetail, attachments, fixAttachments });

  determineEditableFields(userName, authorityCode);
}

async function initializeSubCategorySelect(selectedMajorCategoryCode = "") {
  // 업무 대분류 '전체'를 선택한 경우 중분류 리셋
  if (selectedMajorCategoryCode === "") initializeSelect(SELECT_ID.SUB_CATEGORY, []);
  else {
    const { subCategoryCodes } = await getSubCategoryCodes(selectedMajorCategoryCode);

    initializeSelect(SELECT_ID.SUB_CATEGORY, subCategoryCodes);
  }
}

async function fillDefectEditFormValues(data) {
  setSelectValueByText("majorCategory", data.majorCategory || "");

  // 업무 대분류에 따른 업무 중분류 초기화
  const selectedMajorCategoryCode = document.getElementById("majorCategory").value;

  await initializeSubCategorySelect(selectedMajorCategoryCode);

  setSelectValueByText("subCategory", data.subCategory || "");
  setSelectValueByText("testStage", data.testStage || "");
  setSelectValueByText("defectType", data.defectType || "");
  setSelectValueByText("defectSeverity", data.defectSeverity || "");

  document.getElementById("seq").value = data.seq || "";
  document.getElementById("testId").value = data.testId || "";
  document.getElementById("defectRegistrar").value = data.defectRegistrar || "";
  document.getElementById("defectDescription").value = data.defectDescription || "";
  document.getElementById("programId").value = data.programId || "";
  document.getElementById("programName").value = data.programName || "";
  document.getElementById("programType").value = data.programType || "";
  document.getElementById("defectHandler").value = data.defectHandler || "";
  document.getElementById("defectResolutionDetails").value = data.defectResolutionDetails || "";
  document.getElementById("pl").value = data.pl || "";
  document.getElementById("plComments").value = data.plComments || "";
  document.getElementById("defectRegistrarComment").value = data.defectRegistrarComment || "";
  document.getElementById("defectStatus").value = data.defectStatus || "";
  document.getElementById("originalDefectNumber").value = data.originalDefectNumber || "";

  document.getElementById("defectDiscoveryDate").value = convertDate(data.defectDiscoveryDate);
  document.getElementById("defectScheduledDate").value = convertDate(data.defectScheduledDate);
  document.getElementById("defectCompletionDate").value = convertDate(data.defectCompletionDate);
  document.getElementById("plConfirmDate").value = convertDate(data.plConfirmDate);
  document.getElementById("defectRegConfirmDate").value = convertDate(data.defectRegConfirmDate);

  // PL
  const plRadioButtons = document.querySelectorAll('input[name="plDefectJudgeClass"]');

  // data.plDefectJudgeClass의 값에 맞는 라디오 버튼 선택
  plRadioButtons.forEach((radioButton) => {
    if (radioButton.value === data.plDefectJudgeClass) {
      radioButton.checked = true; // 값이 일치하는 라디오 버튼을 선택
    }
  });

  // 첨부파일 채우기
  const fileInputId = "defectFileInput";
  const fileOutputId = "defectFileOutput";

  const fixFileInputId = "defectFixFileInput";
  const fixFileOutputId = "defectFixFileOutput";

  const fileIds = data.attachments.map((file) => file.seq);
  const fixFileIds = data.fixAttachments.map((file) => file.seq);

  await loadFilesToInput(fileInputId, fileIds); // 파일 input에 가져온 파일들 채우기
  await loadFilesToInput(fixFileInputId, fixFileIds);

  addFiles(fileInputId); // 전역변수에 가져온 첨부파일 저장
  addFiles(fixFileInputId);

  updateFilePreview(fileInputId, fileOutputId); // 파일 목록 렌더링
  updateFilePreview(fixFileInputId, fixFileOutputId);
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

/** 이전 페이지 경로에 따라 현재 페이지 초기값 할당 */
async function initializePageByReferer() {
  // 이전 페이지
  const referer = getReferer(document.referrer);

  // 현재 URL에서 쿼리 파라미터를 가져옴
  const { majorCategory, subCategory, programId, programName, programType, developer, pl, testStage, testId } =
    Object.fromEntries(new URLSearchParams(window.location.search).entries());

  const majorCategorySelect = document.getElementById("majorCategory");
  const subCategorySelect = document.getElementById("subCategory");
  const testStageSelect = document.getElementById("testStage");
  const testIdInput = document.getElementById("testId");
  const plInput = document.getElementById("pl");

  if (referer === REFERER.DEV_PROGRESS || referer === REFERER.TEST_PROGRESS) {
    document.getElementById("programId").value = programId;
    document.getElementById("programName").value = programName;
    document.getElementById("programType").value = programType;
    document.getElementById("defectHandler").value = developer;

    plInput.value = pl;

    for (let option of majorCategorySelect.options) {
      if (option.textContent === majorCategory) {
        option.selected = true;

        // 대분류 선택 후 중분류 fetch
        await initializeSubCategorySelect(option.value);

        break;
      }
    }

    for (let option of subCategorySelect.options) {
      if (option.textContent === subCategory) {
        option.selected = true;
        break;
      }
    }

    // 자동 세팅 값 수정 막기
    plInput.readOnly = true;
    testIdInput.readOnly = true;
  }

  // 개발진행관리 > 결함 수정
  if (referer === REFERER.DEV_PROGRESS) {
    const programSearchButton = document.getElementById("programSearchButton");

    testIdInput.value = `UT-${programId}`;

    for (let option of testStageSelect.options) {
      if (option.textContent === "단위테스트") {
        option.selected = true;
        break;
      }
    }

    // 자동 세팅 값 수정 막기
    testStageSelect.classList.add("readonly");
    majorCategorySelect.classList.add("readonly");
    subCategorySelect.classList.add("readonly");

    programSearchButton.disabled = true;
  }

  // 테스트 진행관리 > 결함 수정
  if (referer === REFERER.TEST_PROGRESS) {
    testIdInput.value = testId;

    for (let option of testStageSelect.options) {
      if (option.textContent === testStage) {
        option.selected = true;
        break;
      }
    }

    // 자동 세팅 값 수정 막기
    testStageSelect.classList.add("readonly");
  }
}

// 결함 상세 조회
async function getDefectDetail() {
  try {
    const defectId = new URLSearchParams(window.location.search).get("seq");
    const query = new URLSearchParams({ seq: defectId }).toString();

    const { defectDetail, attachments, fixAttachments } = await tmsFetch(`/defectDetail?${query}`);

    return { defectDetail, attachments, fixAttachments };
  } catch (error) {
    console.error(error.message, `결함(${defectId}) 상세정보를 불러오지 못 했습니다.`);
  }
}

// 결함 수정
async function edit(event) {
  event.preventDefault(); // 폼 제출 기본 동작 방지

  const confirmed = confirm("결함 정보를 수정하시겠습니까?");
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
  const defectId = new URLSearchParams(window.location.search).get("seq");
  defectData["seq"] = defectId;

  formData.forEach((value, key) => {
    if (key !== "defectAttachments" && key !== "defectFixAttachments") {
      defectData[key] = value;
    }
  });

  // notice 데이터 추가 (JSON 형태로 묶기)
  newFormData.append("defect", new Blob([JSON.stringify(defectData)], { type: "application/json" }));

  // 파일 추가
  files.forEach((file) => {
    newFormData.append("file", file); // Blob으로 감싸지 않고 File 객체 그대로 추가
  });

  fixFiles.forEach((file) => {
    newFormData.append("fixfile", file);
  });

  showSpinner();

  try {
    const { status } = await tmsFetch("/defectEdit", {
      method: "POST",
      body: newFormData,
    });

    const success = status === "success";

    if (success) {
      alert(`결함 정보 수정이 완료되었습니다.`);
      event.target.reset(); // 폼 초기화
      window.location.href = "/tms/defect"; // 결함 목록으로 이동
    }
  } catch (error) {
    alert(error.message + "\n다시 시도해주세요.");
  } finally {
    hideSpinner();
  }
}

/** 로그인 이름과 비교하여 인풋 수정 권한 설정 */
function determineEditableFields(loginName, authorityCode) {
  const defectRegistrar = document.getElementById("defectRegistrar").value;
  const defectHandler = document.getElementById("defectHandler").value;
  const pl = document.getElementById("pl").value;
  const AUTHORITY_CODE = { ADMIN: 1, PL: 4 };

  const fileRemoveButtons = document.querySelectorAll(".file-remove-button");
  fileRemoveButtons.forEach((button) => button.classList.add("hidden")); // 삭제 버튼 가리기

  // 로그인 직무 = “04” AND  로그인 이름 = PL명 : 전부 수정 가능
  if (authorityCode === AUTHORITY_CODE.ADMIN || (loginName === pl && authorityCode === AUTHORITY_CODE.PL)) {
    const plConfirmDateInput = document.getElementById("plConfirmDate");
    const plCommentsInput = document.getElementById("plComments");
    const plDefectJudgeClassRadioButtonGroup = document.getElementById("plDefectJudgeClassRadioButtonGroup");
    const defectNumberSearchButton = document.getElementById("defectNumberSearchButton");

    plConfirmDateInput.removeAttribute("readonly");
    plCommentsInput.removeAttribute("readonly");
    plDefectJudgeClassRadioButtonGroup.classList.remove("readonly");
    defectNumberSearchButton.removeAttribute("disabled");

    enableDefectRegistrarFields();
    enableDefectHandlerFields();
    return;
  }

  // 로그인 이름 = 결함 등록자
  if (loginName === defectRegistrar) {
    enableDefectRegistrarFields();
  }

  // 로그인 이름 = 조치담당자
  if (loginName === defectHandler) {
    enableDefectHandlerFields();
  }
}

/** 결함 등록자 관련 필드 활성화: 결함기본정보 + 결함등록자 재테스트 결과 항목 수정 가능 */
function enableDefectRegistrarFields() {
  // 결함기본정보
  const majorCategorySelect = document.getElementById("majorCategory");
  const subCategorySelect = document.getElementById("subCategory");
  const testStageSelect = document.getElementById("testStage");
  const defectDiscoveryDateInput = document.getElementById("defectDiscoveryDate");
  const defectTypeSelect = document.getElementById("defectType");
  const defectSeveritySelect = document.getElementById("defectSeverity");
  const defectDescriptionInput = document.getElementById("defectDescription");
  const defectFileSelectButton = document.getElementById("defectFileSelectButton");
  const programSearchButton = document.getElementById("programSearchButton");
  const fileInputId = "defectFileInput";
  const fileOutputId = "defectFileOutput";

  majorCategorySelect.classList.remove("readonly");
  subCategorySelect.classList.remove("readonly");
  testStageSelect.classList.remove("readonly");
  defectTypeSelect.classList.remove("readonly");
  defectSeveritySelect.classList.remove("readonly");

  defectDiscoveryDateInput.removeAttribute("readonly");
  defectDescriptionInput.removeAttribute("readonly");

  defectFileSelectButton.removeAttribute("hidden");
  programSearchButton.removeAttribute("disabled");

  updateFilePreview(fileInputId, fileOutputId);

  // 결함등록자 재테스트 결과
  const defectRegConfirmDateInput = document.getElementById("defectRegConfirmDate");
  const defectRegistrarCommentInput = document.getElementById("defectRegistrarComment");

  defectRegConfirmDateInput.removeAttribute("readonly");
  defectRegistrarCommentInput.removeAttribute("readonly");
}

/** 조치 담당자 관련 필드 활성화: 개발자 조치결과 – 조치예정일(yyyy-mm-dd), 조치완료일, 조치내역, 조치 첨부파일 */
function enableDefectHandlerFields() {
  const defectScheduledDateInput = document.getElementById("defectScheduledDate");
  const defectCompletionDateInput = document.getElementById("defectCompletionDate");
  const defectResolutionDetailsInput = document.getElementById("defectResolutionDetails");
  const defectFixFileSelectButton = document.getElementById("defectFixFileSelectButton");
  const fixFileInputId = "defectFixFileInput";
  const fixFileOutputId = "defectFixFileOutput";

  defectScheduledDateInput.removeAttribute("readonly");
  defectCompletionDateInput.removeAttribute("readonly");
  defectResolutionDetailsInput.removeAttribute("readonly");

  defectFixFileSelectButton.removeAttribute("hidden");
  updateFilePreview(fixFileInputId, fixFileOutputId);
}

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
  loadFilesToInput,
  updateFilePreview,
  convertDate,
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
const devProgressEditForm = document.getElementById("devProgressEditForm");
const majorCategorySelect = document.getElementById("majorCategory");
const goBackButton = document.getElementById("goBackButton");

const fileInput = document.getElementById("fileInput");
const fileSelectButton = document.getElementById("fileSelectButton");

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

  // 프로그램 개발 정보 수정
  devProgressEditForm.addEventListener("submit", edit);

  // 뒤로가기
  goBackButton.addEventListener("click", goBack);
}

/**  수정 폼 초기화 함수 */
async function initializeEditForm() {
  // 모든 비동기 호출을 병렬로 실행
  const [
    { majorCategoryCodes },
    { programTypes },
    { programDetailTypes },
    { levels },
    { programStatusList },
    { testResultList },
    { devProgressDetail, defectCounts, attachments },
  ] = await Promise.all([
    getMajorCategoryCodes(),
    getProgramTypes(),
    getProgramDetailTypes(),
    getLevels(),
    getProgramStatusList(),
    getTestResultList(),
    getDevProgressDetail(),
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

  Object.values(SELECT_ID).forEach((selectId) => initializeSelect(selectId, SELECT_DATA[selectId])); // 셀렉트 초기화

  // 상세 정보 초기화
  fillFormValues({ ...devProgressDetail, ...defectCounts, attachments });
}

async function fillFormValues(data) {
  setSelectValueByText("majorCategory", data.majorCategory || "");

  // 업무 대분류에 따른 업무 중분류 초기화
  const selectedMajorCategoryCode = document.getElementById("majorCategory").value;

  await initializeSubCategorySelect(selectedMajorCategoryCode);

  setSelectValueByText("subCategory", data.subCategory || "");
  setSelectValueByText("programType", data.programType || "");
  setSelectValueByText("programDetailType", data.programDetailType || "");
  setSelectValueByText("programStatus", data.programStatus || "");
  setSelectValueByText("priority", data.priority || "");
  setSelectValueByText("difficulty", data.difficulty || "");
  setSelectValueByText("plTestResult", data.plTestResult || "");
  setSelectValueByText("thirdTestResult", data.thirdTestResult || "");
  setSelectValueByText("itTestResult", data.itTestResult || "");
  setSelectValueByText("busiTestResult", data.busiTestResult || "");

  document.getElementById("minorCategory").value = data.minorCategory || "";
  document.getElementById("programId").value = data.programId || "";
  document.getElementById("programName").value = data.programName || "";
  document.getElementById("className").value = data.className || "";
  document.getElementById("screenId").value = data.screenId || "";
  document.getElementById("screenName").value = data.screenName || "";
  document.getElementById("developer").value = data.developer || "";
  document.getElementById("reqId").value = data.reqId || "";
  document.getElementById("deletionHandler").value = data.deletionHandler || "";
  document.getElementById("deletionReason").value = data.deletionReason || "";

  document.getElementById("deletionDate").value = convertDate(data.deletionDate);
  document.getElementById("plannedStartDate").value = convertDate(data.plannedStartDate);
  document.getElementById("plannedEndDate").value = convertDate(data.plannedEndDate);
  document.getElementById("actualStartDate").value = convertDate(data.actualStartDate);
  document.getElementById("actualEndDate").value = convertDate(data.actualEndDate);
  document.getElementById("devTestEndDate").value = convertDate(data.devtestendDate);
  document.getElementById("plTestScdDate").value = convertDate(data.plTestScdDate);
  document.getElementById("plTestCmpDate").value = convertDate(data.plTestCmpDate);
  document.getElementById("thirdPartyTestDate").value = convertDate(data.thirdPartyTestDate);
  document.getElementById("thirdPartyConfirmDate").value = convertDate(data.thirdPartyConfirmDate);
  document.getElementById("itTestDate").value = convertDate(data.itTestDate);
  document.getElementById("itConfirmDate").value = convertDate(data.itConfirmDate);
  document.getElementById("busiTestDate").value = convertDate(data.busiTestDate);
  document.getElementById("busiConfirmDate").value = convertDate(data.busiConfirmDate);

  document.getElementById("pl").value = data.pl || "";
  document.getElementById("plTestNotes").value = data.plTestNotes || "";

  document.getElementById("thirdPartyTestMgr").value = data.thirdPartyTestMgr || "";
  document.getElementById("thirdPartyTestNotes").value = data.thirdPartyTestNotes || "";
  document.getElementById("thirdPartyDefect").value = `${data.thirdPartyDefectCount}/${data.thirdPartySolutionCount}`;

  document.getElementById("itMgr").value = data.itMgr || "";
  document.getElementById("itTestNotes").value = data.itTestNotes || "";
  document.getElementById("itDefect").value = `${data.itDefectCount}/${data.itSolutionCount}`;

  document.getElementById("busiMgr").value = data.busiMgr || "";
  document.getElementById("busiTestNotes").value = data.busiTestNotes || "";
  document.getElementById("busiDefect").value = `${data.busiDefectCount}/${data.busiSolutionCount}`;

  document.getElementById("devStatus").value = data.devStatus || "";
  document.getElementById("lastModifier").value = data.lastModifier || "";

  const { year: yy, monthValue: mm, dayOfMonth: dd } = data.lastModifiedDate;
  const lastModifiedDate = `${yy}-${String(mm).padStart(2, "0")}-${String(dd).padStart(2, "0")}`;

  document.getElementById("lastModifiedDate").value = lastModifiedDate.length === 10 ? lastModifiedDate : "";

  // 첨부파일 채우기
  const fileInputId = "fileInput";
  const fileOutputId = "fileOutput";

  const fileIds = data.attachments.map((file) => file.seq);

  await loadFilesToInput(fileInputId, fileIds); // 파일 input에 가져온 파일들 채우기

  addFiles(fileInputId); // 전역변수에 가져온 첨부파일 저장
  updateFilePreview(fileInputId, fileOutputId); // 파일 목록 렌더링
}

function setSelectValueByText(selectId, text) {
  const selectElement = document.getElementById(selectId);
  const options = selectElement.options ?? [];

  for (let i = 0; i < options.length; i++) {
    if (options[i].textContent === text) {
      selectElement.value = options[i].value;
      break;
    }
  }
}

async function initializeSubCategorySelect(selectedMajorCategoryCode) {
  // 업무 대분류 '전체'를 선택한 경우 중분류 리셋
  if (selectedMajorCategoryCode === "") initializeSelect(SELECT_ID.SUB_CATEGORY, []);
  else {
    const { subCategoryCodes } = await getSubCategoryCodes(selectedMajorCategoryCode);

    initializeSelect(SELECT_ID.SUB_CATEGORY, subCategoryCodes);
  }
}

async function getDevProgressDetail() {
  try {
    // 현재 URL에서 쿼리 파라미터를 가져옴
    const devProgressId = new URLSearchParams(window.location.search).get("seq");
    const query = new URLSearchParams({ seq: devProgressId }).toString();

    const {
      devProgress: devProgressDetail,
      defectCounts,
      attachments = [],
    } = await tmsFetch(`/devProgressDetail?${query}`);

    return { devProgressDetail, defectCounts, attachments };
  } catch (error) {
    console.error("프로그램 개발 상세 정보를 불러오지 못 했습니다.", error);
  }
}

// 프로그램 개발 수정
async function edit(event) {
  event.preventDefault(); // 폼 제출 기본 동작 방지

  const confirmed = confirm("프로그램 개발 정보를 수정하시겠습니까?");
  if (!confirmed) return;

  // 기존 FormData 객체 생성
  const formData = new FormData(event.target);

  // 새로운 FormData 객체 생성
  const newFormData = new FormData();

  // 파일 추출 (다중 파일 지원)
  const files = formData.getAll("file"); // 다중 파일을 배열로 가져오기

  const devProgressData = {};
  const devProgressId = new URLSearchParams(window.location.search).get("seq");

  // seq 추가
  devProgressData["seq"] = devProgressId;

  // 파일을 제외한 나머지 데이터 추출
  formData.forEach((value, key) => {
    if (key != "file") {
      devProgressData[key] = value;
    }
  });

  // 데이터 추가 (JSON 형태로 묶기)
  newFormData.append("devProgress", new Blob([JSON.stringify(devProgressData)], { type: "application/json" }));

  // 파일 추가
  files.forEach((file) => {
    newFormData.append("file", file); // Blob으로 감싸지 않고 File 객체 그대로 추가
  });

  showSpinner();

  try {
    const { status } = await tmsFetch("/devProgressEdit", {
      method: "POST",
      body: newFormData,
    });

    const success = status === "success";

    if (success) {
      alert(`프로그램 개발 정보 수정이 완료되었습니다.`);
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
  const confirmed = confirm("수정을 취소하시겠습니까? 작성 중인 정보는 저장되지 않습니다.");

  if (!confirmed) return;

  window.history.back();
}

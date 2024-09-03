import {
  renderTMSHeader,
  initializeSelect,
  getMajorCategoryCodes,
  getSubCategoryCodes,
  getProgramTypes,
  getProgramDetailTypes,
  getLevels,
  getProgramStatusList,
  getDevStatusList,
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
  DEV_STATUS: "devStatus",
};

// DOM 요소들
const majorCategorySelect = document.getElementById("majorCategory");

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
    { devStatusList },
  ] = await Promise.all([
    getMajorCategoryCodes(),
    getProgramTypes(),
    getProgramDetailTypes(),
    getLevels(),
    getProgramStatusList(),
    getDevStatusList(),
  ]);

  const SELECT_DATA = {
    [SELECT_ID.MAJOR_CATEGORY]: majorCategoryCodes,
    [SELECT_ID.PROGRAM_TYPE]: programTypes,
    [SELECT_ID.PROGRAM_DETAIL_TYPE]: programDetailTypes,
    [SELECT_ID.PRIORITY]: levels,
    [SELECT_ID.DIFFICULTY]: levels,
    [SELECT_ID.PROGRAM_STATUS]: programStatusList,
    [SELECT_ID.DEV_STATUS]: devStatusList,
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

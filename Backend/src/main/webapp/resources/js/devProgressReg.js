import {
  tmsFetch,
  renderTMSHeader,
  initializeSelect,
  getMajorCategoryCodes,
  getSubCategoryCodes,
  showSpinner,
  hideSpinner,
} from "./common.js";

// 문서 로드 시 초기화
document.addEventListener("DOMContentLoaded", init);

// 초기화 함수
function init() {
  renderTMSHeader();
  setupEventListeners();
}

// 이벤트 핸들러 설정
function setupEventListeners() {
  console.log("?");
}

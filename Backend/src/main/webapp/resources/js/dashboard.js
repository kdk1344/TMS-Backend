import {
  tmsFetch,
  openModal,
  closeModal,
  closeModalOnClickOutside,
  renderTMSHeader,
  convertDate,
  getCurrentDate,
  updateFilePreview,
  showSpinner,
  hideSpinner,
  downloadFile,
} from "./common.js";

// DOM 요소들

// 문서 로드 시 초기화
document.addEventListener("DOMContentLoaded", init);

// 초기화 함수
function init() {
  renderTMSHeader();
  //   setupEventListeners();
  loadInitialNoticeDetail();
}
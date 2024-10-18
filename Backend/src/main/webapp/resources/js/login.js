﻿import {
  tmsFetch,
  showSpinner,
  hideSpinner,
  redirectHomeOnLogin,
  getNotices,
  convertDate,
  openModal,
  closeModal,
  setupModalEventListeners,
} from "./common.js";

// DOM 요소
const loginForm = document.getElementById("loginForm");

// 문서 로드 시 초기화
document.addEventListener("DOMContentLoaded", init);

// 초기화 함수
function init() {
  redirectHomeOnLogin();
  loadSavedUserId();
  renderNotices();
  setupEventListeners();
}

// 로컬 스토리지에서 저장된 아이디를 입력 필드에 설정하는 함수
function loadSavedUserId() {
  const savedUserId = localStorage.getItem("savedUserId");
  const userIdInput = document.getElementById("userID");
  const rememberMeCheckbox = document.getElementById("rememberMe");

  if (savedUserId) {
    userIdInput.value = savedUserId;
    rememberMeCheckbox.checked = true;
  }
}

// 이벤트 핸들러 설정
function setupEventListeners() {
  if (loginForm) {
    loginForm.addEventListener("submit", login);
  }

  const testGuideModalId = "testGuideModal";

  document.getElementById("devProgressButton").addEventListener("click", () => {
    const modalTitle = "[개발 진행관리]절차안내";
    const modalHtmlContent = `
  <p>테스트에 참여하기 위해 다음 단계를 따라주세요.</p>

  <p>1. 개발 진행 관리: 프로젝트 진행 상황을 점검합니다.</p>
  <p>2. 제3자 테스트: 외부 업체가 테스트를 수행합니다.</p>
  <p>3. 통합 테스트: 각 모듈을 통합하여 전체 시스템을 테스트합니다.</p>
  <p>4. 결함 관리: 발견된 문제를 관리하고 해결합니다.</p>

  <p>위 단계를 모두 완료하시면 테스트 참여가 완료됩니다.</p>
`;
    renderTestGuideModal(modalTitle, modalHtmlContent);
    openModal(testGuideModalId);
  });

  document.getElementById("thirdPartyTestButton").addEventListener("click", () => {
    const modalTitle = "[제3자 테스트] 절차안내";
    const modalHtmlContent = `
  <p>테스트에 참여하기 위해 다음 단계를 따라주세요.</p>

  <p>1. 개발 진행 관리: 프로젝트 진행 상황을 점검합니다.</p>
  <p>2. 제3자 테스트: 외부 업체가 테스트를 수행합니다.</p>
  <p>3. 통합 테스트: 각 모듈을 통합하여 전체 시스템을 테스트합니다.</p>
  <p>4. 결함 관리: 발견된 문제를 관리하고 해결합니다.</p>

  <p>위 단계를 모두 완료하시면 테스트 참여가 완료됩니다.</p>
`;
    renderTestGuideModal(modalTitle, modalHtmlContent);
    openModal(testGuideModalId);
  });

  document.getElementById("integrationTestButton").addEventListener("click", () => {
    const modalTitle = "[통합테스트] 절차안내";
    const modalHtmlContent = `
  <p>테스트에 참여하기 위해 다음 단계를 따라주세요.</p>

  <p>1. 개발 진행 관리: 프로젝트 진행 상황을 점검합니다.</p>
  <p>2. 제3자 테스트: 외부 업체가 테스트를 수행합니다.</p>
  <p>3. 통합 테스트: 각 모듈을 통합하여 전체 시스템을 테스트합니다.</p>
  <p>4. 결함 관리: 발견된 문제를 관리하고 해결합니다.</p>

  <p>위 단계를 모두 완료하시면 테스트 참여가 완료됩니다.</p>
`;
    renderTestGuideModal(modalTitle, modalHtmlContent);
    openModal(testGuideModalId);
  });

  document.getElementById("defectButton").addEventListener("click", () => {
    const modalTitle = "[결함관리] 절차안내";
    const modalHtmlContent = `
  <p>테스트에 참여하기 위해 다음 단계를 따라주세요.</p>

  <p>1. 개발 진행 관리: 프로젝트 진행 상황을 점검합니다.</p>
  <p>2. 제3자 테스트: 외부 업체가 테스트를 수행합니다.</p>
  <p>3. 통합 테스트: 각 모듈을 통합하여 전체 시스템을 테스트합니다.</p>
  <p>4. 결함 관리: 발견된 문제를 관리하고 해결합니다.</p>

  <p>위 단계를 모두 완료하시면 테스트 참여가 완료됩니다.</p>
`;
    renderTestGuideModal(modalTitle, modalHtmlContent);
    openModal(testGuideModalId);
  });

  document.getElementById("closeTestGuideModalButton").addEventListener("click", () => closeModal(testGuideModalId));
  setupModalEventListeners([testGuideModalId]);
}

// 테스트 참여자 절차안내 모달 콘텐츠 표시
function renderTestGuideModal(modalTitle = "", modalHtmlContent = "", modalTextContent = "") {
  const titleElement = document.getElementById("testGuideModalTitle");
  const contentElement = document.getElementById("testGuideModalContent");

  // 모달 제목 설정
  titleElement.textContent = modalTitle;

  // HTML 콘텐츠가 있을 경우에는 HTML로 설정, 그렇지 않으면 텍스트 콘텐츠 설정
  if (modalHtmlContent.length > 0) {
    contentElement.innerHTML = modalHtmlContent;
  } else {
    contentElement.textContent = modalTextContent;
  }
}

// 공지사항 목록 표시
async function renderNotices() {
  const { notices } = await getNotices();
  const noticeList = document.getElementById("noticeList");

  if (noticeList) {
    noticeList.innerHTML = "";

    notices.forEach((notice) => {
      const row = document.createElement("li");

      row.innerHTML = `
        <span class="notice-post-date">${convertDate(notice.postDate)}</span>
        <span class="notice-title ellipsis">${notice.title}</span>
      `;

      noticeList.appendChild(row);
    });
  }
}

async function login(event) {
  event.preventDefault(); // 폼 제출 기본 동작 방지

  const formData = new FormData(event.target);
  const formDataObj = Object.fromEntries(formData.entries());

  if (document.getElementById("rememberMe").checked) {
    // 체크박스가 체크된 상태면 아이디를 로컬 스토리지에 저장
    localStorage.setItem("savedUserId", formDataObj.userID);
  } else {
    // 체크박스가 해제된 상태면 저장된 아이디 삭제
    localStorage.removeItem("savedUserId");
  }

  try {
    showSpinner();

    const response = await tmsFetch("/login", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(formDataObj),
    });

    const success = response.status === "success";
    if (success) window.location.href = "/tms/dashboard"; // 대시보드로 이동
  } catch (error) {
    alert(error.message);
  } finally {
    hideSpinner();
  }
}

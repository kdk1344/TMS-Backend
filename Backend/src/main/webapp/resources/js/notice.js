import {
  tmsFetch,
  openModal,
  closeModal,
  setupModalEventListeners,
  renderTMSHeader,
  convertDate,
  getCurrentDate,
  updateFilePreview,
  showSpinner,
  hideSpinner,
  initializeDateFields,
  setupPagination,
  checkSession,
  AUTHORITY_CODE,
  getNotices,
} from "./common.js";

let currentPage = 1;

// DOM 요소들
const noticeFilterForm = document.getElementById("noticeFilterForm");

const noticeRegisterForm = document.getElementById("noticeRegisterForm");
const fileSelectButtonForRegister = document.getElementById("fileSelectButtonForRegister");
const fileInputForRegister = document.getElementById("fileInputForRegister");
const fileOutputForRegister = document.getElementById("fileOutputForRegister");

const noticeTableBody = document.getElementById("noticeTableBody");
const selectAllNoticeCheckbox = document.getElementById("selectAllNoticeCheckbox");
const deleteNoticeButton = document.getElementById("deleteNoticeButton");

const openNoticeRegisterModalButton = document.getElementById("openNoticeRegisterModalButton");
const closeNoticeRegisterModalButton = document.getElementById("closeNoticeRegisterModalButton");

// 모달 아이디
const MODAL_ID = {
  NOTICE_REGISTER: "noticeRegisterModal",
};

// 문서 로드 시 초기화
document.addEventListener("DOMContentLoaded", init);

// 초기화 함수
function init() {
  renderTMSHeader();
  setupEventListeners();
  renderNotices();
  initializeDateFields("startPostDateForFilter", "endPostDateForFilter");
  initializePageByUser();
}

// 이벤트 핸들러 설정
function setupEventListeners() {
  // 공지사항 테이블 클릭 이벤트 핸들러
  if (noticeTableBody) {
    noticeTableBody.addEventListener("click", (event) => {
      const clickedElement = event.target;

      if (clickedElement.type === "checkbox") {
        return;
      }

      const row = event.target.closest("tr");

      if (row) {
        const noticeID = row.querySelector(".notice-seq").textContent.trim();
        window.location.href = `/tms/ntdetail?seq=${noticeID}`;
      }
    });
  }

  // 공지사항 등록 폼 이벤트 핸들러
  if (noticeRegisterForm) {
    noticeRegisterForm.addEventListener("submit", registerNotice);

    // 파일 선택 버튼 클릭 시 파일 입력 필드 클릭
    fileSelectButtonForRegister.addEventListener("click", () => {
      fileInputForRegister.click();
    });

    // 파일 입력 필드에서 파일이 선택되면 파일 목록 업데이트
    fileInputForRegister.addEventListener("change", () => {
      const fileInputId = "fileInputForRegister";
      const fileListOutputId = "fileOutputForRegister";

      updateFilePreview(fileInputId, fileListOutputId);
    });
  }

  //   공지사항 필터 폼 제출 및 리셋 이벤트 핸들러
  if (noticeFilterForm) {
    noticeFilterForm.addEventListener("submit", submitNoticeFilter);
    noticeFilterForm.addEventListener("reset", resetNoticeFilter);
  }

  // 공지사항 삭제 버튼 클릭 이벤트 핸들러
  if (deleteNoticeButton) {
    deleteNoticeButton.addEventListener("click", deleteNotice);
  }

  // 전체 선택 체크박스 클릭 이벤트 핸들러
  if (selectAllNoticeCheckbox) {
    selectAllNoticeCheckbox.addEventListener("click", toggleAllCheckboxes);
  }

  // 모달 열기 및 닫기 버튼 이벤트 핸들러
  if (openNoticeRegisterModalButton && closeNoticeRegisterModalButton) {
    openNoticeRegisterModalButton.addEventListener("click", () => {
      // 현재 날짜를 입력 필드에 설정
      noticeRegisterForm.elements["postDate"].value = getCurrentDate();

      openModal(MODAL_ID.NOTICE_REGISTER);
    });

    closeNoticeRegisterModalButton.addEventListener("click", () => {
      noticeRegisterForm.reset();
      fileOutputForRegister.innerHTML = ""; // 첨부파일 프리뷰 비우기
      closeModal(MODAL_ID.NOTICE_REGISTER);
    });
  }

  // 모달 외부 클릭 시 닫기 버튼 이벤트 핸들러
  setupModalEventListeners(Object.values(MODAL_ID));
}

async function initializePageByUser() {
  const { authorityCode } = await checkSession();
  const accessCode = new Set([AUTHORITY_CODE.ADMIN, AUTHORITY_CODE.PM, AUTHORITY_CODE.TEST_MGR]);
  const buttonIds = ["openNoticeRegisterModalButton", "deleteNoticeButton"];
  const buttons = buttonIds.map((buttonId) => document.getElementById(buttonId));

  if (accessCode.has(authorityCode)) return;

  buttons.forEach((button) => button.classList.add("hidden")); // 등록, 삭제 버튼 가리기
}

// 공지사항 목록 표시
async function renderNotices(getNoticesProps = {}) {
  const { notices, totalPages } = await getNotices(getNoticesProps);
  if (noticeTableBody) {
    noticeTableBody.innerHTML = "";

    notices.forEach((notice) => {
      const row = document.createElement("tr");
      row.innerHTML = `
        <td><input type="checkbox" name="notice" value="${notice.seq}"></td>
        <td class="notice-seq">${notice.seq}</td>
        <td class="notice-post-date">${convertDate(notice.postDate)}</td>
        <td class="notice-title">${notice.title}</td>
        <td class="notice-content ellipsis">${notice.content}</td>
      `;
      noticeTableBody.appendChild(row);
    });

    setupPagination({ paginationElementId: "noticePagination", totalPages, currentPage, changePage });
  }
}

// 페이지 변경
function changePage(page) {
  currentPage = page;

  const startDate = document.getElementById("startPostDateForFilter").value;
  const endDate = document.getElementById("endPostDateForFilter").value;
  const title = document.getElementById("titleForFilter").value.trim();
  const content = document.getElementById("contentForFilter").value;

  renderNotices({ page: currentPage, startDate, endDate, title, content });
}

// 공지사항 필터링
function submitNoticeFilter(event) {
  event.preventDefault(); // 폼 제출 기본 동작 방지

  const startDate = document.getElementById("startPostDateForFilter").value;
  const endDate = document.getElementById("endPostDateForFilter").value;
  const title = document.getElementById("titleForFilter").value.trim();
  const content = document.getElementById("contentForFilter").value;

  // 페이지를 1로 초기화하고 renderNotices 호출
  currentPage = 1;
  renderNotices({ page: currentPage, startDate, endDate, title, content });
}

// 공지사항 필터 리셋
function resetNoticeFilter() {
  this.reset(); // 폼 초기화
}

// 공지사항 등록
async function registerNotice(event) {
  event.preventDefault(); // 폼 제출 기본 동작 방지

  const confirmed = confirm("공지사항을 등록하시겠습니까?");
  if (!confirmed) return;

  // 기존 FormData 객체 생성
  const formData = new FormData(event.target);

  // 새로운 FormData 객체 생성
  const newFormData = new FormData();

  // 파일 추출 (다중 파일 지원)
  const files = formData.getAll("file"); // 다중 파일을 배열로 가져오기

  // 파일을 제외한 나머지 데이터 추출
  const noticeData = {};

  formData.forEach((value, key) => {
    if (key !== "file") {
      noticeData[key] = value;
    }
  });

  // notice 데이터 추가 (JSON 형태로 묶기)
  newFormData.append("notice", new Blob([JSON.stringify(noticeData)], { type: "application/json" }));

  // 파일 추가
  files.forEach((file) => {
    newFormData.append("file", file); // Blob으로 감싸지 않고 File 객체 그대로 추가
  });

  showSpinner();

  try {
    const { status } = await tmsFetch("/ntwrite", {
      method: "POST",
      body: newFormData,
    });

    const success = status === "success";

    if (success) {
      alert(`공지사항 등록이 완료되었습니다.`);
      event.target.reset(); // 폼 초기화
      closeModal(MODAL_ID.NOTICE_REGISTER); // 모달 닫기
      location.reload(); // 페이지 새로고침
    }
  } catch (error) {
    alert(error.message + "\n다시 시도해주세요.");
  } finally {
    hideSpinner();
  }
}

// 체크박스 전체 선택/해제
function toggleAllCheckboxes() {
  const checkboxes = document.querySelectorAll('input[name="notice"]');
  checkboxes.forEach((checkbox) => (checkbox.checked = selectAllNoticeCheckbox.checked));
}

// 공지사항 삭제
async function deleteNotice() {
  try {
    const confirmed = confirm("공지사항을 삭제하시겠습니까?");

    if (!confirmed) return;

    const selectedNoticeIDs = Array.from(document.querySelectorAll('input[name="notice"]:checked')).map(
      (checkbox) => checkbox.value
    );

    if (selectedNoticeIDs.length === 0) {
      alert("삭제할 공지사항을 선택해주세요.");
      return;
    }

    showSpinner();

    const { status } = await tmsFetch("/ntdelete", {
      method: "DELETE",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(selectedNoticeIDs),
    });

    const success = status === "success";

    if (success) {
      alert(`공지사항 삭제가 완료되었습니다.`);
      location.reload(); // 페이지 새로고침
    }
  } catch (error) {
    alert(error.message + "\n다시 시도해주세요.");
  } finally {
    hideSpinner();
  }
}

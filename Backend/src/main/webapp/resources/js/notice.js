import { tmsFetch, openModal, closeModal, closeModalOnClickOutside, renderTMSHeader, convertDate } from "./common.js";

let currentPage = 1;

// DOM 요소들
const noticeFilterForm = document.getElementById("noticeFilterForm");

const noticeTableBody = document.getElementById("noticeTableBody");
const noticePagination = document.getElementById("noticePagination");
const selectAllNoticeCheckbox = document.getElementById("selectAllNoticeCheckbox");

const openNoticeRegisterModalButton = document.getElementById("openNoticeRegisterModalButton");
const closeNoticeRegisterModalButton = document.getElementById("closeNoticeRegisterModalButton");

// 모달 아이디
const MODAL_ID = {
  NOTICE_REGISTER: "noticeRegisterModal",
  NOTICE_EDIT: "noticeEditModal",
  NOTICE_FILE_DOWNLOAD: "noticeFileDownloadModal",
};

// 문서 로드 시 초기화
document.addEventListener("DOMContentLoaded", init);

// 초기화 함수
function init() {
  renderTMSHeader();
  setupEventListeners();
  loadInitialNotices();
}

// 이벤트 핸들러 설정
function setupEventListeners() {
  // 공지사항 파일 업로드 버튼 클릭 이벤트 핸들러
  //   if (uploadNoticeFileButton) {
  //     uploadNoticeFileButton.addEventListener("click", () => {
  //       if (uploadNoticeFileInput) {
  //         uploadNoticeFileInput.click(); // 파일 선택 창 열기
  //       } else {
  //         console.error("File input element not found.");
  //       }
  //     });
  //   }

  // 공지사항 파일 업로드 인풋 change 이벤트 핸들러
  //   if (uploadNoticeFileInput) {
  //     uploadNoticeFileInput.addEventListener("change", uploadNoticeFile);
  //   }

  // 공지사항 테이블 클릭 이벤트 핸들러
  //   if (noticeTableBody) {
  //     // 공지사항 테이블에서 클릭된 행의 데이터 로드 (이벤트 위임)
  //     noticeTableBody.addEventListener("click", (event) => {
  //       const clickedElement = event.target;

  //       if (clickedElement.type === "checkbox") {
  //         return;
  //       }

  //       const row = event.target.closest("tr");

  //       if (row) {
  //         loadNoticeDataFromRow(row);
  //       }
  //     });
  //   }

  // 공지사항 수정 폼 제출 이벤트 핸들러
  //   if (noticeEditForm) {
  //     noticeEditForm.addEventListener("submit", editNotice);
  //   }

  // 공지사항 등록 폼 제출 이벤트 핸들러
  if (noticeRegisterForm) {
    // noticeRegisterForm.addEventListener("submit", registerNotice);
  }

  //   공지사항 필터 폼 제출 및 리셋 이벤트 핸들러
  if (noticeFilterForm) {
    noticeFilterForm.addEventListener("submit", submitNoticeFilter);
    noticeFilterForm.addEventListener("reset", resetNoticeFilter);
  }

  // 공지사항 삭제 버튼 클릭 이벤트 핸들러
  //   if (deleteNoticeButton) {
  //     deleteNoticeButton.addEventListener("click", deleteNotice);
  //   }

  // 전체 선택 체크박스 클릭 이벤트 핸들러
  //   if (selectAllNoticeCheckbox) {
  //     selectAllNoticeCheckbox.addEventListener("click", toggleAllCheckboxes);
  //   }

  // 모달 열기 및 닫기 버튼 이벤트 핸들러
  if (openNoticeRegisterModalButton && closeNoticeRegisterModalButton) {
    openNoticeRegisterModalButton.addEventListener("click", () => openModal(MODAL_ID.NOTICE_REGISTER));
    closeNoticeRegisterModalButton.addEventListener("click", () => closeModal(MODAL_ID.NOTICE_REGISTER));

    // openNoticeFileDownloadModalButton.addEventListener("click", () => {
    //   copyFilterValuesToDownloadForm(); // 공지사항 필터링 값 복사
    //   openModal(MODAL_ID.NOTICE_FILE_DOWNLOAD); // 모달 열기
    // });

    // closeNoticeEditModalButton.addEventListener("click", () => closeModal(MODAL_ID.NOTICE_EDIT));
  }

  // 모달 외부 클릭 시 닫기 버튼 이벤트 핸들러
  setupModalEventListeners();
}

function setupModalEventListeners() {
  const modals = Object.values(MODAL_ID); // 모든 모달 ID 배열

  modals.forEach((modalId) => {
    // 모달 외부 클릭 시 닫기 설정
    window.addEventListener("click", (event) => closeModalOnClickOutside(event, modalId));
  });
}

// 초기 공지사항 목록 로드
function loadInitialNotices() {
  getNotices();
}

/** @todo 게시일자 쿼리파라미터 2개 받는 걸로 수정 필요 */
async function getNotices({ page = 1, size = 10, postDate = "", title = "", content = "" } = {}) {
  try {
    const query = new URLSearchParams({ page, size, postDate, title, content }).toString();
    const { totalPages, notices } = await tmsFetch(`/notices?${query}`);

    displayNotices(notices, totalPages);
  } catch (error) {
    console.error("Error fetching notices:", error);
  }
}

// 공지사항 목록 표시
function displayNotices(notices, totalPages) {
  if (noticeTableBody) {
    noticeTableBody.innerHTML = "";

    /** @todo seq -> noticeID로 바꿔달라고 하기 */
    /** 공지사항 조회 시 제목만 보이는 게 자연스러울 것 같음 -> 논의하기 */
    notices.forEach((notice) => {
      const row = document.createElement("tr");
      row.innerHTML = `
        <td><input type="checkbox" name="notice" value="${notice.seq}"></td>
        <td class="notice-post-date">${convertDate(notice.postDate)}</td>
        <td class="notice-title">${notice.title}</td>
        <td class="notice-content">${notice.content}</td>
      `;
      noticeTableBody.appendChild(row);
    });

    setupPagination(totalPages);
  }
}

// 페이지네이션 설정
function setupPagination(totalPages) {
  if (noticePagination) {
    noticePagination.innerHTML = "";

    createPaginationButton("<", currentPage <= 1, () => changePage(currentPage - 1), "prev");

    for (let i = 1; i <= totalPages; i++) {
      createPaginationButton(i, i === currentPage, () => changePage(i));
    }

    createPaginationButton(">", currentPage >= totalPages, () => changePage(currentPage + 1), "next");
  }
}

// 페이지 버튼 생성
function createPaginationButton(text, disabled, onClick, buttonType = "page") {
  const button = document.createElement("button");
  button.textContent = text;
  button.disabled = disabled;

  // buttonType에 따라 클래스 추가
  if (buttonType === "page") {
    button.classList.toggle("active", disabled); // 현재 페이지 표시할 클래스 추가
  }

  button.addEventListener("click", onClick);
  noticePagination.appendChild(button);
}

// 페이지 변경
function changePage(page) {
  currentPage = page;

  const title = document.getElementById("titleForFilter").value.trim();
  const content = document.getElementById("contentForFilter").value;

  getNotices({ page: currentPage, title, content });
}

// 공지사항 필터링
function submitNoticeFilter(event) {
  event.preventDefault(); // 폼 제출 기본 동작 방지

  /** @todo 게시일자 기준 필터링 추가 필요 */
  const title = document.getElementById("titleForFilter").value.trim();
  const content = document.getElementById("contentForFilter").value;

  // 페이지를 1로 초기화하고 getNotices 호출
  currentPage = 1;
  getNotices({ page: currentPage, title, content });
}

// 공지사항 필터 리셋
function resetNoticeFilter() {
  this.reset(); // 폼 초기화
}

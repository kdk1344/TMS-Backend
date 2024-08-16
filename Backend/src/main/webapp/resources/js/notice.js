import { tmsFetch, openModal, closeModal, closeModalOnClickOutside, renderTMSHeader, convertDate } from "./common.js";

let currentPage = 1;

// DOM 요소들
const noticeTableBody = document.getElementById("noticeTableBody");
const noticePagination = document.getElementById("noticePagination");
const selectAllNoticeCheckbox = document.getElementById("selectAllNoticeCheckbox");

// 문서 로드 시 초기화
document.addEventListener("DOMContentLoaded", init);

// 초기화 함수
function init() {
  renderTMSHeader();
  //   setupEventListeners();
  loadInitialNotices();
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
  getNotices({ page: currentPage });
}

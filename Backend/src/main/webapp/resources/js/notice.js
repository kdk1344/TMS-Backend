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
} from "./common.js";

let currentPage = 1;

// DOM 요소들
const noticeFilterForm = document.getElementById("noticeFilterForm");

const noticeRegisterForm = document.getElementById("noticeRegisterForm");
const fileSelectButtonForRegister = document.getElementById("fileSelectButtonForRegister");
const fileInputForRegister = document.getElementById("fileInputForRegister");

const noticeTableBody = document.getElementById("noticeTableBody");
const noticePagination = document.getElementById("noticePagination");
const selectAllNoticeCheckbox = document.getElementById("selectAllNoticeCheckbox");
const deleteNoticeButton = document.getElementById("deleteNoticeButton");

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
      closeModal(MODAL_ID.NOTICE_REGISTER);
    });

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

async function getNotices({ page = 1, size = 10, startDate = "", endDate = "", title = "", content = "" } = {}) {
  try {
    const query = new URLSearchParams({ page, size, startDate, endDate, title, content }).toString();
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

  const startDate = document.getElementById("startPostDateForFilter").value;
  const endDate = document.getElementById("endPostDateForFilter").value;
  const title = document.getElementById("titleForFilter").value.trim();
  const content = document.getElementById("contentForFilter").value;

  getNotices({ page: currentPage, startDate, endDate, title, content });
}

// 공지사항 필터링
function submitNoticeFilter(event) {
  event.preventDefault(); // 폼 제출 기본 동작 방지

  const startDate = document.getElementById("startPostDateForFilter").value;
  const endDate = document.getElementById("endPostDateForFilter").value;
  const title = document.getElementById("titleForFilter").value.trim();
  const content = document.getElementById("contentForFilter").value;

  // 페이지를 1로 초기화하고 getNotices 호출
  currentPage = 1;
  getNotices({ page: currentPage, startDate, endDate, title, content });
}

// 공지사항 필터 리셋
function resetNoticeFilter() {
  this.reset(); // 폼 초기화
}

// 공지사항 등록
// async function registerNotice(event) {
//   event.preventDefault(); // 폼 제출 기본 동작 방지

//   const confirmed = confirm("공지사항을 등록하시겠습니까?");

//   if (!confirmed) return;

//   const formData = new FormData(event.target);

//   // 첨부파일을 formData에 추가
//   // for (const file of fileInputForRegister.files) {
//   //   console.log(file.name);
//   //   formData.append("file", file);
//   // }

//   // FormData 객체를 JSON 객체로 변환
//   // const formDataObj = Object.fromEntries(formData.entries());

//   showSpinner();

//   try {
//     const { status } = await tmsFetch("/ntwrite", {
//       method: "POST",
//       body: formData,
//     });

//     const success = status === "success";

//     if (success) {
//       alert(`공지사항 등록이 완료되었습니다.`);
//       event.target.reset(); // 폼 초기화
//       closeModal(MODAL_ID.NOTICE_REGISTER); // 모달 닫기
//     }
//   } catch (error) {
//     alert(error.message + "\n다시 시도해주세요.");
//   } finally {
//     hideSpinner();
//   }
// }

async function registerNotice(event) {
  event.preventDefault(); // 폼 제출 기본 동작 방지

  const confirmed = confirm("공지사항을 등록하시겠습니까?");
  if (!confirmed) return;

  // 기존 FormData 객체 생성
  const formData = new FormData(event.target);

  // 새로운 FormData 객체 생성
  const newFormData = new FormData();

  // 파일 추출
  const file = formData.get("file");
  if (file) {
    // 파일을 제외한 나머지 데이터 추출
    const entries = formData.entries();
    const noticeData = {};

    for (const [key, value] of entries) {
      if (key !== "file") {
        noticeData[key] = value;
      }
    }

    // notice 데이터 추가
    newFormData.append("notice", JSON.stringify(noticeData));
    // 파일 추가
    newFormData.append("file", file);
  } else {
    // 파일이 없는 경우에도 처리할 수 있도록
    const entries = formData.entries();
    const noticeData = {};

    for (const [key, value] of entries) {
      noticeData[key] = value;
    }

    newFormData.append("notice", JSON.stringify(noticeData));
  }

  showSpinner();

  try {
    const response = await tmsFetch("/ntwrite", {
      method: "POST",
      body: newFormData, // 새로운 FormData 객체를 body로 설정
    });

    const result = await response.json(); // 서버 응답을 JSON으로 변환
    const success = result.status === "success";

    if (success) {
      alert(`공지사항 등록이 완료되었습니다.`);
      event.target.reset(); // 폼 초기화
      closeModal(MODAL_ID.NOTICE_REGISTER); // 모달 닫기
    } else {
      throw new Error(result.message || "등록 실패");
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

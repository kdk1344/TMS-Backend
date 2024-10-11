import {
  tmsFetch,
  openModal,
  closeModal,
  setupModalEventListeners,
  renderTMSHeader,
  getCurrentDate,
  updateFilePreview,
  showSpinner,
  hideSpinner,
  downloadFile,
  loadFilesToInput,
  addFiles,
  checkSession,
  AUTHORITY_CODE,
} from "./common.js";

// DOM 요소들
const noticeEditForm = document.getElementById("noticeEditForm");
const fileInputForEdit = document.getElementById("fileInputForEdit");
const fileOutputForEdit = document.getElementById("fileOutputForEdit");

const openNoticeEditModalButton = document.getElementById("openNoticeEditModalButton");
const closeNoticeEditModalButton = document.getElementById("closeNoticeEditModalButton");

const deleteNoticeButton = document.getElementById("deleteNoticeButton");
const goNoticeListButton = document.getElementById("goNoticeListButton");

// 모달 아이디
const MODAL_ID = {
  NOTICE_EDIT: "noticeEditModal",
};

// 문서 로드 시 초기화
document.addEventListener("DOMContentLoaded", init);

// 초기화 함수
function init() {
  renderTMSHeader();
  setupEventListeners();
  loadInitialNoticeDetail();
  initializePageByUser();
}

// 이벤트 핸들러 설정
function setupEventListeners() {
  // 공지사항 수정 폼 이벤트 핸들러
  if (noticeEditForm) {
    noticeEditForm.addEventListener("submit", editNotice);

    // 파일 선택 버튼 클릭 시 파일 입력 필드 클릭
    fileSelectButtonForEdit.addEventListener("click", () => {
      fileInputForEdit.click();
    });

    // 파일 입력 필드에서 파일이 선택되면 파일 목록 업데이트
    fileInputForEdit.addEventListener("change", () => {
      const fileInputId = "fileInputForEdit";
      const fileListOutputId = "fileOutputForEdit";

      addFiles(fileInputId);
      updateFilePreview(fileInputId, fileListOutputId);
    });
  }

  if (deleteNoticeButton) {
    deleteNoticeButton.addEventListener("click", deleteNoticeDetail);
  }

  if (goNoticeListButton) {
    goNoticeListButton.addEventListener("click", () => (window.location.href = "/tms/notice"));
  }

  // 모달 열기 및 닫기 버튼 이벤트 핸들러
  if (openNoticeEditModalButton && closeNoticeEditModalButton) {
    openNoticeEditModalButton.addEventListener("click", () => {
      loadNoticeDataFromNoticeDetail(); // 공지사항 수정 폼에 데이터 채우기
      openModal(MODAL_ID.NOTICE_EDIT);
    });

    closeNoticeEditModalButton.addEventListener("click", () => {
      noticeEditForm.reset();
      fileOutputForEdit.innerHTML = ""; // 첨부파일 프리뷰 비우기
      closeModal(MODAL_ID.NOTICE_EDIT);
    });
  }

  // 모달 외부 클릭 시 닫기 버튼 이벤트 핸들러
  setupModalEventListeners(Object.values(MODAL_ID));
}

async function initializePageByUser() {
  const { authorityCode } = await checkSession();
  const accessCode = new Set([AUTHORITY_CODE.ADMIN, AUTHORITY_CODE.PM, AUTHORITY_CODE.TEST_MGR]);
  const buttonIds = ["openNoticeEditModalButton", "deleteNoticeButton"];
  const buttons = buttonIds.map((buttonId) => document.getElementById(buttonId));

  if (accessCode.has(authorityCode)) return;

  buttons.forEach((button) => button.classList.add("hidden")); // 수정, 삭제 버튼 가리기
}

// 공지사항 데이터 로드
async function loadInitialNoticeDetail() {
  const { noticeDetail, attachments } = await getNoticeDetail();

  displayNoticeDetail(noticeDetail, attachments);
}

async function getNoticeDetail() {
  try {
    // 현재 URL에서 쿼리 파라미터를 가져옴
    const noticeID = new URLSearchParams(window.location.search).get("seq");
    const query = new URLSearchParams({ seq: noticeID }).toString();

    const { notice, attachments } = await tmsFetch(`/ntdetail?${query}`);

    return { noticeDetail: notice, attachments };
  } catch (error) {
    console.error("Error fetching notice detail:", error);
  }
}

// 공지사항 상세 표시
function displayNoticeDetail(noticeDetail, attachments) {
  if (!noticeDetail) return;

  // 공지사항 상세 정보를 DOM 요소에 넣기
  document.getElementById("noticeSeq").textContent = noticeDetail.seq || "N/A";
  document.getElementById("noticePostDate").textContent = getCurrentDate(noticeDetail.postDate) || "N/A";
  document.getElementById("noticeTitle").textContent = noticeDetail.title || "N/A";
  document.getElementById("noticeContent").textContent = noticeDetail.content || "N/A";

  // 첨부파일 리스트 표시
  const attachmentsList = document.getElementById("noticeAttachments");
  attachmentsList.innerHTML = ""; // 기존 내용을 초기화

  if (attachments && attachments.length > 0) {
    attachments.forEach((attachment) => {
      const listItem = document.createElement("li");
      const link = document.createElement("a");

      listItem.id = attachment.seq;

      // 다운로드 링크 설정
      link.href = "#";
      link.textContent = attachment.fileName;
      link.addEventListener("click", (event) => {
        event.preventDefault();
        downloadFile(attachment.seq); // 클릭 시 파일 다운로드 시작
      });

      listItem.appendChild(link);
      attachmentsList.appendChild(listItem);
    });
  } else {
    // 첨부파일이 없을 경우
    const noAttachmentsItem = document.createElement("li");
    noAttachmentsItem.textContent = "첨부파일이 없습니다.";
    attachmentsList.appendChild(noAttachmentsItem);
  }
}

// 공지사항 수정
async function editNotice(event) {
  event.preventDefault(); // 폼 제출 기본 동작 방지

  const confirmed = confirm("공지사항을 수정하시겠습니까?");
  if (!confirmed) return;

  // 기존 FormData 객체 생성
  const formData = new FormData(event.target);

  // 새로운 FormData 객체 생성
  const newFormData = new FormData();

  // 파일 추출 (다중 파일 지원)
  const files = formData.getAll("file"); // 다중 파일을 배열로 가져오기

  // 파일을 제외한 나머지 데이터 추출
  const noticeData = {};
  const noticeID = new URLSearchParams(window.location.search).get("seq");

  noticeData["seq"] = noticeID;

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
    const { status } = await tmsFetch("/ntupdate", {
      method: "POST",
      body: newFormData,
    });

    const success = status === "success";

    if (success) {
      alert(`공지사항 수정이 완료되었습니다.`);
      event.target.reset(); // 폼 초기화
      closeModal(MODAL_ID.NOTICE_EDIT); // 모달 닫기
      location.reload(); // 페이지 새로고침
    }
  } catch (error) {
    alert(error.message + "\n다시 시도해주세요.");
  } finally {
    hideSpinner();
  }
}

// HTML 요소에서 공지사항 정보를 가져와서 공지사항 수정 폼에 미리 채우기
async function loadNoticeDataFromNoticeDetail() {
  const noticePostDate = document.getElementById("noticePostDate").textContent;
  const noticeTitle = document.getElementById("noticeTitle").textContent;
  const noticeContent = document.getElementById("noticeContent").textContent;
  const fileIDs = Array.from(document.querySelectorAll("#noticeAttachments li")).map((file) => file.id);

  // 폼에 공지사항 정보 입력
  document.getElementById("postDateForEdit").value = noticePostDate;
  document.getElementById("titleForEdit").value = noticeTitle;
  document.getElementById("contentForEdit").value = noticeContent;

  const fileInputId = "fileInputForEdit";
  const fileListOutputId = "fileOutputForEdit";

  await loadFilesToInput(fileInputId, fileIDs);

  updateFilePreview(fileInputId, fileListOutputId);

  // 모달 열기
  openModal(MODAL_ID.NOTICE_EDIT);
}

// 공지사항 삭제
async function deleteNoticeDetail() {
  try {
    const confirmed = confirm("공지사항을 삭제하시겠습니까?");

    if (!confirmed) return;

    const noticeID = new URLSearchParams(window.location.search).get("seq");

    showSpinner();

    const { status } = await tmsFetch("/ntdelete", {
      method: "DELETE",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify([noticeID]),
    });

    const success = status === "success";

    if (success) {
      alert(`공지사항 삭제가 완료되었습니다.`);
      window.location.href = "/tms/notice"; // 공지사항 페이지로 이동
    }
  } catch (error) {
    alert(error.message + "\n다시 시도해주세요.");
  } finally {
    hideSpinner();
  }
}

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
  document.getElementById("notice-seq").textContent = noticeDetail.seq || "N/A";
  document.getElementById("notice-post-date").textContent = getCurrentDate(noticeDetail.postDate) || "N/A";
  document.getElementById("notice-title").textContent = noticeDetail.title || "N/A";
  document.getElementById("notice-content").textContent = noticeDetail.content || "N/A";

  // 첨부파일 리스트 표시
  const attachmentsList = document.getElementById("notice-attachments");
  attachmentsList.innerHTML = ""; // 기존 내용을 초기화

  if (attachments && attachments.length > 0) {
    attachments.forEach((attachment) => {
      const listItem = document.createElement("li");
      const link = document.createElement("a");

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

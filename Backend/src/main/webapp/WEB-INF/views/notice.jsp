<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="ko">
  <head>
    <%@ include file="./common.jsp" %>
    <meta charset="UTF-8" />
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/notice.css" />

    <title>TMS 공지사항</title>
    <script type="module" src="${pageContext.request.contextPath}/resources/js/notice.js"></script>
  </head>

  <body>
    <header class="header">
      <!-- 공통 헤더 정보 동적으로 삽입-->
    </header>

    <main class="content">
      <h1 class="page-title">공지사항</h1>

      <!-- Register Modal -->
      <div id="noticeRegisterModal" class="modal">
        <!-- Modal content -->
        <div class="modal-content">
          <h2>공지사항 등록</h2>
          <form id="noticeRegisterForm" class="modal-form">
            <div class="modal-form-group">
              <label for="postDateForRegister">게시일자</label>
              <input type="date" id="postDateForRegister" name="postDate" readonly />
            </div>

            <div class="modal-form-group">
              <label for="titleForRegister">제목</label>
              <input
                type="text"
                id="titleForRegister"
                name="title"
                placeholder="제목을 입력해 주세요."
                required
                maxlength="100"
              />
            </div>

            <div class="modal-form-group">
              <label for="contentForRegister">게시내용</label>
              <textarea
                id="contentForRegister"
                name="content"
                placeholder="내용을 입력해 주세요."
                required
                maxlength="10000"
              ></textarea>
            </div>

            <div class="modal-form-group">
              <label for="fileInputForRegister">첨부파일</label>
              <div class="file-preview-container">
                <button type="button" class="file-button" id="fileSelectButtonForRegister">파일 선택</button>
                <input type="file" id="fileInputForRegister" class="hidden" name="file" multiple />
                <div id="fileOutputForRegister" class="file-preview"></div>
              </div>
            </div>

            <div class="flex-box justify-end">
              <button type="button" id="closeNoticeRegisterModalButton" class="cancel-button">취소</button>
              <button type="submit" class="save-button">저장</button>
            </div>
          </form>
        </div>
      </div>

      <!-- 첨부파일 미리보기 모달 -->
      <div id="previewModal" class="modal">
        <div class="modal-content hidden-scroll">
          <div class="flex-box justify-between">
            <h2>첨부파일 미리보기</h2>
            <button type="button" id="closePreviewModallButton" class="modal-close-button">
              <img src="${pageContext.request.contextPath}/resources/images/close_icon.png" alt="닫기" />
            </button>
          </div>

          <div id="previewImageInModalWrapper">
            <img id="previewImageInModal" />
          </div>
        </div>
      </div>

      <!-- 공지사항 조회 필터링 폼 -->
      <form id="noticeFilterForm">
        <div class="filter-container">
          <div class="form-group">
            <label for="startPostDateForFilter">게시일자</label>
            <input id="startPostDateForFilter" name="startDate" type="date" />
            <span>-</span>
            <input id="endPostDateForFilter" name="endDate" type="date" />
          </div>
          <div class="form-group">
            <label for="titleForFilter">제목</label>
            <input type="text" id="titleForFilter" name="title" />
          </div>
          <div class="form-group">
            <label for="contentForFilter">내용</label>
            <input type="text" id="contentForFilter" name="content" />
          </div>
        </div>
        <div class="flex-box align-end">
          <button type="reset" id="resetButton" class="reset-button">초기화</button>
          <button type="submit" id="searchNoticeButton">조회</button>
        </div>
      </form>

      <!-- 버튼 그룹 -->
      <div class="button-group">
        <div class="flex-box">
          <button id="openNoticeRegisterModalButton">등록</button>
          <button id="deleteNoticeButton" class="delete-button">삭제</button>
        </div>
      </div>

      <!-- 공지사항 목록 테이블 -->
      <div class="table-container">
        <table id="noticeTable">
          <thead>
            <tr>
              <th><input type="checkbox" id="selectAllNoticeCheckbox" /></th>
              <th class="notice-seq">번호</th>
              <th class="notice-post-date">게시일자</th>
              <th class="notice-title">제목</th>
              <th class="notice-content">게시내용</th>
            </tr>
          </thead>
          <tbody id="noticeTableBody">
            <!-- 공지사항 정보 동적으로 삽입-->
          </tbody>
        </table>
      </div>

      <div id="totalCount"></div>

      <!-- 페이지네이션 -->
      <div id="noticePagination" class="pagination">
        <!-- 페이지네이션 버튼 동적으로 삽입 -->
      </div>
    </main>
  </body>
</html>

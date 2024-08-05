<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>프로그램 개발 진행현황 조회</title>
    <style>
        body {
            font-family: Arial, sans-serif;
        }
        .container {
            width: 80%;
            margin: 0 auto;
        }
        .tabs {
            display: flex;
            margin-bottom: 20px;
        }
        .tabs button {
            flex: 1;
            padding: 10px;
            cursor: pointer;
            background-color: #f1f1f1;
            border: 1px solid #ccc;
            border-bottom: none;
        }
        .tabs button.active {
            background-color: #007bff;
            color: white;
        }
        .content {
            border: 1px solid #ccc;
            padding: 20px;
        }
        .form-group {
            margin-bottom: 10px;
        }
        .form-group label {
            display: inline-block;
            width: 120px;
        }
        .form-group input, .form-group select {
            width: 200px;
            padding: 5px;
        }
        .form-group .date-range input {
            width: 90px;
        }
        .buttons {
            margin-top: 20px;
        }
        .buttons button {
            padding: 10px 15px;
            margin-right: 10px;
        }
        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
        }
        table, th, td {
            border: 1px solid #ccc;
        }
        th, td {
            padding: 10px;
            text-align: left;
        }
        .pagination {
            margin-top: 20px;
            text-align: center;
        }
        .pagination button {
            padding: 5px 10px;
        }
    </style>
</head>
<body>
<div class="container">
    <div class="tabs">
        <button class="active">개발진행관리</button>
        <button>통합테스트 진행관리</button>
        <button>관리자</button>
    </div>
    <div class="content">
        <h2>프로그램 개발 진행현황 조회</h2>
        <form>
            <div class="form-group">
                <label for="task-category">업무 대분류</label>
                <select id="task-category" name="task-category"></select>
                <label for="task-type">업무 중분류</label>
                <select id="task-type" name="task-type"></select>
                <label for="program-type">프로그램구분</label>
                <select id="program-type" name="program-type"></select>
            </div>
            <div class="form-group">
                <label for="program-name">프로그램명</label>
                <input type="text" id="program-name" name="program-name"/>
                <label for="register-status">등록 상태</label>
                <select id="register-status" name="register-status"></select>
                <label for="developer">개발자</label>
                <input type="text" id="developer" name="developer"/>
                <label for="pl">PL</label>
                <input type="text" id="pl" name="pl"/>
            </div>
            <div class="form-group">
                <label for="progress-status">진행상태</label>
                <select id="progress-status" name="progress-status"></select>
            </div>
            <div class="form-group date-range">
                <label for="start-date">시작예정일</label>
                <input type="date" id="start-date" name="start-date"/>
                ~
                <input type="date" id="end-date" name="end-date"/>
                <label for="complete-date">완료예정일</label>
                <input type="date" id="complete-date" name="complete-date"/>
            </div>
            <div class="buttons">
                <button type="button" onclick="search()">조회</button>
                <button type="reset">초기화</button>
                <button type="button" onclick="uploadFile()">파일업로드</button>
                <button type="button" onclick="downloadFile()">파일다운로드</button>
            </div>
        </form>
        <table>
            <thead>
                <tr>
                    <th></th>
                    <th>대분류</th>
                    <th>중분류</th>
                    <th>프로그램 구분</th>
                    <th>프로그램 ID</th>
                    <th>프로그램 명</th>
                    <th>화면 ID</th>
                    <th>화면명</th>
                    <th>등록상태</th>
                    <th>개발자</th>
                    <th>시작예정일</th>
                    <th>완료예정일</th>
                    <th>시작일</th>
                    <th>완료일</th>
                    <th>PL 확인일</th>
                    <th>진행상태</th>
                </tr>
            </thead>
            <tbody>
                <!-- Data rows go here -->
            </tbody>
        </table>
        <div class="pagination">
            <button>1</button>
            <button>2</button>
            <button>3</button>
            <button>4</button>
            <button>5</button>
            <button>6</button>
            <button>7</button>
            <button>8</button>
            <button>9</button>
            <button>10</button>
        </div>
    </div>
</div>
<script>
    function search() {
        // Add search functionality
    }
    
    function uploadFile() {
        // Add file upload functionality
    }
    
    function downloadFile() {
        // Add file download functionality
    }
</script>
</body>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
	<!DOCTYPE html>
	<html lang="ko">

	<head>
		<meta charset="UTF-8">
			<link rel="stylesheet" type="text/css" href="../../resources/css/style.css">
			<title>System Administor</title>

	</head>
<body>
    <div class="container">
        <div class="header">
            <h1>TMS</h1>
            <div class="search-bar">
                <select name="category" id="category">
                    <option value="option1">카테고리1</option>
                    <option value="option2">카테고리2</option>
                </select>
                <input type="text" id="search-input" placeholder="조회 조건 입력">
                <button onclick="search()">조회</button>
                <button onclick="reset()">초기화</button>
            </div>
        </div>
        <div class="content">
            <div class="buttons">
                <button onclick="addNew()">등록</button>
                	<div style="background-color: #fff; border: 1px solid #000; padding: 20px; width: 300px; position: fixed; top: 50%; left: 50%; transform: translate(-50%, -50%); box-shadow: 0 4px 8px rgba(0,0,0,0.1); display:none;">
			            <h1>사용자 등록/수정</h1>
			            <form action="join" method="post">
			                <label for="userID">ID</label>
			                <input type="text" id="userID" name="userID" placeholder="수정시 변경불가">
			
			                <label for="userName">사용자명</label>
			                <input type="text" id="userName" name="userName">
			
			                <label for="password">패스위드</label>
			                <input type="password" id="password" name="password">
			
			                <label for="userType">직무</label>
			                <select id="userType" name="userType">
			                    <option value="type1">Type 1</option>
			                    <option value="type2">Type 2</option>
								<option value="type3">Type 3</option>
								<option value="type4">Type 4</option>
			                </select>
			
			                <button type="button" onclick="submitForm()">저장</button>
			                <button type="button" onclick="closePopup()">닫기</button>
			            </form>
			        </div>
                <button onclick="deleteSelected()">삭제</button>
            </div>
            <div class="list">
                <!-- 리스트 데이터 표시 영역 -->
            </div>
        </div>
        <div class="footer">
            <span>총회 리스팅 영역</span>
        </div>
    </div>
</body>
<script type="text/javasript">
</script>
</html>

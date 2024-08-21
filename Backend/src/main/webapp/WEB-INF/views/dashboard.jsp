<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>TMS 개발진행관리</title>
    <style>
        body {
            margin: 0;
            font-family: Arial, sans-serif;
            background-color: #000;
            color: #fff;
        }

        /* 헤더 스타일 */
        header {
            display: flex;
            background-color: #0056b3;
            padding: 10px;
            justify-content: space-between;
            align-items: center;
            height: 60px;
        }

        header .logo {
            color: #fff;
            font-size: 24px;
            font-weight: bold;
        }

        header .menu {
            display: flex;
            gap: 20px;
        }

        header .menu-item {
            background-color: #dcdcdc;
            color: #000;
            padding: 10px 20px;
            border-radius: 5px;
            font-weight: bold;
            cursor: pointer;
            text-decoration: none;
        }

        /* 메인 컨텐츠 스타일 */
        main {
            padding: 20px;
        }

        .main-display {
            background-color: #4574b6;
            height: 300px;
            display: flex;
            justify-content: center;
            align-items: center;
            font-size: 24px;
            color: #fff;
            border-radius: 5px;
            margin-bottom: 20px;
        }

        .content-box {
            background-color: #333;
            padding: 20px;
            border-radius: 5px;
            margin-bottom: 20px;
        }

        .content-box h2 {
            font-size: 20px;
            margin-bottom: 10px;
        }

        .content-box p {
            margin: 10px 0;
            border-bottom: 1px solid #555;
            padding-bottom: 10px;
        }

        /* 하단 네비게이션 */
        footer {
            background-color: #111;
            padding: 20px;
            text-align: center;
            border-top: 2px solid #333;
        }

        footer .pagination {
            display: flex;
            justify-content: center;
            gap: 10px;
        }

        footer .pagination span,
        footer .pagination a {
            background-color: #333;
            color: #fff;
            padding: 10px;
            border-radius: 50%;
            width: 30px;
            height: 30px;
            display: flex;
            justify-content: center;
            align-items: center;
            cursor: pointer;
        }

        footer .pagination .active {
            background-color: #0056b3;
        }
    </style>
</head>
<body>

<header>
    <div class="logo">TMS</div>
    <div class="menu">
        <a href="/dev-progress" class="menu-item">개발진행관리</a>
        <a href="/test-progress" class="menu-item">통합테스트 진행관리</a>
        <a href="/defect-progress" class="menu-item">결함진행관리</a>
        <% 
            Integer authorityCode = (Integer) session.getAttribute("authorityCode"); 
            if (authorityCode != null) { 
                // 특정 권한 코드에 따라 관리자 메뉴 노출 여부 결정
                if (authorityCode == 0) { 
        %>
                    <a href="/adminUser" class="menu-item">관리자</a>
        <% 
                }
            } 
        %>
        <a href="/tms/logout">로그아웃</a>
    </div>
</header>

<main>
    <!-- 화면 진입 시 최신 게시 내용 -->
    <div class="main-display">
        화면 진입 시 최신 게시 내용 Display
    </div>
    
     <!-- authorityCode 표시 -->
    <div class="content-box">
        <h2>현재 로그인된 사용자의 권한 코드</h2>
        <p>
            권한 코드: 
            <% if (authorityCode != null) { %>
                <%= authorityCode %>
            <% } else { %>
                권한 코드가 없습니다.
            <% } %>
        </p>
    </div>

    <!-- 기타 콘텐츠 상자 -->
    <div class="content-box">
        <h2>추가 정보</h2>
        <p>세부 내용 1</p>
        <p>세부 내용 2</p>
        <p>세부 내용 3</p>
    </div>
</main>

<footer>
    <div class="pagination">
        <span class="active">1</span>
        <a href="#">2</a>
        <a href="#">3</a>
        <a href="#">4</a>
        <a href="#">5</a>
    </div>
</footer>

</body>
</html>
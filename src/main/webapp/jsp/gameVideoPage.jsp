<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String path = request.getContextPath();
    String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path;
    String wsBasePath = "ws://" + request.getServerName() + ":" + request.getServerPort() + path;
%>
<html>
<head>
    <title>五子棋-我的录像</title>
    <%--使得js文件可以使用jsp路径--%>
    <basePath id="basePath" value="<%=basePath%>"></basePath>
    <basePath id="wsBasePath" value="<%=wsBasePath%>"></basePath>

    <link href="../css/gamePageCSS.css" rel="stylesheet">
    <link href="../css/chessCSS.css" rel="stylesheet">
</head>
<body>
<div id="gameForm">
    <ul id="gameBox">

    </ul>
</div>

<div id="gamePlayBox" data-room="" style="display: none">
    <div id="gamePlayBoxLeft" class="gamePlayBox-item">
        <div class="headIconImg gamePlayBox-item">
            <img id="roomMasterHeadIcon" alt="玩家一头像" src="../images/system/默认头像.png">
        </div>
        <div id="roomMasterLevel" class=" gamePlayBox-item" style="color: white">
            等级
        </div>
        <div id="roomMasterName" class=" gamePlayBox-item" style="color: white">
            未知
        </div>
        <div class="gamePlayBox-item">
            <img id="roomMasterChessColor" alt="棋子" src="../images/system/blackChess.png">
        </div>
    </div>
    <div id="gamePlayBoxCenter" class="gamePlayBox-item">
        <div id="chessContent">
            <div id="chessboard">
                <!--显示悬浮效果的棋子-->
                <div data-pointColor="black" id="hoverPoint"></div>
            </div>
            <div id="chessboardMask" style="display: none"></div>
        </div>
        <div id="gamePlayBoxBtnBar" class="gamePlayBox-item">
            <input type="button" id="speedUp" value="加速">
            <input type="button" id="speedDown" value="减速">
        </div>
        <div id="gamePlayBoxSpeedText" class="gamePlayBox-item" style="color: white">
            速度×1
        </div>
    </div>
    <div id="gamePlayBoxRight" class="gamePlayBox-item">
        <div class="headIconImg gamePlayBox-item">
            <img id="playerHeadIcon" alt="玩家二头像" src="../images/system/默认头像.png">
        </div>
        <div id="playerLevel" class="gamePlayBox-item" style="color: white">
            等级
        </div>
        <div id="playerName" class=" gamePlayBox-item" style="color: white">
            未知
        </div>
        <div class="gamePlayBox-item">
            <img id="playerChessColor" alt="棋子" src="../images/system/whiteChess.png">
        </div>
    </div>
</div>

<script src="../js/jquery.min.js"></script>
<script src="../js/jquery.cookie.js"></script>
<script src="../js/gameVideoPageJS.js"></script>
</body>
</html>

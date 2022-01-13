<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String path = request.getContextPath();
    String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path;
    String wsBasePath = "ws://" + request.getServerName() + ":" + request.getServerPort() + path;
%>
<html>
<head>
    <title>五子棋-我的游戏</title>

    <%--使得js文件可以使用jsp路径--%>
    <basePath id="basePath" value="<%=basePath%>"></basePath>
    <basePath id="wsBasePath" value="<%=wsBasePath%>"></basePath>

    <link href="../css/gamePageCSS.css" rel="stylesheet">
    <link href="../css/chessCSS.css" rel="stylesheet">
    <link href="../css/onlinePlayerList.css" rel="stylesheet">
</head>
<body>
<div id="gameForm">
    <ul id="gameBox">
        <li style="order: 1000">
            <div class="createGameTable">
                <a class="createGameBtn" href=".popBoxTime" rel="leanModal"></a>
            </div>
        </li>
    </ul>
    <!--遮罩-->
    <div id="lean_overlay"></div>

    <!--表单-->
    <div class="popBoxTime" style="display: none">
        <div class="startGameTimeForm">
            <div class="timeTop">
                <span>游戏参数设定</span>
                <span><a class="popBoxCloseBtn" href="javascript:void(0);"></a></span>
            </div>
            <div class="timeContent">
                <div class="timeContent-item">
                    <label for="oneStepTime">单手限时</label>
                    <input id="oneStepTime" name="oneStepTime" placeholder="单位秒(最少5秒)" type="number" min="5">
                </div>
                <div class="timeContent-item">
                    <label for="wholeGameTime">单局限时</label>
                    <input id="wholeGameTime" name="wholeGameTime" placeholder="单位秒(最少60秒)" type="number" min="60">
                </div>
                <div class="timeContent-item">
                    <label for="kind">选择棋子</label>
                    <select name="kind" id="kind">
                        <option value="1">黑棋</option>
                        <option value="2">白棋</option>
                    </select>
                </div>

                <input class="createTimeFormSubmit" type="submit" value="创建游戏">
            </div>
        </div>
    </div>

    <div id="onlinePlayerBox" data-room="" style="display: none">
        <ul id="onlinePlayerList">
            <li class="playerItem inputArea">
                <input id="findPlayerInput" placeholder="请输入玩家名称" type="text">
                <input id="findPlayerBtn" type="button" value="查找">
                <input id="closeBoxBtn" type="button" value="关闭">
            </li>
            <li class="playerItem">
                <span>头像</span>
                <span>用户名</span>
                <span>昵称</span>
                <span>等级</span>
                <span style="flex: 2;">操作</span>
            </li>
        </ul>
    </div>
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
        <div id="roomMasterStepTime" class="gamePlayBox-item"
             style="color: white; font-size: 30px;font-family: timeFont;">
            00:00:00
        </div>
        <div id="roomMasterGameTime" class="gamePlayBox-item"
             style="color: white; font-size: 30px;font-family: timeFont;">
            00:00:00
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
            <input type="button" id="giveUpBtn" value="认输">
            <input type="button" id="drawBtn" value="平局">
            <input type="button" id="regretStepBtn" value="悔棋">
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
        <div id="playerStepTime" class="gamePlayBox-item" style="color: white; font-size: 30px; font-family: timeFont;">
            00:00:00
        </div>
        <div id="playerGameTime" class="gamePlayBox-item" style="color: white; font-size: 30px;font-family: timeFont;">
            00:00:00
        </div>
        <div class="gamePlayBox-item">
            <img id="playerChessColor" alt="棋子" src="../images/system/whiteChess.png">
        </div>
    </div>
</div>

<script src="../js/jquery.min.js"></script>
<script src="../js/jquery.cookie.js"></script>
<script src="../js/jquery.leanModal.min.js"></script>

<script src="../js/gamePageJS.js"></script>
</body>
</html>

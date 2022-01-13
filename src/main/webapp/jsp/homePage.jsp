<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String path = request.getContextPath();
    String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path;
    String wsBasePath = "ws://" + request.getServerName() + ":" + request.getServerPort() + path;
%>
<html>
<head>
    <%--使得js文件可以使用jsp路径--%>
    <basePath id="basePath" value="<%=basePath%>"></basePath>
    <basePath id="wsBasePath" value="<%=wsBasePath%>"></basePath>

    <title>五子棋-主页</title>
    <link href="../images/system/五子棋图标.ico" rel="shortcut icon" sizes="16x16">

    <link href="../css/mainCSS.css" rel="stylesheet">
    <link href="../css/accordionBoxCSS.css" rel="stylesheet">
    <link href="../css/navListCSS.css" rel="stylesheet">
    <link href="../css/popBoxCSS.css" rel="stylesheet">
    <link href="../css/formCSS.css" rel="stylesheet">
</head>
<body>
<div id="backgroundBox">
    <div id="header">
        <h1 style="text-align: center;"></h1>
    </div>
    <div id="navbarTop">
        <div id="navbarLeft">
            <a class="textAndImage settingNavBtn" href="javascript:void(0);">
                <span><img alt="设置" src="../images/system/设置.png" style="width:20px; height:20px;"></span>
                <span>设置</span>
            </a>
        </div>
        <div id="navbarRight">
            <a class="textAndImage homeNavBtn" href="javascript:void(0);" style="display: none">
                <span><img alt="主页" src="../images/system/主页.png" style="width:20px; height:20px;"></span>
                <span>主页</span>
            </a>
            <a class="textAndImage registerNavBtn" href="javascript:void(0);">
                <span><img alt="注册" src="../images/system/注册.png" style="width:20px; height:20px;"></span>
                <span>用户注册</span>
            </a>
            <a class="textAndImage loginNavBtn" href="javascript:void(0);">
                <span><img alt="用户" src="../images/system/用户.png" style="width:20px; height:20px;"></span>
                <span>用户登录</span>
            </a>
            <a class="textAndImage userInfoNavBtn" href="javascript:void(0);" style="display: none;">
                <span><img alt="用户" src="../images/system/用户.png" style="width:20px; height:20px;"></span>
                <span>个人信息</span>
            </a>
            <a class="textAndImage logoutNavBtn" href="javascript:void(0);" style="display: none;">
                <span><img alt="登出" src="../images/system/登出.png" style="width:20px; height:20px;"></span>
                <span>登出</span>
            </a>
        </div>
    </div>
    <div id="main">
        <div class="navList" id="sideNavbar" style="display: none;">
            <ul>
                <!--使用active属性-->
                <li>
                    <div id="infoBox">
                        <div id="infoBoxTop">
                            <img id="infoBoxHeadIcon" alt="头像" src="../images/system/默认头像.png">
                        </div>
                        <div id="infoBoxBottom">
                            <div class="infoBoxItem">
                                <span>用户名</span>
                                <span>昵称</span>
                            </div>
                            <div class="infoBoxItem">
                                <span id="infoBoxUsername">未知</span>
                                <span id="infoBoxNickname">未知</span>
                            </div>
                            <div class="infoBoxItem">
                                <span><img id="infoBoxGender" alt="性别头像" src="../images/system/男.png"></span>
                                <span><img id="infoBoxAdmin" alt="性别头像" src="../images/system/管理员.png"></span>
                            </div>
                        </div>
                    </div>
                </li>
                <li><a class="pageJump" target="<%=basePath%>/gobang/welcomePage">欢迎</a></li>
                <li><a class="pageJump" target="<%=basePath%>/gobang/userManagePage">用户管理</a></li>
                <li><a class="pageJump" target="<%=basePath%>/gobang/gamePage">我的游戏</a></li>
                <li><a class="pageJump" target="<%=basePath%>/gobang/watchGamePage">我要观战</a></li>
                <li><a class="pageJump" target="<%=basePath%>/gobang/gameVideoPage">我的录像</a></li>
            </ul>
        </div>
        <div id="centerBox" style="align-items: center; justify-content: center;">
            <!--短文小提示-->
            <div class="boxAccordion">
                <div class="container"><div class="box Hover">
                    <div class="name"><h3>五子棋介绍:</h3>
                        <div class="name1">五子棋是起源于中国古代的传统黑白棋之一，在一块类似围棋的棋盘上，你和对手轮流放下黑白棋子，无论是横竖还是斜。
                            只要有五颗相同颜色的棋子连成一线即可 获得一句胜利。它不仅能使人增强思维能力，提高智力，而且富含丰富的哲理，有助于修身养性。
                            现在五子棋有“连珠”、“连五子”、“五子连珠”、“串珠”、 “五目”、“五目碰”、“五格”等多种称谓。
                        </div>
                    </div>
                </div>
                    <div class="box">
                        <div class="name"><h3>五子棋规则:</h3>
                            <div class="name1">1、对局双方各执一色棋子</div>
                            <div class="name1">2、空棋盘开局</div>
                            <div class="name1">3、黑先白后，交替下子</div>
                            <div class="name1">4、棋子下在棋盘空白点上</div>
                            <div class="name1">5、棋子下定后，不得移动或拿掉</div>
                            <div class="name1">6、先形成五子连线者获胜</div>
                        </div>
                    </div>
                    <div class="box">
                        <div class="name"><h3>五子棋的兵法和口诀:</h3>
                            <div class="name1">1、先手要攻，后手要守，以攻为守，以守待攻。攻守转换，慎思变化，先行争夺，地破天惊。</div>
                            <div class="name1">2、守取外势，攻聚内力，八卦易守，成角易攻。阻断分隔，稳如泰山，不思争先，胜如登天。</div>
                            <div class="name1">3、初盘争二，终局抢三，留三不冲，变化万千。多个先手，细算次先，五子要点，次序在前。</div>
                            <div class="name1">4、斜线为阴，直线为阳，阴阳结合，防不胜防。连三连四，易见为明，跳三跳四，暗剑深藏。</div>
                        </div>
                    </div>
                    <div class="box">
                        <div class="name"><h3>五子棋的文案:</h3>
                            <div class="name1">1、人生如棋，识局者生，破局者存，掌局者胜</div>
                            <div class="name1">2、运筹千里风生面，落子铿然竹满胸</div>
                            <div class="name1">3、博弈人生由己定，输赢落子但凭天</div>
                            <div class="name1">4、人生如棋，识局者生，破局者存，掌局者胜</div>
                        </div>
                    </div>
                </div>
            </div>
            <!--对战主页-->
            <iframe id="iframeChangePage" src="" style="display: none"></iframe>
        </div>
    </div>
    <div id="footer">
        &copy; 2020 Copyright 五子棋小游戏
    </div>
</div>

<!--遮罩-->
<div class="mask"></div>
<!--弹窗-->
<div class="popBox">
    <!--通过js加载表单-->
    <div class="popContent loginForm" style="display: none;">
    </div>
    <div class="popContent registerForm" style="display: none">
    </div>
    <div class="popContent manageForm" style="display: none">
    </div>
</div>

<script src="../js/jquery.min.js"></script>
<script src="../js/jquery.ajaxFileUpload.js"></script>
<script src="../js/jquery.leanModal.min.js"></script>
<script src="../js/jquery.cookie.js"></script>

<script src="../js/mainJS.js"></script>
<script src="../js/popMaskBox.js"></script>
<script>
    /*存储websocket的类*/
    var websocket = null;

    /**
     * 用户登录成功后执行
     *
     * 与后端webSocket进行连接
     * 该方法在loginPage被调用，使得页面webSocket作用域变大
     * @param url webSocket连接地址
     */
    function initWebSocketConnect(url) {
        websocket = new WebSocket(url);
        console.log("websocket成功连接");

        /*初始化玩家个人信息*/
        $.ajax({
            url: basePath + "/gobang/getUserInfoAuto",
            data: {
                username: $.cookie("username")
            },
            method: "post",
            success: function (result) {
                console.log("用户展示信息为: ", result)
                if (result.code == 10) {
                    /*显示玩家信息*/
                    $("#infoBoxUsername").text(result.data["name"]);
                    $("#infoBoxNickname").text(result.data["nickname"]);
                    /*判断用户性别*/
                    if (result.data["gender"] == "男") {
                        $("#infoBoxGender").attr("src", "../images/system/男.png");
                    } else {
                        $("#infoBoxGender").attr("src", "../images/system/女.png");
                    }
                    /*判断用户是否是管理员*/
                    if (result.data["admin"] == true) {
                        $("#infoBoxAdmin").attr("src", "../images/system/管理员.png");
                    } else {
                        $("#infoBoxAdmin").attr("src", "../images/system/非管理员.png");
                    }
                }
            },
            error: function (e) {
                console.log(e);
            },
            async: false
        });

    }

    $(function () {
        /*导航页跳转页面*/
        $(".pageJump").click(function () {
            $("#frameContentRight").attr("src", $(this).attr("target"));
        });

        /*登出操作*/
        $(".logout").click(function () {
            $.post($(this).attr('target'), function (result) {
                console.log(result);
                if (result.code == 10) {
                    alert("登出成功");
                } else {
                    alert("登出失败, 请先登录");
                }
            });
        });
    });
</script>
</body>
</html>

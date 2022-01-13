var basePath = $("#basePath").attr("value");
var wsBasePath = $("#wsBasePath").attr("value");

/*---------------------------------------
                websocket
-----------------------------------------*/
var ws = parent.websocket;

function initWebSocket() {
    ws.onopen = function (evt) {
        console.log("连接", evt);
    }
    /**
     * websocket处理对局
     * @param evt
     */
    ws.onmessage = function (evt) {
        var parse = JSON.parse(evt.data);
        var code = parse["code"];
        var data = parse["data"];
        console.log("websocket.code: ", code, "websocket.data: ", data);

        var $hoverPoint = $("#hoverPoint");
        var $gamePlayBox = $("#gamePlayBox");
        var $chessboardMask = $("#chessboardMask");
        var $squareBox = $(".squareBox");
        /*方法处理*/
        if (code == 201) {
            /*创建房间*/
            createGameRoom(data);
        } else if (code == 202) {
            /*邀请玩家*/
            var roomMaster = data["roomMaster"];
            var roomID = data["roomID"];
            /*玩家接受到邀请*/
            if (confirm("是否接受玩家" + roomMaster + "的邀请")) {
                /*接受邀请则*/
                /*发送后端websocket*/
                ws.send(JSON.stringify({
                    code: 203,
                    msg: "",
                    data: {
                        roomID: roomID.toString(),
                        roomMaster: roomMaster,
                        isAgree: true
                    }
                }));
            } else {
                /*发送后端websocket*/
                ws.send(JSON.stringify({
                    code: 203,
                    msg: "",
                    data: {
                        roomID: roomID.toString(),
                        roomMaster: roomMaster,
                        isAgree: false
                    }
                }));
            }
        } else if (code == 203) {
            /*玩家是否确认邀请*/
            /*isAgree为false和true时后端穿的参数不同*/
            var isAgree = data["isAgree"];
            /*如果玩家拒绝则提示*/
            if (isAgree == false) {
                var invitedPlayer = data["invitedPlayer"];
                alert(invitedPlayer + "拒绝了您的邀请");
            } else if (isAgree == true) {
                var usernameCookie = $.cookie("username");
                var roomMasterNickname = data["roomMasterNickname"];
                var playerNickname = data["playerNickname"];
                /*判断是否是房主*/
                if (usernameCookie == playerNickname) {
                    createGameRoom(data);
                } else if (usernameCookie == roomMasterNickname) { //是房主则添加玩家信息
                    var $item = $("#gameForm_" + data["roomID"]);
                    var playerGender = data["playerGender"];
                    var playerLevel = data["playerLevel"];
                    var playerKind = data["playerKind"];
                    var playerImgUrl = data["playerImgUrl"];
                    $item.find(".playerTwoName").text(playerNickname);
                    $item.find(".playerTwoGender").text(playerGender);
                    $item.find(".playerTwoLevel").text(playerLevel);
                    $item.find(".playerTwoHeadIcon").attr("src", playerImgUrl);
                    if (playerKind == 1) { //黑棋
                        $($item).find(".playerTwoKind").text("执黑");
                    } else if (playerKind == 2) {   //白棋
                        $($item).find(".playerTwoKind").text("执白");
                    }
                    /*关闭邀请玩家的盒子*/
                    $("#onlinePlayerBox").fadeOut();
                    /*禁用邀请，直到玩家退出*/
                    $item.find(".inviteBtn").prop("disabled", true);
                    $item.find(".kickBtn").prop("disabled", false);
                    $item.find(".beginBtn").prop("disabled", false);
                }
            }
        } else if (code == 204) {
            /*踢出*/
            var kickOutPlayer = data["kickOutPlayer"];
            /*如果被踢出的是玩家*/
            if (kickOutPlayer == $.cookie("username")) {
                alert("你被房主踢出");
                $("#li_" + data["roomID"]).remove();
            } else {
                var $item = $("#gameForm_" + data["roomID"]);
                $item.find(".playerTwoName").text("未知");
                $item.find(".playerTwoGender").text("未知");
                $item.find(".playerTwoLevel").text("未知");
                $item.find(".playerTwoHeadIcon").attr("src", "../images/system/默认头像.png");
                $($item).find(".playerTwoKind").text("未知");

                $item.find(".inviteBtn").prop("disabled", false);
                $item.find(".kickBtn").prop("disabled", true);
                $item.find(".beginBtn").prop("disabled", true);
            }
        } else if (code == 205) {
            /*开始游戏*/
            var roomID = data["roomID"];
            var $item = $("#gameForm_" + data["roomID"]);
            var startPlayer = data["startPlayer"];
            /*显示棋盘，关闭表单*/
            initChessboard();
            $("#gameForm").fadeOut(function () {
                $gamePlayBox.css({
                    "display": "flex"
                });
                /*记录房间号*/
                $gamePlayBox.attr("data-room", roomID);
            });
            /*赋值*/
            countOne = roomMasterStepTime = playerStepTime = $($item).find(".countOne").text();
            countTwo = roomMasterGameTime = playerGameTime = $($item).find(".countTwo").text();
            /*房主的信息栏*/
            $gamePlayBox.find("#roomMasterHeadIcon").attr("src", $($item).find(".playerOneHeadIcon").attr("src"));
            if ("执黑" == $($item).find(".playerOneKind").text()) {
                $gamePlayBox.find("#roomMasterChessColor").attr("src", "../images/system/blackChess.png");
            } else {
                $gamePlayBox.find("#roomMasterChessColor").attr("src", "../images/system/whiteChess.png");
            }
            $gamePlayBox.find("#roomMasterLevel").text($($item).find(".playerOneLevel").text());
            $gamePlayBox.find("#roomMasterName").text($($item).find(".playerOneName").text());
            $gamePlayBox.find("#roomMasterStepTime").text(formatTime(roomMasterStepTime));
            $gamePlayBox.find("#roomMasterGameTime").text(formatTime(roomMasterGameTime));
            /*玩家的信息栏*/
            $gamePlayBox.find("#playerHeadIcon").attr("src", $($item).find(".playerTwoHeadIcon").attr("src"));
            if ("执黑" == $($item).find(".playerTwoKind").text()) {
                $gamePlayBox.find("#playerChessColor").attr("src", "../images/system/blackChess.png");
            } else {
                $gamePlayBox.find("#playerChessColor").attr("src", "../images/system/whiteChess.png");
            }
            $gamePlayBox.find("#playerLevel").text($($item).find(".playerTwoLevel").text());
            $gamePlayBox.find("#playerName").text($($item).find(".playerTwoName").text());
            $gamePlayBox.find("#playerStepTime").text(formatTime(playerStepTime));
            $gamePlayBox.find("#playerGameTime").text(formatTime(playerGameTime));
            /*判断是否先手*/
            if (startPlayer != $.cookie("username")) {
                $hoverPoint.attr("data-pointColor", "white");
                $hoverPoint.css("background-image", "url('/images/system/白棋子.png')");
                $hoverPoint.css("display", "none");
                /*棋子悬浮效果，对每个格子的判断*/
                $squareBox.unbind("mousemove");
                $chessboardMask.css({
                    "display": "block"
                });
                $("#gamePlayBoxBtnBar input").prop("disabled", true);
            } else {
                $hoverPoint.attr("data-pointColor", "black");
                $hoverPoint.css("background-image", "url('/images/system/黑棋子.png')");
                $hoverPoint.css("display", "block");
                $chessboardMask.css({
                    "display": "none"
                });
                $("#gamePlayBoxBtnBar input").prop("disabled", false);
            }
            /*先手计时*/
            if (startPlayer == $gamePlayBox.find("#roomMasterName").text()) {
                roomMasterStepTimeInterval = setInterval(roomMasterStepTimer, 1000);
                roomMasterGameTimeInterval = setInterval(roomMasterGameTimeTimer, 1000);

            } else {
                playerStepTimeInterval = setInterval(playerStepTimeTimer, 1000);
                playerGameTimeInterval = setInterval(playerGameTimeTimer, 1000);
            }
            $("#regretStepBtn").prop("disabled", true);
        } else if (code == 206) {
            var $item = $("#gameForm_" + data["roomID"]);
            /*下棋*/
            /*判断输赢*/
            if (data["isWin"] == true) {
                /*如果胜利者为本人*/
                if (data["winner"] == $.cookie("username")) {
                    alert("您胜利了");
                } else {
                    alert(data["winner"] + "胜利了");
                }
                /*退出对战*/
                $gamePlayBox.fadeOut(function () {
                    $("#gameForm").css({"display": "flex"});
                    /*清空棋盘的棋子*/
                    $(".chess").remove();
                    clearInterval(roomMasterStepTimeInterval);
                    clearInterval(roomMasterGameTimeInterval);
                    clearInterval(playerStepTimeInterval);
                    clearInterval(playerGameTimeInterval);
                });
                location.reload();
            } else {
                /*判断下棋者*/
                if (data["chessPlayer"] == $gamePlayBox.find("#roomMasterName").text()) {
                    roomMasterStepTime = countOne;
                    $gamePlayBox.find("#roomMasterStepTime").text(formatTime(roomMasterStepTime));
                    clearInterval(roomMasterStepTimeInterval);
                    clearInterval(roomMasterGameTimeInterval);
                    playerStepTimeInterval = setInterval(playerStepTimeTimer, 1000);
                    playerGameTimeInterval = setInterval(playerGameTimeTimer, 1000);
                } else {
                    playerStepTime = countOne;
                    $gamePlayBox.find("#playerStepTime").text(formatTime(playerStepTime));
                    clearInterval(playerStepTimeInterval);
                    clearInterval(playerGameTimeInterval);
                    roomMasterStepTimeInterval = setInterval(roomMasterStepTimer, 1000);
                    roomMasterGameTimeInterval = setInterval(roomMasterGameTimeTimer, 1000);
                }
                /*禁止下棋，使用解除绑定*/
                if (data["chessPlayer"] == $.cookie("username")) {
                    /*禁止下棋*/
                    $hoverPoint.css("display", "none");
                    /*棋子悬浮效果，对每个格子的判断*/
                    $squareBox.unbind("mousemove");
                    $chessboardMask.css({
                        "display": "block"
                    });
                    $("#gamePlayBoxBtnBar input:not(#regretStepBtn)").prop("disabled", true);
                    $("#regretStepBtn").prop("disabled", false);
                } else {
                    /*可以下棋*/
                    $squareBox.mousemove(function (mouse) {
                        mouseMove(this, 30, mouse);
                    });
                    $hoverPoint.css("display", "block");
                    $chessboardMask.css({
                        "display": "none"
                    });
                    $("#gamePlayBoxBtnBar input:not(#regretStepBtn)").prop("disabled", false);
                    $("#regretStepBtn").prop("disabled", true);
                }
                /*如果是落子玩家则无需再次落子*/
                if (data["chessPlayer"] != $.cookie("username")) {
                    getAndSetChess(data, 30);
                }
            }
        } else if (code == 207) {
            /*移除房间*/
            $("#li_" + data).remove();
        } else if (code == 208) {
            /*认输*/
            var giveUpPlayer = data["giveUpPlayer"];
            var $item = $("#gameForm_" + data["roomID"]);
            /*认输*/
            /*判断认输的玩家*/
            if (giveUpPlayer != $.cookie("username")) {
                alert("玩家" + giveUpPlayer + "认输");
            } else {
                alert("您认输了");
            }
            /*退出对战*/
            $gamePlayBox.fadeOut(function () {
                $("#gameForm").css({"display": "flex"});
                /*清空棋盘的棋子*/
                $(".chess").remove();
            });
            location.reload();
        } else if (code == 209) {
            /*发送平局*/
            var drawPlayer = data["drawPlayer"];
            var roomID = data["roomID"];
            if (confirm("是否接受玩家" + drawPlayer + "的平局请求")) {
                /*接受邀请则*/
                /*发送后端websocket*/
                ws.send(JSON.stringify({
                    code: 210,
                    msg: "",
                    data: {
                        "roomID": roomID,
                        "drawPlayer": drawPlayer,
                        "isAgree": true
                    }
                }));
            } else {
                /*发送后端websocket*/
                ws.send(JSON.stringify({
                    code: 210,
                    msg: "",
                    data: {
                        "roomID": roomID,
                        "drawPlayer": drawPlayer,
                        "isAgree": false
                    }
                }));
            }
        } else if (code == 210) {
            var $item = $("#gameForm_" + data["roomID"]);
            /*是否同意平局*/
            var isAgree = data["isAgree"];
            var refusePlayer = data["refusePlayer"];
            if (isAgree == true) {
                alert("双方达成平局");
                /*退出对战*/
                $gamePlayBox.fadeOut(function () {
                    $("#gameForm").css({"display": "flex"});
                    /*清空棋盘的棋子*/
                    $(".chess").remove();
                });
                location.reload();
            } else {
                alert("玩家" + refusePlayer + "拒绝平局");
            }
        } else if (code == 211) {
            /*请求悔棋*/
            var regretPlayer = data["regretPlayer"];
            var roomID = data["roomID"];
            if (confirm("是否接受玩家" + regretPlayer + "的悔棋")) {
                /*接受邀请则*/
                /*发送后端websocket*/
                ws.send(JSON.stringify({
                    code: 212,
                    msg: "",
                    data: {
                        "roomID": roomID,
                        "isAgree": true
                    }
                }));
            } else {
                /*发送后端websocket*/
                ws.send(JSON.stringify({
                    code: 212,
                    msg: "",
                    data: {
                        "roomID": roomID,
                        "isAgree": false
                    }
                }));
            }
        } else if (code == 212) {
            /*是否同意悔棋*/
            var $item = $("#gameForm_" + data["roomID"]);
            /*是否同意平局*/
            var isAgree = data["isAgree"];
            if (isAgree == true) {
                var stepX = data["stepX"];
                var stepY = data["stepY"];
                var color = data["color"];
                var time = data["time"];
                $("#chess_" + stepX + "_" + stepY).remove();
                /*给请求悔棋的玩家落子*/
                /*判断下棋者*/
                if (data["agreePlayer"] != $gamePlayBox.find("#roomMasterName").text()) {
                    roomMasterStepTime = countOne;
                    $gamePlayBox.find("#roomMasterStepTime").text(formatTime(roomMasterStepTime));
                    clearInterval(roomMasterStepTimeInterval);
                    clearInterval(roomMasterGameTimeInterval);
                    playerStepTimeInterval = setInterval(playerStepTimeTimer, 1000);
                    playerGameTimeInterval = setInterval(playerGameTimeTimer, 1000);
                } else {
                    playerStepTime = countOne;
                    $gamePlayBox.find("#playerStepTime").text(formatTime(playerStepTime));
                    clearInterval(playerStepTimeInterval);
                    clearInterval(playerGameTimeInterval);
                    roomMasterStepTimeInterval = setInterval(roomMasterStepTimer, 1000);
                    roomMasterGameTimeInterval = setInterval(roomMasterGameTimeTimer, 1000);
                }
                /*禁止下棋，使用解除绑定*/
                if (data["agreePlayer"] == $.cookie("username")) {
                    /*禁止下棋*/
                    $hoverPoint.css("display", "none");
                    /*棋子悬浮效果，对每个格子的判断*/
                    $squareBox.unbind("mousemove");
                    $chessboardMask.css({
                        "display": "block"
                    });
                    $("#gamePlayBoxBtnBar input:not(#regretStepBtn)").prop("disabled", true);
                    $("#regretStepBtn").prop("disabled", false);
                } else {
                    /*可以下棋*/
                    $squareBox.mousemove(function (mouse) {
                        mouseMove(this, 30, mouse);
                    });
                    $hoverPoint.css("display", "block");
                    $chessboardMask.css({
                        "display": "none"
                    });
                    $("#gamePlayBoxBtnBar input:not(#regretStepBtn)").prop("disabled", false);
                    $("#regretStepBtn").prop("disabled", true);
                }
            } else {
                var refusePlayer = data["refusePlayer"];
                alert("玩家" + refusePlayer + "拒绝悔棋");
            }
        }
    }

    ws.onclose = function (evt) {
        console.log("关闭", evt);
    }
    ws.onerror = function (evt) {
        console.log("出错", evt);
    }
}

/*---------------------------------------
            游戏界面创建表单主函数
-----------------------------------------*/
$(function () {
    /*设置弹窗*/
    $("a[rel*=leanModal]").leanModal({top: 200, overlay: 0.1, closeButton: ".popBoxCloseBtn"});
    $.ajax({
        url: basePath + "/gobang/getMyRoom",
        method: "post",
        data: {
            name: $.cookie("username")
        },
        success: function (result) {
            if (result.code == 10) {
                console.log("initGameRoom.data: ", result.data);
                initGameRoom(result.data);
            }
        },
        error: function (result) {
            console.log(result);
        },
        async: false
    });

    /*初始化连接*/
    initWebSocket();
    /*初始化表单按钮事件*/
    initFormBtn();
    /*初始化对战房间的按钮*/
    initGameBtn();
});

/**
 * 初始化表单按钮
 */
function initFormBtn() {
    var $onlinePlayerBox = $("#onlinePlayerBox");
    var $findPlayerBtn = $("#findPlayerBtn");
    var $closeBoxBtn = $("#closeBoxBtn");

    /*动态生成的表单中的按钮因此使用on方法绑定事件*/
    /*邀请按钮点击事件*/
    $(document).on("click", ".inviteBtn", function () {
        var roomID = $(this).attr("data-num");

        //获取在线用户并展示
        $.ajax({
            url: basePath + "/gobang/getOnlinePlayers",
            data: {},
            method: "post",
            success: function (result) {
                console.log("在线玩家: ", result.data);
                $(".playerItemDelete").remove();
                initOnlinePlayerList(result.data, roomID);
            },
            error: function (e) {
                console.log(e);
            },
            async: false
        });
        $onlinePlayerBox.fadeIn();
    });
    /*踢出按钮点击事件*/
    $(document).on("click", ".kickBtn", function () {
        var num = $(this).attr("data-num");
        /*发送后端websocket*/
        ws.send(JSON.stringify({
            code: 204,
            msg: "",
            data: {
                roomID: num
            }
        }));
    });
    /*开始按钮点击事件*/
    $(document).on("click", ".beginBtn", function () {
        var num = $(this).attr("data-num");
        /*开始游戏*/
        ws.send(JSON.stringify({
            code: 205,
            msg: "",
            data: {
                roomID: num
            }
        }));
    });
    /*删除按钮点击事件*/
    $(document).on("click", ".deleteIcon", function () {
        var num = $(this).attr("data-num");
        /*关闭房间*/
        ws.send(JSON.stringify({
            code: 207,
            msg: "",
            data: {
                roomID: num
            }
        }));
    });

    /*邀请列表邀请玩家按钮*/
    $(document).on("click", ".invitePlayerBtn", function () {
        var num = $(this).attr("data-num");
        var roomID = $(this).attr("data-room");
        var playerItem = "#playerItem_" + num;

        var username = $(playerItem).find(".username").text();
        console.log("邀请的玩家为： ", username);
        ws.send(JSON.stringify({
            code: 202,
            msg: "",
            data: {
                roomID: roomID,
                invitedPlayer: username
            }
        }));
    });

    /*查找玩家*/
    $findPlayerBtn.click(function () {
        $.ajax({
            url: basePath + "/gobang/findOnlinePlayers",
            data: {
                username: $("#findPlayerInput").val()
            },
            method: "post",
            success: function (result) {
                console.log("查找到的玩家: ", result.data);
                $(".playerItemDelete").fadeOut(function () {
                    $(".playerItemDelete").remove();
                    initOnlinePlayerList(result.data);
                });
            },
            error: function (e) {
                console.log(e);
            },
            async: false
        });
    });

    /*关闭展示在线玩家的盒子*/
    $closeBoxBtn.click(function () {
        /*清除表单数据*/
        $onlinePlayerBox.fadeOut(function () {
            $(".playerItemDelete").remove();
        });
    });

    /*弹窗表单按钮点击事件*/
    $(".createTimeFormSubmit").click(function () {
        var $oneStepTime = $("#oneStepTime");
        var $wholeGameTime = $("#wholeGameTime");
        var $kind = $("#kind");

        console.log("parseInt($oneStepTime.val()): " + parseInt($oneStepTime.val()));
        console.log("parseInt($wholeGameTime.val()): " + parseInt($wholeGameTime.val()));
        //input text=number时不能正常判断 有bug
        /*需要填写表单内容*/
        if ($oneStepTime.val() == "" || $oneStepTime.val() < 5) {
            alert("请正确输入单手限时");
        } else if ($wholeGameTime.val() == "" || $wholeGameTime.val() < 60) {
            alert("请正确输入单局限时");
        } else if (parseInt($oneStepTime.val()) > parseInt($wholeGameTime.val())) {
            /*表单时长限制*/
            alert("单手限时不能大于单局限时");
        } else if ($kind.val() == "") {
            /*表单时长限制*/
            alert("请选择棋子");
        } else {
            /*发送后端websocket*/
            ws.send(JSON.stringify({
                code: 201,
                msg: "",
                data: {
                    kind: $kind.val(),
                    stepTime: $oneStepTime.val(),
                    gameTime: $wholeGameTime.val()
                }
            }));
            /*关闭弹窗*/
            $(".popBoxCloseBtn").trigger("click");

            /*清空表单的值*/
            $oneStepTime.val("");
            $wholeGameTime.val("");
            $kind.val(1);
        }
    });
}

/**
 * 初始化对战按钮
 */
function initGameBtn() {
    var $giveUpBtn = $("#giveUpBtn");
    var $drawBtn = $("#drawBtn");
    var $regretStepBtn = $("#regretStepBtn");

    var $gamePlayBox = $("#gamePlayBox");
    var $chessboardMask = $("#chessboardMask");

    var roomID = $(this).attr("data-room");
    /*认输按钮*/
    $(document).on("click", "#giveUpBtn", function () {
        ws.send(JSON.stringify({
            code: 208,
            msg: "",
            data: {
                roomID: $gamePlayBox.attr("data-room")
            }
        }));
    });
    /*请求平局按钮*/
    $(document).on("click", "#drawBtn", function () {
        alert("请等待对方同意");
        ws.send(JSON.stringify({
            code: 209,
            msg: "",
            data: {
                roomID: $gamePlayBox.attr("data-room")
            }
        }));
    });
    /*请求悔棋按钮*/
    $(document).on("click", "#regretStepBtn", function () {
        alert("请等待对方同意");
        ws.send(JSON.stringify({
            code: 211,
            msg: "",
            data: {
                roomID: $gamePlayBox.attr("data-room")
            }
        }));
    });
}

/**
 * 查询所有房间
 * @param data
 */
function initGameRoom(data) {
    for (var i = 0; i < data.length; i++) {
        createGameRoom(data[i]);
    }
}

/**
 * 添加房间
 * @param data
 */
function createGameRoom(data) {
    var roomID = data["roomID"];
    var form = createForm(data);

    /*将创建好的表单内容放入flex布局中*/
    var $li = $("<li id='li_" + roomID + "'></li>");
    form.appendTo($li);
    $li.appendTo($("#gameBox"));
}

/**
 * 创建表单
 * 参数: roomID, stepTime, gameTime, roomMasterImgUrl, roomMasterNickname, roomMasterGender, roomMasterLevel, roomMasterKind, playerImgUrl, playerNickname, playerGender, playerLevel, playerKind
 * @param data
 */
function createForm(data) {
    var roomID = data["roomID"];
    var stepTime = data["stepTime"];
    var gameTime = data["gameTime"];
    var status = data["status"];

    var roomMasterImgUrl = data["roomMasterImgUrl"];
    var roomMasterNickname = data["roomMasterNickname"];
    var roomMasterGender = data["roomMasterGender"];
    var roomMasterLevel = data["roomMasterLevel"];
    var roomMasterKind = data["roomMasterKind"];

    var playerImgUrl = data["playerImgUrl"];
    var playerNickname = data["playerNickname"];
    var playerGender = data["playerGender"];
    var playerLevel = data["playerLevel"];
    var playerKind = data["playerKind"];

    /*表单*/
    var $item = $("<div class='gameForm' id='gameForm_" + roomID + "'>\n" +
        "    <div class='gameFormLeft'>\n" +
        "        <img alt='玩家一头像' class='playerOneHeadIcon' src='../images/system/默认头像.png'>\n" +
        "        <div class='playerOneName gameForm-item'>未知</div>\n" +
        "        <div class='playerOneGender gameForm-item'>未知</div>\n" +
        "        <div class='playerOneLevel gameForm-item'>未知</div>\n" +
        "        <div class='playerOneKind gameForm-item'>未知</div>\n" +
        "    </div>\n" +
        "    <div class='gameFormCenter'>\n" +
        "        <div class='timeCount' style='background-image: url(" + '../images/system/五子棋图标.jpg' + ");'>\n" +
        "            <div>\n" +
        "                <i><font class='countOne'></font>秒</i>\n" +
        "            </div>\n" +
        "            <div>\n" +
        "                <i><font class='countTwo'></font>秒</i>\n" +
        "            </div>\n" +
        "        </div>\n" +
        "\n" +
        "        <div class='gameBtnBar'>\n" +
        "            <input class='inviteBtn' data-num='" + roomID + "' type='button' value='邀请'>\n" +
        "            <input class='kickBtn' data-num='" + roomID + "' type='button' value='踢出'>\n" +
        "            <input class='beginBtn' data-num='" + roomID + "' type='button' value='开始'>\n" +
        "        </div>\n" +
        "    </div>\n" +
        "    <div class='gameFormRight'>\n" +
        "        <img alt='玩家二头像' class='playerTwoHeadIcon' src='../images/system/默认头像.png'>\n" +
        "        <div class='playerTwoName gameForm-item'>未知</div>\n" +
        "        <div class='playerTwoGender gameForm-item'>未知</div>\n" +
        "        <div class='playerTwoLevel gameForm-item'>未知</div>\n" +
        "        <div class='playerTwoKind gameForm-item'>未知</div>\n" +
        "    </div>\n" +
        "    <span><img alt='删除' class='deleteIcon' data-num='" + roomID + "' src='../images/system/删除.jpg'></span>\n" +
        "</div>");

    /*如果房间有两个人禁用邀请*/
    if (undefined != roomMasterNickname && undefined != playerNickname) {
        $($item).find(".inviteBtn").prop("disabled", true);
    } else {  //只有一人则禁用踢出和开始
        $($item).find(".kickBtn").prop("disabled", true);
        $($item).find(".beginBtn").prop("disabled", true);
    }
    /*不是房主，禁用按钮和删除*/
    if ($.cookie("username") != roomMasterNickname) {
        $($item).find(".gameBtnBar").text("等待房主");
        $($item).find(".deleteIcon").remove();
    }
    /*如果房间对局已经结束*/
    if (status == 103) {
        $($item).find(".gameBtnBar").text("对局已经结束，可在观看录像复盘");
    }
    /*赋值*/
    if (undefined != stepTime) {
        $($item).find(".countOne").text(stepTime);
    }
    if (undefined != gameTime) {
        $($item).find(".countTwo").text(gameTime);
    }
    if (undefined != roomMasterImgUrl) {
        $($item).find(".playerOneHeadIcon").attr("src", roomMasterImgUrl);
    }
    if (undefined != roomMasterNickname) {
        $($item).find(".playerOneName").text(roomMasterNickname);
    }
    if (undefined != roomMasterGender) {
        $($item).find(".playerOneGender").text(roomMasterGender);
    }
    if (undefined != roomMasterLevel) {
        $($item).find(".playerOneLevel").text("等级:" + roomMasterLevel);
    }
    if (undefined != roomMasterKind) {
        if (roomMasterKind == 1) { //黑棋
            $($item).find(".playerOneKind").text("执黑");
        } else if (roomMasterKind == 2) {   //白棋
            $($item).find(".playerOneKind").text("执白");
        }
    }
    if (undefined != playerImgUrl) {
        $($item).find(".playerTwoHeadIcon").attr("src", playerImgUrl);
    }
    if (undefined != playerNickname) {
        $($item).find(".playerTwoName").text(playerNickname);
    }
    if (undefined != playerGender) {
        $($item).find(".playerTwoGender").text(playerGender);
    }
    if (undefined != playerLevel) {
        $($item).find(".playerTwoLevel").text("等级:" + playerLevel);
    }
    if (undefined != playerKind) {
        if (playerKind == 1) { //黑棋
            $($item).find(".playerTwoKind").text("执黑");
        } else if (playerKind == 2) {   //白棋
            $($item).find(".playerTwoKind").text("执白");
        }
    }
    return $item;
}

/**
 * 初始化在线用户邀请表单
 * @param data
 * @param roomID 被点击表单的roomID
 */
function initOnlinePlayerList(data, roomID) {
    for (var i = 0; i < data.length; i++) {
        var $item = $("<li class='playerItem playerItemDelete' id='playerItem_" + i + "'>\n" +
            "            <span><img alt='玩家头像' class='headIcon' src='../images/system/默认头像.png'></span>\n" +
            "            <span class='username'></span>\n" +
            "            <span class='nickname'></span>\n" +
            "            <span class='level'></span>\n" +
            "            <input class='invitePlayerBtn' data-num='" + i + "' data-room='" + roomID + "' type='button' value='邀请'>\n" +
            "        </li>");
        $($item).find(".headIcon").attr("src", data["imgUrl"]);
        $($item).find(".username").text(data[i]["name"]);
        $($item).find(".nickname").text(data[i]["nickname"]);
        $($item).find(".level").text(data[i]["level"]);
        $item.appendTo($("#onlinePlayerList"));
    }
}

/*---------------------------------------
        游戏界面对局算法以及生成棋盘
-----------------------------------------*/
/**
 * 初始化棋盘
 */
function initChessboard() {
    /*像动态生成棋盘*/
    var chessboardX = 16, chessboardY = 16, padding = 30;
    drawSquare(chessboardX, chessboardY, padding);

    var $chessboard = $("#chessboard");
    var $hoverPoint = $("#hoverPoint");

    var $squareBox = $(".squareBox");

    /*棋子悬浮效果，对每个格子的判断*/
    $squareBox.mousemove(function (mouse) {
        mouseMove(this, padding, mouse);
    });

    //移出棋盘就隐藏
    $chessboard.mouseleave(function () {
        $hoverPoint.hide();
    });

    /*鼠标点击悬浮棋子则落子*/
    $hoverPoint.click(function () {
        setChess(this, padding);
    });
}

/**
 * 绘制棋盘格子
 * 样式如下
 * height: padding;
 * width: padding;
 * display: inline-flex;
 * border-left: 1px solid lightgray;
 * border-top: 1px solid lightgray;
 * z-index: 1;
 * position: absolute;
 *
 * @param xNum 棋盘行数
 * @param yNum 棋盘列数
 * @param padding 格子宽度
 */
function drawSquare(xNum, yNum, padding) {
    var $chessboard = $("#chessboard");
    var $chessboardMask = $("#chessboardMask");
    var $chessContent = $("#chessContent");
    /*高宽为chessboard的高宽加上40px的padding*/
    $chessContent.css({
        "width": (xNum - 1) * padding + 40 + "px",
        "height": (yNum - 1) * padding + 40 + "px"
    });
    /**
     * 设置遮罩大小
     * 高宽为chessboard的高宽加上40px的padding
     */
    $chessboardMask.css({
        "width": (xNum - 1) * padding + 40 + "px",
        "height": (yNum - 1) * padding + 40 + "px"
    });
    /**
     * 绘制棋盘大小和样式
     * 样式如下
     * "width": (xNum - 1) * padding + "px",
     * "height": (yNum - 1) * padding + "px",
     * "position": "absolute",
     * "border": "1px solid black",
     * "border-top": "none",
     * "border-left": "none",
     * "left": "20px",
     * "top": "20px"
     */
    $chessboard.css({
        "width": (xNum - 1) * padding + "px",
        "height": (yNum - 1) * padding + "px",
        "position": "absolute",
        "border": "1px solid black",
        "border-top": "none",
        "border-left": "none",
        "left": "20px",
        "top": "20px"
    });

    /*绘制棋盘格子*/
    for (var x = 0; x < xNum - 1; x++) {
        for (var y = 0; y < yNum - 1; y++) {
            var $squareBox = $("<div class='squareBox' data-x='" + x + "' data-y='" + y + "'></div>");
            $squareBox.css({
                "width": padding + "px",
                "height": padding + "px",
                "display": "inline-flex",
                "position": "absolute",
                "left": x * padding + "px",
                "top": y * padding + "px"
            });
            /*最后一行特殊处理*/
            if (x == xNum - 1) {
                $squareBox.css("border-bottom", "1px solid black");
            } else {
                $squareBox.css("border-top", "1px solid black");
            }
            /*最后一列特殊处理*/
            if (y == yNum - 1) {
                $squareBox.css("border-right", "1px solid black");
            } else {
                $squareBox.css("border-left", "1px solid black");
            }

            $squareBox.appendTo($chessboard);
        }
    }
}

/**
 * 处理鼠标棋子悬浮事件，对于落子辅助一下
 * @param squareBox 鼠标所在的方格
 * @param padding 棋盘横线的间隔，即棋盘格子的宽度
 * @param mouse 当前格子的鼠标
 */
function mouseMove(squareBox, padding, mouse) {
    var $hoverPoint = $("#hoverPoint");

    /*鼠标在当前格子上相距格式左上角点的位置*/
    var x = mouse.offsetX;
    var y = mouse.offsetY;
    /*获取当前格子的坐标*/
    var boxCoordinate = {
        x: parseInt($(squareBox).attr("data-x")),
        y: parseInt($(squareBox).attr("data-y"))
    }
    /*如果鼠标大于格子的大小的一半则棋子悬浮的坐标(x或y)加一*/
    var hoverPointCoordinate = {
        x: x <= padding / 2 ? boxCoordinate.x : boxCoordinate.x + 1,
        y: y <= padding / 2 ? boxCoordinate.y : boxCoordinate.y + 1
    }
    /*设置悬浮棋子的属性，并设置悬浮真实位置，最后显示悬浮效果*/
    $hoverPoint.css(getLocation(hoverPointCoordinate, padding));
    /*将坐标存入悬浮棋子的属性中*/
    $hoverPoint.attr("data-x", hoverPointCoordinate.x);
    $hoverPoint.attr("data-y", hoverPointCoordinate.y);
    $hoverPoint.show();
}

/**
 * 落子
 *
 * @param point 点击的悬浮棋子对象
 * @param padding 棋盘格子的宽度
 */
function setChess(point, padding) {
    var $chessboard = $("#chessboard");
    var $gamePlayBox = $("#gamePlayBox");
    /*获取悬浮棋子所在的坐标*/
    var hoverPointCoordinate = {
        x: parseInt($(point).attr("data-x")),
        y: parseInt($(point).attr("data-y"))
    }
    /*获取悬浮棋子的颜色*/
    var pointColor = $(point).attr("data-pointColor");
    /*设置落子*/
    var $chess = $("<div id='chess_" + hoverPointCoordinate.x + "_" + hoverPointCoordinate.y + "' class='chess chess_" + pointColor + "' data-pointColor='" + pointColor + "'></div>");
    $chess.css(getLocation(hoverPointCoordinate, padding));
    $chess.appendTo($chessboard);

    /*判断落子者*/
    if ($.cookie("username") == $gamePlayBox.find("#roomMasterName").text()) {
        clearInterval(roomMasterStepTimeInterval);
        roomMasterStepTime = countOne;
        clearInterval(roomMasterGameTimeInterval);
    } else {
        clearInterval(playerStepTimeInterval);
        playerStepTime = countOne;
        clearInterval(playerGameTimeInterval);
    }
    /*发送给后端*/
    ws.send(JSON.stringify({
        code: 206,
        msg: "",
        data: {
            roomID: parseInt($gamePlayBox.attr("data-room")),
            stepX: hoverPointCoordinate.x,
            stepY: hoverPointCoordinate.y,
            color: pointColor == "black" ? 1 : 2,
            /*通过面板信息判断下棋者然后返回其落子剩余时间*/
            time: $.cookie("username") == $gamePlayBox.find("#roomMasterName").text() ? parseInt(roomMasterGameTime) : parseInt(playerGameTime)
        }
    }));
}

/**
 * 生成棋子
 * @param data
 * @param padding
 */
function getAndSetChess(data, padding) {
    var hoverPointCoordinate = {
        x: data["stepX"],
        y: data["stepY"]
    }
    var pointColor;
    if (data["color"] == 1) {
        pointColor = "black";
    } else {
        pointColor = "white";
    }
    /*设置落子*/
    var $chess = $("<div id='chess_" + hoverPointCoordinate.x + "_" + hoverPointCoordinate.y + "' class='chess chess_" + pointColor + "' data-pointColor='" + pointColor + "'></div>");
    $chess.css(getLocation(hoverPointCoordinate, padding));
    $chess.appendTo($("#chessboard"));
}


/**
 * 返回棋子的真实位置
 * @param coordinate 坐标
 * @param padding 棋盘格子的宽度
 * @returns
 */
function getLocation(coordinate, padding) {
    return {
        left: coordinate.x * padding - padding / 2,
        top: coordinate.y * padding - padding / 2
    }
}

/*---------------------------------------
                倒计时函数
-----------------------------------------*/
var countOne;
var countTwo;
var roomMasterStepTime;
var roomMasterGameTime;
var playerStepTime;
var playerGameTime;

var roomMasterStepTimeInterval;
var roomMasterGameTimeInterval;
var playerStepTimeInterval;
var playerGameTimeInterval;

function roomMasterStepTimer() {
    if (roomMasterStepTime > 0) {
        roomMasterStepTime--;
        $("#roomMasterStepTime").text(formatTime(roomMasterStepTime));
    } else {
        // ws.send(JSON.stringify({
        //     code: 206,
        //     msg: "",
        //     data: {
        //         isTimeOut: true,
        //         roomID: parseInt($("#gamePlayBox").attr("data-room"))
        //     }
        // }));
    }
}

function roomMasterGameTimeTimer() {
    if (roomMasterGameTime > 0) {
        roomMasterGameTime--;
        $("#roomMasterGameTime").text(formatTime(roomMasterGameTime));
    }
}

function playerStepTimeTimer() {
    if (playerStepTime > 0) {
        playerStepTime--;
        $("#playerStepTime").text(formatTime(playerStepTime));
    }
}

function playerGameTimeTimer() {
    if (playerGameTime > 0) {
        playerGameTime--;
        $("#playerGameTime").text(formatTime(playerGameTime));
    }
}

/**
 * 格式化时间
 * @param value
 * @returns {string}
 */
function formatTime(value) {
    var h = "0";
    var m = "0";
    var s = "0";

    var secondTime = parseInt(value);// 秒
    var minuteTime = 0;// 分
    var hourTime = 0;// 小时

    if (secondTime < 10) {
        secondTime = s + secondTime;
        minuteTime = m + minuteTime;
        hourTime = h + hourTime;
    }

    if (secondTime < 60 && secondTime >= 10) {
        minuteTime = m + minuteTime;
        hourTime = h + hourTime;
    }
    if (secondTime > 60) {
        minuteTime = parseInt(secondTime / 60);
        if (minuteTime < 10) {
            minuteTime = m + minuteTime;
        }
        secondTime = parseInt(secondTime % 60);
        if (secondTime < 10) {
            secondTime = s + secondTime;
        }
        if (hourTime < 10) {
            hourTime = h + hourTime;
        }
        if (minuteTime > 60) {
            hourTime = parseInt(minuteTime / 60);
            if (hourTime < 10) {
                hourTime = h + hourTime;
            }
            minuteTime = parseInt(minuteTime % 60);
            if (minuteTime < 10) {
                minuteTime = m + minuteTime;
            }
        }
    }
    return hourTime + ":" + minuteTime + ":" + secondTime;
}

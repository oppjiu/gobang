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
    /*对局*/
    ws.onmessage = function (evt) {
        var parse = JSON.parse(evt.data);
        var code = parse["code"];
        var data = parse["data"];
        console.log("websocket.code: ", code, "websocket.data: ", data);
        var $hoverPoint = $("#hoverPoint");
        var $gamePlayBox = $("#gamePlayBox");
        var $chessboardMask = $("#chessboardMask");
        var $squareBox = $(".squareBox");

        if (code == 206) {
            var $item = $("#gameForm_" + data["roomID"]);
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
                });

                $item.find(".gameBtnBar").text("对局已经结束");
            } else {
                getAndSetChess(data, 30);
            }
        } else if (code == 208) {
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
            });
            $item.find(".gameBtnBar").text("对局已经结束");
        } else if (code == 210) {
            var $item = $("#gameForm_" + data["roomID"]);
            /*是否同意平局*/
            var isAgree = data["isAgree"];
            var refusePlayer = data["refusePlayer"];
            if (isAgree == true) {
                alert("双方达成平局");
                /*退出对战*/
                /*退出对战*/
                $gamePlayBox.fadeOut(function () {
                    $("#gameForm").css({"display": "flex"});
                    /*清空棋盘的棋子*/
                    $(".chess").remove();
                });
                $item.find(".gameBtnBar").text("对局已经结束");
            } else {
                alert("玩家" + refusePlayer + "拒绝平局");
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
            }
        } else if (code == 213) {
            /*观棋*/
            var roomID = data["roomID"];
            if (data["watchGamePlayer"] == $.cookie("username")) {
                /*进入观棋*/
                initChessboard();
                /*设置观战遮罩*/
                $chessboardMask.css({
                    "display": "block",
                    "background-color": "rgba(0,110,255,0.5)"
                });
                /*初始化对局棋盘*/
                initChess(data["gameList"], 30);
                $("#gameForm").fadeOut(function () {
                    $gamePlayBox.css({
                        "display": "flex"
                    });
                    /*记录房间号*/
                    $gamePlayBox.attr("data-room", roomID);
                });

                /*房间号*/
                var $item = $("#gameForm_" + data["roomID"]);
                /*初始化对战表单*/
                /*房主的信息栏*/
                $gamePlayBox.find("#roomMasterHeadIcon").attr("src", $($item).find(".playerOneHeadIcon").attr("src"));
                if ("执黑" == $($item).find(".playerOneKind").text()) {
                    $gamePlayBox.find("#roomMasterChessColor").attr("src", "../images/system/blackChess.png");
                } else {
                    $gamePlayBox.find("#roomMasterChessColor").attr("src", "../images/system/whiteChess.png");
                }
                $gamePlayBox.find("#roomMasterLevel").text($($item).find(".playerOneLevel").text());
                $gamePlayBox.find("#roomMasterName").text($($item).find(".playerOneName").text());
                /*玩家的信息栏*/
                $gamePlayBox.find("#playerHeadIcon").attr("src", $($item).find(".playerTwoHeadIcon").attr("src"));
                if ("执黑" == $($item).find(".playerTwoKind").text()) {
                    $gamePlayBox.find("#playerChessColor").attr("src", "../images/system/blackChess.png");
                } else {
                    $gamePlayBox.find("#playerChessColor").attr("src", "../images/system/whiteChess.png");
                }
                $gamePlayBox.find("#playerLevel").text($($item).find(".playerTwoLevel").text());
                $gamePlayBox.find("#playerName").text($($item).find(".playerTwoName").text());

                console.log("===text()", $($item).find(".playerTwoLevel").text());
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
                生成表单
-----------------------------------------*/

$(function () {
    $.ajax({
        url: basePath + "/gobang/getPlayingRoom",
        method: "post",
        success: function (result) {
            if (result.code == 10) {
                console.log("getPlayingRoom.data: ", result.data);
                initGameRoom(result.data);
                initWebSocket();
                if (!($("ul").has("li").length > 0)) {
                    alert("现没有游戏正在进行");
                }
            }
        },
        error: function (result) {
            console.log(result);
        },
        async: false
    });

    /*观战按钮点击事件*/
    $(document).on("click", ".watchGameBtn", function () {
        var num = $(this).attr("data-num");
        /*观棋*/
        /*发送后端websocket*/
        ws.send(JSON.stringify({
            code: 213,
            msg: "",
            data: {
                roomID: num
            }
        }));
    });
});

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
        "            <input class='watchGameBtn' data-num='" + roomID + "' type='button' value='观战'>\n" +
        "        </div>\n" +
        "    </div>\n" +
        "    <div class='gameFormRight'>\n" +
        "        <img alt='玩家二头像' class='playerTwoHeadIcon' src='../images/system/默认头像.png'>\n" +
        "        <div class='playerTwoName gameForm-item'>未知</div>\n" +
        "        <div class='playerTwoGender gameForm-item'>未知</div>\n" +
        "        <div class='playerTwoLevel gameForm-item'>未知</div>\n" +
        "        <div class='playerTwoKind gameForm-item'>未知</div>\n" +
        "    </div>\n" +
        "</div>");

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

/*---------------------------------------
            观战自定义初始化棋盘
-----------------------------------------*/
/**
 * 初始化棋盘
 */
function initChessboard() {
    /*像动态生成棋盘*/
    var chessboardX = 16, chessboardY = 16, padding = 30;
    drawSquare(chessboardX, chessboardY, padding);
}

/**
 * 重新进入游戏或者观战者初始化棋局
 */
function initChess(data, padding) {
    for (var i = 0; i < data.length; i++) {
        getAndSetChess(data[i], 30);
    }
}

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
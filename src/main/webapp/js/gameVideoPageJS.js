var basePath = $("#basePath").attr("value");
var wsBasePath = $("#wsBasePath").attr("value");

/*---------------------------------------
                生成表单
-----------------------------------------*/
/*存储复盘数据长度的变量*/
var i = 0;
var timeSpeed = 1;
var timer;
$(function () {
    var $gamePlayBox = $("#gamePlayBox");

    /*获取可复盘的对局表单*/
    $.ajax({
        url: basePath + "/gobang/getPlayedRoom",
        method: "post",
        data: {
            "name": $.cookie("username")
        },
        success: function (result) {
            if (result.code == 10) {
                console.log("getPlayingRoom.data: ", result.data);
                initGameRoom(result.data);
                if (!($("ul").has("li").length > 0)) {
                    alert("没有可复盘的对局");
                }
            }
        },
        error: function (result) {
            console.log(result);
        },
        async: false
    });

    /*复盘按钮点击事件*/
    $(document).on("click", ".watchGameBtn", function () {
        /*房间号*/
        var num = $(this).attr("data-num");
        var $item = $("#gameForm_" + num);
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
        $.ajax({
            url: basePath + "/gobang/getPlayedGameStepData",
            data: {
                "roomID": num
            },
            method: "post",
            success: function (result) {
                console.log("请求复盘的数据", result.data)
                if (result.code == 10) {
                    var $gamePlayBox = $("#gamePlayBox");
                    $("#gameForm").fadeOut(function () {
                        $gamePlayBox.css({"display": "flex"});
                        /*记录房间号*/
                        $gamePlayBox.attr("data-room", num);
                    });
                    /*初始化棋盘*/
                    initChessboard();
                    /*初始化速度按钮*/
                    initGameBtn(result.data);
                    /*时间间隔落子*/
                    /*js定时器是单线程，改变速度必须重设setInterval*/
                    timer = setInterval(function () {
                        showChess(result.data);
                    }, 1000 / timeSpeed);
                }
            },
            error: function (result) {
                console.log(result);
            },
            async: false
        });
    });
});

/**
 * 控制面板
 * @param data
 */
function showChess(data) {
    if (i < data["gameStepData"].length) {
        /*落子*/
        getAndSetChess(data["gameStepData"][i], 30);
        i++;
    } else {
        /*提示并退出*/
        if (data["winner"] == undefined) {
            alert("双方达成平局")
        } else {
            alert("玩家[" + data["winner"] + "]赢啦");
        }

        /*清除定时器*/
        clearInterval(timer);
        /*关闭对战复盘窗口*/
        $("#gamePlayBox").fadeOut(function () {
            $("#gameForm").css({"display": "flex"});
        });
        /*清空棋盘的棋子*/
        $(".chess").remove();
        i = 0;
        timeSpeed = 1;
    }
}

/**
 * 初始化游戏的按钮
 */
function initGameBtn(data) {
    /*按钮控制速度在1~3之间*/
    $(document).on("click", "#speedUp", function () {
        if (1 <= timeSpeed && timeSpeed < 5) {
            clearInterval(timer);
            timer = setInterval(function () {
                showChess(data);
            }, 1000 / timeSpeed);
            timeSpeed++;
            $("#gamePlayBoxSpeedText").text("速度×" + timeSpeed);
        }
    });
    $(document).on("click", "#speedDown", function () {
        if (1 < timeSpeed && timeSpeed <= 5) {
            timeSpeed--;
            clearInterval(timer);
            timer = setInterval(function () {
                showChess(data);
            }, 1000 / timeSpeed);
            $("#gamePlayBoxSpeedText").text("速度×" + timeSpeed);
        }
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
        "            <input class='watchGameBtn' data-num='" + roomID + "' type='button' value='复盘'>\n" +
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

/**/
/**
 * 初始化棋盘
 */
function initChessboard() {
    /*像动态生成棋盘*/
    var chessboardX = 16, chessboardY = 16, padding = 30;
    drawSquare(chessboardX, chessboardY, padding);
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
    var $chess = $("<div class='chess chess_" + pointColor + "' data-pointColor='" + pointColor + "'></div>");
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
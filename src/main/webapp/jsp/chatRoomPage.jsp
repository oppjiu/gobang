<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String path = request.getContextPath();
    String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path;
    String wsBasePath = "ws://" + request.getServerName() + ":" + request.getServerPort() + path;
%>
<html>
<head>
    <title>五子棋-聊天室</title>
    <style>
        #contentLeft {
            float: left;
        }

        #contentRight {
            float: left;
        }

        #chatMessageArea {
            float: left;
            width: 500px;
            height: 600px;
            border-style: solid;
            border-width: 1px;
            border-color: aqua;
        }

        body {
            overflow: auto;
        }
    </style>

    <script src="../js/jquery.min.js"></script>
    <script>
        var ws = parent.websocket;

        function initWebSocket() {
            ws.onopen = function (evt) {
                console.log("连接", evt);
            }
            /**
             * 接受后端使用sendText()方法向前端发送的消息
             * @param evt evt.data属性为json字符串对象
             */
            ws.onmessage = function (evt) {
                var parse = JSON.parse(evt.data);
                var code = parse["code"];
                var data = parse["data"];
                console.log("websocket.code: ", code, "websocket.data: ", data);
                if (code == 200) { //聊天方法
                    //广播或者私聊成功
                    if (data["code"] == 10) {
                        //展示消息
                        $("#chatMessageArea").append("<p>" + data["data"] + "</p>");
                    } else if (data["code"] == 0) {    //不在线
                        alert("用户不在线");
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

        $(function () {
            //获取在线用户并展示
            $.ajax({
                url: "<%=basePath%>/gobang/getOnlinePlayers",
                data: {},
                method: "post",
                success: function (result) {
                    //初始化webSocket
                    initWebSocket();

                    console.log("在线用户: ", result.data);
                    for (var i = 0; i < result.data.length; i++) {
                        $("<option value='" + result.data[i] + "'>" + result.data[i]['name'] + "</option>").appendTo($("#onlineUser"));
                    }
                },
                error: function (e) {
                    console.log(e);
                },
                /**
                 * 重要！！！
                 * 必须设置同步，异步会出现空指针异常
                 * 因为异步可能会导致initWebSocket先于api请求执行而无法设置session导致获取session时空指针异常
                 */
                async: false
            });

            //发送消息
            $("#sendBtn").click(function () {
                //私聊
                var json = {
                    code: 200,
                    message: "",
                    data: {
                        "isBroadcast": false,
                        "fromWho": null,
                        "toWho": $("#onlineUser").val(),
                        "text": $("#chatMessage").val()
                    }
                }
                //调用后端用@OnMessage注解的方法
                if (ws != null) {
                    ws.send(JSON.stringify(json));
                } else {
                    alert("未登录");
                }
            });

            //发送广播
            $("#broadcastBtn").click(function () {
                //广播
                var json = {
                    code: 200,
                    message: "",
                    data: {
                        "isBroadcast": true,
                        "fromWho": null,
                        "toWho": $("#onlineUser").val(),
                        "text": $("#chatMessage").val()
                    }
                }
                //调用后端用@OnMessage注解的方法
                if (ws != null) {
                    ws.send(JSON.stringify(json));
                } else {
                    alert("未登录");
                }
            });
        });
    </script>
</head>
<body>
<div id="container">
    <div id="contentLeft">
        <label for="onlineUser">在线用户</label>
        <select id="onlineUser">
        </select>
    </div>
    <div id="contentRight">
        <label for="chatMessage">聊天内容</label>
        <textarea name="chatBox" id="chatMessage" cols="20" rows="10"></textarea>
        <br>
        <input id="sendBtn" type="button" value="发送">
        <input id="broadcastBtn" type="button" value="广播">
        <br>
        <div id="chatMessageArea"></div>
    </div>
</div>
</body>
</html>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String path = request.getContextPath();
    String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path;
    String wsBasePath = "ws://" + request.getServerName() + ":" + request.getServerPort() + path;
%>
<html>

<head>
    <title>五子棋-欢迎页</title>
    <script src="../js/jquery.min.js"></script>
    <style>
        body {
            background: url("../images/system/bg/五子棋封面1.png");
            background-size: 100% 100%;
            height: 110%;
            position: fixed;
            width: 110%;
        }

        .img {

        }

        h1 {
            position: absolute;
            top: 50%;
            left: 50%;
            transform: translate(-50%, -50%);
            font: bold 60px Arial, Helvetica, sans-serif;
            color: #ffa9d0;
            text-shadow: #ff7eb6 0.1em 0.1em 0.1em;
        }
    </style>
</head>
<body>
<div class="img"><h1>欢迎进入五子棋</h1></div>
</body>
</html>

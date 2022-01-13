<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String path = request.getContextPath();
    String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path;
    String wsBasePath = "ws://" + request.getServerName() + ":" + request.getServerPort() + path;
%>
<html>
<head>
    <title>五子棋-404-错误</title>
    <link rel="stylesheet" href="../css/404.css">
</head>
<body>
<h1>HTTP状态 404 - 未找到</h1>
<hr class="line"/>
<p><b>类型</b> 状态报告</p>
<p><b>消息</b> 请求的资源不可用</p>
<p><b>描述</b> 源服务器未能找到目标资源的表示或者是不愿公开一个已经存在的资源表示。</p>
<hr class="line"/>
</body>
</html>

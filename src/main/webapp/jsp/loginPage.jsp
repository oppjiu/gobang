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

    <title>五子棋-我要登录</title>
    <%--    <link href="../css/formCSS.css" rel="stylesheet">--%>

    <%--    <script src="../js/jquery.min.js"></script>--%>
    <%--    <script src="../js/jquery.cookie.js"></script>--%>

    <script src="../js/loginFormJS.js"></script>

</head>
<body>
<!--登录表单-->
<div class="form">
    <div class="form-top">
        <img alt="头像" class="loginFormHeadIcon" src="../images/system/默认头像.png">
    </div>

    <div class="form-bottom">
        <div class="form-bottom-item item1">
            <label for="username">用户名</label>
            <input id="username" name="username" type="text">
        </div>

        <div class="form-bottom-item item2">
            <label for="password">密码</label>
            <input id="password" name="password" type="password">
        </div>

        <div class="form-bottom-item item3">
            <label for="validateCode">验证码</label>
            <input id="validateCode" name="validateCode" required type="text"><br>
        </div>

        <div class="form-bottom-item item4">
            <label for="validateCodeImg"><a href="javaScript:void(0);" id="changeValidateCode" style="color: white">看不清，换一张</a></label>
            <img alt="验证码" id="validateCodeImg" src="<%=basePath%>/gobang/validateCode">
        </div>

        <div class="form-bottom-item item5">
            <label for="rememberForm">记住我</label>
            <input id="rememberForm" name="rememberForm" title="记住我" type="checkbox">
        </div>

        <div class="form-bottom-item item6">
            <input class="loginFormSubmit" type="submit" value="登录">
        </div>
    </div>
</div>

</body>
</html>

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

    <title>五子棋-账号注册</title>
    <%--    <link href="../css/formCSS.css" rel="stylesheet">--%>

    <%--    <script src="../js/jquery.min.js"></script>--%>
    <%--    <script src="../js/jquery.cookie.js"></script>--%>

    <script src="../js/registerFormJS.js"></script>

</head>
<body>
<!--注册表单-->
<div class="form registerForm">
    <div class="form-top">
        <img alt="头像" class="registerFormHeadIcon" src="../images/system/默认头像.png">
    </div>

    <div class="form-bottom">
        <div class="form-bottom-item item1">
            <label>用户名</label>
            <input name="username" type="text">
        </div>

        <div class="form-bottom-item item2">
            <label>昵称</label>
            <input name="nickname" type="text">
        </div>

        <div class="form-bottom-item item3">
            <label>性别</label>
            <select class="grid-item" name="gender">
                <option value="男">男</option>
                <option value="女">女</option>
            </select>
        </div>

        <div class="form-bottom-item item4">
            <label>密码</label>
            <input name="password" type="password">
        </div>

        <div class="form-bottom-item item5">
            <label>确认密码</label>
            <input name="confirmPwd" type="password">
        </div>

        <div class="form-bottom-item item6">
            <input class="registerFormSubmit" type="submit" value="注册">
        </div>
    </div>
</div>
</body>
</html>

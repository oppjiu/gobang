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

    <title>五子棋-个人信息</title>
    <%--    <link href="../css/formCSS.css" rel="stylesheet">--%>
    <%--        --%>
    <%--    <script src="../js/jquery.min.js"></script>--%>
    <%--    <script src="../js/jquery.cookie.js"></script>--%>
    <%--    <script src="../js/jquery.ajaxFileUpload.js"></script>--%>

    <script src="../js/userInformationJS.js"></script>

</head>
<body>
<!--个人信息表单-->
<div class="form">
    <div class="form-top">
        <img alt="头像" class="manageFormHeadIcon" src="../images/system/默认头像.png">
    </div>

    <div class="form-bottom">
        <div class="change-area item1 changePwd">
            <div class="form-bottom-item changePwd-item1">
                <label>用户名</label>
                <input disabled name="username" type="text">
            </div>

            <div class="form-bottom-item changePwd-item2">
                <label>密码</label>
                <input name="password" type="password">
            </div>

            <div class="form-bottom-item changePwd-item3">
                <label>新密码</label>
                <input name="newPassword" type="password">
            </div>
        </div>

        <div class="change-area item1 changeInfo">
            <div class="form-bottom-item changeInfo-item1">
                <label>用户名</label>
                <input disabled name="username" type="text">
            </div>

            <div class="form-bottom-item changeInfo-item2">
                <label>昵称</label>
                <input name="nickname" type="text">
            </div>

            <div class="form-bottom-item changeInfo-item3">
                <label>性别</label>
                <select class="grid-item" name="gender">
                    <option value="男">男</option>
                    <option value="女">女</option>
                </select>
            </div>

            <div class="form-bottom-item changeInfo-item4">
                <label>管理员</label>
                <select class="grid-item" disabled name="admin">
                    <option value="true">是</option>
                    <option value="false">否</option>
                </select>
            </div>
        </div>
        <div class="change-area item1 changeImg">
            <div class="form-bottom-item changeImg-item1">
                <label>用户名</label>
                <input disabled name="username" type="text">
            </div>
            <div class="form-bottom-item changeImg-item2">
                <label>上传头像</label>
                <input class="changeImg-item1 uploadFile" id="uploadFile" name="uploadFile" type="file"/>
            </div>
        </div>
        <div class="form-bottom-item item2">
            <input class="submit changePwdBtn" type="submit" value="改密">
            <input class="submit changeInfoBtn" type="submit" value="编辑">
            <input class="submit changeImgBtn" type="submit" value="头像">
            <input class="submit chancelBtn" type="submit" value="取消">
        </div>

        <div class="form-bottom-item item3">
            <div style="color: white">
                等级:
                <img id="levelStar_1" src="../images/system/星星.png" width="20px" height="20px">
                <img id="levelStar_2" src="../images/system/星星.png" width="20px" height="20px">
                <img id="levelStar_3" src="../images/system/星星.png" width="20px" height="20px">
                <img id="levelStar_4" src="../images/system/星星.png" width="20px" height="20px">
                <img id="levelStar_5" src="../images/system/星星.png" width="20px" height="20px">
            </div>
        </div>
    </div>
</div>

</body>
</html>

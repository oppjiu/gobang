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

    <title>五子棋-用户管理</title>
    <link rel="stylesheet" href="../css/userManageCSS.css">

    <script src="../js/jquery.min.js"></script>
    <script src="../js/jquery.ajaxFileUpload.js"></script>
</head>
<body>
<ul class="box" id="box"></ul>
<script>
    $.ajax({
        url: "<%=basePath%>/gobang/findAllUsers",
        method: "post",
        success: function (result) {
            console.log(result);
            initForm(result.data);
        },
        error: function (e) {
            console.log(e);
        },
        /*必须同步*/
        async: false
    });

    $(function () {
        var $changePwdBtn = $(".changePwdBtn");
        var $changeInfoBtn = $(".changeInfoBtn");
        var $changeImgBtn = $(".changeImgBtn");
        var $chancelBtn = $(".chancelBtn");
        var $deleteIcon = $(".deleteIcon");

        /*修改密码按钮点击事件*/
        $changePwdBtn.click(function () {
            var num = $(this).attr("data-num");
            var form = "#form_" + num;
            var flag = $(form).attr("data-flag");

            var $changePwd = $(form).find(".changePwd");
            var $changeInfo = $(form).find(".changeInfo");
            var $changeImg = $(form).find(".changeImg");
            var $changePwdBtn = $(form).find(".changePwdBtn");
            var $changeInfoBtn = $(form).find(".changeInfoBtn");
            var $changeImgBtn = $(form).find(".changeImgBtn");
            var $chancelBtn = $(form).find(".chancelBtn");


            if (flag == 0) {
                $changePwdBtn.val("提交");

                $changePwdBtn.attr("disabled", false);
                $changeInfoBtn.attr("disabled", true);
                $changeImgBtn.attr("disabled", true);

                $chancelBtn.css("display", "block");
                $changePwd.css("display", "grid");
                $changeInfo.css("display", "none");
                $changeImg.css("display", "none");
                $(form).attr("data-flag", 1);
            } else if (flag == 1) {
                changePassword(form);
            }
        });

        /*修改信息按钮点击事件*/
        $changeInfoBtn.click(function () {
            var num = $(this).attr("data-num");
            var form = "#form_" + num;
            var flag = $(form).attr("data-flag");

            var $changePwd = $(form).find(".changePwd");
            var $changeInfo = $(form).find(".changeInfo");
            var $changeImg = $(form).find(".changeImg");
            var $changePwdBtn = $(form).find(".changePwdBtn");
            var $changeInfoBtn = $(form).find(".changeInfoBtn");
            var $changeImgBtn = $(form).find(".changeImgBtn");
            var $chancelBtn = $(form).find(".chancelBtn");

            if (flag == 0) {
                $changeInfoBtn.val("保存");

                $changePwdBtn.attr("disabled", true);
                $changeInfoBtn.attr("disabled", false);
                $changeImgBtn.attr("disabled", true);

                $chancelBtn.css("display", "block");
                $changePwd.css("display", "none");
                $changeInfo.css("display", "grid");
                $changeImg.css("display", "none");
                $(form).attr("data-flag", 2);
            } else if (flag == 2) {
                changeUserInfo(form);
            }
        });

        /*上传图片按钮点击事件*/
        $changeImgBtn.click(function () {
            var num = $(this).attr("data-num");
            var form = "#form_" + num;
            var flag = $(form).attr("data-flag");

            var $changePwd = $(form).find(".changePwd");
            var $changeInfo = $(form).find(".changeInfo");
            var $changeImg = $(form).find(".changeImg");
            var $changePwdBtn = $(form).find(".changePwdBtn");
            var $changeInfoBtn = $(form).find(".changeInfoBtn");
            var $changeImgBtn = $(form).find(".changeImgBtn");
            var $chancelBtn = $(form).find(".chancelBtn");

            if (flag == 0) {
                $changeImgBtn.val("上传");

                $changePwdBtn.attr("disabled", true);
                $changeInfoBtn.attr("disabled", true);
                $changeImgBtn.attr("disabled", false);
                $chancelBtn.attr("display", "block");

                $chancelBtn.css("display", "block");
                $changePwd.css("display", "none");
                $changeInfo.css("display", "none");
                $changeImg.css("display", "grid");
                $(form).attr("data-flag", 3);
            } else if (flag == 3) {
                uploadHeadIcon(form, num);
            }
        });

        /*取消操作按钮事件*/
        $chancelBtn.click(function () {
            var num = $(this).attr("data-num");
            var form = "#form_" + num;

            var $changePwdBtn = $(form).find(".changePwdBtn");
            var $changeInfoBtn = $(form).find(".changeInfoBtn");
            var $changeImgBtn = $(form).find(".changeImgBtn");

            $changePwdBtn.val("改密");
            $changeInfoBtn.val("编辑");
            $changeImgBtn.val("头像");
            clearBtnState(form);
        });

        /*删除用户按钮事件*/
        $deleteIcon.click(function () {
            var num = $(this).attr("data-num");
            var form = "#form_" + num;
            /*删除用户请求*/
            deleteUser(num, form);
        });
    });

    /**
     * 因为ajaxFileUpload脚本没有提供data传参所以必须使用url传参
     * 使用encodeURI(encodeURI(name))编码，在后端进行解码
     * @param name 用户名
     * @param form 用户表单
     * @param fileElementId 上传文件的Id
     */
    function ajaxFileUpload(name, form, fileElementId) {
        $.ajaxFileUpload({
            url: "<%=basePath%>/gobang/uploadImg/" + encodeURI(encodeURI(name)),
            fileElementId: fileElementId,
            secureuri: false,
            dataType: 'json',
            success: function (result) {
                console.log(result);
                if (result.code == 10) {
                    //头像更换
                    $(form).find(".headIcon").attr("src", result.data["url"]);
                } else if (result.code == 0) {
                    console.log("上传出错");
                } else {
                    console.log("出现异常");
                }
            },
            error: function (data, status, e) {
                console.log(e);
            }
        });
    }

    /**
     * 按钮一共有四种状态 0, 1, 2, 3
     * flag设置四种状态的点击
     * 加载数据
     * @param data
     */
    function initForm(data) {
        for (var i = 0; i < data.length; i++) {
            var $item = $("<div id='form_" + i + "' class='form' data-flag='" + 0 + "'>\n" +
                "    <div class='form-left'>\n" +
                "        <img alt='头像' class='headIcon' src='../images/system/默认头像.png' title='头像'>\n" +
                "    </div>\n" +
                "\n" +
                "    <div class='form-right'>\n" +
                "        <div class='change-area item1 changePwd'>\n" +
                "            <div class='form-right-item changePwd-item1'>\n" +
                "                <label>用户名</label>\n" +
                "                <input name='username' type='text' disabled>\n" +
                "            </div>\n" +
                "\n" +
                "            <div class='form-right-item changePwd-item2'>\n" +
                "                <label>密码</label>\n" +
                "                <input name='password' type='password'>\n" +
                "            </div>\n" +
                "\n" +
                "            <div class='form-right-item changePwd-item3'>\n" +
                "                <label>新密码</label>\n" +
                "                <input name='newPassword' type='password'>\n" +
                "            </div>\n" +
                "        </div>\n" +
                "\n" +
                "        <div class='change-area item1 changeInfo'>\n" +
                "            <div class='form-right-item changeInfo-item1'>\n" +
                "                <label>用户名</label>\n" +
                "                <input name='username' type='text' disabled>\n" +
                "            </div>\n" +
                "            <div class='form-right-item changeInfo-item2'>\n" +
                "                <label>昵称</label>\n" +
                "                <input name='nickname' type='text'>\n" +
                "            </div>\n" +
                "            <div class='form-right-item changeInfo-item3'>\n" +
                "                <label>性别</label>\n" +
                "                <select class='grid-item' name='gender'>\n" +
                "                    <option value='男'>男</option>\n" +
                "                    <option value='女'>女</option>\n" +
                "                </select>\n" +
                "            </div>\n" +
                "\n" +
                "            <div class='form-right-item changeInfo-item4'>\n" +
                "                <label>管理员</label>\n" +
                "                <select class='grid-item' name='admin' disabled>\n" +
                "                    <option value='true'>是</option>\n" +
                "                    <option value='false'>否</option>\n" +
                "                </select>\n" +
                "            </div>\n" +
                "        </div>\n" +
                "\n" +
                "        <div class='change-area item1 changeImg'>\n" +
                "            <div class='form-right-item changeImg-item1'>\n" +
                "                <label>用户名</label>\n" +
                "                <input name='username' type='text' disabled>\n" +
                "            </div>\n" +
                "            <div class='form-right-item changeImg-item2'>\n" +
                "                <label>上传头像</label>\n" +
                "                <input class='changeImg-item1 uploadFile' id='uploadFile_" + i + "' name='uploadFile' type='file'/>\n" +
                "            </div>\n" +
                "        </div>\n" +
                "\n" +
                "        <div class='form-right-item item2'>\n" +
                "            <input class='submit changePwdBtn' type='submit' value='改密' data-num='" + i + "'>\n" +
                "            <input class='submit changeInfoBtn' type='submit' value='编辑' data-num='" + i + "'>\n" +
                "            <input class='submit changeImgBtn' type='submit' value='头像' data-num='" + i + "'>\n" +
                "            <input class='submit chancelBtn' type='submit' value='取消' data-num='" + i + "'>\n" +
                "        </div>\n" +
                "\n" +
                "        <div class='form-right-item item3'>\n" +
                "        <div class='rankName'>\n" +
                "            <label>等级:</label>\n" +
                "        </div>\n" +
                "        <div class='rankNum'>\n" +
                "            <ul class='listRank'>\n" +
                "                <li>\n" +
                "                    <img src='../images/system/星星.png' width='20px' height='20px'>\n" +
                "                </li>\n" +
                "                <li>\n" +
                "                    <img src='../images/system/星星.png' width='20px' height='20px'>\n" +
                "                </li>\n" +
                "                <li>\n" +
                "                    <img src='../images/system/星星.png' width='20px' height='20px'>\n" +
                "                </li>\n" +
                "                <li>\n" +
                "                    <img src='../images/system/星星.png' width='20px' height='20px'>\n" +
                "                </li>\n" +
                "                <li>\n" +
                "                    <img src='../images/system/星星.png' width='20px' height='20px'>\n" +
                "                </li>\n" +
                "            </ul>\n" +
                "        </div>\n" +
                "        </div>\n" +
                "<span>\n" +
                "   <img alt='删除' class='deleteIcon' data-num='" + i + "' src='../images/system/删除.jpg'>\n" +
                "</span>" +
                "    </div>\n" +
                "</div>");

            //加载数据
            var userData = data[i];
            if (userData["imgUrl"] != null) {
                $item.find(".headIcon").attr("src", userData["imgUrl"]);
            }
            $item.find(".changePwd input[name='username']").val(userData["name"]);
            $item.find(".changeImg input[name='username']").val(userData["name"]);
            $item.find(".changeInfo input[name='username']").val(userData["name"]);
            /*等级*/
            var userLevel = userData["level"];
            if (userLevel >= 1) {
                for (var j = 0; j < userLevel; j++) {
                    $item.find(".listRank").find("li").eq(j).find("img").attr("src", '../images/system/亮星星.png');
                }
            }

            $item.find(".changeInfo input[name='nickname']").val(userData["nickname"]);
            $item.find(".changeInfo select[name='gender']").val(userData["gender"]);
            if (userData["admin"]) {
                $item.find(".changeInfo select[name='admin']").val("true");
            } else {
                $item.find(".changeInfo select[name='admin']").val("false");
            }

            var $li = $("<li class='liTry' id='li_" + i + "'></li>");
            $item.appendTo($li);
            $li.appendTo($("#box"));
        }
    }

    function clearBtnState(form) {
        $(form).find(".changePwdBtn").attr("disabled", false);
        $(form).find(".changeInfoBtn").attr("disabled", false);
        $(form).find(".changeImgBtn").attr("disabled", false);
        $(form).find(".chancelBtn").css("display", "none");
        $(form).attr("data-flag", 0);
    }

    /*修改密码*/
    function changePassword(form) {
        var username = $(form).find(".changePwd input[name='username']").val();
        var password = $(form).find(".changePwd input[name='password']").val();
        var newPassword = $(form).find(".changePwd input[name='newPassword']").val();

        if ($.trim(password) == "") {
            alert("密码不能为空");
        } else if ($.trim(newPassword) == "") {
            alert("新密码不能为空");
        } else if (password == newPassword) {
            alert("旧密码与新密码相同，请重新输入新密码");
        } else {
            $.ajax({
                url: "<%=basePath%>/gobang/modifyUserPwd",
                data: {
                    "username": username,
                    "password": password,
                    "newPassword": newPassword
                },
                method: "post",
                success: function (result) {
                    if (result.code == 10) {
                        $(form).find(".changePwd input[name='password']").val("");
                        $(form).find(".changePwd input[name='newPassword']").val("");
                        alert("改密成功");
                        $(form).find(".changePwdBtn").val("改密");
                        clearBtnState();
                    } else if (result.code == 0) {
                        alert("密码输入错误");
                    } else {
                        console.log("请求错误");
                    }
                },
                error: function (e) {
                    console.log(e);
                }
            });
        }
    }

    /*修改个人信息*/
    function changeUserInfo(form) {
        var username = $(form).find(".changeInfo input[name='username']").val();
        var nickname = $(form).find(".changeInfo input[name='nickname']").val();
        var gender = $(form).find(".changeInfo select[name='gender']").val();
        var isAdmin = $(form).find(".changeInfo select[name='admin']").val();

        if ($.trim(nickname) == "") {
            alert("昵称不能为空");
        } else {
            $.ajax({
                url: "<%=basePath%>/gobang/modifyUserInfo",
                data: {
                    "username": username,
                    "nickname": nickname,
                    "gender": gender,
                    "isAdmin": isAdmin
                },
                method: "post",
                success: function (result) {
                    if (result.code == 10) {
                        alert("保存成功");
                        $(form).find(".changeInfoBtn").val("编辑");
                        clearBtnState();
                    } else if (result.code == 0) {
                        alert("密码输入错误");
                    } else {
                        console.log("请求错误");
                    }
                },
                error: function (e) {
                    console.log(e);
                }
            });
        }
    }

    /*上传头像*/
    function uploadHeadIcon(form, num) {
        var fileElementId = "uploadFile_" + num;
        //判断是否选择了文件进行上传
        var fileInput = $(form).find("#" + fileElementId).get(0).files[0];
        if (fileInput) {
            var name = $(form).find(".changeImg input[name='username']").val();
            ajaxFileUpload(name, form, fileElementId);
            alert("上传成功");
            $(form).find(".changeImgBtn").val("头像");
            clearBtnState();
            console.info(fileInput);
        } else {
            alert("请选择上传文件！");
        }
    }

    /**
     * 删除用户
     * @param num 表单的序号
     * @param form 表单id号
     */
    function deleteUser(num, form) {
        var username = $(form).find(".changePwd input[name='username']").val();
        /*确认是否删除用户*/
        if (confirm("确定删除该用户?")) {
            $.ajax({
                url: "<%=basePath%>/gobang/deleteUser",
                method: "post",
                data: {
                    "username": username
                },
                success: function (result) {
                    if (result.code == 10) {
                        alert("删除成功");
                        /*移除当前表单元素*/
                        $("#li_" + num).remove();
                    } else if (result.code == 0) {
                        if (result.message != "") {
                            alert("不能删除自己");
                        }
                    } else {
                        console.log("删除失败");
                    }
                },
                error: function (e) {
                    console.log(e);
                },
                /*必须同步*/
                async: false
            });
        }
    }
</script>
</body>
</html>

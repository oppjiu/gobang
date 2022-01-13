var basePath = $("#basePath").attr("value");

/*主函数*/
$(function () {
    /**
     * 按钮一共有四种状态 0, 1, 2, 3
     * flag设置四种状态的点击
     */
    var flag = 0;

    var $changePwd = $(".changePwd");
    var $changeInfo = $(".changeInfo");
    var $changeImg = $(".changeImg");

    var $changePwdBtn = $(".changePwdBtn");
    var $changeInfoBtn = $(".changeInfoBtn");
    var $changeImgBtn = $(".changeImgBtn");
    var $chancelBtn = $(".chancelBtn");

    //初始化用户信息
    initUserinfo();

    $changePwdBtn.click(function () {
        if (flag == 0) {
            $changePwdBtn.val("提交");

            $changePwdBtn.attr("disabled", false);
            $changeInfoBtn.attr("disabled", true);
            $changeImgBtn.attr("disabled", true);

            $chancelBtn.css("display", "block");
            $changePwd.css("display", "grid");
            $changeInfo.css("display", "none");
            $changeImg.css("display", "none");
            flag = 1;
        } else if (flag == 1) {
            changePassword();
        }
    });

    $changeInfoBtn.click(function () {
        if (flag == 0) {
            $changeInfoBtn.val("保存");

            $changePwdBtn.attr("disabled", true);
            $changeInfoBtn.attr("disabled", false);
            $changeImgBtn.attr("disabled", true);

            $chancelBtn.css("display", "block");
            $changePwd.css("display", "none");
            $changeInfo.css("display", "grid");
            $changeImg.css("display", "none");
            flag = 2;
        } else if (flag == 2) {
            changeUserInfo();
        }
    });

    $changeImgBtn.click(function () {
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
            flag = 3;
        } else if (flag == 3) {
            uploadHeadIcon();
        }
    });

    $chancelBtn.click(function () {
        $changePwdBtn.val("改密");
        $changeImgBtn.val("编辑");
        $changeImgBtn.val("头像");
        $changePwdBtn.attr("disabled", false);
        $changeInfoBtn.attr("disabled", false);
        $changeImgBtn.attr("disabled", false);
        $chancelBtn.css("display", "none");
        flag = 0;
    });

    //修改密码
    function changePassword() {
        var username = $(".changePwd input[name='username']").val();
        var password = $(".changePwd input[name='password']").val();
        var newPassword = $(".changePwd input[name='newPassword']").val();

        if ($.trim(password) == "") {
            parent.alert("密码不能为空");
        } else if ($.trim(newPassword) == "") {
            parent.alert("新密码不能为空");
        } else if (password == newPassword) {
            parent.alert("旧密码与新密码相同，请重新输入新密码");
        } else {
            $.ajax({
                url: basePath + "/gobang/modifyUserPwd",
                data: {
                    "username": username,
                    "password": password,
                    "newPassword": newPassword
                },
                method: "post",
                success: function (result) {
                    if (result.code == 10) {
                        $(".changePwd input[name='password']").val("");
                        $(".changePwd input[name='newPassword']").val("");
                        parent.alert("改密成功");
                        $changePwdBtn.val("改密");
                        clearBtnState();
                    } else if (result.code == 0) {
                        parent.alert("密码输入错误");
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

    //修改个人信息
    function changeUserInfo() {
        var username = $(".changeInfo input[name='username']").val();
        var nickname = $(".changeInfo input[name='nickname']").val();
        var gender = $(".changeInfo select[name='gender']").val();
        var isAdmin = $(".changeInfo select[name='admin']").val();

        if ($.trim(nickname) == "") {
            parent.alert("昵称不能为空");
        } else {
            $.ajax({
                url: basePath + "/gobang/modifyUserInfo",
                data: {
                    "username": username,
                    "nickname": nickname,
                    "gender": gender,
                    "isAdmin": isAdmin
                },
                method: "post",
                success: function (result) {
                    if (result.code == 10) {
                        parent.alert("保存成功");
                        $changeInfoBtn.val("编辑");
                        clearBtnState();
                    } else if (result.code == 0) {
                        parent.alert("密码输入错误");
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

    //上传头像
    function uploadHeadIcon() {
        //判断是否选择了文件进行上传
        var fileInput = $('#uploadFile').get(0).files[0];
        if (fileInput) {
            var name = $(".changeImg input[name='username']").val();
            ajaxFileUpload(name);
            parent.alert("上传成功");
            $changeImgBtn.val("头像");
            clearBtnState();
            console.info(fileInput);
        } else {
            parent.alert("请选择上传文件！");
        }
    }

    function clearBtnState() {
        $changePwdBtn.attr("disabled", false);
        $changeInfoBtn.attr("disabled", false);
        $changeImgBtn.attr("disabled", false);
        $chancelBtn.css("display", "none");
        flag = 0;
    }
});

/**
 * 不安全，后端返回的信息能容包含密码
 * @param userData
 * @returns {null}
 */
function initUserinfo() {
    var usernameCookie = $.cookie("username");

    $.ajax({
        url: basePath + "/gobang/getSelfInfo",
        data: {
            "username": usernameCookie,
        },
        method: "post",
        success: function (result) {
            console.log("获取个人信息结果: ", result);
            if (result.code == 10) {
                var userData = result.data;
                if (userData["imgUrl"] != null) {
                    $(".manageFormHeadIcon").attr("src", userData["imgUrl"]);
                }
                $(".changePwd input[name='username']").val(userData["name"]);
                $(".changeImg input[name='username']").val(userData["name"]);
                $(".changeInfo input[name='username']").val(userData["name"]);
                /*更新等级*/
                var level = userData["level"];
                if (level >= 1) {
                    for (var i = 1; i <= level; i++) {
                        $("#levelStar_" + i).attr("src", "../images/system/亮星星.png");
                    }
                }
                $(".level").val(userData["level"]);

                $(".changeInfo input[name='nickname']").val(userData["nickname"]);
                $(".changeInfo select[name='gender']").val(userData["gender"]);
                if (userData["admin"]) {
                    $(".changeInfo select[name='admin']").val("true");
                } else {
                    $(".changeInfo select[name='admin']").val("false");
                }
            } else {
                console.log("获取登录用户信息错误");
            }
        },
        error: function (e) {
            console.log(e);
        }
    });
}

/**
 * 因为ajaxFileUpload脚本没有提供data传参所以必须使用url传参
 * 使用encodeURI(encodeURI(name))编码，在后端进行解码
 * @param name 用户名
 */
function ajaxFileUpload(name) {
    $.ajaxFileUpload({
        url: basePath + "/gobang/uploadImg/" + encodeURI(encodeURI(name)),
        fileElementId: 'uploadFile',
        secureuri: false,
        dataType: 'json',
        success: function (result) {
            console.log("图片上传结果", result);
            if (result.code == 10) {
                //头像更换
                $(".manageFormHeadIcon").attr("src", result.data["url"]);
            } else if (result.code == 0) {
                alert("上传出错");
            } else {
                alert("出现异常");
            }
        },
        error: function (data, status, e) {
            console.log(e);
        }
    });
}

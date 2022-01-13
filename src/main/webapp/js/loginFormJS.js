var basePath = $("#basePath").attr("value");
var wsBasePath = $("#wsBasePath").attr("value");

/**
 * loginForm使用了id
 */
$(function () {
    var $username = $("#username");
    var $password = $("#password");
    var $rememberForm = $("#rememberForm");
    var $validateCode = $("#validateCode");
    var $loginFormHeadIcon = $(".loginFormHeadIcon");

    var usernameCookie = $.cookie("username");
    var rememberFormCookie = $.cookie("rememberForm");

    //如果选择了记住我，自动填充表单信息
    if ($.trim(rememberFormCookie) == "true") {
        //给用户名框赋值
        $username.val(usernameCookie);
        //获取用户密码
        $.ajax({
            url: basePath + "/gobang/getUserInfoAuto",
            data: {
                "username": usernameCookie
            },
            method: "post",
            /**
             *
             * @param result result.code状态码 result.data的键pwd
             */
            success: function (result) {
                console.log("自动获取密码结果: " + result.data);
                //给密码框赋值
                if (result.code == 10) {
                    console.log("获取密码成功");
                    $password.val(result.data["pwd"]);
                    //如果用户有设置头像
                    if (result.data["imgUrl"] != null) {
                        $loginFormHeadIcon.attr("src", result.data["imgUrl"]);
                    }
                } else if (result.code == 0) {
                    console.log("获取密码失败");
                } else {
                    console.log("获取密码异常");
                }
            },
            error: function (e) {
                console.log(e);
            },
            async: true
        });
        //复选框打勾
        $rememberForm.prop("checked", true);
    }

    /**
     * 点击登录事件
     * 返回值-20, -10, 0, 10
     */
    $(".loginFormSubmit").click(function () {
        $validateCode.val()
        if ($.trim($username.val()) == "") {
            parent.alert("请输入用户名");
        } else if ($.trim($password.val()) == "") {
            parent.alert("请输入密码");
        } else if ($.trim($validateCode.val()) == "") {
            parent.alert("请输入验证码");
        } else {
            $.ajax({
                url: basePath + "/gobang/login",
                data: {
                    "username": $username.val(),
                    "password": $password.val(),
                    "rememberForm": $rememberForm.prop("checked"),
                    "validateCode": $validateCode.val()
                },
                method: "post",
                success: function (result) {
                    if (result.code == -30) {
                        parent.alert("已经登录");
                    } else if (result.code == -20) {
                        parent.alert("验证码输入错误");
                    } else if (result.code == -10) {
                        parent.alert("登录异常");
                    } else if (result.code == 0) {
                        parent.alert("登录失败，请检查用户名或密码输入是否正确");
                    } else if (result.code == 10) {
                        parent.alert("登录成功");
                        //父窗口初始化webSocket
                        parent.initWebSocketConnect(wsBasePath + "/ws/game");
                        parent.loginSuccess();
                        //当前页面（子页面）跳转
                        parent.$("#iframeChangePage").attr("src", basePath + "/gobang/welcomePage");
                    }

                    //验证码刷新
                    $("#validateCodeImg").attr("src", basePath + "/gobang/validateCode?time=" + new Date().getTime());
                },
                error: function (e) {
                    console.log(e);
                }
            });
        }
    });

    /**
     * 点击文字更新验证码事件
     */
    $("#changeValidateCode").click(function () {
        $("#validateCodeImg").attr("src", basePath + "/gobang/validateCode?time=" + new Date().getTime());
    });

    /**
     * 点击验证码图片更新验证码事件
     */
    $("#validateCodeImg").click(function () {
        $("#validateCodeImg").attr("src", basePath + "/gobang/validateCode?time=" + new Date().getTime());
    });
});

var basePath = $("#basePath").attr("value");

$(function () {
    var $username = $(".registerForm input[name='username']");
    var $nickname = $(".registerForm input[name='nickname']");
    var $gender = $(".registerForm select[name='gender']");
    var $password = $(".registerForm input[name='password']")
    var $confirmPwd = $(".registerForm input[name='confirmPwd']");

    //点击登录事件
    $(".registerFormSubmit").click(function () {
        if ($.trim($username.val()) == "") {
            parent.alert("用户名不能为空");
        } else if ($.trim($nickname.val()) == "") {
            parent.alert("昵称不能为空");
        } else if ($.trim($password.val()) == "") {
            parent.alert("密码不能为空");
        } else if ($.trim($confirmPwd.val()) == "") {
            parent.alert("确认密码不能为空");
        } else {
            if (($.trim($confirmPwd.val()) != $.trim($password.val()))) {
                parent.alert("两次密码输入不一致！");
            } else {
                //ajax提交表单数据
                $.ajax({
                    url: basePath + "/gobang/register",
                    data: {
                        "username": $username.val(),
                        "nickname": $nickname.val(),
                        "gender": $gender.val(),
                        "password": $password.val()
                    },
                    method: "post",
                    success: function (result) {
                        console.log("注册结果: ", result);
                        if (result.code == -10) {
                            parent.alert("出错了");
                        } else if (result.code == 0) {
                            parent.alert("注册失败，已有该用户名");
                        } else if (result.code == 10) {
                            parent.alert("注册成功");
                            $username.val("");
                            $nickname.val("");
                            $password.val("");
                            $confirmPwd.val("");
                        }
                    },
                    error: function (e) {
                        console.log(e);
                    },
                    async: true
                });
            }
        }
    });
});

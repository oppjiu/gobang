var basePath = $("#basePath").attr("value");
var wsBasePath = $("#wsBasePath").attr("value");

/*主函数*/
$(function () {
    var $settingsNavBtn = $(".settingsNavBtn");
    var $chatNavBtn = $(".chatNavBtn");
    var $loginNavBtn = $(".loginNavBtn");
    var $logoutNavBtn = $(".logoutNavBtn");
    var $registerNavBtn = $(".registerNavBtn");
    var $userInfoNavBtn = $(".userInfoNavBtn");
    var $homeNavBtn = $(".homeNavBtn");

    var $registerForm = $(".registerForm");
    var $loginForm = $(".loginForm");
    var $manageForm = $(".manageForm");

    var $header = $("#header");
    var $footer = $("#footer");
    var $sideNavbar = $("#sideNavbar");
    var $boxAccordion = $(".boxAccordion");
    var $popBox = $(".popBox");
    var $mask = $(".mask");
    var $iframe = $("iframe");

    initAccordionBox();
    $settingsNavBtn.click(function () {

    });

    $chatNavBtn.click(function () {
        chatRoomPage
    });

    /*返回主页*/
    $homeNavBtn.click(function () {
        $userInfoNavBtn.fadeOut();
        $logoutNavBtn.fadeOut();
        $sideNavbar.fadeOut();
        $homeNavBtn.fadeOut();
        $iframe.fadeOut(function () {
            $header.fadeIn();
            $footer.fadeIn();
            $boxAccordion.fadeIn();
            $loginNavBtn.fadeIn();
        });
    });

    /*注册按钮事件*/
    $registerNavBtn.click(function () {
        $registerForm.fadeIn();
        createMask();

        $registerForm.load(basePath + "/gobang/registerPage");
    });

    /*登录按钮事件*/
    $loginNavBtn.click(function () {
        $.ajax({
            url: basePath + "/gobang/loginPage",
            method: "get",
            /*需要登录*/
            success: function (result) {
                $loginForm.fadeIn();
                // $loginForm.css({display: "flex"});
                $manageForm.css({display: "none"});
                $registerForm.css({display: "none"});
                createMask();
                $loginForm.load(basePath + "/gobang/loginPage");
            },
            /*自动登录*/
            error: function (result) {
                loginSuccess();
            }
        });
    });

    /*个人信息按钮事件*/
    $userInfoNavBtn.click(function () {
        /*关闭盒子内的其他表单*/
        $loginForm.css({display: "none"});
        $registerForm.css({display: "none"});
        /*打开个人信息表单*/
        $manageForm.fadeIn();
        createMask();

        $manageForm.load(basePath + "/gobang/userInformationPage");
    });

    /*登出按钮事件*/
    $logoutNavBtn.click(function () {
        $.post(basePath + "/gobang/logout", function (result) {
            console.log(result);
            if (result.code == 10) {
                alert("登出成功");
                $userInfoNavBtn.fadeOut();
                $logoutNavBtn.fadeOut();
                $sideNavbar.fadeOut();
                $homeNavBtn.fadeOut();
                $iframe.fadeOut(function () {
                    $header.fadeIn();
                    $footer.fadeIn();
                    $boxAccordion.fadeIn();
                    $loginNavBtn.fadeIn();
                });
            } else {
                alert("登出失败, 请先登录");
            }
        });
    });

    /**
     * 点击到遮罩就退出
     */
    $mask.click(function () {
        closeMaskAndPopBox();
    });

    /*导航页跳转页面*/
    $(".pageJump").click(function () {
        $("#iframeChangePage").attr("src", $(this).attr("target"));
    });
});

/*初始化手风琴文本*/
function initAccordionBox() {
    var Hover = $(".container .box")
    Hover.each(function () {
        $(this).mouseenter(function () {
            Hover.removeClass("Hover");
            $(this).addClass("Hover");
        })
    });
}

/*登录成功 loginPage调用*/
function loginSuccess() {
    $(".loginNavBtn").fadeOut();
    $("#header").fadeOut();
    $("#footer").fadeOut();
    $(".boxAccordion").fadeOut(function () {
        $("#sideNavbar").fadeIn();
        $("iframe").fadeIn();
        $(".userInfoNavBtn").fadeIn();
        $(".logoutNavBtn").fadeIn();
        $(".homeNavBtn").fadeIn();
    });

    closeMaskAndPopBox();
}
/*---------------------------------------

	弹窗代码可用抽取封装为函数

-----------------------------------------*/

/*创建弹窗*/
function createMask() {
    /*显示弹窗*/
    $(".mask").css({
        display: 'block',
        height: $(document).offsetHeight
    })
    setPopBoxPosition(false);
}

/*关闭遮罩和弹窗*/
function closeMaskAndPopBox() {
    $(".mask, .popContent, .popBox").fadeOut();
}

/**
 * 设置弹窗位置
 *
 * @param isResize 是否是窗口重置
 */
function setPopBoxPosition(isResize) {
    var $popBox = $(".popBox");
    //获取元素自身的宽度
    var boxWidth = $popBox.offsetWidth;
    //获取元素自身的高度
    var boxHeight = $popBox.offsetHeight;
    //获取实际页面的left值。（页面宽度减去元素自身宽度/2）
    var left = (document.documentElement.clientWidth - boxWidth) / 2;
    //获取实际页面的top值。（页面宽度减去元素自身高度/2）
    var top = (document.documentElement.clientHeight - boxHeight) / 2;

    /*如果是窗口重置 */
    if (isResize) {
        $popBox.css({
            left: left + "px",
            top: top + "px",
        });
    } else {
        /*不是窗口重置则设置display: flex*/
        $popBox.css({
            left: left + "px",
            top: top + "px",
            display: "flex"
        });
    }
}

/*页面刷新重置盒子位置 */
window.onresize = function () {
    setPopBoxPosition(true);
}
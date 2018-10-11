
// 用于多异步控制
var p1 = false;
var p2 = false;
var bulletin = null;

// 初始化页面
$.get(API.login_type, function (data) {

    if (!fillUserInfo(data.data)) return;
    layoutBars();
    p1 = true;
    if (p2)
        layoutViews();
});

$.get(API.get_bull, {id: getOneArg(false)}, function (data) {

    if (data.status !== 200) {
        alert(data.message);
        $('.bull_loading').text('加载失败');
        $('#bull_content').text('3 秒后跳转');
        setTimeout(function () {
            location.href = "/index.html";
        }, 3000);
        return;
    }
    bulletin = data.data;
    p2 = true;
    if (p1)
        layoutViews();
});

// 布局
function layoutViews() {
    if (userInfo.role === ROLE.admin ||
        (userInfo.role === ROLE.lab && bulletin.from !== '系统公告' &&
            bulletin.from.indexOf(userInfo.lab.name) !== -1))
        $('#footer_butts').removeClass("hidden");
    $('#bull_from').text(bulletin.from);
    $('#bull_time').text(formatServerTime(bulletin.time));
    $('#bull_title').text(bulletin.title);
    $('#bull_content').html(bulletin.content);
    $('#read_count').text(bulletin.read_count);
}

jQuery(function($) {

    $('#edit_bull').on('click', function (e) {
        location.href = "/bulletin/edit.html" + location.search;
        e.preventDefault();
    });

    $('#delete_bull').on('click', function (e) {
        $.post(API.del_bull, {ids: bulletin.id}, function (data) {
            alert(data.message);
            if (data.status === 200) {
                location.href = '/index.html';
            }
        });
        e.preventDefault();
    });

});
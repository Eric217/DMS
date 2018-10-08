
// 用于多异步控制
var p1 = false;
var p2 = false;
var bulletin = null;
var bull_id = getOneArg(false);

// 初始化页面
$.get(API.login_type, function (data) {

    fillUserInfo(data.data);
    if (userInfo.role === ROLE.normal) {
        alert("没有权限，即将离开页面");
        location.href = '/index.html';
    }
    layoutBars();
    p1 = true;
    if (p2)
        layoutViews();
});

if (bull_id.length > 0) { // edit bull with id

    $.get(API.get_bull, {id: bull_id}, function (data) {

        if (data.status !== 200) {
            alert(data.message);
            return;
        }
        bulletin = data.data;
        $('#bull_name').val(bulletin.title);
        $('#bull_content').val(bulletin.content);
        p2 = true;
        if (p1)
            layoutViews();
    });


    if (userInfo.role === ROLE.normal) {
        alert("没有权限，即将离开页面");
        location.href = '/index.html';
    }

} else { // new bull
    p2 = true;
    if (p1)
        layoutViews();
}

// 布局
function layoutViews() {
    var pass = false;
    if (userInfo.role === ROLE.admin)
        pass = true;
    else {
        if (bulletin) {
            if (bulletin.from !== '系统公告' && bulletin.from.indexOf(userInfo.lab.name) !== -1)
                pass = true;
        } else {
            pass = true;
        }
    }
    if (!pass) {
        alert("没有权限，即将离开页面");
        location.href = '/index.html';
    }
}

jQuery(function($) {

    $('#post_edit_bull').on('click', function (e) {
        var t1 = $('#bull_name').val();
        var t2 = $('#bull_content').val();
        if (bulletin) {
            if (t1 === bulletin.title && t2 === bulletin.content)
                location.href = "/index.html";
        }

        if (bull_id.length > 0) { // update with id
            bulletin = {
                id: bull_id,
                title: t1,
                content: t2
            };
            $.post(API.update_bull, bulletin, function (data) {
                alert(data.message);
                if (data.status === 200)
                    location.href = '/bulletin/detail.html' + location.search;
            })
        } else { // create
            bulletin = {
                title: t1,
                content: t2
            };
            $.post(API.create_bull, bulletin, function (data) {
                alert(data.message);
                if (data.status === 200)
                    location.href = '/index.html';
            })
        }
        e.preventDefault();
    });

});

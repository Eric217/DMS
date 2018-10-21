
var v1 = false;
var v2 = false;
var p_idx = getOneArg(false);
p_idx = Number(p_idx);

var identity = 2;

if (!p_idx) {
    alert("参数错误");
    location.href = '/index.html';
}
var proj = null;

// 初始化页面
$.get(API.login_type, function (data) {

    if (!fillUserInfo(data.data)) return;
    layoutBars();
    v1 = true;
    if (v2)
        layoutViews();
});

$.get(API.get_a_proj, {id: p_idx}, function (data) {
    if (data.status !== 200) {
        alert(data.message);
        if (data.status === 400)
            location.href = '/project/my.html';
        return;
    }
    proj = data.data;
    v2 = true;
    if (v1)
        layoutViews();
});

function layoutViews() {

    $('#project_name').text(proj.name);
    $('#desc').text(placeholder(proj.description));
    $('#coach').text(placeholder(proj.coach_id));
    $('#lab_select').text(proj.lab_name);
    $('#duration').text(parseInt(proj.duration/(24*3600)));
    $('#type').text(proj.type);
    $('#aim').html(proj.aim);

    var c = 1;
    var l = proj.members.length;

    $('.mem' + l).hide();
    // noinspection JSUnresolvedVariable
    for (var i = 1; i <= l; i++) {
        var id_idx = '#id' + c;
        // noinspection JSUnresolvedVariable
        var tid = proj.members[i-1].id;
        if (tid !== proj.leader_id) {
            c++;
            $(id_idx).text(proj.members[i-1].name + ', 学号：' + tid);
        } else {
            $('#leader_id').text(proj.members[i-1].name + ', 学号：' + tid);
        }
    }
}

// 按钮事件
jQuery(function($) {

    $('#delete_butt').on('click', function () {

        $.post(API.del_proj_admin, {ids: p_idx}, function (data) {
            reactToResponse(data, function () {
                if (userInfo.role === ROLE.admin)
                    location.href = '/project/all.html';
                else
                    location.href = '/project/lab.html';
            });
        });
    });

    $('#edit_butt').on('click', function () {
        location.href = '/project/edit.html' + location.search;
    });

});
var lab = null;
var lab_id = getOneArg(false);

if (!lab_id || lab_id.length === 0) {
    alert("参数错误, 正在跳转");
    location.href = '/lab/list.html';
}

var p1 = false;
var p2 = false;

$.get(API.login_type, function (data) {

    if (!fillUserInfo(data.data)) return;

    layoutBars();
    p1 = true;
    if (p2)
        layoutViews();

});

$.get(API.get_a_lab, {id: lab_id}, function (data) {
    if (data.status !== 200) {
        alert(data.message);
        return;
    }
    lab = data.data;
    p2 = true;
    if (p1)
        layoutViews();
});

function layoutViews() {
    if (userInfo.role === ROLE.lab && '' + userInfo.lab.id === lab_id)
         $('#edit_lab').removeClass('hidden');

    $('#lab_title').text(lab.name);
    $('#lab_content').html(lab.description);
    $('#lab_location').text(lab.classroom);
    // noinspection JSUnresolvedVariable
    $('#create_time').text(lab.create_time);

    // noinspection JSUnresolvedVariable
    if (lab.leader) {
        $('#leader_name').text(lab.leader.name);
        $('#leader_email').text(lab.leader.email);
    } else {
        $('.leader_header').hide();
        $('#empty_header').removeClass('hidden');
    }
}

$(function ($) {
    $('#delete_lab').on('click', function () {
        MyConfirm("删除一个实验室会删除其所有项目。确定要继续吗？", function () {
            $.post(API.del_lab, {id: lab_id}, function (data) {
                alert(data.message);
                location.href = data.status === 200 ? '/lab/list.html' : location.href;
            });
        });
    });
    $('#edit_lab').on('click', function () {
        location.href = '/lab/edit.html?' + lab_id;
    });
});
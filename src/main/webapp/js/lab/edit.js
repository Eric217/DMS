
var lab = null;
var lab_id = getOneArg(false);

if (lab_id.length === 0)
    location.href = '/lab/create.html';

// 初始化页面
$.get(API.login_type, function (data) {

    if (!fillUserInfo(data.data)) return;

    if (userInfo.role === ROLE.normal || (userInfo.role === ROLE.lab &&
        '' + userInfo.lab.id !== lab_id)) {
        PermissionDenied("权限不足，即将离开页面", '/lab/list.html');
        return;
    }

    $.get(API.get_a_lab, {id: lab_id}, function (data) {
        if (data.status !== 200) {
            alert(data.message);
            return;
        }
        lab = data.data;
        layoutViews();
    });

    layoutBars();

});

function layoutViews() {
    if (userInfo.role === ROLE.admin)
        $('.admin_input').attr("disabled", false);

    $('#lab_name').val(lab.name);
    $('#lab_desc').html(lab.description);
    $('#lab_room').val(lab.classroom);
    $('#leader').val(lab.leader_id);
    // noinspection JSUnresolvedVariable
    $('#create_time').val(lab.create_time);
}

if (lab_id.length > 0) {


} else
    location.href = '/lab/create.html';


// 按钮事件
jQuery(function ($) {

    $('#save_lab').on('click', function (e) {

        var mod = {
            id: lab_id,
            name: $('#lab_name').val().replace('实验室', '').trim(),
            description: $('#lab_desc').html(),
            leader_id: $('#leader').val(),
            classroom: $('#lab_room').val()
        };

        if (mod.name === lab.name && mod.leader_id === lab.leader_id &&
            mod.classroom === lab.classroom && mod.description === lab.description) {
            location.href = '/lab/detail.html' + '?' + lab_id; return; }

        if (mod.name.length < 1) {
            alert("实验室名称不能为空或包含非法字符");
            return;
        }
        if (mod.description.length < 1) {
            alert("实验室描述不能为空");
            return;
        }
        if (mod.classroom.length < 1) {
            alert("必须为实验室指定一个教室或房间");
            return;
        }

        $.post(API.modify_lab, mod, function (data) {
            reactToResponse(data, function () {
                location.href = "/lab/detail.html" + '?' + lab_id;
            });
        });
        e.preventDefault();
    });

    $('#delete_lab').on('click', function () {
        MyConfirm("删除一个实验室会删除其所有项目。确定要继续吗？", function () {
            $.post(API.del_lab, {id: lab_id}, function (data) {
                alert(data.message);
                location.href = data.status === 200 ? '/lab/list.html' : location.href;
            });
        });
    });

    $('#lab_desc').ace_wysiwyg({
        toolbar:
            [
                'font',
                null,
                'fontSize',
                null,
                {name: 'bold', className: 'btn-info'},
                {name: 'italic', className: 'btn-info'},
                {name: 'strikethrough', className: 'btn-info'},
                {name: 'underline', className: 'btn-info'},
                null,
                {name: 'insertunorderedlist', className: 'btn-success'},
                {name: 'insertorderedlist', className: 'btn-success'},
                {name: 'outdent', className: 'btn-purple'},
                {name: 'indent', className: 'btn-purple'},
                null,
                {name: 'justifyleft', className: 'btn-primary'},
                {name: 'justifycenter', className: 'btn-primary'},
                {name: 'justifyright', className: 'btn-primary'},
                {name: 'justifyfull', className: 'btn-inverse'},
                null,
                {name: 'createLink', className: 'btn-pink'},
                {name: 'unlink', className: 'btn-pink'},
                null,
                null,
                'foreColor',
                null,
                {name: 'undo', className: 'btn-grey'},
                {name: 'redo', className: 'btn-grey'}
            ]
    }).prev().addClass('wysiwyg-style2');

});
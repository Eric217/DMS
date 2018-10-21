
var v1 = false;
var v2 = false;
var p_idx = getOneArg(false);
if (p_idx.length === 0)
    location.href = '/project/create.html';
p_idx = Number(p_idx);
var proj = null;

// 初始化页面
$.get(API.login_type, function (data) {

    if (!fillUserInfo(data.data)) return;
    if (userInfo.role === ROLE.normal) {
        location.href = '/project/modify.html'; return; }
    layoutBars();
    v1 = true;
    if (v2)
        layoutViews();
});

$.get(API.get_a_proj, {id: p_idx}, function (data) {
    if (data.status !== 200) {
        alert(data.message);
        return;
    }
    proj = data.data;
    if (userInfo.role === ROLE.lab && userInfo.lab.name !== proj.lab_name) {
        PermissionDenied('没有权限', '/project/lab.html'); return; }
    v2 = true;
    if (v1)
        layoutViews();
});

function layoutViews() {
    $('#project_name').val(proj.name);
    $('#desc').val(proj.description);
    $('#coach').val(proj.coach_id);
    var lab_sel = $('#lab_select');
    lab_sel.append("<option value='" + proj.lab_name + "'>" + proj.lab_name + "</option>");
    lab_sel.val(proj.lab_name);
    lab_sel.attr('disabled', true);
    $('#duration').val(parseInt(proj.duration/(24*3600)));
    $('#type').val(proj.type);
    $('#aim').html(proj.aim);
    var lab_lea = $('#leader_id');
    lab_lea.val(proj.leader_id);
    lab_lea.attr('disabled', true);
    var c = 1;
    // noinspection JSUnresolvedVariable
    for (var i = 1; i <= proj.members.length; i++) {
        var id_idx = '#id' + c;
        // noinspection JSUnresolvedVariable
        var tid = proj.members[i-1].id;
        if (tid !== proj.leader_id) {
            c++;
            $(id_idx).val(tid);
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

    $('#save_butt').on('click', function (e) {
        var c = '@';
        var id1_input = $('#id1');
        var dur = $('#duration').val();
        dur = Number(dur)*24*3600;
        if (!dur || dur < proj.duration) {
            alert('只能延长持续时间；时间格式需要正确');
            return;
        }
        var loginModel = {
            id:         proj.id,
            name:       $('#project_name').val(),
            duration:   dur,
            description:$('#desc').val(),
            coach_id:   $("#coach").val(),
            lab_name:   $("#lab_select").val(),
            leader_id:  proj.leader_id,
            opt_status: proj.opt_status,
            aim:        $('#aim').html(),
            type:       $('#type').val(),
            memberIds:  id1_input.val() +c+ $('#id2').val() +c+ $('#id3').val() +c+
                $('#id4').val() +c+ $('#id5').val()
        };
        if (loginModel.name.length < 1) {
            alert("项目名不能为空");
            return;
        }
        if (loginModel.lab_name.length < 1) {
            alert("实验室不能为空");
            return;
        }
        if (loginModel.aim.length < 1) {
            alert("项目目标不能为空");
            return;
        }
        if (loginModel.type.length < 1) {
            alert("项目类型不能为空");
            return;
        }
        if (id1_input.val().length < 1) {
            alert("成员学号不能为空");
        }

        $.post(API.update_proj, loginModel, function (data) {
            alert(data.message);
            if (data.status === 200) {
                location.href = "/project/detail.html?" + p_idx;
            }
        });
        e.preventDefault();
    });
    $('#aim').ace_wysiwyg({
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
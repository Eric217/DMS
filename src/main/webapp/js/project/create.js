
// 初始化页面
$.get(API.login_type, function (data) {

    if (data.data.role === ROLE.admin) {
        alert("管理员不参与项目，因此不可创建");
        location.href = "/index.html";
        return;
    }

    if (!fillUserInfo(data.data)) return;
    layoutBars();

});

// 下拉框
$.get(API.get_lab_names, function (data) {
    if (data.status !== 200) {
        alert(data.message);
        return;
    }
    var names = data.data;
    var opts = "";
    for (var idx in names) {
        var n = names[idx];
        opts += "<option value='" + n + "'>" + n + "</option>";
    }
    $('#lab_select').append(opts);
});

// 按钮事件
jQuery(function($) {

    $('#create_butt').on('click', function (e) {
        var c = '@';
        var id1_input = $('#id1');
        var dur = $('#duration').val();
        dur = Number(dur)*24*3600;
        if (!dur) dur = 2592000;
        var loginModel = {
            name:       $('#project_name').val(),
            duration:   dur,
            description:$('#desc').val(),
            coach_id:   $("#coach").val(),
            lab_name:   $("#lab_select").val(),
            leader_id:  userInfo.user.id,
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

        $.post(API.create_project, loginModel, function (data) {
            alert(data.message);
            if (data.status === 200) {
                location.href = "/project/my.html";
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
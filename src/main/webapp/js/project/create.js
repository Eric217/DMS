
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
        var loginModel = {
            name:       $('#project_name').val(),
            duration:   (isNumber(dur) ? dur : 2592000),
            description:$('#desc').val(),
            coach_id:   $("#coach").val(),
            lab_name:   $("#lab_select").val(),
            leader_id:  userInfo.user.id,
            aim:        $('#aim').val(),
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

});
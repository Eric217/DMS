
jQuery(function($) {

    $('#create_proj_butt').on('click', function (e) {
        var loginModel = {
            name: $('#name').val(),
            duration: 2592000,
            description: $('#desc').val(),
            coach_id: $("#coach").val(),
            lab_name: $("#lab").val(),
            leader_id: userInfo.user.id,
            aim: $('#aim').val(),
            type:  $('#type').val(),
            id1: $('#id1').val(),
            id2: $('#id2').val(),
            id3: $('#id3').val(),
            id4: $('#id4').val(),
            id5: $('#id5').val()
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
        if (loginModel.id1.length < 1) {
            alert("每个项目最少二人");
        }

        $.post(API.create_proj, loginModel, function (data) {
            alert(data.message);
            if (data.status === 200) {
                location.href = "/lab/index_l.html";
            }
        });
        e.preventDefault();
    });

});

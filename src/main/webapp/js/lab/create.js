
// 初始化页面
$.get(API.login_type, function (data) {

    if (!fillUserInfo(data.data)) return;

    if (data.data.role !== ROLE.admin) {
        alert("没有权限，即将跳转");
        location.href = "/index.html";
        return;
    }
    layoutBars();
});


// 按钮事件
jQuery(function($) {

    $('#create_butt').on('click', function (e) {

        var create_lab_mod = {
            name: $('#lab_name').val().replace('实验室', '').trim(),
            description: $('#lab_desc').html(),
            leader_id: $('#leader').val(),
            classroom: $('#lab_room').val()
        };

        if (create_lab_mod.name.length < 1) {
            alert("实验室名称不能为空或包含非法字符");
            return;
        }
        if (create_lab_mod.description.length < 1) {
            alert("实验室描述不能为空");
            return;
        }
        if (create_lab_mod.classroom.length < 1) {
            alert("必须为实验室指定一个教室或房间");
            return;
        }

        $.post(API.create_lab, create_lab_mod, function (data) {
            reactToResponse(data, function () {
                location.href = "/lab/list.html";
            });
        });
        e.preventDefault();
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
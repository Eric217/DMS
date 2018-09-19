
jQuery(function($) {
    $('#btn-login-light').on('click', function (e) {
        $('body').attr('class', 'login-layout light-login');
        $('#id-company-text').attr('class', 'blue');
        e.preventDefault();
    });
    $('#btn-login-dark').on('click', function (e) {
        $('body').attr('class', 'login-layout');
        $('#id-company-text').attr('class', 'blue');
        e.preventDefault();
    });
    $('#btn-login-blur').on('click', function (e) {
        $('body').attr('class', 'login-layout blur-login');
        $('#id-company-text').attr('class', 'light-blue');
        e.preventDefault();
    });
    $('#login-button').on('click', function (e) {
        var loginModel = {
            sid: $('#username_input').val(),
            password: $("#pass_input").val(),
            code: $("#verify_input").val(),
            remember: document.getElementById("checkbox").checked
        };
        if (loginModel.sid.length < 1) {
            alert("用户名不能为空");
            return;
        }
        if (loginModel.password.length < 1) {
            alert("密码不能为空");
            return;
        }
        if (loginModel.code.length < 1) {
            alert("请输入验证码");
            return;
        }
        $.post(API.login, loginModel, function (data) {
            if (data.status === 200) {
                jump(data.data);
            } else {
                alert(data.message);
                refreshCode();
            }
        });
        e.preventDefault();
    });
    $("#code_refresh").on('click', function (e) {
        refreshCode();
        e.preventDefault();
    });
    $("#code_image").on('click', function (e) {
        refreshCode();
        e.preventDefault();
    });

    $("#register_button").on('click', function (e) {

        var regModel = {
            email: $('#email_input1').val(),
            password: $("#pass_input2").val(),
            code: $("#verify_input2").val(),
            name: $('#name_input').val(),
            check: document.getElementById("check_r_box").checked
        };
        if (regModel.email.length < 1) {
            alert("邮箱不能为空");
            return;
        }
        if (regModel.name.length < 1) {
            alert("邮箱不能为空");
            return;
        }
        if (regModel.password.length < 8 || regModel.password.length > 16) {
            alert("密码长度不符合条件");
            return;
        }
        if (regModel.code.length < 1) {
            alert("请输入验证码");
            return;
        }
        if (!regModel.check) {
            alert("请先同意服务条款");
            return;
        }
        $.post(API.register, regModel, function (data) {
            alert(data.message);
            if (data.status === 200) {
                location.href = "http://sdu.edu.cn";
            }
        });
        e.preventDefault();
    });
    $("#send_code").on('click', function (e) {
        var model = {email: $('#email_input1').val()};
        if (model.email.length < 1) {
            alert("邮箱不能为空"); return;
        }
        if (model.email.length < 3) { // TODO: - Regex
            alert("只允许用山东大学邮箱注册"); return;
        }
        $.post(API.register_code, model, function (data) {
            alert(data.message);
        });
        e.preventDefault();
    });
    $("#send_code2").on('click', function (e) {
        refreshCode();
        e.preventDefault();
    });

});

function refreshCode() {
    var code_img = $('#code_image');
    code_img.attr("src", null);
    code_img.attr("src", API.image_code);
}

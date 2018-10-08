
function refreshCode() {
    var code_img = $('#code_image');
    code_img.attr("src", null);
    code_img.attr("src", API.image_code + "?" + Math.random());
}

function getCode(jqv_obj, n) {

    (function() {
        if(n > 0) {
            $('#send_code').attr("disabled", true);
            $('#send_code2').attr("disabled", true);
            jqv_obj.text('' + (n--) + '秒后再试');
            setTimeout(arguments.callee, 1000);
        } else {
            $('#send_code').attr("disabled", false);
            $('#send_code2').attr("disabled", false);
            jqv_obj.text('发送验证码');
        }
    })();
}

onpageshow = function () {
    $.get(API.login_type, function (data) {
        var u_t = data.data.role;
        if (u_t === undefined || u_t === ROLE.no_user)
            return;
        location.href = "/index.html";
    });
};

jQuery(function($) {

    $(document).on('click', '.toolbar a[data-target]', function(e) {
        e.preventDefault();
        var target = $(this).data('target');
        $('.widget-box.visible').removeClass('visible');//hide others
        $(target).addClass('visible');//show target
    });

    $('#btn-login-light').on('click', function (e) {
        $('body').attr('class', 'login-layout light-login');
        $('#id-company-text').attr('class', 'blue');
        $('#id-title-text').attr('class', 'blue');
        e.preventDefault();
    });
    $('#btn-login-dark').on('click', function (e) {
        $('body').attr('class', 'login-layout');
        $('#id-company-text').attr('class', 'light-blue');
        $('#id-title-text').attr('class', 'blue');
        e.preventDefault();
    });
    $('#btn-login-blur').on('click', function (e) {
        $('body').attr('class', 'login-layout blur-login');
        $('#id-company-text').attr('class', 'light-blue');
        $('#id-title-text').attr('class', 'light-blue');
        e.preventDefault();
    });
    $('#sdu_pic').on('click', function () {
        location.href = "http://www.sdu.edu.cn";
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
                if (data.data.role === undefined || data.data.role === ROLE.no_user)
                    alert("发生了意外错误");
                else
                    location.href = "/index.html";
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
            alert("姓名不能为空");
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
        REQ("post", regModel, API.register, function () {
            $('#register_button').attr({disabled: "disabled"});
        }, function () {
            $('#register_button').removeAttr("disabled");
        }, function (data) {
            alert(data.message);
            if (data.status === 200) {
                location.href = "/login.html";
            }
        });
        e.preventDefault();
    });
    $("#send_code").on('click', function (e) {
        var model = {email: $('#email_input1').val()};
        if (model.email.length < 1) {
            alert("邮箱不能为空"); return;
        }
        if (model.email.length < 3) { // TODO: - Regex: is a sdu email ?
            alert("只允许用山东大学邮箱注册"); return;
        }
        $.post(API.register_code, model, function (data) {
            alert(data.message);
        });
        getCode($('.sending_code'), 10);
        e.preventDefault();
    });
    $("#send_code2").on('click', function (e) {
        var model = {email: $('#email_input2').val()};
        if (model.email.length < 1) {
            alert("邮箱不能为空"); return;
        }
        if (model.email.length < 3) { // TODO: - Regex: is a sdu email ?
            alert("该邮箱尚未注册"); return;
        }
        $.post(API.resetPass_code, model, function (data) {
            alert(data.message);
        });
        getCode($('.sending_code'), 10);
        e.preventDefault();
    });
    $("#reset_password").on('click', function (e) {

        var resModel = {
            email: $('#email_input2').val(),
            password: $("#pass_in3").val(),
            code: $("#verify_input3").val(),
        };
        if (resModel.email.length < 1) {
            alert("邮箱不能为空");
            return;
        }
        if (resModel.password.length < 8 || resModel.password.length > 16) {
            alert("密码长度不符合条件");
            return;
        }
        if (resModel.code.length < 1) {
            alert("请输入验证码");
            return;
        }
        $.post(API.resetPassword, resModel, function (data) {
            alert(data.message);
            if (data.status === 200) {
                location.href = "/login.html";
            }
        });
        e.preventDefault();
    });

});


if ('ontouchstart' in document.documentElement)
    document.write("<script src='assets/js/jquery.mobile.custom.min.js'>" + "<" + "/script>");
$('body').attr('class', 'login-layout light-login');
$('#id-company-text').attr('class', 'blue');
$('#id-title-text').attr('class', 'blue');
refreshCode();
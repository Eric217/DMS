
var userInfo = {
    role: 0,
    user: null,
    lab: null,
    admin: null
};

var ProjectStatus = {
    checking: "审核中",
    rejected: "已拒绝",
    canceled: "已取消",
    processing: "进行中",
    finished: "已完成",
    overtime: "已超时",

    request_modi: "请求修改中",
    request_chck: "请求检查中"
};

/** @param data jquery response -> data */
function fillUserInfo(data) {
    var u_t = data.role;
    if (u_t === undefined || u_t === ROLE.no_user) {
        if (userInfo.role !== ROLE.no_user)
            alert('会话已过期，即将刷新页面');
        location.href = "/login.html";
    }
    userInfo.role = u_t;
    userInfo.user = data.student;
    userInfo.lab = data.lab;
}

// 用于响应点击浏览器的返回 按钮，稍稍控制一下权限
var execOnce = true;
onpageshow = function () {
    if (execOnce)
        execOnce = false;
    else {
        $.get(API.login_type, function (data) {
            var u_t = data.data.role;
            if (u_t === undefined || u_t === ROLE.no_user)
                location.href = "/login.html";
            else if (u_t !== userInfo.role)
                PermissionDenied("页面已过期，即将刷新页面");
            userInfo.user = data.data.student;
            userInfo.lab = data.data.lab;
        });
    }
};

// 日期格式化器
Date.prototype.format = function(format) {
    var o = {
        "M+" : this.getMonth()+1, //month
        "d+" : this.getDate(),    //day
        "h+" : this.getHours(),   //hour
        "m+" : this.getMinutes(), //minute
        "s+" : this.getSeconds(), //second
        "q+" : Math.floor((this.getMonth()+3)/3),  //quarter
        "S" : this.getMilliseconds() //millisecond
    };
    if(/(y+)/.test(format)) format=format.replace(RegExp.$1,
        (this.getFullYear()+"").substr(4 - RegExp.$1.length));
    for(var k in o)if(new RegExp("("+ k +")").test(format))
        format = format.replace(RegExp.$1, RegExp.$1.length === 1 ? o[k] : ("00"+ o[k]).substr((""+ o[k]).length));
    return format;
};

function getServerTime(time) {

    return new Date(time.replace('+0000', 'Z'));
}

function formatServerTime(time) {
    try {
        var a = time.split('T');
        var b = a[1].split(':');
        a = a[0] + ' ' + b[0] + ':' + b[1];
        return a;
    } catch (e) {
        return '日期格式异常';
    }
}

function isNumber(obj) {
    return typeof obj === 'number' && !isNaN(obj)
}

function checkLength(dom, num) {
    if (dom.value.length > num)
        dom.value = dom.value.substring(0, num);
}

// html 参数
function getArgsObj() {
    var str = (location.search.replace('?', ''));
    var args = str.split('&');
    var arg_obj = new Map();
    for (var i = args.length - 1; i >= 0; i--) {
        if (args[i].length > 0) {
            var arg = args[i].split('=');
            if (arg.length === 1)
                arg_obj.set(arg[0], '');
            else if (arg.length === 2)
                arg_obj.set(arg[0], arg[1]);
        }
    }
    return arg_obj;
}

// html 参数，如果 withEqual = true，?a=b 返回 [a, b]; 否则 返回 a
function getOneArg(withEqual) {
    var arg = location.search.replace('?', '').split('=');
    if (withEqual) {
        if (arg.length === 0)
            return ['', ''];
        if (arg.length === 1)
            return [arg[0], ''];
        return arg;
    }
    if (arg.length === 0)
        return '';
    return arg[0];
}

function calculate_status(pro) {
    if (5 === pro.opt_status)
        return ProjectStatus.request_chck;
    if (2 === pro.opt_status)
        return ProjectStatus.rejected;
    if (pro.start_time === undefined)
        return ProjectStatus.checking;
    if (4 === pro.opt_status)
        return ProjectStatus.canceled;
    if (pro.end_time !== undefined)
        return ProjectStatus.finished;

    if (pro.start_time !== undefined && pro.end_time === undefined &&
        pro.start_time.getTime() + pro.duration*1000 > new Date().getTime()) {
        if (3 === pro.opt_status)
            return ProjectStatus.request_modi;
        return ProjectStatus.processing;
    }
    return ProjectStatus.overtime;

}

// 根据权限初步布局
function layoutBars() {

    if (userInfo.role === ROLE.admin) {
        $('.admin_cls').removeClass("hidden");
        $('#user_name_label').text("管理员");
        $('#head_image').attr('src', "/assets/images/avatars/admin.png");

    } else if (userInfo.role !== ROLE.no_user) { // 已登陆的学生，含实验室负责人
        $('#head_image').attr("src", "/assets/images/avatars/user2.png");
        $('#user_name_label').text(userInfo.user.name);
        $('#my_project_li').removeClass("hidden");
        $('.stu_profile').removeClass("hidden");
        if (userInfo.role === ROLE.lab) {
            $('#right_top_lab').removeClass("hidden");
            $('.my_lab_cls').removeClass("hidden");
        }
    }
}


function logout() {
    $.post(API.logout, function (data) {
        if (data.status === 200)
            location.href = "/login.html";
        else
            alert(data.message);
    });
}

function PermissionDenied(msg) {
    if (!msg)
        msg = "权限不足，即将刷新页面";
    alert(msg);
    // noinspection SillyAssignmentJS
    location.href = location.href;
}

function reactToResponse(data, success) {
    switch (data.status) {
        case 200:
            alert(data.message);
            success(); break;
        case 300:
            alert(data.message); break;
        case 400:
            PermissionDenied(data.message); break;
        default:
            alert("操作失败");
    }
}
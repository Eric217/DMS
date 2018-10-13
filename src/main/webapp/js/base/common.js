
var userInfo = {
    role: 0,
    user: null,
    lab: null,
    admin: null
};

var ProjectStatus = {

    /** 待审核*/
    checking: "待审核",

    rejected: "已拒绝",
    canceled: "已取消",
    finished: "已完成",
    overtime: "已超时",

    processing: "进行中",

    request_modi: "请求修改中", // 3
    request_cncl: "请求取消中", // 5
    request_chck: "请求验收中"  // 6
};

function getProjStatus(rowObject) {
    if (!rowObject.start_time) {
        if (rowObject.opt_status === 2)
            return ProjectStatus.rejected;
        return ProjectStatus.checking;
    } else if (rowObject.opt_status === 4) {
        return ProjectStatus.canceled;
    } else if (rowObject.end_time)
        return ProjectStatus.finished;
    else if (secondsSince(rowObject.start_time) > rowObject.duration)
        return ProjectStatus.overtime;
    if (rowObject.opt_status === 3)
        return ProjectStatus.request_modi;
    if (rowObject.opt_status === 5)
        return ProjectStatus.request_cncl;
    if (rowObject.opt_status === 6)
        return ProjectStatus.request_chck;
    return ProjectStatus.processing;
}

/** @param data jquery response -> data */
function fillUserInfo(data) {
    var u_t = data.role;
    if (u_t === undefined || u_t === ROLE.no_user) {
        if (ROLE.no_user !== userInfo.role)
            alert('会话已过期，即将跳转');
        location.href = "/login.html";
        return false;
    }
    userInfo.role = u_t;
    userInfo.user = data.student;
    userInfo.lab = data.lab;
    userInfo.admin = data.admin;
    return true;
}

// 用于响应点击浏览器的返回 按钮，稍稍控制一下权限
var execOnce = true;
onpageshow = function () {
    if (execOnce)
        execOnce = false;
    else {
        $.get(API.login_type, function (data) {
            var u_t = data.data.role;
            if (u_t === undefined || u_t === ROLE.no_user) {
                location.href = "/login.html"; return; }
            else if (u_t !== userInfo.role) {
                PermissionDenied("页面已过期，即将刷新页面", location.href); return; }
            userInfo.user = data.data.student;
            userInfo.lab = data.data.lab;
            userInfo.admin = data.data.admin;
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

/** start 为服务器返回的字符串，函数返回秒数，总是 now - start */
function secondsSince(start) {
    var a = start.split('T'); // [2018-10-08, 22:18:09.000+0000]
    var d = a[0].split('-'); // [2018, 10, 08]
    a = a[1].split('.')[0].split(':'); // [22, 18, 09];
    return (new Date().getTime() - new Date(Number(d[0]), Number(d[1])-1, Number(d[2]),
        Number(a[0]), Number(a[1]), Number(a[2])).getTime())/1000;
}

function font_formatter() {
    return "class='bigger-125'";
}

function time_formatter(cellvalue, options, rowObject) {
    return formatServerTime(rowObject.time);
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

function PermissionDenied(msg, dest) {
    if (!msg)
        msg = "权限不足，即将离开页面";
    alert(msg);
    if (dest)
        location.href = dest;
    else
        location.href = '/index.html';
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
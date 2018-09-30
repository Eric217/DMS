
var userInfo = {
    role: 0,
    user: null,
    lab: null
};

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

function gotoSDU() {
    location.href = "http://www.sdu.edu.cn";
}

function getArgsObj() {
    var str = (location.search.replace('?', ''));
    var args = str.split('&');
    var arg_obj = new Map();
    for (var i = args.length - 1; i >= 0; i--) {
        if (args[i].length > 0) {
            var arg = args[i].split('=');
            arg_obj.set(arg[0], arg[1]);
        }
    }
    return arg_obj;
}

function jump(user_type) {

    if (user_type === 1) { // normal
        location.href = "/normal/index.html";
    } else if (user_type === 2) { // lab
        location.href = "/lab/index_l.html";
    } else if (user_type === 3) { // admin
        location.href = "/admin/index_a.html";
    }

}



function check_index() {

    $.get(API.login_type, function (data) {

        var u_t = data.data.role;
        if (u_t === ROLE.no_user)
            location.href = "/login.html";

        userInfo.role = u_t;
        userInfo.user = data.data.student;
        // 用户是 role 类型，但访问的 path 不是他的，需要跳转。
        if (u_t === ROLE.normal) { // normal
            if (location.pathname.indexOf("normal") === -1)
                location.href = "/normal/index.html";
        } else if (u_t === ROLE.lab) { // lab
            if (location.pathname.indexOf("lab") === -1)
                location.href = "/lab/index_l.html";
        } else if (u_t === ROLE.admin) { // admin
            if (location.pathname.indexOf("admin") === -1)
                location.href = "/admin/index_a.html";
            userInfo.user = data.data.admin;
        }
        fillNameLabel();
    });
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



function logout() {
    $.post(API.logout, function () {
        location.href = "/login.html";
    });

}
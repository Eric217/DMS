

function gotoSDU() {
    location.href = "http://www.sdu.edu.cn";
}


function check_login() {
    $.get(API.login_type, function (data) {
        var u_t = data.data.role;
        if (u_t === ROLE.no_user)
            return;
        jump(u_t);
    });
}

function check_index() {

    function forwardIfNot(url) {
        if (location.pathname !== ('/' + url))
            location.href = url;
    }

    $.get(API.login_type, function (data) {
        var userInfo = {
            role: 0,
            user: null
        };

        var u_t = data.data.role;
        if (u_t === ROLE.no_user)
            location.href = "login.html";

        userInfo.role = u_t;
        userInfo.user = data.data.student;

        if (u_t === ROLE.normal) { // normal
            forwardIfNot("index.html");
        } else if (u_t === ROLE.lab) { // lab
            forwardIfNot("index_l.html");
        } else if (u_t === ROLE.admin) { // admin
            forwardIfNot("index_a.html");
            userInfo.user = data.data.admin;
        }
        fillNameLabel(userInfo);
    });

}



function jump(user_type) {

    if (user_type === 1) { // normal
        location.href = "index.html";
    } else if (user_type === 2) { // lab
        location.href = "index_l.html";
    } else if (user_type === 3) { // admin
        location.href = "index_a.html";
    }

}

function logout() {
    $.post(API.logout, function () {
        location.href = "login.html";
    });

}
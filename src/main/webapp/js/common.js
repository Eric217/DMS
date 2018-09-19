function gotoSDU() {
    location.href = "http://www.sdu.edu.cn";
}

function check_login(to_login) {
    $.get(API.login_type, function (data) {
        var u_t = data.data;
        if (u_t === 0) {
            if (to_login)
                location.href = "login.html";
            else
                return;
        }
        jump(u_t);
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
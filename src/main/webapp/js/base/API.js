
var BASE_URL = "http://localhost:8080";

var REQ = function (type, data, url, before, complete, success) {
    $.ajax({
        type: type,
        data: data,
        url: url,
        beforeSend: before,
        success: success,
        complete: complete
    });
};

var API = {

    // common user:
    login:          BASE_URL + "/user/login",
    image_code:     BASE_URL + "/user/login/code",
    login_type:     BASE_URL + "/user/type",
    register:       BASE_URL + "/user/register/code",
    register_code:  BASE_URL + "/user/register/mail",
    resetPass_code: BASE_URL + "/user/resetPassword/mail",
    resetPassword:  BASE_URL + "/user/resetPassword/code",
    logout:         BASE_URL + "/user/logout",

    // project
    get_my_project: BASE_URL + "/project/student",          // sid=20160030 not null
    get_sb_project: BASE_URL + "/project/student/all/admin",
    get_all_proj:   BASE_URL + "/project/all",              // page, rows, status = 0
    get_lab_proj:   BASE_URL + "/project/lab",              // page, rows, status, lab_id
    get_a_proj:     BASE_URL + "/project/one",              // id=1
    create_project: BASE_URL + "/project/create",
    del_proj_admin: BASE_URL + "/project/delete",           // ids=12@34
    del_proj_stu:   BASE_URL + "/project/delete/student",   // pid=1
    update_proj:    BASE_URL + "/project/update",

    // notification
    get_noti:       BASE_URL + "/noti/get",
    del_noti:       BASE_URL + "/noti/read",                // nid=123
    del_noti_all:   BASE_URL + "/noti/read/all",

    // bulletin
    get_bulls:      BASE_URL + "/bulletin/get",
    get_bull:       BASE_URL + "/bulletin/one",             // id=123
    del_bull:       BASE_URL + "/bulletin/delete",          // ids=12@23
    create_bull:    BASE_URL + "/bulletin/create",
    update_bull:    BASE_URL + "/bulletin/update",          // id=1
    bull_count:     BASE_URL + "/bulletin/count",

    // lab
    get_lab_names:  BASE_URL + "/lab/get/name",
    get_lab_list:   BASE_URL + "/lab/get/all",
    get_a_lab:      BASE_URL + "/lab/get",                  // id=1
    create_lab:     BASE_URL + "/lab/create",
    modify_lab:     BASE_URL + "/lab/update",
    del_lab:        BASE_URL + "/lab/delete"                // id=1


};

var ROLE = {
    no_user:    0,
    normal:     1,
    lab:        2,
    admin:      3
};


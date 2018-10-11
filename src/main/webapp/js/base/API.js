
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


    get_my_project: BASE_URL + "/project/student",          // sid=20160030 not null
    get_sb_project: BASE_URL + "/project/student/all/admin",
    get_all_proj:   BASE_URL + "/project/all",              // page, rows, status = 0
    create_project: BASE_URL + "/project/create",
    del_proj_admin: BASE_URL + "/project/delete",           // ids=12@34
    del_proj_stu:   BASE_URL + "/project/delete/student",   // pid=1

    get_noti:       BASE_URL + "/noti/get",
    del_noti:       BASE_URL + "/noti/read",                // nid=123
    del_noti_all:   BASE_URL + "/noti/read/all",

    get_bulls:      BASE_URL + "/bulletin/get",
    get_bull:       BASE_URL + "/bulletin/one",             // id=123
    del_bull:       BASE_URL + "/bulletin/delete",          // ids=12@23
    create_bull:    BASE_URL + "/bulletin/create",
    update_bull:    BASE_URL + "/bulletin/update",          // id=1
    bull_count:     BASE_URL + "/bulletin/count",

    get_lab_names:  BASE_URL + "/lab/get/name",


    get_pro_admin:  BASE_URL + "/admin/project/all"

};

var ROLE = {
    no_user:    0,
    normal:     1,
    lab:        2,
    admin:      3
};

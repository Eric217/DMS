
var BASE_URL = "http://localhost:8080";

var API = {

    // login logout register
    login:          BASE_URL + "/user/login",
    image_code:     BASE_URL + "/user/login/code",
    login_type:     BASE_URL + "/user/type",
    register:       BASE_URL + "/user/register/code",
    register_code:  BASE_URL + "/user/register/mail",
    logout:         BASE_URL + "/user/logout"
    // student

};

/** 简单的 param， k = p，否则  */
// var parseParam = function(param) {
//     var params = [];
//     $.each(param, function(i) {
//         var value = param[i];
//         if (typeof value === 'undefined')
//             value = '';
//         params.push([i, encodeURIComponent(value)].join('='));
//     });
//     return params.join('&');
// };
//
// var getURL = function (param) {
//     return BASE_URL + '?' + parseParam(param);
// }
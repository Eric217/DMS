function fillNameLabel(userInfo) {
    var l = $('#user_name_label');
    if (userInfo.role === 3) {
        l.text("管理员");
    } else {
        l.text(userInfo.user.name);
    }
}

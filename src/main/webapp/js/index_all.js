function fillNameLabel() {
    var user_name_l = $('#user_name_label');
    var right_top_lab = $('#right_top_lab');
    var right_top_p = $('#right_top_profile');
    if (userInfo.role === 3) {
        user_name_l.text("管理员");
        right_top_lab.css('display','none');
        right_top_p.css('display','none');

    } else if (userInfo.role !== 0) {
        user_name_l.text(userInfo.user.name);
        if (userInfo.role === 1) {
            right_top_lab.css('display','none');
        }
    }
}

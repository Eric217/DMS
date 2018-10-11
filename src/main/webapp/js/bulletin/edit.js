
// 用于多异步控制
var p1 = false;
var p2 = false;
var bulletin = null;
var bull_id = getOneArg(false);

/** leave with msg alert. if msg length 0, no alert. */
function leaveWithMsg(msg) {
    if (msg && msg.length > 0)
        alert(msg);
    if (bull_id.length > 0) { // 有 id 的
        location.href = '/bulletin/detail.html' + location.search;
        return;
    }
    location.href = '/index.html';
}

// 初始化页面
$.get(API.login_type, function (data) {

    if (!fillUserInfo(data.data)) return;
    if (userInfo.role === ROLE.normal) {
        leaveWithMsg("没有权限，即将离开页面"); return; }
    layoutBars();
    p1 = true;
    if (p2)
        validatePermission();
});

if (bull_id.length > 0) { // edit bull with id

    $.get(API.get_bull, {id: bull_id}, function (data) {

        if (data.status !== 200) {
            alert(data.message);
            return;
        }
        bulletin = data.data;
        $('#bull_name').val(bulletin.title);
        $('#editor1').html(bulletin.content);
        p2 = true;
        if (p1)
            validatePermission();
    });
} else { // new bull
    p2 = true;
    if (p1)
        validatePermission();
}

// 布局
function validatePermission() {
    var pass = false;
    if (userInfo.role === ROLE.admin)
        pass = true;
    else if (userInfo.role === ROLE.lab) {
        if (bulletin) {
            if (bulletin.from !== '系统公告' && bulletin.from.indexOf(userInfo.lab.name) !== -1)
                pass = true;
        } else
            pass = true;
    }
    if (!pass)
        leaveWithMsg("没有权限，即将离开页面");
}

jQuery(function($) {

    $('#post_edit_bull').on('click', function (e) {
        var t1 = $('#bull_name').val();
        var t2 = $('#editor1').html();
        if (bulletin && t1 === bulletin.title && t2 === bulletin.content) {
            leaveWithMsg(); return; }

        if (bull_id.length > 0) { // update with id
            bulletin = {
                id: bull_id,
                title: t1,
                content: t2
            };
            $.post(API.update_bull, bulletin, function (data) {
                alert(data.message);
                if (data.status === 200)
                    location.href = '/bulletin/detail.html' + location.search;
            })
        } else { // create
            bulletin = {
                title: t1,
                content: t2
            };
            $.post(API.create_bull, bulletin, function (data) {
                alert(data.message);
                if (data.status === 200)
                    location.href = '/index.html';
            })
        }
        e.preventDefault();
    });

    $('#editor1').ace_wysiwyg({
        toolbar:
            [
                'font',
                null,
                'fontSize',
                null,
                {name: 'bold', className: 'btn-info'},
                {name: 'italic', className: 'btn-info'},
                {name: 'strikethrough', className: 'btn-info'},
                {name: 'underline', className: 'btn-info'},
                null,
                {name: 'insertunorderedlist', className: 'btn-success'},
                {name: 'insertorderedlist', className: 'btn-success'},
                {name: 'outdent', className: 'btn-purple'},
                {name: 'indent', className: 'btn-purple'},
                null,
                {name: 'justifyleft', className: 'btn-primary'},
                {name: 'justifycenter', className: 'btn-primary'},
                {name: 'justifyright', className: 'btn-primary'},
                {name: 'justifyfull', className: 'btn-inverse'},
                null,
                {name: 'createLink', className: 'btn-pink'},
                {name: 'unlink', className: 'btn-pink'},
                null,
                null,
                'foreColor',
                null,
                {name: 'undo', className: 'btn-grey'},
                {name: 'redo', className: 'btn-grey'}
            ]
    }).prev().addClass('wysiwyg-style2');

});

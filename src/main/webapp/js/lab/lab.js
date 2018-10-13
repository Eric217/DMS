
function confirmDeleteLabWithOpt(lid, success) {
    MyConfirm("删除一个实验室会删除其所有项目。确定要继续吗？", function () {
        $.post(API.del_lab, {id: lid}, function (data) {
            if (data.status !== 200) {
                alert(data.message);
            } else
                success();
        });
    });

}

function confirmDeleteLab(lid) {
    confirmDeleteLabWithOpt(lid, function () {
        $('#lab_table').trigger("reloadGrid");
    });
}
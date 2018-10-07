
// 按钮响应
jQuery(function ($) {
    $('#my_noti_refresh').on('click', function () {
        location.href = "/normal/index.html";
    });
    $('#mark_all_read').on('click', function (e) {
        $.post(API.del_noti_all, function (data) {
            switch (data.status) {
                case 200:
                    alert(data.message);
                    layoutViews();
                    break;
                case 300:
                    alert(data.message);
                    break;
                case 400:
                    PermissionDenied(data.message);
                    break;
                default:
                    alert("操作失败");
            }
        });
        e.preventDefault();
    });
    $('#post_noti').on('click', function () {
        location.href = '/post_noti.html';
    });

});

// 布局
function layoutViews() {

    if (userInfo.role === ROLE.admin) {
        $('#post_noti_header').removeClass("hidden");
    } else if (userInfo.role !== ROLE.no_user) { // 已登陆的学生，含实验室负责人
        $('#my_noti_header').removeClass("hidden");
        if (userInfo.role === ROLE.lab) {
            $('#post_bulletin').removeClass("hidden");
            $('#post_noti_butt').removeClass("hidden");
        }
        $.get(API.get_noti, {sid: userInfo.user.id}, function (data) {
            var dataArr = data.data;
            if (dataArr === undefined || dataArr.length === 0) {
                $('#my_noti').text("没有通知");
                $('#mark_all_read').hide();
            } else {
                $('#noti_col').removeClass("hidden");
            }
        });
    }
}

// 下面两个函数是写在一个字符串里调用的，删除功能
// noinspection JSUnusedLocalSymbols
var delete_noti = function (rid) {

    $.post(API.del_noti, {nid: rid}, function (data) {
        switch (data.status) {
            case 200:
                alert(data.message);
                $('#noti-table').trigger("reloadGrid");
                break;
            case 300:
                alert(data.message);
                break;
            case 400:
                PermissionDenied(data.message);
                break;
            default:
                alert("操作失败");
        }
    });
};

// noinspection JSUnusedLocalSymbols
var delete_bull = function (rid) {

    $.post(API.del_bull, {ids: rid}, function (data) {
        switch (data.status) {
            case 200:
                alert(data.message);
                $('#bull_table').trigger("reloadGrid");
                break;
            case 300:
                alert(data.message);
                break;
            case 400:
                PermissionDenied(data.message);
                break;
            default:
                alert("操作失败");
        }
    });
};

// 初始化页面
$.get(API.login_type, function (data) {

    fillUserInfo(data.data);
    layoutBars();
    layoutViews();

    var font_formatter = function () {
        return "class='bigger-125'";
    };

    jQuery(function ($) {
        if (userInfo.role === ROLE.admin)
            return;
        var grid_selector = "#noti-table";
        var pager_selector = "#noti-pager";

        var parent_column = $(grid_selector).closest('[class*="col-"]'); // .closest('.parent')
        //resize to fit page size
        $(window).on('resize.jqGrid', function () {
            $(grid_selector).jqGrid('setGridWidth', parent_column.width());
        });

        //resize on sidebar collapse/expand
        $(document).on('settings.ace.jqGrid', function (ev, event_name) {
            if (event_name === 'sidebar_collapsed' || event_name === 'main_container_fixed') {
                //setTimeout is for webkit only to give time for DOM changes and then redraw!!!
                setTimeout(function () {
                    $(grid_selector).jqGrid('setGridWidth', parent_column.width());
                }, 20);
            }
        });

        var noti_inner_div = function (cellValue, options, rowObject) {
            var rid = rowObject.id;
            return "<span>&nbsp;&nbsp;</span>" +
                "<i class='ace-icon fa fa-trash-o red bigger-110' " +
                "style='cursor: pointer' " +
                "onclick='delete_noti(" + rid + ")'></i>";
        };

        jQuery(grid_selector).jqGrid({

            url: API.get_noti,
            postData: {sid: userInfo.user.id},
            datatype: "json",
            height: 'auto',
            colNames: ['类型', '内容', '时间', '来自', '操作'],
            colModel: [
                {
                    name: 'type', index: 'type', width: 70, sorttype: "string", cellattr:
                    font_formatter
                },
                {
                    name: 'content',
                    index: 'content',
                    width: 200,
                    sortable: false,
                    cellattr:
                    font_formatter
                },
                {
                    name: 'time', index: 'time', sorttype: "date", width: 66,
                    formatter: getFormattedServerTime, cellattr:
                    font_formatter
                },
                {
                    name: 'from', index: 'from', width: 64, sorttype: "string", cellattr:
                    font_formatter
                },
                {
                    name: 'id', index: 'id', width: 45, sortable: false,
                    formatter: noti_inner_div, cellattr: font_formatter
                }
            ],

            viewrecords: true,
            rowNum: 10,
            rowList: [10, 20, 30],
            pager: pager_selector,
            altRows: true,
            selectable: false,

            loadComplete: function () {
                var table = this;
                setTimeout(function () {
                    updatePagerIcons();
                    enableTooltips(table);
                }, 0);
                if (0 === $('#noti-table').jqGrid('getGridParam', 'records'))
                    layoutViews();
            },

            jsonReader: {
                root: 'data',
            }
        });

        $(window).triggerHandler('resize.jqGrid');

        //navButtons
        jQuery(grid_selector).jqGrid('navGrid', pager_selector,
            { 	//navbar options
                add: false,
                del: false,
                edit: false,
                search: false,
                // searchicon: 'ace-icon fa fa-search orange',
                refresh: true,
                refreshicon: 'ace-icon fa fa-refresh green',
            },
            {   //view record form
                recreateForm: true,
                beforeShowForm: function (e) {
                    var form = $(e[0]);
                    form.closest('.ui-jqdialog').find('.ui-jqdialog-title').wrap('<div class="widget-header" />')
                }
            }
        );

        function updatePagerIcons() {
            var replacement =
                {
                    'ui-icon-seek-first': 'ace-icon fa fa-angle-double-left bigger-140',
                    'ui-icon-seek-prev': 'ace-icon fa fa-angle-left bigger-140',
                    'ui-icon-seek-next': 'ace-icon fa fa-angle-right bigger-140',
                    'ui-icon-seek-end': 'ace-icon fa fa-angle-double-right bigger-140'
                };
            $('.ui-pg-table:not(.navtable) > tbody > tr > .ui-pg-button > .ui-icon').each(function () {
                var icon = $(this);
                var $class = $.trim(icon.attr('class').replace('ui-icon', ''));

                if ($class in replacement) icon.attr('class', 'ui-icon ' + replacement[$class]);
            })
        }

        function enableTooltips(table) {
            $('.navtable .ui-pg-button').tooltip({container: 'body'});
            $(table).find('.ui-pg-div').tooltip({container: 'body'});
        }

        $(document).one('ajaxloadstart.page', function () {
            $.jgrid.gridDestroy(grid_selector);
            $('.ui-jqdialog').remove();
        });
    });

    jQuery(function ($) {
        var grid_selector = "#bull_table";
        var pager_selector = "#bull_pager";

        var parent_column = $(grid_selector).closest('[class*="col-"]'); // .closest('.parent')
        //resize to fit page size
        $(window).on('resize.jqGrid', function () {
            $(grid_selector).jqGrid('setGridWidth', parent_column.width());
        });
        $(document).on('settings.ace.jqGrid', function (ev, event_name) {
            if (event_name === 'sidebar_collapsed' || event_name === 'main_container_fixed') {
                //setTimeout is for webkit only to give time for DOM changes and then redraw!!!
                setTimeout(function () {
                    $(grid_selector).jqGrid('setGridWidth', parent_column.width());
                }, 20);
            }
        });

        var bull_inner_div = function (cellValue, options, rowObject) {

            var rid = rowObject.id;
            var code = "/normal/viewBulletin.html" + "?" + rid;
            if (userInfo.role === ROLE.admin)
                return "<span>&nbsp;</span>" +
                    "<a href=\"" + code + "\">查看详情</a>" +
                    "<span>&nbsp;&nbsp;&nbsp;</span>" +
                    "<i class='ace-icon fa fa-trash-o red bigger-110' " +
                    "style='cursor: pointer' " +
                    "onclick='delete_bull(" + rid + ")'></i>";
            return "<span>&nbsp;</span><a href=\"" + code + "\">查看详情</a>";
        };

        jQuery(grid_selector).jqGrid({

            url: API.get_bull,
            postData: {page: 1, rows: 100000},
            datatype: "json",
            height: 'auto',
            colNames: ['来自', '标题', '时间', '操作'],
            colModel: [
                {
                    name: 'from', index: 'from', width: 58, sortable: false, cellattr:
                    font_formatter
                },
                {
                    name: 'title',
                    index: 'title',
                    width: 200,
                    sortable: false,
                    cellattr: font_formatter
                },
                {
                    name: 'time',
                    index: 'time',
                    sorttype: "date",
                    width: 60,
                    cellattr: font_formatter,
                    formatter: getFormattedServerTime
                },
                {
                    name: 'id', index: 'id', width: 45, sortable: false,
                    formatter: bull_inner_div, cellattr: font_formatter
                }],

            viewrecords: true,
            rowNum: 10,
            rowList: [10, 20, 30],
            pager: pager_selector,
            altRows: true,
            selectable: false,
            multiselect: userInfo.role === ROLE.admin,
            loadComplete: function () {
                var table = this;
                setTimeout(function () {
                    updatePagerIcons();
                    enableTooltips(table);
                }, 0);
            },

            jsonReader: {
                root: 'data',
            }

        });
        $(window).triggerHandler('resize.jqGrid');


        var deleteRows = function (rowid) {
            // if (ro)
            alert("delete bulls multi :" + rowid);
        };

        //navButtons
        jQuery(grid_selector).jqGrid('navGrid', pager_selector,
            { 	//navbar options
                add: false,
                edit: false,
                del: userInfo.role === ROLE.admin,
                delfunc: deleteRows,
                search: false,
                refresh: true,
                refreshicon: 'ace-icon fa fa-refresh green',
            },
            {
                //view record form
                recreateForm: true,
                beforeShowForm: function (e) {
                    var form = $(e[0]);
                    form.closest('.ui-jqdialog').find('.ui-jqdialog-title').wrap('<div class="widget-header" />')
                }
            }
        );

        function updatePagerIcons() {
            var replacement = {
                'ui-icon-seek-first': 'ace-icon fa fa-angle-double-left bigger-140',
                'ui-icon-seek-prev': 'ace-icon fa fa-angle-left bigger-140',
                'ui-icon-seek-next': 'ace-icon fa fa-angle-right bigger-140',
                'ui-icon-seek-end': 'ace-icon fa fa-angle-double-right bigger-140'
            };
            $('.ui-pg-table:not(.navtable) > tbody > tr > .ui-pg-button > .ui-icon').each(function () {
                var icon = $(this);
                var $class = $.trim(icon.attr('class').replace('ui-icon', ''));

                if ($class in replacement) icon.attr('class', 'ui-icon ' + replacement[$class]);
            })
        }

        function enableTooltips(table) {
            $('.navtable .ui-pg-button').tooltip({container: 'body'});
            $(table).find('.ui-pg-div').tooltip({container: 'body'});
        }

        $(document).one('ajaxloadstart.page', function () {
            $.jgrid.gridDestroy(grid_selector);
            $('.ui-jqdialog').remove();
        });
    });

});


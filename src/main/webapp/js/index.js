
// 初始化页面
$.get(API.login_type, function (data) {

    fillUserInfo(data.data);
    layoutBars();
    layoutBasicViews();

    var font_formatter = function () {
        return "class='bigger-125'";
    };

    var date_formatter = function(cellvalue, options, rowObject) {
        return formatServerTime(rowObject.time);
    };

    // 加载 bull
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
            var code = "/bulletin/detail.html" + "?" + rid;
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

            url: API.get_bulls,
            postData: {page: 1, rows: 100000},
            datatype: "json",
            height: 'auto',
            colNames: ['来自', '标题', '时间', '操作'],
            colModel: [
                {
                    name: 'from', index: 'from', width: 60, cellattr:
                    font_formatter, sortable: false
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
                    width: 64,
                    cellattr: font_formatter,
                    formatter: date_formatter,
                    sortable: false
                },
                {
                    name: 'id', index: 'id', width: 52, formatter: bull_inner_div,
                    cellattr: font_formatter, sortable: false
                }],

            viewrecords: true,
            rowNum: 100000,
            //rowList: [10, 20, 30],
            pager: pager_selector,
            altRows: true,
            selectable: false,
            sortable: false,
            // loadonce : true,
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
            if (rowid.length === 0)
                return;
            $.post(API.del_bull, {ids: rowid.join('@')}, function (data) {
                alert(data.message);
                $('#bull_table').trigger("reloadGrid");
            });
        };

        //navButtons
        // noinspection JSUnusedGlobalSymbols
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

    // 加载 noti
    jQuery(function ($) {
        if (userInfo.role === ROLE.admin)
            return;
        var noti_table = $('#noti_table');

        var grid_selector2 = "#noti_table";
        var pager_selector2 = "#noti_pager";

        var parent_column = noti_table.closest('[class*="col-"]');
        $(window).on('resize.jqGrid', function () {
            noti_table.jqGrid('setGridWidth', parent_column.width());
        });
        $(document).on('settings.ace.jqGrid', function (ev, event_name) {
            if (event_name === 'sidebar_collapsed' || event_name === 'main_container_fixed') {
                setTimeout(function () {
                    noti_table.jqGrid('setGridWidth', parent_column.width());
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

        noti_table.jqGrid({

            url: API.get_noti,
            postData: {sid: userInfo.user.id},
            datatype: "json",
            height: 'auto',
            colNames: ['类型', '内容', '时间', '来自', '操作'],
            colModel: [
                {
                    name: 'type', index: 'type', width: 50, sortable: false,
                    cellattr: font_formatter
                },
                {
                    name: 'content',
                    index: 'content',
                    width: 200,
                    sortable: false,
                    cellattr: font_formatter
                },
                {
                    name: 'time', index: 'time', width: 70, sortable: false,
                    formatter: date_formatter, cellattr: font_formatter
                },
                {
                    name: 'from', index: 'from', width: 58, sortable: false,
                    cellattr: font_formatter
                },
                {
                    name: 'id', index: 'id', width: 25, formatter: noti_inner_div,
                    cellattr: font_formatter, sortable: false
                }
            ],

            viewrecords: true,
            rowNum: 100000,
            //rowList: [10, 20, 30],
            pager: pager_selector2,
            altRows: true,
            selectable: false,
            sortable: false,

            loadComplete: function () {
                var table = this;
                setTimeout(function () {
                    updatePagerIcons();
                    enableTooltips(table);
                }, 0);
                layoutNotiCol(noti_table.jqGrid('getGridParam', 'records'));
            },

            jsonReader: {
                root: 'data',

            }


        });
        $(window).triggerHandler('resize.jqGrid');


        // noinspection JSUnusedGlobalSymbols
        noti_table.jqGrid('navGrid', pager_selector2,
            { 	//navbar options
                add: false,
                del: false,
                edit: false,
                search: false,
                // searchicon: 'ace-icon fa fa-search orange',
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
            $.jgrid.gridDestroy(grid_selector2);
            $('.ui-jqdialog').remove();
        });
    });

});

// 布局
function layoutBasicViews() {

    if (userInfo.role === ROLE.admin) {
        $('#post_noti_header').removeClass("hidden");
        $('#noti_col').addClass("hidden");
    } else if (userInfo.role !== ROLE.no_user) { // 已登陆的学生，含实验室负责人
        $('#my_noti_header').removeClass("hidden");
        if (userInfo.role === ROLE.lab) {
            $('#post_bulletin').removeClass("hidden");
            $('#post_noti_butt').removeClass("hidden");
        }
    }
}
// 点击 grid 自带的刷新按钮，只会把自己刷没，然后只能通过自定义的刷新按钮刷新整个界面。
function layoutNotiCol(records) {
    if (records === 0) {
        $('#my_noti').text("没有通知");
        $('#mark_all_read').hide();
        $('#noti_col').hide();
    }
}

// 按钮响应
jQuery(function ($) {
    $('#my_noti_refresh').on('click', function () {
        location.href = "/index.html"
    });
    $('#mark_all_read').on('click', function (e) {
        $.post(API.del_noti_all, function (data) {
            reactToResponse(data, function () {
               layoutNotiCol(0);
            });
        });
        e.preventDefault();
    });
    $('#post_noti').on('click', function () {
        location.href = '/notification/post.html';
    });
    $('#post_bulletin').on('click', function () {
        location.href = '/bulletin/edit.html';
    });
});

// 下面两个函数是写在一个字符串里调用的，删除功能
// noinspection JSUnusedLocalSymbols
var delete_noti = function (rid) {
    $.post(API.del_noti, {nid: rid}, function (data) {
        reactToResponse(data, function () {
            $('#noti_table').trigger("reloadGrid");
        });
    });
};

// noinspection JSUnusedLocalSymbols
var delete_bull = function (rid) {
    $.post(API.del_bull, {ids: rid}, function (data) {
        reactToResponse(data, function () {
            $('#bull_table').trigger("reloadGrid");
        });
    });
};
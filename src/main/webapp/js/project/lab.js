// ?id=1&status=0
var arg_map = getArgsObj();
var lab_id = arg_map.get('id');
var lab_status = arg_map.get('status');
if (lab_status === undefined || lab_status.length === 0)
    lab_status = 0;

$.get(API.login_type, function (data) {

    if (!fillUserInfo(data.data)) return;

    layoutBars();

    if (userInfo.role === ROLE.normal) {
        PermissionDenied("没有权限，即将跳转", '/project/my.html'); return; }
    else if (userInfo.role === ROLE.lab) {
        if (lab_id === undefined || lab_id.length === 0)
            lab_id = userInfo.lab.id;
        else if ('' + userInfo.lab.id !== lab_id) {
            PermissionDenied("没有权限，即将跳转", '/project/my.html'); return; }
    } else if (userInfo.role === ROLE.admin) {
        if (lab_id === undefined || lab_id.length === 0) {
            PermissionDenied("参数有误", '/project/all.html'); return; }
    }

    $('#bread_lab_li').attr('href', '/lab/detail.html?' + lab_id);

    if (lab_status === '0' || lab_status === 0) {
        $('[data-toggle="buttons"] label').eq(0).addClass('active');
        $('#lab_p_s').text("全部项目");
    }
    else if (lab_status === '1' || lab_status === 1) {
        $('[data-toggle="buttons"] label').eq(1).addClass('active');
        $('#lab_p_s').text("待审核项目");
    }
    else if (lab_status === '7' || lab_status === 7) {
        $('[data-toggle="buttons"] label').eq(2).addClass('active');
        $('#lab_p_s').text("待处理请求");
    }
    jQuery(function ($) {

        var proj_table = $('#lab_proj_tbl');
        var proj_selector = "#lab_proj_tbl";
        var pager_selector = "#lab_proj_pg";

        $('[data-toggle="buttons"] .btn').on('click', function(){
            var s = $(this).find('input[type=radio]').val();
            if (s === '0') {
                $('#lab_p_s').text("全部项目");
            } else if (s === '1') {
                $('#lab_p_s').text("待审核项目");
            } else if (s === '7' || lab_status === 7) {
                $('#lab_p_s').text("待处理请求");
            }
            proj_table.jqGrid('setGridParam', {
                postData: {page: 1, rows: 100000, status: s, lab_id: lab_id}
            }).trigger("reloadGrid");
        });

        var parent_column = proj_table.closest('[class*="col-"]');
        $(window).on('resize.jqGrid', function () {
            proj_table.jqGrid('setGridWidth', parent_column.width());
        });
        $(document).on('settings.ace.jqGrid', function (ev, event_name) {
            if (event_name === 'sidebar_collapsed' || event_name === 'main_container_fixed') {
                setTimeout(function () {
                    proj_table.jqGrid('setGridWidth', parent_column.width());
                }, 20);
            }
        });

        // noinspection JSUnusedLocalSymbols
        var delete_proj = function (rid) {
            MyConfirm('删除操作不可撤销，确定删除吗?', function () {
                $.post(API.del_proj_admin, {ids: rid}, function (data) {
                    reactToResponse(data, function () {
                        proj_table.trigger("reloadGrid");
                    });
                });
            });

        };

        var deleteRows = function (rowid) {
            if (rowid.length === 0)
                return;
            delete_proj(rowid.join('@'));
        };

        var proj_in_div = function (cellValue, options, rowObject) {

            var rid = rowObject.id;
            var code = "/project/detail.html" + "?" + rid;
            return "<span>&nbsp;</span><a href=\"" + code + "\">查看详情</a>" +
                "<span>&nbsp;&nbsp;&nbsp;</span>" +
                "<a href='/project/edit.html?" + rowObject.id + "'>" +
                "<i class='ace-icon fa fa-pencil green bigger-110'></i></a>" +
                "<span>&nbsp;&nbsp;&nbsp;</span>" +
                "<i class='ace-icon fa fa-trash-o red bigger-110' style='cursor: pointer'"
                + " onclick='delete_proj(" + rid + ")'></i>";
        };

        var title1 = $('#lab_name');

        proj_table.jqGrid({

            url: API.get_lab_proj,
            postData: {page: 1, rows: 100000, status: lab_status, lab_id: lab_id},
            datatype: "json",
            height: 'auto',
            colNames: ['所在实验室', '项目名称', '提交时间', '指导教师', '队长姓名', '项目状态', '操作'],
            colModel: [
                {
                    name: 'lab_name',
                    index: 'lab_name',
                    width: 51,
                    sortable: false,
                    cellattr: font_formatter,
                    formatter: function (cellvalue, options, rowObject) {
                        title1.text(rowObject.lab_name);
                        return rowObject.lab_name;
                    }
                },
                {
                    name: 'name', index: 'name', width: 190, sortable: false,
                    cellattr: font_formatter
                },
                {
                    name: 'submit_time', index: 'submit_time', width: 69, sortable: false,
                    cellattr: font_formatter, formatter: submit_time_formatter
                },
                {
                    name: 'coach_id', index: 'coach_id', width: 38, sortable: false,
                    cellattr: font_formatter, formatter: coach_formatter
                },

                {
                    name: 'leader_id', index: 'leader_id', width: 40,
                    cellattr: font_formatter, sortable: false,
                    formatter: leader_formatter
                },
                {
                    name: 'opt_status', index: 'opt_status', width: 36,
                    cellattr: font_formatter, sortable: false,
                    formatter: status_formatter
                },
                {
                    name: 'id', index: 'id', width: 68,
                    formatter: proj_in_div, sortable: false,
                    cellattr: function() { return "class='bigger-115'"; }
                }
            ],

            viewrecords: true,
            rowNum: 100000,
            //rowList: [10, 20, 30],
            pager: pager_selector,
            altRows: true,
            selectable: false,
            sortable: false,
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

        // noinspection JSUnusedGlobalSymbols
        proj_table.jqGrid('navGrid', pager_selector,
            { 	//navbar options
                add: false,
                del: userInfo.role === ROLE.admin,
                delfunc: deleteRows,
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
            $.jgrid.gridDestroy(proj_selector);
            $('.ui-jqdialog').remove();
        });
    });


});

function lab_name_on_click() {
    location.href = '/lab/detail.html?' + lab_id;
}
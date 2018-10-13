
// noinspection JSUnusedLocalSymbols
var delete_proj = function (id) {
    $.post(API.del_proj_stu, {pid: id}, function (data) {
        reactToResponse(data, function () {
            $('#my_proj_tbl').trigger("reloadGrid");
        });
    });
};

$.get(API.login_type, function (data) {

    if (!fillUserInfo(data.data)) return;
    layoutBars();

    jQuery(function ($) {

        if (userInfo.role === ROLE.admin) {
            PermissionDenied("管理员没有项目，即将跳转", '/project/all.html'); return; }

        var proj_table = $('#my_proj_tbl');

        var grid_selector2 = "#my_proj_tbl";
        var pager_selector2 = "#my_proj_pg";


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

        $('#create_proj').on('click', function () {
            location.href = '/project/create.html';
        });

        var proj_in_div = function (cellValue, options, rowObject) {
            var code = "/project/detail.html" + "?" + rowObject.id;
            var detail = "<span>&nbsp;</span><a href=\"" + code + "\">查看详情</a>";

            if (rowObject.leader_id !== userInfo.user.id)
                return detail;

            var p_status = getProjStatus(rowObject);
            if (p_status === ProjectStatus.checking)
                return detail +
                    "<span>&nbsp;&nbsp;&nbsp;</span>" +
                    "<span style='cursor:pointer;' class='red' " +
                    "onclick='delete_proj(" + rowObject.id + ")'>撤回</span>";

            if (p_status === ProjectStatus.processing) {
                return detail +
                    "<span>&nbsp;&nbsp;&nbsp;</span>" +
                    "<a href='/project/edit.html?" + rowObject.id + "'>" +
                    "<i class='ace-icon fa fa-pencil blue bigger-110'></i></a>";
            }

            if (p_status === ProjectStatus.rejected || p_status===ProjectStatus.canceled
                || p_status === ProjectStatus.finished || p_status === ProjectStatus.overtime)
                return detail +
                    "<span>&nbsp;&nbsp;&nbsp;</span>" +
                    "<i class='ace-icon fa fa-trash-o red bigger-110' " +
                    "style='cursor: pointer' " +
                    "onclick='delete_proj(" + rowObject.id + ")'></i>";

            // 可以在这里为 剩下3种状态指定操作按钮，
            // if (proj_(rowObject)) {
            //     var modi_code = '/project/edit.html' + '?' + rowObject.id;
            //     var to_modi = "<span>&nbsp;</span><a href=\"" + modi_code + "\">修改</a>";
            // }
            return detail;
            // return "<span>&nbsp;</span><a href=\"" + code + "\">查看详情</a>";
        };

        function layoutViews(records) {
            if (records === 0) {
                $('#my_proj').text('没有项目');
                $('#my_proj_d_r').addClass("hidden");
            } else {
                $('#my_proj').text('我的项目');
                $('#my_proj_d_r').removeClass('hidden');
            }
        }

        proj_table.jqGrid({

            url: API.get_my_project,
            postData: {sid: userInfo.user.id},
            datatype: "json",
            height: 'auto',
            colNames: ['所在实验室', '项目名称', '提交时间', '指导教师', '队长姓名', '项目状态', '操作'],
            colModel: [
                {
                    name: 'lab_name',
                    index: 'lab_name',
                    width: 51,
                    sortable: false,
                    cellattr: font_formatter
                },
                {
                    name: 'name', index: 'name', width: 200, sortable: false,
                    cellattr: font_formatter
                },
                {
                    name: 'submit_time', index: 'submit_time', width: 61, sortable: false,
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
                    name: 'id', index: 'id', width: 58,
                    formatter: proj_in_div, sortable: false,
                    cellattr: function() { return "class='bigger-115'"; }
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
                layoutViews(proj_table.jqGrid('getGridParam', 'records'));
            },

            jsonReader: {
                root: 'data',
            }
        });
        $(window).triggerHandler('resize.jqGrid');


        // noinspection JSUnusedGlobalSymbols
        proj_table.jqGrid('navGrid', pager_selector2,
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


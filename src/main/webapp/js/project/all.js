
$.get(API.login_type, function (data) {

    if (!fillUserInfo(data.data)) return;

    layoutBars();

    if (userInfo.role !== ROLE.admin) {
        PermissionDenied("没有权限，即将跳转", '/project/my.html'); return; }

    jQuery(function ($) {

        var proj_table = $('#all_proj_tbl');
        var proj_selector = "#all_proj_tbl";
        var pager_selector = "#all_proj_pg";

        $('[data-toggle="buttons"] .btn').on('click', function(){
            var s = $(this).find('input[type=radio]').val();
            proj_table.jqGrid('setGridParam', {
                postData: {page: 1, rows: 100000, status: s}
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
            $.post(API.del_proj_admin, {ids: rid}, function (data) {
                reactToResponse(data, function () {
                    proj_table.trigger("reloadGrid");
                });
            });
        };

        var deleteRows = function (rowid) {
            if (rowid.length === 0)
                return;
            $.post(API.del_bull, {ids: rowid.join('@')}, function (data) {
                alert(data.message);
                proj_table.trigger("reloadGrid");
            });
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

        proj_table.jqGrid({

            url: API.get_all_proj,
            postData: {page: 1, rows: 100000, status: 0},
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
            multiselect: true,

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
                del: true,
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

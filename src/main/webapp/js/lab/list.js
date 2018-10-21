
$.get(API.login_type, function (data) {

    if (!fillUserInfo(data.data)) return;
    layoutBars();

    jQuery(function ($) {

        var lab_table = $('#lab_table');

        var table_selector = "#lab_table";
        var pager_selector = "#lab_pager";

        var parent_column = lab_table.closest('[class*="col-"]');
        $(window).on('resize.jqGrid', function () {
            lab_table.jqGrid('setGridWidth', parent_column.width());
        });
        $(document).on('settings.ace.jqGrid', function (ev, event_name) {
            if (event_name === 'sidebar_collapsed' || event_name === 'main_container_fixed') {
                setTimeout(function () {
                    lab_table.jqGrid('setGridWidth', parent_column.width());
                }, 20);
            }
        });
        $('#create_lab').on('click', function () {
            location.href = '/lab/create.html';
        });
        var view_in_div = function (cellValue, options, rowObject) {
            var code = "/lab/detail.html" + "?" + rowObject.id;
            var detail = "<span>&nbsp;</span><a href=\"" + code + "\">查看详情</a>";

            if (userInfo.role === ROLE.admin) {
                return detail +
                    "<span>&nbsp;&nbsp;&nbsp;</span>" +
                    "<a href='/lab/edit.html?" + rowObject.id + "'>" +
                    "<i class='ace-icon fa fa-pencil green bigger-110'></i></a>" +
                    "<span>&nbsp;&nbsp;&nbsp;</span>" +
                    "<i class='ace-icon fa fa-trash-o red bigger-110' " +
                    "style='cursor: pointer' " +
                    "onclick='confirmDeleteLab(" + rowObject.id + ")'></i>";
            }

            if (userInfo.role === ROLE.lab && userInfo.lab.id === rowObject.id)
                return detail +
                    "<span>&nbsp;&nbsp;&nbsp;</span>" +
                    "<a href='/lab/edit.html?" + rowObject.id + "'>" +
                    "<i class='ace-icon fa fa-pencil green bigger-110'></i></a>";

            return detail;
        };

        var leader_formatter2 = function (cellValue, options, rowObject) {
            // noinspection JSUnresolvedVariable
            var l = rowObject.leader;
            if (l && l.name && l.name.length > 0)
                return l.name;
            return "无";
        };

        lab_table.jqGrid({

            url: API.get_lab_list,
            datatype: "json",
            height: 'auto',
            colNames: ['名称', '教室', '创建时间', '负责人', '操作'],
            colModel: [
                {
                    name: 'name', index: 'name', width: 65, sortable: false,
                    cellattr: font_formatter
                },
                {
                    name: 'classroom', index: 'classroom', width: 40, sortable: false,
                    cellattr: font_formatter
                },
                {
                    name: 'create_time', index: 'create_time', width: 49, sortable: false,
                    cellattr: font_formatter
                },
                {
                    name: 'leader_id', index: 'leader_id', width: 38,
                    cellattr: font_formatter, sortable: false,
                    formatter: leader_formatter2
                },
                {
                    name: 'id', index: 'id', width: 58,
                    formatter: view_in_div, sortable: false,
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
        lab_table.jqGrid('navGrid', pager_selector,
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
            $.jgrid.gridDestroy(table_selector);
            $('.ui-jqdialog').remove();
        });
    });


});
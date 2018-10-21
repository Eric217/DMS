
var coach_formatter = function(cellValue, options, rowObject) {
    if (rowObject.coach_id.length > 0)
        return rowObject.coach_id;
    return '无';
};

var leader_formatter = function (cellValue, options, rowObject) {
    // noinspection JSUnresolvedVariable
    var arr = rowObject.members;
    for (var idx in arr) {
        // noinspection JSUnfilteredForInLoop
        if (arr[idx].id === rowObject.leader_id)
            // noinspection JSUnfilteredForInLoop
            return arr[idx].name;
    }
    return rowObject.leader_id;
};



var status_formatter = function (cellValue, options, rowObject) {
    return getProjStatus(rowObject);
};

function submit_time_formatter(cellvalue, options, rowObject) {
    return formatServerTime(rowObject.submit_time);
}


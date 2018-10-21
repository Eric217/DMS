
function MyConfirm(prompt, operation) {
    bootbox.confirm(prompt, function(result) {
        if (result) {
            operation();
        }
    });
}

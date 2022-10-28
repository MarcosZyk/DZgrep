//全局请求方法 包括 一些公共方法
function getRequest(url, onSuccess, onError) {
    $.ajax({
        type: 'GET',
        url: url,
        async: true,
        success: onSuccess,
        error: onError
    });
}

function getSyncRequest(url, onSuccess, onError) {
    $.ajax({
        type: 'GET',
        url: url,
        async: false,
        success: onSuccess,
        error: onError
    });
}

function postRequest(url, data, onSuccess, onError) {
    $.ajax({
        type: 'POST',
        url: url,
        async: true,
        data: JSON.stringify(data),
        contentType: 'application/json',
        processData: false,
        success: onSuccess,
        error: onError
    });
}


function directToArticlePage(element) {
    aid = $(element).attr('data-aid');
    window.location.href = '/article?aid=' + aid;
}


function directToUserPage(element) {
    uid = $(element).attr('data-uid');
    window.location.href = '/user?uid=' + uid;
}
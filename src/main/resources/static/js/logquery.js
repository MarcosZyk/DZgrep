let queryNameGenerator = 1;

let queryId = '';
let logQueryResultServerList = [];
let lineCount = 0;
let logData = {};

let selectedLog = {
    lineIndex: 0,
    columnIndex: 0,
}

function queryLog() {
    let activeServer = []
    serverList.forEach(
        function (server) {
            activeServer.push(server.ip);
        }
    );

    alert("Start executing log query");

    logQueryResultServerList = [];
    lineCount = 0;

    postRequest(
        '/log/query',
        {
            queryName: queryNameGenerator++,
            startTime: $("#query-start-time")[0].value,
            endTime: $("#query-end-time")[0].value,
            keyword: $("#query-keyword")[0].value,
            serverIpList: activeServer
        },
        function (res) {
            let logTable = $("#log-table");
            queryId = res.queryId;
            logQueryResultServerList = res.serverList;
            logData = res.logList;
            renderLogTableHeader(res.serverList, logTable);
            res.logList.forEach(
                function (lineData) {
                    renderLogTableLine(res.serverList, lineData, logTable);
                }
            );
            alert("Finish executing log query");
        },
        function (error) {
            alert(error);
        }
    );
}

function renderLogTableHeader(serverList, logTable) {
    let ui = '<div class="row">\n';
    ui += '                <div class="col">\n' +
        '\n' +
        '                </div>\n';
    serverList.forEach(
        function (serverInfo) {
            ui += '                <div class="col">\n' +
                '                    <div class="card text-white bg-dark mb-3" style="max-width: 18rem;">\n' +
                '                        <div class="card-body">\n' +
                '                            <h5 class="card-title">' + serverInfo + '</h5>\n' +
                '                        </div>\n' +
                '                    </div>\n' +
                '                </div>\n';
        }
    );
    ui += '            </div>';
    logTable.append(ui);
}

function renderLogTableLine(serverList, lineData, logTable) {
    let ui = '<div class="row">\n';

    ui += '                <div class="col">\n' +
        '                    <div class="card text-white bg-primary mb-3" style="max-width: 18rem;">\n' +
        '                        <div class="card-body">\n' +
        '                            <h5 class="card-title">' + lineData.time + '</h5>\n' +
        '                        </div>\n' +
        '                    </div>\n' +
        '                </div>\n';
    let columnIndex = 0
    serverList.forEach(
        function (server) {
            ui += '                <div class="col">\n';
            if (lineData.serverLogs[server] !== undefined) {
                let log = lineData.serverLogs[server];
                ui += '                    <div id=' + 'log-' + lineCount + '-' + columnIndex + ' ' +
                    'class="card ' + getLogCardStyle(log.type) + ' mb-3" ' +
                    'style="max-width: 18rem;" ' +
                    'onclick="showLogInfo(this)" ' +
                    'data-bs-toggle="modal" data-bs-target="#log-detail-modal">\n' +
                    '                        <div class="card-body">\n' +
                    '                            <p class="card-text">' + log.content + '</p>\n' +
                    '                        </div>\n' +
                    '                    </div>\n';
            }

            ui += '                </div>\n';
            columnIndex++;
        }
    );
    ui += '            </div>';
    lineCount++;
    logTable.append(ui);
}

function getLogCardStyle(type) {
    if (type === 'ERROR') {
        return 'text-white bg-danger';
    } else if (type === 'WARN') {
        return 'text-dark bg-warning';
    } else if (type === 'DEBUG') {
        return 'text-white bg-secondary';
    } else {
        return 'text-dark bg-light';
    }
}

function showLogInfo(logUi) {
    let strs = logUi.id.split('-');
    let lineIndex = strs[1];
    let columnIndex = strs[2];

    selectedLog.lineIndex = lineIndex;
    selectedLog.columnIndex = columnIndex;

    let body = $("#log-detail-modal-body");

    body.empty();
    body.append(generateLogDetailModal(lineIndex, columnIndex));
}

function generateLogDetailModal(lineIndex, columnIndex) {
    let logInfo = logData[lineIndex].serverLogs[logQueryResultServerList[columnIndex]];
    let ui = '<div>';

    ui += '<b>server: </b> ' + logQueryResultServerList[columnIndex] + '<br/>';
    ui += '<b>time: </b> ' + logData[lineIndex].time + '<br/>';
    ui += '<b>thread: </b> ' + logInfo.thread + '<br/>';
    ui += '<b>type: </b> ' + logInfo.type + '<br/>';
    ui += '<b>source: </b> ' + logInfo.source + '<br/>';
    ui += '<b>content: </b> ' + logInfo.content + '<br/>';

    ui += '<hr/>'

    ui += '<div id="log-context"><button type="button" class="btn btn-primary btn-lg" onclick="queryLogContext()">get context</button></div>'

    ui += '</div>';
    return ui;
}

function queryLogContext() {
    postRequest(
        '/log/context',
        {
            queryId: queryId,
            targetServer: logQueryResultServerList[selectedLog.columnIndex],
            index: logData[selectedLog.lineIndex].serverLogs[logQueryResultServerList[selectedLog.columnIndex]].index,
        },
        function (res) {
            let logContextUI = $("#log-context");
            logContextUI.empty();
            logContextUI.append('<p>' + res.replaceAll('\n', '<br/>') + '</p>');
        },
        function (error) {
            alert(error);
        }
    )
}
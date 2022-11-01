let queryNameGenerator = 1;

function queryLog() {
    let activeServer = []
    serverList.forEach(
        function (server) {
            activeServer.push(server.ip);
        }
    );

    alert("Start executing log query");

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
    serverList.forEach(
        function (server) {
            ui += '                <div class="col">\n';
            if (lineData.serverLogs[server] !== undefined) {
                let log = lineData.serverLogs[server];
                ui += '                    <div class="card ' + getLogCardStyle(log.type) + ' mb-3" style="max-width: 18rem;">\n' +
                    '                        <div class="card-body">\n' +
                    '                            <p class="card-text">' + log.content + '</p>\n' +
                    '                        </div>\n' +
                    '                    </div>\n';
            }

            ui += '                </div>\n';
        }
    );
    ui += '            </div>';
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
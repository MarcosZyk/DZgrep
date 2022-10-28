let queryNameGenerator = 1;
function queryLog(){
    let activeServer = []
    serverList.forEach(
        function (server){
            activeServer.push(server.ip);
        }
    );

    alert("Start executing log query");

    postRequest(
        '/log/query',
        {
            queryName: queryNameGenerator++,
            startTime:$("#query-start-time")[0].value,
            endTime:$("#query-end-time")[0].value,
            keyword:$("#query-keyword")[0].value,
            serverIpList:activeServer
        },
        function (res) {
            alert("Finish executing log query");
        },
        function (error){
            alert(error);
        }
    )
}
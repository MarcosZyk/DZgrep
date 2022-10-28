class ServerForm {

    id;
    ip = '';
    user = '';
    password = '';
    logDir = '';

    constructor(id) {
        this.id = id;
    }

    toElement(isReadOnly) {
        return '   <form>\n' +
            '       <div class="row mb-3">\n' +
            '           <label for="server-' + this.id + '-ip" class="col-sm-2 col-form-label">ip</label>\n' +
            '           <div class="col-sm-10">\n' +
            '               <input type="text" class="form-control" id="server-' + this.id + '-ip"  value="' + this.ip + '" ' + (isReadOnly ? ' disabled readonly' : '') + '>\n' +
            '           </div>\n' +
            '       </div>\n' +
            '       <div class="row mb-3">\n' +
            '           <label for="server-' + this.id + '-user" class="col-sm-2 col-form-label">user</label>\n' +
            '           <div class="col-sm-10">\n' +
            '               <input type="text" class="form-control" id="server-' + this.id + '-user" value="' + this.user + '" ' + (isReadOnly ? ' disabled readonly' : '') + '>\n' +
            '           </div>\n' +
            '       </div>\n' +
            '       <div class="row mb-3">\n' +
            '           <label for="server-' + this.id + '-password" class="col-sm-2 col-form-label">password</label>\n' +
            '           <div class="col-sm-10">\n' +
            '               <input type="text" class="form-control" id="server-' + this.id + '-password" value="' + this.password + '" ' + (isReadOnly ? ' disabled readonly' : '') + '>\n' +
            '           </div>\n' +
            '       </div>\n' +
            '       <div class="row mb-3">\n' +
            '           <label for="server-' + this.id + '-logDir" class="col-sm-2 col-form-label">log dir</label>\n' +
            '           <div class="col-sm-10">\n' +
            '               <input type="text" class="form-control" id="server-' + this.id + '-logDir" value="' + this.logDir + '" ' + (isReadOnly ? ' disabled readonly' : '') + '>\n' +
            '           </div>\n' +
            '       </div>\n' +
            '   </form>\n';
    }

}

let serverIdGenerator = 0;
let serverIdForAdd = 'add';
let serverFormForAdd = new ServerForm(serverIdForAdd);
let serverList = [];


$(document).ready(function () {
    prepareAddServerBlock();
});

function prepareAddServerBlock() {
    $("#add-server-block").append(getAddServerUIElement());
}

function getAddServerUIElement() {
    return ' <div class="modal fade" id="server-' + serverIdForAdd + '-modal" tabindex="-1" aria-labelledby="exampleModalLabel" aria-hidden="true">\n' +
        '       <div class="modal-dialog">\n' +
        '             <div class="modal-content">\n' +
        '                  <div class="modal-header">\n' +
        '                      <h5 class="modal-title" id="server-' + serverIdForAdd + '-modal-label">New Server Info</h5>\n' +
        '                      <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>\n' +
        '                  </div>\n' +
        '<div class="modal-body">\n' +
        serverFormForAdd.toElement(false) +
        '</div>\n' +
        '                  <div class="modal-footer">\n' +
        '                      <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>\n' +
        '                      <button type="button" class="btn btn-primary" onclick="addServer()">submit</button>\n' +
        '                  </div>\n' +
        '              </div>\n' +
        '          </div>\n' +
        '      </div>';
}

function addServer() {
    let newServer = new ServerForm(serverIdGenerator++);
    newServer.ip = $("#server-" + serverIdForAdd + "-ip")[0].value;
    newServer.user = $("#server-" + serverIdForAdd + "-user")[0].value;
    newServer.password = $("#server-" + serverIdForAdd + "-password")[0].value;
    newServer.logDir = $("#server-" + serverIdForAdd + "-logDir")[0].value;
    $("#server-list").append(generateServerInfoUI(newServer));
    serverList.push(newServer);

    postRequest(
        '/server/register',
        {
            ip: newServer.ip,
            username: newServer.user,
            password: newServer.password,
            logDir: newServer.logDir,
            isActive: 'true'
        },
        function (res) {

        },
        function (error) {
            alert(error)
        }
    )

}

function generateServerInfoUI(serverInfo) {
    return '<div class="accordion-item">\n' +
        '                <h2 class="accordion-header" id="flush-headingOne">\n' +
        '                    <button class="accordion-button collapsed" type="button" data-bs-toggle="collapse"\n' +
        '                            data-bs-target="#flush-collapse-' + serverInfo.id + '-" aria-expanded="false" aria-controls="flush-collapseOne">\n' +
        '                       ' + (serverInfo.user.toString() + '@' + serverInfo.ip.toString()) + '\n' +
        '                    </button>\n' +
        '                </h2>\n' +
        '                <div id="flush-collapse-' + serverInfo.id + '-" class="accordion-collapse collapse" aria-labelledby="flush-headingOne"\n' +
        '                     data-bs-parent="#server-list">\n' +
        '                    <div class="accordion-body">\n' +
        serverInfo.toElement(true) +
        '\n' +
        '                        <br/>\n' +
        '\n' +
        '                        <button type="button" class="btn btn-success">activate</button>\n' +
        '\n' +
        '                        <!-- Button trigger modal -->\n' +
        '                        <button type="button" class="btn btn-primary" data-bs-toggle="modal"\n' +
        '                                data-bs-target="#server-' + serverInfo.id + '-modal">edit\n' +
        '                        </button>\n' +
        '\n' +
        '                        <button type="button" class="btn btn-danger">remove</button>\n'
        + generateServerInfoModal(serverInfo);

}

function generateServerInfoModal(serverInfo) {
    return '                        <!-- Modal -->\n' +
        '                        <div class="modal fade" id="server-' + serverInfo.id + '-modal" tabindex="-1" aria-labelledby="exampleModalLabel"\n' +
        '                             aria-hidden="true">\n' +
        '                            <div class="modal-dialog">\n' +
        '                                <div class="modal-content">\n' +
        '                                    <div class="modal-header">\n' +
        '                                        <h5 class="modal-title" id="server-' + serverInfo.id + '-modal-label">' + (serverInfo.ip + 'Server Info') + '</h5>\n' +
        '                                        <button type="button" class="btn-close" data-bs-dismiss="modal"\n' +
        '                                                aria-label="Close"></button>\n' +
        '                                    </div>\n' +
        '                                    <div class="modal-body">\n' +
        serverInfo.toElement(false) +
        '                                    </div>\n' +
        '                                    <div class="modal-footer">\n' +
        '                                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close\n' +
        '                                        </button>\n' +
        '                                        <button type="button" class="btn btn-primary">Save changes</button>\n' +
        '                                    </div>\n' +
        '                                </div>\n' +
        '                            </div>\n' +
        '                        </div>\n' +
        '                    </div>\n' +
        '                </div>\n' +
        '            </div>';
}
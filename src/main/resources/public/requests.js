function timeoutRequest() {
alert("timeout");
}

function sendRequest(request,callback) {
    let httpRequest = new XMLHttpRequest();
    httpRequest.open('POST','request?'+encodeURI(JSON.stringify(request)));

    httpRequest.onreadystatechange = function () {
        if(this.readyState == 4 && this.status == 200){
            let response = JSON.parse(this.responseText);
            callback(response);
        }
    };

    httpRequest.timeout = 5000;
    httpRequest.ontimeout = function () {
        timeoutRequest();
    }
    httpRequest.send();
}

function sendTest() {
    let request = {};
    request.key = "test";
    request.content = "Hello World";
    sendRequest(request,testCallback);
}

function testCallback(response) {
    alert(JSON.stringify(response));
}

$(document).ready(function () {
    $(".content").load("homeContent.html");
})

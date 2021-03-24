

function loadHomepage() {
    sendRequest("post","isLoggedIn",{},function (status,res) {
        const response = JSON.parse(res);
        if(response.loggedIn == "true"){
            $(".content").load("home.html");
        }else{
            $(".content").load("login.html");
        }
    });
}


function sendRequest(method,head,body,callback) {
    const httpRequest = new XMLHttpRequest();
    httpRequest.open(method,head+"?"+encodeURI(JSON.stringify(body)));

    httpRequest.onreadystatechange = function () {

        if(this.readyState == 4){
            //let response = JSON.parse(this.responseText);
            callback(this.status,this.responseText);
        }
    };

    httpRequest.timeout = 5000;
    httpRequest.ontimeout = function () {
        alert("Connection lost.");
    };
    httpRequest.send();
}

$(document).ready(function () {
    loadHomepage();
});
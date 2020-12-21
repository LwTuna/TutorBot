function timeoutRequest() {
alert("timeout");
}

function sendRequest(request,callback) {
    let httpRequest = new XMLHttpRequest();
    httpRequest.open('POST','request?'+encodeURI(JSON.stringify(request)));

    httpRequest.onreadystatechange = function () {
        if(this.readyState == 4 && this.status == 200){
            let response = JSON.parse(this.responseText);
            if(response.key == 'error') alert(response.message);
            callback(response);
        }
    };

    httpRequest.timeout = 5000;
    httpRequest.ontimeout = function () {
        timeoutRequest();
    };
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





function onIsLoginResponse(response) {
    if(!response.loggedIn){
        $(".content").load("login.html");
    }else{
        $(".content").load("homeContent.html");
    }
}

function onReady() {
    let request = {};
    request.key = "isLoggedIn";
    sendRequest(request,onIsLoginResponse);
}

$(document).ready(function () {
    onReady();
});

function setReviewPage() {
    $(".content").load("review.html");
}




/*****************Cookie Management ******************/
/*

// Copied from an StackOverflow answer https://stackoverflow.com/questions/14573223/set-cookie-and-get-cookie-with-javascript
function setCookie(name,value,days) {
    let expires = "";
    if (days) {
        let date = new Date();
        date.setTime(date.getTime() + (days*24*60*60*1000));
        expires = "; expires=" + date.toUTCString();
    }
    document.cookie = name + "=" + (value || "")  + expires + "; path=/";
}
function getCookie(name) {
    let nameEQ = name + "=";
    let ca = document.cookie.split(';');
    for(let i=0;i < ca.length;i++) {
        let c = ca[i];
        while (c.charAt(0)==' ') c = c.substring(1,c.length);
        if (c.indexOf(nameEQ) == 0) return c.substring(nameEQ.length,c.length);
    }
    return null;
}
function eraseCookie(name) {
    document.cookie = name +'=; Path=/; Expires=Thu, 01 Jan 1970 00:00:01 GMT;';
}
* */
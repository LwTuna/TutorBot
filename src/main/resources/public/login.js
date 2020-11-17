function login() {
    let request = {};
    request.key = "login";
    request.user = document.getElementById("usr").value;
    request.password = hashPwd(document.getElementById("pwd").value);
    sendRequest(request,onLoginResponse);
}

function onLoginResponse(response) {
    if(response.success){
        $(".content").load("homeContent.html");
    }else{
        alert("Login failed."+response.status);
    }


}

function hashPwd(pwd) {
    let hashObj = new jsSHA("SHA-512", "TEXT", {numRounds: 1});
    hashObj.update(pwd);
    return hashObj.getHash("HEX");
}

function register() {
    let request = {};
    request.key = "register";
    request.user = document.getElementById("usr").value;
    request.password = hashPwd(document.getElementById("pwd").value);
    sendRequest(request,onRegisterResponse);
}

function onRegisterResponse(response) {
    alert(response.message);
}
function signIn(event){

    const ignInName= document.getElementById("signInName").value;
    const signInPass= document.getElementById("signInPass").value
    if(isEmpty(ignInName) || isEmpty(signInPass)) return;
    let req = {};
    req.signInName = ignInName;
    req.signInPass = hashPwd(signInPass);

    sendRequest("post","login",req,function(status,res) {
        const response = JSON.parse(res);
        if(response.success === "true"){
            $(".content").load("home.html");
        }else{

            alert(response.message);
            document.getElementById("signInForm").reset();
        }
    });
}

$(document).ready(function () {
    document.getElementById("loginBtn").addEventListener("click", signIn);
});

function hashPwd(pwd) {
    let hashObj = new jsSHA("SHA-512", "TEXT", {numRounds: 1});
    hashObj.update(pwd);
    return hashObj.getHash("HEX");
}

function isEmpty(str) {
    return (!str || 0 === str.length);
}
function seeInEditor(btn){
    const id = btn.dataset.exid;
    let req = {};
    req.id = id;
    sendRequest("post","getAnswer",req,loadEditor);
}

function  loadEditor(status,res){
    if(status == 200){
        $(".content").load("answer.html",function () {
            const response = JSON.parse(res);
            const editor = ace.edit("editor");
            document.getElementById("exHead").innerHTML = response.head;
            editor.setValue(response.answer);
            document.getElementById("backBtn").dataset.exid = response.exercise_id;
        });
    }
}

function vote(btn,score) {
    const id = btn.dataset.exid;
    let obj = {};
    obj.answId = id;
    obj.up = score == 1;

    sendRequest("post","vote",obj,function (status,res) {
        if(status === 200) {
            const response = JSON.parse(res);
            const score = response.score;
            document.getElementById("score"+id).innerHTML = score;
        }
    });
}
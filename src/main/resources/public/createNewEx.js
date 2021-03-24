$(document).ready(function () {
    const editor = ace.edit("editor");
    editor.setTheme("ace/theme/monokai");
    editor.getSession().setMode("ace/mode/java");
    editor.setReadOnly(false);

});


function createEx(btn) {
    const conf =confirm("Aufgabe wirklich erstellen/bearbeiten?");

    if(conf === true){
        let req = {};
        req.exid = btn.dataset.exid;
        req.head = document.getElementById("textHead").value;
        req.answers = ace.edit("editor").getValue();
        sendRequest("post","createExercise",req,function (status,response) {
            if(status == 200){
                alert("Erfolgreich Erstellt/Bearbeitet!");
                $(".content").load("home.html");
            }else if(status == 401){
                alert("Error, not logged In Or not enough Permissions.");
            }else if(status == 500){
                alert("Something went wrong submitting your soultion. ServerError Status Code 500.");
            }
        });
    }
}
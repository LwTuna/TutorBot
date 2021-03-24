

$(document).ready(function () {
    const editor = ace.edit("editor");
    editor.setTheme("ace/theme/monokai");
    editor.getSession().setMode("ace/mode/java");
    editor.setReadOnly(false);

});


function submitExcercise(btn) {
   const conf =confirm("Aufgabe wirklich abgeben?");

   if(conf == true){
       const exId = btn.dataset.id;
       let req = {};
       req.exId = exId;
       req.answer = ace.edit("editor").getValue();
       sendRequest("post","submitExercise",req,function (status,response) {
           if(status == 200){
               alert("Erfolgreich Abgegeben!");
               $(".content").load("home.html");
           }else if(status == 401){
               alert("Error, not logged In.");
           }else if(status == 500){
               alert("Something went wrong submitting your soultion. ServerError Status Code 500.");
           }
       });
   }
}
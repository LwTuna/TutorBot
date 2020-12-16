
let loadedQuestions = 0;

let questionsLoaded = [];

function loadQuestion(question,index) {
    switch (question.type) {
        case "input":
            $("#q"+index).load("questionTypes/inputQuestion.html",function () {
                document.getElementById("q"+index).getElementsByClassName("question")[0].innerHTML = question.question;
            });
            break;
        case "textArea":
            $("#q"+index).load("questionTypes/textareaQuestion.html",function () {
                document.getElementById("q"+index).getElementsByClassName("question")[0].innerHTML = question.question;
            });

            break;
        case "multipleChoice":
            $("#q"+index).load("questionTypes/multipleChoiceQuestion.html",function () {
                document.getElementById("q"+index).getElementsByClassName("question")[0].innerHTML = question.question;

                for(let i=0;i<4;i++){

                    document.getElementById("q"+index).getElementsByClassName("form-check-label")[i].innerHTML += question.answers[i];
                }
            });
            break;
    }
    loadedQuestions ++;


}

function submitAnswersCallback(response) {
   if(response.success == true){
       $(".content").load("homeContent.html");
   }else{
       alert("Something happend while trying to sumbit your answers!");
   }
}

function finishedQuestions() {
    let answers = [];

    questionsLoaded.forEach(function (item,index,array) {
        let element;
        if(document.getElementById("q"+item).getElementsByTagName("textarea").length>0){
            element= document.getElementById("q"+item).getElementsByTagName("textarea")[0];
        }else if(document.getElementById("q"+item).getElementsByTagName("input")[0] != null){
            if(document.getElementById("q"+item).getElementsByTagName("input")[0].type == input){
                element= document.getElementById("q"+item).getElementsByTagName("input")[0];
            }

        }else {
            //TODO Manage Multiple Choice Questions
        }
        answers[index] = {"index":item, "answer":element.value};
    });

    sendRequest({"key":"sumbitAnswers","answers":answers},submitAnswersCallback);
}

function loadQuestions(response) {

    const questions = response.questionsArray;
    let content = document.getElementById("content").innerHTML;
    questions.forEach(function (item,index,array) {
        content += "<div class=\"row\">\n" +
            "        <div class=\"col-sm-4\" id=\"q"+item.questionID+"\" style=\"left: 50px\">\n" +
            "        </div>\n" +
            "    </div>";
        questionsLoaded.push(item.questionID);
    });
    content += "<div class=\"row text-center justify-content-center\">\n" +
        "        <div class=\"col\">\n" +
        "            <button type=\"button\" class=\"btn btn-primary\" id=\"finishedQuestions\" onclick=\"finishedQuestions()\">Abgabe</button>\n" +
        "        </div>\n" +
        "    </div>";
    document.getElementById("content").innerHTML = content;
    questions.forEach(function (item,index,array) {
        loadQuestion(item, item.questionID);
    });

}




$(document).ready(function () {
    let request = {};
    request.key = "getQuestions";
    sendRequest(request,loadQuestions);
});


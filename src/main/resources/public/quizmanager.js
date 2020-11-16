


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



}

function loadQuestions(response) {

    const questions = response.questionsArray;
    let content = document.getElementById("content").innerHTML;
    questions.forEach(function (item,index,array) {
        content += "<div class=\"row\">\n" +
            "        <div class=\"col-sm-4\" id=\"q"+index+"\" style=\"left: 50px\">\n" +
            "        </div>\n" +
            "    </div>";
    });
    document.getElementById("content").innerHTML = content;
    questions.forEach(function (item,index,array) {
        loadQuestion(item,index);
    });
}

$(document).ready(function () {
    let request = {};
    request.key = "getQuestions";
    sendRequest(request,loadQuestions);
});


$(document).ready(function () {
    loadQuestions();
});

function loadQuestions() {
    sendRequest("post","questions",{},function (status,res) {
        if(status == 200){
            document.getElementById("cardContainer").innerHTML = "";
            const response = JSON.parse(res);
            const questions = response.questions;
            for(let i=0;i<questions.length;i++){
                const question = questions[i];
                addQuestionCard(
                    question.title.replace(/(?:\r\n|\r|\n)/g, '<br>'),
                    question.body.replace(/(?:\r\n|\r|\n)/g, '<br>'),
                    question.id,
                    question.author,
                    question.date,
                    question.answers);
            }
        }
    });
}

function sumbitQuestion() {
    let req = {};
    req.body = document.getElementById("questionHeader").value;;
    req.title = document.getElementById("questionBody").value;
    sendRequest("post","createQuestion",req,function (status,res) {
        if(status == 200){
            loadQuestions();
            document.getElementById("questionHeader").value = "";
            document.getElementById("questionBody").value = "";
        }
    })
}

function sendQAnswer(btn) {
    let req = {};
    req.qid = btn.dataset.qid;
    req.answer = document.getElementById("answerHead"+req.qid).value;
    sendRequest("post","submitQAnswer",req,function (status,res) {
        if(status == 200) {
            loadQuestions();
        }
    });
}
function upvoteQAnswer(btn) {
    let req = {};
    req.ansId = btn.dataset.ansid;
    sendRequest("post","upvoteQAnswer",req,function (status,res) {
        if(status == 200) {
            const response = JSON.parse(res);
            updateScore(btn.dataset.ansid,response.score);
        }
    });
}
function downvoteQAnswer(btn) {
    let req = {};
    req.ansId = btn.dataset.ansid;
    sendRequest("post","downvoteQAnswer",req,function (status,res) {
        if(status == 200) {
            const response = JSON.parse(res);
            updateScore(btn.dataset.ansid,response.score);
        }
    });
}

function updateScore(ansid,score) {
    document.getElementById("score"+ansid).innerHTML = score;
}

function addQuestionCard(questionTitle,questionBody,questionId,questionAuthor,questionDate,answers) {
    document.getElementById("cardContainer").innerHTML +=
        " <div class=\"row my-3\">\n" +
        "                <div class=\"col\">\n" +
        "                    <div class=\"card\">\n" +
        "                        <div class=\"card-header\">\n" +
        "                            <h4>"+questionTitle+"</h4>\n" +
        "                        </div>\n" +
        "                        <div class=\"card-body \">\n" +
        "                            <p> "+questionBody+"</p>\n" +
        "                            <div class=\"row border-top pt-2\">\n" +
        "                                <div class=\"col col-2 mx-auto\">\n" +
        "                                    <button class=\"btn btn-secondary btn-sm\" type=\"button\" data-toggle=\"collapse\" data-target=\"#answers"+questionId+"Collapse\" aria-expanded=\"false\" aria-controls=\"answers"+questionId+"Collapse\">Zeige Antworten</button>\n" +
        "                                </div>\n" +
        "                                <div class=\"col col-10 mx-auto\">\n" +
        "                                    Frage von "+questionAuthor+" am "+questionDate+"\n" +
        "                                </div>\n" +
        "                            </div>\n" +
        "                        </div>\n" +
        "                        <div class=\"collapse\" id=\"answers"+questionId+"Collapse\">\n" +
        "                            <div class=\"card-footer\" id='answerContainer"+questionId+"'>\n" +
        "                            </div>\n" +
        "                        </div>\n" +
        "                    </div>\n" +
        "                </div>\n" +
        "            </div>"


    for(let i=0;i<answers.length;i++){
        const answer = answers[i];
        addAnswerCard(questionId,answer.score,answer.body.replace(/(?:\r\n|\r|\n)/g, '<br>'),answer.author,answer.date,answer.id);
    }
    addAnswerInputCard(questionId);
}

function addAnswerCard(questionId,score,answerBody,answerAuthor,answerDate,answerId){
    document.getElementById("answerContainer"+questionId).innerHTML+=
                                    "<div class=\"row my-1\">\n" +
    "                                    <div class=\"col col-1\">\n" +
    "                                        <div class=\"container-fluid\">\n" +
    "                                            <div class=\"row\">\n" +
    "                                                <div class=\"col mx-auto\">\n" +
    "                                                    <button class=\"btn\" style=\"color:#09a70b\" data-ansid='"+answerId+"' onclick='upvoteQAnswer(this)'><i class=\"fas fa-arrow-up\"></i></button>\n" +
    "                                                </div>\n" +
    "                                            </div>\n" +
    "                                            <div class=\"row\">\n" +
    "                                                <div class=\"col text-center\" id='score"+answerId+"'>\n" +
    "                                                    "+score+"\n" +
    "                                                </div>\n" +
    "                                            </div>\n" +
    "                                            <div class=\"row\">\n" +
    "                                                <div class=\"col mx-auto\">\n" +
    "                                                    <button class=\"btn\" style=\"color:#a70202\" data-ansid='"+answerId+"' onclick='downvoteQAnswer(this)'><i class=\"fas fa-arrow-down\"></i></button>\n" +
    "                                                </div>\n" +
    "                                            </div>\n" +
    "                                        </div>\n" +
    "                                    </div>\n" +
    "                                    <div class=\"col col-11\">\n" +
    "                                        <div class=\"card\">\n" +
    "                                            <div class=\"card-body\">\n" +
    "                                                "+answerBody+"\n" +
    "                                            </div>\n" +
    "                                            <div class=\"card-footer\">Antwort von "+answerAuthor+" am "+answerDate+"</div>\n" +
    "                                        </div>\n" +
    "                                    </div>\n" +
    "                                </div>\n";
}

function addAnswerInputCard(questionId) {
    document.getElementById("answerContainer"+questionId).innerHTML+= "<div class=\"row my-1\">\n" +
    "                                    <div class=\"col col-12\">\n" +
    "                                        <div class=\"card\">\n" +
    "                                            <div class=\"card-header text-center\">\n" +
    "                                                <h5>Antworte auf die Frage</h5>\n" +
    "                                            </div>\n" +
    "                                            <div class=\"card-body\">\n" +
    "                                                <textarea class=\"form-control\" id=\"answerHead"+questionId+"\" rows=\"3\"></textarea>\n" +
    "                                            </div>\n" +
    "                                            <div class=\"card-footer text-right\">\n" +
    "                                                <button class=\"btn btn-secondary\" data-qid='"+questionId+"' onclick='sendQAnswer(this)'>Antworten</button>\n" +
    "                                            </div>\n" +
    "                                        </div>\n" +
    "                                    </div>\n" +
    "                                </div>\n";
}


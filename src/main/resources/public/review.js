let answers;
function setAnswersToReview(response){
    if(response.hasAnswers){
        document.getElementById("answerContainer").style.display = "block";
        const row = document.getElementById("answerContainer").children.item(1);
        for(let i = 0;i<response.answers.length;i++){
            row.innerHTML +=
                "<div class=\"col border mx-1 py-4 bg-light\" data-answerId = \""+response.answers[i]._id+"\">\n" +
                +response.answers[i].answer +
                "<br><button class=\"btn btn-primary\" onclick=\"bestAnswer(this)\">Beste Lösung</button>"+
                "            </div>";
        }

        answers = response.answers;
    }else{
        document.getElementById("noAnswersFound").style.display = "block";
    }
}

function bestAnswer(button) {
    if(confirm("Diese Lösung als Beste makieren?")){
        let request = {};
        request.key = "submitBestAnswer";
        request.val = button.parentElement.dataset.answerid;
        sendRequest(request,submitBestAnswerCallback);
    }
}

function submitBestAnswerCallback(response) {
    alert("Lösung erfolgreich eingetragen!");
    onReady();
}

function getAnswersToReview() {
    let request = {};
    request.key = "getAnswersToReview";
    request.questionID = 1; //TODO
    sendRequest(request,setAnswersToReview);
}


$(document).ready(function () {
    getAnswersToReview();
});
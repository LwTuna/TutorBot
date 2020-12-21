let answers;
function setAnswersToReview(response){
    if(response.hasAnswers){
        document.getElementById("answerContainer").style.display = "block";
        answers = response.answers;
    }else{
        document.getElementById("noAnswersFound").style.display = "block";
    }
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
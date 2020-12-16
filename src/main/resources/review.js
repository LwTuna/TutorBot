function setAnswersToReview(response){
    if(response.hasAnswers){
        document.getElementById(answerContainer).style.display = "block";
    }
}


function getAnswersToReview() {
    let request = {};
    request.key = "getAnswersToReview";
    sendRequest(request,setAnswersToReview);
}


$(document).ready(function () {
    getAnswersToReview();
});
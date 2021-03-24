$(document).ready(function () {
    getExercises(false);
    getExercises(true);
    loadQuestionButton();
    loadEditing();
});

function loadQuestionButton() {
    document.getElementById("btnContainer").innerHTML +=
        "<div class=\"row text-center justify-content-center my-5\">\n" +
        "      <button class=\"btn btn-secondary\" type=\"button\" onclick='loadQuestionSite()' >" +
        "                    Fragen\n" +
        "      </button>\n" +
        "</div>";
}

function loadQuestionSite() {
    $(".content").load("questions.html");
}

function loadEditing() {
    sendRequest("post","getRole",{},function (status,res) {
       if(status == 200){
           const response = JSON.parse(res);
           if(response.level >= 1){
               document.getElementById("btnContainer").innerHTML +=
                   "<div class=\"row text-center justify-content-center my-5\">\n" +
                   "            <div class=\"dropdown\">\n" +
                   "                <button class=\"btn btn-secondary\" type=\"button\" onclick='createNewEx()' >" +
                   "                    Neue Aufgabe Erstellen\n" +
                   "                </button>\n" +
                   "            </div>\n" +
                   "        </div>";
               let obj = {};
               obj.all = true;
               sendRequest("post","getExercises",obj,function (status,res) {
                   if(status == 200){
                       document.getElementById("btnContainer").innerHTML +=
                           "<div class=\"row text-center justify-content-center my-5\">\n" +
                           "            <div class=\"dropdown\">\n" +
                           "                <button class=\"btn btn-secondary dropdown-toggle\" type=\"button\" id=\"dropDownAnswerButton\" data-toggle=\"dropdown\" aria-haspopup=\"true\" aria-expanded=\"false\">\n" +
                           "                    Aufgaben Bearbeiten\n" +
                           "                </button>\n" +
                           "                <div class=\"dropdown-menu\" aria-labelledby=\"dropdownMenuButton\" id=\"exEditDropdown\">\n" +
                           "\n" +
                           "                </div>\n" +
                           "            </div>\n" +
                           "        </div>";
                       const response = JSON.parse(res);
                       exercises = response.exercises;
                       for(let i=0;i<response.exercises.length;i++){
                           const exHead = response.exercises[i].head;
                           const exId = response.exercises[i].id;
                           document.getElementById("exEditDropdown").innerHTML +=
                               "<button class='dropdown-item' type='button' data-exid='"+exId+"' onclick='updateEx(this)'>"+exHead+"</button>";
                       }
                   }
               });

               document.getElementById("btnContainer").innerHTML +=
                   "<div class=\"row text-center justify-content-center my-5\">\n" +
                   "      <button class=\"btn btn-secondary\" type=\"button\" onclick='loadSetActiveExercises()' >" +
                   "                    Aktive Aufgaben verwalten\n" +
                   "      </button>\n" +
                   "</div>";

           }
       }
    });



}



function createNewEx() {
    $(".content").load("createNewEx.html");
}

function updateEx(btn) {

    $(".content").load("createNewEx.html",function () {
        const id = btn.dataset.exid;
        let req={};
        req.id = id;
        sendRequest("post","getExercise",req,function (status,res) {
            if(status === 200) {
                const response = JSON.parse(res);
                const exObj = response;
                const editor = ace.edit("editor");
                document.getElementById("textHead").value = exObj.head;
                editor.setValue(exObj.answers);
                document.getElementById("createExBtn").dataset.exid = id;
            }
        })

    });
}

function loadAnswersDropdown(status,res) {
    if(status === 200) {
        const response = JSON.parse(res);
        for(let i=0;i<response.exercises.length;i++){
            const exHead = response.exercises[i].head;
            const exId = response.exercises[i].id;
            document.getElementById("answerdropdown").innerHTML +=
                "<button class='dropdown-item' type='button' data-exid='"+exId+"' onclick='loadAnswerPage(this)'>"+exHead+"</button>";
        }
    }
}

function loadAnswerPage(btn) {
    const exId = btn.dataset.exid;
    $(".content").load("answers.html",function () {
        document.getElementById("cardContainer").innerHTML = "";

        let obj = {};
        obj.id = exId;
        sendRequest("post","getAnswers",obj,addAnswers);
    });
}

function addAnswers(status,res) {
    if(status === 200) {
        const response = JSON.parse(res);
        for(let i=0;i<response.answers.length;i++){
            addAnswerCard(response.answers[i]);
        }
    }
}

function addAnswerCard(obj) {
    const id = obj.id;
    const head = obj.head;
    let answer = obj.answer;
    answer = answer.replace(/(?:\r\n|\r|\n)/g, '<br>');
    const date = obj.lastChanged;
    const author = obj.realname;
    const score = obj.score;
    document.getElementById("cardContainer").innerHTML +=
    "<div class=\"container bg-light py-3 border border-dark\" data-exid=\""+id+"\">\n" +
        "                <div class=\"row\">\n" +
        "                    <div class=\"col col-1\">\n" +
        "                        <div class=\"container-fluid\">\n" +
        "                            <div class=\"row\">\n" +
        "                                <div class=\"col mx-auto\">\n" +
        "                                    <button type=\"button\" class=\"btn \"style=\"color:#09a70b\" data-exid=\""+id+"\" onclick='vote(this,1)'><i class=\"fas fa-arrow-up\"></i></button>\n" +
        "                                </div>\n" +
        "                            </div>\n" +
        "                            <div class=\"row mx-auto\">\n" +
        "                                <div class=\"col text-center \">\n" +
        "                                    <a id=\"score"+id+"\">"+score+"</a>\n" +
        "                                </div>\n" +
        "                            </div>\n" +
        "                            <div class=\"row\">\n" +
        "                                <div class=\"col mx-auto\">\n" +
        "                                    <button type=\"button\" class=\"btn\" style=\"color:#a70202\" data-exid=\""+id+"\" onclick='vote(this,-1)'><i class=\"fas fa-arrow-down\"></i></button>\n" +
        "                                </div>\n" +
        "                            </div>\n" +
        "                        </div>\n" +
        "                    </div>\n" +
        "                    <div class=\"col col-11\">\n" +
        "                        <div class=\"card\" >\n" +
        "                            <div class=\"card-header\">\n" +
        "                                <h5 class=\"card-title\">"+head+"</h5>\n" +
        "                            </div>\n" +
        "                            <div class=\"card-body\">\n" +
        "                                <p class=\"card-text\">\n" +
        "                                "+answer+" " +
        "                                </p>\n" +
        "                                <button class=\"btn btn-secondary\" data-exid=\""+id+"\" onclick='seeInEditor(this)'>Siehe im Editor</button>\n" +
        "                            </div>\n" +
        "                            <div class=\"card-footer text-right\">"+author+" last change: "+date+"</div>\n" +
        "                        </div>\n" +
        "                    </div>\n" +
        "                </div>\n" +
        "            </div>";
}

function loadSetActiveExercises() {
    $(".content").load("setActiveExercises.html",function () {
        sendRequest("post","activeExercises",{},function (status,res) {
            if(status == 200){
                const response = JSON.parse(res);
                const active = response.active;
                const inactive = response.inactive;

                for(let i=0;i<active.length;i++){
                    document.getElementById("activeExercises").innerHTML +=
                        "<li data-exid=\""+active[i].id+"\">"+active[i].head+"</li>";
                }
                for(let i=0;i<inactive.length;i++){
                    document.getElementById("allExercises").innerHTML +=
                        "<li data-exid=\""+inactive[i].id+"\">"+inactive[i].head+"</li>";
                }
            }
        });
    });

}

function getExercises(all) {
    let obj = {};
    obj.all = all;
    if(all){
        sendRequest("post","getExercises",obj,loadAnswersDropdown);
    }else{
        sendRequest("post","getExercises",obj,loadExercises);
    }

}

var exercises = [];

function loadExercises(status,res) {
    if(status === 200){
        const response = JSON.parse(res);
        exercises = response.exercises;
        const exAmt = response.exercises.length;
        const badgeColor = exAmt > 0 ? "badge-warning" : "badge-light";
        document.getElementById("dropDownExerciseButton").innerHTML+="<span class=\"badge "+badgeColor+"\">"+exAmt+"</span>";
        for(let i=0;i<response.exercises.length;i++){
            const exHead = response.exercises[i].head;
            const exId = response.exercises[i].id;
            document.getElementById("exercisedropdown").innerHTML +=
                "<button class='dropdown-item' type='button' data-exid='"+exId+"' onclick='loadExercise(this)'>"+exHead+"| Fällig Bis: "+response.exercises[i].due_date+"</button>";
        }
    }else{
        alert("Status = "+status);
    }
}

function loadExercise(btn) {
    const exId = btn.dataset.exid;
    const exObj = getByExId(exId);
    if(exObj === undefined) return;
    $(".content").load("exercise.html",function () {
        const editor = ace.edit("editor");
        document.getElementById("exHead").innerHTML = exObj.head;
        editor.setValue(exObj.answers);
        document.getElementById("sumbitExercise").dataset.id = exId;
    });
}

function getByExId(exId) {
    for(let i=0;i<exercises.length;i++){
        if(exercises[i].id == exId) return exercises[i];
    }
    return undefined;
}
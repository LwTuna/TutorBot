package de.wip.tutorbot;

import de.wip.tutorbot.persistent.DatabaseHandler;
import de.wip.tutorbot.sessions.LoggedInUser;
import de.wip.tutorbot.sessions.Role;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.mail.MessagingException;
import java.io.*;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class TutorBot {

    private Timer timer;

    private List<Integer> currentExerciseIds;
    private int hoursUntilDue = 48;
    private int period = 3_600_000;

    private String pathToFile = "tutorBot.txt";

    private final String emailSubject = "Dir wurder eine neue Aufgabe vom TutorBotOOP zugeteilt.";

    private static final String email = "tutorbotthm";
    private static final String password = ",Vb@Ecan)~t4zyx@";


    public TutorBot() {
        
        loadCurrentExerciseId();
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
               assignRandomUserExercise();
            }


        }, 1000, period);

    }
    private void assignRandomUserExercise() {
        Random random = new Random(System.currentTimeMillis());
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        LocalDateTime localDateTime = LocalDateTime.now().plusHours(hoursUntilDue);
        try {
            int exId = currentExerciseIds.get(random.nextInt(currentExerciseIds.size()));
            JSONArray array = DatabaseHandler.getUsersWhichHaventDoneEx(Role.USER.getPermissionLevel(),exId);
            if(!array.isEmpty()){
                JSONObject randomUser = array.getJSONObject(random.nextInt(array.length()));
                DatabaseHandler.assignExercise(randomUser.getString("id"),
                        String.valueOf(exId),
                        dateTimeFormatter.format(localDateTime));

                String message = buildMessage(randomUser.getString("realname"),
                        DatabaseHandler.getExercise(String.valueOf(exId)).getString("head"),
                        dateTimeFormatter.format(localDateTime));
                try {
                    TutorBot.sendNotification(randomUser.getString("email"),emailSubject,message, true);
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String buildMessage(String realname,String aufgabe,String zeitBis) {
        return
                "<html>\n" +
                        "<p>Hallo "+realname+", </p>\n" +
                        "<p>Ihnen wurde eine neue Aufgabe vom Tutor Bot in OOP zugeteilt :</p>\n" +
                        "<h4>"+aufgabe+"</h4>\n" +
                        "<p>Bitte erledigen Sie die Aufgabe bis : "+zeitBis+"</p>\n" +
                        "<p>Viele Gr&uuml;&szlig;e,<p>\n" +
                        "<p>Dein OOP Tutor Bot<p>\n" +
                        "</html>";

    }


    private void loadCurrentExerciseId() {
        File file = new File(pathToFile);
        if(!file.exists()) {
            try {
                file.createNewFile();
                currentExerciseIds = new ArrayList<>();
                setCurrentExerciseId(new JSONArray().put(0));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            try {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
                StringBuilder stringBuilder = new StringBuilder();
                String line ;
                while ((line = bufferedReader.readLine()) != null){
                    stringBuilder.append(line);
                }
                JSONObject jsonObject = new JSONObject(stringBuilder.toString());
                JSONArray array = jsonObject.getJSONArray("currentExIds");
                currentExerciseIds = new ArrayList<>();
                for(int i=0;i<array.length();i++){
                    currentExerciseIds.add(array.getInt(i));
                }
                bufferedReader.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void saveCurrentExerciseId() {
        JSONObject jsonObject = new JSONObject();
        JSONArray array = new JSONArray();

        for(Integer i:currentExerciseIds){
            array.put(i);
        }

        jsonObject.put("currentExIds",array);
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(new File(pathToFile)));
            bufferedWriter.write(jsonObject.toString());
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setCurrentExerciseId(JSONArray array){
        currentExerciseIds = new ArrayList<>();
        for(int i=0;i<array.length();i++){
            currentExerciseIds.add(array.getInt(i));
        }
        saveCurrentExerciseId();
    }


    public List<Integer> getCurrentExerciseIds(){
        return currentExerciseIds;
    }



    public static void sendNotification(String recipient,String subject,String message,boolean isHTML) throws MessagingException {
        EmailManager.sendMail(EmailManager.getGmailSession(email,password),recipient,subject,message, isHTML);
    }

    public void sendNotificationAboutNewAnswer(LoggedInUser loggedInUser, int qid, String answer) throws MessagingException {

        Thread thread = new Thread(()->{
            JSONObject recAcc = DatabaseHandler.getAccountFromQuestionId(qid);
            JSONObject question = DatabaseHandler.getQuestion(qid);
            String message = "Du hast eine neue Antwort auf deine Frage :\n"+
                    ""+question.getString("title")+"\n"+
                    "\n"+
                    "Antwort von "+loggedInUser.getRealname()+ " um "+DatabaseHandler.getCurrentDate()+". \n"+
                    ""+answer+"\n\n";

            try {
                sendNotification(recAcc.getString("email"),"Neue Antwort auf deine Frage!",message,false);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }

    public void postAnAnswerToQuestion(String idInDB,String body, String title) {
        Thread thread = new Thread(()->{
            String message = null;
            try {
                message = getBestAnswerToQuestion(body,title);
            } catch (IOException e) {
                e.printStackTrace();
            }
            int qid = DatabaseHandler.getQuestion(body,title,idInDB).getInt("id");

            try {
                DatabaseHandler.putQAnswer(qid, "2",message);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }

    public static String getBestAnswerToQuestion(String body, String title) throws IOException {
       /* var url = new URL(URLEncoder.encode("http:/www.google.de/search?q="+title.replace(" ","+"),"UTF-8"));
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

        String inputLine;
        StringBuilder builder = new StringBuilder();
        while ((inputLine = in.readLine()) != null)
            builder.append(inputLine+"\n");
        in.close();

        return builder.toString();*/
       return "";
    }
}

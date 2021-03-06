package rayan.egor.ftc.engine;

import android.os.Parcel;
import android.os.Parcelable;

import junit.framework.Assert;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Match implements Parcelable {

    //YAY MAGIC NUMBERS!
    public static final byte ANSWER_UNANSWERED = 0;//must remain zero
    public static final byte ANSWER_CORRECT = 1;//must remain one
    public static final byte ANSWER_WRONG = 2;//must remain two

    static final Parcelable.Creator<Match> CREATOR
            = new Parcelable.Creator<Match>() {

        public Match createFromParcel(Parcel in) {
            return new Match(in);
        }

        public Match[] newArray(int size) {
            return new Match[size];
        }
    };
    private boolean isStarted;

    private String myNickname;
    private String enemyNickname;
    private String type;
    private Question questions[] = new Question[3];
    private byte myAnswers[] = new byte[3];// 0 - unanswered, 1 - correct, 2 - wrong
    private byte enemyAnswers[] = new byte[3];
    private int myRatingChange;
    private int enemyRatingChange;
    private String matchToken;

    public Match(String myNickname, JSONObject jsonObject) throws JSONException {
        boolean isFirst;
        if (myNickname.equals(jsonObject.getString("username1")))
            isFirst = true;
        else if (myNickname.equals(jsonObject.getString("username2")))
            isFirst = false;
        else
            throw new JSONException("Given username is not in given JSONObject");
        String myNumber = isFirst ? "1" : "2";
        String enemyNumber = (!isFirst) ? "1" : "2";

        this.myNickname = jsonObject.getString("username" + myNumber);
        this.enemyNickname = jsonObject.getString("username" + enemyNumber);

        if (jsonObject.isNull("username" + enemyNumber))
            enemyNickname = null;

        type = jsonObject.getString("type");
        JSONArray questionsJsonArray = jsonObject.getJSONArray("questions");
        Assert.assertEquals(questionsJsonArray.length(), this.questions.length);
        for (int i = 0; i < questionsJsonArray.length(); i++) {
            JSONObject jsonQuestion = questionsJsonArray.getJSONObject(i);
            Question question = new Question(jsonQuestion);
            this.questions[i] = question;
        }

        JSONArray myAnswers = jsonObject.getJSONArray("user" + myNumber + "Answers");
        for (int i = 0; i < myAnswers.length(); i++) {
            this.myAnswers[i] = (byte) myAnswers.getInt(i);
        }
        JSONArray enemyAnswers = jsonObject.getJSONArray("user" + enemyNumber + "Answers");
        for (int i = 0; i < enemyAnswers.length(); i++) {
            this.enemyAnswers[i] = (byte) enemyAnswers.getInt(i);
        }

        this.myRatingChange = jsonObject.getInt("user" + myNumber + "RatingChange");
        this.enemyRatingChange = jsonObject.getInt("user" + enemyNumber + "RatingChange");

        matchToken = jsonObject.getString("match_token");
        isStarted = jsonObject.getBoolean("isStarted");
    }

    public Match(String type, Question[] questions, String enemyNickname) {
        this.type = type;
        this.questions = questions;
        this.enemyNickname = enemyNickname;
    }

    public Match(Parcel in) {
        type = in.readString();

        in.readByteArray(myAnswers);
        in.readByteArray(enemyAnswers);
        in.readTypedArray(questions, Question.CREATOR);

        myNickname = in.readString();
        enemyNickname = in.readString();

        myRatingChange = in.readInt();
        enemyRatingChange = in.readInt();

        matchToken = in.readString();
        isStarted = in.readInt() == 1;
    }

    public void setMyNickname(String myNickname) {
        this.myNickname = myNickname;
    }

    public String getMyNickname() {
        return myNickname;
    }

    public int getMyRatingChange() {
        return myRatingChange;
    }

    public void setMyRatingChange(int myRatingChange) {
        this.myRatingChange = myRatingChange;
    }

    public int getEnemyRatingChange() {
        return enemyRatingChange;
    }

    public void setEnemyRatingChange(int enemyRatingChange) {
        this.enemyRatingChange = enemyRatingChange;
    }

    public String getType() {
        return type;
    }

    public Question[] getQuestions() {
        return questions;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(type);

        parcel.writeByteArray(myAnswers);
        parcel.writeByteArray(enemyAnswers);
        parcel.writeTypedArray(questions, 0);

        parcel.writeString(myNickname);
        parcel.writeString(enemyNickname);

        parcel.writeInt(myRatingChange);
        parcel.writeInt(enemyRatingChange);

        parcel.writeString(matchToken);
        parcel.writeInt(isStarted ? 1 : 0);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public int getAnsweredByMe() {
        for (int i = 0; i < myAnswers.length; i++) {
            if (myAnswers[i] == ANSWER_UNANSWERED)
                return i;
        }
        return 3;
    }

    public int getAnsweredByEnemy() {
        for (int i = 0; i < enemyAnswers.length; i++) {
            if (enemyAnswers[i] == ANSWER_UNANSWERED)
                return i;
        }
        return 3;
    }

    public int getMyCorrect() {
        int myCorrect = 0;
        for (int i = 0; i < myAnswers.length; i++)
            if (myAnswers[i] == Match.ANSWER_CORRECT)
                myCorrect++;
        return myCorrect;
    }

    public int getEnemyCorrect() {
        int enemyCorrect = 0;
        for (int i = 0; i < enemyAnswers.length; i++)
            if (enemyAnswers[i] == Match.ANSWER_CORRECT)
                enemyCorrect++;
        return enemyCorrect;
    }


    public byte[] getMyAnswers() {
        return myAnswers;
    }

    public byte[] getEnemyAnswers() {
        return enemyAnswers;
    }

    public String getEnemyNickname() {
        return enemyNickname;
    }

    public void setEnemyNickname(String enemyNickname) {
        this.enemyNickname = enemyNickname;
    }

    public String getMatchToken() {
        return matchToken;
    }
}

package rayan.egor.ftc.engine;

import android.os.Parcel;
import android.os.Parcelable;

import junit.framework.Assert;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Question implements Parcelable {

    public static final Creator<Question> CREATOR = new Creator<Question>() {
        @Override
        public Question createFromParcel(Parcel in) {
            return new Question(in);
        }

        @Override
        public Question[] newArray(int size) {
            return new Question[size];
        }
    };
    private String answers[] = new String[4];//first answer is always correct
    private String question;

    public Question(String question, String correctAnswer, String wrongAnswer1, String wrongAnswer2, String wrongAnswer3) {
        this.question = question;
        answers[0] = correctAnswer;
        answers[1] = wrongAnswer1;
        answers[2] = wrongAnswer2;
        answers[3] = wrongAnswer3;
    }

    public Question(Parcel in) {
        question = in.readString();
        in.readStringArray(answers);
    }

    public Question(JSONObject jsonObject) throws JSONException {
        question = jsonObject.getString("question");
        JSONArray answersJsonArray = jsonObject.getJSONArray("answers");
        Assert.assertEquals(answersJsonArray.length(), answers.length);
        for (int id = 0; id < answers.length; id++) {
            answers[id] = answersJsonArray.getString(id);
        }
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(question);
        parcel.writeStringArray(answers);
    }

    //warning: upper/lower case matters
    public boolean isCorrectAnswer(CharSequence answer) {
        return answer.equals(answers[0]);
    }

    public String getQuestion() {
        return question;
    }

    public String[] getAnswers() {
        return answers;
    }

    public List<String> getShuffledAnswers() {
        List<String> answers = new ArrayList<>(Arrays.asList(this.answers));
        Collections.shuffle(answers);
        return answers;
    }

    public List<String> getShuffledAnswers(Random random) {
        List<String> answers = new ArrayList<>(Arrays.asList(this.answers));
        Collections.shuffle(answers, random);
        return answers;
    }

    @Override
    public int describeContents() {
        return 0;
    }
}

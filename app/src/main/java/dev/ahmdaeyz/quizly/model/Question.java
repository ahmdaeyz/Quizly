package dev.ahmdaeyz.quizly.model;

import android.os.Parcelable;

import androidx.annotation.NonNull;

import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

import dev.ahmdaeyz.quizly.common.enums.QuestionType;
public abstract class Question {

    private String text;
    private String rightAnswer;
    private QuestionType type;
    private String userAnswer = " ";

    public Question(String text, String rightAnswer, QuestionType type) {
        this.text = text;
        this.rightAnswer = rightAnswer;
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public String getRightAnswer() {
        return rightAnswer;
    }

    public QuestionType getType() {
        return type;
    }

    public String getUserAnswer() {
        return userAnswer.replaceFirst(" ,","");
    }

    public void setUserAnswer(String userAnswer) {
        this.userAnswer = userAnswer;
    }

    @NonNull
    @Override
    public String toString() {
        return "Right answer: "+ rightAnswer + " ,User answer: "+ userAnswer;
    }

}

package dev.ahmdaeyz.quizly.model;

import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

import java.util.Arrays;
import java.util.List;

import dev.ahmdaeyz.quizly.common.enums.QuestionType;
@Parcel
public class MultipleRightChoicesQuestion extends Question {
    private List<String> answers;
    private List<String> rightAnswers;
    @ParcelConstructor
    public MultipleRightChoicesQuestion(String text, String rightAnswer,List<String> answers) {
        super(text, rightAnswer, QuestionType.MRC);
        this.rightAnswers = rightAnswersFromString(rightAnswer);
        this.answers = answers;
    }

    private List<String> rightAnswersFromString(String answers){
        return Arrays.asList(answers.split(","));
    }

    public List<String> getRightAnswers() {
        return rightAnswers;
    }

    public List<String> getAnswers() {
        return answers;
    }
}

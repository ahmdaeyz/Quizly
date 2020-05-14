package dev.ahmdaeyz.quizly.model;

import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

import java.util.List;

import dev.ahmdaeyz.quizly.common.enums.QuestionType;
@Parcel
public class MultipleChoicesQuestion extends Question {

    private List<String> answers;

    public List<String> getAnswers() {
        return answers;
    }

    @ParcelConstructor
    public MultipleChoicesQuestion(String text, String rightAnswer, List<String> answers) {
        super(text, rightAnswer, QuestionType.MCQ);
        this.answers = answers;
    }
}

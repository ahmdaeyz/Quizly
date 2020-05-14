package dev.ahmdaeyz.quizly.data;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.content.res.TypedArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import dev.ahmdaeyz.quizly.R;
import dev.ahmdaeyz.quizly.common.enums.QuestionType;
import dev.ahmdaeyz.quizly.model.MultipleChoicesQuestion;
import dev.ahmdaeyz.quizly.model.MultipleRightChoicesQuestion;
import dev.ahmdaeyz.quizly.model.Question;
import dev.ahmdaeyz.quizly.model.QuestionsProvider;
import dev.ahmdaeyz.quizly.model.TextQuestion;

public class InAppQuestionsProviderImpl implements QuestionsProvider {

    public InAppQuestionsProviderImpl(Resources resources) {
        this.resources = resources;
    }

    private Resources resources;


    @Override
    public List<Question> getQuestions() {
        TypedArray questionsFromRes = resources.obtainTypedArray(R.array.questions);
        List<Question> questionsList = new ArrayList<Question>();
        for(int i =0; i< questionsFromRes.length(); ++i){
            String questionText, rightAnswer = "";
            QuestionType questionType;
            List<String> answers;
            TypedArray currentQuestion = resources.obtainTypedArray(questionsFromRes.getResourceId(i,0));
            int questionTextArrayResId = currentQuestion.getResourceId(0,0);
            @SuppressLint("ResourceType") int answersArrayResId = currentQuestion.getResourceId(1,0);
            List<String> questionTextArrayList = Arrays.asList(resources.getStringArray(questionTextArrayResId));
            List<String> questionAnswersList =  Arrays.asList(resources.getStringArray(answersArrayResId));
            questionType = getQuestionType(questionTextArrayList.get(0));
            questionText = questionTextArrayList.get(1);
            rightAnswer = questionTextArrayList.get(2);
            answers = questionAnswersList;
            questionsList.add(
                    getQuestion(questionType,questionText,rightAnswer,answers)
            );
            currentQuestion.recycle();
        }
        questionsFromRes.recycle();
        return questionsList;
    }

    private Question getQuestion(QuestionType questionType, String questionText, String rightAnswer, List<String> answers) {
        switch (questionType){
            case MCQ:
                return new MultipleChoicesQuestion(questionText,rightAnswer,answers);
            case MRC:
                return new MultipleRightChoicesQuestion(questionText,rightAnswer, answers);
            case TEXT:
                return new TextQuestion(questionText,rightAnswer);
        }
        return null;
    }

    private QuestionType getQuestionType(String type){
        return QuestionType.valueOf(type.toUpperCase());
    }
}

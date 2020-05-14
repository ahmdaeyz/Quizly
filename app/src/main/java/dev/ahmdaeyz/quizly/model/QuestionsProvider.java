package dev.ahmdaeyz.quizly.model;

import java.util.List;

import dev.ahmdaeyz.quizly.model.Question;

public interface QuestionsProvider {
    List<Question> getQuestions();
}

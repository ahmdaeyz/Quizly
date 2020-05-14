package dev.ahmdaeyz.quizly.data;

import android.content.res.Resources;

import java.util.List;

import dev.ahmdaeyz.quizly.model.Question;
import dev.ahmdaeyz.quizly.model.QuestionsProvider;

public class QuestionsStore {
    private static QuestionsStore questionsStore = null;
    private List<Question> questions;


    private QuestionsStore(List<Question> questions){
        this.questions = questions;
    }

    public static QuestionsStore getInstance(){
        if(questionsStore == null){
            throw new AssertionError("Please call initQuestions First.");
        }
        return questionsStore;
    }

    public static QuestionsStore initQuestions(Resources resources){
       if (questionsStore != null){
           return questionsStore;
       }
        QuestionsProvider questionsProvider = new InAppQuestionsProviderImpl(resources);
        return new QuestionsStore(questionsProvider.getQuestions());
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public int scoreQuestions(List<Question> questions){
        int score = 0;
        for (Question q: questions){
          if (q.getUserAnswer() != null){
              if (q.getUserAnswer().equals(q.getRightAnswer()))
                  score++;
          }
        }
        return score;
    }
}

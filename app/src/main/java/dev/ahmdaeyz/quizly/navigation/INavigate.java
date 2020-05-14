package dev.ahmdaeyz.quizly.navigation;

public interface INavigate {
    public interface INavigateFromQuiz {
        void toResult(int score,String userName);
    }
    public interface INavigateFromResult {
        void toQuiz(String name);
        void toWelcome();
    }
    public interface INavigateFromWelcome {
        void toQuiz(String userName);
    }
}

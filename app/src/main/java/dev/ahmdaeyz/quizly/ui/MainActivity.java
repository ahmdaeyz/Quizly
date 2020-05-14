package dev.ahmdaeyz.quizly.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import dev.ahmdaeyz.quizly.R;
import dev.ahmdaeyz.quizly.ui.navigation.INavigate;
import dev.ahmdaeyz.quizly.ui.quiz.QuizFragment;
import dev.ahmdaeyz.quizly.ui.result.ResultFragment;
import dev.ahmdaeyz.quizly.ui.welcome.WelcomeFragment;

import static dev.ahmdaeyz.quizly.ui.welcome.WelcomeFragment.USER_NAME;

public class MainActivity extends AppCompatActivity implements INavigate.INavigateFromQuiz,INavigate.INavigateFromResult,INavigate.INavigateFromWelcome {
    public static final String IS_IN_QUIZ = "isInQuiz";
    FragmentManager fragmentManager =  getSupportFragmentManager();
    private WelcomeFragment welcomeFragment;
    private QuizFragment quizFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(savedInstanceState!=null) {
            welcomeFragment = (WelcomeFragment) fragmentManager.getFragment(savedInstanceState, "WelcomeFragment");
            quizFragment = (QuizFragment) fragmentManager.getFragment(savedInstanceState,"QuizFragment");
        }

        if (!isInQuiz()){
            if (welcomeFragment==null) {
                welcomeFragment = new WelcomeFragment();
                fragmentManager
                        .beginTransaction()
                        .add(R.id.fragment_container,welcomeFragment)
                        .commit();
            }
        }else{
            if (quizFragment==null){
                String userName = getUserNameIfExists();
                quizFragment = QuizFragment.newInstance(userName);
            }
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.fragment_container,quizFragment)
                    .commit();
        }

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (welcomeFragment!=null && fragmentManager.getFragments().get(0).getClass() == WelcomeFragment.class) {
            fragmentManager.putFragment(outState, "WelcomeFragment", welcomeFragment);
        }
        if (quizFragment!=null &&  fragmentManager.getFragments().get(0).getClass() == QuizFragment.class) {
            fragmentManager.putFragment(outState, "QuizFragment", quizFragment);
        }
    }

    @Override
    public void toResult(int score, String userName) {
        ResultFragment resultFragment = ResultFragment.newInstance(score,userName);
        fragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container,resultFragment)
                .commit();
    }

    @Override
    public void toQuiz(String name) {
        QuizFragment quizFragment = QuizFragment.newInstance(name);
        fragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container,quizFragment)
                .commit();
        indicateInQuiz();
    }

    @Override
    public void toWelcome() {
        WelcomeFragment welcomeFragment = new WelcomeFragment();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container,welcomeFragment)
                .commit();
    }
    private void indicateInQuiz(){
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit()
        .putBoolean(IS_IN_QUIZ,true);
        editor.apply();
    }
    private Boolean isInQuiz(){
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        return sharedPref.getBoolean(IS_IN_QUIZ,false);
    }
    private String getUserNameIfExists(){
        SharedPreferences sharedPreferences = this.getPreferences(Context.MODE_PRIVATE);
        return sharedPreferences.getString(USER_NAME,"");
    }
}

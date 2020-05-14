package dev.ahmdaeyz.quizly.ui.quiz;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jakewharton.rxbinding3.viewpager2.PageScrollEvent;
import com.jakewharton.rxbinding3.viewpager2.RxViewPager2;

import org.parceler.Parcels;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import dev.ahmdaeyz.quizly.databinding.FragmentQuizBinding;
import dev.ahmdaeyz.quizly.model.Question;
import dev.ahmdaeyz.quizly.data.QuestionsStore;
import dev.ahmdaeyz.quizly.ui.navigation.INavigate;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.BehaviorSubject;

import static dev.ahmdaeyz.quizly.ui.MainActivity.IS_IN_QUIZ;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link QuizFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class QuizFragment extends Fragment {

    public static final String QUESTIONS = "Questions";
    private static final String ARG_USERNAME = "QUIZ_USER_NAME";
    private static final String CURRENT_POSITION = "CURRENT_POSITION";
    private INavigate.INavigateFromQuiz navigateFromQuiz;
    private FragmentQuizBinding binding;
    static CompositeDisposable disposables = new CompositeDisposable();
    private BehaviorSubject<Integer> position = BehaviorSubject.create();
    private BehaviorSubject<List<Question>> questions;
    private List<Question> questionsList;
    private String userName;
    private QuestionsStore store;
    private QuestionsViewPagerAdapter questionAdapter;
    public QuizFragment() {
        // Required empty public constructor
    }

    public static QuizFragment newInstance(String userName) {
        QuizFragment fragment = new QuizFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USERNAME, userName);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            navigateFromQuiz = (INavigate.INavigateFromQuiz) context;
        }catch (ClassCastException e){
            Log.e("WelcomeFragment","Must Implement IQuizFragmentCallback");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userName = getArguments().getString(ARG_USERNAME);
        }else {
            Log.i("QuizFragment","Null yaba");
        }
        if (savedInstanceState!=null){
            questionsList = Parcels.unwrap(savedInstanceState.getParcelable(QUESTIONS));
        }
        store = QuestionsStore.initQuestions(getResources());
        if (questionsList == null){
            questionsList = store.getQuestions();
        }
        questions = BehaviorSubject.createDefault(questionsList);
        questionAdapter = new QuestionsViewPagerAdapter(questionsList,questions);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentQuizBinding.inflate(inflater, container, false);
        binding.submitButton
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int score = calculateUserScore();
                        questions.onComplete();
                        position.onComplete();
                        navigateFromQuiz.toResult(score,userName);
                        indicateQuizFinished();
                    }
                });
        return binding.getRoot();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (questions !=null){
            outState.putParcelable(QUESTIONS, Parcels.wrap(questions.getValue()));
        }
        if (position!=null){
            outState.putInt(CURRENT_POSITION,position.getValue());
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState!=null){
            int currentIndex = savedInstanceState.getInt(CURRENT_POSITION);
            Log.d("CurrentIndex",currentIndex+"");
            binding.questionsPager.setCurrentItem(currentIndex,true);
        }
        binding.questionsPager.setAdapter(questionAdapter);

        disposables.add(
                RxViewPager2.pageScrollEvents(binding.questionsPager)
                        .debounce(200, TimeUnit.MILLISECONDS)
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<PageScrollEvent>() {
                            @Override
                            public void accept(PageScrollEvent pageScrollEvent) throws Exception {
                                int currentPosition = pageScrollEvent.getPosition();
                                position.onNext(currentPosition);
                                Log.d("CurrentPosition", currentPosition + "");
                                if (currentPosition == questionsList.size()-1){
                                    binding.submitButton.setVisibility(View.VISIBLE);
                                    Log.d("Should","be visible");
                                }
                                else{
                                    binding.submitButton.setVisibility(View.GONE);
                                    Log.d("Should","be gone");
                                }
                            }
                        })
        );

        binding.stepIndicator.setViewPager2(binding.questionsPager);
    }
    private void indicateQuizFinished() {
        SharedPreferences sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit()
                .putBoolean(IS_IN_QUIZ,false);
        editor.apply();
    }

    private int calculateUserScore() {
        return store.scoreQuestions(questions.getValue());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        disposables.clear();
    }

}

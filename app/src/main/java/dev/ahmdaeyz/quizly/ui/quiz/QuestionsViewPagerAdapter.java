package dev.ahmdaeyz.quizly.ui.quiz;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxCompoundButton;
import com.jakewharton.rxbinding2.widget.RxTextView;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import dev.ahmdaeyz.quizly.common.reactiveextenstions.RxCustomCheckbox;
import dev.ahmdaeyz.quizly.databinding.McqViewPagerItemBinding;
import dev.ahmdaeyz.quizly.databinding.MrcViewPagerItemBinding;
import dev.ahmdaeyz.quizly.databinding.TextViewPagerItemBinding;
import dev.ahmdaeyz.quizly.model.MultipleChoicesQuestion;
import dev.ahmdaeyz.quizly.model.MultipleRightChoicesQuestion;
import dev.ahmdaeyz.quizly.model.Question;
import dev.ahmdaeyz.quizly.model.TextQuestion;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.BehaviorSubject;

import static dev.ahmdaeyz.quizly.ui.quiz.QuizFragment.disposables;

public class QuestionsViewPagerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int MCQ_TYPE = 0;
    private static final int MRC_TYPE = 1;
    private static final int TEXT_TYPE = 2;
    private BehaviorSubject<List<Question>> questions;
    private List<Question> questionsList = new ArrayList<Question>();

    public QuestionsViewPagerAdapter(List<Question> questionsList,BehaviorSubject<List<Question>> questions) {
        this.questionsList = questionsList;
        this.questions = questions;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType){
            case MCQ_TYPE:
                return MCQViewHolder.from(parent,questions);
            case MRC_TYPE:
                return MRCViewHolder.from(parent,questions);
            case TEXT_TYPE:
                return TextViewHolder.from(parent,questions);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Question question = questionsList.get(position);
        switch (question.getType()){
            case MCQ:
                try {
                    holder.getClass().getMethod("bind", MultipleChoicesQuestion.class, int.class).invoke(holder, question, position);
                } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    e.printStackTrace();
                }
                break;
            case MRC:
                try {
                    holder.getClass().getMethod("bind", MultipleRightChoicesQuestion.class, int.class).invoke(holder, question, position);
                } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                    e.printStackTrace();
                }
                break;
            case TEXT:
                try {
                    holder.getClass().getMethod("bind", TextQuestion.class, int.class).invoke(holder, question, position);
                } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    public int getItemCount() {
        return questionsList.size();
    }

    @Override
    public int getItemViewType(int position) {
        Question question = questionsList.get(position);
        switch (question.getType()){
            case MCQ:
                return MCQ_TYPE;
            case MRC:
                return MRC_TYPE;
            case TEXT:
                return TEXT_TYPE;
        }
        return 0;
    }

    static class MCQViewHolder extends RecyclerView.ViewHolder{
        private McqViewPagerItemBinding binding;
        private BehaviorSubject<List<Question>> questions;
        private MCQViewHolder(@NonNull McqViewPagerItemBinding binding,BehaviorSubject<List<Question>> questions) {
            super(binding.getRoot());
            this.binding = binding;
            this.questions = questions;
        }
        public static MCQViewHolder from(ViewGroup parent, BehaviorSubject<List<Question>> questions) {
            McqViewPagerItemBinding binding = McqViewPagerItemBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
            return new MCQViewHolder(binding,questions);
        }

        public void bind(MultipleChoicesQuestion question,int position){
            binding.questionText.setText(question.getText());
            binding.firstMcqChoice.answerText.setText(question.getAnswers().get(0));
            binding.secondMcqChoice.answerText.setText(question.getAnswers().get(1));
            binding.thirdMcqChoice.answerText.setText(question.getAnswers().get(2));
            if (!questions.getValue().get(position).getUserAnswer().equals("")){
                switch (questions.getValue().get(position).getUserAnswer()){
                    case "0":
                        binding.firstMcqChoice.answerRadioButton.setChecked(true);
                        break;
                    case "1":
                        binding.secondMcqChoice.answerRadioButton.setChecked(true);
                        break;
                    case "2":
                        binding.thirdMcqChoice.answerRadioButton.setChecked(true);
                        break;
                }
            }
            observeChoicesRadioButtons(position);
            observeChoices(position);
        }

        private void observeChoicesRadioButtons(int position){
            disposables.add(
                    RxCompoundButton.checkedChanges(binding.firstMcqChoice.answerRadioButton)
                            .subscribe(new Consumer<Boolean>() {
                                @Override
                                public void accept(Boolean isChecked) throws Exception {
                                    if (isChecked){
                                        binding.secondMcqChoice.answerRadioButton.setChecked(false);
                                        binding.thirdMcqChoice.answerRadioButton.setChecked(false);
                                        questions.getValue().get(position).setUserAnswer(0+"");
                                        questions.onNext(questions.getValue());
                                    }
                                }
                            })
            );

            disposables.add(
                    RxCompoundButton.checkedChanges(binding.secondMcqChoice.answerRadioButton)
                            .subscribe(new Consumer<Boolean>() {
                                @Override
                                public void accept(Boolean isChecked) throws Exception {
                                    if (isChecked){
                                        binding.firstMcqChoice.answerRadioButton.setChecked(false);
                                        binding.thirdMcqChoice.answerRadioButton.setChecked(false);
                                        questions.getValue().get(position).setUserAnswer(1+"");
                                        questions.onNext(questions.getValue());
                                    }
                                }
                            })
            );

            disposables.add(
                    RxCompoundButton.checkedChanges(binding.thirdMcqChoice.answerRadioButton)
                            .subscribe(new Consumer<Boolean>() {
                                @Override
                                public void accept(Boolean isChecked) throws Exception {
                                    if (isChecked){
                                        binding.secondMcqChoice.answerRadioButton.setChecked(false);
                                        binding.firstMcqChoice.answerRadioButton.setChecked(false);
                                        questions.getValue().get(position).setUserAnswer(2+"");
                                        questions.onNext(questions.getValue());
                                    }
                                }
                            })
            );
        }
        private void observeChoices(int position) {
            disposables.add(RxView.clicks(binding.firstMcqChoice.getRoot())
                    .subscribe(new Consumer<Object>() {
                        @Override
                        public void accept(Object o) throws Exception {
                            binding.firstMcqChoice.answerRadioButton.setChecked(true);
                            binding.secondMcqChoice.answerRadioButton.setChecked(false);
                            binding.thirdMcqChoice.answerRadioButton.setChecked(false);
                            questions.getValue().get(position).setUserAnswer(0+"");
                            questions.onNext(questions.getValue());
                        }
                    }));

            disposables.add(RxView.clicks(binding.secondMcqChoice.getRoot())
                    .subscribe(new Consumer<Object>() {
                        @Override
                        public void accept(Object o) throws Exception {
                            binding.firstMcqChoice.answerRadioButton.setChecked(false);
                            binding.secondMcqChoice.answerRadioButton.setChecked(true);
                            binding.thirdMcqChoice.answerRadioButton.setChecked(false);
                            questions.getValue().get(position).setUserAnswer(1+"");
                            questions.onNext(questions.getValue());
                        }
                    }));

            disposables.add(RxView.clicks(binding.thirdMcqChoice.getRoot())
                    .subscribe(new Consumer<Object>() {
                        @Override
                        public void accept(Object o) throws Exception {
                            binding.firstMcqChoice.answerRadioButton.setChecked(false);
                            binding.secondMcqChoice.answerRadioButton.setChecked(false);
                            binding.thirdMcqChoice.answerRadioButton.setChecked(true);
                            questions.getValue().get(position).setUserAnswer(2+"");
                            questions.onNext(questions.getValue());
                        }
                    }));
        }

    }

    static class MRCViewHolder extends RecyclerView.ViewHolder {
        private MrcViewPagerItemBinding binding;
        private BehaviorSubject<List<Question>> questions;

        public MRCViewHolder(@NonNull MrcViewPagerItemBinding binding,BehaviorSubject<List<Question>> questions) {
            super(binding.getRoot());
            this.binding = binding;
            this.questions = questions;
        }

        public static MRCViewHolder from(ViewGroup parent, BehaviorSubject<List<Question>> questions) {
            MrcViewPagerItemBinding binding = MrcViewPagerItemBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
            return new MRCViewHolder(binding,questions);
        }

        public void bind(MultipleRightChoicesQuestion question, int position) {
            binding.questionText.setText(question.getText());
            binding.firstChoice.answerText.setText(question.getAnswers().get(0));
            binding.secondChoice.answerText.setText(question.getAnswers().get(1));
            binding.thirdChoice.answerText.setText(question.getAnswers().get(2));
            if (!questions.getValue().get(position).getUserAnswer().equals("")){
                switch (questions.getValue().get(position).getUserAnswer()){
                    case "0":
                        binding.firstChoice.answerCheckBox.setChecked(true);
                        break;
                    case "1":
                        binding.secondChoice.answerCheckBox.setChecked(true);
                        break;
                    case "2":
                        binding.thirdChoice.answerCheckBox.setChecked(true);
                        break;
                }
            }
            ObserveCheckedChanges(position);
        }

        private void ObserveCheckedChanges(int position) {
            disposables.add(RxCustomCheckbox.checkedChanges(binding.firstChoice.answerCheckBox)
                    .skipInitialValue()
                    .subscribe(new Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean isChecked) throws Exception {
                            if (isChecked){
                                String currentAnswer = questions.getValue().get(position).getUserAnswer();
                                questions.getValue()
                                        .get(position)
                                        .setUserAnswer(currentAnswer+","+"0");
                                questions.onNext(questions.getValue());
                            }else{
                                String currentAnswer = questions.getValue().get(position).getUserAnswer();
                                questions.getValue()
                                        .get(position)
                                        .setUserAnswer(currentAnswer
                                                .replace(",0","")
                                                .replace("0","")

                                        );
                                questions.onNext(questions.getValue());
                            }

                        }
                    }));

            disposables.add(RxCustomCheckbox.checkedChanges(binding.secondChoice.answerCheckBox)
                    .skipInitialValue()
                    .subscribe(new Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean isChecked) throws Exception {
                            if (isChecked){
                                String currentAnswer = questions.getValue().get(position).getUserAnswer();
                                questions.getValue().get(position).setUserAnswer(currentAnswer+","+"1");
                                questions.onNext(questions.getValue());
                            }else{
                                String currentAnswer = questions.getValue().get(position).getUserAnswer();
                                questions.getValue()
                                        .get(position)
                                        .setUserAnswer(currentAnswer
                                                .replace(",1","")
                                                .replace("1",""));
                                questions.onNext(questions.getValue());
                            }
                        }
                    }));

            disposables.add(RxCustomCheckbox.checkedChanges(binding.thirdChoice.answerCheckBox)
                    .skipInitialValue()
                    .subscribe(new Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean isChecked) throws Exception {
                            if (isChecked) {
                                String currentAnswer = questions.getValue().get(position).getUserAnswer();
                                questions.getValue()
                                        .get(position)
                                        .setUserAnswer(currentAnswer + "," + "2");
                                questions.onNext(questions.getValue());
                            } else {
                                String currentAnswer = questions.getValue().get(position).getUserAnswer();
                                questions.getValue()
                                        .get(position)
                                        .setUserAnswer(currentAnswer
                                                .replace(",2", "")
                                                .replace("2",""));
                                questions.onNext(questions.getValue());
                            }
                        }
                    }));
        }
    }

    static class TextViewHolder extends RecyclerView.ViewHolder {
        private TextViewPagerItemBinding binding;
        private BehaviorSubject<List<Question>> questions;

        public TextViewHolder(@NonNull TextViewPagerItemBinding binding, BehaviorSubject<List<Question>> questions) {
            super(binding.getRoot());
            this.binding = binding;
            this.questions = questions;
        }

        public static TextViewHolder from(ViewGroup parent, BehaviorSubject<List<Question>> questions) {
            TextViewPagerItemBinding binding = TextViewPagerItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new TextViewHolder(binding, questions);
        }

        public void bind(TextQuestion question, int position) {
            binding.questionText.setText(question.getText());
            if (!questions.getValue().get(position).getUserAnswer().equals("")){
                binding.userAnswer.answerText.setText(questions.getValue().get(position).getUserAnswer());
            }

            disposables.add(
                    RxTextView.textChanges(binding.userAnswer.answerText)
                            .skipInitialValue()
                            .debounce(300, TimeUnit.MILLISECONDS)
                            .subscribe(new Consumer<CharSequence>() {
                                @Override
                                public void accept(CharSequence charSequence) throws Exception {
                                    questions.getValue().get(position).setUserAnswer(charSequence.toString().trim());
                                    questions.onNext(questions.getValue());
                                }
                            }));

        }
    }
}

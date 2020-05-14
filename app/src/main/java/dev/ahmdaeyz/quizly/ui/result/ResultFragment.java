package dev.ahmdaeyz.quizly.ui.result;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import dev.ahmdaeyz.quizly.R;
import dev.ahmdaeyz.quizly.databinding.FragmentResultBinding;
import dev.ahmdaeyz.quizly.navigation.INavigate;

import static dev.ahmdaeyz.quizly.ui.welcome.WelcomeFragment.USER_NAME;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ResultFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ResultFragment extends Fragment {
    private static final String ARG_RESULT = "result";
    private static final String ARG_USER_NAME = "username";
    private INavigate.INavigateFromResult navigateFromResult;
    private int result;
    private String userName;
    private FragmentResultBinding binding;
    public ResultFragment() {
        // Required empty public constructor
    }

    public static ResultFragment newInstance(int result, String userName) {
        ResultFragment fragment = new ResultFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_RESULT, result);
        args.putString(ARG_USER_NAME, userName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            navigateFromResult = (INavigate.INavigateFromResult) context;
        }catch (ClassCastException e){
            Log.e("WelcomeFragment","Must Implement IResultFragmentCallback");
        }
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            result = getArguments().getInt(ARG_RESULT);
            userName = getArguments().getString(ARG_USER_NAME);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentResultBinding.inflate(inflater,container,false);
        binding.greetingText.setText(getString(R.string.greeting_text,userName));
        binding.userScore.setText(result+ "");
        binding.playAgainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateFromResult.toQuiz(userName);
            }
        });
        binding.newPlayerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateFromResult.toWelcome();
                removeNameFromSharedPreferences();
            }

        });
        Toast
                .makeText(
                        requireContext(),
                        "Your score: "+ result,
                        Toast.LENGTH_SHORT
                ).show();
        return binding.getRoot();
    }

    private void removeNameFromSharedPreferences() {
        SharedPreferences sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE);
        sharedPreferences.edit()
                .putString(USER_NAME,"")
                .apply();
    }
}

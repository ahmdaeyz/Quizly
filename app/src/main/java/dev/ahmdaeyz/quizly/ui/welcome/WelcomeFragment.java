package dev.ahmdaeyz.quizly.ui.welcome;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.textfield.TextInputLayout;

import dev.ahmdaeyz.quizly.R;
import dev.ahmdaeyz.quizly.ui.navigation.INavigate;

public class WelcomeFragment extends Fragment {

    public static final String USER_NAME = "userName";
    private String userName;
    private INavigate.INavigateFromWelcome navigateFromWelcome;
    public WelcomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            navigateFromWelcome = (INavigate.INavigateFromWelcome) context;
        }catch (ClassCastException e){
            Log.e("WelcomeFragment","Must Implement IWelcomeFragmentCallback");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View fragmentView = inflater.inflate(R.layout.fragment_welcome, container, false);
        Button goToQuiz = fragmentView.findViewById(R.id.go_to_quiz_button);
        goToQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextInputLayout userText = fragmentView.findViewById(R.id.name_text_field);
                final String username = userText.getEditText().getText().toString();
                userName = username;
                navigateFromWelcome.toQuiz(userName);
                saveNameToSharedPreferences();
            }
        });
        return fragmentView;
        }

    private void saveNameToSharedPreferences() {
        SharedPreferences sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit()
                .putString(USER_NAME,userName);
        editor.apply();
    }
}

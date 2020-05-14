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

import com.jakewharton.rxbinding2.widget.RxTextView;
import com.mikhaellopez.rxanimation.RxAnimation;
import com.mikhaellopez.rxanimation.RxAnimationExtensionKt;

import dev.ahmdaeyz.quizly.databinding.FragmentWelcomeBinding;
import dev.ahmdaeyz.quizly.navigation.INavigate;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;

public class WelcomeFragment extends Fragment {

    public static final String USER_NAME = "userName";
    private FragmentWelcomeBinding binding;
    private String userName;
    private INavigate.INavigateFromWelcome navigateFromWelcome;
    private CompositeDisposable disposables = new CompositeDisposable();
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
        binding = FragmentWelcomeBinding.inflate(inflater,container,false);
        disposables.add(RxTextView.textChanges(binding.nameEditTextField)
                .filter((charSequence -> !charSequence.toString().equals("")))
                .subscribe(new Consumer<CharSequence>() {
                    @Override
                    public void accept(CharSequence charSequence) throws Exception {
                        userName = charSequence.toString();
                    }
                }));
        binding.goToQuizButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userName != null){
                    navigateFromWelcome.toQuiz(userName);
                    saveNameToSharedPreferences();
                }else{
                    disposables.add(RxAnimationExtensionKt
                            .shake(RxAnimation.INSTANCE
                                    .from(binding.nameEditTextField)
                                    ,100,5f,10f)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Consumer<View>() {
                                @Override
                                public void accept(View view) throws Exception {
                                    binding.nameTextField.setError("Name can't be empty!");
                                }
                            }));

                }
            }
        });
        return binding.getRoot();
        }

    private void saveNameToSharedPreferences() {
        SharedPreferences sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit()
                .putString(USER_NAME,userName);
        editor.apply();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        disposables.clear();
    }
}

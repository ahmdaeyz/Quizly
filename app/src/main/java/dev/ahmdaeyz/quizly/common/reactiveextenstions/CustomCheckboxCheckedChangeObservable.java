package dev.ahmdaeyz.quizly.common.reactiveextenstions;

import android.annotation.SuppressLint;
import android.widget.CompoundButton;

import com.jakewharton.rxbinding2.InitialValueObservable;

import net.igenius.customcheckbox.CustomCheckBox;

import io.reactivex.Observer;
import io.reactivex.android.MainThreadDisposable;

import static com.jakewharton.rxbinding2.internal.Preconditions.checkMainThread;

final class CustomCheckboxCheckedChangeObservable extends InitialValueObservable<Boolean> {
    private final CustomCheckBox view;

    CustomCheckboxCheckedChangeObservable(CustomCheckBox view) {
        this.view = view;
    }

    @SuppressLint("RestrictedApi")
    @Override protected void subscribeListener(Observer<? super Boolean> observer) {
        if (!checkMainThread(observer)) {
            return;
        }
        CustomCheckboxCheckedChangeObservable.Listener listener = new CustomCheckboxCheckedChangeObservable.Listener(view, observer);
        observer.onSubscribe(listener);
        view.setOnCheckedChangeListener(listener);
    }

    @Override protected Boolean getInitialValue() {
        return view.isChecked();
    }

    static final class Listener extends MainThreadDisposable implements CustomCheckBox.OnCheckedChangeListener {
        private final CustomCheckBox view;
        private final Observer<? super Boolean> observer;

        Listener(CustomCheckBox view, Observer<? super Boolean> observer) {
            this.view = view;
            this.observer = observer;
        }

        @Override
        protected void onDispose() {
            view.setOnCheckedChangeListener(null);
        }

        @Override
        public void onCheckedChanged(CustomCheckBox checkBox, boolean isChecked) {
            if (!isDisposed()) {
                observer.onNext(isChecked);
            }
        }
    }
}

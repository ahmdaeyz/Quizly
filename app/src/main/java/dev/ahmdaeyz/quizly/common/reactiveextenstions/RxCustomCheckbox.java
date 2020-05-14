package dev.ahmdaeyz.quizly.common.reactiveextenstions;

import android.annotation.SuppressLint;
import android.widget.CompoundButton;

import androidx.annotation.CheckResult;
import androidx.annotation.NonNull;

import com.jakewharton.rxbinding2.InitialValueObservable;

import net.igenius.customcheckbox.CustomCheckBox;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;

import static com.jakewharton.rxbinding2.internal.Preconditions.checkNotNull;


/**
 * Static factory methods for creating {@linkplain Observable observables} and {@linkplain Consumer
 * actions} for {@link net.igenius.customcheckbox.CustomCheckBox}.
 */
public final class RxCustomCheckbox {
    /**
     * Create an observable of booleans representing the checked state of {@code view}.
     * <p>
     * <em>Warning:</em> The created observable keeps a strong reference to {@code view}. Unsubscribe
     * to free this reference.
     * <p>
     * <em>Warning:</em> The created observable uses {@link CompoundButton#setOnCheckedChangeListener}
     * to observe checked changes. Only one observable can be used for a view at a time.
     * <p>
     * <em>Note:</em> A value will be emitted immediately on subscribe.
     */
    @SuppressLint("RestrictedApi")
    @CheckResult @NonNull
    public static InitialValueObservable<Boolean> checkedChanges(@NonNull CustomCheckBox view) {
        checkNotNull(view, "view == null");
        return new CustomCheckboxCheckedChangeObservable(view);
    }

    /**
     * An action which sets the checked property of {@code view}.
     * <p>
     * <em>Warning:</em> The created observable keeps a strong reference to {@code view}. Unsubscribe
     * to free this reference.
     *
     * @deprecated Use view::setChecked method reference.
     */
    @SuppressLint("RestrictedApi")
    @Deprecated
    @CheckResult
    @NonNull
    public static Consumer<? super Boolean> checked(@NonNull CustomCheckBox view) {
        checkNotNull(view, "view == null");
        return view::setChecked;
    }

    /**
     * An action which sets the toggles property of {@code view} with each value.
     * <p>
     * <em>Warning:</em> The created observable keeps a strong reference to {@code view}. Unsubscribe
     * to free this reference.
     */
    @SuppressLint("RestrictedApi")
    @CheckResult @NonNull
    public static Consumer<? super Object> toggle(@NonNull CustomCheckBox view) {
        checkNotNull(view, "view == null");
        return value -> view.toggle();
    }

    private RxCustomCheckbox() {
        throw new AssertionError("No instances.");
    }
}

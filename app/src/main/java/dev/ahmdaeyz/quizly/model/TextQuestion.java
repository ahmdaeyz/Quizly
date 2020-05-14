package dev.ahmdaeyz.quizly.model;

        import org.parceler.Parcel;
        import org.parceler.ParcelConstructor;

        import dev.ahmdaeyz.quizly.common.enums.QuestionType;
@Parcel
public class TextQuestion extends Question {
    @ParcelConstructor
    public TextQuestion(String text, String rightAnswer) {
        super(text, rightAnswer, QuestionType.TEXT);
    }
}

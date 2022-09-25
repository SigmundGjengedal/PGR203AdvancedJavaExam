package no.kristiania.options;

public class Option {


    private String text;
    private int questionId;
    private long id;

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public int getQuestionId() {
        return questionId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}

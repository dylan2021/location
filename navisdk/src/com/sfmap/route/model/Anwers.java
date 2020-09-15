package com.sfmap.route.model;

public class Anwers {
    private String question_seq;
    private String template_id;
    private String answer_type;
    private String answer_txt;

    public String getQuestion_seq() {
        return question_seq;
    }

    public void setQuestion_seq(String question_seq) {
        this.question_seq = question_seq;
    }

    public String getTemplate_id() {
        return template_id;
    }

    public void setTemplate_id(String template_id) {
        this.template_id = template_id;
    }

    public String getAnswer_type() {
        return answer_type;
    }

    public void setAnswer_type(String answer_type) {
        this.answer_type = answer_type;
    }

    public String getAnswer_txt() {
        return answer_txt;
    }

    public void setAnswer_txt(String answer_txt) {
        this.answer_txt = answer_txt;
    }
}
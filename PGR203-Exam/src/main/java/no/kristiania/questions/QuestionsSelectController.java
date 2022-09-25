package no.kristiania.questions;

import no.kristiania.http.HttpController;
import no.kristiania.http.HttpMessage;

import java.sql.SQLException;

public class QuestionsSelectController implements HttpController {

    private final QuestionDao questionDao;

    public QuestionsSelectController(QuestionDao qDao) {
        this.questionDao = qDao;
    }

    @Override
    public HttpMessage handle(HttpMessage request) throws SQLException {
        String response = "";

        for (Question question : questionDao.listAll()) {
            response +=  "<option value=" + question.getId() + ">"+question.getText() +"</option>";
        }
        return new HttpMessage("HTTP/1.1 200 ok",response);
    }
}

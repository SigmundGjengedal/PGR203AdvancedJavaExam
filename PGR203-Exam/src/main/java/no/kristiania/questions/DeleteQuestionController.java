package no.kristiania.questions;

import no.kristiania.http.HttpController;
import no.kristiania.http.HttpMessage;
import java.sql.SQLException;
import java.util.Map;

import static no.kristiania.http.HttpServer.responseWith500;
import static no.kristiania.http.HttpServer.responseWithLocation;

public class DeleteQuestionController implements HttpController {
    private final QuestionDao questionDao;

    public DeleteQuestionController(QuestionDao questionDao) {
        this.questionDao = questionDao;
    }

    @Override
    public HttpMessage handle(HttpMessage request) throws SQLException {

        boolean dbHasNoQuestions = questionDao.listAll().isEmpty();
        if(!dbHasNoQuestions) {
            // fetching data:
            Map<String, String> queryMap = HttpMessage.parseRequestParameters(request.messageBody);
            long questionID = Long.parseLong(queryMap.get("id")); // caster til long
            Question question = questionDao.retrieveById(questionID); // henter eksisterende spørsmål
            questionDao.delete(question);
            return responseWithLocation("/index.html");
        }
        else {
            String errorMessage = "<p>No questions registered in Database </p>"
                    ;
            return responseWith500(errorMessage);
        }
    }
}

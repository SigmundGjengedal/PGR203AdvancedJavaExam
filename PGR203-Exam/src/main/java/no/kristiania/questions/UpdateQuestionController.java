package no.kristiania.questions;

import no.kristiania.http.HttpController;
import no.kristiania.http.HttpMessage;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Map;

import static no.kristiania.http.HttpServer.responseWith500;
import static no.kristiania.http.HttpServer.responseWithLocation;

public class UpdateQuestionController implements HttpController {
    private final QuestionDao qDao;

    public UpdateQuestionController(QuestionDao questionDao) {
        this.qDao = questionDao;
    }

    @Override
    public HttpMessage handle(HttpMessage request) throws SQLException {
        // control structure
        boolean dbHasNoQuestions = qDao.listAll().isEmpty();
        if(!dbHasNoQuestions){
            // fetching data:
            Map<String, String> queryMap = HttpMessage.parseRequestParameters(request.messageBody);
            // decoding data utf-8
            String decodedTitle = URLDecoder.decode(queryMap.get("title"), StandardCharsets.UTF_8).trim();
            String decodedText = URLDecoder.decode(queryMap.get("text"), StandardCharsets.UTF_8).trim();
            // updating correct question
            long questionID = Long.parseLong(queryMap.get("id"));
            Question question = qDao.retrieveById(questionID);
            // input handling in logical order.
            if(decodedText.length()<1 && decodedTitle.length()<1){
                String errorMessage = "<p> No title or text entered * </p>";
                return responseWith500(errorMessage);
            }
            if(decodedTitle.length()>0){
                question.setTitle(decodedTitle);
            }
            if(decodedText.length()>0){
                question.setText(decodedText);
            }
            qDao.upDate(question);
            return responseWithLocation("/updateQuestion.html");
        }
        else {
            String errorMessage = "<p>No questions registered in Database </p>";
            return responseWith500(errorMessage);
        }
    }
}

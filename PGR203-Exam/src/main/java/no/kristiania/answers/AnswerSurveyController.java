package no.kristiania.answers;

import no.kristiania.http.HttpController;
import no.kristiania.http.HttpMessage;
import no.kristiania.options.OptionDao;

import java.sql.SQLException;
import java.util.Map;

import static no.kristiania.http.HttpServer.responseWithLocation;

public class AnswerSurveyController implements HttpController {

    private final AnswersDao answerDao;
    private OptionDao optionDao;

    public AnswerSurveyController(AnswersDao answersDao, OptionDao optionDao) {
        this.answerDao = answersDao;
        this.optionDao = optionDao;
    }

    @Override
    public HttpMessage handle(HttpMessage request) throws SQLException {

      // querymap. key: question_id / value: option_id
        Map<String, String> queryMap = HttpMessage.parseRequestParameters(request.messageBody);
        for(var entry: queryMap.entrySet() ){
            int questionID = Integer.parseInt(entry.getKey());
            int optionID = Integer.parseInt(entry.getValue());
            Answer answer =new Answer();
            answer.setOptionId(optionID);
            answer.setQuestionId(questionID);
            answerDao.save(answer);
        }
        return responseWithLocation("/index.html");

    }
}

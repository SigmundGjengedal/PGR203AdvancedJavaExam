package no.kristiania.questions;

import no.kristiania.http.HttpController;
import no.kristiania.http.HttpMessage;
import no.kristiania.surveys.Survey;
import no.kristiania.surveys.SurveyDao;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Map;

import static no.kristiania.http.HttpServer.responseWith500;
import static no.kristiania.http.HttpServer.responseWithLocation;

public class AddQuestionController implements HttpController {

    private final QuestionDao questionDao;
    private SurveyDao<Survey> surveyDao;

    public AddQuestionController(QuestionDao qDao) {
        this.questionDao = qDao;
    }
    public AddQuestionController(QuestionDao qDao, SurveyDao<Survey> surveyDao) {
        this.questionDao = qDao;
        this.surveyDao = surveyDao;

    }

    @Override
    public HttpMessage handle(HttpMessage request) throws SQLException {
        // fetching data:
        Map<String, String> queryMap = HttpMessage.parseRequestParameters(request.messageBody);
        // decoding data utf-8
        String decodedTitle = URLDecoder.decode(queryMap.get("title"), StandardCharsets.UTF_8).trim();
        String decodedText = URLDecoder.decode(queryMap.get("text"), StandardCharsets.UTF_8).trim();
        boolean dbHasZeroSurveys = surveyDao.listAll().isEmpty();
        // setting values to object if values and survey exists
        if(decodedTitle.length()>0 && decodedText.length()>0 && !dbHasZeroSurveys) {
            long surveyID = Long.parseLong(queryMap.get("id"));
            Question question = new Question();
            question.setTitle(decodedTitle);
            question.setText(decodedText);
            question.setSurveyId(surveyID);
            questionDao.save(question);
        return responseWithLocation("/newQuestion.html");
        }else {
            // determining error message
            String errorMessage = dbHasZeroSurveys
                    ?  "<p>No surveys registered in Database </p>"
                    : "<p> fill inn required fields * </p>"
                    ;
            return responseWith500(errorMessage);
        }
    }
}

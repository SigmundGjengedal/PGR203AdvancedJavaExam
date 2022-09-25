package no.kristiania.surveys;

import no.kristiania.http.HttpController;
import no.kristiania.http.HttpMessage;

import java.sql.SQLException;

public class SurveySelectController implements HttpController {

    private final SurveyDao<Survey> surveyDao;

    public SurveySelectController(SurveyDao<Survey> surveyDao) throws SQLException {
        this.surveyDao= surveyDao;
    }

    @Override
    public HttpMessage handle(HttpMessage request) throws SQLException {
        String response = "";
        for (Survey survey : surveyDao.listAll()) {
            response +=  "<option value=" + survey.getSurveyId() + ">"+survey.getTitle() +"</option>";
        }
        return new HttpMessage("HTTP/1.1 200 ok",response);
    }
}

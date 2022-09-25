package no.kristiania.surveys;

import no.kristiania.http.HttpController;
import no.kristiania.http.HttpMessage;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Map;

import static no.kristiania.http.HttpServer.responseWith500;
import static no.kristiania.http.HttpServer.responseWithLocation;

public class AddSurveyController implements HttpController {

    private final SurveyDao surveyDao;

    public AddSurveyController(SurveyDao surveyDao) {
        this.surveyDao = surveyDao;
    }

    @Override
    public HttpMessage handle(HttpMessage request) throws SQLException {
        // fetching data:
        Map<String, String> queryMap = HttpMessage.parseRequestParameters(request.messageBody);
        // decoding data utf-8
        String decodedTitle = URLDecoder.decode(queryMap.get("title"), StandardCharsets.UTF_8).trim();
        String decodedDescription = URLDecoder.decode(queryMap.get("description"), StandardCharsets.UTF_8).trim();
        // setting values to object
        Survey survey = new Survey();
        if(decodedTitle.length()>0 && decodedDescription.length()>0){
            survey.setTitle(decodedTitle);
            survey.setDescription(decodedDescription);
            surveyDao.save(survey);
            return responseWithLocation("/index.html");
        } else {
            String errormessage = "<p>Fill inn required fields * </p>";
            return responseWith500(errormessage);
        }
    }
}

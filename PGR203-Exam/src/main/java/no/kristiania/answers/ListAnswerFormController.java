package no.kristiania.answers;

import no.kristiania.http.HttpController;
import no.kristiania.http.HttpMessage;
import no.kristiania.options.Option;
import no.kristiania.options.OptionDao;
import no.kristiania.questions.Question;
import no.kristiania.questions.QuestionDao;
import no.kristiania.surveys.SurveyDao;

import java.sql.SQLException;
import java.util.List;

import static no.kristiania.http.HttpServer.responseWith500;

public class ListAnswerFormController implements HttpController {

    private final OptionDao optionDao;
    private final QuestionDao questionDao;
    private SurveyDao surveyDao;

    public ListAnswerFormController(OptionDao optionDao, QuestionDao questionDao, SurveyDao surveyDao) {
        this.optionDao = optionDao;
        this.questionDao = questionDao;
        this.surveyDao = surveyDao;
    }

    @Override
    public HttpMessage handle(HttpMessage request) throws SQLException {
        String response = "";
        boolean dbHasZeroSurveys = surveyDao.listAll().isEmpty();
        if(!dbHasZeroSurveys){
            int surveyID = Integer.parseInt(request.messageBody);
            List<Question> questions = questionDao.listQuestionBySurveyId(surveyID);
            if(!questions.isEmpty()){
                int counter = 0;
                for (Question q: questions) {
                    counter +=1;
                    response +="<h3>Question "+counter+" : " + q.getText() +"</h3>";
                    for (Option option : optionDao.retrieveByQuestionId((int) q.getId())) {
                        response +=   "<label class='radio'><input required name='" + q.getId()+ "' type='radio' value='"+ option.getId()+ "'>" + option.getText() + "</input></label>";
                    }
                }
                response+= "<br><button class='btn'>Submit Answers</button>";
                return new HttpMessage("HTTP/1.1 200 ok",response);
            }
        }
        // determining error message
        String errorMessage = dbHasZeroSurveys
                ?  "<p>No surveys registered in Database </p>"
                : "<p>Survey has no questions registered in Database </p>"
                ;
        return responseWith500(errorMessage);

    }
}

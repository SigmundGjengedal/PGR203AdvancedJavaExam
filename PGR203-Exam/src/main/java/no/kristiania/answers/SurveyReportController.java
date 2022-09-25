package no.kristiania.answers;

import no.kristiania.http.HttpController;
import no.kristiania.http.HttpMessage;

import java.sql.SQLException;

public class SurveyReportController implements HttpController {
    private AnswersDao answersDao;

    public SurveyReportController(AnswersDao answersDao) {
        this.answersDao = answersDao;
    }

    @Override
    public HttpMessage handle(HttpMessage request) throws SQLException {
        String response = "";
        String startLine = "HTTP/1.1 200 OK";
        if(request.messageBody.length()>0 ) {
            for (String string : answersDao.getSurveyRapportCountForAnswers(Integer.parseInt(request.messageBody))) {
                response += "<li>" + string + "</li>";

            }
        }      else {
            response = "<h1 ><strong>500 Internal Server Error</strong> </h1>"+
                    "<p>No questions registered in Database </p>";
            startLine = "HTTP/1.1 500 Internal Server Error";
            ;

        } return new HttpMessage(startLine, response);
    }


}

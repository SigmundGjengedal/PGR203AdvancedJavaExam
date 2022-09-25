package no.kristiania.answers;

import no.kristiania.http.HttpController;
import no.kristiania.http.HttpMessage;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class FullSurveyReportController implements HttpController {

    private final AnswersDao answersDao;

    public FullSurveyReportController(AnswersDao answersDao) {
        this.answersDao = answersDao;
    }
    @Override
    public HttpMessage handle(HttpMessage request) throws SQLException {
        String response = "";
        String startLine = "HTTP/1.1 200 OK";
        if(request.messageBody.length()>0 ) {
               HashMap<Integer,String> report = answersDao.getSurveyReportQTextQId(Long.parseLong(request.messageBody));
               for (Map.Entry<Integer,String> entry : report.entrySet() ) {
                long questionId = entry.getKey();
                String questionText = entry.getValue();
                response += "<h3>"+ questionText + "</h3>";
                   for (String string : answersDao.getSurveyRapportCountForAnswers(questionId)) {
                       response += "<li>" + string + "</li>";
                   }
              }
        }
        else {
            response = "<h1 ><strong>500 Internal Server Error</strong> </h1>"+
                    "<p>No questions registered in Database </p>";
            startLine = "HTTP/1.1 500 Internal Server Error";
            ;

        } return new HttpMessage(startLine, response);
    }
}

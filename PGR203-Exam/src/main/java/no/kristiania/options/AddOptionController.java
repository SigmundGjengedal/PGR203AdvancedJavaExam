package no.kristiania.options;

import no.kristiania.http.HttpController;
import no.kristiania.http.HttpMessage;
import no.kristiania.questions.QuestionDao;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Map;

import static no.kristiania.http.HttpServer.responseWith500;
import static no.kristiania.http.HttpServer.responseWithLocation;

public class AddOptionController implements HttpController {
    private final OptionDao optionDao;
    private QuestionDao qDao;

    public AddOptionController(OptionDao optionDao, QuestionDao qDao) {
        this.optionDao = optionDao;
        this.qDao = qDao;
    }
    public AddOptionController(OptionDao optionDao) {
        this.optionDao = optionDao;
    }

    @Override
    public HttpMessage handle(HttpMessage request) throws SQLException {

            Map<String,String> buffer = HttpMessage.parseRequestParameters(request.messageBody);
            String text = URLDecoder.decode(buffer.get("option"), StandardCharsets.UTF_8).trim();
            boolean dbHasNoQuestions = qDao.listAll().isEmpty();
            if(!dbHasNoQuestions && text.length()>0 ){
                Option option = new Option();
                option.setText(text);
                option.setQuestionId(Integer.parseInt(buffer.get("questions")));
                optionDao.save(option);
                return responseWithLocation("/addOption.html");
            }
            else {
                // determining error message
                String errorMessage = dbHasNoQuestions
                        ?  "<p>No questions registered in Database </p>"
                        : "<p>Fill inn required fields * </p>"
                        ;
                return responseWith500(errorMessage);
        }
    }
}

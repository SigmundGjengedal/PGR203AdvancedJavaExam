package no.kristiania.http;

import no.kristiania.answers.*;

import no.kristiania.options.AddOptionController;

import no.kristiania.options.OptionDao;
import no.kristiania.questions.*;
import no.kristiania.surveys.AddSurveyController;
import no.kristiania.surveys.Survey;
import no.kristiania.surveys.SurveyDao;
import no.kristiania.surveys.SurveySelectController;
import org.flywaydb.core.Flyway;
import org.postgresql.ds.PGSimpleDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;

public class QuestionnaireServer {

    private static final Logger logger = LoggerFactory.getLogger(HttpServer.class);

    public static void main(String[] args) throws IOException, SQLException {
        // datasource and dao´s
        DataSource dataSource = createDatasource();
        QuestionDao questionDao = new QuestionDao(dataSource);
        AnswersDao answersDao = new AnswersDao(dataSource);
        OptionDao optionDao = new OptionDao(dataSource);
        SurveyDao surveyDao = new SurveyDao<Survey>(dataSource);

        HttpServer server = new HttpServer(0);
        // survey
        server.addController("/api/newSurvey",new AddSurveyController(surveyDao));
        server.addController("/api/surveys/select", new SurveySelectController(surveyDao));
        // question
        server.addController("/api/newQuestion",new AddQuestionController(questionDao,surveyDao));
        server.addController("/api/editQuestions",new UpdateQuestionController(questionDao));
        server.addController("/api/questions/select", new QuestionsSelectController(questionDao));
        server.addController("/api/options/add", new AddOptionController(optionDao,questionDao));
        server.addController("/api/questions/delete",new DeleteQuestionController(questionDao));
        // answers
        server.addController("/api/listAnswerForm",new ListAnswerFormController(optionDao,questionDao,surveyDao));
        server.addController("/api/saveAnswer", new AnswerSurveyController(answersDao,optionDao));
        // reports
        server.addController("/api/singleQuestionAnswer", new SurveyReportController(answersDao));
        server.addController("/api/surveyReport", new FullSurveyReportController(answersDao));
        logger.info("Starting http://localhost:{}/index.html",server.getPort());


    }

    private static DataSource createDatasource() throws IOException {
        Properties properties = new Properties();
        try (FileReader fileReader = new FileReader("pgr203.properties")) {
            properties.load(fileReader);
        }
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setURL(properties.getProperty("dataSource.url","jdbc:postgresql://localhost:5432/quest"));
        dataSource.setUser(properties.getProperty("dataSource.username", "question_dbuser"));
        dataSource.setPassword(properties.getProperty("dataSource.password"));

        Flyway.configure().dataSource(dataSource).load().migrate();
        return dataSource;

    }

}

package no.kristiania.survey;


import no.kristiania.http.HttpGetClient;
import no.kristiania.http.HttpPostClient;
import no.kristiania.http.HttpServer;
import no.kristiania.questions.TestData;
import no.kristiania.surveys.AddSurveyController;
import no.kristiania.surveys.Survey;
import no.kristiania.surveys.SurveyDao;
import no.kristiania.surveys.SurveySelectController;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

public class SurveyDaoTest {
    HttpServer server = new HttpServer(0);
    SurveyDao<Survey> surveyDao = new SurveyDao<Survey>(TestData.testDataSource());

    public SurveyDaoTest() throws IOException {
    }

    @Test
    void shouldListSurveySelects() throws SQLException, IOException {
    server.addController("/api/listSurveySelects", new SurveySelectController(surveyDao));
        Survey survey = new Survey();
        survey.setTitle("testSurveyNr1");
        survey.setDescription("testDescription");
        surveyDao.save(survey);
        HttpGetClient getClient = new HttpGetClient("localhost", server.getPort(), "/api/listSurveySelects");

        assertThat(getClient.getMessageBody())
                .contains("<option value=" + survey.getSurveyId() + ">"+survey.getTitle() +"</option>");
    }

    @Test
    void shouldSaveSurvey() throws IOException, SQLException {
        server.addController("/api/newSurvey", new AddSurveyController(surveyDao));



        new HttpPostClient("localhost", server.getPort(),"/api/newSurvey",
                "title=survey+should+be+included&description=description");

        assertThat(surveyDao.listAll())
                .extracting(Survey::getTitle)
                .contains("survey should be included");
    }
}

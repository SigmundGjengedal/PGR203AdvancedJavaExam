package no.kristiania.questions;

import no.kristiania.http.HttpGetClient;
import no.kristiania.http.HttpPostClient;
import no.kristiania.http.HttpServer;
import no.kristiania.surveys.Survey;
import no.kristiania.surveys.SurveyDao;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.SQLException;

import static no.kristiania.questions.TestData.pickOne;
import static no.kristiania.questions.TestData.testDataSource;
import static org.assertj.core.api.Assertions.assertThat;

public class QuestionDaoTest {
    QuestionDao dao = new QuestionDao(TestData.testDataSource());
    HttpServer server = new HttpServer(0);

    public QuestionDaoTest() throws IOException {
    }

    @Test
    void shouldListQuestionsAsSelect() throws SQLException, IOException {
        server.addController("/api/questionsSelect", new QuestionsSelectController(dao));
        Question question = exampleQuestion();
        dao.save(question);

        HttpGetClient client = new HttpGetClient("localhost", server.getPort(), "/api/questionsSelect");

        assertThat(client.getMessageBody())
                .contains("<option value=" + question.getId() + ">"+question.getText() +"</option>");
    }

    @Test
    void shouldSaveQuestionToDatabase() throws SQLException, IOException {
        SurveyDao<Survey> surveyDao = new SurveyDao<Survey>(testDataSource());
        server.addController("/api/newQuestion", new AddQuestionController(dao, surveyDao));
        Survey survey = new Survey();
        survey.setTitle("Something");
        survey.setDescription("Something else");
        surveyDao.save(survey);

        Question question = exampleQuestion();
        question.setText("This should be included in the result");

        HttpPostClient client = new HttpPostClient(
                "localhost", server.getPort(), "/api/newQuestion",
                "id=1&title=" + question.getTitle() + "&text=" + question.getText());

        assertThat(dao.listAll())
                .extracting(Question::getText)
                .contains("This should be included in the result");
    }


   @Test
    void shouldUpdateQuestionText() throws SQLException, IOException {
        server.addController("api/editQuestion", new UpdateQuestionController(dao));
        Question question = exampleQuestion();
        dao.save(question);

        HttpPostClient client = new HttpPostClient("localhost", server.getPort(), "api/editQuestion",
                "id=" + question.getId() + "&title=Updatetest&text=Updatetest");

        assertThat(dao.listAll())
                .extracting(Question::getText)
                .contains("Updatetest")
        ;
    }
    @Test
    void shouldUpdateQuestionTitleAndText() throws SQLException {
        Question question2 = exampleQuestion();
        dao.save(question2);
        // endrer verdier på objektet
        question2.setText("Text Update Test");
        question2.setTitle("Title Update Test");
        // må endres i databasen også.
        dao.upDate(question2);
        assertThat(dao.listAll())
                .anySatisfy(q -> {
                    assertThat(q.getText()).isEqualTo("Text Update Test");
                    assertThat(q.getTitle()).isEqualTo("Title Update Test");
                });
    }

    public static Question exampleQuestion() {
        Question question = new Question();
        question.setTitle(pickOne("Sport", "Travel", "Food", "Love","Work"));
        question.setText(pickOne(
                "Do you prefer x?",
                "Chose one of your hobbies:",
                "tell me your weapon of choice in battlefield:",
                "where would you rather travel?"));
        return question;
    }

    @Test
    void shouldUseControllersToDeleteQuestion() throws IOException, SQLException {
        server.addController("/api/deleteQuestion", new DeleteQuestionController(dao));
        Question question = exampleQuestion();
        question.setText("Should be deleted");
        dao.save(question);
        String body = String.valueOf(question.getId());
        Question question2 = exampleQuestion();
        dao.save(question2);

        HttpPostClient postClient = new HttpPostClient("localhost",server.getPort(),"/api/deleteQuestion","id=" + body);
        assertThat(dao.listAll())
                .extracting(Question::getText)
                .doesNotContain("Should be deleted");



    }
}

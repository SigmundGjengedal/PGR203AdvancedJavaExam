package no.kristiania.options;

import no.kristiania.http.HttpGetClient;
import no.kristiania.http.HttpPostClient;
import no.kristiania.http.HttpServer;
import no.kristiania.questions.Question;
import no.kristiania.questions.QuestionDao;
import no.kristiania.questions.TestData;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

public class OptionDaoTest {

    HttpServer server = new HttpServer(0);

    public OptionDaoTest() throws IOException {
    }

    @Test
    void shouldRetrieveListByQuestionId() throws SQLException {
        OptionDao optionDao = new OptionDao(TestData.testDataSource());
        QuestionDao questionDao = new QuestionDao(TestData.testDataSource());
        Question question = TestData.pkQuestion();
        questionDao.save(question);


        Option optionIncluded = new Option();
        optionIncluded.setText("Kake");
        optionIncluded.setQuestionId(1);

        Option optionIncludedAswell = new Option();
        optionIncludedAswell.setText("Bolle");
        optionIncludedAswell.setQuestionId(optionIncluded.getQuestionId());

        Option optionNotIncluded = new Option();
        optionNotIncluded.setText("Muffins");
        optionNotIncluded.setQuestionId(5);

        optionDao.save(optionIncluded);
        optionDao.save(optionIncludedAswell);

        assertThat(optionDao.retrieveByQuestionId(optionIncluded.getQuestionId()))
                .extracting(Option::getId)
                .contains(optionIncluded.getId(), optionIncludedAswell.getId())
                .doesNotContain(optionNotIncluded.getId());
    }

    @Test
    void shouldGiveOptionQuestionId() throws IOException {
        HttpGetClient client = new HttpGetClient("localhost",server.getPort(), "/api/options/add");

    }

    @Test
    void shouldRetrieveQuestionIDByOptionId() throws SQLException {
        OptionDao optionDao = new OptionDao(TestData.testDataSource());
        Option option = new Option();
        option.setText("Option 1");
        option.setQuestionId(1);
        optionDao.save(option);

        assertThat(optionDao.retrieveQuestionIDByOptionId(option.getId()))
                .isEqualTo(1);
    }

    @Test
    void shouldRespondWith303() throws IOException, SQLException {
        OptionDao optionDao = new OptionDao(TestData.testDataSource());
        QuestionDao questionDao = new QuestionDao(TestData.testDataSource());
        Question q = TestData.pkQuestion();
        questionDao.save(q);

        server.addController("/api/options/add", new AddOptionController(optionDao,questionDao));
        HttpPostClient client = new HttpPostClient("localhost",server.getPort(), "/api/options/add", "questions=1&option=blue");
        assertThat(client.getHeader("Location"))
                .isEqualTo("/addOption.html");
    }
}

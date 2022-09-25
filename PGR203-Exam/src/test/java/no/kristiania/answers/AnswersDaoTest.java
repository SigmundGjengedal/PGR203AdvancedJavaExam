package no.kristiania.answers;

import no.kristiania.http.HttpGetClient;
import no.kristiania.http.HttpServer;
import no.kristiania.options.Option;
import no.kristiania.options.OptionDao;
import no.kristiania.questions.Question;
import no.kristiania.questions.QuestionDao;
import no.kristiania.questions.TestData;
import no.kristiania.surveys.Survey;
import no.kristiania.surveys.SurveyDao;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class AnswersDaoTest {
        HttpServer httpServer = new HttpServer(0);
        AnswersDao aDao = new AnswersDao(TestData.testDataSource());
        QuestionDao qDao = new QuestionDao(TestData.testDataSource());

    public AnswersDaoTest() throws IOException, SQLException {
    }

    @Test
    void ShouldRegisterAnswers() throws SQLException {
        // question acts as foreign key, cannot submit new answer without a corresponding primary key.
        Question question = TestData.pkQuestion();
        qDao.save(question);
        long qId = question.getId();
        // answer
        Answer answer = new Answer();
        answer.setQuestionId((int)qId); // corresponds with q
        answer.setOptionId(TestData.pickOneInteger(1,2,3,4,5,6,7,8,9,11));
        aDao.save(answer);

        assertThat(aDao.retrieveById(answer.getAnswerId()))
                .usingRecursiveComparison()
                .isEqualTo(answer);
    }



    @Test
    void ShouldRetrieveAnswersByQuestionId() throws SQLException {
        // question acts as foreign key, cannot submit new answer without a corresponding primary key.
        Question question = TestData.pkQuestion();
        qDao.save(question);
        long qId = question.getId();
        // answer
        Answer answer1 = new Answer();
        answer1.setQuestionId((int)qId);
        answer1.setOptionId(TestData.pickOneInteger(4,5,6,7,8,9,11));
        aDao.save(answer1);

        Answer answer2 = new Answer();
        answer2.setQuestionId((int)qId);
        answer2.setOptionId(TestData.pickOneInteger(4,5,6,7,8,9,11));
        aDao.save(answer2);

        assertThat(aDao.retrieveByQuestionId(answer1.getQuestionId()))
                .extracting(Answer::getAnswerId)
                .contains(answer1.getAnswerId(), answer2.getAnswerId());
    }

    @Test
    void ShouldRetrieveReport() throws IOException, SQLException {
        SurveyDao surveyDao = new SurveyDao(TestData.testDataSource());
        Survey survey = TestData.pkSurvey();
        surveyDao.save(survey);
        OptionDao optionDao = new OptionDao(TestData.testDataSource());

        httpServer.addController("/api/singleQuestionAnswer", new SurveyReportController(aDao));
        // question acts as foreign key, cannot submit new answer without a corresponding primary key.
        Question question = TestData.pkQuestion();
        qDao.save(question);
        long qId = question.getId();

        Option option = new Option();
        option.setQuestionId((int)qId);//pk
        option.setText("Very tired");
        optionDao.save(option);

        Answer answer = new Answer();
        answer.setQuestionId(1);
        answer.setOptionId((int) option.getId());
        aDao.save(answer);

        HttpGetClient client = new HttpGetClient("localhost", httpServer.getPort(), "/api/singleQuestionAnswer?questionId=1");
        assertEquals("<li>1 answered : Very tired</li>", client.getMessageBody());
    }

    // class methods



}

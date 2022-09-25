package no.kristiania.questions;

import no.kristiania.surveys.Survey;
import org.flywaydb.core.Flyway;
import org.h2.jdbcx.JdbcDataSource;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Random;

public class TestData {
    public static DataSource testDataSource() {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setURL("jdbc:h2:mem:question_db;DB_CLOSE_DELAY=-1");
        Flyway.configure().dataSource(dataSource).load().migrate();
        return dataSource;
    }

    private static Random random = new Random();
    public static String pickOne(String... alternatives) {
        return alternatives[random.nextInt(alternatives.length)];
    }

    public static int pickOneInteger(int... alternatives) {
        return alternatives[random.nextInt(alternatives.length)];
    }

    public static Question pkQuestion() throws SQLException {
        Question q = new Question();
        q.setText("text");
        q.setTitle("title");
        q.setSurveyId(1);
        return q;
    }
    public static Survey pkSurvey() throws SQLException {
        Survey survey = new Survey();
        survey.setDescription("description");
        survey.setTitle("title");
        return survey;
    }
}

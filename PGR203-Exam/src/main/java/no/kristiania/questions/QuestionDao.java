package no.kristiania.questions;

import no.kristiania.http.AbstractDao;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuestionDao extends AbstractDao<Question> {

    private final DataSource dataSource;

    public QuestionDao(DataSource dataSource) {
        super(dataSource);

        this.dataSource = dataSource;
    }

    public void save(Question question) throws SQLException {
        try(Connection connection = dataSource.getConnection()){
            try(PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO questions (title,text,surveyId) values(?,?,?)", Statement.RETURN_GENERATED_KEYS )) {
                statement.setString(1, question.getTitle());
                statement.setString(2, question.getText());
                statement.setLong(3,question.getSurveyId());

                statement.executeUpdate();
                try(ResultSet rs = statement.getGeneratedKeys()){
                    rs.next();
                    question.setId(rs.getLong("id"));
                }
            }
        }

    }


    public Question retrieveById(long id) throws SQLException {
        return super.retrieve("SELECT * FROM questions WHERE id = ?",id);
    }


    public List<Question> listQuestionBySurveyId(long surveyId) throws SQLException {
        return super.listAllById("select * from questions where surveyId = ?", surveyId);
    }


    public List<Question> listAll() throws SQLException {
   return super.listAll("select * from questions");
    }

    public void upDate(Question question) throws SQLException {
        try(Connection connection = dataSource.getConnection()){
            try(PreparedStatement statement = connection.prepareStatement(
                    "UPDATE questions set title=?,text=? where id=?")) {
                statement.setString(1, question.getTitle());
                statement.setString(2, question.getText());
                statement.setLong(3, question.getId());

                statement.executeUpdate();
            }
        }
    }

    public void delete(Question q) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "delete from questions where id =?")) {
                statement.setLong(1,q.getId());
                statement.executeUpdate();
            }
        }
    }

    // **** Help Methods
    @Override
    public Question rowToResult(ResultSet rs) throws SQLException {
        Question question = new Question();
        question.setId(rs.getLong("id"));
        question.setTitle(rs.getString("title"));
        question.setText(rs.getString("text"));
        question.setSurveyId(rs.getLong("surveyId"));
        return question;
    }



}

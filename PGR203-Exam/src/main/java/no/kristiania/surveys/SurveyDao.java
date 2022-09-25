package no.kristiania.surveys;

import no.kristiania.http.AbstractDao;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;

public class SurveyDao<T> extends AbstractDao<Survey> {

    public SurveyDao(DataSource dataSource) {
        super(dataSource);
    }

    public void save(Survey survey) throws SQLException {
        try(Connection connection = dataSource.getConnection()){
            try(PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO surveys (title,description) values(?,?)", Statement.RETURN_GENERATED_KEYS )) {
                statement.setString(1, survey.getTitle());
                statement.setString(2, survey.getDescription());

                statement.executeUpdate();
                try(ResultSet rs = statement.getGeneratedKeys()){
                    rs.next();
                   survey.setSurveyId(rs.getLong("id"));
                }
            }
        }
    }

    public Survey retrieveById(long id) throws SQLException {
        return super.retrieve("SELECT * FROM survey WHERE id = ?", id);
    }

    public List<Survey> listAll() throws SQLException {
        return super.listAll("select * from surveys");
    }

    // Help Methods
    @Override
     public Survey rowToResult(ResultSet rs) throws SQLException {
        Survey survey = new Survey();
        survey.setSurveyId(rs.getLong("id"));
        survey.setTitle(rs.getString("title"));
        survey.setDescription(rs.getString("description"));
        return survey;
    }
}

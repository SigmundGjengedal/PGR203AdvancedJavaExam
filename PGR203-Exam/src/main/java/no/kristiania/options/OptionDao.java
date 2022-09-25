package no.kristiania.options;

import no.kristiania.http.AbstractDao;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OptionDao extends AbstractDao<Option> {


    private final DataSource dataSource;

    public OptionDao(DataSource dataSource) {
        super(dataSource);

        this.dataSource = dataSource;
    }

    public void save(Option option) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("INSERT INTO options (option_text,question_id) VALUES (?,?)", Statement.RETURN_GENERATED_KEYS)) {
                statement.setString(1, option.getText());
                statement.setInt(2,option.getQuestionId());
                statement.executeUpdate();
                try (ResultSet rs = statement.getGeneratedKeys()) {
                    rs.next();
                    option.setId(rs.getLong("option_id"));
                }
            }
        }
    }

    public List<Option> retrieveByQuestionId(long id) throws SQLException {
        return super.listAllById("SELECT * FROM options WHERE question_id = ?", id);
    }


    // Used for TDD. Use super listAllById if its gonna be used.
    public int retrieveQuestionIDByOptionId(long optionID) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("select distinct question_id from options where option_id = ?")) {
                statement.setLong(1,optionID);
                try (ResultSet resultSet = statement.executeQuery()) {
                    resultSet.next();
                    return resultSet.getInt(1);
                }
            }
        }
    }

    @Override
    protected Option rowToResult(ResultSet resultSet) throws SQLException {
        Option option = new Option();
        option.setId(resultSet.getLong("option_id"));
        option.setText(resultSet.getString("option_text"));
        option.setQuestionId(resultSet.getInt("question_id"));
        return option;
    }

}

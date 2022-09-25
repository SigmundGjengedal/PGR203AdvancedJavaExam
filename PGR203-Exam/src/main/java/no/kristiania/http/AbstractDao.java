package no.kristiania.http;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractDao<T> {

    protected final DataSource dataSource;

    public AbstractDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<T> listAll(String sqlStatement) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(sqlStatement)) {
                try (ResultSet resultSet = statement.executeQuery()) {
                    ArrayList<T> result = new ArrayList<>();
                    while(resultSet.next()){
                        result.add(rowToResult(resultSet));
                    }
                    return result;
                }
            }
        }
    }

    public T retrieve(String sqlStatement, long id) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(sqlStatement)) {
                statement.setLong(1, id);
                try (ResultSet resultSet = statement.executeQuery()) {
                    resultSet.next();
                    return rowToResult(resultSet);
                }
            }
        }
    }

    public List<T> listAllById(String sqlStatement, long id) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(sqlStatement)) {
                statement.setLong(1,id);
                try(ResultSet rs = statement.executeQuery()) {
                    ArrayList<T> result = new ArrayList<>();
                    while (rs.next()) {
                        result.add(rowToResult(rs));
                    }
                    return result;
                }
            }
        }
    }

    protected abstract T rowToResult(ResultSet resultSet) throws SQLException;
}



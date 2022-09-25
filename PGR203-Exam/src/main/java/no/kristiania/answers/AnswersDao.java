package no.kristiania.answers;
import no.kristiania.http.AbstractDao;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class AnswersDao extends AbstractDao<Answer> {

    private final DataSource dataSource;


    public AnswersDao(DataSource dataSource) throws SQLException {
        super(dataSource);

        this.dataSource = dataSource;
    }

    public void save(Answer answer) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO answers (question_id,option_id) values (?,?)", Statement.RETURN_GENERATED_KEYS)) {
                statement.setInt(1, answer.getQuestionId());
                statement.setInt(2, answer.getOptionId());
                statement.executeUpdate();
                try(ResultSet rs = statement.getGeneratedKeys()){
                    rs.next();
                    answer.setAnswerId(rs.getLong("answer_id"));
                }
            }
        }
    }

    public Answer retrieveById(long answerId) throws SQLException{
        return super.retrieve("SELECT * FROM answers WHERE answer_id = ?", answerId);
    }


    public ArrayList<String> getSurveyRapportCountForAnswers(long questionId) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "select a.option_id, count(a.option_id) as count, option_text\n"+
                    "from answers a\n"+
                    "join options o\n"+
                    "on a.option_id= o.option_id\n"+
                    "where a.question_id = ?\n"+
                    "group by a.option_id,option_text\n"+
                    "order by a.option_id desc ;"
            )) {
                statement.setLong(1,questionId);
                try(ResultSet rs = statement.executeQuery()){
                    ArrayList<String> result = new ArrayList<String>();
                    while(rs.next()){
                        String optionText = rs.getString("option_text");
                        int count = rs.getInt("count");
                        String countS = String.valueOf(count);
                        result.add(countS + " answered : " + optionText  );
                    }
                    return result;
                }
            }
        }
    }
    public HashMap<Integer,String> getSurveyReportQTextQId(long surveyId) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "select distinct q.text, q.id\n"+
                    "from answers a\n"+
                    "join options o\n"+
                    "on a.option_id = o.option_id\n"+
                    "join questions q\n"+
                    "on a.question_id = q.id\n"+
                    "where a.question_id in\n"+
                            "(select id from questions where surveyid = ?)\n"+
                    "group by q.text, q.id\n"+
                    "order by q.text;"
            )) {
                statement.setLong(1, surveyId);
                try (ResultSet rs = statement.executeQuery()){
                    HashMap<Integer, String> reportMap = new HashMap<>();
                    while (rs.next()) {
                        String qText = rs.getString("text");
                        Integer qID = rs.getInt("id");
                        reportMap.put(qID, qText);
                    }
                    return reportMap;
                }
            }
        }
    }

    public List<Answer> retrieveByQuestionId(long questionId) throws SQLException {
        return super.listAllById("SELECT * FROM answers WHERE question_id = ?",questionId);
    }

    public List<Answer> retrieveAll() throws SQLException {
       return super.listAll("SELECT * FROM answers");
    }

    @Override
    public Answer rowToResult(ResultSet rs) throws SQLException {

        Answer answer = new Answer();
        answer.setAnswerId(rs.getInt("answer_id"));
        answer.setOptionId(rs.getInt("option_id"));
        answer.setQuestionId(rs.getInt("question_id"));
        return answer;
    }

}

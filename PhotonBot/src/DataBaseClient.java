import java.sql.*;
import java.util.ArrayList;

/**
 * Created by soroushomranpour on 8/5/2017 AD.
 */
public class DataBaseClient {
    private Statement statement;
    public void connect(){
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/Bot?useSSL=false", "root", "986532");
            statement = connection.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void insert(String firstName,String lastName,String userId,long chatId,String message){
        String insertStmt = "'"+firstName+"','"+lastName+"','"+userId+"',"+chatId+",'"+message+"'";
        String sqlInsert = "insert ignore into users values ("+insertStmt+");";
        //System.out.println("The SQL query is: " + sqlInsert);  // Echo for debugging
        int countInserted = 0;
        try {
            countInserted = statement.executeUpdate(sqlInsert);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public long instructorId(String name){
        String selectStmt = "select * from instructors where name='"+name+"';";
        try {
            ResultSet rset = statement.executeQuery(selectStmt);
            return rset.getLong("chatid");
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }
    public ArrayList<Instructor> loadInstructor(){
        ArrayList<Instructor> instructors = new ArrayList<>();
        String selectStmt = "select * from instructors;";
        try {
            ResultSet rset = statement.executeQuery(selectStmt);
            while (rset.next()){
                instructors.add(new Instructor(rset.getString("name"),rset.getLong("chatid")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            return instructors;
        }
    }
}

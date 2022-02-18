import org.postgresql.Driver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class JdbcRunner {
    private static final String URL = "jdbc:postgresql://localhost:5432/postgres";
    private static final String USER = "postgres";
    private static final String PASS = "postgres";


    public static void main(String[] args) throws SQLException {
        DriverManager.registerDriver(new Driver());
        try {
            Connection connection = DriverManager.getConnection(URL, USER, PASS);

            Statement makeTable = connection.createStatement();
            System.out.println("Создаю таблицу");
            makeTable.execute("CREATE TABLE IF NOT EXISTS users " +
                    "(id INT PRIMARY KEY, first_name VARCHAR(128), last_name VARCHAR(128), age INT)");
            System.out.println("Добавляю пользователей");

            PreparedStatement addUser = connection.prepareStatement("INSERT INTO users (id, first_name, last_name, age) VALUES (?, ?, ?, ?)");
            for (int i = 0; i < 5; i++) {
                addUser.setInt(1, (byte) (i));
                addUser.setString(2, "Ivan" + i);
                addUser.setString(3, "Ivanov" + i);
                addUser.setInt(4, (byte) (30 + i));
                addUser.executeUpdate();
            }
            System.out.println("Получаю из БД пользователей.");

            Statement getAll = connection.createStatement();
            List<User> allUsers = new ArrayList<>();
            ResultSet resultSet = getAll.executeQuery("SELECT * FROM users");
            while (resultSet.next()) {
                User user = new User();
                user.setId(resultSet.getInt(1));
                user.setFirstName(resultSet.getString(2));
                user.setLastName(resultSet.getString(3));
                user.setAge(resultSet.getByte(4));
                allUsers.add(user);
            }
            for (int i = 0; i < allUsers.size(); i++) {
                System.out.println(allUsers.get(i));
            }

            System.out.println("Очищаю таблицу");
            Statement clearTable = connection.createStatement();
            clearTable.execute("TRUNCATE users");
            System.out.println("Таблица очищена");

        } catch (SQLException e) {
            System.out.println("Соединение не удалось");
            e.printStackTrace();
        }
    }
}

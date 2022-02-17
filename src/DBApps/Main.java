package DBApps;

import java.sql.*;
import java.util.Properties;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws SQLException {


        //Пропъртитата се задават, за да се използват готови след това при конекцията
        Properties props = new Properties();
        props.setProperty("user", "root");
        props.setProperty("password", "11111111");

        //Създаваме конекция към базата данни, използвайки DriverManager
        Connection connection = DriverManager
                .getConnection("jdbc:mysql://localhost:3306/diablo", props);

        //В Statement се пише самата заявка
        PreparedStatement statement = connection.prepareStatement("SELECT user_name,\n" +
                "       first_name,\n" +
                "       last_name,\n" +
                "       count(ug.game_id) as played_games\n" +
                "from users\n" +
                "inner join users_games ug on users.id = ug.user_id\n" +
                "where user_name = ?");

        Scanner scan = new Scanner(System.in);
        System.out.println("Please write your username");
        String username = scan.nextLine();

        //Добавяме данните за параметъра. Първо е индекса и той се използва в случаите, когато имаме повече параметри в заявката.
        statement.setString(1, username);

        //В резълтсет се пазят данните, които е върнала заявката
        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            {
                String dbUserName = resultSet.getString("user_name");
                String dbFirstName = resultSet.getString("first_name");
                String dbLastName = resultSet.getString("last_name");
                int dbPlayedGames = resultSet.getInt("played_games");
                System.out.printf("User: %s \n" +
                        "%s %s has played %d games", dbUserName, dbFirstName, dbLastName, dbPlayedGames);
            }
        } else
            System.out.println("No such user exists");

    }
}


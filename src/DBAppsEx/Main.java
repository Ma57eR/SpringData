package DBAppsEx;

import java.security.PrivateKey;
import java.sql.*;
import java.util.Properties;
import java.util.Scanner;

public class Main {
    private static final String CONN_JDBC = "jdbc:mysql://5.tcp.eu.ngrok.io:18528/";
    private static final String DB_NAME = "minions_db";
    private static final Scanner scan = new Scanner(System.in);
    private static Connection connection;


    public static void main(String[] args) throws SQLException {


        connection = getConnection();

        System.out.println("Enter exercise number");
        int exNum = Integer.parseInt(scan.nextLine());
        switch (exNum) {
            case 2:
                exerciseTwo();
            case 3:
                exerciseThree();
            case 4:
                exerciseFour();
            case 5:
                exerciseFive();
            case 6:
                exerciseSix();
            case 7:
                exerciseSeven();
            case 8:
                exerciseEight();
            case 9:
                exerciseNine();
        }


    }

    private static void exerciseNine() {
    }

    private static void exerciseEight() {
    }

    private static void exerciseSeven() {
    }

    private static void exerciseSix() {
    }

    private static void exerciseFive() {
    }

    private static void exerciseFour() {
    }

    private static void exerciseThree() {
    }

    private static void exerciseTwo() throws SQLException {
        //2ра задача
        PreparedStatement st = connection.prepareStatement("select concat(v.name, ' ',  count(DISTINCT m.id)) from villains v\n" +
                "inner join minions_villains mv on mv.villain_id = v.id\n" +
                "inner join minions m on m.id = mv.minion_id\n" +
                "group by v.name\n" +
                "having count(DISTINCT m.id) > 15\n" +
                "order by count(m.id) desc;");

        ResultSet rs = st.executeQuery();

        while (rs.next()) {
            System.out.println(rs.getString(1));
        }
    }

    // Изваждам създаването на конекцията в Метод, защото по-горе са решенията на задачите и няма смисъл да се правят отделни конекции за всяка от тях
    private static Connection getConnection() throws SQLException {

        Properties prop = new Properties();
        System.out.println("Enter your username: \\if empty then root\\");
        String username = scan.nextLine();
        System.out.println("Enter your password: \\if empty then 11111111\\");
        String pass = scan.nextLine();

        //Ако не са въведени, въвежда служебни
        if (username.equals("")) {
            username = "root";
        }
        if (pass.equals("")) {
            pass = "11111111";
        }
        //Задаваме юзър и парола в Пропърти класа
        prop.setProperty("user", username);
        prop.setProperty("password", pass);

        return DriverManager
                .getConnection(CONN_JDBC + DB_NAME, prop);
    }
}

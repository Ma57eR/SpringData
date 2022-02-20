package DBAppsEx;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

public class Main {

    /*
    Всички задачи са написани под формата на методи.
    За да се пусне някоя от тях, след юзъра и паролата за базата данни, се въвежда номера на задачата
    Според задачата, ще иска и конкретните входни данни, за да върне резултат
     */

    private static final String CONN_JDBC = "jdbc:mysql://localhost:3306/";
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
                //Шеста задача няма решение, тъй като не е задължителна
            case 7:
                exerciseSeven();
            case 8:
                exerciseEight();
            case 9:
                exerciseNine();
        }


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

    private static void exerciseThree() throws SQLException {
        //3та задача
        PreparedStatement st = connection.prepareStatement("select\n" +
                "distinct \n" +
                "v.name,\n" +
                "m.name,\n" +
                "m.age\n" +
                "from minions m\n" +
                "join minions_villains mv on m.id = mv.minion_id\n" +
                "join villains v on v.id = mv.villain_id\n" +
                "where v.id = ?");
        //Въвеждаме ID на злодея
        System.out.println("Enter Villain ID");
        int villainId = Integer.parseInt(scan.nextLine());
        //Вкарва се ИД-то в заявката като параметър
        st.setInt(1, villainId);
        ResultSet rs = st.executeQuery();

        //Проверяваме дали има резултати
        if (rs.next()) {
            while (rs.next()) {
                int n = 1;
                String vilName = rs.getString(1);
                String minName = rs.getString(2);
                int minAge = rs.getInt(3);
                String num = n + ".";
                System.out.printf("Villain: %s %n" +
                        "%s %s %d %n", vilName, num, minName, minAge);
                n++;
            }
        } else {
            System.out.printf("No villain with ID %d exists in the database.", villainId);
        }
    }

    private static void exerciseFour() throws SQLException {
        //Първо проверяваме дали миниона съществува в базата и ако го няма го добавяме
        //След това проверяваме дали вилиана съществува в базата и ако го няма го добавяме
        System.out.println("Enter input from Exercise document:");
        String[] minionInfo = scan.nextLine().split(" ");
        String minionName = minionInfo[1];
        int minionAge = Integer.parseInt(minionInfo[2]);
        String minionCity = minionInfo[3];

        String villainName = scan.nextLine().split(" ")[1];

        //Проверяваме дали града съществува в базата
        PreparedStatement cityInfo = connection.prepareStatement(
                "select t.id from towns t where t.name = ?");
        cityInfo.setString(1, minionCity);
        ResultSet cityResult = cityInfo.executeQuery();

        //Ако града не съществува го добавяме
        if (!cityResult.next()) {
            PreparedStatement addCity = connection.prepareStatement(
                    "INSERT INTO towns (name) VALUES (?)");
            addCity.setString(1, minionCity);
            addCity.executeUpdate();
            System.out.printf("Town %s was added to the database.%n", minionCity);
        }
        PreparedStatement minion = connection.prepareStatement(
                "select\n" +
                        "    m.name,\n" +
                        "    m.age\n" +
                        "from minions m\n" +
                        "where m.name = ?;");
        minion.setString(1, minionName);

        ResultSet currentMinion = minion.executeQuery();
        int minionTownId = 0;
        if (!currentMinion.next()) {
            //Първо трябва да вземем Id на града
            PreparedStatement cityID = connection.prepareStatement(
                    "select t.id from towns t where t.name = ?");
            cityID.setString(1, minionCity);
            ResultSet currentCityID = cityID.executeQuery();
            currentCityID.next();
            minionTownId = currentCityID.getInt(1);

            PreparedStatement addMinion = connection.prepareStatement(
                    "INSERT INTO minions (name, age, town_id) VALUES (?, ?, ?)"
            );
            addMinion.setString(1, minionName);
            addMinion.setInt(2, minionAge);
            addMinion.setInt(3, minionTownId);
            addMinion.executeUpdate();
        }

        //Проверяваме Villain дали съществува
        PreparedStatement villainCheck = connection.prepareStatement(
                "select * from villains where name = ?"
        );
        villainCheck.setString(1, villainName);
        ResultSet currentVillain = villainCheck.executeQuery();

        //Добавяме Villain
        if (!currentVillain.next()) {
            PreparedStatement addVillain = connection.prepareStatement(
                    "INSERT INTO villains (name, evilness_factor) VALUES (?, 'evil')"
            );
            addVillain.setString(1, villainName);
            addVillain.executeUpdate();
            System.out.printf("Villain %s was added to the database.%n", villainName);
        }

        //Добавяме връзка минион / Вилиън
        int minionId = 0;
        int villainId = 0;

        //Взимаме Minion ID
        PreparedStatement minId = connection.prepareStatement(
                "SELECT m.id from minions m where m.name = ?"
        );
        minId.setString(1, minionName);
        ResultSet thisMinId = minId.executeQuery();
        thisMinId.next();
        minionId = thisMinId.getInt(1);

        //Взимаме Villain ID
        PreparedStatement vilId = connection.prepareStatement(
                "SELECT v.id from minions m where v.name = ?"
        );
        vilId.setString(1, minionName);
        ResultSet thisVilId = minId.executeQuery();
        thisVilId.next();
        villainId = thisVilId.getInt(1);

        //Добавяме връзката в базата
        PreparedStatement vilMin = connection.prepareStatement(
                "INSERT INTO minions_villains VALUES (?, ?)"
        );
        vilMin.setInt(1, minionId);
        vilMin.setInt(2, villainId);
        System.out.printf("Successfully added %s to be minion of %s.%n", minionName, villainName);

    }

    private static void exerciseFive() throws SQLException {
        //Създаваме заявка за селектиране на всички градове от дадена държава
        PreparedStatement cities = connection.prepareStatement(
                "UPDATE towns t SET t.name = UPPER(t.name) WHERE t.country = ?"
        );
        System.out.println("Enter country:");
        String cityUpper = scan.nextLine();
        cities.setString(1, cityUpper);
        int updatedCount = cities.executeUpdate();

        if (updatedCount == 0) {
            System.out.println("No town names were affected.");
        } else {
            List<String> uppedCities = new ArrayList();
            PreparedStatement citiesFromCountry = connection.prepareStatement(
                    "SELECT t.name from towns t where t.country = ?"
            );
            citiesFromCountry.setString(1, cityUpper);
            ResultSet listUppedCities = citiesFromCountry.executeQuery();
            while (listUppedCities.next()) {
                uppedCities.add(listUppedCities.getString(1));
            }
            System.out.printf("%d towns were affected.%n [%s]", updatedCount, String.join(", ", uppedCities));
        }

    }

    private static void exerciseSix() throws SQLException {
        //Шеста задача няма решение, тъй като не е задължителна
    }

    private static void exerciseSeven() throws SQLException {
        //Изваждаме всички миниони
        PreparedStatement allMinions = connection.prepareStatement(
                "select m.name from minions m"
        );
        ResultSet minionsNames = allMinions.executeQuery();
        List<String> minionsOrder = new ArrayList();
        while (minionsNames.next()) {
            minionsOrder.add(minionsNames.getString(1));
        }
        List<String> minionsNewOrder = new ArrayList<>();

        while (!minionsOrder.isEmpty()) {
            //Добавяме първия
            minionsNewOrder.add(minionsOrder.get(0));
            minionsOrder.remove(0);
            if (minionsOrder.size() > 0) {
                //Добавяме последния
                minionsNewOrder.add(minionsOrder.get(minionsOrder.size() - 1));
            }
            minionsOrder.remove(minionsOrder.size() - 1);
        }
        System.out.println();
    }

    private static void exerciseEight() throws SQLException {
        System.out.println("Enter minion IDs separated by space:");

        List<Integer> minIDs = Arrays.stream(scan.nextLine().split(" ")).map(Integer::parseInt).collect(Collectors.toList());

        //Ъпдейтваме възрастта на минионите, чиито ИД-та са ни подадени
        PreparedStatement setMinionNameAndAge = connection.prepareStatement(
                "UPDATE minions m " +
                        "SET m.name = LOWER(m.name), m.age = m.age + 1 " +
                        "WHERE m.id = ?"
        );
        for (int i = 0; i < minIDs.size()-1; i++) {
            setMinionNameAndAge.setInt(1, minIDs.get(i));
            setMinionNameAndAge.executeUpdate();
        }
        //Взимаме данните на всички миниони:
        PreparedStatement allMinInfo = connection.prepareStatement(
                "SELECT concat(m.name, ' ', m.age) as info FROM minions m"
        );
        List<String> minionsNamesAndAgesForPrint = new ArrayList<>();
        ResultSet allNamesAndAges = allMinInfo.executeQuery();
        while (allNamesAndAges.next()) {
            minionsNamesAndAgesForPrint.add(allNamesAndAges.getString(1));
        }
        System.out.println(String.join("\n", minionsNamesAndAgesForPrint));
    }

    private static void exerciseNine() throws SQLException {
        /*
        //Заявка за създаване на процедурата в базата
        DELIMITER $$
        CREATE PROCEDURE usp_get_older(minion_id INT)
        BEGIN
        UPDATE minions m SET m.age = m.age + 1
        WHERE m.id = minion_id;
        END $$
        DELIMITER ;
         */

        //Извикваме процедурата
        PreparedStatement callProcedure = connection.prepareStatement(
                "CALL usp_get_older(?)"
        );
        System.out.println("Enter minion ID to update it's age:");
        int minionId = Integer.parseInt(scan.nextLine());
        //Вмъкваме ИД на миниона в процедурата
        callProcedure.setInt(1, minionId);
        callProcedure.executeUpdate();
        //Изваждаме името и годините на същия минион
        PreparedStatement minionNameAndAge = connection.prepareStatement(
                "SELECT concat(m.name, ' ', m.age) as info FROM minions m WHERE m.id = ?"
        );
        minionNameAndAge.setInt(1, minionId);
        ResultSet minNewAge = minionNameAndAge.executeQuery();
        minNewAge.next();
        System.out.println(minNewAge.getString(1));

    }

    // Изваждам създаването на конекцията в Метод, защото по-горе са решенията на задачите и няма смисъл да се правят отделни конекции за всяка от тях
    private static Connection getConnection() throws SQLException {

        Properties prop = new Properties();
        System.out.println("Enter your DB username:");
        String username = scan.nextLine();
        System.out.println("Enter your DB password: ");
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

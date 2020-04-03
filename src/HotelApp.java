import java.sql.*;
import java.util.Scanner;

public class HotelApp {

    private final String url = "jdbc:postgresql://localhost/Hotel";
    private final String user = "postgres";
    private final String password = "admin";

    public Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return conn;
    }


    public HotelApp() {
        displayMenu();
    }

    private void displayMenu() {

        while (true) {
            final StringBuilder sb;
            sb = new StringBuilder();
            sb.append("Choose what you want to do: ")
                    .append("\n1. View hotel status,")
                    .append("\n2. Add new person,")
                    .append("\n3. Delete person,")
                    .append("\n4. Quit application.");

            System.out.println(sb.toString());

            Scanner in = new Scanner(System.in);
            int command = in.nextInt();

            switch (command) {
                case 1:
                    getStatus();
                    break;
                case 2:
                    addPerson();
                    break;
                case 3:
                    deletePerson();
                    break;
                case 4:
                    exitSystem();
                    break;
                default:
                    System.out.println("Wrong command, try again.");
                    break;
            }
        }
    }


    public void getStatus() {

        String SQL = "SELECT id,firstName, lastName FROM person";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SQL)) {
            // display actor information
            displayPerson(rs);
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }


    public long addPerson() {

        String SQL = "INSERT INTO person(firstName,lastName, age) "
                + "VALUES(?,?, ?)";

        long id = 0;

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(SQL,
                     Statement.RETURN_GENERATED_KEYS)) {

            Person person = new Person();

            System.out.println("Enter person first name: ");
            Scanner line2 = new Scanner(System.in);
            person.setFirstName(line2.nextLine());

            System.out.println("Enter person last name: ");
            Scanner line3 = new Scanner(System.in);
            person.setLastName(line3.nextLine());

            System.out.println("Enter person age: ");
            Scanner line4 = new Scanner(System.in);
            person.setAge(line4.nextInt());

            pstmt.setString(1, person.getFirstName());
            pstmt.setString(2, person.getLastName());
            pstmt.setInt(3, person.getAge());

            int affectedRows = pstmt.executeUpdate();
            // check the affected rows
            if (affectedRows > 0) {
                // get the ID back
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        id = rs.getLong(1);
                    }
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return id;
    }

    public int deletePerson() {
        String SQL = "DELETE FROM person WHERE id = ?";
        int affectedrows = 0;
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {

            int id = 0;
            System.out.println("Enter person id : ");
            Scanner line4 = new Scanner(System.in);
            id = line4.nextInt();

            pstmt.setInt(1, id);

            affectedrows = pstmt.executeUpdate();

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return affectedrows;
    }

    public void exitSystem() {
        System.out.println("Thanks for using our program!");
        System.exit(0);
        return;
    }

    private void displayPerson(ResultSet rs) throws SQLException {
        while (rs.next()) {
            System.out.println(rs.getString("id") + "\t"
                    + rs.getString("firstName") + "\t"
                    + rs.getString("lastName"));
        }
    }
}
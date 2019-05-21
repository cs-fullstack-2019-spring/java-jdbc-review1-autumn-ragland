import java.util.Scanner;
import java.sql.*;

public class Main {

//    database connection
    private static String url = "jdbc:postgresql://localhost/19_05_21_am_project";
    private static String user = "student";
    private static String password = "C0d3Cr3w";

    private static Connection connect() throws SQLException {
        Connection conn = null;
        try{
            conn = DriverManager.getConnection(url, user, password);
        }
        catch(SQLException ex){
            System.out.println(ex.getMessage());
        }
        return conn;
    }

//    get match data from game + goal table using prepared statements
    private static void getMatches(int matchID){
        String SQL =
                "SELECT game.id,game.mdate,game.stadium,game.team1,game.team2, " +
                "count(goal.gtime) FROM game " +
                "JOIN goal ON game.id=goal.matchid " +
                "WHERE game.id=? GROUP BY mdate, id, stadium, team1, team2";
        try(Connection conn = connect();
            PreparedStatement pstmt = conn.prepareStatement(SQL)) {
            pstmt.setInt(1, matchID);
            ResultSet rs = pstmt.executeQuery();
            formatResults(rs);
        }
        catch(SQLException ex){
            System.out.println(ex.getMessage());
        }
    }

//    add record to goal table using prepared statements
    private static void addGoal(int matchID, String teamID, String player, int gTime){
        String SQL = "INSERT INTO goal VALUES (?,?,?,?)";
        try(Connection conn = connect();
            PreparedStatement pstmt = conn.prepareStatement(SQL)) {
            pstmt.setInt(1, matchID);
            pstmt.setString(2, teamID);
            pstmt.setString(3, player);
            pstmt.setInt(4, gTime);
            pstmt.executeUpdate();
        }
        catch(SQLException ex){
            System.out.println(ex.getMessage());
        }
    }

//    format getMatches result set
    private static void formatResults(ResultSet rs) throws SQLException{
            while(rs.next()){
                System.out.print("MATCH ID: " + rs.getString(1));
                System.out.print(" MATCH DATE: " + rs.getString(2));
                System.out.println(" STADIUM: " + rs.getString(3));
                System.out.print("TEAM ONE: " + rs.getString(4));
                System.out.print(" TEAM TWO: " + rs.getString(5));
                System.out.println(" Goals: " + rs.getString(6));
            }
    }

//    prompt user and call getMatches or addGoal based on input
    private static void userPrompt(){
        Scanner read = new Scanner(System.in);

        String userInput=" ";

        System.out.println("Welcome to CodeCrew Sports Network.");
        System.out.println("Pick an option below:");

        while(!userInput.equals("q")){

            System.out.println("1. List match results");
            System.out.println("2. Add a goal to the database");

            userInput = read.nextLine();

            if(userInput.equals("1")){
                System.out.println("Please enter the match ID (int)");
                int matchID = read.nextInt();
                getMatches(matchID);
            }

            else if(userInput.equals("2")){
                System.out.println("Please enter the matchID (int)");
                int matchID = read.nextInt();
                System.out.println("Please enter the teamID (string)");
                String teamID = read.nextLine();
                System.out.println("Please enter the player (string)");
                String player = read.nextLine();
                System.out.println("Please enter the goal time (int)");
                int gTime = read.nextInt();
                addGoal(matchID,teamID,player,gTime);
                System.out.println("Goal Added!");
            }

            else{
                System.out.println("Chose option 1 or option 2 or enter q to quit");
            }
        }
    }

//    call prompt
    public static void main(String[] args) {
        userPrompt();
    }
}

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

//    get match data from game + goal table
    private static void getMatches(){
        String SQL =
                "SELECT game.id,game.mdate,game.stadium,game.team1,game.team2, count(goal.gtime) " +
                "FROM game " +
                "JOIN goal ON game.id=goal.matchid " +
                "GROUP BY mdate, id, stadium, team1, team2 " +
                "ORDER BY game.id";
        try(Connection conn = connect();
            PreparedStatement pstmt = conn.prepareStatement(SQL)) {
            ResultSet rs = pstmt.executeQuery();
            formatResults(rs);
        }
        catch(SQLException ex){
            System.out.println(ex.getMessage());
        }
    }

//    get each Team's scores per game
    private static void matchResults(int matchIDNumber, String teamOne, String teamTwo) {
        int team1Score = 0;
        int team2Score = 0;
        String SQL ="SELECT matchid, teamid  FROM goal WHERE matchid = ?";
        try
        {
            Connection conn = connect();
            PreparedStatement pstmt = conn.prepareStatement(SQL) ;
            pstmt.setInt(1,matchIDNumber);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                if(rs.getString("teamID").equalsIgnoreCase(teamOne)) {
                    team1Score++;
                }
                else {
                    team2Score++;
                }
            }

            System.out.print(" " + teamOne + ":");
            System.out.print(team1Score + " ");
            System.out.print(teamTwo + ":");
            System.out.println(team2Score);
        }
        catch (SQLException ex)
        {
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
                System.out.print(" STADIUM: " + rs.getString(3));
                matchResults(rs.getInt("id"),rs.getString("team1"),rs.getString("team2"));
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
                getMatches();
            }

            else if(userInput.equals("2")){

                System.out.println("Please enter the matchID (1001 - 1031)");
                int matchID = read.nextInt();

                System.out.println("Please enter the teamID (ex. POL)");
                String teamID = read.next();

                System.out.println("Please enter the player (last name)");
                String player = read.next();

                System.out.println("Please enter the goal time (int)");
                int gTime = read.nextInt();

                addGoal(matchID,teamID,player,gTime);
                System.out.println("Goal Added!");
            }

            else if(userInput.equals("q")){
                System.out.println("Thanks for visiting the CodeCrew Sports Network");
            }

            else{
                System.out.println("Chose option 1 or option 2");
            }
        }
    }

//    call prompt
    public static void main(String[] args) {
        userPrompt();
    }
}

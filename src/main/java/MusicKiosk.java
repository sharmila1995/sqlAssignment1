
import java.sql.*;
public class MusicKiosk {

    public static void main(String[] args) {
        MusicKiosk app = new MusicKiosk();
        MusicKiosk connect = new MusicKiosk();
        app.createPlaylist();
    }

    private Connection connect() {
        String url = "jdbc:sqlite:C:\\Users\\ericv\\sql-jdbc.wiki\\sql-jdbc\\chinook.db";
        Connection conn = null;
        System.out.println("Connection to SQLite has been established.");
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }


    public void viewTracks() {
        String sql = "SELECT tracks.TrackId AS ID,\n" +
                "tracks.Name,\n" +
                "Composer,\n" +
                "genres.Name AS Genres,\n" +
                "UnitPrice AS Price\n" +
                "FROM tracks\n" +
                "LEFT JOIN\n" +
                "Genres ON tracks.GenreId = Genres.GenreId\n" +
                "ORDER BY tracks.TrackId;";
        try (Connection conn = this.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Integer trackId = rs.getInt("ID");
                String name = rs.getString("Name");
                String composer = rs.getString("Composer");
                String genre = rs.getString("Genres");
                Double unitPrice = rs.getDouble("Price");
                System.out.println("ID: " + trackId + " Song Title: " + name + " Artist: " + composer + " Genre: " + genre + " $" + unitPrice);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    public void createPlaylist() {
        String playlistName = Console.getString("Please name this playlist: ");
        String sql = "INSERT INTO playlists (Name)\n" +
                "VALUES ('"+playlistName+"');";
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()){
            while (rs.next()) {
                String listName = rs.getString(playlistName);
                System.out.println("Playlist name: " + listName);
            }


                //Connection conn = this.connect();
             //PreparedStatement pstmt = conn.prepareStatement(sql)) {
            //pstmt.setString(1, playlistName);
            //ResultSet rs = pstmt.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }


        //Console.getInt("Please select the ID of the song you would like to add to the playlist: ");
    }
}

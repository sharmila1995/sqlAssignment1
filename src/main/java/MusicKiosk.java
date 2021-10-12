
import java.sql.*;
public class MusicKiosk {

    public static void main(String[] args) throws SQLException {
        MusicKiosk app = new MusicKiosk();
        app.viewTracks();
        System.out.println("\n\nWelcome to MusicKiosk.  Please reference the song list above to create a playlist.");
        String playlistName = app.getPlaylistName();
        Integer playlistID = app.createPlaylist(playlistName);
        do {
            app.insertTrackToPlaylist(playlistID, app.getTrackID(app.findAmountOfTracks()));
        } while (anotherTrack());
        app.showInvoice(playlistID, playlistName);
        app.showInvoiceTotal(playlistID);
    }

//Set connection for SQL database
    private Connection connect() {
        String url = "jdbc:sqlite:C:\\Users\\ericv\\sql-jdbc.wiki\\sql-jdbc\\chinook.db";
        Connection conn = null;
        //System.out.println("Connection to SQLite has been established.");
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println("The Music Kiosk database is currently unavailable.  Please check our website for any posted outages, or contact customer support at 1-555-666-7777");
        }
        return conn;
    }
//View all tracks in tracks SQL table
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
                System.out.println("\nID: " + trackId + "\nTitle: " + name + "\nArtist: " + composer + "\nGenre: " + genre + "\nPrice: $" + unitPrice);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }
//Ask user to provide a playlist name
    public String getPlaylistName() {
        String playlistName = Console.getString("\nPlease name this playlist: ");
        return playlistName;
    }
//Create the playlist in SQL table and return the playlist ID as an integer
    public Integer createPlaylist(String playlistName) throws SQLException {
        String sql = "INSERT INTO playlists (Name)" +
                "VALUES (?)";
        String sql2 = "SELECT PlaylistID FROM playlists WHERE Name = ?";
        Connection conn = this.connect();
        int playlistID = 0;
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, playlistName);
            pstmt.executeUpdate();
            try (PreparedStatement pstmt2 = conn.prepareStatement(sql2)) {
                pstmt2.setString(1, playlistName);
                ResultSet rs = pstmt2.executeQuery();
                while (rs.next()) {
                    playlistID = rs.getInt("PlaylistId");
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            conn.close();
        }
        return playlistID;


    }
//Get amount of tracks for user input validation when selecting track ID
    public int findAmountOfTracks() throws SQLException {
        String sql = "SELECT Count(TrackId) as \"Number of Tracks\" FROM tracks;";
        Integer amountOfTracks = null;
        try (Connection conn = this.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                amountOfTracks = rs.getInt("Number of Tracks");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());

        }
        return amountOfTracks;
    }
//Ask user to provide ID of track to add to playlist
    public int getTrackID(int amountOfTracks) {

        Integer trackID = Console.getInt("\nPlease select the ID of the song you would like to add to the playlist: ", 0, amountOfTracks);
        return trackID;
    }
//Inserts playlistID and trackID into SQL playlist_tracks table
    public void insertTrackToPlaylist(Integer playlistID, Integer trackID) throws SQLException {
        String sql = "INSERT INTO playlist_track (PlaylistId, TrackID) VALUES (?, ?);";
        Connection conn = this.connect();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, playlistID);
            pstmt.setInt(2, trackID);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("This song is already on the playlist.  Please pick a different song.");
        } finally {
            conn.close();
            System.out.println("\n"+trackID + " added to playlist");
        }
    }
//Ask user if they would like to add another track
    public static boolean anotherTrack() {
        String choice = Console.getString("\nAdd another track? (y/n)", "y", "n");
        return choice.equalsIgnoreCase("y");
    }
//Display invoice for all songs on playlist and their prices
    public void showInvoice(Integer playlistID, String playlistName) throws SQLException {
        String sql = "SELECT playlist_track.TrackId as \"Track ID\", tracks.Name as Title, tracks.Composer as Artist, UnitPrice as Price, playlist_track.PlaylistId\n" +
                "FROM tracks\n" +
                "INNER JOIN playlist_track ON playlist_track.TrackId=tracks.TrackId AND playlist_track.PlaylistId = ?;";

        Connection conn = this.connect();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, playlistID);
            ResultSet rs = pstmt.executeQuery();
            System.out.println("\nInvoice for playlist "+ playlistName+":");
            while (rs.next()) {
                System.out.println("\nTrack ID: " + rs.getInt("Track ID")
                        + "\nTitle: " + rs.getString("Title")
                        + "\nArtist: " + rs.getString("Artist")
                        + "\nPrice: $" + rs.getBigDecimal("Price"));
            }
        }
    }
//Display sum of all song prices
    public void showInvoiceTotal(Integer playlistID) throws SQLException {
        String sql = "SELECT playlist_track.TrackId, SUM(UnitPrice) as Total, playlist_track.PlaylistId\n" +
                "FROM tracks\n" +
                "INNER JOIN playlist_track ON playlist_track.TrackId=tracks.TrackId AND playlist_track.PlaylistId = ?;";
        Connection conn = this.connect();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, playlistID);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                System.out.println("\nTotal Price: $" + rs.getBigDecimal("Total")+"\n\nThank you for using MusicKiosk.  Please check your email for a link to download your playlist. Have a nice day!");
            }

        }
    }
}


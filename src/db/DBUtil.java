package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtil {
    // --- GANTI DI SINI ---
    private static final String URL = "jdbc:mysql://localhost:3306/paket";
    private static final String USER = "root";
    private static final String PASS = ""; // Ganti dengan password MySQL Anda jika ada

    // Daftar driver saat kelas dimuat
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Gagal memuat MySQL JDBC Driver!", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
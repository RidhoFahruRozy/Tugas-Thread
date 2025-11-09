package dao;

import db.DBUtil;
import model.Package;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PackageDAO {

    /**
     * Metode ini thread-safe. Hanya satu thread (kurir)
     * yang bisa "mengklaim" paket pada satu waktu.
     */
    public synchronized Package claimNextPackage() throws SQLException {
        Package pkg = null;
        String selectSql = "SELECT * FROM packages WHERE status = 'PENDING' LIMIT 1";
        
        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false); 
            
            // 1. Cari paket PENDING
            try (PreparedStatement psSelect = conn.prepareStatement(selectSql)) {
                try (ResultSet rs = psSelect.executeQuery()) {
                    if (rs.next()) {
                        pkg = new Package();
                        pkg.setId(rs.getInt("id"));
                        pkg.setRecipientName(rs.getString("recipient_name"));
                        pkg.setAddress(rs.getString("address"));
                        pkg.setStatus("PROCESSING");
                    }
                }
            }

            // 2. Jika ada paket, update statusnya jadi PROCESSING
            if (pkg != null) {
                String updateSql = "UPDATE packages SET status = 'PROCESSING' WHERE id = ?";
                try (PreparedStatement psUpdate = conn.prepareStatement(updateSql)) {
                    psUpdate.setInt(1, pkg.getId());
                    psUpdate.executeUpdate();
                }
                conn.commit(); 
                return pkg;
            }
            
            conn.rollback(); 
            return null; // Tidak ada paket PENDING
            
        } catch (SQLException e) {
            System.err.println("Error saat klaim paket: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Update status paket setelah selesai dikirim
     */
    public void updatePackageStatus(int id, String status) throws SQLException {
        String sql = "UPDATE packages SET status = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }

    /**
     * Cek sisa paket di antrian
     */
    public int getPendingPackageCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM packages WHERE status = 'PENDING'";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }
}
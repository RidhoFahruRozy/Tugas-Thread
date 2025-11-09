import dao.PackageDAO;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import model.Package;

public class DeliveryApp {

    public static void main(String[] args) {
        // Buat "tim" dengan 3 kurir (3 thread)
        ExecutorService deliveryTeam = Executors.newFixedThreadPool(3);
        PackageDAO dao = new PackageDAO();

        System.out.println("=== PUSAT PENGIRIMAN DIBUKA ===");
        System.out.println("Tim kurir dengan 3 worker siap bertugas...");

        try {
            while (dao.getPendingPackageCount() > 0) {
                
                deliveryTeam.submit(() -> {
                    String workerName = Thread.currentThread().getName(); // Nama kurir
                    Package pkg = null;
                    try {
                        pkg = dao.claimNextPackage();

                        if (pkg != null) {
                            System.out.println("[" + workerName + "] MENGIRIM paket #" + pkg.getId() + " ke " + pkg.getRecipientName());
                            
                            Thread.sleep(2000 + (long) (Math.random() * 3000)); 
 
                            dao.updatePackageStatus(pkg.getId(), "DELIVERED");
                            System.out.println("[" + workerName + "] SELESAI kirim paket #" + pkg.getId() + ".");
                        } else {
                            System.out.println("[" + workerName + "] Cek antrian, tapi sudah kosong.");
                        }
                    } catch (InterruptedException | SQLException e) {
                        System.err.println("[" + workerName + "] Error: " + e.getMessage());
                    }
                });
                
                // Beri jeda 1 detik sebelum cek gudang lagi
                Thread.sleep(1000); 
            }
            
            System.out.println("=== SEMUA PAKET PENDING TELAH DIAMBIL ===");

        } catch (InterruptedException | SQLException e) {
            e.printStackTrace();
        } finally {
            System.out.println("Menunggu semua kurir menyelesaikan pengiriman terakhir...");
            deliveryTeam.shutdown();
            try {
                deliveryTeam.awaitTermination(1, TimeUnit.MINUTES);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("=== PUSAT PENGIRIMAN DITUTUP ===");
        }
    }
}
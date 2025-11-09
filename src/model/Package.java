package model;

public class Package{
    private int id;
    private String recipientName;
    private String address;
    private String status;

    // Getter dan Setter
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getRecipientName() { return recipientName; }
    public void setRecipientName(String name) { this.recipientName = name; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
public class Property {
    private int id;
    private String city;
    private String type;
    private double price;
    private String address; // Address field
    private int ownerID;

    // Constructor with ownerID
    public Property(int id, String city, String type, double price, String address, int ownerID) {
        this.id = id;
        this.city = city;
        this.type = type;
        this.price = price;
        this.address = address; // Initialize address
        this.ownerID = ownerID; // Initialize ownerID
    }

    // Overloaded constructor without ownerID
    public Property(int id, String city, String type, double price, String address) {
        this(id, city, type, price, address, 0); // Default ownerID to 0
    }

    // Getters and setters

    // Getter for ownerID
    public int getOwnerID() {
        return ownerID;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return String.format("Қала: %s | Тип: %s | Баға: %.2f | Мекен-жай: %s", city, type, price,
                address);
    }
}

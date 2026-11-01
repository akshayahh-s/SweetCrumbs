package bakery;

import java.util.ArrayList;
import java.util.List;

public class Order {
    // The database now generates the order ID (starting at 1001).
    // A new order has ID 0 until AppData.addOrder() saves it.
    private int orderId;
    private final String customerName;
    private final String phone;
    private final String address;
    private final String deliveryDate;
    private final String bakerNote;
    private final List<CartItem> items;
    private final double total;
    private String status;

    /** Used by the checkout screen to create a brand-new order. */
    public Order(String customerName, String phone, String address,
                 String deliveryDate, String bakerNote,
                 List<CartItem> cartItems, double total) {
        this(0, customerName, phone, address, deliveryDate, bakerNote,
                cartItems, total, "Pending");
    }

    /** Used by AppData when loading an existing order from the database. */
    public Order(int orderId, String customerName, String phone, String address,
                 String deliveryDate, String bakerNote,
                 List<CartItem> cartItems, double total, String status) {
        this.orderId = orderId;
        this.customerName = customerName;
        this.phone = phone;
        this.address = address;
        this.deliveryDate = deliveryDate;
        this.bakerNote = bakerNote;
        this.items = new ArrayList<>(cartItems);
        this.total = total;
        this.status = status;
    }

    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }
    public String getCustomerName() { return customerName; }
    public String getPhone() { return phone; }
    public String getAddress() { return address; }
    public String getDeliveryDate() { return deliveryDate; }
    public String getBakerNote() { return bakerNote; }
    public List<CartItem> getItems() { return items; }
    public double getTotal() { return total; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}

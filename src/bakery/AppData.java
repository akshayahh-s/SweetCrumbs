package bakery;

import com.mysql.cj.jdbc.Driver;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Back-end data layer. Every method keeps the same name and meaning as the
 * old in-memory version, but now reads/writes the MySQL database using JDBC,
 * so the screens in BakeryApp work without redesign.
 */
public class AppData {

    // Products rarely change, so they are loaded once and kept in memory.
    private final List<Product> products = new ArrayList<>();

    public AppData() {
        loadProducts();
    }

    // ------------------------------------------------------------------
    // PRODUCTS
    // ------------------------------------------------------------------

    private void loadProducts() {
        String sql = "SELECT product_id, name, category, price, description, emoji "
                   + "FROM products ORDER BY product_id";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                products.add(new Product(
                        rs.getInt("product_id"),
                        rs.getString("name"),
                        rs.getString("category"),
                        rs.getDouble("price"),
                        rs.getString("description"),
                        rs.getString("emoji")));
            }
        } catch (SQLException e) {
            throw dbError("loading products", e);
        }
    }

    public List<Product> getProducts() { return products; }

    // ------------------------------------------------------------------
    // USERS
    // ------------------------------------------------------------------

    public List<User> getUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT name, email, phone, is_admin FROM users ORDER BY user_id";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                // Passwords are never loaded back into the app.
                users.add(new User(rs.getString("name"), rs.getString("email"),
                        rs.getString("phone"), "", rs.getBoolean("is_admin")));
            }
        } catch (SQLException e) {
            throw dbError("loading users", e);
        }
        return users;
    }

    /** Returns the matching user, or null if the email/password is wrong. */
    public User authenticate(String email, String password) {
        String sql = "SELECT name, email, phone, is_admin FROM users "
                   + "WHERE LOWER(email) = LOWER(?) AND password_hash = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setString(2, hash(password));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new User(rs.getString("name"), rs.getString("email"),
                            rs.getString("phone"), password, rs.getBoolean("is_admin"));
                }
            }
        } catch (SQLException e) {
            throw dbError("logging in", e);
        }
        return null;
    }

    public void addUser(User user) {
        String sql = "INSERT INTO users (name, email, phone, password_hash, is_admin) "
                   + "VALUES (?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPhone());
            ps.setString(4, hash(user.getPassword()));
            ps.setBoolean(5, user.isAdmin());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw dbError("creating account", e);
        }
    }

    public boolean emailExists(String email) {
        String sql = "SELECT 1 FROM users WHERE LOWER(email) = LOWER(?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw dbError("checking email", e);
        }
    }

    // ------------------------------------------------------------------
    // ORDERS
    // ------------------------------------------------------------------

    /**
     * Saves the order and all its items in ONE transaction: either everything
     * is saved or nothing is. The generated order ID is set on the Order object.
     */
    public void addOrder(Order order) {
        String orderSql = "INSERT INTO orders (customer_name, phone, address, "
                        + "delivery_date, baker_note, total, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        String itemSql = "INSERT INTO order_items (order_id, product_id, quantity, "
                       + "unit_price, cake_message, special_instructions) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);
            try {
                int newId;
                try (PreparedStatement ps = con.prepareStatement(orderSql,
                        Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, order.getCustomerName());
                    ps.setString(2, order.getPhone());
                    ps.setString(3, order.getAddress());
                    ps.setString(4, order.getDeliveryDate());
                    ps.setString(5, order.getBakerNote());
                    ps.setDouble(6, order.getTotal());
                    ps.setString(7, order.getStatus());
                    ps.executeUpdate();

                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        keys.next();
                        newId = keys.getInt(1);
                    }
                }

                try (PreparedStatement ps = con.prepareStatement(itemSql)) {
                    for (CartItem item : order.getItems()) {
                        ps.setInt(1, newId);
                        ps.setInt(2, item.getProduct().getId());
                        ps.setInt(3, item.getQuantity());
                        ps.setDouble(4, item.getProduct().getPrice());
                        ps.setString(5, item.getCakeMessage());
                        ps.setString(6, item.getSpecialInstructions());
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }

                con.commit();
                order.setOrderId(newId);
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw dbError("placing order", e);
        }
    }

    /** Loads all orders (with their items) fresh from the database. */
    public List<Order> getOrders() {
        Map<Integer, Product> productById = new HashMap<>();
        for (Product p : products) productById.put(p.getId(), p);

        Map<Integer, List<CartItem>> itemsByOrder = new HashMap<>();
        List<Order> orders = new ArrayList<>();

        String itemSql = "SELECT order_id, product_id, quantity, cake_message, "
                       + "special_instructions FROM order_items ORDER BY item_id";
        String orderSql = "SELECT order_id, customer_name, phone, address, delivery_date, "
                        + "baker_note, total, status FROM orders ORDER BY order_id";

        try (Connection con = DBConnection.getConnection()) {

            try (PreparedStatement ps = con.prepareStatement(itemSql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Product product = productById.get(rs.getInt("product_id"));
                    if (product == null) continue;
                    CartItem item = new CartItem(product, rs.getInt("quantity"),
                            nullToEmpty(rs.getString("cake_message")),
                            nullToEmpty(rs.getString("special_instructions")));
                    itemsByOrder.computeIfAbsent(rs.getInt("order_id"),
                            k -> new ArrayList<>()).add(item);
                }
            }

            try (PreparedStatement ps = con.prepareStatement(orderSql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("order_id");
                    orders.add(new Order(id,
                            rs.getString("customer_name"),
                            rs.getString("phone"),
                            rs.getString("address"),
                            rs.getString("delivery_date"),
                            nullToEmpty(rs.getString("baker_note")),
                            itemsByOrder.getOrDefault(id, new ArrayList<>()),
                            rs.getDouble("total"),
                            rs.getString("status")));
                }
            }
        } catch (SQLException e) {
            throw dbError("loading orders", e);
        }
        return orders;
    }

    public void updateOrderStatus(int orderId, String status) {
        String sql = "UPDATE orders SET status = ? WHERE order_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setInt(2, orderId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw dbError("updating order status", e);
        }
    }

    // ------------------------------------------------------------------
    // HELPERS
    // ------------------------------------------------------------------

    /** SHA-256 hash as lowercase hex (matches MySQL's SHA2(x, 256)). */
    private static String hash(String text) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(text.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    private static String nullToEmpty(String s) { return s == null ? "" : s; }

    private static RuntimeException dbError(String action, SQLException e) {
        return new RuntimeException("Database error while " + action + ": " + e.getMessage(), e);
    }
}

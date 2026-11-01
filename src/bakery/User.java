package bakery;

public class User {
    private final String name;
    private final String email;
    private final String phone;
    private final String password;
    private final boolean admin;

    public User(String name, String email, String phone,
                String password, boolean admin) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.admin = admin;
    }

    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public boolean isAdmin() { return admin; }

    /** Needed so AppData can hash the password before saving it to the database. */
    public String getPassword() { return password; }

    public boolean passwordMatches(String password) {
        return this.password.equals(password);
    }
}

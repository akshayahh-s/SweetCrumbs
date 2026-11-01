package bakery;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.List;

/**
 * Sweet Crumbs - Home Bakery Desktop Application.
 * Enhanced with responsive product grid, food photo rendering,
 * aesthetic artisan bakery styling, and smooth interactions.
 */
public class BakeryApp extends JFrame {
    private final AppData data = new AppData();
    private final Cart cart = new Cart();
    private User currentUser;

    private final JPanel root = new JPanel(new BorderLayout());
    private String currentPage = "HOME";

    public BakeryApp() {
        setTitle("Sweet Crumbs • Artisan Home Bakery");
        setSize(1220, 820);
        setMinimumSize(new Dimension(980, 680));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Window icon
        try {
            Image icon = ImageLoader.getProductImage(1);
            if (icon != null) setIconImage(icon);
        } catch (Exception ignored) {}

        root.setBackground(Colors.CREAM);
        setContentPane(root);
        showHome();
    }

    public AppData getData() { return data; }
    public Cart getCart() { return cart; }
    public User getCurrentUser() { return currentUser; }

    private void setPage(JPanel page, String pageName) {
        this.currentPage = pageName;
        root.removeAll();
        root.add(createNavBar(), BorderLayout.NORTH);
        root.add(page, BorderLayout.CENTER);
        root.revalidate();
        root.repaint();
    }

    private JPanel createNavBar() {
        JPanel nav = new JPanel(new BorderLayout());
        nav.setBackground(Color.WHITE);
        nav.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Colors.BORDER),
                new EmptyBorder(12, 32, 12, 32)
        ));

        // Brand Logo + Subtitle
        JPanel brand = new JPanel();
        brand.setOpaque(false);
        brand.setLayout(new BoxLayout(brand, BoxLayout.Y_AXIS));

        JLabel logo = new JLabel("🧁 SWEET CRUMBS");
        logo.setFont(new Font("Serif", Font.BOLD, 22));
        logo.setForeground(Colors.ESPRESSO);
        logo.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logo.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                showHome();
            }
        });

        JLabel subtitle = new JLabel("ARTISAN HOME BAKERY • FRESH DAILY");
        subtitle.setFont(new Font("Segoe UI", Font.BOLD, 10));
        subtitle.setForeground(Colors.CARAMEL);

        brand.add(logo);
        brand.add(Box.createVerticalStrut(2));
        brand.add(subtitle);

        // Navigation Links
        JPanel links = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        links.setOpaque(false);

        JButton home = UI.pillButton("Home", "HOME".equals(currentPage));
        JButton menu = UI.pillButton("Our Menu", "MENU".equals(currentPage));
        int cartCount = cart.getItems().stream().mapToInt(CartItem::getQuantity).sum();
        JButton cartBtn = UI.pillButton("🛒 Cart (" + cartCount + ")", "CART".equals(currentPage));
        JButton orders = UI.pillButton("My Orders", "ORDERS".equals(currentPage));

        home.addActionListener(e -> showHome());
        menu.addActionListener(e -> showMenu());
        cartBtn.addActionListener(e -> showCart());
        orders.addActionListener(e -> showOrders());

        links.add(home);
        links.add(menu);
        links.add(cartBtn);

        if (currentUser != null && !currentUser.isAdmin()) {
            links.add(orders);
        }

        if (currentUser != null && currentUser.isAdmin()) {
            JButton admin = UI.pillButton("⚙ Admin Panel", "ADMIN".equals(currentPage));
            admin.addActionListener(e -> showAdmin());
            links.add(admin);
        }

        // User info or Login button
        if (currentUser == null) {
            JButton login = UI.primaryButton("Sign In");
            login.addActionListener(e -> showLogin());
            links.add(login);
        } else {
            JLabel userGreeting = new JLabel("👋 " + currentUser.getName());
            userGreeting.setFont(new Font("Segoe UI", Font.BOLD, 13));
            userGreeting.setForeground(Colors.ESPRESSO);
            userGreeting.setBorder(new EmptyBorder(0, 6, 0, 4));

            JButton logout = UI.secondaryButton("Sign Out");
            logout.addActionListener(e -> {
                currentUser = null;
                showHome();
            });

            links.add(userGreeting);
            links.add(logout);
        }

        nav.add(brand, BorderLayout.WEST);
        nav.add(links, BorderLayout.EAST);
        return nav;
    }

    // ------------------------------------------------------------------
    // HOME SCREEN
    // ------------------------------------------------------------------

    public void showHome() {
        JPanel page = new JPanel(new BorderLayout());
        page.setBackground(Colors.CREAM);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(Colors.CREAM);

        // 1. HERO BANNER
        JPanel hero = createHeroBanner();

        // 2. HIGHLIGHT FEATURES ROW
        JPanel features = createFeaturesRow();

        // 3. TODAY'S SPECIALS (FEATURED PRODUCTS)
        JPanel specials = createSpecialsSection();

        // 4. CUSTOMER REVIEWS / TESTIMONIALS
        JPanel testimonials = createTestimonialsSection();

        // 5. FOOTER / ABOUT
        JPanel footer = createFooterSection();

        content.add(hero);
        content.add(Box.createVerticalStrut(25));
        content.add(features);
        content.add(Box.createVerticalStrut(30));
        content.add(specials);
        content.add(Box.createVerticalStrut(30));
        content.add(testimonials);
        content.add(Box.createVerticalStrut(30));
        content.add(footer);

        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(20);
        scroll.getViewport().setBackground(Colors.CREAM);

        page.add(scroll, BorderLayout.CENTER);
        setPage(page, "HOME");
    }

    private JPanel createHeroBanner() {
        JPanel heroContainer = new JPanel(new BorderLayout());
        heroContainer.setOpaque(false);
        heroContainer.setBorder(new EmptyBorder(30, 45, 10, 45));

        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Warm gradient background
                GradientPaint gp = new GradientPaint(0, 0, new Color(255, 253, 250),
                        getWidth(), getHeight(), new Color(254, 240, 230));
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24);
                g2.setColor(Colors.BORDER);
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 24, 24);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setLayout(new BorderLayout(25, 20));
        card.setBorder(new EmptyBorder(40, 45, 40, 45));

        // Left: Text and CTA
        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));

        JLabel tag = new JLabel("✨ BAKED FRESH TO ORDER • 100% HOMEMADE");
        tag.setFont(new Font("Segoe UI", Font.BOLD, 12));
        tag.setForeground(Colors.DARK_PINK);

        JLabel title = new JLabel("<html><font color='#301C14'>Delight in Every Crumb.</font><br><font color='#D0762A'>Baked with Love.</font></html>");
        title.setFont(new Font("Serif", Font.BOLD, 42));

        JLabel desc = new JLabel(
                "<html><p style='width: 480px; line-height: 1.5; color: #725244;'>"
                + "From decadent chocolate truffle cakes and rich fudgy brownies to golden "
                + "piped butter cookies and glazed cinnamon rolls — handcrafted daily using "
                + "premium chocolate, pure butter, and fresh fruit for your special celebrations."
                + "</p></html>"
        );
        desc.setFont(new Font("Segoe UI", Font.PLAIN, 15));

        JPanel ctaRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        ctaRow.setOpaque(false);

        JButton orderNow = UI.primaryButton("EXPLORE OUR MENU ➔");
        orderNow.setFont(new Font("Segoe UI", Font.BOLD, 14));
        orderNow.setBorder(new EmptyBorder(12, 26, 12, 26));
        orderNow.addActionListener(e -> showMenu());

        JButton viewCart = UI.secondaryButton("View Cart (" + cart.getItems().size() + ")");
        viewCart.setFont(new Font("Segoe UI", Font.BOLD, 14));
        viewCart.setBorder(new EmptyBorder(12, 22, 12, 22));
        viewCart.addActionListener(e -> showCart());

        ctaRow.add(orderNow);
        ctaRow.add(viewCart);

        left.add(tag);
        left.add(Box.createVerticalStrut(10));
        left.add(title);
        left.add(Box.createVerticalStrut(14));
        left.add(desc);
        left.add(Box.createVerticalStrut(24));
        left.add(ctaRow);

        // Right: Featured Hero Photo Trio (Cake, Brownie Box, Cinnamon Roll)
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        right.setOpaque(false);

        JLabel heroPic1 = new JLabel(ImageLoader.getProductIcon(1, 190, 240, 18));
        heroPic1.setToolTipText("Chocolate Truffle Cake");

        JLabel heroPic2 = new JLabel(ImageLoader.getProductIcon(12, 190, 240, 18));
        heroPic2.setToolTipText("Assorted Brownie Box");

        right.add(heroPic1);
        right.add(heroPic2);

        card.add(left, BorderLayout.CENTER);
        card.add(right, BorderLayout.EAST);
        heroContainer.add(card, BorderLayout.CENTER);
        return heroContainer;
    }

    private JPanel createFeaturesRow() {
        JPanel row = new JPanel(new GridLayout(1, 4, 18, 18));
        row.setOpaque(false);
        row.setBorder(new EmptyBorder(0, 45, 0, 45));

        row.add(createFeaturePill("🎂 100% Homemade", "Pure recipes, zero preservatives"));
        row.add(createFeaturePill("🧈 Pure Dairy Butter", "Rich flavor in every batch"));
        row.add(createFeaturePill("💌 Custom Cake Messages", "Personalized lettering on orders"));
        row.add(createFeaturePill("🚚 Fresh Delivery", "Hand-delivered to your doorstep"));

        return row;
    }

    private JPanel createFeaturePill(String title, String sub) {
        JPanel p = UI.roundedCard(14);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(Color.WHITE);

        JLabel t = new JLabel(title);
        t.setFont(new Font("Segoe UI", Font.BOLD, 14));
        t.setForeground(Colors.ESPRESSO);

        JLabel s = new JLabel(sub);
        s.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        s.setForeground(Colors.MUTED_TEXT);

        p.add(t);
        p.add(Box.createVerticalStrut(3));
        p.add(s);
        return p;
    }

    private JPanel createSpecialsSection() {
        JPanel section = new JPanel(new BorderLayout(0, 18));
        section.setOpaque(false);
        section.setBorder(new EmptyBorder(0, 45, 0, 45));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel title = UI.heading("Chef's Recommendations ⭐", 26);
        JLabel subtitle = new JLabel("Our customers' most loved treats, handpicked for you");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(Colors.MUTED_TEXT);

        JButton seeAll = UI.secondaryButton("View Full Menu (" + data.getProducts().size() + ") ➔");
        seeAll.addActionListener(e -> showMenu());

        JPanel titleBox = new JPanel();
        titleBox.setOpaque(false);
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titleBox.add(title);
        titleBox.add(Box.createVerticalStrut(3));
        titleBox.add(subtitle);

        header.add(titleBox, BorderLayout.WEST);
        header.add(seeAll, BorderLayout.EAST);

        // Featured products (Truffle Cake, Fudge Brownie, Cinnamon Roll, Fruit Tart)
        JPanel cardsGrid = new JPanel(new GridLayout(1, 4, 18, 18));
        cardsGrid.setOpaque(false);

        List<Product> products = data.getProducts();
        int[] featuredIds = {1, 6, 10, 11};
        for (int id : featuredIds) {
            for (Product p : products) {
                if (p.getId() == id) {
                    cardsGrid.add(createProductCard(p));
                    break;
                }
            }
        }

        section.add(header, BorderLayout.NORTH);
        section.add(cardsGrid, BorderLayout.CENTER);
        return section;
    }

    private JPanel createTestimonialsSection() {
        JPanel section = new JPanel(new BorderLayout(0, 15));
        section.setOpaque(false);
        section.setBorder(new EmptyBorder(0, 45, 0, 45));

        JLabel title = UI.heading("Loved by Our Customers ❤️", 24);
        section.add(title, BorderLayout.NORTH);

        JPanel reviewsRow = new JPanel(new GridLayout(1, 3, 18, 18));
        reviewsRow.setOpaque(false);

        reviewsRow.add(createReviewCard("★★★★★", "The Chocolate Truffle Cake was the centerpiece of my sister's birthday! Perfectly moist with divine ganache.", "— Priya Sharma", "Bangalore"));
        reviewsRow.add(createReviewCard("★★★★★", "The Cinnamon Rolls are out of this world. Soft, warm glaze, and aromatic cinnamon. A weekly weekend treat!", "— Rohan Mehta", "Indiranagar"));
        reviewsRow.add(createReviewCard("★★★★★", "Ordered the Brownie Box for a house party. Everyone asked where I bought it from. 10/10 presentation & taste!", "— Ananya Das", "Koramangala"));

        section.add(reviewsRow, BorderLayout.CENTER);
        return section;
    }

    private JPanel createReviewCard(String stars, String text, String author, String city) {
        JPanel p = UI.roundedCard(16);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(Color.WHITE);

        JLabel st = new JLabel(stars);
        st.setFont(new Font("Segoe UI", Font.BOLD, 15));
        st.setForeground(Colors.GOLD_STAR);

        JLabel quote = new JLabel("<html><p style='color: #4A332A; font-style: italic; line-height: 1.4;'>" + text + "</p></html>");
        quote.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JLabel auth = new JLabel(author + " • " + city);
        auth.setFont(new Font("Segoe UI", Font.BOLD, 12));
        auth.setForeground(Colors.CARAMEL);

        p.add(st);
        p.add(Box.createVerticalStrut(8));
        p.add(quote);
        p.add(Box.createVerticalStrut(12));
        p.add(auth);
        return p;
    }

    private JPanel createFooterSection() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(Color.WHITE);
        footer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Colors.BORDER),
                new EmptyBorder(25, 45, 25, 45)
        ));

        JLabel left = new JLabel("<html><b>Sweet Crumbs Home Bakery</b><br>"
                + "<font color='#8A6D60'>Handcrafted artisanal baked goods made fresh to order.<br>"
                + "Order timings: 8:00 AM – 9:00 PM Daily • Delivery across town</font></html>");
        left.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JLabel right = new JLabel("<html><div style='text-align: right;'><b>📞 Call / WhatsApp: +91 98765 43210</b><br>"
                + "<font color='#8A6D60'>sweetcrumbs.homebakery@gmail.com<br>"
                + "FSSAI Registered • 100% Eggless Options Available</font></div></html>");
        right.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        footer.add(left, BorderLayout.WEST);
        footer.add(right, BorderLayout.EAST);
        return footer;
    }

    // ------------------------------------------------------------------
    // PRODUCT CARDS & MENU SCREEN
    // ------------------------------------------------------------------

    private JPanel createProductCard(Product product) {
        JPanel card = UI.card();
        card.setLayout(new BorderLayout(0, 10));
        card.setPreferredSize(new Dimension(270, 375));
        card.setMinimumSize(new Dimension(240, 360));

        // 1. Top Image with rounded top corners
        JLabel imageLabel = new JLabel(product.getTopRoundedImageIcon(270, 175, 18));
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        imageLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                showProductDetails(product);
            }
        });

        // 2. Info Panel
        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setBorder(new EmptyBorder(0, 12, 10, 12));

        // Category badge and Rating
        JPanel meta = new JPanel(new BorderLayout());
        meta.setOpaque(false);
        meta.add(UI.categoryBadge(product.getCategory()), BorderLayout.WEST);

        JLabel rating = new JLabel("★ 4.9");
        rating.setFont(new Font("Segoe UI", Font.BOLD, 12));
        rating.setForeground(Colors.GOLD_STAR);
        meta.add(rating, BorderLayout.EAST);

        // Product Name
        JLabel name = new JLabel(product.getName());
        name.setFont(new Font("Segoe UI", Font.BOLD, 15));
        name.setForeground(Colors.ESPRESSO);
        name.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Truncated Description
        String descText = product.getDescription();
        if (descText.length() > 55) descText = descText.substring(0, 52) + "...";
        JLabel desc = new JLabel("<html><font color='#856A5E'>" + descText + "</font></html>");
        desc.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        desc.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Price and Order Button
        JPanel bottomRow = new JPanel(new BorderLayout());
        bottomRow.setOpaque(false);

        JLabel price = new JLabel("₹" + money(product.getPrice()));
        price.setFont(new Font("Segoe UI", Font.BOLD, 17));
        price.setForeground(Colors.CARAMEL);

        JButton viewBtn = UI.primaryButton("Details ➔");
        viewBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        viewBtn.setBorder(new EmptyBorder(6, 14, 6, 14));
        viewBtn.addActionListener(e -> showProductDetails(product));

        bottomRow.add(price, BorderLayout.WEST);
        bottomRow.add(viewBtn, BorderLayout.EAST);

        info.add(meta);
        info.add(Box.createVerticalStrut(6));
        info.add(name);
        info.add(Box.createVerticalStrut(4));
        info.add(desc);
        info.add(Box.createVerticalGlue());
        info.add(Box.createVerticalStrut(8));
        info.add(bottomRow);

        card.add(imageLabel, BorderLayout.NORTH);
        card.add(info, BorderLayout.CENTER);
        return card;
    }

    public void showMenu() {
        JPanel page = new JPanel(new BorderLayout(15, 15));
        page.setBackground(Colors.CREAM);
        page.setBorder(new EmptyBorder(25, 45, 25, 45));

        // Header controls (Title, Search, Filter Pills)
        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));

        JPanel titleRow = new JPanel(new BorderLayout());
        titleRow.setOpaque(false);

        JLabel title = UI.heading("Explore Our Bakery Menu 🥐", 30);
        JLabel countLabel = new JLabel("Showing all " + data.getProducts().size() + " artisan delicacies");
        countLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        countLabel.setForeground(Colors.MUTED_TEXT);

        JPanel titleBox = new JPanel();
        titleBox.setOpaque(false);
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titleBox.add(title);
        titleBox.add(Box.createVerticalStrut(3));
        titleBox.add(countLabel);

        // Search Field with styled rounded border
        JTextField search = new JTextField(18);
        search.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        search.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Colors.BORDER, 1, true),
                new EmptyBorder(8, 12, 8, 12)
        ));
        search.setToolTipText("Search by product name or category...");

        JPanel searchWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchWrap.setOpaque(false);
        JLabel searchIcon = new JLabel("🔍 Search: ");
        searchIcon.setFont(new Font("Segoe UI", Font.BOLD, 13));
        searchIcon.setForeground(Colors.ESPRESSO);
        searchWrap.add(searchIcon);
        searchWrap.add(search);

        titleRow.add(titleBox, BorderLayout.WEST);
        titleRow.add(searchWrap, BorderLayout.EAST);

        // Category Filter Pills
        JPanel filterRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filterRow.setOpaque(false);
        filterRow.setBorder(new EmptyBorder(15, 0, 10, 0));

        String[] categories = {"All", "Cake", "Cupcake", "Brownie", "Cookie", "Pastry", "Gift Box"};
        final String[] activeCategory = {"All"};

        // Responsive grid container
        JPanel grid = new JPanel();
        grid.setOpaque(false);

        JScrollPane scroll = new JScrollPane(grid);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(20);
        scroll.getViewport().setBackground(Colors.CREAM);

        Runnable refreshGrid = () -> {
            String query = search.getText().trim();
            populateResponsiveGrid(grid, scroll, query, activeCategory[0]);
            long matchCount = data.getProducts().stream()
                    .filter(p -> (activeCategory[0].equals("All") || p.getCategory().equalsIgnoreCase(activeCategory[0])))
                    .filter(p -> (query.isEmpty() || p.getName().toLowerCase().contains(query.toLowerCase()) || p.getCategory().toLowerCase().contains(query.toLowerCase())))
                    .count();
            countLabel.setText("Showing " + matchCount + " freshly baked delicacies");
        };

        for (String cat : categories) {
            JButton b = UI.pillButton(cat, cat.equals(activeCategory[0]));
            b.addActionListener(e -> {
                activeCategory[0] = cat;
                // Update button active states
                for (Component c : filterRow.getComponents()) {
                    if (c instanceof JButton) {
                        JButton btn = (JButton) c;
                        boolean isActive = btn.getText().equals(cat);
                        btn.setForeground(isActive ? Color.WHITE : Colors.BROWN);
                    }
                }
                filterRow.repaint();
                refreshGrid.run();
            });
            filterRow.add(b);
        }

        search.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { refreshGrid.run(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { refreshGrid.run(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { refreshGrid.run(); }
        });

        // Recalculate columns dynamically on window resize
        scroll.getViewport().addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                refreshGrid.run();
            }
        });

        top.add(titleRow);
        top.add(filterRow);

        page.add(top, BorderLayout.NORTH);
        page.add(scroll, BorderLayout.CENTER);

        // Initial population
        refreshGrid.run();
        setPage(page, "MENU");
    }

    private void populateResponsiveGrid(JPanel grid, JScrollPane scroll, String search, String category) {
        grid.removeAll();
        String q = search == null ? "" : search.toLowerCase().trim();

        int availableWidth = scroll.getViewport().getWidth();
        if (availableWidth <= 0) availableWidth = 1100;

        int cardWidth = 270;
        int gap = 20;
        int cols = Math.max(1, Math.min(4, (availableWidth - 30) / (cardWidth + gap)));
        grid.setLayout(new GridLayout(0, cols, gap, gap));

        int matched = 0;
        for (Product p : data.getProducts()) {
            boolean categoryMatch = category.equals("All")
                    || p.getCategory().equalsIgnoreCase(category);
            boolean searchMatch = q.isEmpty()
                    || p.getName().toLowerCase().contains(q)
                    || p.getCategory().toLowerCase().contains(q);

            if (categoryMatch && searchMatch) {
                grid.add(createProductCard(p));
                matched++;
            }
        }

        if (matched == 0) {
            grid.setLayout(new BorderLayout());
            JLabel empty = new JLabel("<html><center><h3>No products found matching \"" + escape(q) + "\"</h3>"
                    + "<p>Try searching for cakes, brownies, cookies or cupcakes.</p></center></html>");
            empty.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            empty.setForeground(Colors.MUTED_TEXT);
            empty.setHorizontalAlignment(SwingConstants.CENTER);
            grid.add(empty, BorderLayout.CENTER);
        }

        grid.revalidate();
        grid.repaint();
    }

    // ------------------------------------------------------------------
    // PRODUCT DETAILS MODAL
    // ------------------------------------------------------------------

    private void showProductDetails(Product product) {
        JDialog dialog = new JDialog(this, product.getName() + " • Sweet Crumbs", true);
        dialog.setSize(680, 740);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel();
        panel.setBackground(Colors.WARM_BG);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(25, 35, 25, 35));

        // High-res Food Image (440x250)
        JLabel imageLabel = new JLabel(product.getImageIcon(580, 260, 20));
        imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Header info: Category badge, ID, rating
        JPanel metaRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        metaRow.setOpaque(false);
        metaRow.add(UI.categoryBadge(product.getCategory()));
        JLabel star = new JLabel("★★★★★ 4.9 (48 ratings)");
        star.setFont(new Font("Segoe UI", Font.BOLD, 13));
        star.setForeground(Colors.GOLD_STAR);
        metaRow.add(star);
        metaRow.add(UI.badge("FRESH TO ORDER", Colors.SUCCESS_LIGHT, Colors.SUCCESS));

        JLabel name = UI.centeredHeading(product.getName(), 28);

        JTextArea desc = new JTextArea(product.getDescription());
        desc.setWrapStyleWord(true);
        desc.setLineWrap(true);
        desc.setEditable(false);
        desc.setOpaque(false);
        desc.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        desc.setForeground(Colors.MUTED_TEXT);
        desc.setAlignmentX(Component.CENTER_ALIGNMENT);
        desc.setMaximumSize(new Dimension(580, 60));

        // Pricing & Quantity Controls
        final int[] qty = {1};
        JLabel dynamicPrice = UI.centeredHeading("₹" + money(product.getPrice()), 24);
        dynamicPrice.setForeground(Colors.CARAMEL);

        // Customization Inputs
        JTextField cakeMessage = new JTextField();
        cakeMessage.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cakeMessage.setMaximumSize(new Dimension(580, 42));
        cakeMessage.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Colors.BORDER, 1, true),
                "✍️ Custom message on cake / packaging (optional)"
        ));

        JTextArea instructions = new JTextArea(3, 40);
        instructions.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        instructions.setLineWrap(true);
        instructions.setWrapStyleWord(true);
        JScrollPane instructionScroll = new JScrollPane(instructions);
        instructionScroll.setMaximumSize(new Dimension(580, 75));
        instructionScroll.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Colors.BORDER, 1, true),
                "📝 Dietary requests or notes for baker (optional)"
        ));

        // Quantity Stepper
        JPanel quantityPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        quantityPanel.setOpaque(false);

        JLabel quantityLabel = new JLabel("  Quantity: 1  ");
        quantityLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        quantityLabel.setForeground(Colors.ESPRESSO);

        JButton minus = UI.pillButton(" - ", false);
        JButton plus = UI.pillButton(" + ", false);

        JButton addBtn = UI.primaryButton("🛒 ADD TO CART • ₹" + money(product.getPrice()));
        addBtn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        addBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        addBtn.setBorder(new EmptyBorder(12, 35, 12, 35));

        Runnable updateQtyDisplay = () -> {
            quantityLabel.setText("  Quantity: " + qty[0] + "  ");
            double total = product.getPrice() * qty[0];
            dynamicPrice.setText("₹" + money(total));
            addBtn.setText("🛒 ADD TO CART • ₹" + money(total));
        };

        minus.addActionListener(e -> {
            if (qty[0] > 1) {
                qty[0]--;
                updateQtyDisplay.run();
            }
        });

        plus.addActionListener(e -> {
            qty[0]++;
            updateQtyDisplay.run();
        });

        quantityPanel.add(minus);
        quantityPanel.add(quantityLabel);
        quantityPanel.add(plus);

        addBtn.addActionListener(e -> {
            cart.addItem(product, qty[0], cakeMessage.getText().trim(), instructions.getText().trim());
            dialog.dispose();
            showMenu();
            JOptionPane.showMessageDialog(this,
                    "<html><h3>Added to Cart! 🎉</h3>"
                    + "<b>" + product.getName() + "</b> (Qty: " + qty[0] + ")<br>"
                    + "Subtotal: <b>₹" + money(product.getPrice() * qty[0]) + "</b></html>",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
        });

        panel.add(imageLabel);
        panel.add(Box.createVerticalStrut(15));
        panel.add(metaRow);
        panel.add(Box.createVerticalStrut(8));
        panel.add(name);
        panel.add(Box.createVerticalStrut(10));
        panel.add(desc);
        panel.add(Box.createVerticalStrut(15));
        panel.add(cakeMessage);
        panel.add(Box.createVerticalStrut(10));
        panel.add(instructionScroll);
        panel.add(Box.createVerticalStrut(15));
        panel.add(dynamicPrice);
        panel.add(Box.createVerticalStrut(8));
        panel.add(quantityPanel);
        panel.add(Box.createVerticalStrut(20));
        panel.add(addBtn);

        JScrollPane scroll = new JScrollPane(panel);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(18);
        dialog.add(scroll);
        dialog.setVisible(true);
    }

    // ------------------------------------------------------------------
    // CART & CHECKOUT
    // ------------------------------------------------------------------

    public void showCart() {
        JPanel page = new JPanel(new BorderLayout());
        page.setBackground(Colors.CREAM);
        page.setBorder(new EmptyBorder(25, 45, 25, 45));

        JLabel title = UI.centeredHeading("Your Shopping Cart 🛒", 30);
        page.add(title, BorderLayout.NORTH);

        JPanel content = new JPanel();
        content.setBackground(Colors.CREAM);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        if (cart.isEmpty()) {
            JPanel emptyCard = UI.card();
            emptyCard.setMaximumSize(new Dimension(550, 260));
            emptyCard.setLayout(new BoxLayout(emptyCard, BoxLayout.Y_AXIS));
            emptyCard.setBorder(new EmptyBorder(40, 30, 40, 30));

            JLabel emptyIcon = new JLabel("🧁");
            emptyIcon.setFont(new Font("Segoe UI", Font.PLAIN, 48));
            emptyIcon.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel emptyText = UI.centeredHeading("Your cart is currently empty", 22);
            JLabel emptySub = new JLabel("Explore our freshly baked cakes, brownies, and pastries!");
            emptySub.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            emptySub.setForeground(Colors.MUTED_TEXT);
            emptySub.setAlignmentX(Component.CENTER_ALIGNMENT);

            JButton browse = UI.primaryButton("BROWSE MENU ➔");
            browse.setAlignmentX(Component.CENTER_ALIGNMENT);
            browse.setBorder(new EmptyBorder(10, 24, 10, 24));
            browse.addActionListener(e -> showMenu());

            emptyCard.add(emptyIcon);
            emptyCard.add(Box.createVerticalStrut(10));
            emptyCard.add(emptyText);
            emptyCard.add(Box.createVerticalStrut(6));
            emptyCard.add(emptySub);
            emptyCard.add(Box.createVerticalStrut(20));
            emptyCard.add(browse);

            content.add(Box.createVerticalStrut(50));
            content.add(emptyCard);
        } else {
            content.add(Box.createVerticalStrut(15));
            for (CartItem item : cart.getItems()) {
                content.add(createCartItem(item));
                content.add(Box.createVerticalStrut(12));
            }

            JPanel summaryCard = UI.card();
            summaryCard.setMaximumSize(new Dimension(900, 280));
            summaryCard.setLayout(new BoxLayout(summaryCard, BoxLayout.Y_AXIS));
            summaryCard.setBorder(new EmptyBorder(20, 25, 20, 25));

            JTextArea bakerNote = new JTextArea(3, 40);
            bakerNote.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            bakerNote.setLineWrap(true);
            bakerNote.setWrapStyleWord(true);
            JScrollPane noteScroll = new JScrollPane(bakerNote);
            noteScroll.setBorder(BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(Colors.BORDER, 1, true),
                    "📝 Overall note to baker for this entire order"
            ));

            JPanel totalRow = new JPanel(new BorderLayout());
            totalRow.setOpaque(false);
            JLabel totalLabel = new JLabel("Cart Total (" + cart.getItems().size() + " items):");
            totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
            totalLabel.setForeground(Colors.ESPRESSO);

            JLabel totalVal = new JLabel("₹" + money(cart.getSubtotal()));
            totalVal.setFont(new Font("Segoe UI", Font.BOLD, 24));
            totalVal.setForeground(Colors.CARAMEL);

            totalRow.add(totalLabel, BorderLayout.WEST);
            totalRow.add(totalVal, BorderLayout.EAST);

            JPanel actionRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
            actionRow.setOpaque(false);

            JButton addMore = UI.secondaryButton("+ Add More Items");
            addMore.addActionListener(e -> showMenu());

            JButton checkout = UI.primaryButton("PROCEED TO CHECKOUT ➔");
            checkout.setBorder(new EmptyBorder(12, 28, 12, 28));
            checkout.addActionListener(e -> showCheckout(bakerNote.getText()));

            actionRow.add(addMore);
            actionRow.add(checkout);

            summaryCard.add(noteScroll);
            summaryCard.add(Box.createVerticalStrut(15));
            summaryCard.add(totalRow);
            summaryCard.add(Box.createVerticalStrut(15));
            summaryCard.add(actionRow);

            content.add(Box.createVerticalStrut(10));
            content.add(summaryCard);
        }

        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(18);
        page.add(scroll, BorderLayout.CENTER);
        setPage(page, "CART");
    }

    private JPanel createCartItem(CartItem item) {
        JPanel row = UI.card();
        row.setMaximumSize(new Dimension(900, 110));
        row.setLayout(new BorderLayout(18, 0));
        row.setBorder(new EmptyBorder(10, 15, 10, 15));

        // 1. Food Photo Thumbnail (75x75 rounded)
        JLabel thumb = new JLabel(item.getProduct().getImageIcon(75, 75, 12));

        // 2. Item Info
        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));

        JLabel title = new JLabel(item.getProduct().getName());
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(Colors.ESPRESSO);

        JLabel price = new JLabel("₹" + money(item.getProduct().getPrice()) + " each  •  Subtotal: ₹" + money(item.getSubtotal()));
        price.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        price.setForeground(Colors.CARAMEL);

        StringBuilder notes = new StringBuilder();
        if (!item.getCakeMessage().isEmpty()) {
            notes.append("Message: \"").append(item.getCakeMessage()).append("\"  ");
        }
        if (!item.getSpecialInstructions().isEmpty()) {
            notes.append("• Notes: ").append(item.getSpecialInstructions());
        }

        JLabel noteLabel = new JLabel("<html><font color='#8A6B5C'>" + escape(notes.toString()) + "</font></html>");
        noteLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));

        info.add(title);
        info.add(Box.createVerticalStrut(3));
        info.add(price);
        if (notes.length() > 0) {
            info.add(Box.createVerticalStrut(2));
            info.add(noteLabel);
        }

        // 3. Stepper & Remove controls
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 12));
        controls.setOpaque(false);

        JButton minus = UI.pillButton(" - ", false);
        JButton plus = UI.pillButton(" + ", false);
        JLabel qty = new JLabel(" " + item.getQuantity() + " ");
        qty.setFont(new Font("Segoe UI", Font.BOLD, 15));

        JButton remove = UI.styledButton("Remove", Colors.ROSE_LIGHT, Colors.PINK, Colors.DARK_PINK, 14, 12);

        minus.addActionListener(e -> {
            item.decrease();
            showCart();
        });

        plus.addActionListener(e -> {
            item.increase();
            showCart();
        });

        remove.addActionListener(e -> {
            cart.removeItem(item);
            showCart();
        });

        controls.add(minus);
        controls.add(qty);
        controls.add(plus);
        controls.add(Box.createHorizontalStrut(8));
        controls.add(remove);

        JPanel leftBox = new JPanel(new BorderLayout(14, 0));
        leftBox.setOpaque(false);
        leftBox.add(thumb, BorderLayout.WEST);
        leftBox.add(info, BorderLayout.CENTER);

        row.add(leftBox, BorderLayout.CENTER);
        row.add(controls, BorderLayout.EAST);
        return row;
    }

    private void showCheckout(String bakerNote) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(15, 20, 15, 20));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;

        JTextField name = new JTextField(currentUser == null ? "" : currentUser.getName());
        JTextField phone = new JTextField(currentUser == null ? "" : currentUser.getPhone());
        JTextArea address = new JTextArea(3, 24);
        address.setLineWrap(true);
        address.setWrapStyleWord(true);
        JTextField date = new JTextField("Tomorrow (Standard)");

        int r = 0;
        addField(panel, c, r++, "Full Name *", name);
        addField(panel, c, r++, "Phone Number *", phone);
        addField(panel, c, r++, "Delivery Address *", new JScrollPane(address));
        addField(panel, c, r++, "Preferred Delivery Date/Time *", date);

        JLabel total = new JLabel("Order Total: ₹" + money(cart.getSubtotal()));
        total.setFont(new Font("Segoe UI", Font.BOLD, 18));
        total.setForeground(Colors.CARAMEL);

        c.gridx = 0; c.gridy = r; c.gridwidth = 2;
        panel.add(total, c);

        int result = JOptionPane.showConfirmDialog(
                this, panel, "Complete Your Order • Sweet Crumbs", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (result != JOptionPane.OK_OPTION) return;

        if (name.getText().trim().isEmpty()
                || phone.getText().trim().isEmpty()
                || address.getText().trim().isEmpty()
                || date.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please fill in all required delivery fields.",
                    "Missing Information", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Order order = new Order(
                name.getText().trim(),
                phone.getText().trim(),
                address.getText().trim(),
                date.getText().trim(),
                bakerNote == null ? "" : bakerNote.trim(),
                cart.getItems(),
                cart.getSubtotal()
        );

        data.addOrder(order);
        cart.clear();

        JOptionPane.showMessageDialog(this,
                "<html><h2>Order Placed Successfully! 🎉</h2>"
                        + "Order Number: <b>#" + order.getOrderId() + "</b><br>"
                        + "Total Amount: <b>₹" + money(order.getTotal()) + "</b><br>"
                        + "Delivery To: <b>" + escape(order.getCustomerName()) + "</b><br>"
                        + "Current Status: <b>" + order.getStatus() + "</b><br><br>"
                        + "Thank you for choosing Sweet Crumbs! Fresh goodness coming your way!</html>",
                "Order Confirmed",
                JOptionPane.INFORMATION_MESSAGE);

        showOrders();
    }

    private void addField(JPanel panel, GridBagConstraints c, int row, String label, Component field) {
        c.gridx = 0; c.gridy = row; c.gridwidth = 1;
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(Colors.ESPRESSO);
        panel.add(lbl, c);
        c.gridx = 1; c.gridy = row; c.gridwidth = 1;
        panel.add(field, c);
    }

    // ------------------------------------------------------------------
    // LOGIN & REGISTER
    // ------------------------------------------------------------------

    public void showLogin() {
        JPanel page = new JPanel(new GridBagLayout());
        page.setBackground(Colors.CREAM);

        JPanel box = UI.card();
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setBorder(new EmptyBorder(35, 45, 35, 45));
        box.setPreferredSize(new Dimension(420, 460));

        JLabel title = UI.centeredHeading("Welcome Back 👋", 26);
        JLabel sub = new JLabel("Sign in to track orders or manage your bakery");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sub.setForeground(Colors.MUTED_TEXT);
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField email = new JTextField();
        email.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        email.setMaximumSize(new Dimension(340, 40));
        email.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Colors.BORDER, 1, true), "Email Address"));

        JPasswordField password = new JPasswordField();
        password.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        password.setMaximumSize(new Dimension(340, 40));
        password.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Colors.BORDER, 1, true), "Password"));

        JButton login = UI.primaryButton("SIGN IN ➔");
        login.setAlignmentX(Component.CENTER_ALIGNMENT);
        login.setMaximumSize(new Dimension(340, 42));

        JButton register = UI.secondaryButton("Create an Account");
        register.setAlignmentX(Component.CENTER_ALIGNMENT);
        register.setMaximumSize(new Dimension(340, 40));

        JLabel demo = new JLabel(
                "<html><center><font color='#886F62'>Demo Credentials:<br>"
                        + "Customer: <b>customer@sweetcrumbs.com</b> / 1234<br>"
                        + "Admin: <b>admin@sweetcrumbs.com</b> / admin</font></center></html>");
        demo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        demo.setAlignmentX(Component.CENTER_ALIGNMENT);

        login.addActionListener(e -> {
            User user = data.authenticate(email.getText().trim(), new String(password.getPassword()));
            if (user == null) {
                JOptionPane.showMessageDialog(this,
                        "Invalid email or password. Please try again.",
                        "Authentication Failed", JOptionPane.ERROR_MESSAGE);
            } else {
                currentUser = user;
                if (user.isAdmin()) showAdmin();
                else showHome();
            }
        });

        register.addActionListener(e -> showRegister());

        box.add(title);
        box.add(Box.createVerticalStrut(4));
        box.add(sub);
        box.add(Box.createVerticalStrut(20));
        box.add(email);
        box.add(Box.createVerticalStrut(10));
        box.add(password);
        box.add(Box.createVerticalStrut(20));
        box.add(login);
        box.add(Box.createVerticalStrut(10));
        box.add(register);
        box.add(Box.createVerticalStrut(20));
        box.add(demo);

        page.add(box);
        setPage(page, "LOGIN");
    }

    private void showRegister() {
        JPanel panel = new JPanel(new GridLayout(0, 1, 8, 8));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JTextField name = new JTextField();
        JTextField email = new JTextField();
        JTextField phone = new JTextField();
        JPasswordField password = new JPasswordField();

        panel.add(new JLabel("Full Name:"));
        panel.add(name);
        panel.add(new JLabel("Email Address:"));
        panel.add(email);
        panel.add(new JLabel("Phone Number:"));
        panel.add(phone);
        panel.add(new JLabel("Password:"));
        panel.add(password);

        int result = JOptionPane.showConfirmDialog(
                this, panel, "Create Account • Sweet Crumbs",
                JOptionPane.OK_CANCEL_OPTION);

        if (result != JOptionPane.OK_OPTION) return;

        if (name.getText().trim().isEmpty()
                || email.getText().trim().isEmpty()
                || phone.getText().trim().isEmpty()
                || new String(password.getPassword()).isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.");
            return;
        }

        if (data.emailExists(email.getText().trim())) {
            JOptionPane.showMessageDialog(this, "An account with this email already exists.");
            return;
        }

        User user = new User(
                name.getText().trim(),
                email.getText().trim(),
                phone.getText().trim(),
                new String(password.getPassword()),
                false
        );

        data.addUser(user);
        currentUser = user;

        JOptionPane.showMessageDialog(this, "Account created successfully! Welcome to Sweet Crumbs.");
        showHome();
    }

    // ------------------------------------------------------------------
    // ORDERS SCREEN
    // ------------------------------------------------------------------

    public void showOrders() {
        JPanel page = new JPanel(new BorderLayout());
        page.setBackground(Colors.CREAM);
        page.setBorder(new EmptyBorder(25, 45, 25, 45));

        JLabel title = UI.centeredHeading("My Orders 📦", 30);
        page.add(title, BorderLayout.NORTH);

        JPanel list = new JPanel();
        list.setBackground(Colors.CREAM);
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));

        boolean found = false;
        for (Order order : data.getOrders()) {
            if (currentUser == null || order.getPhone().equals(currentUser.getPhone()) || currentUser.isAdmin()) {
                list.add(createOrderCard(order));
                list.add(Box.createVerticalStrut(14));
                found = true;
            }
        }

        if (!found) {
            list.add(Box.createVerticalStrut(70));
            list.add(UI.centeredHeading("You haven't placed any orders yet.", 22));
            JButton browse = UI.primaryButton("Browse Menu ➔");
            browse.setAlignmentX(Component.CENTER_ALIGNMENT);
            browse.addActionListener(e -> showMenu());
            list.add(Box.createVerticalStrut(15));
            list.add(browse);
        }

        JScrollPane scroll = new JScrollPane(list);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(18);
        page.add(scroll, BorderLayout.CENTER);
        setPage(page, "ORDERS");
    }

    private JPanel createOrderCard(Order order) {
        JPanel card = UI.card();
        card.setMaximumSize(new Dimension(880, 200));
        card.setLayout(new BorderLayout(15, 10));

        // Header: Order ID, Status Badge, Total
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel orderNum = new JLabel("Order #" + order.getOrderId());
        orderNum.setFont(new Font("Segoe UI", Font.BOLD, 18));
        orderNum.setForeground(Colors.ESPRESSO);

        JLabel statusBadge = createStatusBadge(order.getStatus());

        JLabel total = new JLabel("Total: ₹" + money(order.getTotal()));
        total.setFont(new Font("Segoe UI", Font.BOLD, 18));
        total.setForeground(Colors.CARAMEL);

        JPanel rightHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightHeader.setOpaque(false);
        rightHeader.add(statusBadge);
        rightHeader.add(total);

        header.add(orderNum, BorderLayout.WEST);
        header.add(rightHeader, BorderLayout.EAST);

        // Body: Items list with mini photo icons
        JPanel itemsPanel = new JPanel();
        itemsPanel.setOpaque(false);
        itemsPanel.setLayout(new BoxLayout(itemsPanel, BoxLayout.Y_AXIS));

        for (CartItem item : order.getItems()) {
            JPanel itemRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 3));
            itemRow.setOpaque(false);

            JLabel miniThumb = new JLabel(item.getProduct().getImageIcon(36, 36, 8));
            JLabel itemText = new JLabel(item.getProduct().getName() + "  × " + item.getQuantity() + " (₹" + money(item.getSubtotal()) + ")");
            itemText.setFont(new Font("Segoe UI", Font.PLAIN, 14));

            itemRow.add(miniThumb);
            itemRow.add(itemText);
            itemsPanel.add(itemRow);
        }

        // Footer: Delivery info & Baker note
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);

        JLabel deliv = new JLabel("📅 Delivery: " + order.getDeliveryDate() + "  •  Recipient: " + order.getCustomerName());
        deliv.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        deliv.setForeground(Colors.MUTED_TEXT);

        footer.add(deliv, BorderLayout.WEST);

        card.add(header, BorderLayout.NORTH);
        card.add(itemsPanel, BorderLayout.CENTER);
        card.add(footer, BorderLayout.SOUTH);
        return card;
    }

    private JLabel createStatusBadge(String status) {
        Color bg;
        Color fg;
        switch (status.toLowerCase()) {
            case "delivered":
                bg = Colors.SUCCESS_LIGHT;
                fg = Colors.SUCCESS;
                break;
            case "preparing":
            case "confirmed":
                bg = Colors.INFO_LIGHT;
                fg = Colors.INFO;
                break;
            case "ready":
                bg = new Color(243, 229, 245);
                fg = new Color(123, 31, 162);
                break;
            case "cancelled":
                bg = new Color(255, 235, 238);
                fg = new Color(198, 40, 40);
                break;
            default: // Pending
                bg = Colors.WARNING_LIGHT;
                fg = Colors.WARNING;
                break;
        }
        return UI.badge(status.toUpperCase(), bg, fg);
    }

    // ------------------------------------------------------------------
    // ADMIN DASHBOARD
    // ------------------------------------------------------------------

    public void showAdmin() {
        JPanel page = new JPanel(new BorderLayout());
        page.setBackground(Colors.CREAM);
        page.setBorder(new EmptyBorder(25, 45, 25, 45));

        JLabel title = UI.centeredHeading("Bakery Admin Dashboard ⚙", 30);
        page.add(title, BorderLayout.NORTH);

        JPanel tabs = new JPanel(new CardLayout());
        tabs.setBackground(Colors.CREAM);

        JPanel overview = createAdminOverview();
        JPanel orders = createAdminOrders();
        JPanel products = createAdminProducts();

        tabs.add(overview, "OVERVIEW");
        tabs.add(orders, "ORDERS");
        tabs.add(products, "PRODUCTS");

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 15));
        buttons.setOpaque(false);

        JButton overviewBtn = UI.pillButton("📊 Overview", true);
        JButton ordersBtn = UI.pillButton("📦 Orders (" + data.getOrders().size() + ")", false);
        JButton productsBtn = UI.pillButton("🍰 Products (" + data.getProducts().size() + ")", false);

        CardLayout cl = (CardLayout) tabs.getLayout();

        overviewBtn.addActionListener(e -> {
            cl.show(tabs, "OVERVIEW");
            overviewBtn.setForeground(Color.WHITE);
            ordersBtn.setForeground(Colors.BROWN);
            productsBtn.setForeground(Colors.BROWN);
        });

        ordersBtn.addActionListener(e -> {
            cl.show(tabs, "ORDERS");
            overviewBtn.setForeground(Colors.BROWN);
            ordersBtn.setForeground(Color.WHITE);
            productsBtn.setForeground(Colors.BROWN);
        });

        productsBtn.addActionListener(e -> {
            cl.show(tabs, "PRODUCTS");
            overviewBtn.setForeground(Colors.BROWN);
            ordersBtn.setForeground(Colors.BROWN);
            productsBtn.setForeground(Color.WHITE);
        });

        buttons.add(overviewBtn);
        buttons.add(ordersBtn);
        buttons.add(productsBtn);

        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(Colors.CREAM);
        center.add(buttons, BorderLayout.NORTH);
        center.add(tabs, BorderLayout.CENTER);

        page.add(center, BorderLayout.CENTER);
        setPage(page, "ADMIN");
    }

    private JPanel createAdminOverview() {
        JPanel p = new JPanel(new GridLayout(1, 4, 18, 18));
        p.setBackground(Colors.CREAM);
        p.setBorder(new EmptyBorder(25, 10, 25, 10));

        double totalRevenue = data.getOrders().stream().mapToDouble(Order::getTotal).sum();

        p.add(adminStat("TOTAL PRODUCTS", String.valueOf(data.getProducts().size()), "🍰"));
        p.add(adminStat("TOTAL ORDERS", String.valueOf(data.getOrders().size()), "📦"));
        p.add(adminStat("CUSTOMERS", String.valueOf(data.getUsers().size() - 1), "👥"));
        p.add(adminStat("TOTAL REVENUE", "₹" + money(totalRevenue), "💰"));

        return p;
    }

    private JPanel adminStat(String label, String value, String icon) {
        JPanel p = UI.card();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(new EmptyBorder(25, 20, 25, 20));

        JLabel ic = new JLabel(icon);
        ic.setFont(new Font("Segoe UI", Font.PLAIN, 32));
        ic.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel v = UI.centeredHeading(value, 28);
        v.setForeground(Colors.CARAMEL);

        JLabel l = UI.centeredHeading(label, 13);
        l.setForeground(Colors.MUTED_TEXT);

        p.add(ic);
        p.add(Box.createVerticalStrut(8));
        p.add(v);
        p.add(Box.createVerticalStrut(4));
        p.add(l);
        return p;
    }

    private JPanel createAdminOrders() {
        JPanel p = new JPanel();
        p.setBackground(Colors.CREAM);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

        if (data.getOrders().isEmpty()) {
            p.add(Box.createVerticalStrut(40));
            p.add(UI.centeredHeading("No orders received yet.", 20));
            return p;
        }

        for (Order order : data.getOrders()) {
            JPanel card = UI.card();
            card.setMaximumSize(new Dimension(920, 220));
            card.setLayout(new BorderLayout(15, 10));

            JTextArea info = new JTextArea();
            info.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            info.setEditable(false);
            info.setOpaque(false);

            StringBuilder s = new StringBuilder();
            s.append("Order #").append(order.getOrderId()).append("  •  Total: ₹").append(money(order.getTotal())).append("\n");
            s.append("Customer: ").append(order.getCustomerName()).append("  (Phone: ").append(order.getPhone()).append(")\n");
            s.append("Delivery Address: ").append(order.getAddress()).append("\n");
            s.append("Date: ").append(order.getDeliveryDate()).append("\n");
            s.append("Items: ");
            for (CartItem item : order.getItems()) {
                s.append(item.getProduct().getName()).append(" ×").append(item.getQuantity()).append(", ");
            }
            if (!order.getBakerNote().isEmpty()) {
                s.append("\nSpecial Baker Note: ").append(order.getBakerNote());
            }
            info.setText(s.toString());

            JComboBox<String> status = new JComboBox<>(
                    new String[]{"Pending", "Confirmed", "Preparing", "Ready", "Delivered", "Cancelled"});
            status.setSelectedItem(order.getStatus());
            status.setFont(new Font("Segoe UI", Font.BOLD, 13));

            JButton update = UI.primaryButton("UPDATE STATUS");
            update.addActionListener(e -> {
                String newStatus = (String) status.getSelectedItem();
                data.updateOrderStatus(order.getOrderId(), newStatus);
                order.setStatus(newStatus);
                JOptionPane.showMessageDialog(this, "Order #" + order.getOrderId() + " updated to " + newStatus);
                showAdmin();
            });

            JPanel action = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
            action.setOpaque(false);
            action.add(new JLabel("Change Status:"));
            action.add(status);
            action.add(update);

            card.add(new JScrollPane(info), BorderLayout.CENTER);
            card.add(action, BorderLayout.SOUTH);

            p.add(card);
            p.add(Box.createVerticalStrut(12));
        }

        JScrollPane scroll = new JScrollPane(p);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(18);
        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setBackground(Colors.CREAM);
        wrap.add(scroll, BorderLayout.CENTER);
        return wrap;
    }

    private JPanel createAdminProducts() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Colors.CREAM);

        JPanel list = new JPanel();
        list.setBackground(Colors.CREAM);
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));

        for (Product product : data.getProducts()) {
            JPanel row = UI.card();
            row.setMaximumSize(new Dimension(920, 85));
            row.setLayout(new BorderLayout(15, 0));
            row.setBorder(new EmptyBorder(8, 15, 8, 15));

            JLabel thumb = new JLabel(product.getImageIcon(60, 60, 10));

            JPanel mid = new JPanel(new GridLayout(2, 1, 0, 2));
            mid.setOpaque(false);

            JLabel name = new JLabel(product.getName());
            name.setFont(new Font("Segoe UI", Font.BOLD, 15));
            name.setForeground(Colors.ESPRESSO);

            JLabel meta = new JLabel(product.getCategory() + "  •  " + product.getDescription());
            meta.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            meta.setForeground(Colors.MUTED_TEXT);

            mid.add(name);
            mid.add(meta);

            JLabel price = new JLabel("₹" + money(product.getPrice()));
            price.setFont(new Font("Segoe UI", Font.BOLD, 18));
            price.setForeground(Colors.CARAMEL);

            row.add(thumb, BorderLayout.WEST);
            row.add(mid, BorderLayout.CENTER);
            row.add(price, BorderLayout.EAST);

            list.add(row);
            list.add(Box.createVerticalStrut(8));
        }

        JScrollPane scroll = new JScrollPane(list);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(18);
        p.add(scroll, BorderLayout.CENTER);
        return p;
    }

    // ------------------------------------------------------------------
    // HELPERS
    // ------------------------------------------------------------------

    private String money(double value) {
        if (value == (long) value) return String.valueOf((long) value);
        return String.format("%.2f", value);
    }

    private String escape(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }
}

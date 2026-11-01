package bakery;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public final class UI {
    private UI() {}

    public static JButton button(String text) {
        return styledButton(text, Colors.PINK, Colors.DARK_PINK, Colors.BROWN, 16, 13);
    }

    public static JButton primaryButton(String text) {
        return styledButton(text, Colors.ROSE_ACCENT, Colors.ROSE_HOVER, Color.WHITE, 18, 14);
    }

    public static JButton secondaryButton(String text) {
        return styledButton(text, Color.WHITE, Colors.CARAMEL_LIGHT, Colors.BROWN, 16, 13);
    }

    public static JButton pillButton(String text, boolean active) {
        Color bg = active ? Colors.ROSE_ACCENT : Color.WHITE;
        Color hover = active ? Colors.ROSE_HOVER : Colors.ROSE_LIGHT;
        Color fg = active ? Color.WHITE : Colors.BROWN;
        return styledButton(text, bg, hover, fg, 20, 13);
    }

    public static JButton styledButton(String text, Color normalBg, Color hoverBg, Color textFg, int radius, int fontSize) {
        JButton b = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth();
                int h = getHeight();

                ButtonModel model = getModel();
                Color currentBg = normalBg;
                if (model.isPressed()) {
                    currentBg = hoverBg.darker();
                } else if (model.isRollover()) {
                    currentBg = hoverBg;
                }

                // Fill background
                g2.setColor(currentBg);
                g2.fillRoundRect(1, 1, w - 2, h - 2, radius, radius);

                // Border
                if (normalBg.equals(Color.WHITE)) {
                    g2.setColor(Colors.BORDER);
                    g2.setStroke(new BasicStroke(1.2f));
                    g2.drawRoundRect(1, 1, w - 3, h - 3, radius, radius);
                }

                g2.dispose();
                super.paintComponent(g);
            }
        };

        b.setFont(new Font("Segoe UI", Font.BOLD, fontSize));
        b.setForeground(textFg);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setContentAreaFilled(false);
        b.setOpaque(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setBorder(new EmptyBorder(8, 16, 8, 16));
        return b;
    }

    public static JLabel heading(String text, int size) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Serif", Font.BOLD, size));
        label.setForeground(Colors.ESPRESSO);
        return label;
    }

    public static JLabel centeredHeading(String text, int size) {
        JLabel label = heading(text, size);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        return label;
    }

    public static JPanel card() {
        return roundedCard(18);
    }

    public static JPanel roundedCard(int radius) {
        JPanel p = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth();
                int h = getHeight();

                // Subtle shadow
                g2.setColor(Colors.SHADOW);
                g2.fillRoundRect(2, 2, w - 4, h - 3, radius, radius);

                // Card background
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, w - 3, h - 3, radius, radius);

                // Border
                g2.setColor(Colors.BORDER);
                g2.setStroke(new BasicStroke(1.1f));
                g2.drawRoundRect(0, 0, w - 4, h - 4, radius, radius);

                g2.dispose();
                super.paintComponent(g);
            }
        };
        p.setOpaque(false);
        p.setBackground(Color.WHITE);
        p.setBorder(new EmptyBorder(12, 12, 12, 12));
        return p;
    }

    public static JLabel badge(String text, Color bg, Color fg) {
        JLabel badge = new JLabel(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        badge.setOpaque(false);
        badge.setForeground(fg);
        badge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        badge.setBorder(new EmptyBorder(3, 10, 3, 10));
        return badge;
    }

    public static JLabel categoryBadge(String category) {
        Color bg;
        Color fg;
        switch (category.toLowerCase()) {
            case "cake":
                bg = Colors.ROSE_LIGHT;
                fg = Colors.DARK_PINK;
                break;
            case "cupcake":
                bg = new Color(255, 240, 235);
                fg = new Color(190, 75, 55);
                break;
            case "brownie":
                bg = new Color(245, 235, 225);
                fg = Colors.BROWN;
                break;
            case "cookie":
                bg = Colors.CARAMEL_LIGHT;
                fg = Colors.CARAMEL;
                break;
            case "pastry":
                bg = new Color(240, 245, 235);
                fg = new Color(75, 120, 55);
                break;
            case "gift box":
                bg = new Color(250, 235, 250);
                fg = new Color(130, 45, 130);
                break;
            default:
                bg = Colors.CARAMEL_LIGHT;
                fg = Colors.BROWN;
                break;
        }
        return badge(category.toUpperCase(), bg, fg);
    }
}

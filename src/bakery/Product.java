package bakery;

import javax.swing.ImageIcon;

public class Product {
    private final int id;
    private final String name;
    private final String category;
    private final double price;
    private final String description;
    private final String emoji;

    public Product(int id, String name, String category, double price,
                   String description, String emoji) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.description = description;
        this.emoji = emoji;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public double getPrice() { return price; }
    public String getDescription() { return description; }
    public String getEmoji() { return emoji; }

    public String getImageFileName() {
        return ImageLoader.getImageFileName(id);
    }

    public ImageIcon getImageIcon(int width, int height) {
        return ImageLoader.getProductIcon(id, width, height);
    }

    public ImageIcon getImageIcon(int width, int height, int cornerRadius) {
        return ImageLoader.getProductIcon(id, width, height, cornerRadius);
    }

    public ImageIcon getTopRoundedImageIcon(int width, int height, int cornerRadius) {
        return ImageLoader.getProductIcon(id, width, height, cornerRadius, true);
    }
}

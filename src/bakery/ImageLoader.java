package bakery;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ImageLoader handles loading, caching, center-cropping, and smooth high-quality
 * anti-aliased scaling for bakery product photos and UI icons.
 */
public final class ImageLoader {

    private static final Map<Integer, String> PRODUCT_FILES = new HashMap<>();
    private static final Map<String, Image> RAW_IMAGE_CACHE = new ConcurrentHashMap<>();
    private static final Map<String, ImageIcon> SCALED_CACHE = new ConcurrentHashMap<>();

    static {
        PRODUCT_FILES.put(1, "chocolate_truffle_cake.jpg");
        PRODUCT_FILES.put(2, "red_velvet_cake.jpg");
        PRODUCT_FILES.put(3, "black_forest_cake.jpg");
        PRODUCT_FILES.put(4, "chocolate_cupcake.jpg");
        PRODUCT_FILES.put(5, "vanilla_cupcake.jpg");
        PRODUCT_FILES.put(6, "fudge_brownie.jpg");
        PRODUCT_FILES.put(7, "walnut_brownie.jpg");
        PRODUCT_FILES.put(8, "chocolate_chip_cookies.jpg");
        PRODUCT_FILES.put(9, "butter_cookies.jpg");
        PRODUCT_FILES.put(10, "cinnamon_roll.jpg");
        PRODUCT_FILES.put(11, "fruit_tart.jpg");
        PRODUCT_FILES.put(12, "brownie_box.jpg");
    }

    private ImageLoader() {}

    public static String getImageFileName(int productId) {
        return PRODUCT_FILES.getOrDefault(productId, "product_" + productId + ".jpg");
    }

    /**
     * Loads the raw product image with fallback across multiple search locations.
     */
    public static Image getProductImage(int productId) {
        String filename = getImageFileName(productId);
        if (RAW_IMAGE_CACHE.containsKey(filename)) {
            return RAW_IMAGE_CACHE.get(filename);
        }

        Image img = loadRawImage(filename, productId);
        if (img != null) {
            RAW_IMAGE_CACHE.put(filename, img);
        }
        return img;
    }

    private static Image loadRawImage(String filename, int productId) {
        // 1. Try classpath resource
        try {
            InputStream is = ImageLoader.class.getResourceAsStream("/bakery/images/" + filename);
            if (is == null) {
                is = ImageLoader.class.getResourceAsStream("images/" + filename);
            }
            if (is == null) {
                is = ImageLoader.class.getResourceAsStream("/images/" + filename);
            }
            if (is != null) {
                BufferedImage bi = ImageIO.read(is);
                if (bi != null) return bi;
            }
        } catch (Exception ignored) {}

        // 2. Try relative file paths
        String[] candidatePaths = {
            "images/" + filename,
            "images/product_" + productId + ".jpg",
            "src/bakery/images/" + filename,
            "src/bakery/images/product_" + productId + ".jpg",
            "bin/bakery/images/" + filename,
            "../images/" + filename
        };

        for (String p : candidatePaths) {
            File f = new File(p);
            if (f.exists() && f.isFile()) {
                try {
                    BufferedImage bi = ImageIO.read(f);
                    if (bi != null) return bi;
                } catch (Exception ignored) {}
            }
        }

        // 3. Fallback to Camera Roll if relative path was not found
        try {
            String userHome = System.getProperty("user.home");
            File crDir = new File(userHome, "OneDrive/Pictures/Camera Roll");
            if (crDir.exists() && crDir.isDirectory()) {
                File[] matches = crDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".jpeg") || name.toLowerCase().endsWith(".jpg"));
                if (matches != null && productId >= 1 && productId <= matches.length) {
                    // Match by index or filename
                    for (File mf : matches) {
                        try {
                            BufferedImage bi = ImageIO.read(mf);
                            if (bi != null) return bi;
                        } catch (Exception ignored) {}
                    }
                }
            }
        } catch (Exception ignored) {}

        return null;
    }

    /**
     * Gets a smoothly scaled ImageIcon center-cropped to target width and height.
     */
    public static ImageIcon getProductIcon(int productId, int width, int height) {
        return getProductIcon(productId, width, height, 0, false);
    }

    /**
     * Gets a smoothly scaled ImageIcon with rounded corners.
     */
    public static ImageIcon getProductIcon(int productId, int width, int height, int cornerRadius) {
        return getProductIcon(productId, width, height, cornerRadius, false);
    }

    /**
     * Gets a smoothly scaled ImageIcon with rounded corners (optionally top-only for card headers).
     */
    public static ImageIcon getProductIcon(int productId, int width, int height, int cornerRadius, boolean topCornersOnly) {
        String cacheKey = productId + "_" + width + "x" + height + "_r" + cornerRadius + (topCornersOnly ? "_top" : "");
        ImageIcon cached = SCALED_CACHE.get(cacheKey);
        if (cached != null) return cached;

        Image raw = getProductImage(productId);
        if (raw == null) {
            // Return placeholder icon
            ImageIcon placeholder = createPlaceholderIcon(productId, width, height, cornerRadius);
            SCALED_CACHE.put(cacheKey, placeholder);
            return placeholder;
        }

        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = result.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        // Clip to rounded rect
        if (cornerRadius > 0) {
            if (topCornersOnly) {
                Area area = new Area(new RoundRectangle2D.Float(0, 0, width, height + cornerRadius, cornerRadius, cornerRadius));
                area.intersect(new Area(new Rectangle2D.Float(0, 0, width, height)));
                g2.setClip(area);
            } else {
                g2.setClip(new RoundRectangle2D.Float(0, 0, width, height, cornerRadius, cornerRadius));
            }
        }

        // Center-crop scaling logic
        int rawW = raw.getWidth(null);
        int rawH = raw.getHeight(null);
        if (rawW > 0 && rawH > 0) {
            double rawAspect = (double) rawW / rawH;
            double targetAspect = (double) width / height;
            int drawW, drawH, offsetX = 0, offsetY = 0;

            if (rawAspect > targetAspect) {
                // Image is wider than target: fit height, crop sides
                drawH = height;
                drawW = (int) Math.round(height * rawAspect);
                offsetX = (drawW - width) / 2;
            } else {
                // Image is taller than target: fit width, crop top/bottom
                drawW = width;
                drawH = (int) Math.round(width / rawAspect);
                offsetY = (drawH - height) / 2;
            }
            g2.drawImage(raw, -offsetX, -offsetY, drawW, drawH, null);
        } else {
            g2.drawImage(raw, 0, 0, width, height, null);
        }

        g2.dispose();

        ImageIcon icon = new ImageIcon(result);
        SCALED_CACHE.put(cacheKey, icon);
        return icon;
    }

    private static ImageIcon createPlaceholderIcon(int productId, int width, int height, int cornerRadius) {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(new Color(248, 238, 230));
        if (cornerRadius > 0) {
            g2.fill(new RoundRectangle2D.Float(0, 0, width, height, cornerRadius, cornerRadius));
        } else {
            g2.fillRect(0, 0, width, height);
        }

        g2.setColor(new Color(177, 82, 101));
        g2.setFont(new Font("SansSerif", Font.BOLD, Math.max(14, height / 4)));
        String text = "Sweet Crumbs #" + productId;
        FontMetrics fm = g2.getFontMetrics();
        int x = (width - fm.stringWidth(text)) / 2;
        int y = (height - fm.getHeight()) / 2 + fm.getAscent();
        g2.drawString(text, x, y);
        g2.dispose();

        return new ImageIcon(img);
    }
}

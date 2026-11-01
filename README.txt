SWEET CRUMBS - HOME BAKERY  (Front-end + MySQL/JDBC back-end)
KTU S3 CSE - PBCST304 OOP IN JAVA

Technology: Java Swing, JDBC, MySQL 8, Java2D

SETUP
1. Install MySQL 8 and make sure it is running.
2. Create the database and data:
       mysql --default-character-set=utf8mb4 -u root -p < sql/schema.sql
   (or open sql/schema.sql in MySQL Workbench and run it)
3. MySQL Connector/J .jar is included in lib/mysql-connector-j-26.10.0.jar
4. Open src/bakery/DBConnection.java and verify USER and PASSWORD match your MySQL login.
5. Run bakery.Main

Demo login:
  Customer: customer@sweetcrumbs.com / 1234
  Admin   : admin@sweetcrumbs.com / admin

ENHANCEMENTS & IMAGE INTEGRATION
  NEW      images/              High-resolution food photographs for all 12 products:
                                • Chocolate Truffle Cake     • Red Velvet Cake
                                • Black Forest Cake          • Chocolate Cupcake
                                • Vanilla Cupcake            • Fudge Brownie
                                • Walnut Brownie             • Chocolate Chip Cookies
                                • Butter Cookies             • Cinnamon Roll
                                • Fruit Tart                 • Assorted Brownie Box
  NEW      ImageLoader.java     Multi-tier image loader with aspect-ratio preserving center-crop,
                                rounded corner clipping, and memory caching for high performance.
  ENHANCED Product.java         Added methods to retrieve image files and scaled/rounded icons.
  ENHANCED Colors.java          Artisan bakery palette: Warm Cream, Espresso Chocolate, Golden Caramel,
                                Dusty Rose, and status badge colorways.
  ENHANCED UI.java              Custom rounded cards, interactive pill buttons with hover states,
                                category tags, and anti-aliased Java2D drawing.
  ENHANCED BakeryApp.java       • Responsive dynamic-column grid that adapts smoothly on window resize
                                • Hero showcase with featured product photos and bakery feature pills
                                • Customer testimonials and boutique footer
                                • Product details modal with high-res hero image and live price calculator
                                • Cart and My Orders itemized with product thumbnails
                                • Admin dashboard with metric cards and product photo catalog
  ENHANCED Main.java            Enabled system-wide font anti-aliasing and native Look & Feel.

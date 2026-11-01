SWEET CRUMBS - HOME BAKERY  (Front-end + MySQL/JDBC back-end)
KTU S3 CSE - PBCST304 OOP IN JAVA

Technology: Java Swing, JDBC, MySQL

SETUP
1. Install MySQL 8 and make sure it is running.
2. Create the database and data:
       mysql --default-character-set=utf8mb4 -u root -p < sql/schema.sql
   (or open sql/schema.sql in MySQL Workbench and run it)
3. Download MySQL Connector/J (the .jar) from https://dev.mysql.com/downloads/connector/j/
   and put it in the lib/ folder.
   - IntelliJ: File > Project Structure > Libraries > + > Java > choose the jar
   - Eclipse : Right-click project > Build Path > Add External JARs
   - NetBeans: Libraries > Add JAR/Folder
4. Open src/bakery/DBConnection.java and set USER and PASSWORD to your MySQL login.
5. Run bakery.Main

Demo login (same as before)
  Customer: customer@sweetcrumbs.com / 1234
  Admin   : admin@sweetcrumbs.com / admin

FILES CHANGED FROM THE FRONT-END-ONLY VERSION
  NEW      sql/schema.sql       tables + starting data
  NEW      DBConnection.java    JDBC connection
  REWRITTEN AppData.java        now uses SQL instead of in-memory lists
  CHANGED  Order.java           ID comes from the database
  CHANGED  User.java            added getPassword() so it can be hashed when saved
  CHANGED  Main.java            checks the database connection at start-up
  CHANGED  BakeryApp.java       fixed a compile error in showMenu(); order status
                                updates are now saved to the database
  Unchanged: Cart, CartItem, Product, Colors, UI

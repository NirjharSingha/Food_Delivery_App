**Introduction:**

The food delivery app is a command line app using MySQL Database and Java. 

**Local Development:**

To run the app locally, java and mysql server installed in your device. If you have these dependencies installed then you can clone the git repository running the following command in terminal:
```bash
git clone https://github.com/NirjharSingha/Food_Delivery_App.git
cd Food_Delivery_App
```

To connect the app with the databases, the databases should be run on localhost:3306 port.<br>
To connect the app with the databases, you need to create a new user with the username "School_DB_User" and password "password". <br>
To create the user run the following command in terminal to log in MySQL server:
```bash
mysql -u root -p
```

After entering password, you'll log into MySQL server. Now run the following sql scripts to create the user:
```sql
CREATE USER 'Food_Delivery_App_User'@'localhost' IDENTIFIED BY 'password';
GRANT ALL PRIVILEGES ON *.* TO 'Food_Delivery_App_User'@'localhost';
FLUSH PRIVILEGES;
```

Finally, to connect java app with mysql database, we need to connect jdbc( java-mysql-connector ) to the project in library inside project structure.<br>
In project base directory, there is a jar file called mysql-connector-j-8.0.32.jar<br>
You need to add the jar file in the project library to run the project.

If you have done all this, then your app is ready to launch.

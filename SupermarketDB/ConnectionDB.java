/*__________________________________________________________________________________________________________
  - Name:                Frederick Abi Chahine
  - ID:                  201802470
  - Course:              Database Management Systems
  - Date Last Modified:  13/04/2022
  - Program Description: This program implements a supermarket database where a user (worker / employee) of
  						 the supermarket would be able to visualize all products in a supermarket and tamper
  						 with the data in it. First, the program authenticates whether a user is an actual
  						 worker in the supermarket by asking them to either log in or sign up. Then, the user
  						 (if authenticated) would be able to display the statistics of the store, list
  						 all the products, list the most or least expensive products, add or remove a product,
  						 edit product values (such as name, quantity, price), and change the currency of the
  						 products in the supermarket from USD to LBP or vice-versa.
  - Associated files:    1) Supermarket.java
  __________________________________________________________________________________________________________*/

import java.sql.*;

public class ConnectionDB {

	private Connection connect = null;

	public Connection registerDriver() {

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			connect = DriverManager.getConnection("jdbc:mysql://localhost/supermarketdb", "root", "");
		
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}

		return connect;
	}

}

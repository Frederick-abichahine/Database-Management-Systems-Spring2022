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
  - Associated files:    1) ConnectionDB.java
  __________________________________________________________________________________________________________*/

import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Supermarket {
	
	private Connection connection = null;
	private Statement statement = null;
	private ConnectionDB connect = new ConnectionDB();
	private String currency = null; //in order to be able to display the currency
	static private ResultSet result_set = null;

	public void createStatement() {
		
		try {
			connection = connect.registerDriver();
			statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void displayStatistics() {
		
		//This method will display the total number of products in the database, the most and least expensive products,
		//and all the products with quantity = 0
		
		String select1 = "SELECT COUNT(id) FROM products;";
		String select2 = "SELECT name, price FROM products WHERE price = (SELECT MAX(price) FROM products);";
		String select3 = "SELECT name, price FROM products WHERE price = (SELECT MIN(price) FROM products);";
		String select4 = "SELECT name FROM products WHERE quantity = 0;";
		
		try {
			
			//1
			
			result_set = statement.executeQuery(select1);
			if (result_set.next()) {
				System.out.println("________________________________________________");
				System.out.println("- The total number of products is: " + result_set.getInt(1));
			}
			
			//2
			
			result_set = statement.executeQuery(select2);
			ArrayList<String> namelist1 = new ArrayList<String>(); //We require a list here to store all the names of the most expensive products
			ArrayList<Double> pricelist1 = new ArrayList<Double>(); //We require a list here to store all the prices of the most expensive products
			
			while (result_set.next()) { //Will keep looping as long as there are equally expensive products and each time adds that products name and price to both lists
			    namelist1.add(result_set.getString(1));
			    pricelist1.add(result_set.getDouble(2));
			}
			System.out.println("\n- The most expensive product(s) is/are:");
			for (int i = 0; i<namelist1.size(); i++) { //will loop through all of the most expensive products (names and price both have equal size lists)
				int num = i+1; //just for display purposes
				System.out.println("  "+ num +") " + namelist1.get(i) + " (price = " + pricelist1.get(i) + " " + currency + ")");
			}
			
			//3
			
			result_set = statement.executeQuery(select3);
			ArrayList<String> namelist2 = new ArrayList<String>(); //We require a list here to store all the names of the least expensive products
			ArrayList<Double> pricelist2 = new ArrayList<Double>(); //We require a list here to store all the prices of the least expensive products
			
			while (result_set.next()) { //Will keep looping as long as there are equally cheap products and each time adds that products name and price to both lists
			    namelist2.add(result_set.getString(1));
			    pricelist2.add(result_set.getDouble(2));
			}
			System.out.println("\n- The least expensive product(s) is/are:");
			for (int i = 0; i<namelist2.size(); i++) { //will loop through all of the most expensive products (names and price both have equal size lists)
				int num = i+1; //just for display purposes
				System.out.println("  "+ num +") " + namelist2.get(i) + " (price = " + pricelist2.get(i) + " " + currency +")");
			}
			
			//4
			
			result_set = statement.executeQuery(select4);
			ArrayList<String> quantitylist = new ArrayList<String>(); //We require a list here in order to store more than one element when checking for the products with 0 quantity
			
			while (result_set.next()) { //Will keep looping as long as there are products with quantity = 0 and each time adds that product to the list
				quantitylist.add(result_set.getString(1));
			}  
			System.out.println("\n- The product(s) with 0 quantity is/are:");
			if (quantitylist.size() == 0) { //in-case there are no products that have 0 quantity
				System.out.println("  None");
			}
			else { //if the list contains at least one product
				for (int i = 0; i<quantitylist.size(); i++) { //will loop through all of the products with 0 quantity and display them
					int num = i+1; //just for display purposes
					System.out.println("  "+ num +") " + quantitylist.get(i));
				}
			}
			System.out.println("  --> Total (quantity = 0) is: " + quantitylist.size());
			System.out.println("________________________________________________");
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	public void listProducts() {
		
		//This method will either list all available products, list the top 5 most expensive products, or
		//the Top 5 least expensive products in the database depending on the user's choice.
		
		System.out.println("_____{Select Your Option}_____\r\n"
				 + "1) List All Products.\r\n"
				 + "2) List Top 5 Most Expensive Products.\r\n"
				 + "3) List Top 5 Least Expensive Products.\r\n"
				 + "______________________________");

		@SuppressWarnings("resource")
		Scanner scan = new Scanner(System.in);
		int choice = scan.nextInt();
		
		while (choice != 1 && choice != 2 && choice != 3) {
			System.out.println("\n---> Invalid Input. Try again.");
			System.out.println("\n_____{Select Your Option}_____\r\n"
					 + "1) List All Products.\r\n"
					 + "2) List Top 5 Most Expensive Products.\r\n"
					 + "3) List Top 5 Least Expensive Products.\r\n"
					 + "______________________________");
			choice = scan.nextInt();
		}
		
		String select = "";
		
		if (choice == 1) { //adjusts the select query to select all products
			select = "SELECT * FROM products;";
			System.out.println("\n---> Listing All Products...");
		}
		else if (choice == 2) { //adjusts the select query to select only the top 5 most expensive products
			select = "SELECT * FROM products ORDER BY price DESC LIMIT 5;";
			System.out.println("\n---> Listing Top 5 Most Expensive Products...");
		}
		else if (choice == 3) { //adjusts the select query to select only the top 5 least expensive products
			select = "SELECT * FROM products ORDER BY price ASC LIMIT 5;";
			System.out.println("\n---> Listing Top 5 Least Expensive Products...");
		}
		
		try {
			result_set = statement.executeQuery(select);
			//we need 4 arrays for the 4 fields in the products table
			ArrayList<Integer> productID = new ArrayList<Integer>();
			ArrayList<String> productName = new ArrayList<String>();
			ArrayList<Double> productQuantity = new ArrayList<Double>();
			ArrayList<Double> productPrice = new ArrayList<Double>();
			
			while (result_set.next()) { //Will keep looping as long as there are products
				productID.add(result_set.getInt(1));
				productName.add(result_set.getString(2));
				productQuantity.add(result_set.getDouble(3));
				productPrice.add(result_set.getDouble(4));
			}
			System.out.println("______________________________________");
			for (int i = 0; i<productID.size(); i++) { //will loop through all of the products
				System.out.println("[Product ID: " + productID.get(i) + "]:\n" 
													    + "     > Name = " + productName.get(i) + "\n"
													    + "     > Quantity = " + productQuantity.get(i) + "\n"
													    + "     > Price = " + productPrice.get(i)+ " " + currency);
				System.out.println();
			}
			System.out.println("______________________________________");
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void addProduct() {
		
		/*This method simply prompts the user for product information in order to add this product to the database 
		 * Assuming a products name is unique => When a user adds a new product with the same name of an existing
		 * product then the program will instead ask to increment the quantity of the existing product rather than
		 * adding an entirely new product with the same name. If the product does not already exist then a new 
		 * product with this new name will be added to the database.*/
		
		@SuppressWarnings("resource")
		Scanner scan_line = new Scanner(System.in);
		@SuppressWarnings("resource")
		Scanner scan_double = new Scanner(System.in);
		
		System.out.println("\n---> Please enter the name of the product:");
		String name = scan_line.nextLine();
		
		String select = "SELECT quantity FROM products WHERE name = '" + name + "';"; // Check if product with same name exists already
		
		try {
			result_set = statement.executeQuery(select);
			
			if (result_set.next()) { 
				//if the name exists, update quantity of existing product
				System.out.println("\n---> This product already exists.\n" 
								  +"     By how much would you like to increment the quantity?");
				double quantity = scan_double.nextDouble();
				
				while (quantity < 0.0) { //to ensure the user enters a correct number for the quantity since quantity can't be negative
					System.out.println("\n**Invalid Quantity. Quantity must be zero or higher. Try again.**");
					System.out.println("\nBy how much would you like to increment the quantity?");
					quantity = scan_double.nextDouble();
				}
				
				double new_quantity = quantity + result_set.getDouble(1);
				
				String update = "UPDATE products SET quantity = '" + new_quantity + "' WHERE name = '" + name + "';";
				statement.executeUpdate(update); //updates the quantity of this existing product
			}
			
			else { 
				//else the name does not exist and we proceed to add a new product with the given information...
				System.out.println("\n---> Please enter the quantity of the product:");
				double quantity = scan_double.nextDouble();
				while (quantity < 0.0) { //to ensure the user enters a correct number for the quantity since quantity can't be negative
					System.out.println("\n**Invalid Quantity. Quantity must be zero or higher. Try again.**");
					System.out.println("\n---> Please enter the quantity of the product:");
					quantity = scan_double.nextDouble();
				}
				
				System.out.println("\n---> Please enter the price of the product:");
				double price = scan_double.nextDouble();
				while (price < 0.0) { //to ensure correct input of price, can not be negative
					System.out.println("\n**Invalid Price. Price must be zero or higher. Try again.**");
					System.out.println("\n---> Please enter the price of the product:");
					price = scan_double.nextDouble();
				}
				
				String insert = "INSERT INTO products (name, quantity, price) VALUES (" + "'" + name + "','" + quantity + "','" + price +"');";
				
				try {
					statement.executeUpdate(insert); //Update database with this product
					
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void removeProduct() {
		
		/*This method simply prompts the user for the product's ID and removes it from the database.
		 * If the product does not exist then a message is displayed saying so.*/
		
		@SuppressWarnings("resource")
		Scanner scan_int = new Scanner(System.in);
		System.out.println("\n---> Please enter the product's ID:");
		int id = scan_int.nextInt();
		
		String select = "SELECT * FROM products WHERE id = '" + id +"';"; //we need to check if this product exists.
		
		try {
			result_set = statement.executeQuery(select);
			
			if (result_set.next()) { 
				//This will be executed if the ID has been found
				String delete = "DELETE FROM products WHERE id = '" + id + "';"; // the delete query to delete a single record with this product ID
				
				System.out.println("\n---> You have successfully deleted the following product:");
				System.out.println("\n[Product ID: " + result_set.getInt("id") + "]:\n" 
					    + "     > Name = " + result_set.getString("name") + "\n"
					    + "     > Quantity = " + result_set.getDouble("quantity") + "\n"
					    + "     > Price = " + result_set.getDouble("price") + " " + currency);
				System.out.println();
				statement.executeUpdate(delete); //deletes the product
			}
			
			else {
				//If the ID has not been found then this will be executed...
				System.out.println("\n**Product ID Not Found**");
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void editProduct() {
		
		/*This method prompts the user for the ID of a product and searches the databases if that product exists or not.
		 * If that product exists then the user is allowed to edit that product's name, quantity, and price.
		 * If that product does not exist then a message is displayed.*/
		
		@SuppressWarnings("resource")
		Scanner scan_int = new Scanner(System.in);
		@SuppressWarnings("resource")
		Scanner scan_double = new Scanner(System.in);
		@SuppressWarnings("resource")
		Scanner scan_line = new Scanner(System.in);
		
		System.out.println("\n---> Please enter the product's ID:");
		int id = scan_int.nextInt();
		
		String select = "SELECT * FROM products WHERE id = '" + id +"';"; //we need to check if this product exists.
		
		try {
			result_set = statement.executeQuery(select);
			
			if (result_set.next()) { 
				//if the product exists, update whatever the user chooses to update
				
				boolean flag = true; //used to terminate the loop when needed
				
				while (flag) { //will continue looping until the user decides to exit
					
					System.out.println("\n_____{Choose What To Edit}_____\r\n"
							 + "1) Update Product Name.\r\n"
							 + "2) Update Product Quantity.\r\n"
							 + "3) Update Product Price.\r\n"
							 + "4) Exit Edit Mode.\r\n"
							 + "_______________________________");
					
					
					int choice = scan_int.nextInt();
					
					if (choice == 1) {
						//edit name of product
						
						System.out.println("\n---> Please enter new name:");
						String new_name = scan_line.nextLine();
						
						String update = "UPDATE products SET name = '" + new_name + "' WHERE id = '" + id + "';";
						String select2 = "SELECT * FROM products WHERE name = '" + new_name + "';"; // Check if product with same name exists already
						
						try {
							result_set = statement.executeQuery(select2);
							
							if (result_set.next()) { 
								//if the name exists, display the message
								System.out.println("\n**The name already exists. Choose another name.**");
							}
							
							else { 
								//if name does not already exist then change name to the new name
								statement.executeUpdate(update); //updates the name of this existing product
								System.out.println("\n---> Name Updated! Anything else?");
							}
							
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
					
					else if (choice == 2) {
						//edit quantity of product
						
						System.out.println("\n---> Please enter new quantity:");
						double new_quantity = scan_double.nextDouble();
						while (new_quantity < 0.0) { //to ensure right input
							System.out.println("\n**Invalid Quantity. Quantity must be zero or higher. Try again.**");
							System.out.println("\n---> Please enter new quantity:");
							new_quantity = scan_double.nextDouble();
						}
						
						String update = "UPDATE products SET quantity = '" + new_quantity + "' WHERE id = '" + id + "';";
						statement.executeUpdate(update); //updates the quantity of this existing product
						System.out.println("\n---> Quantity Updated! Anything else?");
					}
					
					else if (choice == 3) {
						//edit price of product
						
						System.out.println("\n---> Please enter new price:");
						double new_price = scan_double.nextDouble();
						while (new_price < 0.0) { //to ensure right input
							System.out.println("\n**Invalid Price. Price must be zero or higher. Try again.**");
							System.out.println("\n---> Please enter new price:");
							new_price = scan_double.nextDouble();
						}
						
						String update = "UPDATE products SET price = '" + new_price + "' WHERE id = '" + id + "';";
						statement.executeUpdate(update); //updates the quantity of this existing product
						System.out.println("\n---> Price Updated! Anything else?");
					}
					
					else if (choice == 4) {
						flag = false; //to terminate the loop and exit
						System.out.println("\n---> You are done editing [ Product ID: " + id + " ]");
					}
					
					else { //if the user chooses an option outside of the menu
						System.out.println("\n---> Invalid Input. Try again.");
					}
				}
			}
			
			else { 
				//else the product does not exist and the following message is displayed...
				System.out.println("\n**Product ID Not Found**");
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void displaySingleProduct() {
		
		/*This method will ask to user to enter either a product's name or ID to search for it in the database
		 * and display all the information for that product. If the product is not found then a message will be
		 * displayed on the screen.*/
		
		System.out.println("_____{Search Product By}_____\r\n"
				 + "1) Product ID.\r\n"
				 + "2) Product Name.\r\n"
				 + "_____________________________");

		@SuppressWarnings("resource")
		Scanner scan = new Scanner(System.in);
		@SuppressWarnings("resource")
		Scanner scan_line = new Scanner(System.in);
		
		int choice = scan.nextInt();
		
		while (choice != 1 && choice != 2) {
			System.out.println("\n---> Invalid Input. Try again.");
			System.out.println("\n_____{Search Product By}_____\r\n"
					 + "1) Product ID.\r\n"
					 + "2) Product Name.\r\n"
					 + "_____________________________");
			choice = scan.nextInt();
		}
		
		String select = "";
		
		if (choice == 1) {
			//If the user decides to search by ID
			System.out.println("\n---> Please enter the product's ID:");
			int id = scan.nextInt();
			select = "SELECT * FROM products WHERE id = '" + id + "';";
		}
		
		else if (choice == 2) {
			//If the user decides to search by Name
			System.out.println("\n---> Please enter the product's name:");
			String name = scan_line.nextLine();
			select = "SELECT * FROM products WHERE name = '" + name + "';";
		}
		
		try {
			result_set = statement.executeQuery(select);
			
			if (result_set.next()) { 
				//This will be executed if the ID or Name has been found
				
				System.out.println("\n[Product ID: " + result_set.getInt("id") + "]:\n" 
					    + "     > Name = " + result_set.getString("name") + "\n"
					    + "     > Quantity = " + result_set.getDouble("quantity") + "\n"
					    + "     > Price = " + result_set.getDouble("price") + " " + currency);
				System.out.println();
			}
			
			else {
				//If the ID or Name has not been found one of the following will then be executed...
				
				if (choice == 1) {
					System.out.println("\n**Product ID Not Found**");
				}
				else if (choice == 2) {
					System.out.println("\n**Product Name Not Found**");
				}
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} 
	}
	
	public void changeCurrency() {
		
		/*This method enables the user to change the currency of all products in the database either from
		 * USD to LBP or the inverse. It will start by displaying the current currency, then ask the user
		 * if they would like to change the currency of their products to the other currency and asks for
		 * the rate as well. Then the new currency will be displayed. An error will be displayed if the user
		 * chooses an invalid currency.*/
		
		System.out.println("\n---> The current currency is: " + currency);
		
		@SuppressWarnings("resource")
		Scanner scan_double = new Scanner(System.in);
		
		if (currency.equalsIgnoreCase("USD")) {
			//If the current currency is USD then we must convert to LBP
			
			System.out.println("\n---> Please enter the rate to convert from USD to LBP: ");
			double rate = scan_double.nextDouble();
			while (rate <= 0.0) { //to ensure correct rate input, can not be negative
				System.out.println("\n**Invalid Rate. The rate should be greater than 0. Try again.**");
				System.out.println("\n---> Please enter the rate to convert from LBP to USD: ");
				rate = scan_double.nextDouble();
			}
			
			String update_currency = "UPDATE currency SET currency_type = 'LBP'";
			String update_price = "UPDATE products SET price = price *" + rate + ";";
			try {
				statement.executeUpdate(update_currency); //updates the currency in the database
				statement.executeUpdate(update_price); //changes all prices in the database according to the specified rate
			} catch (SQLException e) {
				e.printStackTrace();
			}
			currency = "LBP"; //updates the currency in java for quick access
		}
		
		else if (currency.equalsIgnoreCase("LBP")) {
			//Otherwise if the currency is currently LBP we must convert to USB
			
			System.out.println("\n---> Please enter the rate to convert from LBP to USD: ");
			double rate = scan_double.nextDouble();
			while (rate <= 0.0) { //to ensure correct rate input, can not be negative
				System.out.println("\n**Invalid Rate. The rate should be greater than 0. Try again.**");
				System.out.println("\n---> Please enter the rate to convert from LBP to USD: ");
				rate = scan_double.nextDouble();
			}
			
			String update_currency = "UPDATE currency SET currency_type = 'USD'";
			String update_price = "UPDATE products SET price = price /" + rate + ";";
			try {
				statement.executeUpdate(update_currency); //updates the currency in the database
				statement.executeUpdate(update_price); //changes all prices in the database according to the specified rate
			} catch (SQLException e) {
				e.printStackTrace();
			}
			currency = "USD"; //updates the currency in java for quick access
		}
		
		System.out.println("\n---> The new currency is: " + currency);
	}
	
	public void readCurrency() {
		
		//a method used to update the currency variable from null to the value in the database
		//runs when the code first runs.
		
		String select = "SELECT currency_type FROM currency";
		try {
			result_set = statement.executeQuery(select);
			
			if (result_set.next()) {
				currency = result_set.getString(1); //reads the currency from the database
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public String hashFunction(String pass) {
		
		//This is a hash function that will be used to hash passwords in the database
		//it is better to hash passwords to prevent any form of hackers from getting access to passwords.
		
		String new_pass = "";
		int ascii = 0;
		for(int x = 0; x < pass.length(); x++) {
			if (x%2 == 0) {
				ascii = (int) pass.charAt(x) + 150;
				new_pass += ascii + "#";
			}
			else {
				ascii = (int) pass.charAt(x) + 150;
				new_pass += ascii + "$";
			}
		}
		return new_pass;
	}
	
	public boolean authenticate() {
		
		//Method to either check if a user enters valid information to log in
		//or to create an account for a user if they do not have an account.
		
		System.out.println("_____{Select Your Option}_____\r\n"
						 + "1) Log in.\r\n"
						 + "2) Sign up.\r\n"
						 + "______________________________");
		
		@SuppressWarnings("resource")
		Scanner scan = new Scanner(System.in);
		int choice = scan.nextInt();
		
		while (choice != 1 && choice != 2) {
			System.out.println("\n---> Invalid Input. Try again.\n");
			System.out.println("_____{Select Your Option}_____\r\n"
					 + "1) Log in.\r\n"
					 + "2) Sign up.\r\n"
					 + "______________________________");
			choice = scan.nextInt();
		}
		
		if (choice == 1) {
			//This will be executed if the user already has an account and wants to log in...
			
			boolean flag = true; //A flag that will be used to indicate if the user input the correct log in information or not.
			int counter = 0; //used to limit the number of times a user can input incorrect information
			
			while (flag && counter<5) { //max number of incorrect inputs is 5
				
				counter++;
				@SuppressWarnings("resource")
				Scanner scan_line = new Scanner(System.in);
				
				System.out.println("\n---> Please enter your email:");
				String email = scan_line.nextLine();
				System.out.println("\n---> Please enter your password:");
				String pass = scan_line.nextLine();
				String hashed_pass = hashFunction(pass);
				
				String checker = "SELECT full_name FROM users WHERE email = '" + email + "' AND password = '" + hashed_pass + "';";
				
				try {
					result_set = statement.executeQuery(checker); //this will check if the user's information is correct.
					
					if (result_set.next()) {
						//If the user's information is correct, then it will enter this if statement and end the loop.
						
						System.out.println("____________________");
						System.out.println("\n~~{ Greetings! }~~\n");
						String name = result_set.getString(1); //This will get the full name
						System.out.println("---> You are logged in as: " + name);
						flag = false; //This will allow us to exit the while loop so it wont prompt the user for information again.
						return true;
					}
					
					else {
						//If the user's information is NOT correct, then it will display this statement and loop again.
						//flag remains true so it will loop again.
						
						System.out.println("\n**Invalid Email or Password. Try again.**");
						int chances = 5 - counter;
						System.out.println("  Chances: " + chances);
					}
					
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		else if (choice == 2) {
			//This will be executed if the user does not have an account and wants to create one...
			
			boolean flag = true; //A flag that will be used to indicate if the user enters a duplicate email
			int counter = 0; //used to limit the number of times a user can input incorrect information
			
			while (flag && counter<5) { //max number of incorrect inputs is 5
				
				counter++;
				@SuppressWarnings("resource")
				Scanner scan_line = new Scanner(System.in);
				
				System.out.println("\n---> Please enter your full name:");
				String name = scan_line.nextLine();
				System.out.println("\n---> Please enter your email:");
				String email = scan_line.nextLine();
				System.out.println("\n---> Please enter your password:");
				String pass = scan_line.nextLine();
				String hashed_pass = hashFunction(pass);
				
				String checker = "SELECT full_name FROM users WHERE email = '" + email + "';";
				
				try {
					result_set = statement.executeQuery(checker);
					
					if (result_set.next()) {
						//We do not want to have more than 1 user with the same email
						//Flag will remain true and loop for new information
						
						System.out.println("\n**This email already exists. Try again.**");
						int chances = 5 - counter;
						System.out.println("  Chances: " + chances);
					}
					
					else {
						//This means that the email does not already exist so the user can register with that email
						
						String insert = "INSERT INTO users (full_name, email, password) VALUES (" + "'" + name + "','" + email + "','" + hashed_pass +"');";
						
						try {
							statement.executeUpdate(insert); //Update database with this user
							
						} catch (SQLException e) {
							e.printStackTrace();
						}

						System.out.println("\n---> You have successfully signed up! Logging in...");
						System.out.println("____________________");
						System.out.println("\n~~{ Greetings! }~~\n");
						System.out.println("---> You are logged in as: " + name);
						flag = false; //This will allow us to exit the while loop so it wont prompt the user for information again.
						return true;
					}
					
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return false;
	}
	
	public static int displayMenu() {
		
		//A method that displays the menu of choices for the user to choose from
		
		System.out.println("\n_______{MENU}_______\r\n");
		System.out.println("1) Display Statistics.\r\n"
				+ "2) List Products.\r\n"
				+ "3) Add New Product.\r\n"
				+ "4) Remove Product.\r\n"
				+ "5) Edit Product.\r\n"
				+ "6) Display Single Product.\r\n"
				+ "7) Change Currency.\r\n"
				+ "8) Exit.");
		System.out.println("____________________");
		System.out.println("\nPlease choose an option:");
		
		@SuppressWarnings("resource")
		Scanner scan = new Scanner(System.in);
		int choice = scan.nextInt();
		
		return choice;
	}
	
	public static void main(String[] args) {
		
		Supermarket sm = new Supermarket();
		sm.createStatement();
			
		if (sm.authenticate()) {
			/*If the user is authorized to use this program, the authentication will return true
			 * and the user will be able to proceed with the program.*/
			
			sm.readCurrency(); //So that we can read the currency from the database into java and store it in a variable for quick access.
			//System.out.println("The currency is: " + sm.currency);
			
			int counter = 0; //in order to ensure that the user does not input consecutive wrong options from the menu
			int option = displayMenu();
			
			while (option != 8 && counter < 5) { 
				//8 is for the user to exit 
				//and we need to make sure that the user does not input an invalid option 5 consecutive times.
				
				if (option == 1) {
					counter = 0; //we refresh counter since the user input a valid option.
					System.out.println("\n---> Displaying Statistics...");
					sm.displayStatistics();
					option = displayMenu();
				}
				
				else if (option == 2) {
					counter = 0; //we refresh counter since the user input a valid option.
					sm.listProducts();
					option = displayMenu();
				}
				
				else if (option == 3) {
					counter = 0; //we refresh counter since the user input a valid option.
					sm.addProduct();
					option = displayMenu();
				}
				
				else if (option == 4) {
					counter = 0; //we refresh counter since the user input a valid option.
					sm.removeProduct();
					option = displayMenu();
				}
				
				else if (option == 5) {
					counter = 0; //we refresh counter since the user input a valid option.
					sm.editProduct();
					option = displayMenu();
				}
				
				else if (option == 6) {
					counter = 0; //we refresh counter since the user input a valid option.
					sm.displaySingleProduct();
					option = displayMenu();
				}
				
				else if (option == 7) {
					counter = 0; //we refresh counter since the user input a valid option.
					sm.changeCurrency();
					option = displayMenu();
				}
				
				else if (option != 8) { //if the user enters an invalid option from the menu
					System.out.println("\n**Invalid input. Please choose an option from the menu ONLY.**");
					counter++; //we increment counter to keep track of the consecutive invalid inputs.
					int chances = 5 - counter;
					System.out.println("  Chances: " + chances);
					
					if (counter < 5) {
						option = displayMenu();
					}
				}
			}
			
			if (counter == 5 && option != 8) { //this will be displayed only if the user inputs 5 consecutive errors without exiting.
				System.out.println("\n_______________{Program Terminated}_______________\r\n"
						 + "You reached your maximum consecutive invalid inputs.\r\n"
						 + "__________________________________________________");
			}
			else { //otherwise the user has exited and a message is displayed
				System.out.println("\n__________{You have successfully exited!}__________");
			}
		}
		
		else {
			//The user exceeded their invalid inputs and is not allowed to proceed with the program.
			
			System.out.println("\n_______________{Access Denied}_______________\r\n"
							 + "You exceeded your incorrect input limit.\r\n"
							 + "You are not authorized to use this software.\r\n"
							 + "_____________________________________________");
		}
	}
}

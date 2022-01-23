//Class that controls the database server.
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
//Imported packages.
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
public class Dbmanager{
	//Connection variable "conn" and the needed constructor to input the Connection value.
	//It's a private static so it can be controlled from any method of this class.
	private static Connection conn=null;
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	//Constructor.
	private static void setConnection(Connection value){
		    Dbmanager.conn=value;
	}
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	//A method that takes a string as an input and return an array of integers.
	//It takes the date and time in a format of string and cut it so it can be used by
	//LocalDateTime variable and the package methods.
	public static ArrayList<Integer> cutdatestring(String s){
		ArrayList<Integer> rep = new ArrayList<Integer>();
		rep.add(Integer.parseInt(s.substring(0,4)));
		rep.add(Integer.parseInt(s.substring(5,7)));
		rep.add(Integer.parseInt(s.substring(8,10)));
		rep.add(Integer.parseInt(s.substring(11,13)));
		rep.add(Integer.parseInt(s.substring(14,16)));
		rep.add(Integer.parseInt(s.substring(17,19)));
		return rep;
	}
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	//Method for connection to the database
	public static void connect() throws SQLException{
		String url="jdbc:mysql://localhost:3306/";
		String dbName="carrentdb";
		String driver="com.mysql.jdbc.Driver";
		String userName="root"; 
		String password="";
		try{
			Class.forName(driver).newInstance();
			setConnection(DriverManager.getConnection(url+dbName,userName,password));
			System.out.println("Connected to database.");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	//Method for disconnection from the database
	public static void disconnect() throws SQLException{
		conn.close();
		System.out.println("Disconnected from database.");
	}
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	//Method for deleting all tables.
	//First it "disables" the foreign keys, and then the drop table command can execute
	public static void dropalltable() throws SQLException{
		int i=0;
		boolean success=true;
		String[] array={"store","car","customer","rent_detail","help"};
		String sql=null;
		Statement st=null;
		try{
			st=conn.createStatement();
			sql="SET GLOBAL FOREIGN_KEY_CHECKS=0;";
			st.executeUpdate(sql);
		}catch(SQLException s){
			System.out.println("SQL statement is not executed!");
			System.out.println(s);
		}
		for(i=0;i<5;i++){
			try{
				st=conn.createStatement();
				sql="DROP TABLE "+array[i]+";";
				st.executeUpdate(sql);
			}catch(SQLException s){
				success=false;
				System.out.println("SQL statement is not executed!");
				System.out.println(s);
				System.out.println("Failed to delete "+array[i]+" table.");
			}
		}
		if(success==true){
			System.out.println("Successfully deleted all tables.");
		}
	}
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	//Method that creates the tables asked and makes the link between them.
	//Sets primary keys for tables and foreign keys for the linking.
	public static void createalltable() throws SQLException{
		Statement st=null;
		String sql=null;
		try{
			st=conn.createStatement();
			sql="CREATE TABLE IF NOT EXISTS help (counter INTEGER NOT NULL);";
			st.executeUpdate(sql);
			try{
				st=conn.createStatement();
				sql="SELECT counter FROM help";
				ResultSet res=st.executeQuery(sql);
				if(res.next()==false){
					try{
						st=conn.createStatement();
						sql="INSERT INTO help VALUES (0);";
						st.execute(sql);
					}catch(SQLException s){
						System.out.println("SQL statement is not executed!");
						System.out.println(s);
					}		
				}
				try{
					st=conn.createStatement();
					sql="UPDATE help SET counter=(counter+1);";
					st.execute(sql);
				}catch(SQLException s){
					System.out.println("SQL statement is not executed!");
					System.out.println(s);
				}
			}catch(SQLException s){
				System.out.println("SQL statement is not executed!");
				System.out.println(s);
			}
		}catch(SQLException s){
			System.out.println("SQL statement is not executed!");
			System.out.println(s);
		}
		try{
			st=conn.createStatement();
			sql="SELECT counter FROM help";
			ResultSet res=st.executeQuery(sql);
			res.next();
			if(res.getInt(1)==1){
				System.out.println("Help table successfully created in database.");
				System.out.println("Creating table store in database.");
				try{
					st=conn.createStatement();
					sql="CREATE TABLE IF NOT EXISTS store (`Number` VARCHAR(50) NOT NULL, location VARCHAR(30) NOT NULL, carsum INTEGER, "+
						"PRIMARY KEY (`Number`, location));";
					st.executeUpdate(sql);
					System.out.println("Store table successfully created in database.");
				}catch(SQLException s){
					System.out.println("SQL statement is not executed!");
					System.out.println(s);
				}
				System.out.println("Creating table car in database.");
				try{
					st=conn.createStatement();
					sql="CREATE TABLE IF NOT EXISTS car (license_plate VARCHAR(20) NOT NULL, "+
							"type VARCHAR(10) NOT NULL, sit_num INTEGER NOT NULL, "+
							"door_num INTEGER NOT NULL, conventional INTEGER NOT NULL, "+
							"fuel_type VARCHAR(10) NOT NULL, price_per_day DOUBLE NOT NULL, "+
							"store_number VARCHAR(50) NOT NULL, store_location VARCHAR(30) NOT NULL, PRIMARY KEY (license_plate), "+
							"FOREIGN KEY (store_number, store_location) REFERENCES store(`Number`, location));";
					st.executeUpdate(sql);
					System.out.println("Car table successfully created in database.");
				}catch(SQLException s){
					System.out.println("SQL statement is not executed!");
					System.out.println(s);
				}
				System.out.println("Creating table customer in database.");
				try{
					st=conn.createStatement();
					sql="CREATE TABLE IF NOT EXISTS customer (license_num VARCHAR(10) NOT NULL, "+
							"name VARCHAR(20) NOT NULL, surname VARCHAR(30) NOT NULL, "+
							"e_mail VARCHAR(50) NOT NULL, telephone_num VARCHAR(10) NOT NULL, "+
							"cellphone_num VARCHAR(15) NOT NULL, PRIMARY KEY (license_num));";
					st.executeUpdate(sql);
					System.out.println("Customer table successfully created in database.");
				}catch(SQLException s){
					System.out.println("SQL statement is not executed!");
					System.out.println(s);
				}
				System.out.println("Creating table rent_detail in database.");
				try{
					st=conn.createStatement();
					sql="CREATE TABLE IF NOT EXISTS rent_detail (id INTEGER NOT NULL AUTO_INCREMENT, `Number` VARCHAR(50) NOT NULL, "+
							"location VARCHAR(30) NOT NULL, rent_date VARCHAR(20) NOT NULL, return_date VARCHAR(20) NOT NULL, "+
							"return_number VARCHAR(50), return_location VARCHAR(30), type VARCHAR(10), "+
							"car_license_plate VARCHAR(20), customer_license_num VARCHAR(10), PRIMARY KEY (id), FOREIGN KEY (car_license_plate) "+
							"REFERENCES car(license_plate), FOREIGN KEY (customer_license_num) REFERENCES customer(license_num));";
					st.executeUpdate(sql);
					System.out.println("Rent_detail table successfully created in database.");
				}catch(SQLException s){
					System.out.println("SQL statement is not executed!");
					System.out.println(s);
				}
			}
		}catch(SQLException s){
			System.out.println("SQL statement is not executed!");
			System.out.println(s);
		}
		//Setting primary key inside the create table statement to improve returning results for the user.
		/*try{
			st=conn.createStatement();
			sql="ALTER TABLE car ADD CONSTRAINT car_pk PRIMARY KEY ( license_plate );";
			st.executeUpdate(sql);
			System.out.println("Successfully added primary key in car table.");
		}catch(SQLException s){
			System.out.println("SQL statement is not executed!");
			System.out.println(s);
		}
		try{
			st=conn.createStatement();
			sql="ALTER TABLE customer ADD CONSTRAINT customer_pk PRIMARY KEY ( license_num );";
			st.executeUpdate(sql);
			System.out.println("Successfully added primary key in car table.");
		}catch(SQLException s){
			System.out.println("SQL statement is not executed!");
			System.out.println(s);
		}
		//Done it with another way cause auto increment primary key id
		try{
			st=conn.createStatement();
			sql="ALTER TABLE rent_detail ADD CONSTRAINT rent_detail_pk PRIMARY KEY ( id );";
			st.executeUpdate(sql);
			System.out.println("Successfully added primary key in car table.");
		}catch(SQLException s){
			System.out.println("SQL statement is not executed!");
			System.out.println(s);
		}
		//Setting primary key and foreign keys inside the create table statement to improve returning results for the user.
		try{
			st=conn.createStatement();
			sql="ALTER TABLE store ADD CONSTRAINT store_pk PRIMARY KEY ( `Number`, location );";
			st.executeUpdate(sql);
			System.out.println("Successfully added primary keys in car table.");
		}catch(SQLException s){
			System.out.println("SQL statement is not executed!");
			System.out.println(s);
		}
		try{
			st=conn.createStatement();
			sql="ALTER TABLE car ADD CONSTRAINT car_store_fk FOREIGN KEY ( store_number, store_location ) "+
					"REFERENCES store ( `Number`, location );";
			st.executeUpdate(sql);
			System.out.println("Successfully added foreign keys and relation for car table from store table.");
		}catch(SQLException s){
			System.out.println("SQL statement is not executed!");
			System.out.println(s);
		}
		try{
			st=conn.createStatement();
			sql="ALTER TABLE rent_detail ADD CONSTRAINT rent_detail_car_fk FOREIGN KEY ( car_license_plate ) "+
					"REFERENCES car ( license_plate );";
			st.executeUpdate(sql);
			System.out.println("Successfully added foreign keys and relation for rent_detail table from car table.");
		}catch(SQLException s){
			System.out.println("SQL statement is not executed!");
			System.out.println(s);
		}
		try{
			st=conn.createStatement();
			sql="ALTER TABLE rent_detail ADD CONSTRAINT rent_detail_customer_fk FOREIGN KEY ( customer_license_num ) "+
					"REFERENCES customer ( license_num );";
			st.executeUpdate(sql);
			System.out.println("Successfully added foreign keys and relation for car table from customer table.");
		}catch(SQLException s){
			System.out.println("SQL statement is not executed!");
			System.out.println(s);
		}*/
	}
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	//Method to add a store at store table.
	public static void addstore(String Number,String Location) throws SQLException{
		try{
			PreparedStatement pst=conn.prepareStatement("INSERT INTO store VALUES (?,?,0);");
			pst.setString(1, Number);
			pst.setString(2, Location);
			pst.execute();
			System.out.println("Store successfully added.");
		}catch(SQLException s){
			System.out.println("SQL statement is not executed!");
			System.out.println(s);
		}
	}
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	//Method to add a car at car table. It updates the car summary value field at store table.
	public static void addcar(String License_Plate,String Type,int Sit_Num,int Door_Num,int Conventional,
			String Fuel_Type,double Price_per_Day,String Number,String Location) throws SQLException{
		try{
			PreparedStatement pst=conn.prepareStatement("INSERT INTO car VALUES (?,?,?,?,?,?,?,?,?);");
			pst.setString(1, License_Plate);
			pst.setString(2, Type);
			pst.setInt(3, Sit_Num);
			pst.setInt(4, Door_Num);
			pst.setInt(5, Conventional);
			pst.setString(6, Fuel_Type);
			pst.setDouble(7, Price_per_Day);
			pst.setString(8, Number);
			pst.setString(9, Location);
			pst.execute();
			System.out.println("Car successfully added.");
			try{
				pst=conn.prepareStatement("UPDATE store SET carsum=(carsum+1) WHERE number=? AND location=?;");
				pst.setString(1, Number);
				pst.setString(2, Location);
				pst.execute();
			}catch(SQLException s){
				System.out.println("SQL statement is not executed!");
				System.out.println(s);
			}
		}catch(SQLException s){
			System.out.println("SQL statement is not executed!");
			System.out.println(s);
		}
	}
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	//Method to delete a car from car table. It updates the car summary value field at store table.
	public static void deletecar(String License_Plate) throws SQLException{
		//All this comments is for a different way to make this work but i did not figure that
		//in time, keeping them in case of recoding this in the future..
		//The idea is delete the future rents, if the car is rented at the moment keep this rent form
		//and inform the administrator, and release the foreign key from past rents for this car for
		//saving purposes so that the car can be deleted.. Something like that.
		//!!Now i just delete every rent entry so i can free the foreign key license plate from rent_detail
		//!!table so i can delete this car.
		//LocalDateTime now=LocalDateTime.now(),next=null;
		try{
			int i=0;
			String[] s=new String[3];
			for (i=0;i<3;i++){
				s[i]=null;
			}
			PreparedStatement pst=conn.prepareStatement("SELECT license_plate, store_number, store_location FROM car WHERE "+
					"license_plate=?");
			pst.setString(1, License_Plate);
			ResultSet res=pst.executeQuery();
			res.next();
			s[0]=res.getString(2);
			s[1]=res.getString(3);
			try{
				pst=conn.prepareStatement("SELECT rent_date FROM rent_detail WHERE car_license_plate=?");
				pst.setString(1, License_Plate);
				res=pst.executeQuery();
			}catch(SQLException se){
				System.out.println("SQL statement is not executed!");
				System.out.println(se);
			}
			if(res.next()){
				do{
					//s[2]=res.getString(1);
					//next=LocalDateTime.of(Integer.parseInt(s[2].substring(0,4)),Integer.parseInt(s[2].substring(5,7)),
					//		Integer.parseInt(s[2].substring(8,10)),Integer.parseInt(s[2].substring(11,13)),
					//		Integer.parseInt(s[2].substring(14,16)), Integer.parseInt(s[2].substring(17,19)));
					//if((now.isBefore(next))||(now.isEqual(next))){
					try{
						pst=conn.prepareStatement("DELETE FROM rent_detail WHERE car_license_plate=?;");
						pst.setString(1, License_Plate);
						pst.execute();
					}catch(SQLException se){
						System.out.println("SQL statement is not executed!");
						System.out.println(se);
					}
					//}
				}while(res.next());
				System.out.println("Car rents successfully deleted.");
			}
			//else{
			try{
				//resource leak: 'pst' is not closed at this location
				/*PreparedStatement*/ pst/*1*/=conn.prepareStatement("DELETE FROM car WHERE license_plate=?;");
				pst/*1*/.setString(1, License_Plate);
				pst/*1*/.execute();
				System.out.println("Car successfully deleted.");
			}catch(SQLException se){
				System.out.println("SQL statement is not executed!");
				System.out.println(se);
			}
			//}
			try{
				pst=conn.prepareStatement("UPDATE store SET carsum=(carsum-1) WHERE number=? AND location=?;");
				pst.setString(1, s[0]);
				pst.setString(2, s[1]);
				pst.execute();
			}catch(SQLException se){
				System.out.println("SQL statement is not executed!");
				System.out.println(se);
			}
		}catch(SQLException se){
			System.out.println("SQL statement is not executed!");
			System.out.println(se);
		}
	}
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	//Method that prints every car(rented or not) at a certain store.
	public static void allcars(String Number,String Location) throws SQLException{
		try{
			String s=null;
			PreparedStatement pst=conn.prepareStatement("SELECT License_Plate, Type, Sit_Num, Door_Num, Conventional, Fuel_Type, Price_per_Day "+
			"FROM car WHERE Store_number=? AND Store_location=?;");
			pst.setString(1, Number);
			pst.setString(2, Location);
			ResultSet res=pst.executeQuery();
			if(res.next()){
				System.out.println("License Plate"+"\t"+"Type"+"\t"+"Sits"+"\t"+"Doors"+"\t"+"Conventional"+"\t"+"Fuel Type"+"\t"+"Price/Day");
				do{
					if(res.getInt(5)==1){
						s="Yes";
					}
					else{
						s="No";
					}
					System.out.println(res.getString(1)+"\t\t"+res.getString(2)+"\t"+res.getInt(3)+"\t"+res.getInt(4)+"\t"+s+
						"\t\t"+res.getString(6)+"\t\t"+res.getDouble(7));
				}while (res.next());
			}
			else{
				System.out.println("Sorry, no cars found for store at "+Number+","+Location+".");
			}
		}catch(SQLException s){
			System.out.println("SQL code does not execute.");
			System.out.println(s);
		}
	}
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	//Method that prints rented car at a certain store.
	public static void rentedcars(String Number,String Location) throws SQLException{
		ArrayList<Integer> rep = new ArrayList<Integer>();
		int flag=0;
		try{
			LocalDateTime now=LocalDateTime.now(),rent_time,return_time;
			DateTimeFormatter formatter=DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
			String s=null;
			PreparedStatement pst=conn.prepareStatement("SELECT car.License_Plate, car.Type, car.Sit_Num, car.Door_Num, car.Conventional, car.Fuel_Type, "+
			"car.Price_per_Day, rd.Rent_Date, rd.Return_Date FROM car, rent_detail rd WHERE car.Store_number=? AND car.Store_location=? AND "+
			"car.License_Plate=rd.Car_License_Plate;");
			pst.setString(1, Number);
			pst.setString(2, Location);
			ResultSet res=pst.executeQuery();
			if(res.next()){
				do{
					rep=cutdatestring(res.getString(8));
					rent_time=LocalDateTime.of(rep.get(0),rep.get(1),rep.get(2),rep.get(3),rep.get(4),rep.get(5));
					rep=cutdatestring(res.getString(9));
					return_time=LocalDateTime.of(rep.get(0),rep.get(1),rep.get(2),rep.get(3),rep.get(4),rep.get(5));
					if((now.isAfter(rent_time))&&(now.isBefore(return_time))){
						flag+=1;
						if(flag==1){
							System.out.println("License Plate"+"\t"+"Type"+"\t"+"Sits"+"\t"+"Doors"+"\t"+"Conventional"+"\t"+"Fuel Type"+"\t"+"Price/Day"+
									"\t"+"Rent Date"+"\t\t"+"Return Date");
						}
						if(res.getInt(5)==1){
							s="Yes";
						}
						else{
							s="No";
						}
						System.out.println(res.getString(1)+"\t\t"+res.getString(2)+"\t"+res.getInt(3)+"\t"+res.getInt(4)+"\t"+s+
								"\t\t"+res.getString(6)+"\t\t"+res.getDouble(7)+"\t\t"+rent_time.format(formatter)+"\t"+return_time.format(formatter));
					}
				}while (res.next());
			}
			if(flag==0){
				System.out.println("Sorry, no cars found rented at:"+now.format(formatter)+" for store at "+Number+","+Location+".");
			}
		}catch(SQLException s){
			System.out.println("SQL code does not execute.");
			System.out.println(s);
		}
	}
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	//Method that validates the address of the returning store
	public static String checkcar(String License_Plate){
		String result=null;
		try{
			PreparedStatement pst=conn.prepareStatement("SELECT License_Plate FROM Car WHERE License_Plate=?;");
			pst.setString(1, License_Plate);
			ResultSet res=pst.executeQuery();
			if(res.next()){
				result=res.getString(1);
			}
		}catch(SQLException s){
			System.out.println("SQL code does not execute.");
			System.out.println(s);
		}
		return result;
	}
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	//Method that validates the address of the returning store
	public static String checkadress(String Number){
		String result=null;
		try{
			PreparedStatement pst=conn.prepareStatement("SELECT Number FROM Store WHERE Number=?;");
			pst.setString(1, Number);
			ResultSet res=pst.executeQuery();
			if(res.next()){
				result=res.getString(1);
			}
		}catch(SQLException s){
			System.out.println("SQL code does not execute.");
			System.out.println(s);
		}
		return result;
	}
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	//Method that validates the location of the returning store
	public static String checklocation(String Location){
		String result=null;
		try{
			PreparedStatement pst=conn.prepareStatement("SELECT Location FROM Store WHERE Location=?;");
			pst.setString(1, Location);
			ResultSet res=pst.executeQuery();
			if(res.next()){
				result=res.getString(1);
			}
		}catch(SQLException s){
			System.out.println("SQL code does not execute.");
			System.out.println(s);
		}
		return result;
	}
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	//Method that validates the location of the returning store
	public static boolean validatestore(String Number,String Location){
		boolean result=false;
		try{
			PreparedStatement pst=conn.prepareStatement("SELECT Number,Location FROM Store WHERE Number=? AND Location=?;");
			pst.setString(1, Number);
			pst.setString(2, Location);
			ResultSet res=pst.executeQuery();
			if(res.next()){
				result=true;
			}
		}catch(SQLException s){
			System.out.println("SQL code does not execute.");
			System.out.println(s);
		}
		return result;
	}
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	public static String checkcustomer(String License_Num){
		String result=null;
		try{
			PreparedStatement pst=conn.prepareStatement("SELECT License_Num FROM Customer WHERE License_Num=?;");
			pst.setString(1, License_Num);
			ResultSet res=pst.executeQuery();
			if(res.next()){
				result=res.getString(1);
			}
		}catch(SQLException s){
			System.out.println("SQL code does not execute.");
			System.out.println(s);
		}
		return result;
	}
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	public static String[] existedcustomers(String License_Num){
		String[] mylist = new String[5];
		for(int i=0;i<5;i++){
			mylist[i]=null;
		}
		try{
			PreparedStatement pst=conn.prepareStatement("SELECT * FROM Customer WHERE License_Num=?;");
			pst.setString(1, License_Num);
			ResultSet res=pst.executeQuery();
			if(res.next()){
				mylist[0]=res.getString(2);
				mylist[1]=res.getString(3);
				mylist[2]=res.getString(4);
				mylist[3]=res.getString(5);
				mylist[4]=res.getString(6);
			}
		}catch(SQLException s){
			System.out.println("SQL code does not execute.");
			System.out.println(s);
		}
		return mylist;
	}
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	//Method that prints all the available cars for the client for certain store, rent date,
	//return date and type(optional).
	public static ArrayList<String> clientavailablecars(String Number,String Location,String Rent_Date,String Return_Date,
			String Type) throws SQLException{
		ArrayList<String> mylist = new ArrayList<String>();
		try{
			String s=null;
			PreparedStatement pst;
			if(Type==null){
				pst=conn.prepareStatement("SELECT C.License_Plate, C.Type, C.Sit_Num, C.Door_Num, C.Conventional, C.Fuel_Type, C.Price_per_Day FROM car C "+
						"WHERE C.Store_number=? AND C.Store_location=? AND C.License_Plate NOT IN (SELECT RD.car_license_plate FROM rent_detail RD WHERE RD.number=? "+
						"AND RD.location=? AND (? BETWEEN rd.Rent_Date AND rd.Return_Date OR ? BETWEEN rd.Rent_Date AND rd.Return_Date));");
				pst.setString(1, Number);
				pst.setString(2, Location);
				pst.setString(3, Number);
				pst.setString(4, Location);
				pst.setString(5, Rent_Date);
				pst.setString(6, Return_Date);
				/*if(Type!=res.getString(2)){
					continue;
				}*/
			}
			else{
				pst=conn.prepareStatement("SELECT C.License_Plate, C.Type, C.Sit_Num, C.Door_Num, C.Conventional, C.Fuel_Type, C.Price_per_Day FROM car C "+
						"WHERE C.Store_number=? AND C.Store_location=? AND C.Type=? AND C.License_Plate NOT IN (SELECT RD.car_license_plate FROM rent_detail RD "+
						"WHERE RD.number=? AND RD.location=? AND (? BETWEEN rd.Rent_Date AND rd.Return_Date OR ? BETWEEN rd.Rent_Date AND rd.Return_Date));");
				pst.setString(1, Number);
				pst.setString(2, Location);
				pst.setString(3, Type);
				pst.setString(4, Number);
				pst.setString(5, Location);
				pst.setString(6, Rent_Date);
				pst.setString(7, Return_Date);
			}
			ResultSet res=pst.executeQuery();
			if(res.next()){
				System.out.println("License Plate"+"\t"+"Type"+"\t"+"Sits"+"\t"+"Doors"+"\t"+"Conventional"+"\t"+"Fuel Type"+"\t"+"Price/Day");
				do{
					if(res.getInt(5)==1){
						s="Yes";
					}
					else{
						s="No";
					}
					mylist.add(res.getString(1));
					System.out.println(res.getString(1)+"\t\t"+res.getString(2)+"\t"+res.getInt(3)+"\t"+res.getInt(4)+"\t"+s+
							"\t\t"+res.getString(6)+"\t\t"+res.getDouble(7));
				}while (res.next());
			}
		}catch(SQLException s){
			System.out.println("SQL code does not execute.");
			System.out.println(s);
		}
		return mylist;
	}
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	//Method that returns the double variable type price for a car per day from car table.
	public static double carvalue(String License_Plate){
		double result=0;
		try{
			PreparedStatement pst=conn.prepareStatement("SELECT Price_per_Day FROM car WHERE License_Plate=?;");
			pst.setString(1, License_Plate);
			ResultSet res=pst.executeQuery();
			res.next();
			result=res.getDouble(1);
		}catch(SQLException s){
			System.out.println("SQL code does not execute.");
			System.out.println(s);
		}
		return result;
	}
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	//Method that gets the type of car.
	//It's supportive case i build the whole program with this structure so i don't get the type.
	//I believe it's really easy to get it through some other method but by this time it will cost me a lot of changes.
	public static String cartype(String License_Plate){
		String result=null;
		try{
			PreparedStatement pst=conn.prepareStatement("SELECT Type FROM car WHERE License_Plate=?;");
			pst.setString(1, License_Plate);
			ResultSet res=pst.executeQuery();
			res.next();
			result=res.getString(1);
		}catch(SQLException s){
			System.out.println("SQL code does not execute.");
			System.out.println(s);
		}
		return result;
	}
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	//Method that adds a client in the client table.
	public static void addclient(String License_Num,String Name,String Surname,String E_Mail,String Telephone_Num,
			String Cellphone_Num){
		try{
			PreparedStatement pst=conn.prepareStatement("INSERT INTO customer VALUES (?,?,?,?,?,?);");
			pst.setString(1, License_Num);
			pst.setString(2, Name);
			pst.setString(3, Surname);
			pst.setString(4, E_Mail);
			pst.setString(5, Telephone_Num);
			pst.setString(6, Cellphone_Num);
			pst.execute();
			System.out.println("Customer successfully added.");
		}catch(SQLException s){
			System.out.println("SQL statement is not executed!");
			System.out.println(s);
		}
	}
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	//Method that updates the rent detail table when we make a successful car rent.
	public static void rentcar(String Number,String Location,String Return_Number,String Return_Location,String Rent_Date,
			String Return_Date,String Type,String Car_License_Plate,String Customer_License_Num) throws SQLException{
		try{
			PreparedStatement pst=conn.prepareStatement("INSERT INTO rent_detail (Number,Location,Rent_Date,Return_Date,Return_Number,Return_Location,"+
					"Type,Car_License_Plate,Customer_License_Num) VALUES (?,?,?,?,?,?,?,?,?);");
			pst.setString(1,Number);
			pst.setString(2,Location);
			pst.setString(3,Rent_Date);
			pst.setString(4,Return_Date);
			pst.setString(5,Return_Number);
			pst.setString(6,Return_Location);
			pst.setString(7,Type);
			pst.setString(8,Car_License_Plate);
			pst.setString(9,Customer_License_Num);
			pst.execute();
			System.out.println("Car successfully rented.");
		}catch(SQLException s){
			System.out.println("SQL code does not execute.");
			System.out.println(s);
		}
	}
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
}
//End of class.
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
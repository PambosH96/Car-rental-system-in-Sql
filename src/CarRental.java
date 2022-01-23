//Class that controls the application.
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
//Imported packages.
import java.util.ArrayList;
import java.util.Scanner;
import java.sql.*;
import java.time.Duration;
import java.time.LocalDateTime;
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
public class CarRental{
	//Only main method for this class, the application itself.
	//Every method needed is called from Dbmanager.java.
	public static void main(String[] args) throws SQLException{
		//Variables needed.
		LocalDateTime rent_time,return_time;
		int i=0, i1=0, i2=0, i3=0, menu1=0, menu2=0, locali=0;
		double d=0,d2=0;
		ArrayList<String> mylist = new ArrayList<String>();
		ArrayList<Integer> rep = new ArrayList<Integer>();
		String[] s=new String[14];
		String iscus=null;
		for (i=0;i<14;i++){
			s[i]=null;
		}
		//2 scanners, one for numbers and one for strings.
		//That way i don't have to flush the scanner in some cases to prevent,
		//false reading.
		Scanner scan=new Scanner(System.in);
		Scanner Sscan=new Scanner(System.in);
		//Connection to the database call.
		System.out.print("Connect to the Database?(Yes/No):");
		if(Sscan.nextLine().compareToIgnoreCase("yes")==0){
			Dbmanager.connect();
		}
		else{
			System.exit(0);
		}
		//Table creation to the database.
		Dbmanager.createalltable();
		System.out.println("~~~Welcome to Exclusive rent a car system manager~~~");
		//Manage the menu till someone enters -1.
		while(true){
			System.out.println("Welcome to main menu!");
			System.out.println("To access the database manager enter '1', to access the rent utility enter '2', to exit the system enter '-1':");
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
			//In this section, we take privilege as the "admin" user and we can input the basic
			//compounds to the starting databases, or even later if we want to add something.
			//We can execute adding a store or cars and deleting cars.
			menu1=scan.nextInt();
			if(menu1==1){
				System.out.println("Welcome Admin!");
				//Menu runs till the Admin user put the exit condition.
				while(true){
					System.out.println("Choose: '1' to add a store, '2' to add a car, '3' to delete a car, '0' to go back to main menu.");
					menu2=scan.nextInt();
					//Back to main menu.
					if(menu2==0){
						System.out.println("Back to main menu.");
						break;
					}
					//Add a store. We input the address and the location of the store,
					//and call addstore method from Dbmanager class.
					else if(menu2==1){
						do{
							System.out.print("Enter the store address:");
							s[0]=Sscan.nextLine();
							System.out.print("Enter the store location:");
							s[1]=Sscan.nextLine();
							if(Dbmanager.validatestore(s[0], s[1])){
								System.out.println("Store already exists. Try again!");
							}
						}while(Dbmanager.validatestore(s[0], s[1]));
						Dbmanager.addstore(s[0], s[1]);
						continue;
					}
					//Add a car. We input the license plate, the type, the sit number,
					//the door number, if the car is conventional, the fuel type,
					//the price per day, the store address and the store location
					//and call addcar method from Dbmanager class.
					else if(menu2==2){
						do{
						System.out.print("Enter the car's license plate:");
						s[0]=Sscan.nextLine();
						s[0]=s[0].toUpperCase();
							if(s[0].equalsIgnoreCase(Dbmanager.checkcar(s[0]))){
								System.out.println("Car with license plate:"+s[0]+" already exists. Try again!");
							}
						}while(s[0].equalsIgnoreCase(Dbmanager.checkcar(s[0])));
						System.out.print("Enter the car's type:");
						s[1]=Sscan.nextLine();
						System.out.print("Enter the car's sit number:");
						i1=scan.nextInt();
						System.out.print("Enter the car's door number:");
						i2=scan.nextInt();
						System.out.print("Is the car conventional('yes'/'no'):");
						if(Sscan.nextLine().compareToIgnoreCase("yes")==0){
							i3=1;
						}
						else{
							i3=0;
						}
						System.out.print("Enter the car's fuel type:");
						s[2]=Sscan.nextLine();
						System.out.print("Enter the car's price per day:");
						d=scan.nextDouble();
						do{
							System.out.print("Enter the car's store address:");
							s[3]=Sscan.nextLine();
							while((s[3].equalsIgnoreCase(Dbmanager.checkadress(s[3])))==false){
								System.out.println("Invalid input. Store address not found.");
								System.out.print("Enter the store address:");
								s[3]=Sscan.nextLine();
							}
							System.out.print("Enter the car's store location:");
							s[4]=Sscan.nextLine();
							while((s[4].equalsIgnoreCase(Dbmanager.checklocation(s[4])))==false){
								System.out.println("Invalid input. Store location not found.");
								System.out.print("Enter the store location:");
								s[4]=Sscan.nextLine();
							}
							if(!(Dbmanager.validatestore(s[3], s[4]))){
								System.out.println("Address and location exists but don't match. Try again!");
							}
						}while(!(Dbmanager.validatestore(s[3], s[4])));
						Dbmanager.addcar(s[0], s[1], i1, i2, i3, s[2], d, s[3], s[4]);
						continue;
					}
					//Delete a car. We input the license plate and the car gets deleted.
					else if(menu2==3){
						System.out.print("Enter the car's license plate:");
						s[0]=Sscan.nextLine();
						while((s[0].equalsIgnoreCase(Dbmanager.checkcar(s[0])))==false){
							System.out.println("Invalid input. Car not found.");
							System.out.print("Enter the car's license plate:");
							s[0]=Sscan.nextLine();
						}
						Dbmanager.deletecar(s[0]);
						continue;
					}
					//System occurs an error if the input is not valid.
					else{
						System.out.println("Option not available!");
						continue;
					}
				}
				continue;
			}
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
			//
			else if(menu1==2){
				//In this section, we login as the "Employee" user and we can do:
				//1)Show store's cars, available or not.
				//2)Show store's cars that are rented at the moment.
				//3)Rent a car.
				System.out.println("Welcome Employee.");
				//Menu runs till the Employee user put the exit condition.
				while(true){
					System.out.println("Choose: '1' to show all cars in a store, '2' to show all cars rented at the moment in the store, "+
							"'3' to rent a car, '0' to go back to main menu.");
					menu2=scan.nextInt();
					//Back to main menu.
					if(menu2==0){
						System.out.println("Back to main menu.");
						break;
					}
					//Show the list of all cars(rented or not). We input the address,
					//and the location of the store and call allcars method from Dbmanager class.
					else if(menu2==1){
						do{
							System.out.print("Enter the store address:");
							s[0]=Sscan.nextLine();
							while((s[0].equalsIgnoreCase(Dbmanager.checkadress(s[0])))==false){
								System.out.println("Invalid input. Store address not found.");
								System.out.print("Enter the store address:");
								s[0]=Sscan.nextLine();
							}
							System.out.print("Enter the store location:");
							s[1]=Sscan.nextLine();
							while((s[1].equalsIgnoreCase(Dbmanager.checklocation(s[1])))==false){
								System.out.println("Invalid input. Store location not found.");
								System.out.print("Enter the store location:");
								s[1]=Sscan.nextLine();
							}
							if(!(Dbmanager.validatestore(s[0], s[1]))){
								System.out.println("Address and location exists but don't match. Try again!");
							}
						}while(!(Dbmanager.validatestore(s[0], s[1])));
						Dbmanager.allcars(s[0], s[1]);
						continue;
					}
					//Show the list of rented cars. We input the address and the location
					//of the store and call rentedcars method from Dbmanager class.
					else if(menu2==2){
						do{
							System.out.print("Enter the store address:");
							s[0]=Sscan.nextLine();
							while((s[0].equalsIgnoreCase(Dbmanager.checkadress(s[0])))==false){
								System.out.println("Invalid input. Store address not found.");
								System.out.print("Enter the store address:");
								s[0]=Sscan.nextLine();
							}
							System.out.print("Enter the store location:");
							s[1]=Sscan.nextLine();
							while((s[1].equalsIgnoreCase(Dbmanager.checklocation(s[1])))==false){
								System.out.println("Invalid input. Store location not found.");
								System.out.print("Enter the store location:");
								s[1]=Sscan.nextLine();
							}
							if(!(Dbmanager.validatestore(s[0], s[1]))){
								System.out.println("Address and location exists but don't match. Try again!");
							}
						}while(!(Dbmanager.validatestore(s[0], s[1])));
						Dbmanager.rentedcars(s[0], s[1]);
						continue;
					}
					//We access the car reservation menu.
					//First we input the store address, the store location,
					//the return store address, the return store location,
					//the rent date, the return date and the car type and we
					//get a list with all available cars for renting at the
					//moment.
					else if(menu2==3){
						do{
							System.out.print("Enter the store address:");
							s[0]=Sscan.nextLine();
							while((s[0].equalsIgnoreCase(Dbmanager.checkadress(s[0])))==false){
								System.out.println("Invalid input. Store address not found.");
								System.out.print("Enter the store address:");
								s[0]=Sscan.nextLine();
							}
							System.out.print("Enter the store location:");
							s[1]=Sscan.nextLine();
							while((s[1].equalsIgnoreCase(Dbmanager.checklocation(s[1])))==false){
								System.out.println("Invalid input. Store location not found.");
								System.out.print("Enter the store location:");
								s[1]=Sscan.nextLine();
							}
							if(!(Dbmanager.validatestore(s[0], s[1]))){
								System.out.println("Address and location exists but don't match. Try again!");
							}
						}while(!(Dbmanager.validatestore(s[0], s[1])));
						do{
							System.out.print("Enter the return store address:");
							s[2]=Sscan.nextLine();
							while((s[2].equalsIgnoreCase(Dbmanager.checkadress(s[2])))==false){
								System.out.println("Invalid input. Store address not found.");
								System.out.print("Enter the return store address:");
								s[2]=Sscan.nextLine();
							}
							System.out.print("Enter the return store location:");
							s[3]=Sscan.nextLine();
							while((s[3].equalsIgnoreCase(Dbmanager.checklocation(s[3])))==false){
								System.out.println("Invalid input. Store location not found.");
								System.out.print("Enter the return store location:");
								s[3]=Sscan.nextLine();
							}
							if(!(Dbmanager.validatestore(s[2], s[3]))){
								System.out.println("Address and location exists but don't match. Try again!");
							}
						}while(!(Dbmanager.validatestore(s[2], s[3])));
						System.out.print("Enter the rent date(e.g. 2000-12-31 23:59:59):");
						s[4]=Sscan.nextLine();
						rep=Dbmanager.cutdatestring(s[4]);
						rent_time=LocalDateTime.of(rep.get(0),rep.get(1),rep.get(2),rep.get(3),rep.get(4),rep.get(5));
						do{
							System.out.print("Enter the return date(e.g. 2000-12-31 23:59:59):");
							s[5]=Sscan.nextLine();
							rep=Dbmanager.cutdatestring(s[5]);
							return_time=LocalDateTime.of(rep.get(0),rep.get(1),rep.get(2),rep.get(3),rep.get(4),rep.get(5));
							if((return_time.isBefore(rent_time))||(return_time.isEqual(rent_time))){
								System.out.println("Invalid return date input. Return date must be after rent date:"+s[4]+"!");
							}
						}while((return_time.isBefore(rent_time))||(return_time.isEqual(rent_time)));
						System.out.print("Enter the car type(null for nothing):");
						s[6]=Sscan.nextLine();
						if(s[6].equalsIgnoreCase("null")){
							s[6]=null;
						}
						mylist=Dbmanager.clientavailablecars(s[0], s[1], s[4], s[5], s[6]);
						if(mylist.isEmpty()){
							System.out.println("No cars available.");
							continue;
						}
						else{
						//We select the car we want by the license plate number and the application
						//returns the information we gave plus the total price.
							do{
								System.out.print("Select the car with the license plate you want:");
								s[7]=Sscan.nextLine();
								s[7]=s[7].toUpperCase();
								if(mylist.contains(s[7])==false){
									System.out.println("Invalid license plate.");
								}
							}while(mylist.contains(s[7])==false);
						}
						Duration duration=Duration.between(rent_time,return_time);
						locali=(int)Math.ceil((double)duration.toMillis()/86400000);
						d2=locali*Dbmanager.carvalue(s[7]);
						System.out.println("Store address:"+s[0]);
						System.out.println("Store location:"+s[1]);
						if(s[2]==null){
							System.out.println("Return store adress:"+s[0]);
						}
						else{
							System.out.println("Return store adress:"+s[2]);
						}
						if(s[3]==null){
							System.out.println("Return store location:"+s[1]);
						}
						else{
							System.out.println("Return store location:"+s[3]);
						}
						System.out.println("Rent date:"+s[4]);
						System.out.println("Return date:"+s[5]);
						s[6]=Dbmanager.cartype(s[7]);
						System.out.println("Car type:"+s[6]);
						System.out.println("Car license plate:"+s[7]);
						System.out.println("Car price for "+locali+" days:"+d2+"€");
						System.out.println("Do you want to proceed?(Yes/No)");
					//If we don't want to proceed with the renting the function just 
					//continues and no values are stored. Else we input the client's
					//information and we call addclient from Dbmanager to create the
					//client input to customer table and the primary key that we want 
					//as a foreign key on rent_detail table.
					//After we call rentcar from Dbmanager and we store the information
					//about the renting at rent_detail table. We insert only 9 elements
					//cause primary key id is auto increment.
						if(Sscan.nextLine().equalsIgnoreCase("no")){
							continue;
						}
						iscus="no";
						do{
							System.out.print("Enter your license number:");
							s[8]=Sscan.nextLine();
							if(s[8].equalsIgnoreCase(Dbmanager.checkcustomer(s[8]))){
								String[] cusinfo = new String[5];
								for(int z=0;z<5;z++){
									cusinfo[z]=null;
								}
								System.out.println("License number:"+s[8]+" already exists.");
								cusinfo=Dbmanager.existedcustomers(s[8]);
								System.out.print("Are you "+cusinfo[0]+" "+cusinfo[1]+" with license number:"+s[8]+"?(Yes/No)");
								iscus=Sscan.nextLine();
								if(iscus.equalsIgnoreCase("yes")){
									s[9]=cusinfo[0];
									s[10]=cusinfo[1];
									s[11]=cusinfo[2];
									s[12]=cusinfo[3];
									s[13]=cusinfo[4];
									break;
								}
								else{
									continue;
								}
							}
							else{
								break;
							}
						}while(true);
						if(iscus.equalsIgnoreCase("no")){
							System.out.print("Enter your name:");
							s[9]=Sscan.nextLine();
							System.out.print("Enter your Surname:");
							s[10]=Sscan.nextLine();
							System.out.print("Enter your Email:");
							s[11]=Sscan.nextLine();
							System.out.print("Enter your telephone number:");
							s[12]=Sscan.nextLine();
							System.out.print("Enter your cellphone number:");
							s[13]=Sscan.nextLine();
							Dbmanager.addclient(s[8],s[9],s[10],s[11],s[12],s[13]);
						}
						Dbmanager.rentcar(s[0],s[1],s[2],s[3],s[4],s[5],s[6],s[7],s[8]);
						continue;
					}
					//System occurs an error if the input is not valid.
					else{
						System.out.println("Option not available!");
						continue;
					}
				}
				continue;
			}
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
			//With this option we exit the program and we also have the option to delete
			//all tables.
			else if(menu1==-1){
				System.out.print("Do you want to drop the tables?('yes'/'no'):");
				if(Sscan.nextLine().compareToIgnoreCase("yes")==0){
					Dbmanager.dropalltable();
				}
				Dbmanager.disconnect();
				System.out.println("Goodbye!");
				scan.close();
				Sscan.close();
				System.exit(0);
			}
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
			//Printing an error message for invalid input and continue the while loop,
			//that means running again the basic menu.
			else{
				System.out.println("Option not available!");
				continue;
			}
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		}
	}
}
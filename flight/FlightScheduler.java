import java.util.*;
import java.io.*;
import java.lang.Math;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.text.DecimalFormat;
public class FlightScheduler {

    private static FlightScheduler instance;
	private ArrayList<Flight> Flights = new ArrayList<Flight>();
	private ArrayList<Location> Locations = new ArrayList<Location>();
	private int flight_id ;
	public FlightScheduler (){
		flight_id = 0;
	}
    public static void main(String[] args) {
        instance = new FlightScheduler(args);
        instance.run();
    }

    public static FlightScheduler getInstance() {
        return instance;
    }

    public FlightScheduler(String[] args) {
	}

    public void run() {
        // Do not use System.exit() anywhere in your code,
        // otherwise it will also exit the auto test suite.
        // Also, do not use static attributes otherwise
        // they will maintain the same values between testcases.
        // START YOUR CODE HERE
		Scanner in = new Scanner(System.in);
		System.out.print("User: ");
		while (in.hasNextLine()){
			String command = in.nextLine();
			String[] command1 = command.split(" ");
			if(command1[0].toUpperCase().equals("FLIGHTS") && command1.length == 1) {
				//FLIGHTS 
				Collections.sort(Flights,Comparator.comparing(Flight::getdepartTime));
				Collections.sort(Flights,Comparator.comparing(Flight::getsource));
				System.out.println("Flights");
				System.out.println("-------------------------------------------------------");
				System.out.printf("%-5s%-12s%-12s%s\n","ID","Departure","Arrival","Source --> Destination");
				System.out.println("-------------------------------------------------------");
				if(Flights.size() == 0){
					System.out.println("(None)");
				}
				else{
					for(int i = 0;i < Flights.size();i++){
						System.out.printf("%4s %-12s%-12s%s --> %s\n",Flights.get(i).getId(),Flight.toString(Flights.get(i).getdepartTime()),Flight.toString(Flights.get(i).getarriveTime()),Flights.get(i).getsource(),Flights.get(i).getdest());
					}
					
				}
				
			}
			else if(command1[0].toUpperCase().equals("FLIGHT")){
				if(command1.length == 1){
					System.out.println("Usage:\nFLIGHT <id> [BOOK/REMOVE/RESET] [num]\nFLIGHT ADD <departure time> <from> <to> <capacity>\nFLIGHT IMPORT/EXPORT <filename>");
				}
				else{
					if(command1[1].toUpperCase().equals("ADD")) {
						if(command1.length < 7){
							System.out.println("Usage:   FLIGHT ADD <departure time> <from> <to> <capacity>\nExample: FLIGHT ADD Monday 18:00 Sydney Melbourne 120");
						}
						else{
					//FLIGHT ADD <departure time> <from> <to> <capacity> - add a flight 
							String date1 = command1[2];
							String date2 = command1[3];
							String start = command1[4];
							String end = command1[5];
							String capacity = command1[6];
							int booked = 0;
							if(!isposint(capacity)){
								System.out.println("Invalid positive integer capacity.");


							}
							else if(start.toUpperCase().equals(end.toUpperCase())) {
								System.out.println("Source and destination cannot be the same place.");

							}
							else{
								int cap = Integer.parseInt(capacity);
								int status = addFlight(date1,date2,start,end,cap,booked);
								if(status == -1){
									System.out.println("Invalid departure time. Use the format <day_of_week> <hour:minute>, with 24h time.");

								}
								else if(status == -2){
									System.out.println("Invalid starting location.");

								}
								else if(status == -3){
									System.out.println("Invalid ending location.");

								}
								else if(status == -4){
									int source = 0;
									int dest = 0;
									for(int i =0; i < Locations.size();i++){
										if(Locations.get(i).getname().toUpperCase().equals(start.toUpperCase())) {
											source = i;
										}
										if(Locations.get(i).getname().toUpperCase().equals(end.toUpperCase())) {
											dest = i;
										}
									}
									Flight f = new Flight(flight_id,date1,date2,Locations.get(source),Locations.get(dest),cap,booked);
									System.out.println("Scheduling conflict! This flight clashes with "+Locations.get(source).hasRunwayDepartureSpace(f)+".");
								}
								else if(status == -5){
									int source = 0;
									int dest = 0;
									for(int i =0; i < Locations.size();i++){
										if(Locations.get(i).getname().toUpperCase().equals(start.toUpperCase())) {
											source = i;
										}
										if(Locations.get(i).getname().toUpperCase().equals(end.toUpperCase())) {
											dest = i;
										}
									}
									Flight f = new Flight(flight_id,date1,date2,Locations.get(source),Locations.get(dest),cap,booked);
									System.out.println("Scheduling conflict! This flight clashes with "+Locations.get(dest).hasRunwayArrivalSpace(f)+".");
								}
								else{
									System.out.println("Successfully added Flight "+(flight_id-1)+".");
								}
								
							}
							

						}
					}
					else if(command1[1].toUpperCase().equals("IMPORT")){
					//FLIGHT IMPORT <filename> 
						importFlights(command1);
					}
					else if(command1[1].toUpperCase().equals("EXPORT")){
					//FLIGHT EXPORT <filename> 
						if(command1.length<2){
							System.out.println("Error writing file.");
						}
						else{
							String filename = command1[2];
							 try (PrintWriter writer = new PrintWriter(new File(filename))) {
								Collections.sort(Flights,Comparator.comparing(Flight::getdepartTime));
								Collections.sort(Flights,Comparator.comparing(Flight::getsource));

      							StringBuilder sb = new StringBuilder();
								for(int i=0;i < Flights.size();i++){
      								sb.append(Flight.toString1(Flights.get(i).getdepartTime()));
      								sb.append(',');
									sb.append(Flights.get(i).getsource());
									sb.append(',');
									sb.append(Flights.get(i).getdest());
									sb.append(',');
									sb.append(Flights.get(i).getcapacity());
									sb.append(',');
									sb.append(Flights.get(i).getbooked());
									sb.append('\n');
								}

								writer.write(sb.toString());

								System.out.println("Exported " +Flights.size()+" flights.");

								} catch (FileNotFoundException e) {
									System.out.println("Error writing file.");
								}
						}	
					}
					else if(command1.length == 2){
					//FLIGHT <id>
							if(!validid(command1[1])){
								System.out.println("Invalid Flight ID.");
							}
							else{
								int flightID = Integer.parseInt(command1[1]);
								if(!containid(flightID)) {
									System.out.println("Invalid Flight ID.");
								}
								else{
									Flight f = findflight(flightID);
									System.out.println("Flight "+flightID);
									System.out.println("Departure:    "+Flight.toString(f.getdepartTime())+" "+f.getsource());
									System.out.println("Arrival:      "+Flight.toString(f.getarriveTime())+" "+f.getdest());
									int distance = (int)Math.round(f.getDistance());
									DecimalFormat df1 = new DecimalFormat("#,###");
									System.out.println("Distance:     "+df1.format(distance)+"km");
									int hour = f.getDuration()/60;
									int minute = f.getDuration()%60;
									System.out.println("Duration:     "+hour+"h "+minute+"m");
									double price = f.getTicketPrice();
									DecimalFormat df2 = new DecimalFormat("#.##");
									System.out.println("Ticket Cost:  $"+df2.format(price));
									int capacity = f.getcapacity();
									int booked = f.getbooked();
									System.out.println("Passengers:   "+booked+"/"+capacity);
								}
							}
					}
					else if(command1[2].toUpperCase().equals("BOOK")){
					//FLIGHT <id> BOOK <num> 
					    if(!isposint(command1[3])) {
							System.out.println("Invalid number of passengers to book.");
						}
						int flightID = Integer.parseInt(command1[1]);
						int number_of_passenger = Integer.parseInt(command1[3]);
						int num = 0;
						Flight f = findflight(flightID);
						if(f != null){
								double cost = f.book(number_of_passenger);
								if(cost < 0){
									System.out.print("The flight is full");
								}
								else{
									if(number_of_passenger < (f.getcapacity()-f.getbooked())){
										num = number_of_passenger;
									}
									else{
										num = f.getcapacity()-f.getbooked();
									}
									DecimalFormat df = new DecimalFormat("0.00");
									System.out.println("Booked "+num+ " passengers on flight "+flightID+" for a total cost of $"+df.format(cost));
								}
						}
					}
					else if(command1[2].toUpperCase().equals("REMOVE")){
						//FLIGHT <id> REMOVE 
						try{
						int flightID = Integer.parseInt(command1[1]);
						for(int i = 0;i < Flights.size();i++){
							if(flightID == Flights.get(i).getId()){
								Flights.remove(Flights.get(i));
							}
						}
						System.out.println("remove flight" + flightID);
						}catch (Exception e){
						}
					}
					else if(command1[2].toUpperCase().equals("RESET")) {
					//FLIGHT <id> RESET
						int flightID = Integer.parseInt(command1[1]);
						for(int i = 0;i < Flights.size();i++){
							if(flightID == Flights.get(i).getId()){
								Flights.get(i).setpassenger();
							}
						}
						System.out.println("Reset passengers booked to "+ 0+ " for " + "Flight "+ flightID + ","+ Flight.toString(findflight(flightID).getdepartTime())+findflight(flightID).getsource()+"-->"+findflight(flightID).getdest());
					}

				}
			}
			else if(command1[0].toUpperCase().equals("LOCATIONS") && command1.length == 1) {
				//LOCATIONS
				
				
				Collections.sort(Locations, Comparator.comparing(Location::getname));
				System.out.println("Locations ("+Locations.size()+"):");
				if(Locations.size() == 0){
					System.out.println("(None)");
				}
				else{
					for(int i = 0;i < Locations.size();i++){
						System.out.print(Locations.get(i).getname());
						if(i != Locations.size() - 1) {
							System.out.print(", ");
						}
					}
					System.out.println("");
				}

			}
			else if(command1[0].toUpperCase().equals("LOCATION")) {
				if(command1.length == 1){
					System.out.println("Usage:\nLOCATION <name>\nLOCATION ADD <name> <latitude> <longitude> <demand_coefficient>\nLOCATION IMPORT/EXPORT <filename>");
				}
				else{
					if(command1[1].toUpperCase().equals("ADD")){
						//LOCATION ADD <name> <lat> <long> <demand_coefficient>
						if(command1.length < 6){ //Not enough command arguments given.
							System.out.println("Usage:   LOCATION ADD <name> <lat> <long> <demand_coefficient>\nExample: LOCATION ADD Sydney -33.847927 150.651786 0.2");
						}
						else{
							String name = command1[2];
							String lat = command1[3];
							String lon = command1[4];
							String demand = command1[5];
							//System.out.println("Location add" + name+" "+latitude+" "+longtitude+" "+demand_coefficient);
							int status = addLocation(name,lat,lon,demand);
							if(status == -1){
								//Location is already present in thedatabase (case insensitive based onname)
								System.out.println("This location already exists.");

							}
							else if(status == -2) {
								System.out.println("Invalid latitude. It must be a number of degrees between -85 and +85.");

							}
							else if(status == -3) {
								System.out.println("Invalid longitude. It must be a number of degrees between -180 and +180.");

							}
							else if(status== -4) {
								System.out.println("Invalid demand coefficient. It must be a number between -1 and +1.");
	
							}
							else{
								System.out.println("Successfully added location "+name+".");
							}
						}
					}
				else if(command1.length == 2 && !command1[1].toUpperCase().equals("IMPORT") &&  !command1[1].toUpperCase().equals("EXPORT")){
					//LOCATION <name> 
					String name = command1[1];
					if(!containlocation(name)) {
						System.out.println("Invalid location name.");

					}
					else{
						Location l = null;
						for (int i = 0;i < Locations.size();i ++){
							if(name.toUpperCase().equals(Locations.get(i).getname().toUpperCase())){
								l = Locations.get(i);
								break;
							}
						}
						System.out.println("Location:    " +  l.getname());
						DecimalFormat df = new DecimalFormat("0.000000");
						System.out.println("Latitude:    " +  df.format(l.getlat()));
						System.out.println("Longitude:   " +  df.format(l.getlon()));
						DecimalFormat df1 = new DecimalFormat("0.0000");
						if(l.getDemand() > 0){
							System.out.println("Demand:      " + "+"+df1.format(l.getDemand()));
						}
						else {
							System.out.println("Demand:      " +df1.format(l.getDemand()));
						}

					}
				
				}
				else if(command1[1].toUpperCase().equals("IMPORT")){
				//FLIGHT IMPORT <filename> 
					/*if(command1.length<3){
						System.out.println("Erro reading file.");
					}*/
					importLocations(command1);

				}
				else if(command1[1].toUpperCase().equals("EXPORT")){
					if(command1.length<3){
							System.out.println("Error writing file.");
						}
						else{
							String filename = command1[2];
							 try (PrintWriter writer = new PrintWriter(new File(filename))) {
								Collections.sort(Locations, Comparator.comparing(Location::getname));
      							StringBuilder sb = new StringBuilder();
								for(int i=0;i < Locations.size();i++){
      								sb.append(Locations.get(i).getname());
      								sb.append(',');
									sb.append(Locations.get(i).getlat());
									sb.append(',');
									sb.append(Locations.get(i).getlon());
									sb.append(',');
									sb.append(Locations.get(i).getDemand());
									sb.append('\n');
								}

								writer.write(sb.toString());

								System.out.println("Exported " +Locations.size()+" locations.");

								} catch (FileNotFoundException e) {
									System.out.println("Error writing file.");
								}
						}	

				}
			}
		}
			else if (command1[0].toUpperCase().equals("SCHEDULE") && command1.length == 2){
				//SCHEDULE <location_name>
				String location = command1[1];
				System.out.println("SCHEDULE" + location);

			}
			else if (command1[0].toUpperCase().equals("DEPARTURES") && command1.length == 2){
				//DEPARTURES <location_name>
				String location = command1[1];
				System.out.println("DEPARTURES" + location);

			}
			else if (command1[0].toUpperCase().equals("ARRIVALS") && command1.length == 2) {
				//ARRIVALS <location_name>
				String location = command1[1];
				 Location l = new Location();
				 for(int i =0 ;i<Locations.size();i++){
					 if(Locations.get(i).getname().toUpperCase().equals(location)) {
						 l = Locations.get(i);
					 }
				 } 

				System.out.println("ARRVIALS" + location);

			}
			else if (command1[0].toUpperCase().equals("TRAVEL")){
			}
			else if (command1[0].toUpperCase().equals("HELP")){
				System.out.println("FLIGHTS - list all available flights ordered by departure time, then departure location name");
				System.out.println("FLIGHT ADD <departure time> <from> <to> <capacity> - add a flight");
				System.out.println("FLIGHT IMPORT/EXPORT <filename> - import/export flights to csv file");
				System.out.println("FLIGHT <id> - view information about a flight (from->to, departure arrival times, current ticket price, capacity, passengers booked)");
				System.out.println("FLIGHT <id> BOOK <num> - book a certain number of passengers for the flight at the current ticket price, and then adjust the ticket price to reflect the reduced capacity remaining. If no number is given, book 1 passenger. If the given number of bookings is more than the remaining capacity, only accept bookings until the capacity is full.");
				System.out.println("FLIGHT <id> REMOVE - remove a flight from the schedule");
				System.out.println("FLIGHT <id> RESET - reset the number of passengers booked to 0, and the ticket price to its original state.");
				System.out.println("");
				System.out.println("LOCATIONS - list all available locations in alphabetical order");
				System.out.println("LOCATION ADD <name> <lat> <long> <demand_coefficient> - add a location");
				System.out.println("LOCATION <name> - view details about a location (it's name, coordinates, demand coefficient)");
				System.out.println("LOCATION IMPORT/EXPORT <filename> - import/export locations to csv file");
				System.out.println("SCHEDULE <location_name> - list all departing and arriving flights, in order of the time they arrive/depart");
				System.out.println("DEPARTURES <location_name> - list all departing flights, in order of departure time");
				System.out.println("ARRIVALS <location_name> - list all arriving flights, in order of arrival time");
				System.out.println("");
				System.out.println("TRAVEL <from> <to> [sort] [n] - list the nth possible flight route between a starting location and destination, with a maximum of 3 stopovers. Default ordering is for shortest overall duration. If n is not provided, display the first one in the order. If n is larger than the number of flights available, display the last one in the ordering.");
				System.out.println("");
				System.out.println("can have other orderings:");
				System.out.println("TRAVEL <from> <to> cost - minimum current cost");
				System.out.println("TRAVEL <from> <to> duration - minimum total duration");
				System.out.println("TRAVEL <from> <to> stopovers - minimum stopovers");
				System.out.println("TRAVEL <from> <to> layover - minimum layover time");
				System.out.println("TRAVEL <from> <to> flight_time - minimum flight time");
				System.out.println("");
				System.out.println("HELP - outputs this help string.");
				System.out.println("EXIT - end the program.");
			}
			else if(command1[0].toUpperCase().equals("EXIT")){
				System.out.println("Application closed.");
				return;
			}
			else{
				System.out.println("Invalid command. Type 'help' for a list of commands.");
			}
			System.out.println("");
			System.out.print("User: ");

		}
		

    }

    // Add a flight to the database
	// handle error cases and return status negative if error 
	// (different status codes for different messages)
	// do not print out anything in this function
	public int addFlight(String date1, String date2, String start, String end, int capacity, int booked) {
		if(!isDate(date1,date2)) {
			return -1;
		}
		else if(!containlocation(start)) {
			return -2;
		}
		else if(!containlocation(end)) {
			return -3;
		}
		//else{
			else{
				int source = 0;
				int dest = 0;
				for(int i =0; i < Locations.size();i++){
					if(Locations.get(i).getname().toUpperCase().equals(start.toUpperCase())) {
						source = i;
					}
					if(Locations.get(i).getname().toUpperCase().equals(end.toUpperCase())) {
						dest = i;
					}
				}
				Flight f = new Flight(flight_id,date1,date2,Locations.get(source),Locations.get(dest),capacity,booked);
				if(Locations.get(source).hasRunwayDepartureSpace(f)!=null){
					return -4;
				}
				else if(Locations.get(dest).hasRunwayArrivalSpace(f)!=null) {
					return -5;
				}
				else{
					Flights.add(f);
					Locations.get(source).addDeparture(f);
					Locations.get(dest).addArrival(f);
					flight_id = flight_id+1;
					return 1;
				}
				/*Location start1 = getloc(start);
				Location end1 = getloc(end);
				Flight f = new Flight(flight_id,date1,date2,start1,end1,capacity,booked);
				start1.adddeparture(f);
				end1.adda
				Flights.add(f);
				flight_id = flight_id+1;
				return 1;
				*/
			
			}
		//}

	}
	
	// Add a location to the database
    // do not print out anything in this function
    // return negative numbers for error cases
	public int addLocation(String name, String lat, String lon, String demand) {
		if(containlocation(name)){
			return -1;
		}
		else{ 
			if(!isdouble(lat)) {
				return -2;
			}
			else if(Double.parseDouble(lat) < -85 || Double.parseDouble(lat) > 85) {
				return -2;
			}
			else if(!isdouble(lon)) {
				return -3;
			}
			else if(Double.parseDouble(lon)<-180 || Double.parseDouble(lon) >180) {
				return -3;
			}
			else if(!isdouble(demand)){
				return -4;
			}
			else if(Double.parseDouble(demand)<-1 || Double.parseDouble(demand) > 1){
				return -4;
			}
			else{
				Location l = new Location(name,Double.parseDouble(lat),Double.parseDouble(lon),Double.parseDouble(demand));
				Locations.add(l);
				return 0;
			}
		}
	}
	ArrayList<String> ar = new ArrayList<String>();
	//flight import <filename>
	public void importFlights(String[] command) {
		try {
			if (command.length < 3) throw new FileNotFoundException();
			BufferedReader br = new BufferedReader(new FileReader(new File(command[2])));
			String line;
			int count = 0;
			int err = 0;
			
			while ((line = br.readLine()) != null) {
				String[] lparts = line.split(",");
				if (lparts.length < 5) continue;
				String[] dparts = lparts[0].split(" ");
				if (dparts.length < 2) continue;
				int booked = 0;
				
				try {
					booked = Integer.parseInt(lparts[4]);
					
				} catch (NumberFormatException e) {
					continue;
				}
				
				int status = addFlight(dparts[0], dparts[1], lparts[1], lparts[2], Integer.parseInt(lparts[3]), booked);
				if (status < 0) {
					err++;
					/*String str = dparts[0]+dparts[1]+lparts[1]+lparts[2]+Integer.parseInt(lparts[3])+booked + status; // dsds
					ar.add(str);*/
					continue;
				}
				count++;
			}
			br.close();
			System.out.println("Imported "+count+" flight"+(count!=1?"s":"")+".");
			if (err > 0) {
				if (err == 1) System.out.println("1 line was invalid.");
				else System.out.println(err+" lines were invalid.");
			}
		} catch (IOException e) {
			System.out.println("Error reading file.");
			return;
		}
	}
	
	//location import <filename>
	public void importLocations(String[] command) {
		try {
			if (command.length < 3) throw new FileNotFoundException();
			BufferedReader br = new BufferedReader(new FileReader(new File(command[2])));
			String line;
			int count = 0;
			int err = 0;
			
			while ((line = br.readLine()) != null) {
				String[] lparts = line.split(",");
				if (lparts.length < 4) continue;
								
				int status = addLocation(lparts[0], lparts[1], lparts[2], lparts[3]);
				if (status < 0) {
					err++;
					continue;
				}
				count++;
			}
			br.close();
			System.out.println("Imported "+count+" location"+(count!=1?"s":"")+".");
			if (err > 0) {
				if (err == 1) System.out.println("1 line was invalid.");
				else System.out.println(err+" lines were invalid.");
			}
			
		} catch (IOException e) {
			System.out.println("Error reading file.");
			return;
		}
	}
	public boolean containlocation(String locationname) {
		for(int i = 0;i < Locations.size();i++){
			if(locationname.toUpperCase().equals(Locations.get(i).getname().toUpperCase())){
				return true;
			}
		}
		return false;
	} 
	public boolean isdouble (String i){
		try{
			double a = Double.parseDouble(i);
			return true;
		} catch (Exception e){
			return false;
		}
		 
	}
	public boolean isposint (String i){
		try{
			int a = Integer.parseInt(i);
			if (a > 0){
				return true;
			}
			return false;
		} catch (Exception e){
			return false;
		}
		 
	}
	public boolean isDate (String date1,String date2){
		try{
			String i = null;
			if(date1.toUpperCase().equals("MONDAY")) {
				i = "Monday " + date2;
			}
			else if(date1.toUpperCase().equals("TUESDAY")) {
				i = "Tuesday " + date2;
			}
			else if(date1.toUpperCase().equals("WEDNESDAY")) {
				i = "Wednesday " + date2;
			}
			else if(date1.toUpperCase().equals("THURSDAY")) {
				i = "Thursday " + date2;
			}
			else if(date1.toUpperCase().equals("FRIDAY")) {
				i = "Friday " + date2; 
			}
			else if(date1.toUpperCase().equals("SATURDAY")){
				i = "Saturday " + date2;
			}
			else if(date1.toUpperCase().equals("SUNDAY")){
				i = "Sunday " + date2;
			}
			SimpleDateFormat df = new SimpleDateFormat("EEEE HH:mm");
			df.setLenient(false);
            Date d = df.parse(i);
			return true;
		} catch (Exception e){
			return false;
		}	
	}
	public boolean validid(String id){
		try{
			int fid = Integer.parseInt(id);
			if(fid >= 0){
				return true;
			}
			return false;
		} catch (Exception e){
			return false;
		}
	}
	public boolean containid(int id){
		for(int i = 0;i < Flights.size();i++){
			if(id == Flights.get(i).getId()) {
				return true;
			}
		}
		return false;
	}
	public int getflightid(){
		return flight_id;
	}
	public Flight findflight(int id){
		for(int i = 0;i < Flights.size();i++){
			if(id == Flights.get(i).getId()){
				Flight f = Flights.get(i);
				return f;
			}
		}
		return null;
	}
	public Location getloc(String name){
		for(int i = 0;i < Locations.size();i++){
			if(name.toUpperCase().equals(Locations.get(i).getname().toUpperCase())){
				Location l =Locations.get(i);
				return l;
			}
		}
		return null;
	}
}

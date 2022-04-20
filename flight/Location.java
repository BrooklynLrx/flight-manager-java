import java.util.*;
import java.io.*;
import java.lang.Math;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.text.DecimalFormat;
public class Location implements Cloneable{
	//. Attributes will be the location name, latitude and
	//longitude coordinates, lists of arriving and departing flights, and a demand coefficient.
	private String name;
	private double lat;
	private double lon;
	private ArrayList<Flight> arriving_Flights = new ArrayList<Flight>();
	private ArrayList<Flight> departing_Flights = new ArrayList<Flight>();
	private double demand;
	public Location(){};
	public Location(String name, double lat, double lon, double demand) {
		this.name = name;
		this.lat = lat;
		this.lon = lon;
		this.demand = demand;
	}
	public String getname(){
		return name;
	}
	public void setname(String name){
		this.name = name;
	}
	public double getlat(){
		return lat;
	}
	public void setlat(double lat){
		this.lat = lat;
	}
	public double getlon(){
		return lon;
	}
	public ArrayList<Flight> getarr(){
		return arriving_Flights;
	}
    //Implement the Haversine formula - return value in kilometres
    public static double distance(Location l1, Location l2) {
		double r = 6371;// earth radius
		double lat1 = l1.lat*Math.PI / 180;
		double lat2 = l2.lat*Math.PI / 180;
		double lon1 = l1.lon*Math.PI / 180;
		double lon2 = l2.lon*Math.PI / 180;
		double latdif = (lat2-lat1)/2;
        double londif = (lon2-lon1)/2;
        double distance = 2*r*Math.asin(Math.sqrt(Math.pow(Math.sin(latdif),2)+Math.cos(lat1)*Math.cos(lat2)*Math.pow(Math.sin(londif),2)));
		return distance;
    }

    public void addArrival(Flight f) {
		if(f.getdest().equals(name)){
			arriving_Flights.add(f);
		}
	}
	
	public void addDeparture(Flight f) {
		if(f.getsource().equals(name)){
			departing_Flights.add(f);
		}
	}
	public Location clone() {
      	Location n = new Location();
    	try{
    		n=(Location)super.clone();
    	}
    	catch(Exception e ){
    	    e.printStackTrace();
    	}
       	return n;
	}
	
	/**
	 * Check to see if Flight f can depart from this location.
	 * If there is a clash, the clashing flight string is returned, otherwise null is returned.
	 * A conflict is determined by if any other flights are arriving or departing at this location within an hour of this flight's departure time.
	 * @param f The flight to check.
	 * @return "Flight <id> [departing/arriving] from <name> on <clashingFlightTime>". Return null if there is no clash.
	 */
	public String hasRunwayDepartureSpace(Flight f) {
		Date depart_time = f.getdepartTime();
		Flight departs_conflict = null;
		Flight arrival_conflict = null;
		Flight conflict=  null;
		String result = null;
		String status;
		String time;
		int lastgap1 = 60;
		if(departing_Flights.size() == 0 && arriving_Flights.size() == 0){
			return null;
		}
		//check departs first
		for(int i = 0;i < departing_Flights.size();i++){
			int gap = gap(depart_time,departing_Flights.get(i).getdepartTime());
			if(departing_Flights.get(i).getsource().equals(f.getsource())){
				if(gap < 60 && gap < lastgap1){				
					departs_conflict = departing_Flights.get(i);
					lastgap1 = gap;
				}
			}
		}
		int lastgap2 = 60;
		//check arrivals next
		for(int i = 0;i < arriving_Flights.size();i++){
			int gap = gap(depart_time,arriving_Flights.get(i).getarriveTime());
			if(arriving_Flights.get(i).getdest().equals(f.getsource())){
				if(gap < 60 && gap < lastgap2){
					arrival_conflict = arriving_Flights.get(i);
					lastgap2 = gap;
				}
			}
		}
		if(departs_conflict == null && arrival_conflict == null){
			return null;
		}
		else if(departs_conflict !=null && arrival_conflict == null){
			conflict = departs_conflict;
			status = " departing ";
			time = Flight.toString1(conflict.getdepartTime());
			int id = conflict.getid();
			String from = conflict.getsource();
			result = "Flight "+id + status+"from " +from + " on " + time;
		}
		else if(departs_conflict == null && arrival_conflict != null){
			conflict = arrival_conflict;
			status = " arriving ";
			time = Flight.toString1(conflict.getarriveTime());
			int id = conflict.getid();
			String at = conflict.getdest();
			result = "Flight "+id + status+"at " +at + " on " + time;
		}
		else{
			if (arrival_conflict.getarriveTime().getTime()-departs_conflict.getdepartTime().getTime() > 0) {
				if(departs_conflict.getdepartTime().getTime() - depart_time.getTime() > 0){
					conflict = departs_conflict;
					status = " departing ";
					time = Flight.toString1(conflict.getdepartTime());
					int id = conflict.getid();
					String from = conflict.getsource();
					result = "Flight "+id + status+"from " +from + " on " + time;
				}
				else{
					conflict  = arrival_conflict;
					status = " arriving ";
					time = Flight.toString1(conflict.getarriveTime());
					int id = conflict.getid();
					String at = conflict.getdest();
					result = "Flight "+id + status+"at " +at + " on " + time;
				}
			}
		    if (arrival_conflict.getarriveTime().getTime()-departs_conflict.getdepartTime().getTime() < 0) {
				if(arrival_conflict.getarriveTime().getTime()-depart_time.getTime() > 0){
					conflict  = arrival_conflict;
					status = " arriving ";
					time = Flight.toString1(conflict.getarriveTime());
					int id = conflict.getid();
					String at = conflict.getdest();
					result = "Flight "+id + status+"at " +at + " on " + time;
				}
				else{
					conflict = departs_conflict;
					status = " departing ";
					time = Flight.toString1(conflict.getdepartTime());
					int id = conflict.getid();
					String from = conflict.getsource();
					result = "Flight "+id + status+"from " +from + " on " + time;
				}
			}
		}
		return result;

    }

    /**
	 * Check to see if Flight f can arrive at this location.
	 * A conflict is determined by if any other flights are arriving or departing at this location within an hour of this flight's arrival time.
	 * @param f The flight to check.
	 * @return String representing the clashing flight, or null if there is no clash. Eg. "Flight <id> [departing/arriving] from <name> on <clashingFlightTime>"
	 */
	public String hasRunwayArrivalSpace(Flight f)  {
		Date arrive_time = f.getarriveTime();
		Flight departs_conflict = null;
		Flight arrival_conflict = null;
		Flight conflict=  null;
		String result = null;
		String status;
		String time;
		int lastgap1 = 60;
		if(departing_Flights.size() == 0 && arriving_Flights.size() == 0){
			return null;
		}
		//check departs first
		for(int i = 0;i < departing_Flights.size();i++){
			int gap = gap(arrive_time,departing_Flights.get(i).getdepartTime());
			if(departing_Flights.get(i).getsource().equals(f.getdest())){
				if(gap < 60 && gap < lastgap1){				
					departs_conflict = departing_Flights.get(i);
					lastgap1 = gap;
				}
			}
		}
		int lastgap2 = 60;
		//check arrivals next
		for(int i = 0;i < arriving_Flights.size();i++){
			int gap = gap(arrive_time,arriving_Flights.get(i).getarriveTime());
			if(arriving_Flights.get(i).getdest().equals(f.getdest())){
				if(gap < 60 && gap < lastgap2){
					arrival_conflict = arriving_Flights.get(i);
					lastgap2 = gap;
				}
			}
		}
		if(departs_conflict == null && arrival_conflict == null){
			return null;
		}
		else if(departs_conflict !=null && arrival_conflict == null){
			conflict = departs_conflict;
			status = " departing ";
			time = Flight.toString1(conflict.getdepartTime());
			int id = conflict.getid();
			String from = conflict.getsource();
			result = "Flight "+id + status+"from " +from + " on " + time;
		}
		else if(departs_conflict == null && arrival_conflict != null){
			conflict = arrival_conflict;
			status = " arriving ";
			time = Flight.toString1(conflict.getarriveTime());
			int id = conflict.getid();
			String at = conflict.getdest();
			result = "Flight "+id + status+"at " +at + " on " + time;
		}
		else{
			if (arrival_conflict.getarriveTime().getTime()-departs_conflict.getdepartTime().getTime() > 0) {
				if(departs_conflict.getdepartTime().getTime() - arrive_time.getTime() > 0){
					conflict = departs_conflict;
					status = " departing ";
					time = Flight.toString1(conflict.getdepartTime());
					int id = conflict.getid();
					String from = conflict.getsource();
					result = "Flight "+id + status+"from " +from + " on " + time;
				}
				else{
					conflict  = arrival_conflict;
					status = " arriving ";
					time = Flight.toString1(conflict.getarriveTime());
					int id = conflict.getid();
					String at = conflict.getdest();
					result = "Flight "+id + status+"at " +at + " on " + time;
				}
			}
		    if (arrival_conflict.getarriveTime().getTime()-departs_conflict.getdepartTime().getTime() < 0) {
				if(arrival_conflict.getarriveTime().getTime()-arrive_time.getTime() > 0){
					conflict  = arrival_conflict;
					status = " arriving ";
					time = Flight.toString1(conflict.getarriveTime());
					int id = conflict.getid();
					String at = conflict.getdest();
					result = "Flight "+id + status+"at " +at + " on " + time;
				}
				else{
					conflict = departs_conflict;
					status = " departing ";
					time = Flight.toString1(conflict.getdepartTime());
					int id = conflict.getid();
					String from = conflict.getsource();
					result = "Flight "+id + status+"from " +from + " on " + time;
				}
			}
		}
		return result;
    }
	public double getDemand(){
		return demand;
	}
	public static int gap(Date d1,Date d2) {
        /*String sunafter = "2007/01/07 Sunday 12:00";
        String monmor = "2007/01/01 Monday 12:00";
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/DD EEEE HH:mm");
        df.setLenient(false);
        Date sunaf = df.parse(sunafter);
        Date monmo = df.parse(monmor);
        if(d1.after(sunaf) && d2.before(monmo)){
            int gap =(int) (d2.getTime()-d1.getTime())/(60*1000)+24*60*7;
            return gap;
        }*/
        //else{
            int gap = Math.abs((int)((d2.getTime()-d1.getTime())/(60*1000)));
			if (gap > 10020 && gap < 10080){
				gap = 10080-gap;
			}
            return gap;
        
    }
	public static void main (String[] args) throws Exception{
		Location l1 = new Location("Sydney",-33.847927,150.651786,0.4);
		Location l2 = new Location("Perth",-32.0397559,115.681346,0.5);
		Location l3 = new Location("sdf",-42.8823399,147.3198016,0.1);
		Location l4 = l3.clone();
		System.out.println(l3);
		System.out.println(l4);
		Flight f0 = new Flight(0,"Wednesday","6:00",l1,l2,200,5); //11
		Flight f3 = new Flight(1,"Wednesday","7:35",l1,l2,200,5);//11:57
		
		
		//l1.addDeparture(f1);
		//l1.addDeparture(f0);
		System.out.println(l1.hasRunwayDepartureSpace(f0));
		

		/*l1.addArrival(f3);
		l1.addArrival(f4);*/
		

	}
}

import java.util.*;
import java.io.*;
import java.lang.Math;
import java.text.SimpleDateFormat;
public class Flight {
    // flight ID, departure time, source and destination
    //locations, capacity, ticket price, number of passengers booked,
    private int flight_ID;
    private String day;
    private String time;
    private Location source_location;
    private Location destination_location;

    private int capacity;
    private int number_of_passengers_booked;
    private double ticket_price;
    //String date1, String date2, String start, String end, int capacity, int booked
    public Flight(){};
    public Flight(int flight_ID,String day,String time,Location start,Location end,int capacity,int number_of_passengers_booked){
        this.flight_ID = flight_ID;
        this.day = day;
        this.time = time;
        source_location = start;
        destination_location = end;
        this.capacity = capacity;
        this.number_of_passengers_booked = number_of_passengers_booked;
        ticket_price = getTicketPrice();

    }
    public int getId(){
        return flight_ID;
    }
    public String getsource(){
        return source_location.getname();
    }
    public String getdest(){
        return destination_location.getname();
    }
    public int getid(){
        return flight_ID;
    }
    public int getcapacity(){
        return capacity;
    }
    public int getbooked(){
        return number_of_passengers_booked;
    }
    public void setpassenger() {
        this.number_of_passengers_booked = 0;
    }
    public Date getarriveTime() {
        try{
            String str = this.day+" "+this.time;
            Date res = toTime(str);
            res =addMinute(res,getDuration());
            return res;
        } catch (Exception e){
            System.out.println("error");
        }
        return null;
    }
    public Date getdepartTime() {
        try{
            String str = this.day+" "+this.time;
            Date res = toTime(str);
            return res;
        } catch (Exception e){
            System.out.println("error");
        }
        return null;
    }
    //get the number of minutes this flight takes (rround to nearest whole number)
    public int getDuration() {
       double distance = getDistance();
       double speed = 720;
       double minutes = distance/speed*60;
       int result = (int)Math.round(minutes);
       return result;
    }

    //implement the ticket price formula
    public double getTicketPrice() {
        double x = (double)this.number_of_passengers_booked/this.capacity;
        double y = 1;
        double d = getDistance();
        if(x > 0 && x <= 0.5) {
            y = (-0.4)*x + 1;
        }
        else if(x > 0.5 && x <=0.7) {
            y = x + 0.3;
        }
        else if(x > 0.7 && x < 1) {
            y = 0.2/Math.PI*Math.atan(20*x-14) + 1;
        }
        double T = y * d/100*(30+4*(destination_location.getDemand()-source_location.getDemand()));
        return T;
    }

    //book the given number of passengers onto this flight, returning the total cost
    public double book(int num) {
        double cost = 0;
        if(capacity == number_of_passengers_booked){
            return -1;
        }
        if(num >= capacity - number_of_passengers_booked){
            num = capacity - number_of_passengers_booked;
        }
        while(num > 0){
            if(capacity == number_of_passengers_booked){
                System.out.println("Flight is now full.");
            }
            cost = cost+ticket_price;
            number_of_passengers_booked ++;
            ticket_price = getTicketPrice();
            num--;
        }
        return cost;
    }

    //return whether or not this flight is full
    public boolean isFull() {
        if(this.capacity == this.number_of_passengers_booked && this.capacity != 0) {
            return true;
        }
		return false;
	}

    //get the distance of this flight in km
    public double getDistance() {
        double distance = Location.distance(source_location,destination_location);
		return distance;
	}

    //get the layover time, in minutes, between two flights
    public static int layover(Flight x, Flight y) throws Exception{
        //SimpleDateFormat sdf=new SimpleDateFormat("EEEE HH:mm");
        String str1=x.day+" "+x.time;
        String str2=y.day+" "+y.time;
        Date dt1=toTime(str1);
        dt1=addMinute(dt1,x.getDuration());
        Date dt2=toTime(str2);
        int result = getMinutes(dt1,dt2);
        return result;
    }
    //time function
    public static Date toTime(String ds)throws Exception{
      String []result = ds.split(" ");
        if(result[0].equals("Sunday")) {
            SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/DD EEEE HH:mm");
            df.setLenient(false);
            Date d = df.parse("2007/01/07 "+ds);
            return d;
        }
        else{
            SimpleDateFormat df = new SimpleDateFormat("yyyy EEEE HH:mm");
            df.setLenient(false);
            Date d = df.parse("2007 "+ds);
            return d;
        }
    }
    public static String toString(Date time) {
        SimpleDateFormat sdf=new SimpleDateFormat("EEE HH:mm");
        String reStr = sdf.format(time);
        return reStr;
    }
     public static String toString1(Date time) {
        SimpleDateFormat sdf=new SimpleDateFormat("EEEE HH:mm");
        String reStr = sdf.format(time);
        return reStr;
    }
    public static int getMinutes(Date start,Date end){
        int result = (int)((end.getTime()-start.getTime())/(60*1000));
        if(result < 0) {
            return 10080+result;
        }
        return result;
    }
    public static Date addMinute(Date time,int minutes){
        Calendar rightNow = Calendar.getInstance();
        rightNow.setTime(time);
        rightNow.add(Calendar.MINUTE,minutes);
        Date restime = rightNow.getTime();
        return restime;
    }
    public static void main(String[] args) {
        
        try{
            Date dt1 = toTime("Sunday 23:00");
            Date dt2 = toTime("Tuesday 23:00");
            System.out.println(getMinutes(dt1,dt2));
            Location location1 = new Location("Sydney",-33.847927,150.651786,0.4);
            Location location2 = new Location("Perth",-32.0397559,115.681346,0.5);
            Location location3 = new Location("Townsville",-19.2967487,146.6151507,1);
            Flight f1 = new Flight(5,"Wednesday","10:00",location1,location2,200,5);
            System.out.println(f1.getDuration());
            Flight f2 = new Flight(5,"Wednesday","15:00",location2,location3,200,5);
            System.out.println(f2.getDuration());
            System.out.println(layover(f1,f2));
            System.out.println(toString(toTime("Wednesday 15:00")));
            System.out.println(f1.getdepartTime());
        } catch (Exception e) {
            System.out.println("error");
        }

    }
}

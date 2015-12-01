package maxbyers.capeventplanner;

import java.util.HashMap;

/**
 * Created by jeffmilling on 12/1/15.
 */
public class Event {
    private String title;
    private String date;
    private String location;
    private String description;
    private double price;
    private String user;
    private boolean display;
    private boolean complete;
    private HashMap<String, String> users;

    // Empty constructor for Firebase.
    public Event(){

    }

    public Event(String title, String date, String location,
                 String description, double price, boolean display, String user,
                 boolean complete, HashMap<String, String> users){
        this.title = title;
        this.date = date;
        this.description = description;
        this.location = location;
        this.price = price;
        this.display = display;
        this.user = user;
        this.complete = complete;
        this.users = users;
    }

    public String getTitle() {
        return title;
    }
    public String getDate() {
        return date;
    }
    public String getLocation() {
        return location;
    }
    public String getDescription() {
        return description;
    }
    public double getPrice() {
        return price;
    }
    public boolean getDisplay() {
        return display;
    }
    public String getUser() {
        return user;
    }
    public boolean getComplete() {
        return complete;
    }
    public HashMap<String, String> getUsers() {
        return users;
    }
}
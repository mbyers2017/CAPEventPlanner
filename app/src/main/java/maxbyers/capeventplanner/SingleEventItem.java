package maxbyers.capeventplanner;

/**
 * Created by jeffmilling on 11/12/15.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.text.DecimalFormat;
import java.util.HashMap;


public class SingleEventItem extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.single_event_item);
        this.getActionBar().setDisplayHomeAsUpEnabled(true);

        final TextView txtTitle = (TextView) findViewById(R.id.single_event_title);
        final TextView txtDate = (TextView) findViewById(R.id.single_event_date);
        final TextView txtDescription = (TextView) findViewById(R.id.single_event_description);
        final TextView txtPrice = (TextView) findViewById(R.id.single_event_price);
        final TextView txtLocation = (TextView) findViewById(R.id.single_event_location);



        Intent i = getIntent();
        // getting attached intent data
        final String title = i.getStringExtra("title");
        Firebase mainRef = new Firebase("https://cap-event-planner.firebaseio.com/");
        mainRef.setAndroidContext(getApplicationContext());
        Firebase ref = mainRef.child("events").child(title);
        Event event = null;
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Event event = snapshot.getValue(Event.class);
                txtTitle.setText(title);
                txtDate.setText(event.getDate());
                txtDescription.setText(event.getDescription());
                txtLocation.setText(event.getLocation());
                DecimalFormat decim = new DecimalFormat("0.00");
                String price = decim.format(event.getPrice());
                txtPrice.setText("$" + price);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return true;
    }
    public static class Event{
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
}

package maxbyers.capeventplanner;

/**
 * Created by jeffmilling on 11/12/15.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
        final TextView txtNumber = (TextView) findViewById(R.id.single_event_number);
        final Button rsvpButton = (Button) findViewById(R.id.rsvp_button);



        final Intent i = getIntent();
        // getting attached intent data
        final String title = i.getStringExtra("title");
        final String user = i.getStringExtra("user");
        Firebase mainRef = new Firebase("https://cap-event-planner.firebaseio.com/");
        mainRef.setAndroidContext(getApplicationContext());
        final Firebase ref = mainRef.child("events").child(title);
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
                if (event.getUsers() == null) {
                    txtNumber.setText("Number of students attending: 0");
                }
                else {
                    txtNumber.setText("Number of students attending: " + event.getUsers().size());
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });

        rsvpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user == null) {
                    Context context = getApplicationContext();
                    CharSequence text = "You must be logged in to sign up for this event!";
                    Toast.makeText(context, text, Toast.LENGTH_LONG).show();
                } else {
                    ref.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            if (snapshot.exists() && ((HashMap<String, String>) snapshot.getValue()).containsValue(user)) {
                                Context context = getApplicationContext();
                                CharSequence text = "You already signed up for this event!";
                                Toast.makeText(context, text, Toast.LENGTH_LONG).show();
                            } else {
                                snapshot.getRef().push().setValue(user, new Firebase.CompletionListener() {
                                    @Override
                                    public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                                        if (firebaseError == null) {
                                            firebase.getParent().addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot snapshot) {
                                                    txtNumber.setText("Number of students attending: " + ((HashMap<String, String>) snapshot.getValue()).size());
                                                    Context context = getApplicationContext();
                                                    CharSequence text = "You successfully signed up for this event!";
                                                    Toast.makeText(context, text, Toast.LENGTH_LONG).show();
                                                }

                                                @Override
                                                public void onCancelled(FirebaseError firebaseError) {

                                                }
                                            });
                                        }
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }
                    });
                }
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
}

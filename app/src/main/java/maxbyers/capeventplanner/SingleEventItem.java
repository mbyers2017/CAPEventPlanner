package maxbyers.capeventplanner;

/**
 * Created by jeffmilling on 11/12/15.
 */

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
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
import java.util.Iterator;


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
        final Button emailButton = (Button) findViewById(R.id.email_button);
        final Button deleteButton = (Button) findViewById(R.id.delete_button);



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
                if (user != null) {
                    if (event.getUser().equals(user)) {
                        emailButton.setVisibility(View.VISIBLE);
                    }
                    snapshot.getRef().getParent().getParent().child("users").child(user).child("authorized").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            if (snapshot.exists() && ((Boolean) snapshot.getValue())) {
                                emailButton.setVisibility(View.VISIBLE);
                                deleteButton.setVisibility(View.VISIBLE);
                            }
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }
                    });
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

        emailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ref.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (!snapshot.exists()) {
                            Context context = getApplicationContext();
                            CharSequence text = "This event currently has no attendees!";
                            Toast.makeText(context, text, Toast.LENGTH_LONG).show();
                        } else {
                            String emails = "";
                            Iterator<String> iter = ((HashMap<String, String>) snapshot.getValue()).values().iterator();
                            while (iter.hasNext()) {
                                emails += iter.next() + "@hmc.edu, ";
                            }
                            ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                            ClipData clip = ClipData.newPlainText("label", emails);
                            clipboard.setPrimaryClip(clip);
                            Context context = getApplicationContext();
                            CharSequence text = "All attendees' email addresses have been copied to the clipboard!";
                            Toast.makeText(context, text, Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });
            }
        });
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (!snapshot.exists()) {
                            Context context = getApplicationContext();
                            CharSequence text = "This event has already been deleted!";
                            Toast.makeText(context, text, Toast.LENGTH_LONG).show();
                            finish();
                        } else {
                            snapshot.getRef().removeValue();
                            Context context = getApplicationContext();
                            CharSequence text = "Event deleted successfully!";
                            Toast.makeText(context, text, Toast.LENGTH_LONG).show();
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });
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

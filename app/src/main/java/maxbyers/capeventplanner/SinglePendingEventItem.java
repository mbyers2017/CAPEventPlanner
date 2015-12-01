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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SinglePendingEventItem extends Activity {

    private static Pattern pattern1;

    private static Pattern pattern2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.single_pending_event_item);
        this.getActionBar().setDisplayHomeAsUpEnabled(true);

        pattern1 = Pattern.compile("\\$[0-9,\\.]+");
        pattern2 = Pattern.compile("[0-9,\\.]+");

        final TextView txtTitle = (TextView) findViewById(R.id.single_pending_event_title);
        final TextView txtDate = (TextView) findViewById(R.id.single_pending_event_date);
        final TextView txtDescription = (TextView) findViewById(R.id.single_pending_event_description);
        final TextView txtPrice = (TextView) findViewById(R.id.single_pending_event_price);
        final TextView txtLocation = (TextView) findViewById(R.id.single_pending_event_location);
        final Button denyButton = (Button) findViewById(R.id.deny_button);
        final Button approveButton = (Button) findViewById(R.id.approve_button);



        Intent i = getIntent();
        // getting attached intent data
        final String title = i.getStringExtra("title");
        final Firebase mainRef = new Firebase("https://cap-event-planner.firebaseio.com/");
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
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });

        denyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ref.child("date").setValue("01/01/2000");
                Context context = getApplicationContext();
                CharSequence text = "This event has been denied!";
                Toast.makeText(context, text, Toast.LENGTH_LONG).show();
                finish();
            }
        });

        approveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText pendingEventSubsidy = (EditText) findViewById(R.id.event_subsidy_edittext);
                String subsidy = pendingEventSubsidy.getText().toString();

                Matcher matcher1 = pattern1.matcher(subsidy);
                Matcher matcher2 = pattern2.matcher(subsidy);
                String match = null;
                if (matcher1.find()) {
                    match = matcher1.group();
                    match = match.replaceAll("\\$", "");
                    match = match.replaceAll(",", "");
                }
                else if (matcher2.find()) {
                    match = matcher2.group();
                    match = match.replaceAll("\\$", "");
                    match = match.replaceAll(",", "");
                }

                final String firebaseMatch = match;

                if (firebaseMatch == null) {
                    Context context = getApplicationContext();
                    CharSequence text = "Please enter a valid subsidy price!";
                    Toast.makeText(context, text, Toast.LENGTH_LONG).show();
                }
                else {
                    ref.child("display").setValue(true);
                    ref.child("price").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            snapshot.getRef().setValue(Double.parseDouble(snapshot.getValue()+"") - Double.parseDouble(firebaseMatch));
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }
                    });
                    Context context = getApplicationContext();
                    CharSequence text = "This event has been approved!";
                    Toast.makeText(context, text, Toast.LENGTH_LONG).show();
                    finish();
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

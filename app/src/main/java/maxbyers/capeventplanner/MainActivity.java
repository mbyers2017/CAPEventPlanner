package maxbyers.capeventplanner;

import android.app.Activity;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ListFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class MainActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    public static class ContextWrapper {

        private Context context;

        public ContextWrapper() { context = null; };

        public void setContext(Context context) { this.context = context; };

        public Context getContext() { return context; };

    }

    /**
     * Comment....
     */
    public static class FirebaseWrapper {

        private Firebase innerFirebaseRef;

        public FirebaseWrapper() {
            innerFirebaseRef = null;
        }

        public void setRef(String url) {
            innerFirebaseRef = new Firebase(url);
        }

        public Firebase getRef() {
            return innerFirebaseRef;
        }
    }

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    private static FirebaseWrapper myFirebaseWrapper = new FirebaseWrapper();

    private static ContextWrapper myContextWrapper = new ContextWrapper();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        Firebase.setAndroidContext(this);
        myFirebaseWrapper.setRef("https://cap-event-planner.firebaseio.com/");
        myContextWrapper.setContext(getApplicationContext());
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
            case 4:
                mTitle = getString(R.string.title_section4);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment implements View.OnClickListener {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Initialize all global variables.
         */
        ListView upcoming_events;
        ArrayAdapter<String> adapter;

        EditText create_event_name;
        EditText create_event_date;
        EditText create_event_description;
        EditText create_event_location;
        EditText create_event_price;

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            if (this.getArguments().getInt(ARG_SECTION_NUMBER) == 1) {
                final View rootView = inflater.inflate(R.layout.fragment_upcoming_events, container, false);
                upcoming_events = (ListView) rootView.findViewById(R.id.events_listview);

                Firebase eventsRef = myFirebaseWrapper.getRef().child("events");
                eventsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        ArrayList<String> eventTitles = new ArrayList<String>();
                        Toast.makeText(myContextWrapper.getContext(), "a", Toast.LENGTH_LONG).show();
                        HashMap<String, Object> events = (HashMap<String, Object>) snapshot.getValue();
                        Toast.makeText(myContextWrapper.getContext(), "events.size: " + events.size(), Toast.LENGTH_LONG).show();
                        Set<String> keys = events.keySet();
                        Toast.makeText(myContextWrapper.getContext(), "keys.size:" + keys.size(), Toast.LENGTH_LONG).show();
                        Iterator<String> iter = keys.iterator();
                        Toast.makeText(myContextWrapper.getContext(), "d", Toast.LENGTH_LONG).show();
                        while (iter.hasNext()) {
                            HashMap<String, Object> currentEvent = (HashMap<String, Object>) events.get(iter.next());
                            eventTitles.add((String) currentEvent.get("title"));
                        }
                        Toast.makeText(myContextWrapper.getContext(), eventTitles.get(0), Toast.LENGTH_LONG).show();
                        adapter = new ArrayAdapter<String>(myContextWrapper.getContext(), android.R.layout.simple_list_item_1, eventTitles);
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });

                upcoming_events.setAdapter(adapter);

                return rootView;
            } else if (this.getArguments().getInt(ARG_SECTION_NUMBER) == 2) {
                final View rootView = inflater.inflate(R.layout.fragment_approve_event, container, false);
                return rootView;
            } else if (this.getArguments().getInt(ARG_SECTION_NUMBER) == 3) {
                final View rootView = inflater.inflate(R.layout.fragment_create_event, container, false);

                // Button to delete an event from the database.
                Button cancelButton = (Button) rootView.findViewById(R.id.cancel_button);
                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Set global EditText variables to user inputs.
                        getEditTextFields(rootView);

                        String name = create_event_name.getText().toString();
                        // Ensure the name isn't null otherwise we delete all events...
                        if (name.equals("")){
                            Context context = myContextWrapper.getContext();
                            CharSequence text = "You must enter the name of the event you want to delete!";
                            Toast.makeText(context, text, Toast.LENGTH_LONG).show();
                        }
                        else {
                            // Create a reference to the event in Firebase.
                            Firebase eventRef = myFirebaseWrapper.getRef().child("events").child(name);
                            CharSequence noEvent = "There is no event with that name!";
                            CharSequence failure = "Your event was not successfully deleted! Please try again.";
                            CharSequence success = "Your event was successfully deleted!";
                            // Delete event info attached to that name.
                            removeValueAndShowToast(eventRef, success, failure, noEvent);
                        }

                        // Set all fields to empty after an event in progress is deleted.
                        setEditTextToEmpty();
                    }
                });

                // Button to save an event to the database.
                Button saveButton = (Button) rootView.findViewById(R.id.save_button);
                saveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Firebase messageRef = myFirebaseWrapper.getRef().child("message");

                        messageRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                //create_event_description.setText((String) snapshot.getValue());
                            }

                            @Override
                            public void onCancelled(FirebaseError firebaseError) {

                            }
                        });
                        // Set global EditText variables to user inputs.
                        getEditTextFields(rootView);

                        String name = create_event_name.getText().toString();
                        String date = create_event_date.getText().toString();
                        String location = create_event_location.getText().toString();
                        String description = create_event_description.getText().toString();
                        String price = create_event_price.getText().toString();

                        Event newEvent = new Event(name, date, location,
                                description, price, false, "NA", false);
                        // Ensure event has a name or otherwise the child doesn't exist!
                        if (name.equals("")){
                            Context context = myContextWrapper.getContext();
                            CharSequence text = "Your event must have a name!";
                            Toast.makeText(context, text, Toast.LENGTH_LONG).show();
                        }
                        else {
                            Firebase sampleRef = myFirebaseWrapper.getRef().child("events").child(name);
                            CharSequence failure = "Your event was not successfully saved! Please try again.";
                            CharSequence success = "Your event was successfully saved!";
                            setValueAndShowToast(sampleRef, newEvent, success, failure);
                        }
                    }
                });

                // Button to submit an event to the database so it can be  reviewed by CAP.
                Button submitButton = (Button) rootView.findViewById(R.id.submit_button);
                submitButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Set global EditText variables to user inputs.
                        getEditTextFields(rootView);

                        // Create variables to hold the strings entered into the text boxes.
                        String name = create_event_name.getText().toString();
                        String date = create_event_date.getText().toString();
                        String location = create_event_location.getText().toString();
                        String description = create_event_description.getText().toString();
                        String price = create_event_price.getText().toString();

                        Event newEvent = new Event(name, date, location,
                                description, price, false, "NA", true);

                        // Ensure event has a name or otherwise the child doesn't exist!
                        if (name.equals("")){
                            Context context = myContextWrapper.getContext();
                            CharSequence text = "Your event must have a name!";
                            Toast.makeText(context, text, Toast.LENGTH_LONG).show();
                        }
                        else { // Add event to database and inform user of success.
                            Firebase sampleRef = myFirebaseWrapper.getRef().child("events").child(name);
                            CharSequence success = "Your event was successfully created!";
                            CharSequence failure = "Your event was not successfully created! Please try again.";
                            setValueAndShowToast(sampleRef, newEvent, success, failure);
                        }
                    }
                });
                return rootView;
            } else if (this.getArguments().getInt(ARG_SECTION_NUMBER) == 4) {
                View rootView = inflater.inflate(R.layout.fragment_settings, container, false);
                return rootView;
            } else {
                View rootView = inflater.inflate(R.layout.fragment_main, container, false);
                return rootView;
            }
        }

        /**
         *This is a helper function to add a new event to the database at the given
         * reference and then show a toast explaining if the action was successful.
         *
         * @param ref Firebase reference.
         * @param newEvent Event we are adding to Firebase.
         * @param successMessage Message displayed if event is added successfully.
         * @param errorMessage Message displayed if event is not added.
         */
        public void setValueAndShowToast(Firebase ref, Event newEvent, final CharSequence successMessage,
                                         final CharSequence errorMessage) {
            ref.setValue(newEvent, new Firebase.CompletionListener() {
                @Override
                public void onComplete(FirebaseError firebaseeError, Firebase firebase) {
                    if (firebaseeError != null) {
                        Context context = myContextWrapper.getContext();
                        Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
                    } else {
                        Context context = myContextWrapper.getContext();
                        Toast.makeText(context, successMessage, Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

        /**
         * This is a helper function to remove an event from the database at the given reference
         * and then show a toast explaining if the deletion was successful.
         *
         * @param ref Firebase reference.
         * @param successMessage Message displayed if event is deleted successfully.
         * @param errorMessage Message displayed if event is not removed.
         * @param noEvent Message displayed if event doesn't exist.
         */
        public void removeValueAndShowToast(Firebase ref, final CharSequence successMessage,
                                         final CharSequence errorMessage, final CharSequence noEvent) {
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.getValue() == null) { // If no change, then the event didn't exist.
                        Context context = myContextWrapper.getContext();
                        Toast.makeText(context, noEvent, Toast.LENGTH_LONG).show();
                    } else {
                        // Event exists so we remove it.
                        snapshot.getRef().removeValue(new Firebase.CompletionListener() {
                            @Override
                            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                                if (firebaseError != null) {
                                    Context context = myContextWrapper.getContext();
                                    Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
                                } else {
                                    Context context = myContextWrapper.getContext();
                                    Toast.makeText(context, successMessage, Toast.LENGTH_LONG).show();
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

        /**
         * Helper function to set all EditText global variables to the
         * information currently in their corresponding fields on the form.
         * @param rootView The current view where the information is coming from.
         */
        public void getEditTextFields(View rootView) {
            create_event_name = (EditText) rootView.findViewById(R.id.event_name_edittext);
            create_event_date = (EditText) rootView.findViewById(R.id.event_date_edittext);
            create_event_location = (EditText) rootView.findViewById(R.id.event_location_edittext);
            create_event_description = (EditText) rootView.findViewById(R.id.event_description_edittext);
            create_event_price = (EditText) rootView.findViewById(R.id.event_price_edittext);
        }

        /**
         * Helper function to set the EditText variables to empty strings
         * which empties all of the text fields on the create event form.
         */
        public void setEditTextToEmpty() {
            create_event_name.setText("");
            create_event_date.setText("");
            create_event_location.setText("");
            create_event_description.setText("");
            create_event_price.setText("");
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }

        @Override
        public void onClick(View v) {

        }
    }

    public static class Event{
        private String title;
        private String date;
        private String location;
        private String description;
        private String price;
        private String user;
        private boolean display;
        private boolean complete;

        // Empty constructor for Firebase.
        public Event(){}

        public Event(String title, String date, String location,
                     String description, String price, boolean display,
                     String user, boolean complete){
            this.title = title;
            this.date = date;
            this.description = description;
            this.location = location;
            this.price = price;
            this.display = display;
            this.user = user;
            this.complete = complete;
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
        public String getPrice() {
            return price;
        }
        public boolean getDisplay() {
            return display;
        }
        public String getUser() {return user; }
        public boolean getComplete() {return complete; }
    }
}

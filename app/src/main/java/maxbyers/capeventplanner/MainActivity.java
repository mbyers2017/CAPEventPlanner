package maxbyers.capeventplanner;

import android.app.Activity;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.app.ProgressDialog;
import android.content.IntentSender;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.MutableData;
import com.firebase.client.ServerValue;
import com.firebase.client.Transaction;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    public static class ContextWrapper {

        private Context context;

        public ContextWrapper() {
            context = null;
        }

        public void setContext(Context context) {
            this.context = context;
        }

        public Context getContext() {
            return context;
        }

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

    public static class GoogleWrapper {

        private static final String TAG = MainActivity.class.getSimpleName();

        /* *************************************
         *              GENERAL                *
         ***************************************/
        /* TextView that is used to display information about the logged in user */
        private TextView mLoggedInStatusTextView;

        /* A dialog that is presented until the Firebase authentication finished. */
        private ProgressDialog mAuthProgressDialog;

        /* Data from the authenticated user */
        private AuthData mAuthData;

        /* Listener for Firebase session changes */
        private Firebase.AuthStateListener mAuthStateListener;

        /* *************************************
         *              GOOGLE                 *
         ***************************************/
        /* Request code used to invoke sign in user interactions for Google+ */
        public static final int RC_GOOGLE_LOGIN = 1;

        /* Client used to interact with Google APIs. */
        private GoogleApiClient mGoogleApiClient;

        /* A flag indicating that a PendingIntent is in progress and prevents us from starting further intents. */
        private boolean mGoogleIntentInProgress;

        /* Track whether the sign-in button has been clicked so that we know to resolve all issues preventing sign-in
         * without waiting. */
        private boolean mGoogleLoginClicked;

        /* Store the connection result from onConnectionFailed callbacks so that we can resolve them when the user clicks
         * sign-in. */
        private ConnectionResult mGoogleConnectionResult;

        /* The login button for Google */
        private SignInButton mGoogleLoginButton;

        private Button mGoogleLogoutButton;

        private View.OnClickListener loginClickListener;

        private View.OnClickListener logoutClickListener;

        private boolean loginScreenReached;

        private boolean approvalVisible;

        public GoogleWrapper() {
            mLoggedInStatusTextView = null;
            mAuthProgressDialog = null;
            mAuthData = null;
            mAuthStateListener = null;
            mGoogleApiClient = null;
            mGoogleIntentInProgress = false;
            mGoogleLoginClicked = false;
            mGoogleConnectionResult = null;
            mGoogleLoginButton = null;
            mGoogleLogoutButton = null;
            loginClickListener = null;
            logoutClickListener = null;
            loginScreenReached = false;
            approvalVisible = false;
        }

        public String getTag() {
            return TAG;
        }

        public int getRC() {
            return RC_GOOGLE_LOGIN;
        }

        public void setMLoggedInStatusTextView(TextView newValue) {
            mLoggedInStatusTextView = newValue;
        }

        public TextView getMLoggedInStatusTextView() {
            return mLoggedInStatusTextView;
        }

        public void setMAuthProgressDialog(ProgressDialog newValue) {
            mAuthProgressDialog = newValue;
        }

        public ProgressDialog getMAuthProgressDialog() {
            return mAuthProgressDialog;
        }

        public void setmAuthData(AuthData newValue) {
            mAuthData = newValue;
        }

        public AuthData getMAuthData() {
            return mAuthData;
        }

        public void setMAuthStateListener(Firebase.AuthStateListener newValue) {
            mAuthStateListener = newValue;
        }

        public Firebase.AuthStateListener getMAuthStateListener() {
            return mAuthStateListener;
        }

        public void setMGoogleApiClient(GoogleApiClient newValue) {
            mGoogleApiClient = newValue;
        }

        public GoogleApiClient getMGoogleApiClient() {
            return mGoogleApiClient;
        }

        public void setMGoogleIntentInProgress(boolean newValue) {
            mGoogleIntentInProgress = newValue;
        }

        public boolean getMGoogleIntentInProgress() {
            return mGoogleIntentInProgress;
        }

        public void setMGoogleLoginClicked(boolean newValue) {
            mGoogleLoginClicked = newValue;
        }

        public boolean getMGoogleLoginClicked() {
            return mGoogleLoginClicked;
        }

        public void setMGoogleConnectionResult(ConnectionResult newValue) {
            mGoogleConnectionResult = newValue;
        }

        public ConnectionResult getMGoogleConnectionResult() {
            return mGoogleConnectionResult;
        }

        public void setMGoogleLoginButton(SignInButton newValue) {
            mGoogleLoginButton = newValue;
        }

        public SignInButton getMGoogleLoginButton() {
            return mGoogleLoginButton;
        }

        public void setMGoogleLogoutButton(Button newValue) {
            mGoogleLogoutButton = newValue;
        }

        public Button getMGoogleLogoutButton() {
            return mGoogleLogoutButton;
        }

        public void setLoginClickListener(View.OnClickListener newValue) {
            loginClickListener = newValue;
        }

        public View.OnClickListener getLoginClickListener() {
            return loginClickListener;
        }

        public void setLogoutClickListener(View.OnClickListener newValue) {
            logoutClickListener = newValue;
        }

        public View.OnClickListener getLogoutClickListener() {
            return logoutClickListener;
        }

        public void setLoginScreenReached(boolean newValue) {
            loginScreenReached = newValue;
        }

        public boolean getLoginScreenReached() {
            return loginScreenReached;
        }

        public void setApprovalVisible(boolean newValue) {
            approvalVisible = newValue;
        }

        public boolean getApprovalVisible() {
            return approvalVisible;
        }
    }

    private static Pattern pattern1;

    private static Pattern pattern2;

    /**
     * Fragments managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    private static FirebaseWrapper myFirebaseWrapper = new FirebaseWrapper();

    private static ContextWrapper myContextWrapper = new ContextWrapper();

    private static GoogleWrapper myGoogleWrapper = new GoogleWrapper();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pattern1 = Pattern.compile("\\$[0-9,\\.]+");
        pattern2 = Pattern.compile("[0-9,\\.]+");

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

        /* Setup the Google API object to allow Google+ logins */
        myGoogleWrapper.setMGoogleApiClient(new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .build());

        if (!myGoogleWrapper.getMGoogleApiClient().isConnecting()) {
            myGoogleWrapper.getMGoogleApiClient().connect();
        }

        /* Setup the progress dialog that is displayed later when authenticating with Firebase */
        myGoogleWrapper.setMAuthProgressDialog(new ProgressDialog(this));
        myGoogleWrapper.getMAuthProgressDialog().setTitle("Loading");
        myGoogleWrapper.getMAuthProgressDialog().setMessage("Authenticating with Firebase...");
        myGoogleWrapper.getMAuthProgressDialog().setCancelable(false);
        myGoogleWrapper.getMAuthProgressDialog().show();

        myGoogleWrapper.setMAuthStateListener(new Firebase.AuthStateListener() {
            @Override
            public void onAuthStateChanged(AuthData authData) {
                myGoogleWrapper.getMAuthProgressDialog().hide();
                setAuthenticatedUser(authData);
            }
        });
        /* Check if the user is authenticated with Firebase already. If this is the case we can set the authenticated
         * user and hide hide any login buttons */
        myFirebaseWrapper.getRef().addAuthStateListener(myGoogleWrapper.getMAuthStateListener());

        myGoogleWrapper.setLoginClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myGoogleWrapper.setMGoogleLoginClicked(true);
                if (!myGoogleWrapper.getMGoogleApiClient().isConnecting()) {
                    if (myGoogleWrapper.getMGoogleConnectionResult() != null) {
                        resolveSignInError();
                    } else if (myGoogleWrapper.getMGoogleApiClient().isConnected()) {
                        getGoogleOAuthTokenAndLogin();
                    } else {
                    /* connect API now */
                        Log.d(myGoogleWrapper.getTag(), "Trying to connect to Google API");
                        myGoogleWrapper.getMGoogleApiClient().connect();
                    }
                }
            }
        });

        myGoogleWrapper.setLogoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // if changing configurations, stop tracking firebase session.
        myFirebaseWrapper.getRef().removeAuthStateListener(myGoogleWrapper.getMAuthStateListener());
    }

    /**
     * This method fires when any startActivityForResult finishes. The requestCode maps to
     * the value passed into startActivityForResult.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            myGoogleWrapper.setMGoogleLoginClicked(false);
        }
        myGoogleWrapper.setMGoogleIntentInProgress(false);
        if (!myGoogleWrapper.getMGoogleApiClient().isConnecting()) {
            myGoogleWrapper.getMGoogleApiClient().connect();
        }
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
        if (myGoogleWrapper.getApprovalVisible()) {
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
                case 5:
                    mTitle = getString(R.string.title_section5);
                    break;
            }
        }
        else {
            switch (number) {
                case 1:
                    mTitle = getString(R.string.title_section1);
                    break;
                case 2:
                    mTitle = getString(R.string.title_section3);
                    break;
                case 3:
                    mTitle = getString(R.string.title_section4);
                    break;
                case 4:
                    mTitle = getString(R.string.title_section5);
                    break;
            }
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
        /* If a user is currently authenticated, display a logout menu */
        if (myGoogleWrapper.getMAuthData() != null) {
            getMenuInflater().inflate(R.menu.main, menu);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            logout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /*
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
    */





    private void updateMarker() {
        myFirebaseWrapper.getRef().child("marker").runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData currentData) {
                if(currentData.getValue() == null) {
                    currentData.setValue(1);
                } else {
                    currentData.setValue((Long) currentData.getValue() + 1);
                }
                return Transaction.success(currentData); //we can also abort by calling Transaction.abort()

            }

            @Override
            public void onComplete(FirebaseError firebaseError, boolean b, DataSnapshot dataSnapshot) {

            }
        });
    }

    /**
     * Unauthenticate from Firebase and from providers where necessary.
     */
    private void logout() {
        if (myGoogleWrapper.getMAuthData() != null) {
            /* logout of Firebase */
            myFirebaseWrapper.getRef().unauth();
            /* Update authenticated user and show login buttons */
            setAuthenticatedUser(null);
            /* Logout of any of the Frameworks. This step is optional, but ensures the user is not logged into
             * Facebook/Google+ after logging out of Firebase. */
            /* Logout from Google+ */
            if (myGoogleWrapper.getMGoogleApiClient().isConnected()) {
                Plus.AccountApi.clearDefaultAccount(myGoogleWrapper.getMGoogleApiClient());
                myGoogleWrapper.getMGoogleApiClient().disconnect();
            }
        }
    }

    /**
     * This method will attempt to authenticate a user to firebase given an oauth_token (and other
     * necessary parameters depending on the provider)
     */
    private void authWithFirebase(final String provider, Map<String, String> options) {
        if (options.containsKey("error")) {
            showErrorDialog(options.get("error"));
        } else {
            myGoogleWrapper.getMAuthProgressDialog().show();
            myFirebaseWrapper.getRef().authWithOAuthToken(provider, options.get("oauth_token"), new AuthResultHandler(provider));
        }
    }

    /**
     * Once a user is logged in, take the mAuthData provided from Firebase and "use" it.
     */
    private void setAuthenticatedUser(AuthData authData) {
        if (myGoogleWrapper.getMGoogleApiClient().isConnected()) {
            if (authData != null) {
                final String email = Plus.AccountApi.getAccountName(myGoogleWrapper.getMGoogleApiClient());
                if (email.length() >= 10) {
                    final String emailName = email.substring(0, email.length()-10);
                    Firebase userAuthorizationRef = myFirebaseWrapper.getRef().child("users").child(emailName).child("authorized");
                    userAuthorizationRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                if ((boolean) snapshot.getValue()) {
                                    myGoogleWrapper.setApprovalVisible(true);
                                    mNavigationDrawerFragment.updateDrawer(true);
                                }
                            }
                            else {
                                snapshot.getRef().setValue(false);
                            }
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }
                    });
                }
            }
            else {
                myGoogleWrapper.setApprovalVisible(false);
                mNavigationDrawerFragment.updateDrawer(false);
            }
        }

        if (myGoogleWrapper.getLoginScreenReached()) {
            if (authData != null) {
                /* Hide all the login buttons */
                myGoogleWrapper.getMGoogleLoginButton().setVisibility(View.GONE);
                myGoogleWrapper.getMGoogleLogoutButton().setVisibility(View.VISIBLE);
                myGoogleWrapper.getMLoggedInStatusTextView().setVisibility(View.VISIBLE);
                /* show a provider specific status text */
                String name = null;
                if (authData.getProvider().equals("google")) {
                    name = (String) authData.getProviderData().get("displayName");
                } else {
                    Log.e(myGoogleWrapper.getTag(), "Invalid provider: " + authData.getProvider());
                }
                if (name != null) {
                    myGoogleWrapper.getMLoggedInStatusTextView().setText("Logged in as " + name);
                }
            } else {
                /* No authenticated user show all the login buttons */
                myGoogleWrapper.getMGoogleLoginButton().setVisibility(View.VISIBLE);
                myGoogleWrapper.getMGoogleLogoutButton().setVisibility(View.GONE);
                myGoogleWrapper.getMLoggedInStatusTextView().setVisibility(View.GONE);
            }
        }

        myGoogleWrapper.setmAuthData(authData);
        /* invalidate options menu to hide/show the logout button */
        ActivityCompat.invalidateOptionsMenu(this);
    }

    /**
     * Show errors to users
     */
    private void showErrorDialog(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    /**
     * Utility class for authentication results
     */
    private class AuthResultHandler implements Firebase.AuthResultHandler {

        private final String provider;

        public AuthResultHandler(String provider) {
            this.provider = provider;
        }

        @Override
        public void onAuthenticated(AuthData authData) {
            myGoogleWrapper.getMAuthProgressDialog().hide();
            Log.i(myGoogleWrapper.getTag(), provider + " auth successful");
            setAuthenticatedUser(authData);
        }

        @Override
        public void onAuthenticationError(FirebaseError firebaseError) {
            myGoogleWrapper.getMAuthProgressDialog().hide();
            showErrorDialog(firebaseError.toString());
        }
    }

    /* A helper method to resolve the current ConnectionResult error. */
    private void resolveSignInError() {
        if (myGoogleWrapper.getMGoogleConnectionResult().hasResolution()) {
            try {
                myGoogleWrapper.setMGoogleIntentInProgress(true);
                myGoogleWrapper.getMGoogleConnectionResult().startResolutionForResult(this, myGoogleWrapper.getRC());
            } catch (IntentSender.SendIntentException e) {
                // The intent was canceled before it was sent.  Return to the default
                // state and attempt to connect to get an updated ConnectionResult.
                myGoogleWrapper.setMGoogleIntentInProgress(false);
                myGoogleWrapper.getMGoogleApiClient().connect();
            }
        }
    }

    private void getGoogleOAuthTokenAndLogin() {
        myGoogleWrapper.getMAuthProgressDialog().show();
        /* Get OAuth token in Background */
        AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
            String errorMessage = null;

            @Override
            protected String doInBackground(Void... params) {
                String token = null;

                try {
                    String email = Plus.AccountApi.getAccountName(myGoogleWrapper.getMGoogleApiClient());
                    if (email == null || email.length() < 7 || !email.substring(email.length()-7).equals("hmc.edu")) {
                        token = "nonHMCemail";
                    }
                    else {
                        String scope = String.format("oauth2:%s", Scopes.PLUS_LOGIN);
                        token = GoogleAuthUtil.getToken(MainActivity.this, Plus.AccountApi.getAccountName(myGoogleWrapper.getMGoogleApiClient()), scope);
                    }
                } catch (IOException transientEx) {
                    /* Network or server error */
                    Log.e(myGoogleWrapper.getTag(), "Error authenticating with Google: " + transientEx);
                    errorMessage = "Network error: " + transientEx.getMessage();
                } catch (UserRecoverableAuthException e) {
                    Log.w(myGoogleWrapper.getTag(), "Recoverable Google OAuth error: " + e.toString());
                    /* We probably need to ask for permissions, so start the intent if there is none pending */
                    if (!myGoogleWrapper.getMGoogleIntentInProgress()) {
                        myGoogleWrapper.setMGoogleIntentInProgress(true);
                        Intent recover = e.getIntent();
                        startActivityForResult(recover, myGoogleWrapper.getRC());
                    }
                } catch (GoogleAuthException authEx) {
                    /* The call is not ever expected to succeed assuming you have already verified that
                     * Google Play services is installed. */
                    Log.e(myGoogleWrapper.getTag(), "Error authenticating with Google: " + authEx.getMessage(), authEx);
                    errorMessage = "Error authenticating with Google: " + authEx.getMessage();
                }
                return token;
            }

            @Override
            protected void onPostExecute(String token) {
                myGoogleWrapper.setMGoogleLoginClicked(false);
                if (token != null) {
                    if (token.equals("nonHMCemail")) {
                        myGoogleWrapper.getMAuthProgressDialog().hide();
                        /* Logout from Google+ */
                        if (myGoogleWrapper.getMGoogleApiClient().isConnected()) {
                            Plus.AccountApi.clearDefaultAccount(myGoogleWrapper.getMGoogleApiClient());
                            myGoogleWrapper.getMGoogleApiClient().disconnect();
                        }
                        /* Update authenticated user and show login buttons */
                        setAuthenticatedUser(null);
                        Context context = myContextWrapper.getContext();
                        CharSequence text = "You must use a valid hmc.edu account!";
                        Toast.makeText(context, text, Toast.LENGTH_LONG).show();
                    }
                    else {
                        /* Successfully got OAuth token, now login with Google */
                        myFirebaseWrapper.getRef().authWithOAuthToken("google", token, new AuthResultHandler("google"));
                    }
                } else if (errorMessage != null) {
                    myGoogleWrapper.getMAuthProgressDialog().hide();
                    showErrorDialog(errorMessage);
                    /* Logout from Google+ */
                    if (myGoogleWrapper.getMGoogleApiClient().isConnected()) {
                        Plus.AccountApi.clearDefaultAccount(myGoogleWrapper.getMGoogleApiClient());
                        myGoogleWrapper.getMGoogleApiClient().disconnect();
                    }
                    /* Update authenticated user and show login buttons */
                    setAuthenticatedUser(null);
                }
            }
        };
        task.execute();
    }

    @Override
    public void onConnected(final Bundle bundle) {
        /* Connected with Google API, use this to authenticate with Firebase */
        getGoogleOAuthTokenAndLogin();
    }


    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (!myGoogleWrapper.getMGoogleIntentInProgress()) {
            /* Store the ConnectionResult so that we can use it later when the user clicks on the Google+ login button */
            myGoogleWrapper.setMGoogleConnectionResult(result);

            if (myGoogleWrapper.getMGoogleLoginClicked()) {
                /* The user has already clicked login so we attempt to resolve all errors until the user is signed in,
                 * or they cancel. */
                resolveSignInError();
            } else {
                Log.e(myGoogleWrapper.getTag(), result.toString());
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        // ignore
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
        ArrayAdapter<Event> adapter;

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
                // upcoming event viewer
                final View rootView = inflater.inflate(R.layout.fragment_upcoming_events, container, false);
                upcoming_events = (ListView) rootView.findViewById(R.id.events_listview);

                final Firebase timeRef = myFirebaseWrapper.getRef().child("time");
                timeRef.setValue(ServerValue.TIMESTAMP);
                timeRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        Date date = new Date((Long) snapshot.getValue());
                        DateFormat format = new SimpleDateFormat("MM/dd/yyyy");
                        format.setTimeZone(TimeZone.getTimeZone("Etc/GMT+12"));
                        final String formattedDate = format.format(date);

                        snapshot.getRef().getParent().child("events").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                ArrayList<Event> events = new ArrayList<Event>();
                                for (DataSnapshot child : snapshot.getChildren()) {
                                    Event event = child.getValue(Event.class);
                                    if (/*event.getComplete() && event.getDisplay() && */dateToInt(event.getDate()) >= dateToInt(formattedDate)) {
                                        events.add(event);
                                    }
                                }

                                events = sort(events, "date");

                                adapter = new EventsAdapter(myContextWrapper.getContext(), events);
                                upcoming_events.setAdapter(adapter);
                            }

                            @Override
                            public void onCancelled(FirebaseError firebaseError) {

                            }
                        });
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });



                // listening to single list item on click
                upcoming_events.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {

                        // selected item
                        String eventTitle = (String) adapter.getItem(position).getTitle();


                        //String event = ((TextView) view).getText().toString();

                        // Launching new Activity on selecting single List Item
                        Intent i = new Intent(myContextWrapper.getContext(), SingleEventItem.class);
                        // sending data to new activity
                        i.putExtra("title", eventTitle);
                        startActivity(i);
                    }
                });

                return rootView;
            } else if (this.getArguments().getInt(ARG_SECTION_NUMBER) == 2 && myGoogleWrapper.getApprovalVisible()) {
                final View rootView = inflater.inflate(R.layout.fragment_approve_event, container, false);

                return rootView;
            } else if (this.getArguments().getInt(ARG_SECTION_NUMBER) == 3 && myGoogleWrapper.getApprovalVisible() ||
                    this.getArguments().getInt(ARG_SECTION_NUMBER) == 2 && !myGoogleWrapper.getApprovalVisible()) {
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
                                description, Double.parseDouble(price), false, "NA", false, null);
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
                        final String name = create_event_name.getText().toString();
                        final String date = create_event_date.getText().toString();
                        final String location = create_event_location.getText().toString();
                        final String description = create_event_description.getText().toString();
                        final String price = create_event_price.getText().toString();
                        String user = null;

                        if (myGoogleWrapper.getMAuthData() != null) {
                            final String email = Plus.AccountApi.getAccountName(myGoogleWrapper.getMGoogleApiClient());
                            if (email.length() >= 10) {
                                user = email.substring(0, email.length()-10);
                            }
                        }

                        final String firebaseUser = user;

                        Matcher matcher1 = pattern1.matcher(price);
                        Matcher matcher2 = pattern2.matcher(price);
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

                        // Ensure event has a name or otherwise the child doesn't exist!
                        if (name.equals("")){
                            Context context = myContextWrapper.getContext();
                            CharSequence text = "Your event must have a name!";
                            Toast.makeText(context, text, Toast.LENGTH_LONG).show();
                        }
                        else if (!name.matches("^[a-zA-Z0-9 ]*$")) {
                            Context context = myContextWrapper.getContext();
                            CharSequence text = "Your event name must contain only alphanumeric characters!";
                            Toast.makeText(context, text, Toast.LENGTH_LONG).show();
                        }
                        else {
                            myFirebaseWrapper.getRef().child("events").child(name).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        Context context = myContextWrapper.getContext();
                                        CharSequence text = "An event with that name already exists!";
                                        Toast.makeText(context, text, Toast.LENGTH_LONG).show();
                                    } else {
                                        if (!date.matches("^(1[0-2]|0[1-9])/(3[01]|[12][0-9]|0[1-9])/(19|20)[0-9]{2}$")) {
                                            Context context = myContextWrapper.getContext();
                                            CharSequence text = "Please enter a valid date in the correct format!";
                                            Toast.makeText(context, text, Toast.LENGTH_LONG).show();
                                        }
                                        else if (location.equals("")){
                                            Context context = myContextWrapper.getContext();
                                            CharSequence text = "Your event must have a location!";
                                            Toast.makeText(context, text, Toast.LENGTH_LONG).show();
                                        }
                                        else if (description.equals("")){
                                            Context context = myContextWrapper.getContext();
                                            CharSequence text = "Your event must have a description!";
                                            Toast.makeText(context, text, Toast.LENGTH_LONG).show();
                                        }
                                        else if (firebaseMatch == null) {
                                            Context context = myContextWrapper.getContext();
                                            CharSequence text = "Please enter a valid price!";
                                            Toast.makeText(context, text, Toast.LENGTH_LONG).show();
                                        }
                                        else if (firebaseUser == null) {
                                            Context context = myContextWrapper.getContext();
                                            CharSequence text = "You must be logged in to create an event!";
                                            Toast.makeText(context, text, Toast.LENGTH_LONG).show();
                                        }
                                        else { // Add event to database and inform user of success.
                                            Event newEvent = new Event(name, date, location, description,
                                                    Double.parseDouble(firebaseMatch), false, firebaseUser, true, null);
                                            CharSequence success = "Your event was successfully created!";
                                            CharSequence failure = "Your event was not successfully created! Please try again.";
                                            setValueAndShowToast(snapshot.getRef(), newEvent, success, failure);
                                            snapshot.getRef().getParent().getParent().child("users").child(firebaseUser).child("events").push().setValue(name);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(FirebaseError firebaseError) {

                                }
                            });
                        }
                    }
                });
                return rootView;
            } else if (this.getArguments().getInt(ARG_SECTION_NUMBER) == 4 && myGoogleWrapper.getApprovalVisible() ||
                    this.getArguments().getInt(ARG_SECTION_NUMBER) == 3 && !myGoogleWrapper.getApprovalVisible()) {
                final View rootView = inflater.inflate(R.layout.fragment_account, container, false);

                myGoogleWrapper.setLoginScreenReached(true);

                /* Load the Google login button */
                myGoogleWrapper.setMGoogleLoginButton((SignInButton) rootView.findViewById(R.id.login_button));
                myGoogleWrapper.getMGoogleLoginButton().setOnClickListener(myGoogleWrapper.getLoginClickListener());
                myGoogleWrapper.setMGoogleLogoutButton((Button) rootView.findViewById(R.id.logout_button));
                myGoogleWrapper.getMGoogleLogoutButton().setOnClickListener(myGoogleWrapper.getLogoutClickListener());
                myGoogleWrapper.setMLoggedInStatusTextView((TextView) rootView.findViewById(R.id.login_status));

                if (myGoogleWrapper.getMAuthData() != null) {
                    String name = (String) myGoogleWrapper.getMAuthData().getProviderData().get("displayName");
                    myGoogleWrapper.getMLoggedInStatusTextView().setText("Logged in as " + name);
                    myGoogleWrapper.getMGoogleLoginButton().setVisibility(View.GONE);
                    myGoogleWrapper.getMGoogleLogoutButton().setVisibility(View.VISIBLE);
                    myGoogleWrapper.getMLoggedInStatusTextView().setVisibility(View.VISIBLE);
                }

                return rootView;
            } else if (this.getArguments().getInt(ARG_SECTION_NUMBER) == 5 && myGoogleWrapper.getApprovalVisible() ||
                    this.getArguments().getInt(ARG_SECTION_NUMBER) == 4 && !myGoogleWrapper.getApprovalVisible()) {
                final View rootView = inflater.inflate(R.layout.fragment_settings, container, false);
                return rootView;
            } else {
                final View rootView = inflater.inflate(R.layout.fragment_main, container, false);
                return rootView;
            }
        }

        public ArrayList<Event> sort(ArrayList<Event> original, String criterion) {
            int size = original.size();
            if (size <= 1) {
                return original;
            }
            ArrayList<Event> first = new ArrayList<Event>();
            ArrayList<Event> second = new ArrayList<Event>();
            for (int i = 0; i < size/2; i++) {
                first.add(original.get(i));
            }
            for (int i = size/2; i < size; i++) {
                second.add(original.get(i));
            }
            return merge(sort(first, criterion), sort(second, criterion), criterion);
        }

        public ArrayList<Event> merge(ArrayList<Event> first, ArrayList<Event> second, String criterion) {
            ArrayList<Event> result = new ArrayList<Event>();
            int i = 0;
            int j = 0;
            while (i < first.size() && j < second.size()) {
                if (criterion.equals("date") && dateToInt(first.get(i).getDate()) < dateToInt(second.get(j).getDate())) {
                    result.add(first.get(i));
                    i++;
                }
                else {
                    result.add(second.get(j));
                    j++;
                }
            }
            while (i < first.size()) {
                result.add(first.get(i));
                i++;
            }
            while (j < second.size()) {
                result.add(second.get(j));
                j++;
            }
            return result;
        }

        public int dateToInt(String date) {
            return Integer.parseInt(date.substring(3,5))+32*Integer.parseInt(date.substring(0,2))+32*15*Integer.parseInt(date.substring(6));
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
                    if (!snapshot.exists()) { // If no change, then the event didn't exist.
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

    /**
     * Declared to recycle views.
     * "View lookup cache"
     */
    private static class ViewHolder {
        TextView name;
        TextView date;
        TextView location;
    }

    /**
     * A custom adapter class which allows us to take data
     * from the events class and put it into a list view.
     *
     * Details: This class recycles views to save memory and
     * reduce calls to findViewById.
     */
    public static class EventsAdapter extends ArrayAdapter<Event> {

        public EventsAdapter(Context context, ArrayList<Event> events) {
            super(context, R.layout.item_event, events);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            Event event = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            ViewHolder viewHolder; // view lookup cache stored in tag
            if (convertView == null) {
                viewHolder = new ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.item_event, parent, false);
                // Define all parameters to be dsiplayed here.
                viewHolder.name = (TextView) convertView.findViewById(R.id.eventName);
                viewHolder.date = (TextView) convertView.findViewById(R.id.eventDate);
                viewHolder.location = (TextView) convertView.findViewById(R.id.eventLocation);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            if (position % 2 == 1) {
                convertView.setBackgroundColor(Color.LTGRAY);
            } else {
                convertView.setBackgroundColor(Color.WHITE);
            }
            // Populate the data into the template view using the data object
            viewHolder.name.setText(event.getTitle());
            viewHolder.date.setText(event.getDate());
            viewHolder.location.setText(event.getLocation());
            // Return the completed view to render on screen
            return convertView;
        }
    }
}

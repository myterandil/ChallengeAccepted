package gaddet_bazaar.challengeaccepted;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;


public class Challenge extends Activity {
    private String KEY_TITLE;
    private String KEY_DIFFICULTY;
    private String KEY_WHAT;
    private ParseObject lastChallange;
    private String KEY_USER;
    private String KEY_I_TITLE;
    private String KEY_I_DIFFICULTY;
    private String KEY_I_WHAT;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge);

        final Button backToList = (Button) findViewById(R.id.backToList);
        final Button replaceChallenge = (Button) findViewById(R.id.replaceChallenge);
        final Button backToMyChallenge = (Button) findViewById(R.id.backToMyChallenge);
        backToList.setVisibility(View.INVISIBLE);
        replaceChallenge.setVisibility(View.INVISIBLE);
        backToMyChallenge.setVisibility(View.INVISIBLE);
        final TextView textViev = (TextView) findViewById(R.id.t1);
        final TextView textViev1 = (TextView) findViewById(R.id.t2);
        final TextView textViev2 = (TextView) findViewById(R.id.t3);
        Parse.initialize(this, "wjJOQEbz9NoeeF92YncLPcOCfLwlWFq8sipVnv4m", "adi8YGFIWSMKhJ3KpOOoA2dt4qpBPLMdNjfN1m0f");


        ParseUser currentUser = ParseUser.getCurrentUser();


        if (currentUser == null) {
            navigateToLogin();

        } else {
            final Intent intent = getIntent();
            KEY_USER = currentUser.getUsername();
            ParseQuery<ParseObject> query = ParseQuery.getQuery("lastChallange");
            query.whereEqualTo("username", KEY_USER);
            query.getFirstInBackground(new GetCallback<ParseObject>() {
                public void done(final ParseObject object, ParseException e) {
                    if (object != null) {

                        KEY_TITLE = object.getString("title");
                        KEY_DIFFICULTY = object.getString("difficulty");
                        KEY_WHAT = object.getString("what");
                        KEY_I_TITLE = intent.getStringExtra("title");
                        KEY_I_DIFFICULTY = intent.getStringExtra("difficulty");
                        KEY_I_WHAT = intent.getStringExtra("what");

                        if (KEY_I_TITLE != null) {
                            textViev.setText(KEY_I_TITLE);
                            textViev1.setText(KEY_I_DIFFICULTY);
                            textViev2.setText(KEY_I_WHAT);
                            backToList.setVisibility(View.VISIBLE);
                            View.OnClickListener listener1 = new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    navigateToList();
                                }
                            };
                            backToList.setOnClickListener(listener1);
                            replaceChallenge.setVisibility(View.VISIBLE);
                            View.OnClickListener listener2 = new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    fromlisttokeyset(intent);
                                    ParseQuery<ParseObject> query = ParseQuery.getQuery("lastChallange");
                                    query.whereEqualTo("username", KEY_USER);
                                    query.getFirstInBackground(new GetCallback<ParseObject>() {
                                                                   public void done(final ParseObject object, ParseException e) {
                                                                       if (object != null) {
                                                                           object.deleteInBackground();
                                                                           dataSaver();
                                                                           recreate();
                                                                       } else {
                                                                           Toast.makeText(Challenge.this, "There is a problem here!", Toast.LENGTH_LONG).show();
                                                                       }


                                                                   }
                                                               }
                                    );

                                }
                            };
                            replaceChallenge.setOnClickListener(listener2);
                            backToMyChallenge.setVisibility(View.VISIBLE);
                            View.OnClickListener listener3 = new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    recreate();

                                }
                            };
                            backToMyChallenge.setOnClickListener(listener3);


                        } else {

                            textViev.setText(KEY_TITLE);
                            textViev1.setText(KEY_DIFFICULTY);
                            textViev2.setText(KEY_WHAT);
                        }

                    } else {
                        fromlisttokeyset(intent);

                        if (KEY_TITLE != null) {


                            textViev.setText(KEY_TITLE);
                            textViev1.setText(KEY_DIFFICULTY);
                            textViev2.setText(KEY_WHAT);
                            if (KEY_TITLE != null) {
                                dataSaver();
                            } else {
                                Toast.makeText(Challenge.this, "There is a problem here!", Toast.LENGTH_LONG).show();
                            }

                        } else {
                            navigateToList();

                        }
                    }
                }
            });


        }
    }

    private void fromlisttokeyset(Intent intent) {
        KEY_TITLE = intent.getStringExtra("title");
        KEY_DIFFICULTY = intent.getStringExtra("difficulty");
        KEY_WHAT = intent.getStringExtra("what");
    }


    // action bar ve options menüsü
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.challenge, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.logout) {
            ParseUser.logOut();
            navigateToLogin();
        }
        if (itemId == R.id.give_up) {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("lastChallange");
            query.whereEqualTo("username", KEY_USER);
            query.getFirstInBackground(new GetCallback<ParseObject>() {
                                           public void done(final ParseObject object, ParseException e) {
                                               if (object != null) {
                                                   object.deleteInBackground();
                                                   navigateToList();
                                               } else {
                                                   Toast.makeText(Challenge.this, "There is a problem here!", Toast.LENGTH_LONG).show();
                                               }


                                           }
                                       }
            );
        }
        if (itemId == R.id.list) {
            navigateToList();
        }
        return super.onOptionsItemSelected(item);
    }


    // methodlar
    //Parse.com a veri saklama
    private void dataSaver() {
        lastChallange = new ParseObject("lastChallange");
        lastChallange.put("title", KEY_TITLE);
        lastChallange.put("difficulty", KEY_DIFFICULTY);
        lastChallange.put("what", KEY_WHAT);
        lastChallange.put("username", KEY_USER);
        lastChallange.saveInBackground();
    }

    //listeye yönlendirme
    private void navigateToList() {
        Intent i = new Intent(this, ChallengeListActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

    //login ekranına yönlendirme
    private void navigateToLogin() {
        Intent intent = new Intent(Challenge.this, Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    // restart activity
    public void recreate() {
        Intent intent = new Intent(Challenge.this, MyActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);


    }

    //life cycle listesi.

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }
}

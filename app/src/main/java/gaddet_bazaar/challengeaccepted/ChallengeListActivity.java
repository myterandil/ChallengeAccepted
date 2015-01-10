package gaddet_bazaar.challengeaccepted;


import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.Parse;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class ChallengeListActivity extends ListActivity {
    public static final String TAG = ChallengeListActivity.class.getSimpleName();
    private final String KEY_TITLE = "title";
    private final String KEY_DIFFICULTY = "difficulty";
    protected JSONObject mJSONObject;
    protected ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge_list);
        Parse.initialize(this, "wjJOQEbz9NoeeF92YncLPcOCfLwlWFq8sipVnv4m", "adi8YGFIWSMKhJ3KpOOoA2dt4qpBPLMdNjfN1m0f");

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        if (isNetworkAvailable()) {
            mProgressBar.setVisibility(View.VISIBLE);
            GetChallenges getChallenges = new GetChallenges();
            getChallenges.execute();
        } else {
            Toast.makeText(this, "No network", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        try {
            JSONArray challenges = mJSONObject.getJSONArray("challenges");
            JSONObject challenge = challenges.getJSONObject(position);
            String title = challenge.getString("title");
            title = Html.fromHtml(title).toString();
            String difficulty = challenge.getString("difficulty");
            difficulty = Html.fromHtml(difficulty).toString();
            String what = challenge.getString("what");
            what = Html.fromHtml(what).toString();

            Intent intent = new Intent(this, Challenge.class);
            intent.putExtra("title", title);
            intent.putExtra("difficulty", difficulty);
            intent.putExtra("what", what);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);


        } catch (JSONException e) {
            Log.e(TAG, "hata var", e);
        }

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager menager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = menager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        }
        return isAvailable;
    }


    private void netveriduzenleyici() {
        mProgressBar.setVisibility(View.INVISIBLE);
        if (mJSONObject == null) {
            hatagosterici();

        } else {
            try {
                JSONArray challenges = mJSONObject.getJSONArray("challenges");
                ArrayList<HashMap<String, String>> challengeType = new ArrayList<HashMap<String, String>>();

                for (int i = 0; i < challenges.length(); i++) {
                    JSONObject title = challenges.getJSONObject(i);
                    String titles = title.getString(KEY_TITLE);
                    titles = Html.fromHtml(titles).toString();
                    String difficulty = title.getString(KEY_DIFFICULTY);
                    difficulty = Html.fromHtml(difficulty).toString();

                    HashMap<String, String> challenge = new HashMap<String, String>();
                    challenge.put(KEY_TITLE, titles);
                    challenge.put(KEY_DIFFICULTY, difficulty);

                    challengeType.add(challenge);


                }
                String[] keys = {KEY_TITLE, KEY_DIFFICULTY};
                int[] ids = {android.R.id.text1, android.R.id.text2};
                SimpleAdapter adapter = new SimpleAdapter(this, challengeType, android.R.layout.simple_list_item_2, keys, ids);
                setListAdapter(adapter);

            } catch (JSONException e) {
                Log.e(TAG, "hata var", e);
            }
        }

    }

    private void hatagosterici() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.title));
        builder.setMessage(getString(R.string.error));
        builder.setPositiveButton(android.R.string.ok, null);
        AlertDialog dialog = builder.create();
        dialog.show();
        TextView emptyTextView = (TextView) getListView().getEmptyView();
        emptyTextView.setText(getString(R.string.no_item));
    }

    private void navigateToLogin() {
        Intent intent = new Intent(this, Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

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
        return super.onOptionsItemSelected(item);
    }

    private class GetChallenges extends AsyncTask<Object, Void, JSONObject> {
        String urlstring = getString(R.string.base);
        int responseCode = -1;
        JSONObject jsonResponse = null;

        @Override
        protected JSONObject doInBackground(Object... arg0) {
            try {
                URL challengeListUrl = new URL(urlstring);
                HttpURLConnection connection = (HttpURLConnection) challengeListUrl.openConnection();
                connection.connect();
                responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = connection.getInputStream();
                    Reader reader = new InputStreamReader(inputStream);
                    int nextCharacter; // read() returns an int, we cast it to char later
                    String responseData = "";
                    while (true) { // Infinite loop, can only be stopped by a "break" statement
                        nextCharacter = reader.read(); // read() without parameters returns one character
                        if (nextCharacter == -1) // A return value of -1 means that we reached the end
                            break;
                        responseData += (char) nextCharacter; // The += operator appends the character to the end of the string
                    }

                    jsonResponse = new JSONObject(responseData);
                } else {
                    Log.i(TAG, "yanlis kod: " + responseCode);
                }

            } catch (MalformedURLException e) {
                Log.e(TAG, "Hata VAR!!", e);
            } catch (IOException e) {
                Log.e(TAG, "Hata VAR!!", e);
            } catch (Exception e) {
                Log.e(TAG, "Hata VAR!!", e);
            }


            return jsonResponse;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            mJSONObject = result;
            netveriduzenleyici();
        }
    }
}




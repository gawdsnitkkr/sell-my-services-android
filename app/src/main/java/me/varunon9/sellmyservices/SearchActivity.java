package me.varunon9.sellmyservices;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static me.varunon9.sellmyservices.constants.AppConstants.Urls;

import me.varunon9.sellmyservices.constants.AppConstants;
import me.varunon9.sellmyservices.db.DbHelper;
import me.varunon9.sellmyservices.db.models.SearchHistory;
import me.varunon9.sellmyservices.db.services.SearchHistoryService;
import me.varunon9.sellmyservices.utils.AjaxCallback;
import me.varunon9.sellmyservices.utils.AjaxUtility;
import me.varunon9.sellmyservices.utils.ContextUtility;

public class SearchActivity extends AppCompatActivity {

    DbHelper dbHelper;
    SearchHistoryService searchHistoryService;
    ContextUtility contextUtility;
    AjaxUtility ajaxUtility;
    Singleton singleton;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // declare all local variables here
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        final EditText searchServicesEditText =
                (EditText) findViewById(R.id.searchServicesEditText);

        setSupportActionBar(toolbar);

        // display back button in action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dbHelper = new DbHelper(this);
        searchHistoryService = new SearchHistoryService(dbHelper);
        contextUtility = new ContextUtility(this);
        ajaxUtility = new AjaxUtility(getApplicationContext());
        singleton = Singleton.getInstance(getApplicationContext());

        searchServicesEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int actionId, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        && keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    String searchText = searchServicesEditText.getText().toString();
                    searchServices(searchText, false);
                    return true;
                }
                return false;
            }
        });

        /**
         * asynchronously populate search History list
         * Android warning of `memory leak` can be ignored because `getRecentSearchHistories`
         * db query is talking roughly 30 ms
         * in fact even without async call, performance impact is not visible
         */
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                populateSearchHistoryListView();
                return null;
            }
        }.execute();

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void populateSearchHistoryListView() {
        ListView searchHistoryListView;
        List<SearchHistory> searchHistoryList;
        ArrayList<String> searchHistoryTextArrayList = new ArrayList<>();

        searchHistoryListView = (ListView) findViewById(R.id.searchHistoryListView);

        // getting recent 10 searches from sqlite db
        searchHistoryList = searchHistoryService.getRecentSearchHistories(10);

        // constructing strings arrayList from searchHistory
        for (SearchHistory searchHistory: searchHistoryList) {
            searchHistoryTextArrayList.add(searchHistory.getSearchText());
        }

        // if list is empty then showing default values
        if (searchHistoryList.isEmpty()) {
            String suggestionsArray[] =
                    getResources().getStringArray(R.array.search_suggestions_array);
            for (String suggestion: suggestionsArray) {
                searchHistoryTextArrayList.add(suggestion);
            }
        }

        // populate the searchHistoryListView from sqlite db
        contextUtility.populateSimpleListView(searchHistoryListView, searchHistoryTextArrayList);

        searchHistoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String searchText = (String) adapterView.getItemAtPosition(position);
                searchServices(searchText, true);
            }
        });
    }

    private void searchServices(final String searchText, final boolean fromSearchHistory) {
        try {
            JSONObject body = new JSONObject();
            Location location = singleton.getCurrentLocation();
            String url = String.format(Urls.SEARCH_SERVICES
                    + "?latitude=%f&longitude=%f&searchText=%s",
                        location.getLatitude(), location.getLongitude(), searchText);
            showProgressDialog("Loading", "Please wait", false);
            ajaxUtility.makeHttpRequest(url, "GET", null, new AjaxCallback() {
                @Override
                public void onSuccess(JSONObject response) {
                    dismissProgressDialog();
                    try {
                        JSONArray result = response.getJSONArray("result");
                        if (result.length() > 0) {
                            // saving search text to sqlite db
                            if (!fromSearchHistory) {
                                SearchHistory searchHistory = new SearchHistory();
                                searchHistory.setSearchText(searchText);
                                searchHistoryService.createSearchHistory(searchHistory);
                            }

                            // go to MainActivity and show sellers on map
                            Intent intent = new Intent(SearchActivity.this,
                                    MainActivity.class);
                            // clear history stack so that back button does no lead to SearchActivity
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            Bundle args = new Bundle();
                            args.putString(AppConstants.SERVICES, result.toString());
                            intent.putExtras(args);
                            startActivity(intent);
                        } else {
                            showMessage("No sellers found. Try searching multiple keywords");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(JSONObject response) {
                    dismissProgressDialog();
                    try {
                        String message = response.getString("message");
                        int statusCode = response.getInt("statusCode");
                        showMessage(statusCode + ": " + message);
                    } catch (Exception e) {
                        e.printStackTrace();
                        showMessage(AppConstants.GENERIC_ERROR_MESSAGE);
                    }
                }

            });
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void showProgressDialog(String title, String message, boolean isCancellable) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(SearchActivity.this);
        }
        progressDialog.setTitle(title);
        progressDialog.setMessage(message);
        progressDialog.setCancelable(isCancellable);
        progressDialog.show();
    }

    private void dismissProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    private void showMessage(String message) {
        View parentLayout = findViewById(R.id.searchActivityContent);
        Snackbar.make(parentLayout, message, Snackbar.LENGTH_LONG).show();
    }

}

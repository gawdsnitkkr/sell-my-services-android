package me.varunon9.sellmyservices;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static me.varunon9.sellmyservices.constants.AppConstants.Urls;
import me.varunon9.sellmyservices.db.DbHelper;
import me.varunon9.sellmyservices.db.models.SearchHistory;
import me.varunon9.sellmyservices.utils.AjaxCallback;
import me.varunon9.sellmyservices.utils.AjaxUtility;
import me.varunon9.sellmyservices.utils.ContextUtility;

public class SearchActivity extends AppCompatActivity {

    DbHelper dbHelper;
    ContextUtility contextUtility;
    AjaxUtility ajaxUtility;

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
        contextUtility = new ContextUtility(this);
        ajaxUtility = new AjaxUtility(getApplicationContext());

        populateSearchHistoryListView(dbHelper);
        searchServicesEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int actionId, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        && keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    String searchText = searchServicesEditText.getText().toString();
                    searchServices(searchText);
                    return true;
                }
                return false;
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void populateSearchHistoryListView(DbHelper dbHelper) {
        ListView searchHistoryListView;
        List<SearchHistory> searchHistoryList;
        ArrayList<String> searchHistoryTextArrayList = new ArrayList<>();

        searchHistoryListView = (ListView) findViewById(R.id.searchHistoryListView);

        // getting recent 10 searches from sqlite db
        searchHistoryList = dbHelper.getRecentSearchHistories(10);

        // constructing strings arrayList from searchHistory
        for (SearchHistory searchHistory: searchHistoryList) {
            searchHistoryTextArrayList.add(searchHistory.getSearchText());
        }

        // populate the searchHistoryListView from sqlite db
        contextUtility.populateListView(searchHistoryListView, searchHistoryTextArrayList);

        // todo: add click listener to search
    }

    private void searchServices(final String searchText) {
        try {
            JSONObject body = new JSONObject();
            String url = Urls.SEARCH_SELLERS;
            body.put("latitude", 0);
            body.put("longitude", 0);
            body.put("searchText", searchText);
            ajaxUtility.makePostRequest(url, body, new AjaxCallback() {
                @Override
                public void onSuccess(String response) {
                    try {
                        JSONObject responseJson = new JSONObject(response);
                        if (responseJson.getBoolean("success")) {
                            // saving search text to sqlite db
                            SearchHistory searchHistory = new SearchHistory();
                            searchHistory.setSearchText(searchText);
                            dbHelper.createSearchHistory(searchHistory);
                        }
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(VolleyError error) {

                }
            });
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

}

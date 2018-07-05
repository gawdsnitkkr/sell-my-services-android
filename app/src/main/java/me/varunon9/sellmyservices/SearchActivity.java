package me.varunon9.sellmyservices;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import me.varunon9.sellmyservices.db.DbHelper;
import me.varunon9.sellmyservices.db.models.SearchHistory;
import me.varunon9.sellmyservices.utils.ContextUtility;

public class SearchActivity extends AppCompatActivity {

    DbHelper dbHelper;
    ContextUtility contextUtility;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // declare all local variables here
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        // display back button in action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dbHelper = new DbHelper(this);
        contextUtility = new ContextUtility(this);

        populateSearchHistoryListView(dbHelper);

    }

    @Override
    public boolean onSupportNavigateUp(){
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
    }

}

package com.liam.music.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.liam.music.R;

public class SearchActivity extends Activity {
	private Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
	private String selection = MediaStore.Audio.Media.TITLE + " LIKE ? OR "
			+ MediaStore.Audio.Media.ARTIST + " LIKE ?";
	private String[] projection = new String[]{MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.ARTIST};
	private String[] selectionArgs;
	private SimpleCursorAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			// Show the Up button in the action bar.
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
		Intent it = getIntent();
		String query = "%" + it.getStringExtra("query") + "%";
		selectionArgs = new String[] { query, query };
		Cursor c = getContentResolver().query(uri, null, selection, selectionArgs, null);
		if(c.getCount() > 0) {
			mAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2, c, projection, new int[] {android.R.id.text1, android.R.id.text2}, 0);
			ListView searchResultList = (ListView) findViewById(R.id.search_list);
			searchResultList.setAdapter(mAdapter);
		} else {
			TextView noResultText= (TextView) findViewById(R.id.no_result);
			noResultText.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.search, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}

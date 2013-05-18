package com.liam.music.activity;

import com.liam.music.R;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class AlbumSongListActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor> {
	private ListView mAlbumList;
	private static final int LOADER_ID = 0;
	private static final Uri URI = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
	private SimpleCursorAdapter mAlbumListAdapter;
	private String[] projection_album = new String[] {MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.ARTIST};
	private LoaderManager.LoaderCallbacks<Cursor> mCallbacks;
//	private String song_title;
//	private String song_path;
//	private String album_id;
	private Context context;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_album_song_list);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			// Show the Up button in the action bar.
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
		mAlbumList = (ListView) findViewById(R.id.album_list);		
		mAlbumListAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2, null, projection_album, new int[] {android.R.id.text1, android.R.id.text2}, 0);
		mCallbacks = this;
		getLoaderManager().initLoader(LOADER_ID, null, mCallbacks);
		mAlbumList.setAdapter(mAlbumListAdapter);
		context = this;
		mAlbumList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				Cursor c = (Cursor) mAlbumListAdapter.getItem(position); // position是Cursor中的，id是MediaStore中的
				int albumIdColumn = c
						.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
				int songTitleColumn = c
						.getColumnIndex(MediaStore.Audio.Media.TITLE);
				int songPathColumn = c
						.getColumnIndex(MediaStore.Audio.Media.DATA);
				int artistColumn = c
						.getColumnIndex(MediaStore.Audio.Media.ARTIST);
				String album_id = c.getString(albumIdColumn);
				String song_title = c.getString(songTitleColumn);
				String song_path = c.getString(songPathColumn);
				String artist = c.getString(artistColumn);
				Toast.makeText(context, "即将播放:" + song_title,
						Toast.LENGTH_LONG).show();
				Intent it = new Intent(context,
						com.liam.music.activity.PlayMySongActivity.class);
				it.putExtra("album_id", album_id);
				it.putExtra("song_title", song_title);
				it.putExtra("song_path", song_path);
				it.putExtra("artist", artist);
				startActivity(it);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.album_list, menu);
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

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		Intent it = getIntent();
		String album_id = it.getStringExtra("album_id");
		String selection = MediaStore.Audio.Media.ALBUM_ID + "=?";
		String[] selectionArgs = {album_id};
		return new CursorLoader(this, URI, null, selection, selectionArgs, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor cursor) {
		mAlbumListAdapter.swapCursor(cursor);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		mAlbumListAdapter.swapCursor(null);		
	}

}

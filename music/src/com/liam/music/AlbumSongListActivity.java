package com.liam.music;

import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class AlbumSongListActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor> {
	private ListView mAlbumList;
	private int loader_id;
	private Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
	private SimpleCursorAdapter mAlbumListAdapter;
	private String[] projection_album = new String[] {MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.ARTIST};
	private LoaderManager.LoaderCallbacks<Cursor> mCallbacks;
	public static String song_title;
	public static String song_path;
	public static String album_id;
	private Context context;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_album_song_list);
		mAlbumList = (ListView) findViewById(R.id.album_list);		
		mAlbumListAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2, null, projection_album, new int[] {android.R.id.text1, android.R.id.text2}, 0);
		mCallbacks = this;
		getLoaderManager().initLoader(loader_id, null, mCallbacks);
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
				album_id = c.getString(albumIdColumn);
				song_title = c.getString(songTitleColumn);
				song_path = c.getString(songPathColumn);
				Toast.makeText(context, "即将播放:" + song_title,
						Toast.LENGTH_LONG).show();
				Intent it = new Intent(context,
						com.liam.music.PlayMySongActivity.class);
				it.putExtra(album_id, album_id);
				it.putExtra(song_title, song_title);
				it.putExtra(song_path, song_path);
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
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		Intent it = getIntent();
		String album_id = it.getStringExtra(MainActivity.album_id);
		String selection = MediaStore.Audio.Media.ALBUM_ID + "=?";
		String[] selectionArgs = {album_id};
		return new CursorLoader(this, uri, null, selection, selectionArgs, null);
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

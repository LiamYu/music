package com.liam.music;

import android.annotation.SuppressLint;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

@SuppressLint("NewApi")
public class SongListActivity extends FragmentActivity implements
		LoaderManager.LoaderCallbacks<Cursor> {
	ViewPager mViewPager;
	SectionsPagerAdapter mSectionsPagerAdapter;
	// private GetAdapter mGetAdapter;
	// //�Զ��������ȡ�ظ����б����������࣬����SimpleCursorAdapter����
	// private CursorLoader mCursorLoader; // �α�װ����
	private final Uri songlist_uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
	private final Uri album_uri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
	private final Uri artist_uri = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI;
	/*
	 * private final String[] projection = new String[] {
	 * MediaStore.Audio.Media._ID, MediaStore.Audio.Media.DATA,
	 * MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.ARTIST,
	 * MediaStore.Audio.Media.ALBUM}; //
	 * projection��ָҪ���ص���,CursorLoader����Ҫ��_ID�У���������ʱ����
	 */
	private final String[] projection_songlist = new String[] {
			MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.ARTIST }; // SimpleCursorAdapter��projection
	private final String[] projection_album = new String[] {
			MediaStore.Audio.Albums.ALBUM, MediaStore.Audio.Albums.ARTIST }; // SimpleCursorAdapter��projection
	private final String[] projection_artist = new String[] { MediaStore.Audio.Artists.ARTIST }; // SimpleCursorAdapter��projection
	private final String sort_order = "TITLE"; // CursorLoader������˳�򣺰�������������
	private final int loader_songlist = 0; // װ����Loader��ΨһID��Activity/Fragment��Ψһ
	private final int loader_artist = 1;
	private final int loader_album = 2;
	// The callbacks through which we will interact with the LoaderManager.
	private LoaderManager.LoaderCallbacks<Cursor> mCallbacks;
	// The adapter that binds our data to the ListView
	public static SimpleCursorAdapter mSongListAdapter;
	public static SimpleCursorAdapter mArtistListAdapter;
	public static SimpleCursorAdapter mAlbumListAdapter;
	public static String song_title;
	public static String song_path;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_song_list);
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		/*
		 * Initialize the adapter. Note that we pass a 'null' Cursor as the
		 * third argument. We will pass the adapter a Cursor only when the data
		 * has finished loading for the first time (i.e. when the LoaderManager
		 * delivers the data to onLoadFinished). Also note that we have passed
		 * the "0" flag as the last argument. This prevents the adapter from
		 * registering a ContentObserver for the Cursor (the CursorLoader will
		 * do this for us!).
		 */
		mSongListAdapter = new SimpleCursorAdapter(this,
				android.R.layout.simple_list_item_2, null, projection_songlist,
				new int[] { android.R.id.text1, android.R.id.text2 }, 0);
		mAlbumListAdapter = new SimpleCursorAdapter(this,
				android.R.layout.simple_list_item_2, null, projection_album,
				new int[] { android.R.id.text1, android.R.id.text2 }, 0);
		mArtistListAdapter = new SimpleCursorAdapter(this,
				android.R.layout.simple_list_item_1, null, projection_artist,
				new int[] { android.R.id.text1 }, 0);
		// mGetAdapter = new GetAdapter();
		// Associate the (now empty) adapter with the ListView.

		/*
		 * The Activity (which implements the LoaderCallbacks<Cursor> interface)
		 * is the callbacks object through which we will interact with the
		 * LoaderManager. The LoaderManager uses this object to instantiate the
		 * Loader and to notify the client when data is made
		 * available/unavailable.
		 */
		mCallbacks = this;
		/*
		 * Initialize the Loader with id '1' and callbacks 'mCallbacks'. If the
		 * loader doesn't already exist, one is created. Otherwise, the already
		 * created Loader is reused. In either case, the LoaderManager will
		 * manage the Loader across the Activity/Fragment lifecycle, will
		 * receive any new loads once they have completed, and will report this
		 * new data back to the 'mCallbacks' object.
		 */
		LoaderManager lm = getLoaderManager();
		/*
		 * ����: 1�� ��һ��������0 ΪLoader��Ψһ��ʶID�� 2�� �ڶ��������� ΪLoader�Ĺ�������ѡ����������Ϊnull�� 3��
		 * ������������this�������ʾ��ǰActivity�������Fragment�����ṩ��LoaderManager����������ݻ㱨��
		 */
		lm.initLoader(loader_songlist, null, mCallbacks);
		lm.initLoader(loader_artist, null, mCallbacks);
		lm.initLoader(loader_album, null, mCallbacks);

	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle arg1) {
		switch (id) {
		case loader_songlist:
			// �����µĲ�ѯ��������һ���µ��α������CursorLoader
			return new CursorLoader(SongListActivity.this, songlist_uri, null,
					null, null, sort_order); // ����Cursor����
		case loader_album:
			return new CursorLoader(SongListActivity.this, album_uri, null,
					null, null, null);
		case loader_artist:
			return new CursorLoader(SongListActivity.this, artist_uri, null,
					null, null, null);
		}
		return null;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		switch (loader.getId()) {
		// A switch-case is useful when dealing with multiple Loaders/IDs
		case loader_songlist:
			/*
			 * The asynchronous load is complete and the data is now available
			 * for use. Only now can we associate the queried Cursor with the
			 * SimpleCursorAdapter.
			 */
			mSongListAdapter.swapCursor(cursor);
			break;
		case loader_album:
			mAlbumListAdapter.swapCursor(cursor);
			break;
		case loader_artist:
			mArtistListAdapter.swapCursor(cursor);
			break;
		}
		// The listview now displays the queried data.
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		/*
		 * For whatever reason, the Loader's data is now unavailable. Remove any
		 * references to the old data by replacing it with a null Cursor.
		 */
		switch (loader.getId()) {
		case loader_songlist:
			mSongListAdapter.swapCursor(null);
			break;
		case loader_album:
			mAlbumListAdapter.swapCursor(null);
			break;
		case loader_artist:
			mArtistListAdapter.swapCursor(null);
			break;
		}
	}

	// �ڲ���
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			SectionFragment sf = new SectionFragment();
			Bundle args = new Bundle();
			args.putInt(SectionFragment.ARG_SECTION_NUMBER, position + 1);
			sf.setArguments(args);
			return sf;
		}

		@Override
		public int getCount() {
			return 3;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			switch (position) {
			case 0:
				return getString(R.string.title_songlist);
			case 1:
				return getString(R.string.title_artist);
			case 2:
				return getString(R.string.title_album);
			}
			return null;
		}
	}

	// �ڲ���
	public static class SectionFragment extends Fragment {
		private ListView mSongListView;
		private ListView mAlbumListView;
		private ListView mArtistListView;
		public static final String ARG_SECTION_NUMBER = "section_number";

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			switch (getArguments().getInt(ARG_SECTION_NUMBER)) {
			case 1:
				View song_root_view = inflater.inflate(
						R.layout.fragment_song_list, container, false);
				mSongListView = (ListView) song_root_view
						.findViewById(R.id.song_list);
				mSongListView.setAdapter(mSongListAdapter);
				mSongListView.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						Cursor c = (Cursor) mSongListAdapter.getItem(position); // position��Cursor�еģ�id��MediaStore�е�
						int pathColumn = c.getColumnIndex(MediaStore.Audio.Media.DATA);
						int titleColumn = c.getColumnIndex(MediaStore.Audio.Media.TITLE);
						song_path = c.getString(pathColumn);
						song_title = c.getString(titleColumn);
						Toast.makeText(getActivity(), "��������:" + song_title,
								Toast.LENGTH_LONG).show();
						Intent it = new Intent(getActivity(),
								com.liam.music.PlayMySong.class);
						it.putExtra(song_title, song_title);
						it.putExtra(song_path, song_path);
						startActivity(it);
					}
				});
				return song_root_view;
			case 2:
				View artist_root_view = inflater.inflate(
						R.layout.fragment_artist, container, false);
				mArtistListView = (ListView) artist_root_view
						.findViewById(R.id.artist_list);
				mArtistListView.setAdapter(mArtistListAdapter);
				return artist_root_view;
			case 3:
				View album_root_view = inflater.inflate(
						R.layout.fragment_album, container, false);
				mAlbumListView = (ListView) album_root_view
						.findViewById(R.id.album_list);
				mAlbumListView.setAdapter(mAlbumListAdapter);
				return album_root_view;
			}
			return null;
		}

	}

	/*
	 * public SimpleCursorAdapter getAdapter() { return mAdapter; }
	 */

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.song_list_actitivy, menu);
		return true;
	}

}
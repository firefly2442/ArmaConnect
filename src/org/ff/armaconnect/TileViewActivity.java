/*
This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.
This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU General Public License for more details.
You should have received a copy of the GNU General Public License
along with this program. If not, see <http://www.gnu.org/licenses/>.
*/

package org.ff.armaconnect;

import android.app.Activity;
import android.os.Bundle;

import com.qozix.tileview.TileView;

public class TileViewActivity extends Activity {

	//https://github.com/moagrius/TileView/
	private TileView tileView;
	
	@Override
	public void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		tileView = new TileView( this );

		/**
		 * Set an id. This is necessary to enable the save state mechanism of Android.
		 * It is retrieved from a resource value, but it can also be generated with
		 * {@code View.generateViewId()}.
		 */
		tileView.setId( R.id.tileview_id );
		tileView.setSaveEnabled( true );

		// use our own custom bitmap provider to access Android internal storage
		tileView.setBitmapProvider( new BitmapProviderInternalStorage() );

		setContentView( tileView );
	}
	
	@Override
	public void onPause() {
		super.onPause();
		tileView.pause();
	}

	@Override
	public void onResume() {
		super.onResume();
		tileView.resume();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		tileView.destroy();
		tileView = null;
	}
	
	public TileView getTileView(){
		return tileView;
	}
	
	/**
	 * This is a convenience method to moveToAndCenter after layout (which won't happen if called directly in onCreate
	 * see https://github.com/moagrius/TileView/wiki/FAQ
	 */

	public void frameTo( final double x, final double y ) {
		getTileView().post( new Runnable() {
			@Override
			public void run() {
				getTileView().scrollToAndCenter( x, y );
			}			
		});		
	}
}

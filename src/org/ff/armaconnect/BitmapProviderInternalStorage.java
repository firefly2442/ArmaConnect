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


import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.qozix.tileview.graphics.BitmapProvider;
import com.qozix.tileview.tiles.Tile;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Locale;

public class BitmapProviderInternalStorage implements BitmapProvider {
    private static final BitmapFactory.Options OPTIONS = new BitmapFactory.Options();

    static {
        OPTIONS.inPreferredConfig = Bitmap.Config.RGB_565;
    }

    @Override
    public Bitmap getBitmap(Tile tile, Context context ) {
        Object data = tile.getData();
        if( data instanceof String ) {
            String unformattedFileName = (String) tile.getData();
            String formattedFileName = String.format(Locale.US, unformattedFileName, tile.getColumn(), tile.getRow());
            try {
                InputStream inputStream = new FileInputStream(formattedFileName);
                if( inputStream != null ) {
                    try {
                        return BitmapFactory.decodeStream(inputStream, null, OPTIONS);
                    } catch(OutOfMemoryError | Exception e) {
                        // this is probably an out of memory error - you can try sleeping (this method won't be called in the UI thread) or try again (or give up)
                        Log.e("BitmapProviderIntStrg", "Out of memory: " + e);
                    }
                }
            } catch(Exception e) {
                // this is probably an IOException, meaning the file can't be found
                Log.e("BitmapProviderIntStrg", "Error loading file: " + e);
            }
        }
        return null;
    }
}

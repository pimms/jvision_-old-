package pimms.joakimvision;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.GridLayout;

import java.util.ArrayList;
import java.util.List;

import pimms.joakimvision.transport.TrainTile;

public class JVActivity extends Activity {
    private GridLayout _gridLayout;

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);

        // Enable fullscreen - hide both the title bar and the navigation bar.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        final int fs = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setFlags(fs, fs);

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN);


        setContentView(R.layout.activity_main);
        _gridLayout = (GridLayout)findViewById(R.id.fragment_container);

        // This is where new TileFragments are added.
        List<TileFragment> fragments = new ArrayList<>();
        fragments.add(new ClockTile());
        fragments.add(new TrainTile());

        layoutTileFragments(fragments);
    }

    private void layoutTileFragments(List<TileFragment> fragments) {
        TileOrganizer organizer = new TileOrganizer(_gridLayout);
        List<TileOrganizer.TilePosition> positions = organizer.organizeTiles(fragments);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        final int cellWidth = displayMetrics.widthPixels / _gridLayout.getColumnCount();
        final int cellHeight = displayMetrics.heightPixels / _gridLayout.getRowCount();

        for (TileOrganizer.TilePosition tilePos : positions) {
            FragmentManager mgr = getFragmentManager();

            FragmentTransaction trans = mgr.beginTransaction();
            trans.add(R.id.fragment_container, tilePos.fragment);
            trans.commit();

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.columnSpec = GridLayout.spec(tilePos.x, tilePos.w, GridLayout.FILL);
            params.rowSpec = GridLayout.spec(tilePos.y, tilePos.h, GridLayout.FILL);
            params.width = (cellWidth * tilePos.w) - params.leftMargin - params.rightMargin;
            params.height = (cellHeight * tilePos.h) - params.topMargin - params.bottomMargin;

            tilePos.fragment.setGridLayoutParams(params);
        }
    }
}

package pimms.joakimvision;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.TextView;

public class TileFragment extends Fragment {
    private String _labelText = "Default text";
    private GridLayout.LayoutParams _gridLayoutParams;

    public void setText(String text) {
        _labelText = text;
    }

    public final void setGridLayoutParams(GridLayout.LayoutParams params) {
        _gridLayoutParams = params;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        View view = inflater.inflate(R.layout.fragment_tile, container, false);

        TextView tv = (TextView)view.findViewById(R.id.tile_label);
        tv.setText(_labelText);

        view.setLayoutParams(_gridLayoutParams);

        return view;
    }

    public int getPreferredWidth() {
        return 2;
    }

    public int getPreferredHeight() {
        return 2;
    }

    public int getMinimumWidth() {
        return 1;
    }

    public int getMinimumHeight() {
        return 1;
    }

    public final int getArea() {
        return getPreferredHeight() * getPreferredWidth();
    }
}

package pimms.joakimvision;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ClockTile extends TileFragment {
    private TextView _clockLabel;
    private TextView _dateLabel;

    public ClockTile() {
        setPreferredSize(8, 6);
        setMinimumSize(8, 3);
    }

    @Override
    protected View createView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.tile_clock, container, false);

        _clockLabel = (TextView)view.findViewById(R.id.clock_label);
        _dateLabel = (TextView)view.findViewById(R.id.date_label);

        updateTime();
        return view;
    }


    private void updateTime() {
        SimpleDateFormat sdf;

        sdf = new SimpleDateFormat("HH:mm");
        _clockLabel.setText(sdf.format(new Date()));

        sdf = new SimpleDateFormat("dd MMM, yyyy");
        _dateLabel.setText(sdf.format(new Date()));
    }
}

package pimms.joakimvision.transport;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import pimms.joakimvision.R;
import pimms.joakimvision.TileFragment;

public class TrainTile extends TileFragment implements TransportDelegate {
    private enum State {
        LOADING,
        DOWNLOADED,
        ERROR,
    }

    State _state;
    View _rootView;
    ViewGroup _stateContainerView;
    View _stateView;

    /* Only valid for state DOWNLOADED - may be non-nil for other states, but not guaranteed */
    private List<TransportDeparture> _departures;

    /* Only valid for state ERROR - may be non-nil for other states, but not guaranteed */
    private String _errorMessage;


    public TrainTile() {
        setPreferredSize(4, 4);
        setMinimumSize(4, 4);
    }

    @Override
    protected View createView(LayoutInflater inflater, ViewGroup container) {
        _rootView = inflater.inflate(R.layout.tile_train, container, false);
        _stateContainerView = (ViewGroup)_rootView.findViewById(R.id.train_root_view);

        stateTransition(State.LOADING);

        RuterDownloader downloader = new RuterDownloader(this);
        downloader.downloadTransportData(3011750, 3501, "Lillestrøm");

        return _rootView;
    }

    @Override
    public void onTransportDataDownloaded(List<TransportDeparture> departures) {
        Log.d("TrainTile", "TransportDownaloder succeeded");

        if (departures == null || departures.size() == 0) {
            return;
        }

        departures.get(0).setDelayMinutes(1);

        _departures = departures;
        stateTransition(State.DOWNLOADED);
    }

    @Override
    public void onTransportDataFailed(String errorMessage) {
        Log.e("TrainTile", "TransportDownaloder failed: " + errorMessage);

        _errorMessage = errorMessage;
        stateTransition(State.ERROR);
    }


    private void stateTransition(State newState) {
        if (_stateView != null && _stateView.getParent() != null) {
            _stateContainerView.removeView(_stateView);
        }

        _state = newState;

        switch (newState) {
            case LOADING:
                initLoading();
                break;

            case DOWNLOADED:
                initDownloaded();
                break;

            case ERROR:
                initError();
                break;
        }
    }

    private void initLoading() {
        _rootView.setBackgroundColor(getBackgroundColor());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        _stateView = inflater.inflate(R.layout.view_loading, null);
        _stateContainerView.addView(_stateView);
    }

    private void initDownloaded() {
        _rootView.setBackgroundColor(getBackgroundColor());

        LayoutInflater inflater = getActivity().getLayoutInflater();

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        TransportDeparture departure;
        View trainView;
        TextView textView;

        departure = _departures.get(0);
        _stateView = inflater.inflate(R.layout.view_container, null);
        _stateContainerView.addView(_stateView);

        trainView = inflater.inflate(R.layout.train_first, (ViewGroup)_stateView);

        Date depTime = departure.getDeparture();

        textView = (TextView)trainView.findViewById(R.id.label_train_departure_time);
        textView.setText(sdf.format(depTime));

        textView = (TextView)trainView.findViewById(R.id.label_train_departure_delay);
        handleDelayTextView(textView, departure);

        textView = (TextView)trainView.findViewById(R.id.label_train_line_name);
        textView.setText(departure.getLineName());

        textView = (TextView)trainView.findViewById(R.id.label_train_platform);
        textView.setText("Platform " + departure.getPlatformName());

        for (int i=1; i<Math.min(_departures.size(), 4); i++) {
            trainView = inflater.inflate(R.layout.train_later, null);
            ((ViewGroup)_stateView).addView(trainView);

            textView = (TextView)trainView.findViewById(R.id.label_train_departure_time);
            textView.setText(sdf.format(_departures.get(i).getDeparture()));

            textView = (TextView)trainView.findViewById(R.id.label_train_departure_delay);
            handleDelayTextView(textView, _departures.get(i));
        }
    }

    private void initError() {
        _rootView.setBackgroundColor(getResources().getColor(R.color.error_background));

        LayoutInflater inflater = getActivity().getLayoutInflater();
        _stateView = inflater.inflate(R.layout.view_error, null);
        _stateContainerView.addView(_stateView);
        _stateView.getLayoutParams().width = LinearLayout.LayoutParams.MATCH_PARENT;
        _stateView.getLayoutParams().height = LinearLayout.LayoutParams.MATCH_PARENT;

        ((TextView)_stateView.findViewById(R.id.label_error_message)).setText(_errorMessage);
    }


    private void handleDelayTextView(TextView textView, TransportDeparture departure) {
        String msg;
        int color;

        int minutes = departure.getDelayMinutes();

        if (minutes == 0) {
            msg = "On schedule";
            color = R.color.text_green;
        } else if (minutes > 0) {
            msg = "$ min. late";
            color = R.color.text_red;
        } else {
            msg = "$ min early";
            color = R.color.text_red;
        }



        msg = msg.replaceAll("\\$", "" + Math.abs(minutes));
        color = getResources().getColor(color);

        textView.setTextColor(color);
        textView.setText(msg);
    }
}

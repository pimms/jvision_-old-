package pimms.joakimvision.transport;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.List;

import pimms.joakimvision.HTTP;

public class RuterDownloader implements HTTP.Delegate {
    private TransportDelegate _delegate;
    private boolean _isDownloading;
    private int _stopID;
    private int _lineRef;
    private String _destName;

    public RuterDownloader(TransportDelegate delegate) {
        _delegate = delegate;
    }

    /**
     * Download transport data from Ruter's web API. All LISTED departures (that are in the future)
     * are given to the delegate via TransportDelegate#onTransportDataDownloaded.
     *
     * Note that the @stopID and @lineRef parameters must be retrieved "manually" via Ruter's
     * database, which can be found here:
     * http://labs.ruter.no/how-to-use-the-api/infrastructure-flat-files.aspx
     *
     * @param stopID The stop from which the vehicle will depart.
     * @param lineRef The line to retrieve data for.
     * @param destinationName The name of the destination. Almost always the stopping station for
     *                        the departure (Lillestrøm, Skien, Gjøvik, Spikkestad, etc.).
     * @return Whether or not the download started. Only one download can be active per instance of
     *         RuterDownloader at any given time.
     */
    public boolean downloadTransportData(final int stopID, final int lineRef, final String destinationName) {
        if (_isDownloading) {
            Log.w("RuterDownloader", "Attempted do start a second simultaneous download on busy instance");
            return false;
        }

        _isDownloading = true;

        final String url = "http://reisapi.ruter.no/stopvisit/getdepartures/" + stopID;

        _stopID = stopID;
        _lineRef = lineRef;
        _destName = destinationName;

        HTTP http = new HTTP(this);
        http.setMainTreadCallback(false);
        http.getSite(url);

        return true;
    }

    @Override
    public void onHTTPSuccess(String url, String retrievedContent) {
        RuterParser parser = new RuterParser(_lineRef, _destName);
        List<TransportDeparture> departures = null;

        try {
            departures = parser.parse(retrievedContent);
        } catch (Exception e) {
            Log.e("RuterDownloader", "Failed to parse Ruter data");
        }

        _isDownloading = false;

        if (departures == null || departures.size() == 0) {
            MT_notifyDelegateError("No departures found.");
        } else {
            MT_notifyDelegateSuccess(departures);
        }
    }

    @Override
    public void onHTTPError(String url, String message) {
        MT_notifyDelegateError(message);
    }


    private void MT_notifyDelegateError(final String errMsg) {
        (new Handler(Looper.getMainLooper())).post(new Runnable() {
            @Override
            public void run() {
                _delegate.onTransportDataFailed(errMsg);
            }
        });
    }

    private void MT_notifyDelegateSuccess(final List<TransportDeparture> departures) {
        (new Handler(Looper.getMainLooper())).post(new Runnable() {
            @Override
            public void run() {
                _delegate.onTransportDataDownloaded(departures);
            }
        });
    }
}

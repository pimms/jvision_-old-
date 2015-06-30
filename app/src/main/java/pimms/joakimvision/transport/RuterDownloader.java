package pimms.joakimvision.transport;

import android.os.Handler;
import android.os.Looper;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.List;

public class RuterDownloader {
    private TransportDelegate _delegate;
    private boolean _isDownloading;

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
     * @return Whether or not the download was successfully started.
     */
    public boolean downloadTransportData(final int stopID, final int lineRef, final String destinationName) {
        if (_isDownloading) {
            return false;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    doDownload(stopID, lineRef, destinationName);
                } catch (IOException ioEx) {
                    MT_notifyDelegateError("Unable to retrieve data:\n" + ioEx.getMessage());
                    ioEx.printStackTrace();
                } catch (Exception general) {
                    MT_notifyDelegateError("Unable to parse data:\n" + general.getMessage());
                    general.printStackTrace();
                }

                _isDownloading = false;
            }
        }).start();

        return true;
    }


    private void doDownload(int stopID, int lineRef, String destinationName) throws Exception {
        final String url = "http://reisapi.ruter.no/stopvisit/getdepartures/" + stopID;

        HttpClient httpClient = new DefaultHttpClient();

        HttpGet request = new HttpGet(url);
        request.addHeader("Accept", "text/html,application/xhtml+html,application/xml");
        request.addHeader("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.1; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
        HttpResponse response = httpClient.execute(request);

        String xml = EntityUtils.toString(response.getEntity());

        RuterParser parser = new RuterParser(lineRef, destinationName);
        List<TransportDeparture> departures = parser.parse(xml);

        if (departures == null || departures.size() == 0) {
            MT_notifyDelegateError("No departures found.");
        } else {
            MT_notifyDelegateSuccess(departures);
        }
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

package pimms.joakimvision;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;


/**
 * HTTP can be used to retrieve TEXT content from a website. Binary content, such as images
 * and audio will be added at a later point in time. Text will do for now.
 */
public class HTTP {
    public interface Delegate {
        void onHTTPSuccess(String url, String retrievedContent);
        void onHTTPError(String url, String message);
    }

    private Delegate _delegate;
    private boolean _mainThreadCallback = false;

    public HTTP(Delegate delegate) {
        _delegate = delegate;
    }

    public void getSite(final String url) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                fetchSite(url);
            }
        }).start();
    }


    /**
     * Define whether or not the HTTP.Delegate should be notified upon success, or error, when
     * the requested website has been fetched. If false, the Delegate will be notified on a
     * background thread. The Delegate is responsible for maintaining thread safety.
     *
     * By default, HTTP returns the content in a background thread.
     *
     * @param mtCallback Whether or not the callback should occur on the main thread.
     */
    public void setMainTreadCallback(boolean mtCallback) {
        _mainThreadCallback = mtCallback;
    }


    private void fetchSite(String url) {
        HttpClient httpClient = new DefaultHttpClient();

        HttpGet request = new HttpGet(url);
        request.addHeader("Accept", "text/html,application/xhtml+html,application/xml");
        request.addHeader("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.1; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");

        try {
            HttpResponse response = httpClient.execute(request);
            String textContent = EntityUtils.toString(response.getEntity());
            notifySuccess(url, textContent);
        } catch (Exception e) {
            Log.e("HTTP Error", e.getMessage());
            e.printStackTrace();

            notifyFailure(url, e.getMessage());
        }
    }

    private void notifyFailure(final String url, final String message) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                _delegate.onHTTPError(url, message);
            }
        };

        if (_mainThreadCallback) {
            new Handler(Looper.getMainLooper()).post(runnable);
        } else {
            runnable.run();
        }
    }

    private void notifySuccess(final String url, final String content) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                _delegate.onHTTPSuccess(url, content);
            }
        };

        if (_mainThreadCallback) {
            new Handler(Looper.getMainLooper()).post(runnable);
        } else {
            runnable.run();
        }
    }
}

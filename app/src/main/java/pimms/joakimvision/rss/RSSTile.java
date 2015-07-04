package pimms.joakimvision.rss;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Date;
import java.util.List;

import pimms.joakimvision.HTTP;
import pimms.joakimvision.R;
import pimms.joakimvision.TileFragment;

public class RSSTile extends TileFragment implements HTTP.Delegate {
    private String _rssUrl;
    private String _title;
    private ViewGroup _itemContainer;

    public RSSTile() {
        setPreferredSize(6, 8);
        setMinimumSize(6, 4);

        setPriority(0);
    }

    public void setRSSUrl(String url) {
        _rssUrl = url;

        if (getPriortiy() == 0) {
            setPriority(TileFragment.DEFAULT_PRIORITY);
        }
    }

    public void setTitle(String title) {
        _title = title;
    }

    @Override
    protected View createView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.tile_rss, container, false);
        _itemContainer = (ViewGroup)view.findViewById(R.id.rss_container);

        ((TextView)view.findViewById(R.id.rss_title)).setText(_title);

        View loadView = inflater.inflate(R.layout.view_loading, _itemContainer, false);
        _itemContainer.addView(loadView);

        HTTP http = new HTTP(this);
        http.setMainTreadCallback(true);
        http.getSite(_rssUrl);

        return view;
    }


    @Override
    public void onHTTPSuccess(String url, String retrievedContent) {
        getView().setBackgroundColor(getBackgroundColor());
        _itemContainer.removeAllViews();

        LayoutInflater inflater = getActivity().getLayoutInflater();
        List<RSSItem> items = new RSSParser().parseFeed(retrievedContent, 5);
        if (items == null || items.size() == 0) {
            onHTTPError(url, "No items in feed");
            return;
        }

        Date yesterday = new Date(new Date().getTime() - 86400);

        for (RSSItem item : items) {
            View view = inflater.inflate(R.layout.rss_cell, _itemContainer, false);

            /*
            String format;
            if (item.publicationTime.before(yesterday)) {
                format = "dd/MM";
            } else {
                format = "HH:mm";
            }

            String pub = new SimpleDateFormat(format).format(item.publicationTime);

            ((TextView)view.findViewById(R.id.rss_item_date)).setText(pub);
            */

            ((TextView)view.findViewById(R.id.rss_item_title)).setText(item.title);
            ((TextView)view.findViewById(R.id.rss_item_desc)).setText(item.description);

            _itemContainer.addView(view);
        }
    }

    @Override
    public void onHTTPError(String url, String message) {
        getView().setBackgroundColor(getResources().getColor(R.color.error_background));
        _itemContainer.removeAllViews();

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View errView = inflater.inflate(R.layout.view_error, _itemContainer, false);
        ((TextView)errView.findViewById(R.id.label_error_message)).setText(message);
        _itemContainer.addView(errView);
    }
}

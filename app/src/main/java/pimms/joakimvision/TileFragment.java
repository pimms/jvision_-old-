package pimms.joakimvision;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;

import java.security.InvalidParameterException;

public abstract class TileFragment extends Fragment {
    private GridLayout.LayoutParams _gridLayoutParams;
    private int _prefW = 1;
    private int _prefH = 1;
    private int _minW = 1;
    private int _minH = 1;

    public final void setGridLayoutParams(GridLayout.LayoutParams params) {
        _gridLayoutParams = params;
    }

    protected abstract View createView(LayoutInflater inflater, ViewGroup container);

    @Override
    public final View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        View view = createView(inflater, container);

        if (view == null) {
            throw new NullPointerException("TileFragment#createView returned null");
        }

        if (_gridLayoutParams != null) {
            view.setLayoutParams(_gridLayoutParams);
        }

        return view;
    }


    public final int getPreferredWidth() {
        return _prefW;
    }

    public final int getPreferredHeight() {
        return _prefH;
    }

    public final int getMinimumWidth() {
        return _minW;
    }

    public final int getMinimumHeight() {
        return _minH;
    }


    protected void setPreferredSize(int w, int h) {
        if (w <= 0 || h <= 0) {
            throw new InvalidParameterException("Preferred dimensions must be positive");
        }

        _prefW = w;
        _prefH = h;
    }

    protected void setMinimumSize(int w, int h) {
        if (w <= 0 || h <= 0) {
            throw new InvalidParameterException("Minimum dimensions must be positive");
        }

        _minW = w;
        _minH = h;
    }

}

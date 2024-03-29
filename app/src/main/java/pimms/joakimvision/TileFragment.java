package pimms.joakimvision;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;

import java.security.InvalidParameterException;

public abstract class TileFragment extends Fragment {
    public static final int DEFAULT_PRIORITY = 10;

    private static final int BACKGROUND_COLORS[] = new int[] {
            R.color.background_1,
            R.color.background_2,
            R.color.background_3,
            R.color.background_4,
            R.color.background_5,
            R.color.background_6,
            R.color.background_7,
            R.color.background_8,
            R.color.background_9,
            R.color.background_10,
            R.color.background_11,
    };

    private GridLayout.LayoutParams _gridLayoutParams;
    private int _prefW = 1;
    private int _prefH = 1;
    private int _minW = 1;
    private int _minH = 1;
    private int _priority = DEFAULT_PRIORITY;

    public final void setGridLayoutParams(GridLayout.LayoutParams params) {
        _gridLayoutParams = params;
    }

    public final int getBackgroundColor() {
        final int hash = getClass().hashCode();
        final int raw = BACKGROUND_COLORS[hash % BACKGROUND_COLORS.length];

        return getResources().getColor(raw);
    }

    protected abstract View createView(LayoutInflater inflater, ViewGroup container);

    @Override
    public final View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        View view = createView(inflater, container);

        if (view == null) {
            throw new NullPointerException("TileFragment#createView returned null");
        }

        view.setBackgroundColor(getBackgroundColor());

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

    public final int getPriortiy() {
        return _priority;
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

    /**
     * Set the priority of this TileFragment. Tiles with higher priority will be shown more often.
     * The default priority is TileFragment.DEFAULT_PRIORITY.
     *
     * @param priority The new priority to assign.
     */
    protected void setPriority(int priority) {
        _priority = priority;
    }
}

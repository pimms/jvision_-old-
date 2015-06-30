package pimms.joakimvision;

import android.util.Log;
import android.widget.GridLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class TileOrganizer {
    public class TilePosition implements Comparable<TilePosition> {
        public TileFragment fragment;
        public int x;
        public int y;
        public int w;
        public int h;

        @Override
        public int compareTo(TilePosition other) {
            int a1 = w * h;
            int a2 = other.w * other.h;
            return Integer.compare(a1, a2);
        }
    }

    private GridLayout _parentView;
    private boolean _grid[][];
    private Random _random = new Random(System.currentTimeMillis());

    public TileOrganizer(GridLayout parentView) {
        _parentView = parentView;
    }

    public List<TilePosition> organizeTiles(final List<TileFragment> fragments) {
        int nRows = _parentView.getRowCount();
        int nCols = _parentView.getColumnCount();
        _grid = new boolean[nCols][nRows];

        ArrayList<TileFragment> frags = new ArrayList<>(fragments);
        ArrayList<TilePosition> placements = new ArrayList<>();
        Collections.shuffle(frags);

        for (int i=0; i<frags.size(); i++) {
            TilePosition tp = placeTile(frags.get(i));
            if (tp != null) {
                placements.add(tp);
                Log.d("Shit going down", "Tile at " + tp.x + ", " + tp.y + " with sz " + tp.w + ", " + tp.h);
            }
        }

        expandWherePossible(placements);

        return placements;
    }


    private TilePosition placeTile(TileFragment tile) {
        TilePosition tp;

        int prefW = tile.getPreferredWidth();
        int prefH = tile.getPreferredHeight();

        // 10% chance to decrement Width and Height.
        if (prefW > tile.getMinimumWidth() && (_random.nextInt() % 10) == 0)
            prefW--;
        if (prefH > tile.getMinimumHeight() && (_random.nextInt() % 10) == 0)
            prefH--;

        tp = placeTile(prefW, prefH);

        if (tp == null) {
            int w = tile.getPreferredWidth();
            int h = tile.getPreferredHeight();

            while (tp == null) {
                if (w == tile.getMinimumWidth() && h == tile.getMinimumHeight()) {
                    // No solution available
                    break;
                }

                if (w > tile.getMinimumWidth() && h > tile.getMinimumHeight()) {
                    // Decrement either W or H, choose randomly
                    if (_random.nextBoolean()) {
                        w--;
                    } else {
                        h--;
                    }
                } else if (w > tile.getMinimumWidth()) {
                    w--;
                } else {
                    h--;
                }

                tp = placeTile(w, h);
            }
        }

        if (tp != null) {
            tp.fragment = tile;
        }

        return tp;
    }

    private TilePosition placeTile(int w, int h) {
        int nRows = _parentView.getRowCount();
        int nCols = _parentView.getColumnCount();

        TilePosition tilePosition = null;

        for (int y=0; y<nRows; y++) {
            for (int x=0; x<nCols; x++) {
                boolean mismatch = false;

                if (x + w > nCols || y + h > nRows) {
                    mismatch = true;
                } else {
                    // [x+j, y+k] is the area covered by the view. Ensure all squares are free.
                    for (int j=0; j<w; j++) {
                        for (int k=0; k<h; k++) {
                            if (_grid[x + j][y + k]) {
                                mismatch = true;
                            }
                        }
                    }
                }

                if (!mismatch) {
                    // Flag this area as occupied
                    for (int j=0; j<w; j++) {
                        for (int k=0; k<h; k++) {
                            _grid[x+j][y+k] = true;
                        }
                    }

                    tilePosition = new TilePosition();
                    tilePosition.x = x;
                    tilePosition.y = y;
                    tilePosition.w = w;
                    tilePosition.h = h;
                    return tilePosition;
                }
            }
        }

        return null;
    }


    /**
     * Expand all tiles as much as possible. This method does NOT find the optimal solution, nor
     * does it guarantee that all tiles on the available grid is filled. Smaller tiles are
     * prioritized.
     * @param placements The TilePosition objects to expand.
     */
    private void expandWherePossible(List<TilePosition> placements) {
        int expands;

        do {
            expands = 0;

            Collections.sort(placements);

            for (TilePosition tp : placements) {
                if (expand(tp)) {
                    expands++;
                }
            }
        } while (expands > 0);
    }

    /**
     * Expand a TilePosition's width or height by 1 in either direction. Even if there is room
     * for more than a single expansions, only one will be performed. To make an N-1xN-1 Tile fill
     * an NxN grid, this method must therefore be called twice. The _grid is updated to reflect any
     * changes made.
     * @param tilePosition The TilePosition object to expand.
     * @return Whether or not an expansion was possible.
     */
    private boolean expand(TilePosition tilePosition) {
        final int dirs[][] = new int[][] {{1, 0}, {0, 1}, {-1, 0}, {0, -1}};
        final int nCols = _parentView.getColumnCount();
        final int nRows = _parentView.getRowCount();

        for (int dir[] : dirs) {
            final int dirX = dir[0];
            final int dirY = dir[1];

            if (tilePosition.x + dirX < 0 || tilePosition.x + tilePosition.w + dirX > nCols ||
                tilePosition.y + dirY < 0 || tilePosition.y + tilePosition.h + dirY > nRows) {
                continue;
            }

            // If the expansion is on the Y axis, we need to check along the entire X axis
            // for occupied tiles - and vice versa when expanding along the X axis.
            int edgeLength = (dirX == 0 ? tilePosition.w : tilePosition.h);

            // The delta values used when checking the edges of the tile.
            int dx = (dirX == 0 ? 1 : 0);
            int dy = (dirY == 0 ? 1 : 0);

            // If we are checking in a POSITIVE direction, we need to add the current size of the
            // tile along the axis we are expanding.
            final int x = tilePosition.x + (dirX == 1 ? tilePosition.w - 1 : 0);
            final int y = tilePosition.y + (dirY == 1 ? tilePosition.h - 1 : 0);

            // See if we can expand along the edge in question.
            boolean mismatch = false;
            int ix = x;
            int iy = y;

            for (int i=0; i<edgeLength && !mismatch; i++) {
                mismatch = (_grid[ix+dirX][iy+dirY] || mismatch);
                ix += dx;
                iy += dy;
            }

            // If no mismatch were found, update the TilePosition and _grid to make the expansion.
            if (!mismatch) {
                Log.d("TileOrganizer", "Expanded a tile in direction " + dirX + ", " + dirY);

                tilePosition.x += (dirX < 0 ? dirX : 0);
                tilePosition.y += (dirY < 0 ? dirY : 0);
                tilePosition.w += (dirX > 0 ? dirX : 0);
                tilePosition.h += (dirY > 0 ? dirY : 0);

                // Update the grid.
                ix = x;
                iy = y;
                for (int i=0; i<edgeLength; i++) {
                    _grid[ix+dirX][iy+dirY] = true;
                    ix += dx;
                    iy += dy;
                }

                return true;
            }
        }

        return false;
    }
}

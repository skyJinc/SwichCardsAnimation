package sky.library;

import android.view.View;

/**
 * @author sky
 * @version 1.0 on 2018-06-1 下午2:06
 */
public class SkyItem {
    public View view;
    public float zIndex;
    int adapterIndex;

    SkyItem(View view, float zIndex, int adapterIndex) {
        this.view = view;
        this.zIndex = zIndex;
        this.adapterIndex = adapterIndex;
    }

    @Override
    public int hashCode() {
        return view.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof SkyItem && view.equals(((SkyItem) obj).view);
    }
}

package sky.library;

import android.view.View;

/**
 * @author sky
 * @version 1.0 on 2018-06-1 下午2:06
 */
public interface SkyTransformer {
    void transformAnimation(View view, float fraction, int cardWidth, int cardHeight,
                            int fromPosition, int toPosition, View toPositionView);

    void transformInterpolatedAnimation(View view, float fraction, int cardWidth, int cardHeight,
                                        int fromPosition, int toPosition);
}

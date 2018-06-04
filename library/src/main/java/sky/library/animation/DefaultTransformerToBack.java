package sky.library.animation;

import android.view.View;

import sky.library.SkyTransformer;
import sky.library.R;

/**
 * @author sky
 * @version 1.0 on 2018-06-1 下午2:06
 */
public class DefaultTransformerToBack implements SkyTransformer {
    @Override
    public void transformAnimation(View view, float fraction, int cardWidth, int cardHeight,
                                   int fromPosition, int toPosition) {
        float scale;
        int height = view.getResources().getDimensionPixelOffset(R.dimen.card_bottom_height);

        if (fromPosition == 0) {
            scale = (1 - 0.11f * fraction);

            view.setScaleX(scale);
            view.setScaleY(scale);


            view.setTranslationY(height * fraction);
        }
    }

    @Override
    public void transformInterpolatedAnimation(View view, float fraction, int cardWidth,
                                               int cardHeight, int fromPosition, int toPosition) {
    }
}

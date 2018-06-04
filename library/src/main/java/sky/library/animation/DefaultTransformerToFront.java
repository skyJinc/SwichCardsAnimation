package sky.library.animation;

import android.view.View;

import sky.library.SkyTransformer;
import sky.library.R;

/**
 * @author sky
 * @version 1.0 on 2018-06-1 下午2:06
 */
public class DefaultTransformerToFront implements SkyTransformer {
    @Override
    public void transformAnimation(View view, float fraction, int cardWidth, int cardHeight,
                                   int fromPosition, int toPosition,View toPositionView) {
        float scale;
        int height = view.getResources().getDimensionPixelOffset(R.dimen.card_bottom_height);

        if(fromPosition == 1){
            scale = (0.89f + 0.11f * fraction);

            view.setScaleX(scale);
            view.setScaleY(scale);

            view.setTranslationY((height * (1 - fraction)));
        }

    }

    @Override
    public void transformInterpolatedAnimation(View view, float fraction, int cardWidth,
                                               int cardHeight, int fromPosition, int toPosition) {
    }
}

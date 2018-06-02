package sky.library.animation;

import android.view.View;

import sky.library.SkyTransformer;

/**
 * @author sky
 * @version 1.0 on 2018-06-1 下午2:06
 */
public class DefaultTransformerRemove implements SkyTransformer {
    @Override
    public void transformAnimation(View view, float fraction, int cardWidth, int cardHeight, int fromPosition, int toPosition) {
        view.setTranslationY(cardHeight * fraction);
        view.setAlpha(1 - fraction);
    }

    @Override
    public void transformInterpolatedAnimation(View view, float fraction, int cardWidth, int cardHeight, int fromPosition, int toPosition) {

    }
}

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
                                   int fromPosition, int toPosition, View toPositionView) {
        float scale;
        int height = view.getResources().getDimensionPixelOffset(R.dimen.card_bottom_height);

        if (fromPosition == 0) {
            scale = (1 - 0.11f * fraction);

            view.setScaleX(scale);
            view.setScaleY(scale);

            int viewHeight = toPositionView.getHeight() - view.getHeight();

            if (viewHeight > 0) { // 比当前的大
                height += viewHeight;
                view.setTranslationY(height * fraction);
            } else if (viewHeight < 0) { //比当前的小
                viewHeight += height;
                view.setTranslationY(viewHeight * fraction);
            }else { //相等
                view.setTranslationY(height * fraction);
            }
        }
    }

    @Override
    public void transformInterpolatedAnimation(View view, float fraction, int cardWidth,
                                               int cardHeight, int fromPosition, int toPosition) {
    }
}

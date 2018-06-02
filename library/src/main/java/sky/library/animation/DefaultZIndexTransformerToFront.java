package sky.library.animation;

import sky.library.SkyItem;
import sky.library.SkyIndexTransformer;

/**
 * @author sky
 * @version 1.0 on 2018-06-1 下午2:06
 */
public class DefaultZIndexTransformerToFront implements SkyIndexTransformer {
    @Override
    public void transformAnimation(SkyItem card, float fraction, int cardWidth, int cardHeight, int fromPosition, int toPosition) {
        if (fraction < 0.05f) {
            card.zIndex = 1f + 0.01f * fromPosition;
        } else {
            card.zIndex = 1f + 0.01f * toPosition;
        }
    }

    @Override
    public void transformInterpolatedAnimation(SkyItem card, float fraction, int cardWidth, int cardHeight, int fromPosition, int toPosition) {

    }
}

package sky.library.animation;

import sky.library.SkyItem;
import sky.library.SkyIndexTransformer;

/**
 * @author sky
 * @version 1.0 on 2018-06-1 下午2:06
 */
public class DefaultZIndexTransformerCommon implements SkyIndexTransformer {
    @Override
    public void transformAnimation(SkyItem card, float fraction, int cardWidth, int cardHeight, int fromPosition, int toPosition) {
        card.zIndex = 1f + 0.01f * fromPosition + 0.01f * (toPosition - fromPosition) * fraction;
    }

    @Override
    public void transformInterpolatedAnimation(SkyItem card, float fraction, int cardWidth, int cardHeight, int fromPosition, int toPosition) {

    }
}

package sky.library;

/**
 * @author sky
 * @version 1.0 on 2018-06-1 下午2:06
 */
public interface SkyIndexTransformer {
    void transformAnimation(SkyItem card, float fraction, int cardWidth, int cardHeight, int
            fromPosition, int toPosition);

    void transformInterpolatedAnimation(SkyItem card, float fraction, int cardWidth, int
            cardHeight, int fromPosition, int toPosition);
}

package sky.library.animation;

import android.util.Log;
import android.view.View;

import sky.library.SkyTransformer;
import sky.library.R;

/**
 * @author sky
 * @version 1.0 on 2018-06-1 下午2:06
 */
public class DefaultTransformerAdd implements SkyTransformer {

    @Override
    public void transformAnimation(View view, float fraction, int cardWidth, int cardHeight, int fromPosition, int toPosition) {

        float scale = 0;

        Log.i("scale",scale+":"+fromPosition+":"+fraction+":"+(0.11f * fraction));

        if(fromPosition == 0){
            scale = (0.89f + 0.11f * fraction);

            view.setScaleX(scale);
            view.setScaleY(scale);
        }else {
            scale = 0.898f;

            view.setScaleX(scale);
            view.setScaleY(scale);

            int height = view.getResources().getDimensionPixelOffset(R.dimen.card_bottom_height);

            view.setTranslationY(height* fraction);
        }

        view.setAlpha(fraction);
    }

    @Override
    public void transformInterpolatedAnimation(View view, float fraction, int cardWidth, int cardHeight, int fromPosition, int toPosition) {

    }
}

package sky.library;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.BaseAdapter;

/**
 * @author sky
 * @version 1.0 on 2018-06-1 下午2:06
 */
public class SkySwitchView extends ViewGroup {
    /*
     * Three types of animation
     * ANIM_TYPE_FRONT:custom animation for chosen card, common animation for other cards
     * ANIM_TYPE_SWITCH:switch the position by custom animation of the first card and the chosen card
     * ANIM_TYPE_FRONT_TO_LAST:moving the first card to last position by custom animation, common animation for others
     */
    public static final int ANIM_TYPE_FRONT = 0, ANIM_TYPE_SWITCH = 1, ANIM_TYPE_FRONT_TO_LAST = 2;
    //cardHeight / cardWidth = CARD_SIZE_RATIO
    private static final float CARD_SIZE_RATIO = 0.8f;
    //cardHeight / cardWidth = mCardRatio
    private float mCardRatio = CARD_SIZE_RATIO;
    //animation helper
    private SkyAnimationHelper mAnimationHelper;
    //view adapter
    private BaseAdapter mAdapter;
    private int mCardWidth, mCardHeight;

    public SkySwitchView(@NonNull Context context) {
        this(context, null);
    }

    public SkySwitchView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SkySwitchView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
        setClickable(true);
    }

    private void init(Context context, AttributeSet attrs) {
        int animType = ANIM_TYPE_FRONT;
        int animDuration = SkyAnimationHelper.ANIM_DURATION;
        int animAddRemoveDuration = SkyAnimationHelper.ANIM_ADD_REMOVE_DURATION;
        int animAddRemoveDelay = SkyAnimationHelper.ANIM_ADD_REMOVE_DELAY;
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SkySwitchView);
            animType = ta.getInt(R.styleable.SkySwitchView_animType, ANIM_TYPE_FRONT);
            mCardRatio = ta.getFloat(R.styleable.SkySwitchView_cardRatio, CARD_SIZE_RATIO);
            animDuration = ta.getInt(R.styleable.SkySwitchView_animDuration, SkyAnimationHelper.ANIM_DURATION);
            animAddRemoveDuration = ta.getInt(R.styleable.SkySwitchView_animAddRemoveDuration,
                    SkyAnimationHelper.ANIM_ADD_REMOVE_DURATION);
            animAddRemoveDelay = ta.getInt(R.styleable.SkySwitchView_animAddRemoveDelay,
                    SkyAnimationHelper.ANIM_ADD_REMOVE_DELAY);
            ta.recycle();
        }
        mAnimationHelper = new SkyAnimationHelper(animType, animDuration, this);
        mAnimationHelper.setAnimAddRemoveDuration(animAddRemoveDuration);
        mAnimationHelper.setAnimAddRemoveDelay(animAddRemoveDelay);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        if (widthMode != MeasureSpec.EXACTLY || heightMode != MeasureSpec.EXACTLY) {
            int childCount = getChildCount();
            int childWidth = 0, childHeight = 0;
            for (int i = 0; i < childCount; i++) {
                View childView = getChildAt(i);
                childWidth = Math.max(childView.getMeasuredWidth(), childWidth);
                childHeight = Math.max(childView.getMeasuredHeight(), childHeight);
            }
            setMeasuredDimension((widthMode == MeasureSpec.EXACTLY) ? sizeWidth : childWidth,
                    (heightMode == MeasureSpec.EXACTLY) ? sizeHeight : childHeight);
        } else {
            setMeasuredDimension(sizeWidth, sizeHeight);
        }
        if (mCardWidth == 0 || mCardHeight == 0) {
            setCardSize(true);
        }
    }

    private void setCardSize(boolean resetAdapter) {
        mCardWidth = mCardWidth == 0 ? getMeasuredWidth() : mCardWidth;
        mCardHeight = mCardHeight == 0 ? (int) (mCardWidth * mCardRatio) : mCardHeight;
        mAnimationHelper.setCardSize(mCardWidth, mCardHeight);
        mAnimationHelper.initAdapterView(mAdapter, resetAdapter);
    }

    public void updatePositionCardSize(int cardWidth, int cardHeight, int position) {
        this.mCardWidth = cardWidth;
        this.mCardHeight = cardHeight;
        if (this.card(position) != null) {
            this.card(position).getLayoutParams().width = mCardWidth;
            this.card(position).getLayoutParams().height = mCardHeight;
        }
    }

    public void setCardSizeWidth(int cardWidth) {
        setCardSize(cardWidth, getMeasuredHeight(), true);
    }

    public void setCardSizeHeight(int cardHeight) {
        setCardSize(getMeasuredWidth(), cardHeight, true);
    }

    public void setCardSize(int cardWidth, int cardHeight, boolean resetAdapter) {
        mCardWidth = cardWidth;
        mCardHeight = cardHeight;
        mAnimationHelper.setCardSize(mCardWidth, mCardHeight);
        mAnimationHelper.initAdapterView(mAdapter, resetAdapter);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = getChildCount();
        int childWidth, childHeight;
        int childLeft, childTop, childRight, childBottom;
        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            childWidth = childView.getMeasuredWidth();
            childHeight = childView.getMeasuredHeight();
            childLeft = 0;
            childTop = 0;
            childRight = childLeft + childWidth;
            childBottom = childTop + childHeight;
            childView.layout(childLeft, childTop, childRight, childBottom);
        }
    }

    void addCardView(SkyItem card) {
        addView(getCardView(card));
    }

    void addCardView(SkyItem card, int position) {
        addView(getCardView(card), position);
    }

    private View getCardView(final SkyItem card) {
        View view = card.view;
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(mCardWidth,
                mCardHeight);
        view.setLayoutParams(layoutParams);
//        view.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                bringCardToFront(1);
//            }
//        });
        return view;
    }

    private void bringCardToFront(SkyItem card) {
        if (!isClickable()) {
            return;
        }
        mAnimationHelper.bringCardToFront(card);
    }


    public void bringCardToFront(int position) {
        mAnimationHelper.bringCardToFront(position);
    }

    public View card(int posiion) {
        if (mAnimationHelper.getItem(posiion) == null) {
            return null;
        }

        return mAnimationHelper.getItem(posiion).view;
    }


    public void setAdapter(BaseAdapter adapter) {
        this.mAdapter = adapter;
        mAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                mAnimationHelper.notifyDataSetChanged(mAdapter);
            }
        });
        mAnimationHelper.initAdapterView(adapter, true);
    }

    public void setTransformerToFront(SkyTransformer toFrontTransformer) {
        mAnimationHelper.setTransformerToFront(toFrontTransformer);
    }

    public void setTransformerToBack(SkyTransformer toBackTransformer) {
        mAnimationHelper.setTransformerToBack(toBackTransformer);
    }

    public void setCommonSwitchTransformer(SkyTransformer commonTransformer) {
        mAnimationHelper.setCommonSwitchTransformer(commonTransformer);
    }

    public void setTransformerCommon(SkyTransformer transformerCommon) {
        mAnimationHelper.setTransformerCommon(transformerCommon);
    }

    public void setZIndexTransformerToFront(SkyIndexTransformer zIndexTransformerToFront) {
        mAnimationHelper.setZIndexTransformerToFront(zIndexTransformerToFront);
    }

    public void setZIndexTransformerToBack(SkyIndexTransformer zIndexTransformerToBack) {
        mAnimationHelper.setZIndexTransformerToBack(zIndexTransformerToBack);
    }

    public void setZIndexTransformerCommon(SkyIndexTransformer zIndexTransformerCommon) {
        mAnimationHelper.setZIndexTransformerCommon(zIndexTransformerCommon);
    }

    public void setAnimInterpolator(Interpolator animInterpolator) {
        mAnimationHelper.setAnimInterpolator(animInterpolator);
    }

    public void setAnimType(int animType) {
        mAnimationHelper.setAnimType(animType);
    }

    public void setTransformerAnimAdd(SkyTransformer transformerAnimAdd) {
        mAnimationHelper.setTransformerAnimAdd(transformerAnimAdd);
    }

    public void setTransformerAnimRemove(SkyTransformer transformerAnimRemove) {
        mAnimationHelper.setTransformerAnimRemove(transformerAnimRemove);
    }

    void setAnimAddRemoveInterpolator(Interpolator animAddRemoveInterpolator) {
        mAnimationHelper.setAnimAddRemoveInterpolator(animAddRemoveInterpolator);
    }

    public void setCardSizeRatio(float cardSizeRatio) {
        this.mCardRatio = cardSizeRatio;
        setCardSize(false);
    }

    public boolean isAnimating() {
        return mAnimationHelper.isAnimating();
    }

    public void setCardAnimationListener(CardAnimationListener cardAnimationListener) {
        mAnimationHelper.setCardAnimationListener(cardAnimationListener);
    }

    public static interface CardAnimationListener {
        void onAnimationStart();

        void onAnimationEnd();
    }
}

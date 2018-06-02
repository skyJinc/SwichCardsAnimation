package sky.library;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.BaseAdapter;

import java.util.LinkedList;

import sky.library.animation.DefaultCommonTransformer;
import sky.library.animation.DefaultTransformerAdd;
import sky.library.animation.DefaultTransformerRemove;
import sky.library.animation.DefaultTransformerToBack;
import sky.library.animation.DefaultTransformerToFront;
import sky.library.animation.DefaultZIndexTransformerCommon;
import sky.library.animation.DefaultZIndexTransformerToFront;

/**
 * @author sky
 * @version 1.0 on 2018-06-1 下午2:06
 */
class SkyAnimationHelper implements Animator.AnimatorListener,
        ValueAnimator.AnimatorUpdateListener {
    //animation duration
    static final int ANIM_DURATION = 1000, ANIM_ADD_REMOVE_DELAY = 200,
            ANIM_ADD_REMOVE_DURATION = 500;
    //animation type
    private int mAnimType = SkySwitchView.ANIM_TYPE_FRONT;
    //animation duration
    private int mAnimDuration = ANIM_DURATION, mAnimAddRemoveDelay = ANIM_ADD_REMOVE_DELAY,
            mAnimAddRemoveDuration = ANIM_ADD_REMOVE_DURATION;
    //card container view
    private SkySwitchView mCardView;
    //card item list
    private LinkedList<SkyItem> mCards;
    //total card count
    private int mCardCount;
    //card width, card height
    //for judge Z index
    //    private ArrayList<CardItem> mCards4JudgeZIndex;
    //current card moving to back, current card moving to front
    private SkyItem mCardToBack, mCardToFront;
    //current card position moving to front, current card position moving to front
    private int mPositionToBack = 0, mPositionToFront = 0;
    private int mCardWidth, mCardHeight;
    //is doing animation now
    private boolean mIsAnim = false, mIsAddRemoveAnim = false;
    //animator
    private ValueAnimator mValueAnimator;
    //custom animation transformer for card moving to front, card moving to back, and common card
    private SkyTransformer mTransformerToFront, mTransformerToBack, mTransformerCommon;
    //custom animation transformer for card add and remove
    private SkyTransformer mTransformerAnimAdd, mTransformerAnimRemove;
    //custom Z index transformer for card moving to front, card moving to back, and common card
    private SkyIndexTransformer mZIndexTransformerToFront, mZIndexTransformerToBack, mZIndexTransformerCommon;
    //animation interpolator
    private Interpolator mAnimInterpolator, mAnimAddRemoveInterpolator;
    //view adapter needs to be notify while animation
    private BaseAdapter mTempAdapter;
    //current animation fraction
    private float mCurrentFraction = 1;
    //animation listener
    private SkySwitchView.CardAnimationListener mCardAnimationListener;

    SkyAnimationHelper(int mAnimType, int mAnimDuration, SkySwitchView infiniteCardView) {
        this.mAnimType = mAnimType;
        this.mAnimDuration = mAnimDuration;
        this.mCardView = infiniteCardView;
        initTransformer();
        initAnimator();
    }

    private void initTransformer() {
        mAnimInterpolator = new LinearInterpolator();
        mAnimAddRemoveInterpolator = new LinearInterpolator();
        mTransformerToFront = new DefaultTransformerToFront();
        mTransformerToBack = new DefaultTransformerToBack();
        mTransformerCommon = new DefaultCommonTransformer();
        mTransformerAnimAdd = new DefaultTransformerAdd();
        mTransformerAnimRemove = new DefaultTransformerRemove();
        mZIndexTransformerToFront = new DefaultZIndexTransformerToFront();
        mZIndexTransformerToBack = new DefaultZIndexTransformerCommon();
        mZIndexTransformerCommon = new DefaultZIndexTransformerCommon();
    }

    private void initAnimator() {
        mValueAnimator = ValueAnimator.ofFloat(0, 1).setDuration(mAnimDuration);
        mValueAnimator.addUpdateListener(this);
        mValueAnimator.addListener(this);
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        mCurrentFraction = (float) animation.getAnimatedValue();
        float fractionInterpolated = mCurrentFraction;
        if (mAnimInterpolator != null) {
            fractionInterpolated = mAnimInterpolator.getInterpolation(mCurrentFraction);
        }
        doAnimationBackToFront(mCurrentFraction, fractionInterpolated);
        doAnimationFrontToBack(mCurrentFraction, fractionInterpolated);
        doAnimationCommon(mCurrentFraction, fractionInterpolated);
        bringToFrontByZIndex();
    }

    private void doAnimationBackToFront(float fraction, float fractionInterpolated) {
        mTransformerToFront.transformAnimation(mCardToFront.view,
                fraction, mCardWidth, mCardHeight, mPositionToFront, 0);
        if (mAnimInterpolator != null) {
            mTransformerToFront.transformInterpolatedAnimation(mCardToFront.view,
                    fractionInterpolated, mCardWidth, mCardHeight, mPositionToFront, 0);
        }
        doAnimationZIndex(mZIndexTransformerToFront, mCardToFront, fraction, fractionInterpolated,
                mPositionToFront, 0);
    }


    private void doAnimationFrontToBack(float fraction, float fractionInterpolated) {
        if (mAnimType == SkySwitchView.ANIM_TYPE_FRONT) {
            return;
        }
        mTransformerToBack.transformAnimation(mCardToBack.view, fraction, mCardWidth,
                mCardHeight, 0, mPositionToBack);
        if (mAnimInterpolator != null) {
            mTransformerToBack.transformInterpolatedAnimation(mCardToBack.view,
                    fractionInterpolated, mCardWidth, mCardHeight, 0, mPositionToBack);
        }
        doAnimationZIndex(mZIndexTransformerToBack, mCardToBack, fraction, fractionInterpolated,
                0, mPositionToBack);
    }

    private void doAnimationCommon(float fraction, float fractionInterpolated) {
        if (mAnimType == SkySwitchView.ANIM_TYPE_FRONT) {
            for (int i = 0; i < mPositionToFront; i++) {
                SkyItem card = mCards.get(i);
                doAnimationCommonView(card.view, fraction, fractionInterpolated, i, i + 1);
                doAnimationZIndex(mZIndexTransformerCommon, card, fraction, fractionInterpolated,
                        i, i + 1);
            }
        } else if (mAnimType == SkySwitchView.ANIM_TYPE_FRONT_TO_LAST) {
            for (int i = mPositionToFront + 1; i < mCardCount; i++) {
                SkyItem card = mCards.get(i);
                doAnimationCommonView(card.view, fraction, fractionInterpolated, i, i - 1);
                doAnimationZIndex(mZIndexTransformerCommon, card, fraction, fractionInterpolated,
                        i, i - 1);
            }
        }
    }

    private void doAnimationCommonView(View view, float fraction, float fractionInterpolated, int
            fromPosition, int toPosition) {
        mTransformerCommon.transformAnimation(view, fraction, mCardWidth,
                mCardHeight, fromPosition, toPosition);
        if (mAnimInterpolator != null) {
            mTransformerCommon.transformInterpolatedAnimation(view, fractionInterpolated, mCardWidth,
                    mCardHeight, fromPosition, toPosition);
        }
    }

    private void doAnimationZIndex(SkyIndexTransformer transformer, SkyItem card, float fraction,
                                   float fractionInterpolated, int fromPosition, int toPosition) {
        transformer.transformAnimation(card, fraction, mCardWidth,
                mCardHeight, fromPosition, toPosition);
        if (mAnimInterpolator != null) {
            transformer.transformInterpolatedAnimation(card, fractionInterpolated, mCardWidth,
                    mCardHeight, fromPosition, toPosition);
        }
    }


    private void bringToFrontByZIndex() {
        if (mAnimType == SkySwitchView.ANIM_TYPE_FRONT) {
            for (int i = mPositionToFront - 1; i >= 0; i--) {
                SkyItem card = mCards.get(i);
                if (card.zIndex > mCardToFront.zIndex) {
                    mCardToFront.view.bringToFront();
                    mCardView.updateViewLayout(mCardToFront.view, mCardToFront.view.getLayoutParams());
                } else {
                    card.view.bringToFront();
                    mCardView.updateViewLayout(card.view, card.view.getLayoutParams());
                }
            }
        } else {

            boolean cardToFrontBrought = false;
            for (int i = mCardCount - 1; i > 0; i--) {
                SkyItem card = mCards.get(i);
                SkyItem cardPre = i > 1 ? mCards.get(i - 1) : null;
                boolean cardToBackBehindCardPre = cardPre == null ||
                        mCardToBack.zIndex > cardPre.zIndex;
                boolean bringCardToBackViewToFront = mCardToBack.zIndex < card.zIndex && cardToBackBehindCardPre;
                boolean cardToFrontBehindCardPre = cardPre == null ||
                        mCardToFront.zIndex > cardPre.zIndex;
                boolean bringCardToFrontViewToFront = mCardToFront.zIndex < card.zIndex && cardToFrontBehindCardPre;
                if (i != mPositionToFront) {
                    card.view.bringToFront();
                    mCardView.updateViewLayout(card.view, card.view.getLayoutParams());
                    if (bringCardToBackViewToFront) {
                        mCardToBack.view.bringToFront();
                        mCardView.updateViewLayout(mCardToBack.view, mCardToBack.view.getLayoutParams());
                    }
                    if (bringCardToFrontViewToFront) {
                        mCardToFront.view.bringToFront();
                        mCardView.updateViewLayout(mCardToFront.view, mCardToFront.view.getLayoutParams());
                        cardToFrontBrought = true;
                    }
                    if (bringCardToBackViewToFront && bringCardToFrontViewToFront &&
                            mCardToBack.zIndex < mCardToFront.zIndex) {
                        mCardToBack.view.bringToFront();
                        mCardView.updateViewLayout(mCardToBack.view, mCardToBack.view.getLayoutParams());
                    }
                } else {
                    if (cardToFrontBehindCardPre) {
                        mCardToFront.view.bringToFront();
                        mCardView.updateViewLayout(mCardToFront.view, mCardToFront.view.getLayoutParams());
                        cardToFrontBrought = true;
                        if (cardToBackBehindCardPre && mCardToBack.zIndex < mCardToFront.zIndex) {
                            mCardToBack.view.bringToFront();
                            mCardView.updateViewLayout(mCardToBack.view, mCardToBack.view.getLayoutParams());
                        }
                    }
                }
            }
            if (!cardToFrontBrought) {
                mCardToFront.view.bringToFront();
                mCardView.updateViewLayout(mCardToFront.view, mCardToFront.view.getLayoutParams());
            }
        }
    }

    @Override
    public void onAnimationStart(Animator animation) {
        mCurrentFraction = 0;
        if (mCardAnimationListener != null) {
            mCardAnimationListener.onAnimationStart();
        }
    }

    @Override
    public void onAnimationEnd(Animator animation) {
        if (mAnimType == SkySwitchView.ANIM_TYPE_FRONT) {
            //move the card moving to front to the first position
            mCards.remove(mPositionToFront);
            mCards.addFirst(mCardToFront);
        } else if (mAnimType == SkySwitchView.ANIM_TYPE_SWITCH) {
            //switch the position of the card moving to front and back
            mCards.remove(mPositionToFront);
            mCards.removeFirst();
            mCards.addFirst(mCardToFront);
            mCards.add(mPositionToFront, mCardToBack);
        } else {
            //moving the first position card to last
            mCards.remove(mPositionToFront);
            mCards.removeFirst();
            mCards.addFirst(mCardToFront);
            mCards.addLast(mCardToBack);
        }
        mPositionToFront = 0;
        mPositionToBack = 0;
        mCurrentFraction = 1;
        mIsAnim = false;
        if (mTempAdapter != null) {
            notifyDataSetChanged(mTempAdapter);
        }
        if (mCardAnimationListener != null) {
            mCardAnimationListener.onAnimationEnd();
        }
    }

    @Override
    public void onAnimationCancel(Animator animation) {

    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }

    void initAdapterView(BaseAdapter adapter, boolean reset) {
        if (mCardWidth > 0 && mCardHeight > 0) {
            if (mCards == null) {
                mCardView.removeAllViews();
                firstSetAdapter(adapter);
            } else if (reset || mCards.size() != adapter.getCount()) {
                resetAdapter(adapter);
            } else {
                notifySetAdapter(adapter);
            }
        }
    }

    /**
     * reset adapter view
     *
     * @param adapter adapter
     */
    private void resetAdapter(BaseAdapter adapter) {
        if (mTransformerAnimRemove == null) {
            mCardView.removeAllViews();
            firstSetAdapter(adapter);
        } else {
            mIsAddRemoveAnim = true;
            for (int i = 0; i < mCardCount; i++) {
                SkyItem cardItem = mCards.get(i);
                showAnimRemove(cardItem.view, mAnimAddRemoveDelay * i, i, i == mCardCount - 1, adapter);
            }
        }
    }

    private void showAnimRemove(final View view, int delay, final int position,
                                final boolean isLast, final BaseAdapter adapter) {
        final ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1).setDuration(mAnimAddRemoveDuration);
        valueAnimator.setStartDelay(delay);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float fraction = (float) animation.getAnimatedValue();
                mTransformerAnimRemove.transformAnimation(view, fraction, mCardWidth, mCardHeight,
                        position, position);
                if (mAnimAddRemoveInterpolator != null) {
                    mTransformerAnimRemove.transformInterpolatedAnimation(view,
                            mAnimAddRemoveInterpolator.getInterpolation(fraction),
                            mCardWidth, mCardHeight, position, position);
                }
            }
        });
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(View.GONE);
                if (isLast) {
                    mIsAddRemoveAnim = false;
                    mCardView.removeAllViews();
                    if (mTempAdapter != null) {
                        notifyDataSetChanged(mTempAdapter);
                    } else {
                        firstSetAdapter(adapter);
                    }
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        mCardView.post(new Runnable() {
            @Override
            public void run() {
                valueAnimator.start();
            }
        });
    }

    private void firstSetAdapter(BaseAdapter adapter) {
        if (mTransformerAnimAdd != null) {
            mIsAddRemoveAnim = true;
        }
        mCards = new LinkedList<>();
        mCardCount = adapter.getCount();
        for (int i = mCardCount - 1; i >= 0; i--) {
            View child = adapter.getView(i, null, mCardView);
            SkyItem cardItem = new SkyItem(child, 0, i);
            mCardView.addCardView(cardItem);
            mCards.addFirst(cardItem);
            child.setVisibility(View.INVISIBLE);
            showAnimAdd(child, i * mAnimAddRemoveDelay, i, i == mCardCount - 1);
        }
    }

    private void showAnimAdd(final View view, int delay, final int position, final boolean isLast) {
        if (mTransformerAnimAdd == null) {
            return;
        }
        final ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1).setDuration(mAnimAddRemoveDuration);
        valueAnimator.setStartDelay(delay);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float fraction = (float) animation.getAnimatedValue();
                mTransformerAnimAdd.transformAnimation(view, fraction, mCardWidth, mCardHeight,
                        position, position);
                if (mAnimAddRemoveInterpolator != null) {
                    mTransformerAnimAdd.transformInterpolatedAnimation(view,
                            mAnimAddRemoveInterpolator.getInterpolation(fraction),
                            mCardWidth, mCardHeight, position, position);
                }
            }
        });
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (isLast) {
                    mIsAddRemoveAnim = false;
                    if (mTempAdapter != null) {
                        notifyDataSetChanged(mTempAdapter);
                    }
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        mCardView.post(new Runnable() {
            @Override
            public void run() {
                valueAnimator.start();
            }
        });
    }

    private void notifySetAdapter(BaseAdapter adapter) {
        mCardCount = adapter.getCount();
        for (int i = 0; i < mCardCount; i++) {
            SkyItem cardItem = mCards.get(i);
            View child = adapter.getView(cardItem.adapterIndex, cardItem.view, mCardView);
            if (child != cardItem.view) {
                if (cardItem.view != null) {
                    mCardView.removeView(cardItem.view);
                }
                cardItem.view = child;
                mCardView.addCardView(cardItem, i);
                mZIndexTransformerCommon.transformAnimation(cardItem, mCurrentFraction, mCardWidth, mCardHeight,
                        i, i);
                mTransformerCommon.transformAnimation(child, mCurrentFraction, mCardWidth, mCardHeight, i, i);
            }
        }
        for (int i = mCardCount - 1; i >= 0; i--) {
            mCards.get(i).view.bringToFront();
            mCardView.updateViewLayout(mCards.get(i).view, mCards.get(i).view.getLayoutParams());
        }
    }

    void notifyDataSetChanged(BaseAdapter adapter) {
        if (mIsAnim || mIsAddRemoveAnim) {
            mTempAdapter = adapter;
        } else {
            mTempAdapter = null;
            initAdapterView(adapter, false);
        }
    }

    void bringCardToFront(SkyItem card) {
        if (mCards == null || mTransformerCommon == null || mTransformerToFront ==
                null || mTransformerToBack == null) {
            return;
        }
        int position = mCards.indexOf(card);
        bringCardToFront(position);
    }

    void bringCardToFront(int position) {
        if (position >= 0 && position != mPositionToFront && !mIsAnim && !mIsAddRemoveAnim) {
            mPositionToFront = position;
            //if the animation type is not ANIM_TYPE_SWITCH, the card to back post is the last
            // position
            mPositionToBack = mAnimType == SkySwitchView.ANIM_TYPE_SWITCH ? mPositionToFront :
                    (mCardCount - 1);
            mCardToBack = mCards.getFirst();
            mCardToFront = mCards.get(mPositionToFront);
            if (mValueAnimator.isRunning()) {
                mValueAnimator.end();
            }
            mIsAnim = true;
            mValueAnimator.start();
        }
    }

    void setCardSize(int cardWidth, int cardHeight) {
        this.mCardWidth = cardWidth;
        this.mCardHeight = cardHeight;
    }

    void setTransformerToFront(SkyTransformer toFrontTransformer) {
        if (mIsAnim || mIsAddRemoveAnim) {
            return;
        }
        this.mTransformerToFront = toFrontTransformer;
    }

    void setTransformerToBack(SkyTransformer toBackTransformer) {
        if (mIsAnim || mIsAddRemoveAnim) {
            return;
        }
        this.mTransformerToBack = toBackTransformer;
    }

    void setCommonSwitchTransformer(SkyTransformer commonTransformer) {
        if (mIsAnim || mIsAddRemoveAnim) {
            return;
        }
        this.mTransformerCommon = commonTransformer;
    }

    void setTransformerCommon(SkyTransformer transformerCommon) {
        if (mIsAnim || mIsAddRemoveAnim) {
            return;
        }
        this.mTransformerCommon = transformerCommon;
    }

    void setZIndexTransformerToFront(SkyIndexTransformer zIndexTransformerToFront) {
        if (mIsAnim || mIsAddRemoveAnim) {
            return;
        }
        this.mZIndexTransformerToFront = zIndexTransformerToFront;
    }

    void setZIndexTransformerToBack(SkyIndexTransformer zIndexTransformerToBack) {
        if (mIsAnim || mIsAddRemoveAnim) {
            return;
        }
        this.mZIndexTransformerToBack = zIndexTransformerToBack;
    }

    void setZIndexTransformerCommon(SkyIndexTransformer zIndexTransformerCommon) {
        if (mIsAnim || mIsAddRemoveAnim) {
            return;
        }
        this.mZIndexTransformerCommon = zIndexTransformerCommon;
    }

    void setAnimInterpolator(Interpolator animInterpolator) {
        if (mIsAnim || mIsAddRemoveAnim) {
            return;
        }
        this.mAnimInterpolator = animInterpolator;
    }

    void setAnimType(int animType) {
        if (mIsAnim || mIsAddRemoveAnim) {
            return;
        }
        this.mAnimType = animType;
    }

    void setTransformerAnimAdd(SkyTransformer transformerAnimAdd) {
        if (mIsAnim || mIsAddRemoveAnim) {
            return;
        }
        this.mTransformerAnimAdd = transformerAnimAdd;
    }

    void setTransformerAnimRemove(SkyTransformer transformerAnimRemove) {
        if (mIsAnim || mIsAddRemoveAnim) {
            return;
        }
        this.mTransformerAnimRemove = transformerAnimRemove;
    }

    void setAnimAddRemoveInterpolator(Interpolator animAddRemoveInterpolator) {
        if (mIsAnim || mIsAddRemoveAnim) {
            return;
        }
        this.mAnimAddRemoveInterpolator = animAddRemoveInterpolator;
    }

    void setAnimAddRemoveDelay(int animAddRemoveDelay) {
        if (mIsAnim || mIsAddRemoveAnim) {
            return;
        }
        this.mAnimAddRemoveDelay = animAddRemoveDelay;
    }

    void setAnimAddRemoveDuration(int animAddRemoveDuration) {
        if (mIsAnim || mIsAddRemoveAnim) {
            return;
        }
        this.mAnimAddRemoveDuration = animAddRemoveDuration;
    }

    boolean isAnimating() {
        return mIsAnim || mIsAddRemoveAnim;
    }

    void setCardAnimationListener(SkySwitchView.CardAnimationListener cardAnimationListener) {
        this.mCardAnimationListener = cardAnimationListener;
    }
}

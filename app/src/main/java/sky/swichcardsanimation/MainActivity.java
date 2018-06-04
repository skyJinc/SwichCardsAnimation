package sky.swichcardsanimation;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;


import sky.library.SkySwitchView;
import sky.library.SkyTransformer;
import sky.library.animation.DefaultCommonTransformer;
import sky.library.animation.DefaultTransformerAdd;
import sky.library.animation.DefaultTransformerToBack;
import sky.library.animation.DefaultTransformerToFront;
import sky.library.animation.DefaultZIndexTransformerCommon;

public class MainActivity extends AppCompatActivity {
    private SkySwitchView mCardView;
    private BaseAdapter mAdapter1;


    int selectIndexTicket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mCardView = findViewById(R.id.view);
        mAdapter1 = new MyAdapter();
        mCardView.setAdapter(mAdapter1);
        mCardView.setCardAnimationListener(new SkySwitchView.CardAnimationListener() {
            @Override
            public void onAnimationStart() {


//                Toast.makeText(MainActivity.this, "开始动画", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAnimationEnd() {

                selectIndexTicket = selectIndexTicket == 0 ? 1 : 0;
//                Toast.makeText(MainActivity.this, "结束动画", Toast.LENGTH_SHORT).show();
            }
        });
        initButton();
        setStyle1();
    }

    private void initButton() {
        findViewById(R.id.go).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, OneActivity.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCardView.bringCardToFront(mAdapter1.getCount() - 1);
            }
        });
        findViewById(R.id.change).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCardView.isAnimating()) {
                    return;
                }
                Fragment fragment = MainActivity.this.getSupportFragmentManager().findFragmentByTag("blankFragment");
                if (fragment != null) {
                    MainActivity.this.getSupportFragmentManager().beginTransaction().remove(fragment).commitAllowingStateLoss();
                }
                fragment = MainActivity.this.getSupportFragmentManager().findFragmentByTag("hotelFragment");
                if (fragment != null) {
                    MainActivity.this.getSupportFragmentManager().beginTransaction().remove(fragment).commitAllowingStateLoss();
                }

                mCardView.setAdapter(mAdapter1);
            }
        });
    }

    private void setStyle1() {

        final int bottomPadding = getResources().getDimensionPixelOffset(R.dimen.card_bottom_height);
        final int ticketPadding = getResources().getDimensionPixelOffset(R.dimen.item_bottom);
        final int ticket = getResources().getDimensionPixelOffset(R.dimen.item_ticket);
        final int hotel = getResources().getDimensionPixelOffset(R.dimen.item_hotel);
        final int maxTicket = getResources().getDimensionPixelOffset(R.dimen.ticket_max);
        final int maxHotel = getResources().getDimensionPixelOffset(R.dimen.hotel_max);
        final float maxRatioTicket = 1.55f;

        final int selectTicket = (ticket - hotel) + bottomPadding;
        final int selectHotel = (ticket - hotel) - ticketPadding;

        selectIndexTicket = 0;
        mCardView.setCardSizeRatio(maxRatioTicket);

        mCardView.setClickable(true);
        mCardView.setAnimType(SkySwitchView.ANIM_TYPE_SWITCH);
        mCardView.setAnimInterpolator(new LinearInterpolator());
        mCardView.setCommonSwitchTransformer(new DefaultCommonTransformer());
        mCardView.setZIndexTransformerToBack(new DefaultZIndexTransformerCommon());
        mCardView.setTransformerToFront(new SkyTransformer() {
            @Override
            public void transformAnimation(View view, float fraction, int cardWidth, int cardHeight, int fromPosition, int toPosition) {
                float scale;

                if (fromPosition == 1) {
                    scale = (0.89f + 0.11f * fraction);

                    view.setScaleX(scale);
                    view.setScaleY(scale);

                    if (selectIndexTicket == 0) {
                        view.setTranslationY((selectTicket * (1 - fraction)));
                        mCardView.getLayoutParams().height = (int) (maxHotel + (maxTicket - maxHotel) * (1 - fraction));
                    } else {
                        view.setTranslationY(-selectHotel * (1 - fraction));
                        mCardView.getLayoutParams().height = (int) (maxHotel + (maxTicket - maxHotel) * fraction);
                    }


                    if (fraction == 1.0f) {
                        view.invalidate();
                    }
                }

                if (fromPosition == 0) {
                    View fromView = view.findViewById(R.id.frame_bottom);
                    fromView.setVisibility(View.GONE);
                }


            }

            @Override
            public void transformInterpolatedAnimation(View view, float fraction, int cardWidth, int cardHeight, int fromPosition, int toPosition) {

            }
        });
        mCardView.setTransformerAnimAdd(new SkyTransformer() {
            @Override
            public void transformAnimation(View view, float fraction, int cardWidth, int cardHeight, int fromPosition, int toPosition) {
                float scale;

                if (fromPosition == 0) {
                    scale = (0.89f + 0.11f * fraction);

                    view.setScaleX(scale);
                    view.setScaleY(scale);
                } else {
                    scale = 0.898f;

                    view.setScaleX(scale);
                    view.setScaleY(scale);

                    if (selectIndexTicket == 0) {
                        view.setTranslationY(selectTicket * fraction);
                    } else {
                        view.setTranslationY(-selectHotel * fraction);
                    }
                }

                view.setAlpha(fraction);
            }

            @Override
            public void transformInterpolatedAnimation(View view, float fraction, int cardWidth, int cardHeight, int fromPosition, int toPosition) {

            }
        });
        mCardView.setTransformerToBack(new SkyTransformer() {
            @Override
            public void transformAnimation(View view, float fraction, int cardWidth, int cardHeight, int fromPosition, int toPosition) {
                float scale;
                if (fromPosition == 0) {
                    scale = (1 - 0.11f * fraction);

                    view.setScaleX(scale);
                    view.setScaleY(scale);

                    //缩小
                    if (selectIndexTicket == 0) {
                        view.setTranslationY(-selectHotel * fraction);
                    } else {
                        view.setTranslationY(selectTicket * fraction);
                    }

                    View toView = mCardView.card(toPosition);
                    toView.findViewById(R.id.frame_bottom).setVisibility(View.GONE);

                    View fromView = view.findViewById(R.id.frame_bottom);
                    if (fraction > 0.5) {
                        fromView.setVisibility(View.VISIBLE);
                        fromView.setAlpha(fraction);
                    }
                }
            }

            @Override
            public void transformInterpolatedAnimation(View view, float fraction, int cardWidth, int cardHeight, int fromPosition, int toPosition) {

            }
        });

    }


    private class MyAdapter extends BaseAdapter {

        MyAdapter() {
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public Integer getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {


                switch (position) {
                    case 0:
                        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card, parent, false);
                        break;
                    case 1:
                        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card_one, parent, false);
                        break;
                }
            }

            convertView.findViewById(R.id.frame_bottom).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mCardView.bringCardToFront(1);
                }
            });

            switch (position) {
                case 0:
                    convertView.findViewById(R.id.frame_bottom).setVisibility(View.GONE);
                    break;
                case 1:
                    convertView.findViewById(R.id.frame_bottom).setVisibility(View.VISIBLE);
                    break;
            }
            return convertView;
        }
    }
}

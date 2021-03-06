package sky.swichcardsanimation;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;


import sky.library.SkySwitchView;
import sky.library.animation.DefaultTransformerToBack;
import sky.library.animation.DefaultTransformerToFront;
import sky.library.animation.DefaultZIndexTransformerCommon;

public class OneActivity extends AppCompatActivity {
    private SkySwitchView mCardView;
    private BaseAdapter mAdapter1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one);
        mCardView = findViewById(R.id.view);
        mAdapter1 = new MyAdapter();
        mCardView.setAdapter(mAdapter1);
        mCardView.setCardAnimationListener(new SkySwitchView.CardAnimationListener() {
            @Override
            public void onAnimationStart() {
                Toast.makeText(OneActivity.this, "开始动画", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAnimationEnd() {
                Toast.makeText(OneActivity.this, "结束动画", Toast.LENGTH_SHORT).show();
            }
        });
        initButton();
    }

    private void initButton() {
        findViewById(R.id.next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setStyle1();
                mCardView.bringCardToFront(mAdapter1.getCount() - 1);
            }
        });
        findViewById(R.id.change).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCardView.isAnimating()) {
                    return;
                }
                mCardView.setAdapter(mAdapter1);
            }
        });
    }

    private void setStyle1() {
        mCardView.setClickable(true);
        mCardView.setAnimType(SkySwitchView.ANIM_TYPE_SWITCH);
        mCardView.setAnimInterpolator(new LinearInterpolator());
        mCardView.setTransformerToFront(new DefaultTransformerToFront());
        mCardView.setTransformerToBack(new DefaultTransformerToBack());
        mCardView.setZIndexTransformerToBack(new DefaultZIndexTransformerCommon());
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
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout
                        .item_one, parent, false);
            }

            TextView textView = convertView.findViewById(R.id.tv_content);
            switch (position) {
                case 0:
                    textView.setText("第一页");
                    break;
                case 1:
                    textView.setText("第二页");
                    break;
            }
            return convertView;
        }
    }
}

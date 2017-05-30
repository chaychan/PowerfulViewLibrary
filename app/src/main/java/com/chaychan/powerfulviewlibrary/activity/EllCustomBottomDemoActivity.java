package com.chaychan.powerfulviewlibrary.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chaychan.powerfulviewlibrary.R;
import com.chaychan.powerfulviewlibrary.bean.ProductBean;
import com.chaychan.viewlib.ExpandableLinearLayout;
import com.nineoldandroids.animation.ObjectAnimator;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * ExpandableLinearLayout使用自定义底部
 */
public class EllCustomBottomDemoActivity extends AppCompatActivity {

    @Bind(R.id.ell_product)
    ExpandableLinearLayout ellProduct;
    @Bind(R.id.tv_tip)
    TextView tvTip;
    @Bind(R.id.iv_arrow)
    ImageView ivArrow;
    @Bind(R.id.rl_bottom)
    RelativeLayout rlBottom;

    private String[] imgUrls = new String[]{
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1496728066&di=e5669ad80a241da52b03301ee0ba2749&imgtype=jpg&er=1&src=http%3A%2F%2Fimg.taopic.com%2Fuploads%2Fallimg%2F121017%2F240425-12101H2202646.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1496728145&di=c2ece04e1445eaf91fe3f3bf12ad1080&imgtype=jpg&er=1&src=http%3A%2F%2Fimg1.qunarzz.com%2Ftravel%2Fd6%2F1610%2F33%2F21ce9c91e70ab7b5.jpg_r_720x480x95_b2bcd2c5.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1496728182&di=1e06ea8b74863155b9d52736093beda8&imgtype=jpg&er=1&src=http%3A%2F%2Fe.hiphotos.baidu.com%2Fbainuo%2Fcrop%3D0%2C0%2C470%2C285%3Bw%3D470%3Bq%3D79%2Fsign%3Da8aa38e3b73533fae1f9c96e95e3d12f%2F6c224f4a20a44623b885148f9e22720e0df3d794.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1496133522433&di=1132cb36274a205f8ce30e21f47a37ee&imgtype=0&src=http%3A%2F%2Fi3.s2.dpfile.com%2Fpc%2Fb68a2a4316ae56373e83ce65ad7dfada%2528249x249%2529%2Fthumb.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1496728305&di=444bfe10c434c09043855e7a6a7f8ace&imgtype=jpg&er=1&src=http%3A%2F%2Fe.hiphotos.baidu.com%2Fbainuo%2Fcrop%3D0%2C0%2C470%2C285%3Bw%3D470%3Bq%3D99%2Fsign%3D65498f21374e251ff6b8beb89ab6e527%2F0df3d7ca7bcb0a46d662a6226c63f6246b60af6c.jpg"
    };

    private String[] names = new String[]{
            "炒河粉",
            "炒米粉",
            "隆江猪脚饭",
            "烧鸭饭",
            "叉烧饭"
    };

    private String[] intros = new String[]{
            "好吃又不腻",
            "精选上等米粉，绝对好吃",
            "隆江猪脚饭，肥而不腻，入口香爽，深受广东人民的喜爱",
            "简单而美味，充满烧腊香味",
            "色香味俱全"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_ell_custom_bottom_demo);
        ButterKnife.bind(this);


        ellProduct.removeAllViews();//清除所有的子View（避免重新刷新数据时重复添加）
        //添加数据
        for (int i = 0; i < 5; i++) {
            View view = View.inflate(this, R.layout.item_product, null);
            ProductBean productBean = new ProductBean(imgUrls[i], names[i], intros[i], "12.00");
            ViewHolder viewHolder = new ViewHolder(view, productBean);
            viewHolder.refreshUI();
            ellProduct.addItem(view);//添加子条目
        }

        ellProduct.setOnStateChangeListener(new ExpandableLinearLayout.OnStateChangeListener() {
            @Override
            public void onStateChanged(boolean isExpanded) {
                doArrowAnim(isExpanded);//根据状态箭头旋转
                //根据状态更改文字提示
                if (isExpanded) {
                    //展开
                    tvTip.setText("点击收起");
                } else {
                    tvTip.setText("点击展开");
                }
            }
        });

        rlBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ellProduct.toggle();
            }
        });
    }

    // 箭头的动画
    private void doArrowAnim(boolean isExpand) {
        if (isExpand) {
            // 当前是展开，箭头由下变为上
            ObjectAnimator.ofFloat(ivArrow, "rotation", 0, 180).start();
        } else {
            // 当前是收起，箭头由上变为下
            ObjectAnimator.ofFloat(ivArrow, "rotation", -180, 0).start();
        }
    }


    class ViewHolder {
        @Bind(R.id.iv_img)
        ImageView ivImg;
        @Bind(R.id.tv_name)
        TextView tvName;
        @Bind(R.id.tv_intro)
        TextView tvIntro;
        @Bind(R.id.tv_price)
        TextView tvPrice;

        ProductBean productBean;

        public ViewHolder(View view, ProductBean productBean) {
            ButterKnife.bind(this, view);
            this.productBean = productBean;
        }

        private void refreshUI() {
            Glide.with(EllCustomBottomDemoActivity.this)
                    .load(productBean.getImg())
                    .placeholder(R.mipmap.ic_default)
                    .into(ivImg);
            tvName.setText(productBean.getName());
            tvIntro.setText(productBean.getIntro());
            tvPrice.setText("￥" + productBean.getPrice());
        }
    }
}

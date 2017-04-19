package com.chaychan.powerfulviewlibrary.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.chaychan.powerfulviewlibrary.R;
import com.chaychan.viewlib.expandabletextview.ExpandableTextView;

/**
 * 可展开的TextView演示
 *
 */
public class ExpandableTextViewDemoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expandable_text_view_demo);

        ExpandableTextView tv = (ExpandableTextView) findViewById(R.id.tv);
        tv.setText("黄家驹（1962.6.10~1993.6.30），中国香港男歌手、原创音乐人、吉他手、摇滚乐队Beyond主唱及创队成员。[1-2]" +
                "1983年以歌曲《大厦》出道，并担任Beyond乐队的主唱[3]  。1988年凭借专辑《秘密警察》在香港歌坛获得关注[4]  ，其中由黄家驹创作的歌曲《大地》获得十大劲歌金曲奖[5]  。1989年黄家驹作曲的歌曲《真的爱你》获得十大劲歌金曲奖以及十大中文金曲奖[6]  。1990年凭借歌曲《光辉岁月》获得十大劲歌金曲最佳填词奖[7]  。1991年在香港红馆举行“Beyond生命接触演唱会”。1992年赴日本发展歌唱事业。1993年黄家驹创作的歌曲《海阔天空》获得十大中文金曲奖以及叱咤乐坛流行榜我最喜爱的本地创作歌曲大奖[8]  ；6月24日，在日本参与某综艺节目期间意外受伤；6月30日，黄家驹逝世，终年31岁[9]  ；同年，被追颁十大中文金曲奖“无休止符纪念奖”[10]  。");
    }
}

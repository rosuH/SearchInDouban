package me.rosuh.searchindouban;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;
/**
 * 这个类是用来显示 App 作者信息的；
 * 此类使用的是 Activity 继承 Dialog 的主题，来达到显示小窗口的目的
 * * @author rosuh 2018-4-24 10:40:23
 * @version 2.1
 * @since 1.8
 */

public class AboutPageDialog extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_page_dialog);
        setTitle(R.string.about_page);
    }
}

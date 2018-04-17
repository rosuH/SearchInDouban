package me.rosuh.searchindouban;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;

public class AboutPageDialog extends AppCompatActivity {
    private CircleImageView mLogoImageView;
    private TextView mHelloWords;
    private TextView mHomepageTextView;
    private TextView mBlogPageTextView;
    private TextView mProjectPageTextView;
    private TextView mFeedbackTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_page_dialog);

        mLogoImageView = findViewById(R.id.logo_image_view_about);
        mHelloWords = findViewById(R.id.hello_text_view_about);
        mBlogPageTextView = findViewById(R.id.blog_page_text_view);
        mProjectPageTextView = findViewById(R.id.project_page_text_view);
        mFeedbackTextView = findViewById(R.id.feedback_text_view_about);
        setTitle(R.string.about_page);
    }
}

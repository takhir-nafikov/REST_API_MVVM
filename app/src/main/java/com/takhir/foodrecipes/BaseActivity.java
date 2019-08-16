package com.takhir.foodrecipes;

import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

public abstract class BaseActivity extends AppCompatActivity {

    private ProgressBar progressBar;

    @Override
    public void setContentView(int layoutResID) {

        ConstraintLayout constraintLayout =
                (ConstraintLayout) getLayoutInflater().inflate(R.layout.activity_base, null);
        FrameLayout frameLayout = constraintLayout.findViewById(R.id.activity_content);
        progressBar = constraintLayout.findViewById(R.id.progress_bar);

        getLayoutInflater().inflate(layoutResID, frameLayout, true);

        super.setContentView(layoutResID);
    }

    public void showProgressBar(boolean visibility) {
        progressBar.setVisibility(visibility ? View.VISIBLE : View.INVISIBLE);
    }
}

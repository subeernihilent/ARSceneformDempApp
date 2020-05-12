package com.google.ar.sceneform.samples.gltf;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.RelativeLayout;

public class MainActivity extends AppCompatActivity {

    Button viewButton;
    RelativeLayout renderableLayout;
    private float layoutWidthInDp;
    private float layoutHeightInDp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewButton = findViewById(R.id.bt_view_ar);
        renderableLayout = findViewById(R.id.rl_renderable);
        renderableLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                layoutWidthInDp = renderableLayout.getWidth() / getResources().getDisplayMetrics().density;
                layoutHeightInDp = renderableLayout.getHeight() / getResources().getDisplayMetrics().density;
            }
        });
        viewButton.setOnClickListener(v -> {
            navigateToAR();
        });
    }

    private void navigateToAR() {
        Intent intent = new Intent(MainActivity.this, LayoutViewActivity.class);
        Log.d("dimen", layoutWidthInDp + " - " + layoutHeightInDp);
        intent.putExtra("width", layoutWidthInDp);
        intent.putExtra("height", layoutHeightInDp);
        startActivity(intent);
    }
}

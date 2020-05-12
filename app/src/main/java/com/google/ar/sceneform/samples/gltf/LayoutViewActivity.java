package com.google.ar.sceneform.samples.gltf;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.Toast;

import com.google.ar.core.Anchor;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.DpToMetersViewSizer;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.ux.BaseTransformableNode;
import com.google.ar.sceneform.ux.SelectionVisualizer;
import com.google.ar.sceneform.ux.TransformableNode;

import java.lang.ref.WeakReference;

public class LayoutViewActivity extends AppCompatActivity {
    private static final String TAG = LayoutViewActivity.class.getSimpleName();
    private static final double MIN_OPENGL_VERSION = 3.0;

    private MyARFragment arFragment;
    private Renderable renderable;
    private float layoutWidthInDp;
    private float layoutHeightInDp;

    private static final Quaternion ROTATION_ANGLE = Quaternion.axisAngle(new Vector3(-1f, 0, 0), -90f);

    @Override
    @SuppressWarnings({"AndroidApiChecker", "FutureReturnValueIgnored"})
    // CompletableFuture requires api level 24
    // FutureReturnValueIgnored is not valid
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!checkIsSupportedDeviceOrFinish(this)) {
            return;
        }

        setContentView(R.layout.activity_ux);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            layoutWidthInDp = bundle.getFloat("width");
            layoutHeightInDp = bundle.getFloat("height");
        }
        arFragment = (MyARFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);

        WeakReference<LayoutViewActivity> weakActivity = new WeakReference<>(this);

        createViewRenderable(weakActivity);

        arFragment.setOnTapArPlaneListener(
                (HitResult hitResult, Plane plane, MotionEvent motionEvent) -> {
                    Toast.makeText(this, plane.getType() + " plane is detected.", Toast.LENGTH_LONG).show();
                    addToScene(hitResult, plane);
                });

    }

    private void addToScene(HitResult hitResult, Plane plane) {
        if (renderable == null) {
            return;
        }

        // Create the Anchor.
        Anchor anchor = hitResult.createAnchor();
        AnchorNode anchorNode = new AnchorNode(anchor);
        anchorNode.setParent(arFragment.getArSceneView().getScene());

        // Node.
        Node node = new Node();
        node.setRenderable(renderable);

        // To remove the visual selection at image bottom
        arFragment.getTransformationSystem().setSelectionVisualizer(new CustomVisualizer());

        // Create the transformable node and add it to the anchor.
        TransformableNode transformableNode = new TransformableNode(arFragment.getTransformationSystem());

        //To disable scaling
        transformableNode.getScaleController().setEnabled(false);
        node.setLocalRotation(ROTATION_ANGLE);
        node.setLookDirection(new Vector3(0, 10f, 0));
        transformableNode.setParent(anchorNode);
        node.setParent(transformableNode);
        transformableNode.select();
    }

    private int getDpPerMeters(float dimenInDp, float dimenInInch) {
        return (int) (dimenInDp / (dimenInInch * 0.0254));
    }

    private void createViewRenderable(WeakReference<LayoutViewActivity> weakActivity) {

        Log.d("renderable_dimen", layoutWidthInDp + " - " + layoutHeightInDp);
        float renderableWidth = 8.4f;
        float renderableHeight = 14f;

        ViewRenderable.builder()
                .setView(this, R.layout.twodlayout)
//                .setSizer(new FixedHeightViewSizer(1f))
//                .setSizer(new FixedWidthViewSizer(0.6f))
                .setSizer(new DpToMetersViewSizer(getDpPerMeters(layoutHeightInDp, renderableHeight)))
                .build()
                .thenAccept(renderable -> {
                    LayoutViewActivity activity = weakActivity.get();
                    if (activity != null) {
                        activity.renderable = renderable;
                    }
                })
                .exceptionally(
                        throwable -> {
                            Toast toast =
                                    Toast.makeText(this, "Unable to load Tiger renderable", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return null;
                        });
    }

    /**
     * Returns false and displays an error message if Sceneform can not run, true if Sceneform can run
     * on this device.
     *
     * <p>Sceneform requires Android N on the device as well as OpenGL 3.0 capabilities.
     *
     * <p>Finishes the activity if Sceneform can not run
     */
    public static boolean checkIsSupportedDeviceOrFinish(final Activity activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            Log.e(TAG, "Sceneform requires Android N or later");
            Toast.makeText(activity, "Sceneform requires Android N or later", Toast.LENGTH_LONG).show();
            activity.finish();
            return false;
        }
        String openGlVersionString =
                ((ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE))
                        .getDeviceConfigurationInfo()
                        .getGlEsVersion();
        if (Double.parseDouble(openGlVersionString) < MIN_OPENGL_VERSION) {
            Log.e(TAG, "Sceneform requires OpenGL ES 3.0 later");
            Toast.makeText(activity, "Sceneform requires OpenGL ES 3.0 or later", Toast.LENGTH_LONG)
                    .show();
            activity.finish();
            return false;
        }
        return true;
    }

    public static class CustomVisualizer implements SelectionVisualizer {
        @Override
        public void applySelectionVisual(BaseTransformableNode node) {}
        @Override
        public void removeSelectionVisual(BaseTransformableNode node) {
        }
    }
}

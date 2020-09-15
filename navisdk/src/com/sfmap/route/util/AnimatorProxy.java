package com.sfmap.route.util;

import android.graphics.Camera;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.os.Build;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import java.lang.ref.WeakReference;
import java.util.WeakHashMap;

/**
 * A proxy class to allow for modifying post-3.0 view properties on all pre-3.0
 * platforms. <strong>DO NOT</strong> wrap your views with this class if you
 * are using {@code ObjectAnimator} as it will handle that itself.
 */
public final class AnimatorProxy extends Animation {
    /**
     * Whether or not the current running platform needs to be proxied.
     */
    public static final boolean NEEDS_PROXY = Integer.parseInt(Build.VERSION.SDK) < Build.VERSION_CODES.HONEYCOMB;

    private static final WeakHashMap<View, AnimatorProxy> PROXIES =
            new WeakHashMap<View, AnimatorProxy>();

    /**
     * Create a proxy to allow for modifying post-3.0 view properties on all
     * pre-3.0 platforms. <strong>DO NOT</strong> wrap your views if you are
     * using {@code ObjectAnimator} as it will handle that itself.
     *
     * @param view View to wrap.
     * @return Proxy to post-3.0 properties.
     */
    public static AnimatorProxy wrap(View view) {
        AnimatorProxy proxy = PROXIES.get(view);
        // This checks if the proxy already exists and whether it still is the animation of the given view
        if (proxy == null || proxy != view.getAnimation()) {
            proxy = new AnimatorProxy(view);
            PROXIES.put(view, proxy);
        }
        return proxy;
    }

    private final WeakReference<View> view;
    private final Camera mCamera = new Camera();
    private boolean hasPivot;

    private float alpha = 1;
    private float pivotX;
    private float pivotY;
    private float rotationX;
    private float rotationY;
    private float rotationZ;
    private float scaleX = 1;
    private float scaleY = 1;
    private float translationX;
    private float translationY;

    private final RectF rectFBefore = new RectF();
    private final RectF rectFAfter = new RectF();
    private final Matrix tempMatrix = new Matrix();

    private AnimatorProxy(View view) {
        //perform transformation immediately
        setDuration(0);
        //persist transformation beyond duration
        setFillAfter(true);
        view.setAnimation(this);
        this.view = new WeakReference<View>(view);
    }

    public float getAlpha() {
        return alpha;
    }

    public void setAlpha(float alpha) {
        if (this.alpha != alpha) {
            this.alpha = alpha;
            View view = this.view.get();
            if (view != null) {
                view.invalidate();
            }
        }
    }

    public float getPivotX() {
        return pivotX;
    }

    public void setPivotX(float pivotX) {
        if (!hasPivot || this.pivotX != pivotX) {
            prepareForUpdate();
            hasPivot = true;
            this.pivotX = pivotX;
            invalidateAfterUpdate();
        }
    }

    public float getPivotY() {
        return pivotY;
    }

    public void setPivotY(float pivotY) {
        if (!hasPivot || this.pivotY != pivotY) {
            prepareForUpdate();
            hasPivot = true;
            this.pivotY = pivotY;
            invalidateAfterUpdate();
        }
    }

    public float getRotation() {
        return rotationZ;
    }

    public void setRotation(float rotation) {
        if (rotationZ != rotation) {
            prepareForUpdate();
            rotationZ = rotation;
            invalidateAfterUpdate();
        }
    }

    public float getRotationX() {
        return rotationX;
    }

    public void setRotationX(float rotationX) {
        if (this.rotationX != rotationX) {
            prepareForUpdate();
            this.rotationX = rotationX;
            invalidateAfterUpdate();
        }
    }

    public float getRotationY() {
        return rotationY;
    }

    public void setRotationY(float rotationY) {
        if (this.rotationY != rotationY) {
            prepareForUpdate();
            this.rotationY = rotationY;
            invalidateAfterUpdate();
        }
    }

    public float getScaleX() {
        return scaleX;
    }

    public void setScaleX(float scaleX) {
        if (this.scaleX != scaleX) {
            prepareForUpdate();
            this.scaleX = scaleX;
            invalidateAfterUpdate();
        }
    }

    public float getScaleY() {
        return scaleY;
    }

    public void setScaleY(float scaleY) {
        if (this.scaleY != scaleY) {
            prepareForUpdate();
            this.scaleY = scaleY;
            invalidateAfterUpdate();
        }
    }

    public int getScrollX() {
        View view = this.view.get();
        if (view == null) {
            return 0;
        }
        return view.getScrollX();
    }

    public void setScrollX(int value) {
        View view = this.view.get();
        if (view != null) {
            view.scrollTo(value, view.getScrollY());
        }
    }

    public int getScrollY() {
        View view = this.view.get();
        if (view == null) {
            return 0;
        }
        return view.getScrollY();
    }

    public void setScrollY(int value) {
        View view = this.view.get();
        if (view != null) {
            view.scrollTo(view.getScrollX(), value);
        }
    }

    public float getTranslationX() {
        return translationX;
    }

    public void setTranslationX(float translationX) {
        if (this.translationX != translationX) {
            prepareForUpdate();
            this.translationX = translationX;
            invalidateAfterUpdate();
        }
    }

    public float getTranslationY() {
        return translationY;
    }

    public void setTranslationY(float translationY) {
        if (this.translationY != translationY) {
            prepareForUpdate();
            this.translationY = translationY;
            invalidateAfterUpdate();
        }
    }

    public float getX() {
        View view = this.view.get();
        if (view == null) {
            return 0;
        }
        return view.getLeft() + translationX;
    }

    public void setX(float x) {
        View view = this.view.get();
        if (view != null) {
            setTranslationX(x - view.getLeft());
        }
    }

    public float getY() {
        View view = this.view.get();
        if (view == null) {
            return 0;
        }
        return view.getTop() + translationY;
    }

    public void setY(float y) {
        View view = this.view.get();
        if (view != null) {
            setTranslationY(y - view.getTop());
        }
    }

    private void prepareForUpdate() {
        View view = this.view.get();
        if (view != null) {
            computeRect(rectFBefore, view);
        }
    }

    private void invalidateAfterUpdate() {
        View view = this.view.get();
        if (view == null || view.getParent() == null) {
            return;
        }

        final RectF after = rectFAfter;
        computeRect(after, view);
        after.union(rectFBefore);

        ((View) view.getParent()).invalidate(
                (int) Math.floor(after.left),
                (int) Math.floor(after.top),
                (int) Math.ceil(after.right),
                (int) Math.ceil(after.bottom));
    }

    private void computeRect(final RectF r, View view) {
        // compute current rectangle according to matrix transformation
        final float w = view.getWidth();
        final float h = view.getHeight();

        // use a rectangle at 0,0 to make sure we don't run into issues with scaling
        r.set(0, 0, w, h);

        final Matrix m = tempMatrix;
        m.reset();
        transformMatrix(m, view);
        tempMatrix.mapRect(r);

        r.offset(view.getLeft(), view.getTop());

        // Straighten coords if rotations flipped them
        if (r.right < r.left) {
            final float f = r.right;
            r.right = r.left;
            r.left = f;
        }
        if (r.bottom < r.top) {
            final float f = r.top;
            r.top = r.bottom;
            r.bottom = f;
        }
    }

    private void transformMatrix(Matrix m, View view) {
        final float w = view.getWidth();
        final float h = view.getHeight();
        final boolean hasPivot = this.hasPivot;
        final float pX = hasPivot ? pivotX : w / 2f;
        final float pY = hasPivot ? pivotY : h / 2f;

        final float rX = rotationX;
        final float rY = rotationY;
        final float rZ = rotationZ;
        if ((rX != 0) || (rY != 0) || (rZ != 0)) {
            final Camera camera = mCamera;
            camera.save();
            camera.rotateX(rX);
            camera.rotateY(rY);
            camera.rotateZ(-rZ);
            camera.getMatrix(m);
            camera.restore();
            m.preTranslate(-pX, -pY);
            m.postTranslate(pX, pY);
        }

        final float sX = scaleX;
        final float sY = scaleY;
        if ((sX != 1.0f) || (sY != 1.0f)) {
            m.postScale(sX, sY);
            final float sPX = -(pX / w) * ((sX * w) - w);
            final float sPY = -(pY / h) * ((sY * h) - h);
            m.postTranslate(sPX, sPY);
        }

        m.postTranslate(translationX, translationY);
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        View view = this.view.get();
        if (view != null) {
            t.setAlpha(alpha);
            transformMatrix(t.getMatrix(), view);
        }
    }
}

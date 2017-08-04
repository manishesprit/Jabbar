package com.jabbar.AnimationMaster;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import com.jabbar.AnimationMaster.initializers.AccelerationInitializer;
import com.jabbar.AnimationMaster.initializers.ParticleInitializer;
import com.jabbar.AnimationMaster.initializers.RotationInitializer;
import com.jabbar.AnimationMaster.initializers.RotationSpeedInitializer;
import com.jabbar.AnimationMaster.initializers.ScaleInitializer;
import com.jabbar.AnimationMaster.initializers.SpeedModuleAndRangeInitializer;
import com.jabbar.AnimationMaster.initializers.SpeeddByComponentsInitializer;
import com.jabbar.AnimationMaster.modifiers.AlphaModifier;
import com.jabbar.AnimationMaster.modifiers.ParticleModifier;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class ParticleSystem {

    private static long TIMER_TASK_INTERVAL = 33; // Default 30fps
    private ViewGroup mParentView;
    private int mMaxParticles;
    private Random mRandom;

    private ParticleField mDrawingView;

    private ArrayList<Particle> mParticles;
    private final ArrayList<Particle> mActiveParticles = new ArrayList<>();
    private long mTimeToLive;
    private long mCurrentTime = 0;

    private float mParticlesPerMillisecond;
    private int mActivatedParticles;
    private long mEmittingTime;

    private List<ParticleModifier> mModifiers;
    private List<ParticleInitializer> mInitializers;
    private ValueAnimator mAnimator;
    private Timer mTimer;
    private final ParticleTimerTask mTimerTask = new ParticleTimerTask(this);

    private float mDpToPxScale;
    private int[] mParentLocation;

    private int mEmitterXMin;
    private int mEmitterXMax;
    private int mEmitterYMin;
    private int mEmitterYMax;

    private static class ParticleTimerTask extends TimerTask {

        private final WeakReference<ParticleSystem> mPs;

        public ParticleTimerTask(ParticleSystem ps) {
            mPs = new WeakReference<>(ps);
        }

        @Override
        public void run() {
            if (mPs.get() != null) {
                ParticleSystem ps = mPs.get();
                ps.onUpdate(ps.mCurrentTime);
                ps.mCurrentTime += TIMER_TASK_INTERVAL;
            }
        }
    }

    public ParticleSystem(WindowManager manager, ViewGroup parentView, int maxParticles, long timeToLive) {
        mRandom = new Random();
        mParentLocation = new int[2];

        setParentViewGroup(parentView);

        mModifiers = new ArrayList<>();
        mInitializers = new ArrayList<>();

        mMaxParticles = maxParticles;
        // Create the particles

        mParticles = new ArrayList<>();
        mTimeToLive = timeToLive;


        DisplayMetrics displayMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(displayMetrics);
        mDpToPxScale = (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    public ParticleSystem(WindowManager manager, ViewGroup parentView, int maxParticles, Drawable drawable, long timeToLive) {
        this(manager, parentView, maxParticles, timeToLive);
        if (drawable instanceof AnimationDrawable) {
            AnimationDrawable animation = (AnimationDrawable) drawable;
            for (int i = 0; i < mMaxParticles; i++) {
                mParticles.add(new AnimatedParticle(animation));
            }
        } else {
            Bitmap bitmap = null;
            if (drawable instanceof BitmapDrawable) {
                bitmap = ((BitmapDrawable) drawable).getBitmap();
            } else {
                bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                        drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                drawable.draw(canvas);
            }
            for (int i = 0; i < mMaxParticles; i++) {
                mParticles.add(new Particle(bitmap));
            }
        }
    }

    public float dpToPx(float dp) {
        return dp * mDpToPxScale;
    }


    public static void setFPS(double fps) {
        TIMER_TASK_INTERVAL = Math.round(1000 / fps);
    }

    public ParticleSystem addModifier(ParticleModifier modifier) {
        mModifiers.add(modifier);
        return this;
    }

    public ParticleSystem setSpeedRange(float speedMin, float speedMax) {
        mInitializers.add(new SpeedModuleAndRangeInitializer(dpToPx(speedMin), dpToPx(speedMax), 0, 360));
        return this;
    }

    public ParticleSystem setSpeedModuleAndAngleRange(float speedMin, float speedMax, int minAngle, int maxAngle) {
        // else emitting from top (270°) to bottom (90°) range would not be possible if someone
        // entered minAngle = 270 and maxAngle=90 since the module would swap the values
        while (maxAngle < minAngle) {
            maxAngle += 360;
        }
        mInitializers.add(new SpeedModuleAndRangeInitializer(dpToPx(speedMin), dpToPx(speedMax), minAngle, maxAngle));
        return this;
    }

    public ParticleSystem setSpeedByComponentsRange(float speedMinX, float speedMaxX, float speedMinY, float speedMaxY) {
        mInitializers.add(new SpeeddByComponentsInitializer(dpToPx(speedMinX), dpToPx(speedMaxX),
                dpToPx(speedMinY), dpToPx(speedMaxY)));
        return this;
    }


    public ParticleSystem setInitialRotationRange(int minAngle, int maxAngle) {
        mInitializers.add(new RotationInitializer(minAngle, maxAngle));
        return this;
    }


    public ParticleSystem setScaleRange(float minScale, float maxScale) {
        mInitializers.add(new ScaleInitializer(minScale, maxScale));
        return this;
    }


    public ParticleSystem setRotationSpeed(float rotationSpeed) {
        mInitializers.add(new RotationSpeedInitializer(rotationSpeed, rotationSpeed));
        return this;
    }


    public ParticleSystem setRotationSpeedRange(float minRotationSpeed, float maxRotationSpeed) {
        mInitializers.add(new RotationSpeedInitializer(minRotationSpeed, maxRotationSpeed));
        return this;
    }


    public ParticleSystem setAccelerationModuleAndAndAngleRange(float minAcceleration, float maxAcceleration, int minAngle, int maxAngle) {
        mInitializers.add(new AccelerationInitializer(dpToPx(minAcceleration), dpToPx(maxAcceleration),
                minAngle, maxAngle));
        return this;
    }

    public ParticleSystem addInitializer(ParticleInitializer initializer) {
        if (initializer != null) {
            mInitializers.add(initializer);
        }
        return this;
    }


    public ParticleSystem setAcceleration(float acceleration, int angle) {
        mInitializers.add(new AccelerationInitializer(acceleration, acceleration, angle, angle));
        return this;
    }


    public ParticleSystem setParentViewGroup(ViewGroup viewGroup) {
        mParentView = viewGroup;
        if (mParentView != null) {
            mParentView.getLocationInWindow(mParentLocation);
        }
        return this;
    }

    public ParticleSystem setStartTime(long time) {
        mCurrentTime = time;
        return this;
    }

    public ParticleSystem setFadeOut(long milisecondsBeforeEnd, Interpolator interpolator) {
        mModifiers.add(new AlphaModifier(255, 0, mTimeToLive - milisecondsBeforeEnd, mTimeToLive, interpolator));
        return this;
    }

    public ParticleSystem setFadeOut(long duration) {
        return setFadeOut(duration, new LinearInterpolator());
    }


    public void emitWithGravity(ParticleField mDrawingView, View emitter, int gravity, int particlesPerSecond, int emittingTime) {
        // Setup emitter
        configureEmitter(emitter, gravity);
        startEmitting(mDrawingView, particlesPerSecond, emittingTime);
    }


    public void emit(ParticleField mDrawingView, View emitter, int particlesPerSecond, int emittingTime) {
        emitWithGravity(mDrawingView, emitter, Gravity.CENTER, particlesPerSecond, emittingTime);
    }


    public void emit(ParticleField mDrawingView, View emitter, int particlesPerSecond) {
        // Setup emitter
        emitWithGravity(mDrawingView, emitter, Gravity.CENTER, particlesPerSecond);
    }


    public void emitWithGravity(ParticleField mDrawingView, View emitter, int gravity, int particlesPerSecond) {
        // Setup emitter
        configureEmitter(emitter, gravity);
        startEmitting(mDrawingView, particlesPerSecond);
    }

    private void startEmitting(ParticleField mDrawingView, int particlesPerSecond) {
        mActivatedParticles = 0;
        mParticlesPerMillisecond = particlesPerSecond / 1000f;
        // Add a full size view to the parent view
        mEmittingTime = -1; // Meaning infinite
        mDrawingView.setParticles(mActiveParticles);
        updateParticlesBeforeStartTime(particlesPerSecond);
        mTimer = new Timer();
        mTimer.schedule(mTimerTask, 0, TIMER_TASK_INTERVAL);
    }


    private void configureEmitter(int emitterX, int emitterY) {
        // We configure the emitter based on the window location to fix the offset of action bar if present
        mEmitterXMin = emitterX - mParentLocation[0];
        mEmitterXMax = mEmitterXMin;
        mEmitterYMin = emitterY - mParentLocation[1];
        mEmitterYMax = mEmitterYMin;
    }

    private void startEmitting(ParticleField mDrawingView, int particlesPerSecond, int emittingTime) {
        mActivatedParticles = 0;
        mParticlesPerMillisecond = particlesPerSecond / 1000f;
        // Add a full size view to the parent view

        mDrawingView.setParticles(mActiveParticles);
        updateParticlesBeforeStartTime(particlesPerSecond);
        mEmittingTime = emittingTime;
        startAnimator(new LinearInterpolator(), emittingTime + mTimeToLive);
    }

    public void emit(ParticleField mDrawingView, int emitterX, int emitterY, int particlesPerSecond) {
        configureEmitter(emitterX, emitterY);
        startEmitting(mDrawingView, particlesPerSecond);
    }


    public void updateEmitPoint(int emitterX, int emitterY) {
        configureEmitter(emitterX, emitterY);
    }

    public void updateEmitPoint(View emitter, int gravity) {
        configureEmitter(emitter, gravity);
    }

    public void oneShot(View emitter, int numParticles) {
        oneShot(emitter, numParticles, new LinearInterpolator());
    }

    public void oneShot(View emitter, int numParticles, Interpolator interpolator) {
        configureEmitter(emitter, Gravity.CENTER);
        mActivatedParticles = 0;
        mEmittingTime = mTimeToLive;
        // We create particles based in the parameters
        for (int i = 0; i < numParticles && i < mMaxParticles; i++) {
            activateParticle(0);
        }
        // Add a full size view to the parent view
        mDrawingView.setParticles(mActiveParticles);
        // We start a property animator that will call us to do the update
        // Animate from 0 to timeToLiveMax
        startAnimator(interpolator, mTimeToLive);
    }

    private void startAnimator(Interpolator interpolator, long animnationTime) {
        mAnimator = ValueAnimator.ofInt(0, (int) animnationTime);
        mAnimator.setDuration(animnationTime);
        mAnimator.addUpdateListener(new AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int miliseconds = (Integer) animation.getAnimatedValue();
                onUpdate(miliseconds);
            }
        });
        mAnimator.addListener(new AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                cleanupAnimation();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                cleanupAnimation();
            }
        });
        mAnimator.setInterpolator(interpolator);
        mAnimator.start();
    }

    private void configureEmitter(View emitter, int gravity) {
        // It works with an emision range
        int[] location = new int[2];
        emitter.getLocationInWindow(location);

        // Check horizontal gravity and set range
        if (hasGravity(gravity, Gravity.LEFT)) {
            mEmitterXMin = location[0] - mParentLocation[0];
            mEmitterXMax = mEmitterXMin;
        } else if (hasGravity(gravity, Gravity.RIGHT)) {
            mEmitterXMin = location[0] + emitter.getWidth() - mParentLocation[0];
            mEmitterXMax = mEmitterXMin;
        } else if (hasGravity(gravity, Gravity.CENTER_HORIZONTAL)) {
            mEmitterXMin = location[0] + emitter.getWidth() / 2 - mParentLocation[0];
            mEmitterXMax = mEmitterXMin;
        } else {
            // All the range
            mEmitterXMin = location[0] - mParentLocation[0];
            mEmitterXMax = location[0] + emitter.getWidth() - mParentLocation[0];
        }

        // Now, vertical gravity and range
        if (hasGravity(gravity, Gravity.TOP)) {
            mEmitterYMin = location[1] - mParentLocation[1];
            mEmitterYMax = mEmitterYMin;
        } else if (hasGravity(gravity, Gravity.BOTTOM)) {
            mEmitterYMin = location[1] + emitter.getHeight() - mParentLocation[1];
            mEmitterYMax = mEmitterYMin;
        } else if (hasGravity(gravity, Gravity.CENTER_VERTICAL)) {
            mEmitterYMin = location[1] + emitter.getHeight() / 2 - mParentLocation[1];
            mEmitterYMax = mEmitterYMin;
        } else {
            // All the range
            mEmitterYMin = location[1] - mParentLocation[1];
            mEmitterYMax = location[1] + emitter.getHeight() - mParentLocation[1];
        }
    }

    private boolean hasGravity(int gravity, int gravityToCheck) {
        return (gravity & gravityToCheck) == gravityToCheck;
    }

    private void activateParticle(long delay) {
        Particle p = mParticles.remove(0);
        p.init();
        // Initialization goes before configuration, scale is required before can be configured properly
        for (int i = 0; i < mInitializers.size(); i++) {
            mInitializers.get(i).initParticle(p, mRandom);
        }
        int particleX = getFromRange(mEmitterXMin, mEmitterXMax);
        int particleY = getFromRange(mEmitterYMin, mEmitterYMax);
        p.configure(mTimeToLive, particleX, particleY);
        p.activate(delay, mModifiers);
        mActiveParticles.add(p);
        mActivatedParticles++;
    }

    private int getFromRange(int minValue, int maxValue) {
        if (minValue == maxValue) {
            return minValue;
        }
        if (minValue < maxValue) {
            return mRandom.nextInt(maxValue - minValue) + minValue;
        } else {
            return mRandom.nextInt(minValue - maxValue) + maxValue;
        }
    }

    private void onUpdate(long miliseconds) {
        while (((mEmittingTime > 0 && miliseconds < mEmittingTime) || mEmittingTime == -1) && // This point should emit
                !mParticles.isEmpty() && // We have particles in the pool
                mActivatedParticles < mParticlesPerMillisecond * miliseconds) { // and we are under the number of particles that should be launched
            // Activate a new particle
            activateParticle(miliseconds);
        }
        synchronized (mActiveParticles) {
            for (int i = 0; i < mActiveParticles.size(); i++) {
                boolean active = mActiveParticles.get(i).update(miliseconds);
                if (!active) {
                    Particle p = mActiveParticles.remove(i);
                    i--; // Needed to keep the index at the right position
                    mParticles.add(p);
                }
            }
        }
        mDrawingView.postInvalidate();
    }

    private void cleanupAnimation() {
        mParentView.removeView(mDrawingView);
        mDrawingView = null;
        mParentView.postInvalidate();
        mParticles.addAll(mActiveParticles);
    }


    public void stopEmitting() {
        // The time to be emitting is the current time (as if it was a time-limited emitter
        mEmittingTime = mCurrentTime;
    }

    public void cancel() {
        if (mAnimator != null && mAnimator.isRunning()) {
            mAnimator.cancel();
        }
        if (mTimer != null) {
            mTimer.cancel();
            mTimer.purge();
            cleanupAnimation();
        }
    }

    private void updateParticlesBeforeStartTime(int particlesPerSecond) {
        if (particlesPerSecond == 0) {
            return;
        }
        long currentTimeInMs = mCurrentTime / 1000;
        long framesCount = currentTimeInMs / particlesPerSecond;
        if (framesCount == 0) {
            return;
        }
        long frameTimeInMs = mCurrentTime / framesCount;
        for (int i = 1; i <= framesCount; i++) {
            onUpdate(frameTimeInMs * i + 1);
        }
    }
}

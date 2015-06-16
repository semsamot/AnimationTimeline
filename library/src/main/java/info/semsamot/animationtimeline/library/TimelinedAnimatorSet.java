package info.semsamot.animationtimeline.library;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by semsamot on January 2015.
 */
public class TimelinedAnimatorSet {

    private AnimatorSet mAnimatorSet;
    private Timeline mTimeline;
    private List<Item> mItems;
    private float mCurrentSeek;

    public TimelinedAnimatorSet() {
        mAnimatorSet = new AnimatorSet();
        mTimeline = new Timeline();
        mItems = new ArrayList<>();
    }

    public float getCurrentSeek() {
        return mCurrentSeek;
    }

    public long getTimelineDuration() {
        return mTimeline.getDuration();
    }

    public long getRemainingDuration() {
        return (long) (getTimelineDuration() - (getTimelineDuration() * mCurrentSeek));
    }

    public void extendTimeline(long duration) {
        if (duration > mTimeline.endPosition) {
            mTimeline.endPosition = duration;
        }
    }

    public void increaseTimeline(long duration) {
        mTimeline.endPosition += duration;
    }

    public void decreaseTimeline(long duration) {
        mTimeline.endPosition -= duration;

        if (mTimeline.endPosition < 0) {
            mTimeline.endPosition = 0;
        }
    }

    public void seekTimeline(float value) {
        seekTimeline((long)(getTimelineDuration() * value));
        mCurrentSeek = value;
    }

    public void seekTimeline(long time) {
        for (Item item : mItems) {
            if (item.animator instanceof ValueAnimator) {
                ValueAnimator animator = ((ValueAnimator)item.animator);

                long startPosition = item.timeline.startPosition;
                long endPosition = item.timeline.endPosition;

                if (time < startPosition && time < endPosition) {
                    animator.setCurrentPlayTime(0);
                } else  if (time > startPosition && time > endPosition) {
                    animator.setCurrentPlayTime(animator.getDuration());
                } else {
                    animator.setCurrentPlayTime(time - startPosition);
                }

            }
        }

        mCurrentSeek = time;
    }

    public void addAnimatorToItems(Animator animator) {
        addAnimatorToItems(animator, 0);
    }

    public void addAnimatorToItems(Animator animator, long startPosition) {
        Timeline timeline = new Timeline();

        timeline.startPosition = startPosition;
        timeline.endPosition = startPosition + animator.getDuration();

        mItems.add(new Item(animator, timeline));
    }

    public void offsetItemTimeline(Animator animator, long offset) {
        for (Item item : mItems) {
            if (item.animator.equals(animator)) {
                item.timeline.startPosition += offset;
                item.timeline.endPosition += offset;
                break;
            }
        }
    }

    public void playTogether(Animator... items) {
        long maxDuration = 0;

        for (Animator item : items) {
            addAnimatorToItems(item);
            maxDuration = (item.getDuration() > maxDuration) ? item.getDuration() : maxDuration;
        }

        extendTimeline(maxDuration);

        mAnimatorSet.playTogether(items);
    }

    public void playSequentially(Animator... items) {
        long totalDuration = 0;

        for (Animator item : items) {
            addAnimatorToItems(item, totalDuration);
            totalDuration += item.getDuration();
        }

        extendTimeline(totalDuration);

        mAnimatorSet.playSequentially(items);
    }

    public Builder play(Animator anim) {
        return new Builder(this, mAnimatorSet.play(anim), anim);
    }

    public class Builder {

        private TimelinedAnimatorSet mParent;
        private AnimatorSet.Builder mBuilder;
        private Animator mAnim;

        private List<Animator> animators;
        private long mTotalDuration;


        Builder(TimelinedAnimatorSet parent, AnimatorSet.Builder builder, Animator anim) {
            mBuilder = builder;
            mAnim = anim;
            mParent = parent;

            mTotalDuration = mAnim.getDuration();
            mParent.extendTimeline(mTotalDuration);

            mParent.addAnimatorToItems(mAnim);

            animators = new ArrayList<>();
            animators.add(mAnim);
        }

        public Builder with(Animator anim) {
            mParent.extendTimeline(anim.getDuration());
            mParent.addAnimatorToItems(anim);

            animators.add(anim);

            mBuilder.with(anim);
            return this;
        }

        public Builder before(Animator anim) {
            mParent.addAnimatorToItems(anim, mTotalDuration);

            mTotalDuration += anim.getDuration();
            mParent.extendTimeline(mTotalDuration);

            animators.add(anim);

            mBuilder.before(anim);
            return this;
        }

        public Builder after(Animator anim) {
            animators.add(anim);

            for (Animator animator : animators) {
                mParent.offsetItemTimeline(animator, anim.getDuration());
            }

            mTotalDuration += anim.getDuration();
            mParent.extendTimeline(mTotalDuration);

            mParent.addAnimatorToItems(anim, 0);

            mBuilder.after(anim);
            return this;
        }

        /*public Builder after(long delay) {
            mBuilder.after(delay);
            after(mAnim);
            return this;
        }*/
    }

    private class Timeline {
        public long startPosition;
        public long endPosition;

        public long getDuration() {
            return endPosition - startPosition;
        }
    }

    private class Item {
        public Animator animator;
        public Timeline timeline;

        private Item(Animator animator, Timeline timeline) {
            this.animator = animator;
            this.timeline = timeline;
        }
    }
}

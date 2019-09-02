package yuku.ambilwarna.colorpicker;

import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import www.amg_witten.de.apptest.R;

public class AmbilWarnaDialogFragment extends DialogFragment implements View.OnTouchListener, View.OnClickListener {
    private int mColorOriginal;
    private int mColor;
    private int mOriginal;

    private OnAmbilWarnaListener mListener;
    private AmbilWarnaKotak mViewSatVal;
    private View mParentView;
    private View mViewHue;
    private View mViewNewColor;
    private ImageView mViewCursor;
    private ImageView mViewTarget;
    private Button mViewCancelButton;
    private Button mViewOkButton;
    private Button mViewResetButton;
    private ViewGroup mViewContainer;

    private final float[] mCurrentColorHsv = new float[3];

    private static final String KEY_COLOR_ORIGINAL = "key_color_original";
    private static final String KEY_COLOR = "key_color";
    private static final String KEY_THEME = "key_theme";
    private static final String KEY_ORIGINAL = "key_original";

    /**
     * Create a new instance of MyDialogFragment, providing "color"
     * as an argument.
     */
    public static AmbilWarnaDialogFragment newInstance(int color) {
        AmbilWarnaDialogFragment fragment = new AmbilWarnaDialogFragment();

        Bundle args = new Bundle();
        args.putInt("color", color);
        fragment.setArguments(args);

        return fragment;
    }

    /**
     * Create a new instance of MyDialogFragment, providing "color" and "theme"
     * as an argument.
     */
    public static AmbilWarnaDialogFragment newInstance(int color, int theme, int original) {
        AmbilWarnaDialogFragment fragment = new AmbilWarnaDialogFragment();

        Bundle args = new Bundle();
        args.putInt("color", color);
        args.putInt("theme", theme);
        args.putInt("original",original);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null && savedInstanceState.containsKey(KEY_COLOR) && savedInstanceState.containsKey(KEY_THEME) && savedInstanceState.containsKey(KEY_ORIGINAL)) {
            mColorOriginal = savedInstanceState.getInt(KEY_COLOR_ORIGINAL);
            mColor = savedInstanceState.getInt(KEY_COLOR);
            mOriginal = savedInstanceState.getInt(KEY_ORIGINAL);
        } else {
            Bundle args = getArguments();
            mColorOriginal = args.getInt("color");
            mColor = args.getInt("color");
            mOriginal = args.getInt("original");
        }

        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.AppCompatDialogStyle);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mParentView = inflater.inflate(R.layout.ambilwarna_dialog, container, false);

        initView();
        setView();
        setListeners();

        return mParentView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt(KEY_COLOR_ORIGINAL, mColorOriginal);
        outState.putInt(KEY_COLOR, getColor());
        outState.putInt(KEY_ORIGINAL,mOriginal);

        super.onSaveInstanceState(outState);
    }

    private void initView() {
        Color.colorToHSV(mColor, mCurrentColorHsv);

        mViewHue = mParentView.findViewById(R.id.ambilwarna_viewHue);
        mViewSatVal = mParentView.findViewById(R.id.ambilwarna_viewSatBri);
        mViewCursor = mParentView.findViewById(R.id.ambilwarna_cursor);
        View mViewOldColor = mParentView.findViewById(R.id.ambilwarna_warnaLama);
        mViewNewColor = mParentView.findViewById(R.id.ambilwarna_warnaBaru);
        mViewTarget = mParentView.findViewById(R.id.ambilwarna_target);
        mViewCancelButton = mParentView.findViewById(R.id.ambilwarna_btn_no);
        mViewOkButton = mParentView.findViewById(R.id.ambilwarna_btn_yes);
        mViewResetButton = mParentView.findViewById(R.id.ambilwarna_reset);
        mViewContainer = mParentView.findViewById(R.id.ambilwarna_viewContainer);

        mViewSatVal.setHue(getHue());
        mViewOldColor.setBackgroundColor(mColorOriginal);
        mViewNewColor.setBackgroundColor(mColor);
    }

    private void setView() {
        if (mParentView == null)
            return;

        // move cursor & target on first draw
        ViewTreeObserver vto = mParentView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                moveCursor();
                moveTarget();

                mParentView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    private void setListeners() {
        mViewHue.setOnTouchListener(this);
        mViewSatVal.setOnTouchListener(this);
        mViewOkButton.setOnClickListener(this);
        mViewCancelButton.setOnClickListener(this);
        mViewResetButton.setOnClickListener(this);
    }

    public void setOnAmbilWarnaListener(OnAmbilWarnaListener listener) {
        mListener = listener;
    }

    private void moveCursor() {
        float y = mViewHue.getMeasuredHeight() - (getHue() * mViewHue.getMeasuredHeight() / 360.f);
        if (y == mViewHue.getMeasuredHeight()) y = 0.f;
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mViewCursor.getLayoutParams();
        layoutParams.leftMargin = (int) (mViewHue.getLeft() - Math.floor(mViewCursor.getMeasuredWidth() / 2) - mViewContainer.getPaddingLeft());
        layoutParams.topMargin = (int) (mViewHue.getTop() + y - Math.floor(mViewCursor.getMeasuredHeight() / 2) - mViewContainer.getPaddingTop());
        mViewCursor.setLayoutParams(layoutParams);
    }

    private void moveTarget() {
        float x = getSat() * mViewSatVal.getMeasuredWidth();
        float y = (1.f - getVal()) * mViewSatVal.getMeasuredHeight();
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mViewTarget.getLayoutParams();
        layoutParams.leftMargin = (int) (mViewSatVal.getLeft() + x - Math.floor(mViewTarget.getMeasuredWidth() / 2) - mViewContainer.getPaddingLeft());
        layoutParams.topMargin = (int) (mViewSatVal.getTop() + y - Math.floor(mViewTarget.getMeasuredHeight() / 2) - mViewContainer.getPaddingTop());
        mViewTarget.setLayoutParams(layoutParams);
    }

    private int getColor() {
        return Color.HSVToColor(mCurrentColorHsv);
    }

    private float getHue() {
        return mCurrentColorHsv[0];
    }

    private float getSat() {
        return mCurrentColorHsv[1];
    }

    private float getVal() {
        return mCurrentColorHsv[2];
    }

    private void setHue(float hue) {
        mCurrentColorHsv[0] = hue;
    }

    private void setSat(float sat) {
        mCurrentColorHsv[1] = sat;
    }

    private void setVal(float val) {
        mCurrentColorHsv[2] = val;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v.equals(mViewHue)) {
            if (event.getAction() == MotionEvent.ACTION_MOVE
                    || event.getAction() == MotionEvent.ACTION_DOWN
                    || event.getAction() == MotionEvent.ACTION_UP) {

                float y = event.getY();
                if (y < 0.f) y = 0.f;
                if (y > mViewHue.getMeasuredHeight())
                    y = mViewHue.getMeasuredHeight() - 0.001f; // to avoid looping from end to start.
                float hue = 360.f - 360.f / mViewHue.getMeasuredHeight() * y;
                if (hue == 360.f) hue = 0.f;
                setHue(hue);

                // update view
                mViewSatVal.setHue(getHue());
                moveCursor();
                mViewNewColor.setBackgroundColor(getColor());

                return true;
            }
        } else if (v.equals(mViewSatVal)) {
            if (event.getAction() == MotionEvent.ACTION_MOVE
                    || event.getAction() == MotionEvent.ACTION_DOWN
                    || event.getAction() == MotionEvent.ACTION_UP) {

                float x = event.getX(); // touch event are in dp units.
                float y = event.getY();

                if (x < 0.f) x = 0.f;
                if (x > mViewSatVal.getMeasuredWidth()) x = mViewSatVal.getMeasuredWidth();
                if (y < 0.f) y = 0.f;
                if (y > mViewSatVal.getMeasuredHeight()) y = mViewSatVal.getMeasuredHeight();

                setSat(1.f / mViewSatVal.getMeasuredWidth() * x);
                setVal(1.f - (1.f / mViewSatVal.getMeasuredHeight() * y));

                // update view
                moveTarget();
                mViewNewColor.setBackgroundColor(getColor());

                return true;
            }
        }

        return false;
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();

        if (id == R.id.ambilwarna_btn_no) {
            if (mListener != null)
                mListener.onCancel(this);
        } else if (id == R.id.ambilwarna_btn_yes) {
            if (mListener != null)
                mListener.onOk(this, getColor());
        } else if (id == R.id.ambilwarna_reset) {
            mViewNewColor.setBackgroundColor(mOriginal);
            mListener.onOk(this,mOriginal);
        }

        dismiss();
    }
}

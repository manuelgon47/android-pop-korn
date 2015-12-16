package com.mgonzalez.bd.popcornmenu;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PopCornMenu extends RelativeLayout {

    /**
     * Menu button
     */
    protected ImageButton fab;

    /**
     * True when the menu is opened
     */
    protected boolean expanded = false;

    /**
     * Menu items
     */
    List<MenuItem> menuButtons;

    private long openAniationDuration;
    private long closeAniationDuration;

    public PopCornMenu(Context context) {
        super(context);
        if (!isInEditMode()) {
            init(null);
        }
    }

    public PopCornMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            init(attrs);
        }
    }

    public PopCornMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (!isInEditMode()) {
            init(attrs);
        }
    }

    public PopCornMenu(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        if (!isInEditMode()) {
            init(attrs);
        }
    }

    private AttributeSet attrs;
    private int fabId;
    /**
     * Init ListView
     *
     * @param attrs AttributeSet
     */
    private void init(AttributeSet attrs) {
        this.attrs = attrs;
        expanded = false;

        menuButtons = new ArrayList<>();

        setOpenAniationDuration(Constants.OPEN_ANIMATION_DURATION);
        setCloseAniationDuration(Constants.CLOSE_ANIMATION_DURATION);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, getPcFromDP(180));
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        setLayoutParams(params);
        setClipChildren(false);


        // Create menu view
        fab = new ImageButton(getContext(), attrs);
        RelativeLayout.LayoutParams fabParams = new RelativeLayout.LayoutParams(getPcFromDP(56), getPcFromDP(56));
        fabParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        fabParams.topMargin = getPcFromDP(5);
        fabParams.bottomMargin = getPcFromDP(5);
        fab.setLayoutParams(fabParams);
        fab.setBackgroundResource(R.drawable.bg_tab_bar);
        fab.setImageResource(R.drawable.open_menu);
        int fabId = View.generateViewId();
        this.fabId = fabId;
        fab.setId(fabId);


        // Change the menu button color:
        changeMenuColor(Constants.DEFAULT_MENU_BACKGROUND_COLOR);

        // Add menu view
        addView(fab);
    }

    /**
     * Create the menu children.
     *
     * @param buttons Array de resources (drawables) new int[]{R.drawable.drawable1, ...}
     */
    public void setup(int[] buttons) {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, getPcFromDP(61 + 45*buttons.length + 5));
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        setLayoutParams(params);

        menuButtons = new ArrayList<>();

        removeView(fab);


        /**
         * Proceso:
         * 1.- Creo un imageView
         * 2.- Le digo en que ID se tiene que fijara para tomar de referncia (Para ponerlo encima
         * del último botón de menu).
         *      2.1.- Si el array de botones está vacío es el 1er view, con lo cual va encima del botón
         *      de menú.
         *      2.2.- En otro caso, cojo el ID del últomo view añadido.
         * 3.- Le asigno la imagen correspondiente
         * 4.- Genero un ID único para esta vista
         * 5.- Lo añado a la vista y al array de botones
         */
        for(int drawable : buttons) {
            // Create button menu
            ImageView menuBtn = new ImageView(getContext(), attrs);
            RelativeLayout.LayoutParams btn1Params = new RelativeLayout.LayoutParams(getPcFromDP(40), getPcFromDP(40));

            int idViewReferenceTo = menuButtons.size() > 0 ? menuButtons.get(menuButtons.size() - 1).getImageView().getId() : fabId;
            btn1Params.addRule(RelativeLayout.ABOVE, idViewReferenceTo);
            btn1Params.addRule(RelativeLayout.ALIGN_LEFT, fabId);
            btn1Params.addRule(RelativeLayout.ALIGN_RIGHT, fabId);
            btn1Params.topMargin = getPcFromDP(5);
            menuBtn.setLayoutParams(btn1Params);
            menuBtn.setImageResource(drawable);

            // Generate Id
            int btnId = View.generateViewId();
            menuBtn.setId(btnId);

            // Add button menu
            addView(menuBtn);
            MenuItem item = new MenuItem(menuBtn);
            menuButtons.add(item);

        }


        // Add menu view
        addView(fab);
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (!isInEditMode()) {
            createtabBarMenu();
        }
    }


    // **********************
    // *		TabBar		*
    // **********************

    protected void createtabBarMenu() {
        final ViewGroup fabContainer = this;
        setUpTabBarButton();

        fabContainer.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                fabContainer.getViewTreeObserver().removeOnPreDrawListener(this);
                for (MenuItem menuButton : menuButtons) {
                    Float offset = fab.getY() - menuButton.getImageView().getY();
                    menuButton.setOffset(offset);

                    menuButton.getImageView().setTranslationY(offset);
                }

                return true;
            }
        });

    }

    public void collapseFab() {
        if (fab != null) {
            fab.setImageResource(R.drawable.animated_close_menu);
        }
        AnimatorSet animatorSet = new AnimatorSet();
        Collection<Animator> items = new ArrayList<>();
        for (int i = 0; i < menuButtons.size(); i++) {
            MenuItem item = menuButtons.get(i);
            Animator animatorCollapse = createCollapseAnimator(item.getImageView(), item.getOffset());
            Animator animatorRotate = createCollapseRotateAnimator(item.getImageView());
            items.add(animatorCollapse);
            items.add(animatorRotate);
        }

        animatorSet.playTogether(items);

        animatorSet.start();
        animateFab();
        expanded = !expanded;
    }

    protected void expandFab() {
        if (fab != null) {
            fab.setImageResource(R.drawable.animated_open_menu);
        }
        AnimatorSet animatorSet = new AnimatorSet();
        Collection<Animator> items = new ArrayList<>();
        for (int i = 0; i < menuButtons.size(); i++) {
            AnimatorSet animatorBtn = new AnimatorSet();
            MenuItem menuItem = menuButtons.get(i);
            ImageView imgBtn = menuItem.getImageView();
            animatorBtn.playTogether(
                    createExpandAnimator(imgBtn, menuItem.getOffset()),
                    createExpandRotateAnimator(imgBtn),
                    createExpandAlphaAnimator(imgBtn)
            );

            items.add(animatorBtn);
        }

        animatorSet.playTogether(items);


        animatorSet.start();
        animateFab();
        expanded = !expanded;
    }

    // **************************
    // *        Animations      *
    // **************************
    private static final String TRANSLATION_Y = "translationY";
    private static final String ROTATION = "rotation";
    private static final String ALPHA = "alpha";

    // Collapse
    private Animator createCollapseAnimator(View view, float offset) {
        return ObjectAnimator.ofFloat(view, TRANSLATION_Y, 0, offset).setDuration(closeAniationDuration);
    }

    private Animator createCollapseRotateAnimator(View view) {
        return ObjectAnimator.ofFloat(view, ROTATION, 360f, 0f).setDuration(closeAniationDuration);
    }

    // Expand
    private Animator createExpandAnimator(View view, float offset) {
        return ObjectAnimator.ofFloat(view, TRANSLATION_Y, offset, 0).setDuration(openAniationDuration);
    }

    private Animator createExpandRotateAnimator(View view) {
        return ObjectAnimator.ofFloat(view, ROTATION, 0f, 360f).setDuration(openAniationDuration);
    }

    private Animator createExpandAlphaAnimator(View view) {
        return ObjectAnimator.ofFloat(view, ALPHA, 0f, 1f).setDuration(openAniationDuration);
    }

    private void animateFab() {
        Drawable drawable = fab.getDrawable();
        if (drawable instanceof Animatable) {
            ((Animatable) drawable).start();
        }
    }

    /**
     * If the user make click, expand the menu items,
     * if the user make long click, start the drag and drop.
     */
    private void setUpTabBarButton() {
        View tab = fab;
        if (tab != null) {
            tab.setOnTouchListener(null);
            tab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (expanded) {
                        collapseFab();
                    } else {
                        expandFab();
                    }
                }
            });
            tab.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    v.setOnTouchListener(new DraggableTouchListener());
                    return false;
                }
            });
        }

    }

    /**
     * Listener to drag and drop the menu.
     */
    private final class DraggableTouchListener implements View.OnTouchListener {

        private boolean removeRightRule;
        private boolean removeBottomRule;

        public DraggableTouchListener() {
            removeRightRule = false;
            removeBottomRule = false;
        }

        public boolean onTouch(View v, MotionEvent me) {
            ViewGroup parent = PopCornMenu.this;

            switch (me.getAction()) {
                case MotionEvent.ACTION_MOVE:

                    // *****************************
                    // *        Screen size        *
                    // *****************************
                    Activity host = (Activity) getContext();
                    Display display = host.getWindowManager().getDefaultDisplay();
                    View content = host.getWindow().findViewById(Window.ID_ANDROID_CONTENT);
                    int width = display.getWidth();
                    int heightWithTabBar = display.getHeight();
                    int height = content.getHeight();
                    int difHeight = Math.abs(heightWithTabBar - height);

                    float x = me.getRawX();
                    float y = me.getRawY();
                    Log.d(Constants.TAG_DEBUG, "(x, y): (" + x + ", " + y + ")");
                    float radio = v.getWidth() / 2;

                    // ************************
                    // *        Limits        *
                    // ************************
                    boolean rightLimit = x + radio < width;
                    boolean leftLimit = x - radio > 0;

                    boolean bottomLimit = y + radio < height + difHeight;
                    boolean topLimit = y - radio > difHeight;

                    if (leftLimit && rightLimit) {
                        if (!removeRightRule) {
                            RelativeLayout.LayoutParams parentParamsAux = (RelativeLayout.LayoutParams) parent.getLayoutParams();
                            parentParamsAux.removeRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                            removeRightRule = true;
                        }
                        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) v.getLayoutParams();
                        params.leftMargin = (int) (me.getRawX() - (v.getWidth() / 2));
                        v.setLayoutParams(params);
                    }

                    if (bottomLimit && topLimit) {
                        if (!removeBottomRule) {
                            RelativeLayout.LayoutParams parentParamsAux = (RelativeLayout.LayoutParams) parent.getLayoutParams();
                            parentParamsAux.removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                            removeBottomRule = true;
                        }
                        RelativeLayout.LayoutParams parentParams = (RelativeLayout.LayoutParams) parent.getLayoutParams();
                        parentParams.topMargin = (int) (me.getRawY() - (parent.getHeight()));
                        parent.setLayoutParams(parentParams);
                    }

                    // *******************************
                    // *     Muevo el viewgroup      *
                    // *******************************

                    break;

                case MotionEvent.ACTION_UP:
                    setUpTabBarButton();
                    break;
            }
            return true;
        }
    }

    // ****************************
    // *        Customize         *
    // ****************************
    public void setOpenAniationDuration(long openAniationDuration) {
        this.openAniationDuration = openAniationDuration;
    }

    public void setCloseAniationDuration(long closeAniationDuration) {
        this.closeAniationDuration = closeAniationDuration;
    }

    /**
     * Set open and close animation to @param animationDuration
     *
     * @param animationDuration Animation duration for open and close animation
     */
    public void setAnimationDuration(long animationDuration) {
        setOpenAniationDuration(animationDuration);
        setCloseAniationDuration(animationDuration);
    }

    /**
     * Change the menu button backgound color. Not the image button, just the background.
     *
     * @param color You have 3 option:
     *              1.- 0xAAHHHHHH (0x AA: Alpha; HHHHHH: Hex color)
     *              2.- android.graphics.Color.RED
     *              3.- getResources().getColor(R.color.your_resource_color)
     */
    public void changeMenuColor(int color) {
        fab.getBackground().setColorFilter(color, PorterDuff.Mode.MULTIPLY );
    }

    // **********************
    // *        Utils       *
    // **********************
    public int getPcFromDP(int dps) {
        final float scale = getContext().getResources().getDisplayMetrics().density;

        return (int) (dps * scale + 0.5f);
    }

}

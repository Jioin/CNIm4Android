package com.mingchu.cnim4android.activitys;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomNavigationView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.ViewTarget;
import com.mingchu.cnim4android.R;
import com.mingchu.cnim4android.activitys.AccountActivity;
import com.mingchu.cnim4android.fragment.assist.PermissionsFragment;
import com.mingchu.cnim4android.fragment.main.ActiveFragment;
import com.mingchu.cnim4android.fragment.main.ContactFragment;
import com.mingchu.cnim4android.fragment.main.GroupFragment;
import com.mingchu.cnim4android.utils.NavFraHelper;
import com.mingchu.common.app.BaseActivity;
import com.mingchu.common.widget.custom.PortraitView;
import com.mingchu.factory.persistence.Account;

import net.qiujuer.genius.ui.Ui;
import net.qiujuer.genius.ui.widget.FloatActionButton;

import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends BaseActivity implements
        BottomNavigationView.OnNavigationItemSelectedListener,
        NavFraHelper.OnTabChangedListener<Integer> {


    @BindView(R.id.appbar)
    AppBarLayout mLayAppBar;

    @BindView(R.id.fragment_container)
    FrameLayout mFrameLayoutContainer;

    @BindView(R.id.navigation_bottom)
    BottomNavigationView mNavigationView;

    @BindView(R.id.bt_action)
    FloatActionButton mActionButton;

    @BindView(R.id.iv_search)
    ImageView mIvSearch;


    @BindView(R.id.iv_portrait)
    PortraitView mPortraitView;


    @BindView(R.id.txt_title)
    TextView mTxtTitle;


    NavFraHelper mNavFraHelper;

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_main;
    }

    /**
     * MainActivity
     *
     * @param context 上下
     */
    public static void show(Context context) {
        context.startActivity(new Intent(context, MainActivity.class));
    }


    @Override
    protected void initView() {
        super.initView();

        mNavFraHelper = new NavFraHelper(this, R.id.fragment_container, getSupportFragmentManager()
                , this);

        mNavFraHelper.addTab(R.id.action_home,
                new NavFraHelper.Tab(ActiveFragment.class, R.string.title_home))
                .addTab(R.id.action_group,
                        new NavFraHelper.Tab(GroupFragment.class, R.string.title_group))
                .addTab(R.id.action_contact,
                        new NavFraHelper.Tab(ContactFragment.class, R.string.title_contact));

        Glide.with(this).load(R.mipmap.bg_src_morning)
                .centerCrop()
                .into(new ViewTarget<View, GlideDrawable>(mLayAppBar) {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                        this.view.setBackground(resource.getCurrent());
                    }
                });

        //要这样写  要不然找不到控件  我擦嘞
        mNavigationView.setOnNavigationItemSelectedListener(this);

//        Animation operatingAnim = AnimationUtils.loadAnimation(this, R.anim.rotain);
//        LinearInterpolator lin = new LinearInterpolator();
//        operatingAnim.setInterpolator(lin);
//
//        mIvArRow.setAnimation(operatingAnim);
//
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//             mIvArRow.setAnimation(null);
//            }
//        },4000);

    }


    @Override
    protected void initData() {
        super.initData();
        Menu menu = mNavigationView.getMenu();//从底部导航中拿到我们的menu  触发第一次点击
        menu.performIdentifierAction(R.id.action_home, 0);

        //初始化头像加载
        mPortraitView.setup(Glide.with(this), Account.getUser());
    }

    @OnClick(R.id.iv_search)
    void onSearchMenuClick() {
        int type = Objects.equals(
                mNavFraHelper.getCurrentTab().extra,
                R.string.title_group) ?
                SearchActivity.TYPE_GROUP : SearchActivity.TYPE_USER;
        SearchActivity.show(this, type);
    }


    @OnClick(R.id.iv_portrait)
    void onPortraitClick() {
        PersonalActivity.show(this, Account.getUserId());
    }

    @OnClick(R.id.bt_action)
    void onActionClick() {
        //浮动按钮判断当前界面是群还是联系人界面
        //如果是人  则打开群创建的界面  如果是其他都打开用户界面
        if (Objects.equals(mNavFraHelper.getCurrentTab().extra, R.string.title_group)) {
            GroupCreateActivity.show(this);   //打开群组创建界面
        } else {  //打开人的界面
            SearchActivity.show(this, SearchActivity.TYPE_USER);
        }

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return mNavFraHelper.performClikcMenu(item.getItemId());
    }

    /**
     * NavHelper  处理后回调的方法
     *
     * @param newTab 新的tab
     * @param oldTab 旧的tab
     */
    @Override
    public void onTabChanged(NavFraHelper.Tab<Integer> newTab, NavFraHelper.Tab<Integer> oldTab) {
        //从额外字段中取出Title的资源id
        mTxtTitle.setText(newTab.extra);

        //对浮动按钮进行隐藏和显示的动画
        float transY = 0;
        float rotation = 0;
        if (Objects.equals(newTab.extra, R.string.title_home)) {
            //主界面隐藏
            transY = Ui.dipToPx(getResources(), 76);
        } else {
            //默认为0  则显示
            if (Objects.equals(newTab.extra, R.string.title_group)) {
                //群
                mActionButton.setImageResource(R.drawable.ic_group_add);
                rotation = -360;
            } else {
                //联系人
                mActionButton.setImageResource(R.drawable.ic_contact_add);
                rotation = 360;
            }

        }

        mActionButton.animate()
                .rotation(rotation)
                .translationY(transY)
                .setInterpolator(new AnticipateOvershootInterpolator(1))
                .setDuration(480)
                .start();

    }
}

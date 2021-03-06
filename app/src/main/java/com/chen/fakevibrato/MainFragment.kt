package com.chen.fakevibrato

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.chen.fakevibrato.base.BaseSupportFragment
import com.chen.fakevibrato.base.DBaseSupportFragment
import com.chen.fakevibrato.base.MyFragmentPagerAdapter
import com.chen.fakevibrato.http.HttpUtils
import com.chen.fakevibrato.ui.home.presenter.DMainPresenter
import com.chen.fakevibrato.ui.home.presenter.MainPresenter
import com.chen.fakevibrato.ui.home.view.HomeFragment
import com.chen.fakevibrato.ui.message.view.MessageFragment
import com.chen.fakevibrato.ui.my.view.UserFragment
import com.chen.fakevibrato.ui.samecity.view.SameCityFragment
import com.chen.fakevibrato.utils.DisplayUtils
import com.chen.fakevibrato.utils.MyLog
import com.chen.fakevibrato.widget.MyStatePagerAdapter

import com.chen.fakevibrato.widget.swipe.MySwipeLayout
import com.chen.functionmanager.FunctionHasParamNoResult
import com.chen.functionmanager.FunctionManager
import com.flyco.tablayout.listener.CustomTabEntity
import com.flyco.tablayout.listener.OnTabSelectListener
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.layout_side_right.*
import java.util.*


/**
 * home
 */
class MainFragment : BaseSupportFragment<MainPresenter>() {


//    private var adapter: MyStatePagerAdapter? = null
    private var adapter: MyFragmentPagerAdapter? = null
    private val mFragments = ArrayList<Fragment>()
//    private var registeredFragments = SparseArray<Fragment>()
    internal var mTitles = arrayOf("首页", "同城", "", "消息", "我")
    private var sameCityFragment: SameCityFragment? = null
    private var fragment: Fragment? = null
    private var messageFragment: MessageFragment? = null
    private var userFragment: UserFragment? = null
    private var userPosition = 0


    override fun setView(): Int {
        return R.layout.fragment_main
    }

    override fun initPresenter(): MainPresenter {
        return MainPresenter()
    }

    override fun onFirstVisible() {

    }


    override fun initView() {

        sameCityFragment = SameCityFragment()
        fragment = Fragment()
        messageFragment = MessageFragment()
        userFragment = UserFragment()
        mFragments.add(HomeFragment())
        sameCityFragment?.let { mFragments.add(it) }
        fragment?.let { mFragments.add(it) }
        messageFragment?.let { mFragments.add(it) }
        userFragment?.let { mFragments.add(it) }

//        registeredFragments.put(0, HomeFragment())
        adapter = activity?.let { MyFragmentPagerAdapter(it, mFragments) }
        viewPager.adapter = adapter
        viewPager.isUserInputEnabled = false
        viewPager.offscreenPageLimit = mFragments.size
//        viewPager.setSwipeable(false)

        val mTabEntities = ArrayList<CustomTabEntity>()
        for (i in mTitles.indices) {
            val finalI = i
            mTabEntities.add(object : CustomTabEntity {
                override fun getTabTitle(): String {
                    return mTitles[finalI]
                }

                override fun getTabSelectedIcon(): Int {
                    return 0
                }

                override fun getTabUnselectedIcon(): Int {
                    return 0
                }
            })
        }
        mTabLayout.setTabData(mTabEntities)
        initListener()
    }


    private fun initListener() {
        swipeLayout.isSwipe = true
        swipeLayout.setScale(1f)
        FunctionManager.instance.addFunction(object : FunctionHasParamNoResult<Int>("isSwipe") {
            override fun function(p: Int) {
                userPosition = p
                swipeLayout.isSwipe = viewPager.currentItem == 4 && userPosition == 2
            }
        })
        FunctionManager.instance.addFunction(object : FunctionHasParamNoResult<Boolean>("openSwipe") {
            override fun function(p: Boolean) {
                if (swipeLayout.status == MySwipeLayout.Status.Open) {
                    swipeLayout.close()
                } else {
                    swipeLayout.open()
                }
            }
        })
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                if (mTabLayout.currentTab != position) {
                    mTabLayout.currentTab = position
                }
                swipeLayout.isSwipe = position == 4 && userPosition == 2
            }
        })

//        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
//            override fun onPageScrollStateChanged(state: Int) {
//
//            }
//
//            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
//
//            }
//
//            override fun onPageSelected(position: Int) {
//                if (mTabLayout.currentTab != position) {
//                    mTabLayout.currentTab = position
//                }
//                swipeLayout.isSwipe = position == 4 && userPosition == 2
//            }
//        })


        val textView = mTabLayout.getTitleView(0)
        val text = textView.text.toString().trim { it <= ' ' }
        val textPaint = textView.paint
        val textPaintWidth = textPaint.measureText(text).toInt()
        mTabLayout.indicatorWidth = DisplayUtils.px2dp(activity, textPaintWidth.toFloat()).toFloat()

        mTabLayout.setOnTabSelectListener(object : OnTabSelectListener {
            override fun onTabSelect(position: Int) {
                MyLog.d("viewPager: $position   $viewPager")
//                viewPager.currentItem = position
                val textView = mTabLayout.getTitleView(position)
                val text = textView.text.toString().trim { it <= ' ' }
                val textPaint = textView.paint
                val textPaintWidth = textPaint.measureText(text).toInt()
                mTabLayout.indicatorWidth = DisplayUtils.px2dp(activity, textPaintWidth.toFloat()).toFloat()
//                onDispatchSwipeListener.isDispatchSwipe(position != 0)
                FunctionManager.instance.invokeFunction("mainSwipeLayout", position == 0)
                if (position == 0) {
                    mFragments.removeAt(4)
                    mFragments.removeAt(3)
                    mFragments.removeAt(2)
                    mFragments.removeAt(1)
                    adapter?.notifyDataSetChanged()
                } else {
                    if (mFragments.size == 1) {
                        if (sameCityFragment == null) {
                            sameCityFragment = SameCityFragment()
                            sameCityFragment?.let { mFragments.add(it) }
                        } else {
                            sameCityFragment?.let { mFragments.add(it) }
                        }

                        if (fragment == null) {
                            fragment = Fragment()
                            fragment?.let { mFragments.add(it) }
                        } else {
                            fragment?.let { mFragments.add(it) }
                        }

                        if (messageFragment == null) {
                            messageFragment = MessageFragment()
                            messageFragment?.let { mFragments.add(it) }
                        } else {
                            messageFragment?.let { mFragments.add(it) }
                        }

                        if (userFragment == null) {
                            userFragment = UserFragment()
                            userFragment?.let { mFragments.add(it) }
                        } else {
                            userFragment?.let { mFragments.add(it) }
                        }
                        adapter?.notifyDataSetChanged()
                    }
                }

                if (viewPager.currentItem != position) {
                    viewPager.setCurrentItem(position, false)
                }
            }

            override fun onTabReselect(position: Int) {
            }
        })

        ivBottom.setOnClickListener {
            bottomClick()
        }

        tvService.setOnClickListener {
            if (llService.visibility == View.GONE) {
                llService.visibility = View.VISIBLE
                viewService.visibility = View.INVISIBLE
            } else {
                llService.visibility = View.GONE
                viewService.visibility = View.VISIBLE
            }
        }
    }

    private fun bottomClick() {

//    startActivity(Intent(activity, TestViewActivity::class.java))
//    startActivity(Intent(activity, StackActivity::class.java))
//    startActivity(Intent(activity, SwipeActivity::class.java))
        startActivity(Intent(activity, CameraActivity::class.java))
    }

    override fun initData() {

    }

    override fun Load() {

    }

    private fun httptest() {
        val map = HashMap<String, String>()
        map["token"] = ""
        map["appId"] = ""
        map["timeStamp"] = ""
        map["sign"] = ""
        if (activity != null) {
            activity?.let { it1 ->
                //                    HttpUtils.with(it1)
//                    HttpUtils.init(OkHttpEngine())
                HttpUtils.with(it1).url("https:/auth/getUserInfo")
                        .get().addParams(map.toMutableMap()).execute()
            }
        }
    }
}

package com.chen.fakevibrato.ui.my.view

import androidx.recyclerview.widget.LinearLayoutManager
import com.chen.fakevibrato.R
import com.chen.fakevibrato.base.DBaseSupportActivity
import com.chen.fakevibrato.ui.home.presenter.DMainPresenter
import com.chen.fakevibrato.ui.my.adapter.PhoneContactAdapter
import kotlinx.android.synthetic.main.activity_phone_contact.*

/**
 * 手机通讯录
 */
class PhoneContactActivity : DBaseSupportActivity<DMainPresenter>() {
    var adapter : PhoneContactAdapter? = null
    var mList = ArrayList<String>()

    override fun getLayoutId(): Int {
        return R.layout.activity_phone_contact
    }

    override fun initPresenter(): DMainPresenter {
        return DMainPresenter()
    }

    override fun initView() {
        initToolbar(toolbar)
        mList.add("ad")
        recyclerView.layoutManager = LinearLayoutManager(this@PhoneContactActivity)
        adapter = PhoneContactAdapter(this@PhoneContactActivity, mList)
        recyclerView.adapter = adapter
    }

    override fun initListener() {
        ivBack.setOnClickListener {
            finish()
        }
    }

    override fun initData() {

    }
}
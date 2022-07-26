package com.h.android.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.h.android.utils.HLog

/**
 *2020/11/27
 *@author zhangxiaohui
 *@describe
 */
open class HFragment : Fragment(){
    private var rootView: View? = null
    private var firstCreateView = true


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        HLog.d("--->life:onCreateView---" + javaClass.name)
        if (rootView == null) {
            rootView = onCreateViewOnce(inflater, container, savedInstanceState)
            firstCreateView = true
        } else {
            firstCreateView = false
        }
        return if (rootView == null) super.onCreateView(
            inflater,
            container,
            savedInstanceState
        ) else rootView
    }

    /**
     * onCreateView方法执行一次
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    open fun onCreateViewOnce(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        HLog.d("--->life:onCreateViewOnce---" + javaClass.name)
        return null
    }

    /**
     * onViewCreated方法执行一次
     * @param view
     * @param savedInstanceState
     */
    open fun onViewCreateOnce(view: View, savedInstanceState: Bundle?) {
        HLog.d("--->life:onViewCreateOnce---" + javaClass.name)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        HLog.d("--->life:onViewCreated---" + javaClass.name)
        if (firstCreateView) {
            onViewCreateOnce(view, savedInstanceState)
        }
    }

    override fun onResume() {
        super.onResume()
        HLog.d("--->life:onResume---" + javaClass.name)
    }

    override fun onPause() {
        super.onPause()
        HLog.d("--->life:onPause---" + javaClass.name)
    }

    override fun onDestroy() {
        super.onDestroy()
        HLog.d("--->life:onDestroy---" + javaClass.name)
    }
}
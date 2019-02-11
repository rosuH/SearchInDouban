package me.rosuh.searchindouban

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.text.method.LinkMovementMethod
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

/**
 * 这个类是用来显示 App 作者信息的；
 * * @author rosuh 2018-4-24
 */

class AboutPageDialog : DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.about_page_dialog, container, false)
        val tv = view.findViewById<TextView>(R.id.project_page_text_view)
        tv.movementMethod = LinkMovementMethod.getInstance()
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        isCancelable = true
        val window = dialog.window
        window!!.setGravity(Gravity.CENTER)
        window.setWindowAnimations(R.style.animate_dialog)
//        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }
}

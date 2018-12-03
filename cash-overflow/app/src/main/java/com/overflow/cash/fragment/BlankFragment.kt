package com.overflow.cash.fragment


import android.os.Bundle
import android.support.v4.app.Fragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_blank.*
import com.overflow.cash.R


/**
 * Create  [BlankFragment] to handle when data is empty.
 * @author kiditz
 *
 */
class BlankFragment : Fragment() {
    /**
     * <pre>
     *    Input: exitImage, title, description
     * </pre>
     * */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_blank, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tv_title.text = arguments?.getString(ARG_TITLE)
        tv_description.text = arguments?.getString(ARG_DESCRIPTION)
    }

    companion object {
        const val ARG_TITLE = "title"
        const val ARG_DESCRIPTION = "description"
        @JvmStatic
        fun newInstance(title: String, description:String="") =
                BlankFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_TITLE, title)
                        putString(ARG_DESCRIPTION, description)
                    }
                }
    }

}

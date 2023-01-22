package com.example.vibeing.adapters.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.vibeing.R

class DropdownAdapter(ctx: Context, private val visibilityList: ArrayList<Pair<Int, String>>) : ArrayAdapter<Pair<Int, String>>(ctx, 0, visibilityList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return initView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return initView(position, convertView, parent)
    }

    private fun initView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_spinner_dropdown, parent, false)
        val textView = view.findViewById<TextView>(R.id.dropdownTxt)
        val imgView = view.findViewById<ImageView>(R.id.dropdownImg)
        view.setPadding(8, 0, 10, 0)
        textView.textSize = 13.0f
        imgView.setPadding(5, 8, 0, 2)
        textView.setPadding(5, 5, 10, 2)

        val item = visibilityList[position]
        if (view != null) {
            textView.text = item.second
            imgView.setImageResource(item.first)
        }
        return view
    }
}
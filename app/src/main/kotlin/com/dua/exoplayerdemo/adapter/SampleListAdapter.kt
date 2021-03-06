package com.dua.exoplayerdemo.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.dua.exoplayerdemo.R
import com.dua.exoplayerdemo.data.Samples


class SampleListAdapter(context: Context, private val samples: List<Samples.Sample>) : BaseAdapter() {
    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return samples.size
    }

    override fun getItem(position: Int): Any {
        return samples[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        val holder: ViewHolder

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.simple_text_item, null)

            holder = ViewHolder()
            holder.text = convertView!!.findViewById(R.id.simple_text_text_view)
            convertView.tag = holder
        } else {
            holder = convertView.tag as ViewHolder
        }

        holder.text!!.text = samples[position].title
        return convertView
    }

    private class ViewHolder {
        internal var text: TextView? = null
    }
}
package com.srizan.weatherapp.adapters

import android.widget.TextView
import androidx.databinding.BindingAdapter
import kotlin.math.roundToInt

@BindingAdapter("setTemp")
fun setTemp(textView: TextView, temp : Double){
    val tempText = "${temp.roundToInt()}°c"
    textView.text = tempText
}
package com.ak.ui

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.annotation.FontRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.FragmentActivity
import com.ak.countrypicker.R
import com.ak.countrypicker.databinding.LayoutCodePickerBinding
import com.ak.fragment.BottomCountryPickerFragment
import com.ak.model.CountryItem
import com.ak.utils.Constants.list
import com.ak.utils.CountryCodeHelper
import com.ak.utils.setCountryList
import java.util.Locale


class CountryCodePicker(context: Context, attributeSet: AttributeSet) :
    LinearLayout(context, attributeSet), BottomCountryPickerFragment.OnClickItemListener {
    private var binding: LayoutCodePickerBinding? = null
    private var fragment: BottomCountryPickerFragment? = null
    private var color: Int = 0
    private var fontFamilyId = 0
    private var showFlag: Boolean = true
    private var showCodeName: Boolean = true
    private var showDropDownArrow: Boolean = true
    private var defaultCountry: String = ""
    private var excludedCountries: String = ""
    private var textSize = 0
    private var arrowSize = 0

    init {
        binding = LayoutCodePickerBinding.inflate(
            LayoutInflater.from(context), this, true
        )

        context.theme.obtainStyledAttributes(
            attributeSet, R.styleable.CountryCodePicker, 0, 0
        ).apply {
            try {
                showFlag = getBoolean(R.styleable.CountryCodePicker_showFlag, true)
                defaultCountry = getString(R.styleable.CountryCodePicker_defaultNameCode) ?: ""
                excludedCountries =
                    getString(R.styleable.CountryCodePicker_excludedCountries).toString()
                showCodeName = getBoolean(R.styleable.CountryCodePicker_showNameCode, true)
                showDropDownArrow = getBoolean(R.styleable.CountryCodePicker_showArrowDown, true)
                color = getResourceId(R.styleable.CountryCodePicker_contentColor, 0)
                fontFamilyId = getResourceId(R.styleable.CountryCodePicker_android_fontFamily, 0)
                textSize = getDimensionPixelSize(R.styleable.CountryCodePicker_textSize, 0)
                arrowSize = getDimensionPixelSize(R.styleable.CountryCodePicker_arrowSize, 0)
            } finally {
                recycle()
            }

        }

        setCountryList()

        if (textSize > 0) {
            binding?.tvCode?.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize.toFloat())
            binding?.tvNameCode?.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize.toFloat())
        }

        setContentColor(if (color > 0) color else R.color.text_color)

        if (arrowSize > 0) {
            setArrowSize(arrowSize)
        }
        if (fontFamilyId > 0) {
            setTypeFace(fontFamilyId)
        }
        if (!showFlag) {
            hideFlag()
        }
        if (!showCodeName) {
            hideCodeName()
        }
        if (!showDropDownArrow) {
            hideDropDownArrow()
        }

        if (defaultCountry.isNotEmpty()) {
            setDefaultCountry(defaultCountry)
        } else {
            val countryCode = CountryCodeHelper.getDetectedCountry(context, "in")
            setDefaultCountry(countryCode)
        }

        if (excludedCountries.isNotEmpty()) {
            setExcludedCountries(excludedCountries)
        }

        binding?.llNoCodeLayout?.setOnClickListener {
            if (list.isNotEmpty()) {
                fragment = BottomCountryPickerFragment()
                fragment?.setClickListener(this, list)
                fragment?.show((context as FragmentActivity).supportFragmentManager, "Tag")
            }
        }
    }

    override fun onCountryItemClick(item: CountryItem?) {
        item?.flagImage?.let { binding?.ivFlag?.setImageResource(it) }
        item?.phoneCode?.let { binding?.tvCode?.text = "+$it" }
        item?.codeName?.let { binding?.tvNameCode?.text = "($it)".uppercase() }
        fragment?.dismiss()
    }

    fun setContentColor(color: Int) {
        if (Build.VERSION.SDK_INT < 23) {
            binding?.tvCode?.setTextColor(color)
            binding?.tvNameCode?.setTextColor(color)
        }
        if (Build.VERSION.SDK_INT >= 23) {
            binding?.tvCode?.setTextColor(ContextCompat.getColor(context, color))
            binding?.tvNameCode?.setTextColor(ContextCompat.getColor(context, color))
        }
    }

    private fun setTypeFace(@FontRes font: Int) {
        binding?.tvCode?.typeface = ResourcesCompat.getFont(context, font)
        binding?.tvNameCode?.typeface = ResourcesCompat.getFont(context, font)
    }

    fun hideFlag() {
        binding?.ivFlag?.visibility = View.GONE
    }

    fun hideCodeName() {
        binding?.tvNameCode?.visibility = View.GONE
    }

    fun hideDropDownArrow() {
        binding?.ivArrow?.visibility = View.GONE
    }

    fun setDefaultCountry(codeName: String) {
        for (item in list) {
            if (item.codeName?.equals(codeName) == true) {
                item.flagImage?.let { binding?.ivFlag?.setImageResource(it) }
                item.phoneCode?.let { binding?.tvCode?.text = "+$it" }
                item.codeName.let { binding?.tvNameCode?.text = "($it)".uppercase() }
                break
            }
        }
    }

    fun setExcludedCountries(excludedCountries: String) {
        println("excludedCountries==$excludedCountries")
        this.excludedCountries = excludedCountries.lowercase(Locale.getDefault())
        println("this.excludedCountries==${this.excludedCountries}")

        if (excludedCountries.contains(",")) {
            val codeNameArray = excludedCountries.split(",").toTypedArray()
            codeNameArray.let {
                for (i in it.indices) {
                    for (item in list) {
                        if (item.codeName?.equals(it[i]) == true) {
                            list.remove(item)
                            break
                        }
                    }
                }
            }

        } else {
            for (item in list) {
                if (item.codeName == excludedCountries) {
                    list.remove(item)
                }
            }

        }
    }

    fun setArrowSize(arrowSize: Int) {
        if (arrowSize > 0) {
            val params = binding?.ivArrow?.layoutParams as LayoutParams
            params.width = arrowSize
            params.height = arrowSize
            binding?.ivArrow?.layoutParams = params
        }
    }

    fun selectedCountryCode(): String {
        return binding?.tvCode?.text.toString()
    }

//    fun selectedCountryName(): String {
//        return binding?.tvCode?.text.toString()
//    }

}
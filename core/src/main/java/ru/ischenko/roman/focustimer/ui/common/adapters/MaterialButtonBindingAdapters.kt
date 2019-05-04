package ru.ischenko.roman.focustimer.ui.common.adapters

import androidx.annotation.ColorRes
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.BindingAdapter
import com.google.android.material.button.MaterialButton

/**
 * User: roman
 * Date: 04.05.19
 * Time: 15:15
 */

object MaterialButtonBindingAdapters {

    @JvmStatic
    @BindingAdapter("backgroundTint")
    fun updateTimeBindingAdapter(materialButton: MaterialButton, @ColorRes backgroundTint: Int) {
        materialButton.backgroundTintList =
                ResourcesCompat.getColorStateList(materialButton.resources, backgroundTint, materialButton.context.theme)
    }
}
package ru.ischenko.roman.focustimer.ui.common.adapters

import android.graphics.drawable.Drawable
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
    fun backgroundTintBindingAdapter(materialButton: MaterialButton, @ColorRes backgroundTint: Int) {
        materialButton.backgroundTintList =
                ResourcesCompat.getColorStateList(materialButton.resources, backgroundTint, materialButton.context.theme)
    }

    @JvmStatic
    @BindingAdapter("icon")
    fun buttonIconBindingAdapter(materialButton: MaterialButton, icon: Drawable) {
        materialButton.icon = icon
    }
}
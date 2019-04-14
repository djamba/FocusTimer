package ru.ischenko.roman.focustimer.di

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders

inline fun <reified T : ViewModel> FragmentActivity.injectViewModel(viewModelFactory: ViewModelProvider.Factory): T =
        ViewModelProviders.of(this, viewModelFactory).get(T::class.java)

inline fun <reified T : ViewModel> Fragment.injectViewModel(viewModelFactory: ViewModelProvider.Factory): T =
        ViewModelProviders.of(this, viewModelFactory).get(T::class.java)

inline fun <reified T : ViewModel> Fragment.injectSharedViewModel(viewModelFactory: ViewModelProvider.Factory): T =
        ViewModelProviders.of(this.requireActivity(), viewModelFactory).get(T::class.java)
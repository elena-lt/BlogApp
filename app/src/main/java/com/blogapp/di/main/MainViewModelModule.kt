package com.blogapp.di.main

import androidx.lifecycle.ViewModel
import com.blogapp.di.ViewModelKey
import com.blogapp.ui.main.account.AccountViewModel
import com.blogapp.ui.main.blogs.BlogViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class MainViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(AccountViewModel::class)
    abstract fun bindAccountViewModel(accountViewModel: AccountViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(BlogViewModel::class)
    abstract fun bindBlogViewModel(accountViewModel: BlogViewModel): ViewModel
}
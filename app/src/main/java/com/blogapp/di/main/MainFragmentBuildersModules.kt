package com.blogapp.di.main

import com.blogapp.ui.main.account.AccountFragment
import com.blogapp.ui.main.account.ChangePasswordFragment
import com.blogapp.ui.main.account.UpdateAccountFragment
import com.blogapp.ui.main.blogs.BlogFragment
import com.blogapp.ui.main.blogs.UpdateBlogFragment
import com.blogapp.ui.main.blogs.ViewBlogFragment
import com.blogapp.ui.main.createPost.CreateNewBlogFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MainFragmentBuildersModules {

    @ContributesAndroidInjector()
    abstract fun contributeBlogFragment(): BlogFragment

    @ContributesAndroidInjector()
    abstract fun contributeAccountFragment(): AccountFragment

    @ContributesAndroidInjector()
    abstract fun contributeChangePasswordFragment(): ChangePasswordFragment

    @ContributesAndroidInjector()
    abstract fun contributeCreateBlogFragment(): CreateNewBlogFragment

    @ContributesAndroidInjector()
    abstract fun contributeUpdateBlogFragment(): UpdateBlogFragment

    @ContributesAndroidInjector()
    abstract fun contributeViewBlogFragment(): ViewBlogFragment

    @ContributesAndroidInjector()
    abstract fun contributeUpdateAccountFragment(): UpdateAccountFragment
}
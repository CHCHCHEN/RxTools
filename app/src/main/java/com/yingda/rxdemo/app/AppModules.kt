package com.yingda.rxdemo.app

import com.yingda.rxdemo.MainViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * author: chen
 * data: 2021/9/13
 * des: 加载koin模块
 */
val viewModelsModule = module {
    viewModel { MainViewModel() }

}
val appModels = listOf(viewModelsModule)

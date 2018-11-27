package com.overflow.cash.net

import android.accounts.AccountManager
import android.content.Context

import io.reactivex.Flowable
import io.reactivex.FlowableTransformer
import io.reactivex.ObservableTransformer
import io.reactivex.SingleTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import timber.log.Timber

/**
 * @author kiditz.
 */

class RxUtils {
    companion object {

        fun <T> applyFlowableAsysnc(): FlowableTransformer<T, T> {
            return FlowableTransformer { flowable -> flowable.observeOn(AndroidSchedulers.mainThread()) }
        }

        fun <T> applyObservableAsync(): ObservableTransformer<T, T> {
            return ObservableTransformer { observable ->
                observable.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
            }
        }

        fun <T> applySingleAsync(): SingleTransformer<T, T> {
            return SingleTransformer{ observable ->
                observable.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
            }
        }

        fun <T> applyObservableCompute(): ObservableTransformer<T, T> {
            return ObservableTransformer { observable ->
                observable.subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
            }
        }

        fun <T> applyObservableMainThread(): ObservableTransformer<T, T> {
            return ObservableTransformer { observable -> observable.observeOn(AndroidSchedulers.mainThread()) }
        }

        fun <T> applyFlowableMainThread(): FlowableTransformer<T, T> {
            return FlowableTransformer { flowable -> flowable.observeOn(AndroidSchedulers.mainThread()) }
        }

        fun <T> applyFlowableCompute(): FlowableTransformer<T, T> {
            return FlowableTransformer { flowable -> flowable.observeOn(AndroidSchedulers.mainThread()) }
        }

    }

}
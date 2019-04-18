package com.ihomey.linkuphome.data.api


import androidx.lifecycle.LiveData
import java.lang.reflect.Type
import java.util.concurrent.atomic.AtomicBoolean
import retrofit2.*
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException


/**
 * A Retrofit adapter that converts the Call into a LiveData of ApiResponse.
 * @param <R>
</R> */
class LiveDataCallAdapter<R>(private val responseType: Type) :
        CallAdapter<ApiResult<R>, LiveData<ApiResult<R>>> {

    override fun responseType() = responseType

    override fun adapt(call: Call<ApiResult<R>>): LiveData<ApiResult<R>> {
        return object : LiveData<ApiResult<R>>() {
            private var started = AtomicBoolean(false)
            override fun onActive(){
                super.onActive()
                if (started.compareAndSet(false, true)) {
                    call.enqueue(object : Callback<ApiResult<R>> {
                        override fun onResponse(call: Call<ApiResult<R>>, response: Response<ApiResult<R>>) {
                            postValue(response.body())
                        }
                        override fun onFailure(call: Call<ApiResult<R>>, throwable: Throwable) {
//                            if (throwable is HttpException||throwable is SocketTimeoutException||throwable is IOException) {
//                                postValue(throwable.message?.let { ApiResult<R>("10000", it,null) })
//                            } else {
////                                view.onUnknownError(e.getMessage())
//                            }
                        }
                    })
                }
            }
        }
    }
}
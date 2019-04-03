package com.ihomey.linkuphome.data.api


import androidx.lifecycle.LiveData
import java.lang.reflect.Type
import java.util.concurrent.atomic.AtomicBoolean
import android.util.Log
import retrofit2.*
import java.net.UnknownHostException
import java.net.UnknownServiceException


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
                            throwable.printStackTrace()
                            if (throwable is UnknownHostException||throwable is UnknownServiceException) {
                                postValue(throwable.message?.let { ApiResult<R>("10000", it,null) })
                            }else{

                            }
                        }
                    })
                }
            }
        }
    }
}
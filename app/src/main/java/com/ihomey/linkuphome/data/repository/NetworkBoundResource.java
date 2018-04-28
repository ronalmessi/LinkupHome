package com.ihomey.linkuphome.data.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;

import com.ihomey.linkuphome.AppExecutors;
import com.ihomey.linkuphome.data.vo.Resource;

/**
 * Created by dongcaizheng on 2018/1/11.
 */

public abstract class NetworkBoundResource<ResultType> {

    private final MediatorLiveData<Resource<ResultType>> result = new MediatorLiveData<>();

    @MainThread
    NetworkBoundResource(AppExecutors appExecutors) {
        result.setValue(Resource.loading(null));
        appExecutors.diskIO().execute(() -> {
            LiveData<ResultType> dbSource = loadFromDb();
            appExecutors.mainThread().execute(() ->
                    result.addSource(dbSource, value -> result.setValue(Resource.success(value)))
            );
        });
    }


    public LiveData<Resource<ResultType>> asLiveData() {
        return result;
    }

    @NonNull
    @MainThread
    protected abstract LiveData<ResultType> loadFromDb();

}

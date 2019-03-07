package com.ihomey.linkuphome.data.repository;

import com.ihomey.linkuphome.AppExecutors;
import com.ihomey.linkuphome.data.vo.Resource;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

/**
 * Created by dongcaizheng on 2018/1/11.
 */

public abstract class DbBoundResource<ResultType> {

    private final MediatorLiveData<Resource<ResultType>> result = new MediatorLiveData<>();

    @MainThread
    DbBoundResource(AppExecutors appExecutors) {
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

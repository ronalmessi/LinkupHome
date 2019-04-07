/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ihomey.linkuphome.data.api;



import androidx.lifecycle.LiveData;
import retrofit2.CallAdapter;
import retrofit2.Retrofit;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class LiveDataCallAdapterFactory extends CallAdapter.Factory {

    @Override
    public CallAdapter<?, ?> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {
        if (getRawType(returnType) != LiveData.class) {
            return null;
        }
        Type observableType = getParameterUpperBound(0, (ParameterizedType) returnType);
//        Class<?> rawObservableType = getRawType(observableType);
//        if (rawObservableType != ApiResult.class) {
//            throw new IllegalArgumentException("type must be a resource");
//        }
//        if (!(observableType instanceof ParameterizedType)) {
//            throw new IllegalArgumentException("resource must be parameterized");
//        }
//        Type bodyType = getParameterUpperBound(0, (ParameterizedType) observableType);
//        Type resultType = new ParameterizedTypeImpl(ApiResult.class, new Class[]{getRawType(bodyType)});
        return new LiveDataCallAdapter<>(observableType);
    }
}
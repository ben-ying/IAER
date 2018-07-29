package com.yjh.iaer.network;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Observer;
import android.os.AsyncTask;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.widget.Toast;

import com.yjh.iaer.MyApplication;
import com.yjh.iaer.R;

public abstract class NetworkBoundResource<ResultType, RequestType> {
    private final MediatorLiveData<Resource<ResultType>> mResult = new MediatorLiveData<>();

    @MainThread
    public NetworkBoundResource() {
        mResult.setValue(Resource.loading(null));
        final LiveData<ResultType> dbSource = loadFromDb();

        mResult.addSource(dbSource, resultType -> {
            mResult.removeSource(dbSource);
            if (shouldFetch(resultType)) {
                fetchFromNetwork(dbSource);
            } else {
                mResult.addSource(dbSource, resultType1 -> {
                    mResult.setValue(Resource.success(resultType));
                });
            }
        });
    }

    private void fetchFromNetwork(final LiveData<ResultType> dbSource) {
        // start progress
        final LiveData<ApiResponse<RequestType>> apiResponse = createCall();
        // we re-attach dbSource as a new source,
        // it will dispatch its latest value quickly
        mResult.addSource(dbSource, resultType -> {
                mResult.setValue(Resource.loading(resultType));
        });
        mResult.addSource(apiResponse, requestTypeApiResponse -> {
            mResult.removeSource(apiResponse);
            mResult.removeSource(dbSource);
            if (requestTypeApiResponse.isSuccessful()) {
                saveResultAndReInit(requestTypeApiResponse);
            } else {
                onFetchFailed();
                mResult.addSource(dbSource, resultType -> {
                    mResult.setValue(Resource.error(requestTypeApiResponse.getErrorMessage(), resultType));
                });
            }
        });
    }

    @WorkerThread
    protected RequestType processResponse(ApiResponse<RequestType> response) {
        return response.getBody();
    }

    @MainThread
    private void saveResultAndReInit(final ApiResponse<RequestType> response) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                saveCallResult(response.getBody());
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                final LiveData<ResultType> dbSource = loadFromDb();
                mResult.addSource(dbSource, resultType -> {
                    mResult.setValue(Resource.success(resultType));
                });
            }
        }.execute();
    }

    @WorkerThread
    protected abstract void saveCallResult(@NonNull RequestType item);

    // Called with the data in the database to decide whether it should be
    // fetched from the network.
    @MainThread
    protected abstract boolean shouldFetch(@Nullable ResultType data);

    // Called to get the cached data from the database
    @NonNull
    @MainThread
    protected abstract LiveData<ResultType> loadFromDb();

    // Called to create the API call.
    @Nullable
    @MainThread
    protected abstract LiveData<ApiResponse<RequestType>> createCall();

    // Called when the fetch fails. The child class may want to reset components
    // like rate limiter.
    @MainThread
    protected void onFetchFailed() {
        Toast.makeText(MyApplication.sInstance.getApplicationContext(),
                R.string.network_error, Toast.LENGTH_SHORT).show();
    }

    // returns a LiveData that represents the resource
    public final LiveData<Resource<ResultType>> getAsLiveData() {
        return mResult;
    }
}

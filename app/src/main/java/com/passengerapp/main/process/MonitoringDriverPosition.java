package com.passengerapp.main.process;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by adventis on 10/7/15.
 */
public class MonitoringDriverPosition extends Object  {
    private Timer mTimerToGetDriverPosition = null;
    private List<Integer> mReservatioIds = null;
    private int mTimeout = 10;
    private ICurrentDriverPositionResult delegate;

    public MonitoringDriverPosition(List<Integer> reservatioIds, int timeoutInSeconds) {
        mReservatioIds = reservatioIds;
        mTimeout = timeoutInSeconds;
    }

    public void setDelegate(ICurrentDriverPositionResult delegate) {
        this.delegate = delegate;
    }

    public void runMonitorig() {
        stopMonitoring();

        mTimerToGetDriverPosition = new Timer();
        mTimerToGetDriverPosition.schedule(new TimerTask() {
            @Override
            public void run() {
                //updateDriverLocation();
            }
        }, 0, mTimeout*1000);
    }

    public void stopMonitoring() {
        if(mTimerToGetDriverPosition != null)
            mTimerToGetDriverPosition.cancel();
    }

    /*private void updateDriverLocation() {
        for(Integer reservationId : mReservatioIds) {
            GetCurrentDriverPosition asyncGetDriverPosition = new GetCurrentDriverPosition();
            asyncGetDriverPosition.delegate = delegate;
            asyncGetDriverPosition.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, reservationId);
        }

    }*/


   /* private class GetCurrentDriverPosition extends AsyncTask<Integer, Void, GetReservationsLocationData> {
        private int reservationId;
        public DriverViewModel driverViewModel;
        public ICurrentDriverPositionResult delegate;

        @Override
        protected void onPreExecute () {
            super.onPreExecute();
            driverViewModel = new DriverViewModel();
        }

        @Override
        protected GetReservationsLocationData doInBackground (Integer...params){
            if (params.length == 0) {
                return null;
            }
            reservationId = params[0];
            List<String> reseravationId = new ArrayList<String>();
            reseravationId.add(params[0]+"");
           *//* List<GetReservationsLocationData> driverData = driverViewModel.GetReservationsLocation(reseravationId).Content;
            if(driverData.size() != 0) {
                return driverData.get(0);
            }*//*

            return null;
        }

        @Override
        protected void onPostExecute (GetReservationsLocationData getDriverLocationData){
            super.onPostExecute(getDriverLocationData);

            if (delegate != null) {
                delegate.resultForDriverPosition(reservationId, getDriverLocationData);
            }
        }
    }*/
}

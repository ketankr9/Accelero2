package com.utsav.root.accelero2;

import android.util.Log;

/**
 * Created by root on 3/22/17.
 */

public class Filters {
    private float sumMe(Float[] arr,int start,int end){
        float ans=0;
        for(int i=start;i<=end;i++)
            ans+=arr[i];
        return ans;
    }
    public Float[] movingAverage(Float[] x,int windowSize){ // assert(windowSize%2==1)
            int n=x.length;
        Float[] y=new Float[n];
        System.arraycopy(x,0,y,0,n);
        //window size must be odd
        int half=windowSize/2;
        for(int i=half;i<n-half;i++){
            y[i]=sumMe(x,i-half,i+half)/(windowSize);
        }
        return y;
    }
    public Float[] lowpass(Float[] x,Float alpha){
        Log.d("ACCELERO","lowpass filter");
        int l=x.length;
        Float[] y=new Float[l];
        y[0]=x[0];
        for(int i=1;i<l;i++)
            y[i]=alpha*x[i]+(1-alpha)*y[i-1];

        return y;
    }
    public void normalize(Float[] x){
        int l=x.length;
        Float u=sumMe(x,0,l-1);
        u=u/l;
//        Float sd=0;
        Float maxx=-9999999f;
        Float minn=9999999f;
        for(int i=0;i<l;i++){
            if(x[i] > maxx)
                maxx=x[i];
            if(x[i] < minn)
                minn=x[i];
        }
        for(int i=0;i<l;i++)
            x[i]=(x[i]-u)/(maxx-minn);
    }
    public void savgol(){
        Log.d("ACCELERO","Savitry Golay Filter");
    }
}

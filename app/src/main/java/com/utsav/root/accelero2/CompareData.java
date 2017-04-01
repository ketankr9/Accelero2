package com.utsav.root.accelero2;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by root on 3/22/17.
 */

public class CompareData {
//    private Float[] XL,YL,ZL,XR,YR,ZR,timeL,timeR;


    private float summ(Float[] arr,int n){
        float ans=0;
        for(int i=0;i<n;i++)
            ans+=arr[i];
        return ans;
    }
    public Integer[] getPeriodPoint(Float[] x,Float k){
        int l=x.length;
        ArrayList<Float> ps=new ArrayList<Float>();
        ArrayList<Integer> TT=new ArrayList<Integer>();

        for(int i=1;i<l-1;i++){
            if(x[i] > x[i-1] && x[i] > x[i+1] ) {
                ps.add(x[i]);
//                Log.d("ACCE LOCAL MAX",String.valueOf(x[i]));
                TT.add(i);
            }
        }
        Log.d("ACCE ps length",String.valueOf(ps.size()));
        float sd=0; float u=0; int lenps=ps.size();

        ArrayList<Integer> retTime=new ArrayList<Integer>();

        for(int i=0;i<ps.size();i++)
            u+=ps.get(i);
        u=u/lenps;

        for(int i=0;i<lenps;i++)
            sd+=(ps.get(i)-u)*(ps.get(i)-u);
        sd=(float)Math.sqrt(sd)/lenps;
        float thres = u+k*sd;

        Log.d("ACCE u sd thres: ",String.valueOf(u)+" "+String.valueOf(sd)+ " "+String.valueOf(thres));
        for(int i=0;i<lenps;i++){
            if(ps.get(i) > thres){
//                Log.d("ACCE psArray > thres",String.valueOf(ps.get(i)));
                retTime.add(TT.get(i));
            }
        }
        Log.d("ACCE maximal points",String.valueOf(retTime.size()));

    return retTime.toArray(new Integer[retTime.size()]);
    }
    private Integer getAGC(Float[] x,Integer[] pts){
        int nPts=pts.length;
        Float[][] arr=new Float[nPts-1][nPts-1];
        Log.d("ACCE","getAGC");
        for(int i=0;i<nPts-1;i++){
//            String st="";
            for(int j=0;j<nPts-1;j++){

                int a=pts[i],b=pts[i+1];
                int c=pts[j],d=pts[j+1];
                arr[i][j]=dwt( Arrays.copyOfRange(x,a,b), Arrays.copyOfRange(x,c,d) );
//                st+=String.valueOf(arr[i][j])+" ";
            }
//            Log.d("ACCE",String.valueOf(i));

        }
        int startPt=-1;
        float minn=999999999;
        for(int i=0;i<nPts-1;i++){
            float sum=0;

            for(int j=0;j<nPts-1;j++){
                sum+=arr[i][j];
            }
            if(sum<minn){
                minn=sum;
                startPt=i;
            }

        }
        return startPt;
    }

    public float dwtXYZ(Float[] ZL,Float[] ZR,Float k){
        Log.d("ACCE dwtXYZ","BEGINNING");

        // GET INDEX OF MAXIMUM POINTS
        Integer[] ptsL=getPeriodPoint(ZL,k);
        Log.d("ACCE", Arrays.toString(ptsL));
        Log.d("ACCE","finding AGC Left");
        Integer startL=getAGC(ZL,ptsL);
        Log.d("ACC","AGC LEFT found from "+String.valueOf(ptsL[startL])+" to "+String.valueOf(ptsL[startL+1]));

        Integer[] ptsR=getPeriodPoint(ZR,k);
        Log.d("ACCE","after get getAGC Right");
        Integer startR=getAGC(ZR,ptsR);
        Log.d("ACC","after AGC RIGHT"+String.valueOf(ptsR[startR])+" to "+String.valueOf(ptsR[startR+1]));

        Log.d("ACC ZL AGC ARRAY",Arrays.toString( Arrays.copyOfRange( ZL , ptsL[startL] , ptsL[startL+1] ) ) );
        Log.d("ACC ZR AGC ARRAY",Arrays.toString( Arrays.copyOfRange( ZR , ptsR[startR] , ptsR[startR+1] ) ) );
        float ff=dwt( Arrays.copyOfRange(ZL, ptsL[startL], ptsL[startL+1]) ,  Arrays.copyOfRange(ZR, ptsR[startR] , ptsR[startR+1]) );

        Log.d("ACC ff",String.valueOf(ff));
        return ff;
    }
    public float dwt(Float[] x,Float[] y){

        int nRows=x.length;
        int nCols=y.length;
        Float[][] cost=new Float[nRows][nCols];
        cost[0][0]=Math.abs(x[0]-y[0]);
        for(int i=1;i<nRows;i++)
            cost[i][0]=cost[i-1][0]+Math.abs(x[i]-y[0]);
        for(int i=1;i<nCols;i++)
            cost[0][i]=cost[0][i-1]+Math.abs(x[0]-y[i]);
        for(int i=1;i<nRows;i++){
            for(int j=1;j<nCols;j++){
                float mm=Math.min(cost[i-1][j],Math.min(cost[i][j-1],cost[i-1][j-1]));
                cost[i][j]=mm+Math.abs(x[i]-y[j]);
            }
        }
        return cost[nRows-1][nCols-1];
    }


}

import numpy as np
import matplotlib.pyplot as plt
from scipy import signal
from math import factorial
from copy import deepcopy
def lowpass(x,alph):
    y=[]
    # print type(x[5])
    y.append(x[0])
    for i in range(1,len(x)):
        y.append(0)
        # print y[i-1],alph,y[i],x[i]
        y[i]=alph*x[i] + (1-alph)*y[i-1]
    return y
def maa(x):
    SMOOTH_FACT=3
    l=len(x)
    iterations=l//SMOOTH_FACT;
    node=0
    y=[]
    for i in range(iterations):
        num=0
        for k in range(node,node+SMOOTH_FACT):
            num+=x[k]
        node+=SMOOTH_FACT
        num/=SMOOTH_FACT
        y.append(num)
    return y
def movingAvg(x,windowSize):
    y=deepcopy(x)
    half=windowSize//2
    for i in range(half,len(x)-half):
        y[i]=sum(y[i-half:i+half+1])/(windowSize)
    return y

def dwt(x,y):
    nRows,nCols=len(x),len(y)
    cost=[[0 for _ in range(nCols)] for _ in range(nRows)]
    cost[0][0]=abs(x[0]-y[0])
    for i in xrange(1,nRows):
        cost[i][0]=cost[i-1][0] + abs(x[i]-y[0])
    for i in xrange(1,nCols):
        # print "DEB: ",cost[0][i-1],x[0],y[i]
        cost[0][i]=cost[0][i-1] + abs(x[0]-y[i])
    for i in xrange(1,nRows):
        for j in xrange(1,nCols):
            mm=min(cost[i-1][j],cost[i][j-1],cost[i-1][j-1])
            cost[i][j]= mm +abs(x[i]-y[j])

    #print cost matrix
    # for i in cost:
    #     print i
    return cost[-1][-1]
def savGol(x,wind,deg):
    return signal.savgol_filter(x,wind,deg,mode='wrap')

def savitzky_golay(y, window_size, order, deriv=0, rate=1):
    try:
        window_size = np.abs(np.int(window_size))
        order = np.abs(np.int(order))
    except ValueError, msg:
        raise ValueError("window_size and order have to be of type int")
    if window_size % 2 != 1 or window_size < 1:
        raise TypeError("window_size size must be a positive odd number")
    if window_size < order + 2:
        raise TypeError("window_size is too small for the polynomials order")
    order_range = range(order+1)
    half_window = (window_size -1) // 2
    # precompute coefficients
    b = np.mat([[k**i for i in order_range] for k in range(-half_window, half_window+1)])
    m = np.linalg.pinv(b).A[deriv] * rate**deriv * factorial(deriv)
    # pad the signal at the extremes with
    # values taken from the signal itself
    firstvals = y[0] - np.abs( y[1:half_window+1][::-1] - y[0] )
    lastvals = y[-1] + np.abs(y[-half_window-1:-1][::-1] - y[-1])
    y = np.concatenate((firstvals, y, lastvals))
    return np.convolve( m[::-1], y, mode='valid')

def getP(x,t,k):
    l=len(x)
    ps=[]
    ts=[]
    ind=[]
    count=0
    for i in range(1,l-1):
        if x[i] > x[i-1] and x[i] > x[i+1]:
            ps.append(x[i])
            ts.append(t[i])
            count=count+1
    lenps=len(ps)
    # plt.scatter(ts,ps)
    u=sum(ps)/lenps
    sd=0
    for i in range(lenps):
        sd+=(ps[i]-u)*(ps[i]-u)
    sd=sd/lenps
    thres=u+k*sd
    print "thres",thres
    dic={}
    print u,sd
    for i in range(lenps):
        if ps[i] > thres:
            dic[ts[i]]=ps[i]
            ind.append(t.index(ts[i]))
    # return dic
    return ind
def normalize(x):
    y=[]
    lenx=len(x)
    u=sum(x)/lenx
    # sd=0
    # for i in range(lenx):
    #     sd+=(x[i]-u)*(x[i]-u)
    # sd=sd/lenx
    maxx,minn=max(x),min(x)
    for i in range(lenx):
        x[i]=(u-x[i])/(minn)
    return x
def getAGC(x,pts):
    agcDwt=[]
    nPts=len(pts)
    for i in range(nPts-1):
        temp=[]
        for j in range(nPts-1):
            temp.append( dwt( x[ pts[i]:pts[i+1] ] , x[ pts[j] : pts[j+1] ] ))
        agcDwt.append(temp)
    minn=(nPts*2)+99999999999999999
    pos=-1
    for i in range(nPts-1):
        tt=sum(agcDwt[i])
        if tt < minn:
            minn=tt
            pos=i
    # plt.plot( T[pts[pos]:pts[pos+1]] , x[pts[pos]:pts[pos+1]] )
    return (pts[pos],pts[pos+1])

# for data1
with open('myleft.txt') as f:
    contents=f.readlines()
data=[]
X,T,Y,Z=[],[],[],[]
for x in range(1,len(contents)):
    temp=map(float,contents[x].strip().split())
    if len(temp)==4:
        data.append(temp)
        X.append(temp[1])
        Y.append(temp[2])
        Z.append(temp[3])
        T.append(temp[0])
plt.figure("left")
X,T,Y,Z=X[1300:1500],T[1300:1500],Y[1300:1500],Z[1300:1500]
plt.plot(T,X)
plt.figure("Y")
plt.plot(T,Y)
plt.figure("Z")
plt.plot(T,Z)
plt.show()
# y2=maa(X)
# y1=savGol(y,51,1)
# y2=savGol(X,51,2)
# y3=savGol(y,101,4)
# y4=savGol(y,51,4)
# y5=savGol(y,51,5)
# plt.figure("Lowpass")
y=lowpass(X,float(0.5))
y=movingAvg(y,5)
y=movingAvg(y,5)
y=movingAvg(y,5)
y=normalize(y)
# plt.plot(T,y,color="red")
# y=movingAvg(y,5)
# y=movingAvg(y,5)
# plt.plot(T,y,color="black")
# plt.show()
print "filter finished left"
h=5//2
# print "DWT: "+str(5)+" "+str(dwt(y6[h:-h],X[h:-h]))
pts=getP(y,T,0)
# plt.scatter([T[i] for i in pts],[y[i] for i in pts])
# plt.plot(T,y)
(startIndex,endIndex)=getAGC(y,pts)
# gcL=[160, 271, 372, 472, 570, 669, 770, 868, 967, 1070, 1170, 1274, 1376, 1480, 1582, 1683, 1789, 1893, 1999, 2107, 2213, 2320, 2427, 2531, 2638, 2744, 2849, 2956, 3062, 3171, 3277, 3387, 3493, 3601, 3708, 3816, 3926, 4033, 4142, 4250, 4358, 4466, 4573, 4681, 4789, 4897, 5009, 5114, 5223, 5333, 5445, 5553, 5661, 5768, 5875, 5981, 6088, 6198, 6308, 6418, 6528, 6640, 6752, 6867, 6981, 7082, 7134, 7157, 7182, 7199, 7220, 7242, 7267, 7285, 7300]

# plt.scatter([T[i] for i in pts],[y[i] for i in pts])
plt.plot(T[startIndex:endIndex],y[startIndex:endIndex])
# plt.show()

#for data2
with open('myright.txt') as f:
    contents=f.readlines()
data=[]
X,T=[],[]
for x in range(1,len(contents)):
    temp=map(float,contents[x].strip().split())
    if len(temp)==4:
        data.append(temp)
        X.append(temp[3])
        T.append(temp[0])

X,T=X[1300:2500],T[1300:2500]
plt.figure("right")
y6_2=lowpass(X,float(0.5))
y6_2=movingAvg(y6_2,5)
y6_2=movingAvg(y6_2,5)
y6_2=movingAvg(y6_2,5)
# y6_2=movingAvg(y6_2,5)
# y6_2=movingAvg(y6_2,5)
y6_2=normalize(y6_2)
h=5//2
# gcR=[300, 410, 512, 614, 715, 817, 919, 1017, 1116, 1216, 1317, 1416, 1516, 1620, 1722, 1825, 1927, 2028, 2132, 2236, 2341, 2447, 2554, 2659, 2765, 2870, 2976, 3082, 3188, 3295, 3402, 3507, 3611, 3718, 3824, 3930, 4038, 4145, 4253, 4362, 4469, 4576, 4686, 4794, 4901, 5010, 5121, 5232, 5341, 5451, 5563, 5672, 5784, 5893, 6004, 6114, 6223, 6332, 6443, 6553, 6662, 6772, 6882, 6995, 7112, 7299, 7316, 7328, 7350, 7374, 7402]
# print "DWT: "+str(5)+" "+str(dwt(y6_2[h:-h],X[h:-h]))
pts=getP(y6_2,T,0)
(startIndex2,endIndex2)=getAGC(y6_2,pts)
# plt.plot(T,y6_2)
# plt.scatter([T[i] for i in pts],[y6_2[i] for i in pts])
plt.plot(T[startIndex2:endIndex2],y6_2[startIndex2:endIndex2])

plt.show()
# print temp
# temp=temp[::-1]
# print temp
print "DWT:",dwt(y6_2[startIndex2:endIndex2],y[startIndex:endIndex])
plt.show()

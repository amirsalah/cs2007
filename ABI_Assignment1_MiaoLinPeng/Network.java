
public class Network {
    int wholenum=0;
    String [][] Wdata=new String[1800][7];  //ugly, store the data which we will operate
    double learnrate=1;
    double ii=0.8;
    double jj=0.2;
    public Network(String[][] w,int i) {
        wholenum=i-1;
        Wdata=w;
    }
     //create the original weight, all memeber of this array are random();
    public double[] Oweight(){
          double[] w=new double[21];
          for(int i=0;i<21;i++){
             w[i]=Math.random();
          }
         return w;
     }
     //Sum(XiWi) or we call it u
     public double Xworu(double[] x, double[] w){
      double sumxw=0;
      for(int i=0;i<20;i++){
        sumxw=sumxw+x[i]*w[i];
      }
      return sumxw;
     }
     //will used in Nweight(),and other;
     public double Su(double u){
       return 1/(1+Math.pow(Math.E,-u));
     }
    //will used in Nweight();
    public double dSu(double u){
    return Su(u)*(1-Su(u));
    }

    //get the new weight;
    public double Nweight(double q, double x, double oweight, double u){
      double nweight,rate;
      rate=learnrate;
      nweight=oweight+1*rate*q*x*dSu(u);
    return nweight;
    }

    ////////////////////////////////////////
    //the main process of neural network
    ///////////////////////////////////////
    public double[] Process(){
    	double[] rresult=new double[1800];
    	double[] result=new double[1800];
    	double[][] weight=new double[20][20];
    	double[] midinput=new double[20];
    	double[] input=new double[20];
    	double[] midweight=new double[20];
    	double[] u2=new double[20];
    	double oy=0;
    	int cycle=0;
    	double oy1,iy,q,u1;
    	
    	for(int i=0;i<20;i++){
    		weight[i]=Oweight();
    	}

    	midweight=Oweight();
    	
    	for(int a=0;a<wholenum-20+1;a++){
    		for(int j=0;j<20;j++){
    			input[j]=Double.parseDouble(Wdata[wholenum-j-a][6])/100000;//the input must between 0--1, so /100000
    		}

    		for(int k=0;k<20;k++){
    			u2[k]=Xworu(input,weight[k]);
    			midinput[k]=Su(u2[k]);
    		}
    		
    ///////////////////////////////////////
    //regression result
    /////////////////////////////////////////
    		Operdata op=new Operdata(input);
    		rresult[wholenum-20-a]=op.Regprice(input);

    ///////////////////////////////////////////////
    		u1=Xworu(midinput,midweight);
    		oy=Su(u1);
    		result[wholenum-20-a]=oy*100000*ii+jj*rresult[wholenum-20-a];//predict price, the very first value will very near 1*100000.
//    		result[wholenum-20-a]=oy*100000;
    		/////////////////////////////////////////
    		iy=Double.parseDouble(Wdata[wholenum-20-a][6])/100000;//now we know the true value;because we must compare true value and predict value, so true value/100000
    		q=iy-oy;                                               //error between true value and predict value
    		cycle=0;
    		////////////////////////////////////////////
    		// change weight until it fit the requirement
    		///////////////////////////////////////////
    		while(cycle==0){

    			if(q<0.0001&&q>-0.0001){    //ugly, but it's the way to control the speed of conv
                                //and decide the time to finish the process of changing weight
    				cycle=1;
    			}else{
    				for(int b=0;b<20;b++){
    					midweight[b]=Nweight(q,midinput[b],midweight[b],u1); // change the weight of layout
    				}

    				for(int c=0;c<20;c++){
    					for(int d=0;d<21;d++){
    						weight[c][d]=Nweight(q*midweight[c],input[c],weight[c][d],u2[c]); //change the weight of input
    					}
    				}
    				for(int f=0;f<20;f++){
    					u2[f]=Xworu(input,weight[f]);
    					midinput[f]=Su(u2[f]);
    				}
    				u1=Xworu(midinput,midweight);
    				oy1=Su(u1);
    				q=iy-oy1;

    			}
    		}
    	}
    	//result[0]=oy*100000;
    	return result;
}














}

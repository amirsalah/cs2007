
public class Mnet {
   String [][] Wdata=new String[1800][7];
   double[] fresult= new double[1800];
   double learnrate=1;
   double crate=0.5;
   double srate=0.1;
   double esrate=0.02;
   //int instant=1758;
   int instant=0;
   public Mnet(String[][] w, int i) {
        Wdata=w;
        instant=i-1;
        //super();
    }

    public double[] Oweight( int n){
       double[] w=new double[n];
       for(int i=0;i<n;i++){
          w[i]=Math.random();
       }
      return w;
    }
    public double[] Correctweight(double[] weight, int n){
       double result1;
       double[] oweight=new double[n];
       double[] nweight=new double[n];
       double[] rweight=new double[n];
       double rate=learnrate;
       oweight=weight;
       int cycle=0;
  double kresult=Double.parseDouble(Wdata[instant-n][6]);
  //////////////////////////////////////////////////////////////////////////
  //System.out.println("Wdata["+instant+"-"+n+"][6]="+Wdata[instant-n][6]);
  ////////////////////////////////////////////////////////////////////////
      while(cycle==0){
      result1=Mprice(oweight,n);
     if(Double.parseDouble(Wdata[instant-n][6])-result1>=0){
     rate=learnrate;
     }else{
     rate=-learnrate;
     }

      if(result1>=Double.parseDouble(Wdata[instant-n][6])*0.995&&result1<=Double.parseDouble(Wdata[instant-n][6])*1.005){
            rweight=oweight;
            cycle=1;
       }else{
           // System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
           for(int i=0;i<n;i++){
               nweight[i]=oweight[i]+rate*Double.parseDouble(Wdata[instant-i][6])*Math.abs(kresult-result1)/100000000;
           }
          oweight=nweight;
          cycle=0;

       ///////////////////////////

       }
  //System.out.println("rate="+rate);

   }

      return rweight;
    }


    //n= number of weight
   public double Mprice(double[] weight, int n){
      //double result=0;
      double sumresult=0;
       for(int i=0;i<n;i++){
      sumresult=sumresult+weight[i]*Double.parseDouble(Wdata[instant-i][6])/n;
      }
      //result=sumresult+Double.parseDouble(Wdata[instant-n][6])*(0.5+Math.random()/2)/n;
      ////////////////////////////////////////////
// System.out.println("here here instant="+result);
 //System.out.println("n="+n);
// System.out.print("instant-n="+(instant-n));
 ////////////////////////////////////////////

     return sumresult;
   }
   public double Preprice(double[] weight, int n){
   // double result=0;
    double sumresult=0;
     for(int i=0;i<n;i++){
         sumresult=sumresult+weight[i]*Double.parseDouble(Wdata[instant-i-1][6])/n;//some weight but different data,data in Mprice is Wdata[i]--Wdata[i+n]
    }                                                                                // here is Wdata[i+1][i+1+n];
    //////////////////////////////////////////////////////////////////////////
  //System.out.println("preprice Wdata["+instant+"-"+n+"][6]="+Wdata[instant-n][6]);
  ////////////////////////////////////////////////////////////////////////

     //result=sumresult+Double.parseDouble(Wdata[instant-n][6])*(0.5+Math.random()/2)/n;
    ////////////////////////////////////////////
// System.out.println("here here instant="+result);
//System.out.println("n="+n);
// System.out.print("instant-n="+(instant-n));
////////////////////////////////////////////

   return sumresult;
 }


   //n= i in main class
   public double[] Trueop(int n){
       int knum=19;
      // double[] weight=new double[knum];
       //System.out.println("n="+n);
       //double[] result=new double[1800];
       for(int i=0;i<n-19;i++){
       double[] weight=new double[knum];
       weight=Oweight(knum);
       weight=Correctweight(weight,knum);
       fresult[n-20-i]=Preprice(weight,knum);
       ////////////////////////////////////////////
//System.out.println("!!!finresult["+n+"-20-"+i+"]="+fresult[n-20-i]);
////////////////////////////////////////////

       knum=knum+1;
       }
       //double jan26=Preprice(weight,0);
      // System.out.println(jan26);
      // result=fresult;
   return  fresult;
   }










}


public class Operdata {
   double[]Wdata=new double[20];
    public Operdata(double[] w) {
        Wdata=w;
    }
    //get the regression value of predict price
    public double Regprice(double[] x){
        double a,b;
        double averagex,averagey;
        double sumx=0;
        double lxy=0;
        double lxx=0;
        for(int i=0;i<20;i++){
           sumx=sumx+i;
        }
        averagex=sumx/20;
        averagey=Averagey(x);
        for(int j=0;j<20;j++){
            lxy=lxy+(j-averagex)*(x[j]-averagey);
            lxx=lxx+(j-averagex)*(j-averagex);
        }
        b=lxy/lxx;
        a=averagey-b*averagex;
        return (a+20*b)*100000;
    }
    //get the average numbe sum(xn)/n
    public double Averagey(double[] x){
        double sumx=0;
        for(int i=0;i<20;i++){
         sumx=sumx+x[i];
        }
        return sumx/20;

   }


}

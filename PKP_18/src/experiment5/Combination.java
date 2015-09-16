package experiment5;

//----------------------------------
//     組み合わせの生成
//       ( Combination.java )
//----------------------------------
/* 異なる n 個の数字（1,2,3,4,…,n）から
異なる r 個の数字の全ての組み合わせを作る。
 n と r を入力する。
*/
 import java.io.*;
 import java.util.*; 

 public class Combination {
   int[] c;
   int n, r, count;
   public ArrayList<ArrayList<Integer>> all_combinations = new ArrayList<ArrayList<Integer>>();

   public Combination(int n, int r){
     this.n = n;
     this.r = r;
     c = new int[20];
     count = 0;
     for ( int i=0; i<20; i++ ) 
       c[i] = 0;
   }
   public void combine( int m ) {
       if ( m <= r ) {
         for ( int i=c[m-1]+1; i<=n-r+m; i++ ){
            c[m] = i;
            combine(m+1);
         }
       }
       else {
         count = count + 1 ;
         ArrayList<Integer> list = new ArrayList<Integer>();
         for ( int i=1; i<=r; i++ ){ 
//            System.out.print(" " + c[i]);
            list.add(c[i]);
         }
         all_combinations.add(list);
//         System.out.println();
       }
   }

   public static void main(String[] args) {
    int n;
    int r;
       String str = "";
    while (true) {
       System.out.println();
       System.out.println("整数 n と r を（ スペースで区切って ）                                         入力して下さい　");
       System.out.println("( 終了するときは 0 0 を )");
      try {
       // キーボードからの入力は InputStreamReaderクラスを使う。　
       InputStreamReader isr=  new InputStreamReader(System.in);
       // BufferedReaderクラスには、1行ごとにデータを
       // 読み込むメソッドreadLine()が用意されている。
       BufferedReader br=new BufferedReader(isr);
       str=br.readLine();
      } catch(IOException e){}  
               
       // スペースで区切られた文字列を個々のTokenに分け 整数化
       StringTokenizer  st = new StringTokenizer(str , " ");
       n = Integer.parseInt( st.nextToken());
       r = Integer.parseInt( st.nextToken());
     if ( n == 0 ) break;
     else {
        Combination  cb = new Combination(n,r);
        System.out.println(" n =  " + n + ",  r =  " + r + " のとき");
        cb.combine(1);
        System.out.println("( 全部で " + cb.count + " 通り )");
        
        for(ArrayList<Integer> list: cb.all_combinations){
        	for(Integer i: list){
        		System.out.print(" " + i);
        	}
        	System.out.println();
        }
     }
   }
  }
}
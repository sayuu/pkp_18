package experiment5;

//----------------------------------
//     �g�ݍ��킹�̐���
//       ( Combination.java )
//----------------------------------
/* �قȂ� n �̐����i1,2,3,4,�c,n�j����
�قȂ� r �̐����̑S�Ă̑g�ݍ��킹�����B
 n �� r ����͂���B
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
       System.out.println("���� n �� r ���i �X�y�[�X�ŋ�؂��� �j                                         ���͂��ĉ������@");
       System.out.println("( �I������Ƃ��� 0 0 �� )");
      try {
       // �L�[�{�[�h����̓��͂� InputStreamReader�N���X���g���B�@
       InputStreamReader isr=  new InputStreamReader(System.in);
       // BufferedReader�N���X�ɂ́A1�s���ƂɃf�[�^��
       // �ǂݍ��ރ��\�b�hreadLine()���p�ӂ���Ă���B
       BufferedReader br=new BufferedReader(isr);
       str=br.readLine();
      } catch(IOException e){}  
               
       // �X�y�[�X�ŋ�؂�ꂽ��������X��Token�ɕ��� ������
       StringTokenizer  st = new StringTokenizer(str , " ");
       n = Integer.parseInt( st.nextToken());
       r = Integer.parseInt( st.nextToken());
     if ( n == 0 ) break;
     else {
        Combination  cb = new Combination(n,r);
        System.out.println(" n =  " + n + ",  r =  " + r + " �̂Ƃ�");
        cb.combine(1);
        System.out.println("( �S���� " + cb.count + " �ʂ� )");
        
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
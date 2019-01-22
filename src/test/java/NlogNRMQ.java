/**
 * Simple implementation of n*logn processing algorithm - only for testing purposes
 */
public class NlogNRMQ {
    private  char[] A;
    private  int[][] sparseTable;
    public  void preprocessInput(String input) {

        A = input.toCharArray();
        buildSparseTables();
    }
    private  void buildSparseTables(){
        sparseTable = new int[(int)Math.ceil((Math.log(A.length)/Math.log(2.0)))][];
        sparseTable[0]=new int[A.length];
        for (int i=0;i<sparseTable[0].length;i++){
            sparseTable[0][i]=i;
        }

        for (int i=1; i<sparseTable.length;i++){
            sparseTable[i]=new int[A.length-(int)Math.pow(2,i)+1];
            for (int j=0;j<sparseTable[i].length;j++){
                sparseTable[i][j]=
                        A[sparseTable[i-1][j]]<=A[sparseTable[i-1][j+(int)Math.pow(2,i-1)]]?
                                sparseTable[i-1][j]:sparseTable[i-1][j+(int)Math.pow(2,i-1)];
            }
        }
    }
}

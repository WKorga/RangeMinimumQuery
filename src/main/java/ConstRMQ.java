import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

public class ConstRMQ {
    private  Node head;
    private  Node rightMost;
    private  char[] A;
    private  BitSet B;
    private  int[] T;
    private  int[] L;
    private int tSize=0;
    private  int[] R;
    private  int[] superBlocksMinima;
    private  int[][] blocksMinima;
    private  int[][] superBlockSparseTable;
    private  int[][][] blockSparseTable;
    private  int[][][] answers;
    private  int blockSize;
    private  int superblockSize;
    public  void preprocessInput(String input) {
        /**
         * The algorithm preprocesses in linear time and space a given array A
         * Resulting structure is able to answer every RMQ query:
         *  "What is an index of the smallest element of A from index i to j"
         * in constant time
         */
        A = input.toCharArray();
        buildTree();
        buildBTRLArrays();
        buildMinimaTables();
        buildSparseTables();
        computeAllAnswers();
        //System.out.println(answerQuery(0,2));
    }
    private  void buildTree(){
        /**
         * The RMQ problem can be reduced to LCA and then to so-called ±1RMQ problem
         * where the table A contains elements such that |A[i+1]-A[i]|=1
         * In order to do this, first we have to build a cartesian tree of the base array
         */
        head = new Node(0,A[0]);
        rightMost = head;
        for (int i=1;i<A.length;i++){
            Node current = rightMost;
            Node newNode = new Node(i,A[i]);
            while (current.getValue()>A[i]){
                current=current.getParent();
                if (current==null){
                    newNode.setLeft(head);
                    head.setParent(newNode);
                    head = newNode;
                    break;
                }
            }
            if (current!=null){
                newNode.setLeft(current.getRight());
                current.setRight(newNode);
                newNode.setParent(current);
            }
            rightMost=newNode;
        }
    }
    private  void buildBTRLArrays(){
        /**
         * Next, we search the tree in DFS order (recursively using method searchTree),
         * and every time we visit a node, we put its depth in an array
         * The resulting array of depths L is a base array for ±1RMQ problem
         * We also create array T storing indices corresponding to every element of L
         * and array R which contains an index (in L) of the first occurrence of every index from original array A
         * As neighboring values in L differ only by 1, we can also create a binary vector B, where
         *     B[0] = 1
         *     B[i] = 1 iff L[i-1]<L[i]
         */
        B = new BitSet();
        T = new int[2*A.length];
        R = new int[A.length];
        L = new int[T.length];
        searchTree(head,1);

    }
    private  void searchTree(Node node, int depth){
        T[tSize]=node.getIndex();
        L[tSize]=depth;
        tSize++;
        B.set(tSize-1);
        R[node.getIndex()]=tSize-1;
        if (node.getLeft()!=null){
            searchTree(node.getLeft(), depth+1);
            T[tSize]=node.getIndex();
            L[tSize]=depth;
            tSize++;
        }
        if (node.getRight()!=null){
            searchTree(node.getRight(), depth+1);
            T[tSize]=node.getIndex();
            L[tSize]=depth;
            tSize++;
        }
    }
    private  void buildMinimaTables(){
        /**
         * Storing a minimum of every possible query will take too much space, so we (logically) divide
         * the array into superblocks of length log^3 n and blocks of length 0.5*log n
         *
         * For every superblock and superblock's inner blocks, method computes a minimal element and saves it
         * in superBlocksMinima and blocksMinima tables
         * superBlockMinima[i] stores a minimal element of i-th superblock
         * blocksMinima[i][j] stores a minimal element of j-th block in i-th superblock
         * Operation is performed with a single for loop so the time complexity is linear
         * Tables occupy O( n/log^3 n + n/log n) = o(n) space
         */
        blockSize = Math.max((int)(0.5*Math.log(tSize)/Math.log(2.0)),1);
        superblockSize = (int)Math.pow((blockSize*2),3);
        superBlocksMinima = new int[(int) Math.ceil(tSize/(double)superblockSize)];
        blocksMinima = new int[superBlocksMinima.length][superblockSize/blockSize];
        blocksMinima[superBlocksMinima.length-1] =
                new int[tSize%superblockSize==0?superblockSize/blockSize:
                        (int)(Math.ceil(tSize%superblockSize/(double)blockSize))];
        int superBlockMinimum=0;
        int blockMinimum=0;
        for (int i=0;i<tSize;i++){
            if (B.get(i)){
                blockMinimum++;
                superBlockMinimum++;
            }else {
                blockMinimum--;
                superBlockMinimum--;
                if (blockMinimum<0){
                    blocksMinima[i/superblockSize][(i/blockSize)%(superblockSize/blockSize)]=i;
                    blockMinimum=0;
                }
                if (superBlockMinimum<0){
                    superBlocksMinima[i/superblockSize]=i;
                    superBlockMinimum=0;
                }
            }
            if (i%blockSize==0){
                blocksMinima[i/superblockSize][(i/blockSize)%(superblockSize/blockSize)]=i;
                blockMinimum=0;
                if (i%superblockSize==0){
                    superBlocksMinima[i/superblockSize]=i;
                    superBlockMinimum=0;
                }
            }
        }

    }
    private  void buildSparseTables(){
        /**
         * Method takes previously built minima tables for superblocks and blocks and builds sparse tables
         *
         * What is a sparse table?
         * For an array A of n comparable elements, a sparse table is a two dimensional array B[][]
         * where B[k][i] (k=0...log n) = min(A[i],A[i+1],...,A[i + 2^k -1 ])
         * which takes O(n*log n) space and time to build
         *
         * From definition of the table, we can obtain a simple recursive formula for B[k]:
         *     B[0][i] = A[i]
         *     B[k][i] = min( B[k-1][i], B[k-1][i + 2^(k-1)])
         *
         * Which the method uses for both minima tables creating two sparse tables: superBlockSparseTable and blockSparseTable
         * that will be later used for answering RMQ queries
         */
        superBlockSparseTable = new int[(int)Math.ceil((Math.log(superBlocksMinima.length)/Math.log(2.0)))][];
        for (int i=0; i<superBlockSparseTable.length;i++){
            if (i==0){
                superBlockSparseTable[0]=superBlocksMinima;
                continue;
            }
            superBlockSparseTable[i]=new int[superBlocksMinima.length-(int)Math.pow(2,i)+1];
            for (int j=0;j<superBlockSparseTable[i].length;j++){
                superBlockSparseTable[i][j]=
                        L[superBlockSparseTable[i-1][j]]<=L[superBlockSparseTable[i-1][j+(int)Math.pow(2,i-1)]]?
                                superBlockSparseTable[i-1][j]:superBlockSparseTable[i-1][j+(int)Math.pow(2,i-1)];
            }
        }
        blockSparseTable = new int[superBlocksMinima.length][][];
        for (int i=0; i<blockSparseTable.length;i++){
            blockSparseTable[i]=new int[(int)(Math.log(blocksMinima[i].length)/Math.log(2.0))+1][];
            blockSparseTable[i][0]=blocksMinima[i];
            for (int j=1;j<blockSparseTable[i].length;j++){
                blockSparseTable[i][j]=new int[blocksMinima[i].length-(int)Math.pow(2,j)+1];
                for (int k=0;k<blockSparseTable[i][j].length;k++){
                    blockSparseTable[i][j][k]=
                            L[blockSparseTable[i][j-1][k]]<=L[blockSparseTable[i][j-1][k+(int)Math.pow(2,j-1)]]?
                                    blockSparseTable[i][j-1][k]:blockSparseTable[i][j-1][k+(int)Math.pow(2,j-1)];
                }
            }
        }
    }
    private  void computeAllAnswers(){
        /**
         * Using sparse tables we can answer all block-aligned queries in costant time
         * But some queries have to be divided to 2 or 3 parts:
         *  - a middle block-aligned fragment
         *  - one or two short fragments that begin or end in the middle of a block
         * There's also a possibility that the whole query will fit inside a single block
         * That's why, for every block i, we have to compute all possible queries (j,k),
         * which is possible in o(n) time and space, as all blocks are binary vectors that are also
         * a binary representation of a base 10 integer.
         * So we have to store answers for at most 2^blockSize different types of blocks.
         *
         * As to save space, we store answers only to queries (j,k) where j<=k,
         * so the answer to such query will be obtained with answers[i][j][k-j] call
         *
         */
        answers = new int[(int)Math.pow(2,blockSize)][][];
        for (int i=0;i<=tSize/blockSize;i++){
            int type = getBlockType(i);
            if (answers[type]!=null)
                continue;
            answers[type]=new int[blockSize][];
            for (int j=0;j<blockSize;j++){
                answers[type][j]=new int[blockSize-j];
                answers[type][j][0]=j;
                int minimum=0;
                for (int k=j+1;k<blockSize;k++){
                    if (B.get(i*blockSize+k)){
                        minimum++;
                        answers[type][j][k-j]=answers[type][j][k-j-1];
                    }else {
                        minimum--;
                        if (minimum<0){
                            minimum=0;
                            answers[type][j][k-j]=k;
                        }else {
                            answers[type][j][k-j]=answers[type][j][k-j-1];
                        }
                    }
                }
            }
        }
        return;
    }
    public  int answerQuery(int i, int j){
        /**
         * As we reduced the RMQ problem to ±1RMQ, we have to change i and j to appropriate
         * indices of B table using R array.
         */
        i = R[i];
        j = R[j];
        if (i>j){
            int temp=i;
            i=j;
            j=temp;
        }
        int answer = answerSuperblockQuery(i,j);
        return T[answer];
    }
    private  int answerSuperblockQuery(int i, int j){
        /**
         * Method takes two indices - i and j, and returns an index of the smallest element between them (inclusive)
         * The way in which method will answer the query depends on i and j:
         * If i and j are superblock-aligned, we can use sparse table
         * (as to keep code clarity this operation is performed in getSuperBlockMinimum method),
         * but otherwise our query will have to be divided into smaller sub-queries,
         * which will be answered by answerBlockQuery method
         */
        int leftmostSuperBlockIndex = i/superblockSize;
        int rightmostSuperBlockIndex = j/superblockSize;

        //if i is superblock-aligned
        if (i==leftmostSuperBlockIndex*superblockSize){
            //if j is aligned too
            if (j==(rightmostSuperBlockIndex+1)*superblockSize-1){
                return getSuperBlockMinimum(leftmostSuperBlockIndex,rightmostSuperBlockIndex);
            }
            //if i and j are in different superblocks
            if (rightmostSuperBlockIndex-leftmostSuperBlockIndex>0){
                return getMinimum(
                        getSuperBlockMinimum(leftmostSuperBlockIndex,rightmostSuperBlockIndex-1),
                        answerBlockQuery(rightmostSuperBlockIndex*superblockSize,j)
                );
            }
            //otherwise
            return answerBlockQuery(i,j);
        }
        //if j is superblock-aligned (and i is not)
        if (j==(rightmostSuperBlockIndex+1)*superblockSize-1){
            //if i and j are in different superblocks
            if (rightmostSuperBlockIndex-leftmostSuperBlockIndex>0){
                return getMinimum(
                        answerBlockQuery(i,(leftmostSuperBlockIndex+1)*superblockSize-1),
                        getSuperBlockMinimum(leftmostSuperBlockIndex+1,rightmostSuperBlockIndex)
                );
            }
            //otherwise
            return answerBlockQuery(i,j);
        }
        //if i and j are in different superblocks (and none of them is superblock-aligned)
        if (rightmostSuperBlockIndex-leftmostSuperBlockIndex>0){
            //if i and j are in neighboring superblocks
            if (rightmostSuperBlockIndex-leftmostSuperBlockIndex==1){
                return getMinimum(
                        answerBlockQuery(i,(leftmostSuperBlockIndex+1)*superblockSize-1),
                        answerBlockQuery(rightmostSuperBlockIndex*superblockSize,j)
                );
            }
            //if there is at least one superblock between i and j
            return getMinimum(
                    answerBlockQuery(i,(leftmostSuperBlockIndex+1)*superblockSize-1),
                    getMinimum(
                            getSuperBlockMinimum(leftmostSuperBlockIndex+1,rightmostSuperBlockIndex-1),
                            answerBlockQuery(rightmostSuperBlockIndex*superblockSize,j)
                    )
            );
        }
        //if i and j are in the same superblock and are not aligned
        return answerBlockQuery(i,j);
    }
    private  int getSuperBlockMinimum(int left, int right){
        /**
         * Get minimum for superblocks from left to right
         * If left and right are not the same, method uses sparse table
         */
        if (left==right){
            return superBlocksMinima[left];
        }else {
            int k = (int)(Math.log(right-left+1)/Math.log(2.0));
            try{
                return getMinimum(
                        superBlockSparseTable[k][left],
                        superBlockSparseTable[k][right-(int)Math.pow(2,k)+1]
                );
            }catch (Exception e){
                return 0;
            }
        }
    }
    private  int answerBlockQuery(int i, int j){
        /**
         * Method takes two indices - i and j, both in the same superblock and returns an index of the smallest element
         * between them (inclusive)
         * Just as in answerSuperBlockQuery method we have to check all possible cases
         */
        int length = j-i+1;
        int leftmostBlockIndex = i/blockSize;
        int rightmostBlockIndex = j/blockSize;

        //if i is block-aligned
        if (i==leftmostBlockIndex*blockSize){
            //if j is aligned too
            if (j==(rightmostBlockIndex+1)*blockSize-1){
                return getBlockMinimum(leftmostBlockIndex,rightmostBlockIndex);
            }
            //if i and j are in different blocks
            if (rightmostBlockIndex-leftmostBlockIndex>0){
                return getMinimum(
                        getBlockMinimum(leftmostBlockIndex,rightmostBlockIndex-1),
                        //as we store only "relative" indices in answers table, we have to do some math
                        rightmostBlockIndex*blockSize+
                                answers[getBlockType(rightmostBlockIndex)][0][j-rightmostBlockIndex*blockSize]
                );
            }
            //otherwise
            return leftmostBlockIndex*blockSize+
                    answers[getBlockType(leftmostBlockIndex)][i-leftmostBlockIndex*blockSize][j-i];
        }
        //if j is block-aligned (and i is not)
        if (j==(rightmostBlockIndex+1)*blockSize-1){
            //if i and j are in different blocks
            if (rightmostBlockIndex-leftmostBlockIndex>0){
                return getMinimum(
                        leftmostBlockIndex*blockSize+
                                answers [getBlockType(leftmostBlockIndex)]
                                        [i-leftmostBlockIndex*blockSize]
                                        [(leftmostBlockIndex+1)*blockSize-1-i],
                        getBlockMinimum(leftmostBlockIndex+1,rightmostBlockIndex)
                );
            }
            //otherwise
            return leftmostBlockIndex*blockSize+
                    answers[getBlockType(leftmostBlockIndex)][i-leftmostBlockIndex*blockSize][j-i];
        }
        //if i and j are in different blocks (and none of them is block-aligned)
        if (rightmostBlockIndex-leftmostBlockIndex>0){
            //if i and j are in neighboring blocks
            if (rightmostBlockIndex-leftmostBlockIndex==1){
                return getMinimum(
                        leftmostBlockIndex*blockSize+
                                answers [getBlockType(leftmostBlockIndex)]
                                        [i-leftmostBlockIndex*blockSize]
                                        [(leftmostBlockIndex+1)*blockSize-1-i],
                        rightmostBlockIndex*blockSize+
                                answers[getBlockType(rightmostBlockIndex)][0][j-rightmostBlockIndex*blockSize]
                );
            }
            //if there is at least one superblock between i and j
            return getMinimum(
                    leftmostBlockIndex*blockSize+
                            answers [getBlockType(leftmostBlockIndex)]
                                    [i-leftmostBlockIndex*blockSize]
                                    [(leftmostBlockIndex+1)*blockSize-1-i],
                    getMinimum(
                            getBlockMinimum(leftmostBlockIndex+1,rightmostBlockIndex-1),
                            rightmostBlockIndex*blockSize+
                                    answers[getBlockType(rightmostBlockIndex)][0][j-rightmostBlockIndex*blockSize]
                    )
            );
        }
        //if i and j are in the same superblock and are not aligned
        return leftmostBlockIndex*blockSize+
                answers [getBlockType(leftmostBlockIndex)]
                        [i-leftmostBlockIndex*blockSize]
                        [j-i];
    }
    private  int getBlockMinimum(int left, int right){
        /**
         * Gets minimum for blocks from left to right (both in the same superblock)
         * Since we store 'relative' block indices in minima table we have to compute
         * left and right position in their superblock
         * If left and right are not the same, method uses sparse table
         */
        int blocksInSuperBlockCount = blocksMinima[0].length;
        int superBlockIndex = left/blocksInSuperBlockCount;
        left=left-superBlockIndex*blocksInSuperBlockCount;
        right=right-superBlockIndex*blocksInSuperBlockCount;
        if (left==right){
            return blocksMinima[superBlockIndex][left];
        }else {
            int k = (int)(Math.log(right-left+1)/Math.log(2.0));
            return getMinimum(
                    blockSparseTable[superBlockIndex][k][left],
                    blockSparseTable[superBlockIndex][k][right-(int)Math.pow(2,k)+1]
            );
        }
    }
    private  int getBlockType(int blockIndex){
        /**
         * Method computes block's type from bits in B array
         */
        BitSet bits = B.get(blockIndex*blockSize,(blockIndex+1)*blockSize);
        return bits.isEmpty()?0:(int)bits.toLongArray()[0];
    }
    private  int getMinimum(int index1, int index2){
        /**
         * Since all minima tables store indices, when we want to compare query results,
         * we have to compare values in L array and return an index storing a smaller one
         */
        return L[index1]<=L[index2]?index1:index2;
    }
}


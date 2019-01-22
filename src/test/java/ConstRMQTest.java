import junit.framework.TestCase;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Random;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

public class ConstRMQTest {
    @Test
    public void shouldFindProperMinimaForOneBlock(){
        String sequence ="";
        for (int i=0;i<1;i++){
            sequence+=new Random().nextInt(10);
        }
        ConstRMQ constRMQ = new ConstRMQ();
        constRMQ.preprocessInput(sequence);
        /**
         * Check all possible queries
         */
        for (int i=0;i<sequence.length();i++){
            for (int j=i;j<sequence.length();j++){
                int minimum=i;
                for (int k=i;k<=j;k++){
                    if (sequence.charAt(k)<sequence.charAt(minimum))
                        minimum=k;
                }
                assert minimum==constRMQ.answerQuery(i,j);
            }
        }
    }
    @Test
    public void shouldFindProperMinimaForOneSuperblock(){
        String sequence ="";
        for (int i=0;i<527;i++){
            sequence+=new Random().nextInt(10);
        }
        ConstRMQ constRMQ = new ConstRMQ();
        constRMQ.preprocessInput(sequence);
        /**
         * Check all possible queries
         */
        for (int i=0;i<sequence.length();i++){
            for (int j=i;j<sequence.length();j++){
                int minimum=i;
                for (int k=i;k<=j;k++){
                    if (sequence.charAt(k)<sequence.charAt(minimum))
                        minimum=k;
                }
                assert minimum==constRMQ.answerQuery(i,j);
            }
        }
    }
   @Test
    public void shouldFindProperMinimaForManySuperblocks(){
        String sequence ="";
        for (int i=0;i<2367;i++){
            sequence+=new Random().nextInt(10);
        }
        ConstRMQ constRMQ = new ConstRMQ();
        constRMQ.preprocessInput(sequence);
        /**
         * Check all possible queries
         */
        for (int i=0;i<sequence.length();i++){
            for (int j=i;j<sequence.length();j++){
                int minimum=i;
                for (int k=i;k<=j;k++){
                    if (sequence.charAt(k)<sequence.charAt(minimum))
                        minimum=k;
                }
                assert minimum==constRMQ.answerQuery(i,j);
            }
        }
    }
    @Test
    public void shouldBeFasterThanNlogNForBigInput() throws FileNotFoundException {
        //1 million characters
        String sequence = new Scanner(
                new File(this.getClass().getClassLoader().getResource("bigInput.txt").getFile())).nextLine();

        long start = System.currentTimeMillis();
        ConstRMQ constRMQ = new ConstRMQ();
        constRMQ.preprocessInput(sequence);
        long constTime = System.currentTimeMillis()-start;

        start = System.currentTimeMillis();
        NlogNRMQ nlogNRMQ = new NlogNRMQ();
        nlogNRMQ.preprocessInput(sequence);
        long logTime = System.currentTimeMillis()-start;

        assert constTime<logTime;
        System.out.println("Constant RMQ processing time: "+constTime);
        System.out.println("nlogn RMQ processing time: "+logTime);
    }
}
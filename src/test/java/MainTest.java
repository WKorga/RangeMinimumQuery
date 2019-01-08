import junit.framework.TestCase;
import org.junit.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class MainTest{
    @Test
    public void shouldFindProperMinimaForOneBlock(){
        String sequence ="";
        for (int i=0;i<1;i++){
            sequence+=new Random().nextInt(10);
        }
        Main.main(new String[]{sequence});
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
                assert minimum==Main.answerQuery(i,j);
            }
        }
    }
    @Test
    public void shouldFindProperMinimaForOneSuperblock(){
        String sequence ="";
        for (int i=0;i<527;i++){
            sequence+=new Random().nextInt(10);
        }
        Main.main(new String[]{sequence});
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
                assert minimum==Main.answerQuery(i,j);
            }
        }
    }
   @Test
    public void shouldFindProperMinimaForManySuperblocks(){
        String sequence ="";
        for (int i=0;i<2367;i++){
            sequence+=new Random().nextInt(10);
        }
        Main.main(new String[]{sequence});
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
                assert minimum==Main.answerQuery(i,j);
            }
        }
    }
}
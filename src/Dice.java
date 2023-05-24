// imports
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

public class Dice {
    // fields
    private int sides; // signifies dice type, will be d6 here
    private Random rand;

    // constructor
    public Dice(int sides) {
        this.sides = sides;
        this.rand = new Random();
    }


    // rollDie: signifies one dice roll
    public int rollDie() {
        return rand.nextInt(sides)+1;
    }

    // wrapRoll: loop to get all dice rolls for scene wrap
    public List<Integer> wrapRoll(int budget){
        List<Integer> result = new ArrayList<Integer>();
        for(int i = 0; i < budget; i++){
            result.add(rollDie());
        }
        result.sort(Collections.reverseOrder()); // sort in descending order
        return result;
    }

}

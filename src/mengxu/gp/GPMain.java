package mengxu.gp;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by dyska on 21/05/17.
 */
public class GPMain {

    public static void main(String[] args) {
        List<String> gpRunArgs = new ArrayList<>();
        boolean isTest = true;
        int maxTests = 1;
        boolean isDynamic = true;

        //include path to params file
        gpRunArgs.add("-file");

        double utilLevel = 0.85;
        String objective0 = "mean-flowtime";

        gpRunArgs.add("D:/xumeng/ZheJiangLab/SECscheduling/src/mengxu/algorithm/multipletreegp/multipletreegp-dynamicBaseline.params");

        gpRunArgs.add("-p");
        gpRunArgs.add("eval.problem.eval-model.sim-models.0.util-level="+utilLevel);
        gpRunArgs.add("-p");
        gpRunArgs.add("eval.problem.eval-model.objectives.0="+objective0);
        gpRunArgs.add("-p");
        for (int i = 1; i <= 30 && i <= maxTests; ++i) {
            gpRunArgs.add("seed.0="+String.valueOf(i));
            gpRunArgs.add("-p");
            gpRunArgs.add("stat.file="+"job."+String.valueOf(i)+".out.stat");
            //convert list to array
            GPRun.main(gpRunArgs.toArray(new String[0]));
            //now remove the seed, we will add new value in next loop
            gpRunArgs.remove(gpRunArgs.size()-3);
        }
    }
}

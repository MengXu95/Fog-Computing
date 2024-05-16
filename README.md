# README #

This README would normally document whatever steps are necessary to get your application up and running.

### What is this repository for? ###

* This is a package for algorithms for automatic rule design in Workflow Scheduling (WS) using Genetic Programming (GP). Written by Meng Xu, Yi Mei, and Fangfang Zhang.
* The package is based on the Java ECJ package, which is available from https://cs.gmu.edu/~eclab/projects/ecj/.
* Version 1.0.0

### How do I get set up? ###

1. Download the source files in the `src/` folder and the dependencies in the `libraries/` folder.
2. Create a Java Project using `src/` and `libraries/` (the repository is a IDEA IntelliJ project by default).
3. The ECJ functions are located at `src/ec/`, and the functions for WS and GP are in `src/mengxu/`. Now you are ready to run a number of different algorithms.
4. Before starting, it is highly recommended to get yourself familiar with the ECJ package, especially the GP part. You can start from the four tutorials located at `src/ec/app/tutorialx` (x = 1, ..., 4). Turorial 4 is about GP for symbolic regression, which is very useful for understanding this project. A more thorough manual can be found in https://cs.gmu.edu/~eclab/projects/ecj/docs/manual/manual.pdf.

### Project structure ###

The main project is located in `/src/mengxu/`. It contains the following main packages:

* `algorithm/` contains a number of algorithms to run. They are good entry points to start with.
* `dax/` contains a number of benchmark workflows.
* `gp/` contains the core classes for GP for evolving rules.
* `taskscheduling/` contains the core classes for representing a workflow scheduling system.
* `rule/` contains the core classes for representing dispatching rules.
* `ruleanalysis/` contains the classes for rule analysis, e.g. reading rules from the ECJ result file, testing rules, calculating the program length, depth, number of unique terminals, etc.
* `ruleevalulation/` contains the evaluation models for rules, such as discrete event simulation, etc.
* `ruleoptimisation/` contains the classes for optimisation rules, e.g. RuleOptimisationProblem.
* `simulation/` contains the classes for discrete event simulation for dynamic workflow scheduling.

### Running experiments ###

**Example (Multi-tree GP):**

1. Locate the param file `src/mengxu/algorithm/multitreegp/multipletreegp-dynamicBaseline.params`
2. Run `src/mengxu/gp/GPMain.java`.
3. Finally you will get a result file `job.[x].out.stat` in the project home directory, where [x] is the job id.

This work was published on

Meng Xu, Yi Mei, Shiqiang Zhu, Beibei Zhang, Tian Xiang, Fangfang Zhang and Mengjie Zhang, "Genetic Programming for Dynamic Workflow Scheduling in Fog Computing," in IEEE Transactions on Services Computing, 2023.


### Who do I talk to? ###

* Email: meng.xu@ecs.vuw.ac.nz

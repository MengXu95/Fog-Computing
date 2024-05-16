#Want to peform a paired t test on each FJSS instance for two evolution options
#Each FJSS is run 30 times for each evolution option,
#Will be looking at data in /cleaned/ folders
#Will have to specify two folder names within cleaned
#eg: Rscript t_test.R static coevolve simple
#eg: Rscript t_test.R dynamic coevolution simple

working_dir <- "/Users/mengxu/Desktop/XUMENG/ZheJiangLab/ModifiedSimulation/submitToGrid/modified/"
setwd(working_dir)

sprintf("------------------------Start------------------------------")
algos <- c("small", "middle","large")
devices <- c("1", "2", "3")
algo.names <- c("small", "middle","large")
scenarios.name <- c("","","","","","","","","")

# For HEFT
# compareName <- "HEFT"
# best_makespans_b_all <- c(5227.96, 5355.18, 9473.61,
#                            7420.53, 7983.52, 21718.75,
#                            14147.63, 14240.38, 17556.07)

# For FCFS
# compareName <- "FCFS"
# best_makespans_b_all <- c(4870.40, 3668.74, 3196.84,
#                            6312.82, 5345.99, 5614.64,
#                            12378.34, 9996.72, 8622.54)

# For MaxMin
# compareName <- "MaxMin"
# best_makespans_b_all <- c(7162.96, 6909.52, 6451.66,
#                           9751.32, 10921.74, 11545.14,
#                           18185.86, 20093.12, 22647.97)
#
# # For MinMin
compareName <- "MinMin"
best_makespans_b_all <- c(7183.37, 6301.57, 6370.48,
                          9353.39, 9945.55, 10737.11,
                          17005.42, 21158.80, 20759.38)

# best_makespans_b_HEFT <- c(c(rep(5227.96, 30)), c(rep(5355.18, 30)), c(rep(9473.61, 30)),
#                            c(rep(7420.53, 30)), c(rep(7983.52, 30)), c(rep(21718.75, 30)),
#                            c(rep(14147.63, 30)), c(rep(14240.38, 30)), c(rep(17556.07, 30)))

  result.df <- data.frame(Scenario = character(),
                        Algo = character(),
                        Device = integer(),
                        Run = integer(),
                        Generation = integer(),
                        SeqRuleSize = integer(),
                        SeqRuleUniqueTerminals = integer(),
                        RoutRuleSize = integer(),
                        RoutRuleUniqueTerminals = integer(),
                        Obj = integer(),
                        TrainFitness = double(),
                        TestFitness = double(),
                        TrainTime = double()
)


for (a in 1:length(algos)) {
  algo <- algos[a]
  for (m in 1:length(devices)){
    scenario.name <- paste0("N",algo,m,"MTGP")
    scenarios.name[(a-1)*3+m] <- scenario.name
    #scenario <- paste0(objectives[s], "-", utils[s], "-", ddfactors[s])
    testfile <- paste0("result.csv")
    df <- read.csv(paste0(algo, "/", scenario.name, "/results/test/", testfile), header = TRUE)
    result.df <- rbind(result.df,
                       cbind(Scenario = rep(scenario.name, nrow(df)),
                             Algo = rep(algo.names[a], nrow(df)),
                             Device = rep(devices[m], nrow(df)),
                             df))
  }
}


p_value_function = function(a, b) {
  constant_arrays = TRUE
  a_1 = a[1]
  b_1 = b[1]
  i = 2
  while (i <= 30 && constant_arrays) {
    if (!(a_1 == a[i] && b_1 == b[i])) {
      constant_arrays = FALSE
    }
    i = i + 1
  }
  if (constant_arrays) {
    #both arrays are constant, so t test won't work
    if (a_1 == b_1) {
      #all values in both arrays are constant - can't say one
      #is better than the other
      return (NA)
    } else if (a_1 < b_1) {
      #a has better makespans
      return (0)
    } else {
      #b has better makespans
      return (1)
    }
  } else {
    wilcox_test = wilcox.test(a,b,paired=FALSE,alternative = "l")
    return (wilcox_test$p.value)
  }
}


# output_dir = paste("t_tests/",sep="")
# output_file = paste(algos[1],"-",algos[2],"-routing-results.csv",sep="")
# #create the matrix which will store all our results
# output_matrix = matrix(, nrow = length(scenarios.name), ncol = 4)
# colnames(output_matrix) = c("filename",paste(algos[1],">",algos[2],sep=""),paste(algos[1],"=",algos[2],sep=""),paste(algos[1],"<",algos[2],sep=""))

i=1
for (s in 1:(length(scenarios.name)-1)) {
  scenario.name <- scenarios.name[s]
  output_dir = paste("t_tests/",sep="")
  output_file = paste(scenarios.name[s],"-",compareName,"-testFitness-results.csv",sep="")
  #create the matrix which will store all our results
  output_matrix = matrix(, nrow = 1, ncol = 4)
  colnames(output_matrix) = c("filename",paste(scenarios.name[s],">",compareName,sep=""),paste(scenarios.name[s],"=",compareName,sep=""),paste(scenarios.name[s],"<",compareName,sep=""))


  a_better = 0
  b_better = 0
  equal = 0

  best_makespans_a = as.numeric(subset(result.df, Generation == 50 & Scenario == scenarios.name[s])$TestFitness)

  best_makespans_b = as.numeric(rep(best_makespans_b_all[s], 30))

  p_val_a = p_value_function(best_makespans_a,best_makespans_b)
  if (is.na(p_val_a)) {
    equal = 1
  } else if (p_val_a >= 0.05/4) {
    #either neither a and b is better, or b is better
    p_val_b = p_value_function(best_makespans_b,best_makespans_a)
    if (p_val_b < 0.05/4) {
      #b is better than a for this file
      b_better = 1
    } else {
      #equal
      equal = 1
    }
  } else {
    a_better = 1
  }

  output_matrix[i,] = c(scenario.name,a_better,equal,b_better)
  # i = i + 1

  write.csv(output_matrix,file=paste0(output_dir,output_file))

  print(paste("Wrote t-test results to directory:",output_dir))
  print(paste("Filename:",paste0(output_dir,output_file)))
  a_better = sum(output_matrix[,2]==1)
  b_better = sum(output_matrix[,4]==1)
  equal = sum(output_matrix[,3]==1)
  print(paste(scenarios.name[s],"was better in",a_better,"files."))
  print(paste(compareName,"was better in",b_better,"files."))
  print(paste("They were equal in",equal,"files."))
  print(paste("====================================================="))
}


#lets process our results, and save them in the matrix we made

#write.csv(output_matrix)



#Want to peform a paired t test on each FJSS instance for two evolution options
#Each FJSS is run 30 times for each evolution option,
#Will be looking at data in /cleaned/ folders
#Will have to specify two folder names within cleaned
#eg: Rscript t_test.R static coevolve simple
#eg: Rscript t_test.R dynamic coevolution simple

working_dir <- "/Users/mengxu/Desktop/XUMENG/ZheJiangLab/ModifiedSimulation/submitToGrid/newModified20220222"
setwd(working_dir)

sprintf("------------------------Start------------------------------")
# algos <- c("middle","large")
# devices <- c("1", "2", "3")
# algo.names <- c("middle","large")
# scenarios.name <- c("","","","","","")
algos <- c("small", "middle","large")
devices <- c("1", "2", "3")
algo.names <- c("small", "middle","large")
scenarios.name <- c("","","","","","","","","")

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
for (s in 1:(length(scenarios.name)-2)) {
  scenario.name <- scenarios.name[s]
  p <- s+2
  output_dir = paste("t_tests/",sep="")
  output_file = paste(scenarios.name[s],"-",scenarios.name[p],"-sequencing-results.csv",sep="")
  #create the matrix which will store all our results
  output_matrix = matrix(, nrow = 1, ncol = 4)
  colnames(output_matrix) = c("filename",paste(scenarios.name[s],">",scenarios.name[p],sep=""),paste(scenarios.name[s],"=",scenarios.name[p],sep=""),paste(scenarios.name[s],"<",scenarios.name[p],sep=""))


  a_better = 0
  b_better = 0
  equal = 0

  best_makespans_a = as.numeric(subset(result.df, Generation == 50 & Scenario == scenarios.name[s])$SeqRuleSize)

  best_makespans_b = as.numeric(subset(result.df, Generation == 50 & Scenario == scenarios.name[p])$SeqRuleSize)

  p_val_a = p_value_function(best_makespans_a,best_makespans_b)
  if (is.na(p_val_a)) {
    equal = 1
  } else if (p_val_a >= 0.05) {
    #either neither a and b is better, or b is better
    p_val_b = p_value_function(best_makespans_b,best_makespans_a)
    if (p_val_b < 0.05) {
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
  print(paste(scenarios.name[p],"was better in",b_better,"files."))
  print(paste("They were equal in",equal,"files."))
  print(paste("====================================================="))
}


#lets process our results, and save them in the matrix we made

#write.csv(output_matrix)



#Want to peform a paired t test on each FJSS instance for two evolution options
#Each FJSS is run 30 times for each evolution option,
#Will be looking at data in /cleaned/ folders
#Will have to specify two folder names within cleaned
#eg: Rscript t_test.R static coevolve simple
#eg: Rscript t_test.R dynamic coevolution simple

working_dir <- "D:/xumeng/PhdMainCode/Paper 4 - LexicaseSelection - new 20210527/"
setwd(working_dir)

sprintf("------------------------Start------------------------------")
algos <- c( "pBaselineMTGPD", "EpsilonLS50")
algo.names <- c("pBaselineMTGPD", "EpsilonLS50")

objectives <- rep(c("max-flowtime", "mean-flowtime"), 2)
utils <- c(rep(0.85, 2), rep(0.95, 2))
ddfactors <- rep(1.5, 4)

scenarios.name <- c("<Fmax, 0.85>", "<Fmax, 0.95>",
                    "<Fmean, 0.85>", "<Fmean, 0.95>")



#objectives <- rep(c("max-flowtime", "mean-flowtime", "mean-weighted-flowtime"), 2)
#utils <- c(rep(0.85, 3), rep(0.95, 3))
#ddfactors <- rep(1.5, 6)
#
#scenarios.name <- c("<Fmax, 0.85, 1.5>", "<Fmean, 0.85, 1.5>", "<WFmean, 0.85, 1.5>",
#                    "<Fmax, 0.95, 1.5>", "<Fmean, 0.95, 1.5>", "<WFmean, 0.95, 1.5>")

#objectives <- rep(c("max-flowtime", "mean-flowtime", "mean-weighted-flowtime"))
#utils <- c(rep(0.85, 3))
#ddfactors <- rep(1.5, 3)
#
#scenarios.name <- c("<Fmax, 0.85, 1.5>", "<Fmean, 0.85, 1.5>", "<WFmean, 0.85, 1.5>")

#objectives <- rep(c("max-flowtime", "mean-flowtime", "mean-weighted-flowtime"), 2)
#utils <- c(rep(0.85, 3), rep(0.95, 3))
#ddfactors <- rep(1.5, 6)
#
#scenarios.name <- c("<Fmax, 0.85, 1.5>", "<Fmean, 0.85, 1.5>", "<WFmean, 0.85, 1.5>",
#                    "<Fmax, 0.95, 1.5>", "<Fmean, 0.95, 1.5>", "<WFmean, 0.95, 1.5>")

result.df <- data.frame(Scenario = character(),
                        Algo = character(),
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


output_dir = paste("t_tests/",sep="")
output_file = paste(algos[1],"-",algos[2],"-results.csv",sep="")
#create the matrix which will store all our results
output_matrix = matrix(, nrow = length(scenarios.name), ncol = 4)
colnames(output_matrix) = c("filename",paste(algos[1],">",algos[2],sep=""),paste(algos[1],"=",algos[2],sep=""),paste(algos[1],"<",algos[2],sep=""))


i=1
for (s in 1:length(scenarios.name)) {
  scenario.name <- scenarios.name[s]
  scenario <- paste0(objectives[s], "-", utils[s], "-", ddfactors[s])
  testfile <- paste0("missing-", utils[s], "-", ddfactors[s], ".csv")

  a_better = 0
  b_better = 0
  equal = 0

  algoa <- algos[1]
  dfa <- read.csv(paste0(algoa, "/trainResults/", scenario, "/test/", testfile), header = TRUE)
  #best_makespans_a = as.numeric(unlist(dfa["TestFitness"]))
  best_makespans_a = as.numeric(subset(dfa, Generation == 50)$TestFitness)

  algob <- algos[2]
  dfb <- read.csv(paste0(algob, "/trainResults/", scenario, "/test/", testfile), header = TRUE)
  #best_makespans_b = as.numeric(unlist(dfb["TestFitness"]))
  best_makespans_b = as.numeric(subset(dfb, Generation == 50)$TestFitness)

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
  i = i + 1

}


#lets process our results, and save them in the matrix we made

#write.csv(output_matrix)
write.csv(output_matrix,file=paste0(output_dir,output_file))

print(paste("Wrote t-test results to directory:",output_dir))
print(paste("Filename:",paste0(output_dir,output_file)))
a_better = sum(output_matrix[,2]==1)
b_better = sum(output_matrix[,4]==1)
equal = sum(output_matrix[,3]==1)
print(paste(algo.names[1],"was better in",a_better,"files."))
print(paste(algo.names[2],"was better in",b_better,"files."))
print(paste("They were equal in",equal,"files."))


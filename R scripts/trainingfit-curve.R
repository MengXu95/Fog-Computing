library(ggplot2)

setwd("/Users/dyska/Desktop/Uni/COMP489/GPJSS/grid_results/dynamic/test")

algos = c("simple_modified_terminal_final", "coevolution_modified_terminal_final")
algo.names <- c("SimpleGP","CCGP")
objectives <- rep(c("mean-flowtime", "max-flowtime", "mean-weighted-flowtime"), 2)
utils <- c(rep(0.85, 3), rep(0.95, 3))
ddfactors <- rep(4, 6)

scenarios.name <- c("<Mean FT, 0.85>", "<Max FT, 0.85>", "<Mean WFT, 0.85>",
                    "<Mean FT, 0.95>", "<Max FT, 0.95>", "<Mean WFT, 0.95>")

result.df <- data.frame(Scenario = character(),
                        Algo = character(),
                        Run = integer(),
                        Generation = integer(),
                        Size = integer(),
                        UniqueTerminals = integer(),
                        Obj = integer(),
                        TrainFitness = double(),
                        TestFitness = double(),
                        Time = double())

for (s in 1:length(scenarios.name)) {
  scenario.name <- scenarios.name[s]
  scenario <- paste0(utils[s],"-",objectives[s])
  testfile <- paste0("missing-", utils[s], "-", ddfactors[s], ".csv")
  
  for (a in 1:length(algos)) {
    algo <- algos[a]
    df <- read.csv(paste0(algo, "/", scenario,"/",testfile), header = TRUE)
    result.df <- rbind(result.df, 
                       cbind(Scenario = rep(scenario.name, nrow(df)),
                             Algo = rep(algo.names[a], nrow(df)), 
                             df))
  }
}

runs <- unique(result.df$Run)
generations <- 51 #changed here

trainfit.df <- data.frame(Scenario = character(),
                         Algo = character(),
                         Generation = integer(),
                         Mean = double(),
                         StdDev = double(),
                         StdError = double(),
                         ConfInterval = double())

for (s in 1:length(scenarios.name)) {
  scenario.name <- scenarios.name[s]
  
  for (a in 1:length(algos)) {
    algo <- algo.names[a]
    
    for (g in 1:generations) {
      rows <- subset(result.df, Scenario == scenario.name &
                       Algo == algo & Generation == g)
      
      if (nrow(rows) == 0)
        next
      
      rows.mean <- mean(rows$TrainFitness)
      rows.sd <- sd(rows$TrainFitness)
      rows.se <- rows.sd / sqrt(nrow(rows))
      rows.ci <- 1.96 * rows.sd
      
      trainfit.df <- rbind(trainfit.df, data.frame(Scenario = scenario.name,
                                                 Algo = algo,
                                                 Generation = g,
                                                 Mean = rows.mean,
                                                 StdDev = rows.sd,
                                                 StdError = rows.se,
                                                 ConfInterval = rows.ci))
    }
  }
}

g <- ggplot(trainfit.df, aes(Generation, Mean, colour = factor(Algo), shape = factor(Algo))) +
  geom_ribbon(aes(ymin = Mean-StdError, ymax = Mean+StdError, fill = factor(Algo)), alpha = 0.3) +
  geom_line() + geom_point(size = 1)
g <- g + facet_wrap(~ Scenario, ncol = 3, scales = "free")

g <- g + theme(legend.title = element_blank())
g <- g + theme(legend.position = "bottom")

g <- g + labs(y = "Training Fitness")

g <- g + theme(axis.title.x = element_text(size = 12, face = "bold"))
g <- g + theme(axis.title.y = element_text(size = 12, face = "bold"))
g <- g + theme(axis.text.x = element_text(size = 10))
g <- g + theme(axis.text.y = element_text(size = 10))
g <- g + theme(strip.text.x = element_text(size = 12))

ggsave("trainfit-curve.pdf", width = 9, height = 6)

# table showing

finalTrainFit.df <- data.frame(Scenario = character(),
                              Algo = character(),
                              Run = integer(),
                              TrainFitness = double())

for (s in 1:length(scenarios.name)) {
  scenario.name <- scenarios.name[s]
  
  for (a in 1:length(algos)) {
    algo <- algo.names[a]
    
    rows <- subset(result.df, Scenario == scenario.name & Algo == algo & Generation == generations)
    
    finalTrainFit.df <- rbind(finalTrainFit.df, data.frame(Scenario = rep(scenario.name, nrow(rows)),
                                                         Algo = rep(algo, nrow(rows)),
                                                         Run = rows$Run,
                                                         TrainFitness = rows$TrainFitness))
  }
}

for (s in 1:length(scenarios.name)) {
  scenario.name <- scenarios.name[s]
  rows1 <- subset(finalTrainFit.df, Scenario == scenario.name & Algo == algo.names[1])
  rows2 <- subset(finalTrainFit.df, Scenario == scenario.name & Algo == algo.names[2])
  #rows3 <- subset(finalTrainFit.df, Scenario == scenario.name & Algo == algo.names[3])
  cat(sprintf("%s & %.2f(%.2f) & %.2f(%.2f) & %.2f(%.2f) \\\\\n", scenarios.name[s], mean(rows1$TrainFitness), sd(rows1$TrainFitness), mean(rows2$TrainFitness), sd(rows2$TrainFitness), mean(rows3$TrainFitness), sd(rows3$TrainFitness)))          
}

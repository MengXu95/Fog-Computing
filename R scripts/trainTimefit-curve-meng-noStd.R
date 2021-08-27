library(ggplot2)

working_dir <- "D:/xumeng/ZheJiangLab/"
setwd(working_dir)

sprintf("------------------------Start------------------------------")
algos <- c("small2MTGP")
algo.names <- c("MTGP")
scenarios.name <- c("makespan")

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
                        #GenotypeDiversity = double(),
                        #PhenotypeDiversity = double(),
                        #EntropyDiversity = double(),
                        #PseudoIsomorphsDiversity = double(),
                        #EditOneDiversity = double(),
                        #EditTwoDiversity = double()
                        #PCDiversity = double()
                        #AveSeqRulesize = integer(),
                        #AveRouRuleSize = integer(),
                        #AveRuleSize = integer()
)

for (s in 1:length(scenarios.name)) {
  scenario.name <- scenarios.name[s]
  testfile <- paste0("result.csv")
  #scenario <- paste0(objectives[s], "-", utils[s], "-", ddfactors[s])
  #testfile <- paste0("missing-", utils[s], "-", ddfactors[s], ".csv")
  
  for (a in 1:length(algos)) {
    algo <- algos[a]
    df <- read.csv(paste0(algo, "/results/test/", testfile), header = TRUE)
    #df <- read.csv(paste0(algo, "/trainResults/", scenario, "/test/", testfile), header = TRUE)
    result.df <- rbind(result.df, 
                       cbind(Scenario = rep(scenario.name, nrow(df)),
                             Algo = rep(algo.names[a], nrow(df)), 
                             df))
  }
}

runs <- unique(result.df$Run)
generations <- max(result.df$Generation)

testfit.df <- data.frame(Scenario = character(),
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

      rows.mean <- mean(rows$TrainTime)
      rows.sd <- sd(rows$TrainTime)
      rows.se <- rows.sd / sqrt(nrow(rows))
      rows.ci <- 1.96 * rows.sd

      testfit.df <- rbind(testfit.df, data.frame(Scenario = scenario.name,
                                                 Algo = algo,
                                                 Generation = g,
                                                 Mean = rows.mean,
                                                 StdDev = rows.sd,
                                                 StdError = rows.se,
                                                 ConfInterval = rows.ci))
    }
  }
}

g <- ggplot(testfit.df, aes(Generation, Mean, colour = factor(Algo), shape = factor(Algo))) +
  geom_ribbon(aes(ymin = Mean, ymax = Mean, fill = factor(Algo)), alpha = 0.3) +
  geom_line() + geom_point(size = 1)
g <- g + facet_wrap(~ Scenario, ncol = 3, scales = "free")

g <- g + theme(legend.title = element_blank())
g <- g + theme(legend.position = "bottom")

g <- g + labs(y = "Train Time")

g <- g + theme(axis.title.x = element_text(size = 12, face = "bold"))
g <- g + theme(axis.title.y = element_text(size = 12, face = "bold"))
g <- g + theme(axis.text.x = element_text(size = 10))
g <- g + theme(axis.text.y = element_text(size = 10))
g <- g + theme(strip.text.x = element_text(size = 12))

ggsave("TrainTime-curve-noStd.pdf", width = 9, height = 3)

# table showing

finalTestFit.df <- data.frame(Scenario = character(),
                              Algo = character(),
                              Run = integer(),
                              TestFitness = double())

for (s in 1:length(scenarios.name)) {
  scenario.name <- scenarios.name[s]
  
  for (a in 1:length(algos)) {
    algo <- algo.names[a]
    
    rows <- subset(result.df, Scenario == scenario.name & Algo == algo & Generation == generations)
    
    finalTestFit.df <- rbind(finalTestFit.df, data.frame(Scenario = rep(scenario.name, nrow(rows)),
                                                         Algo = rep(algo, nrow(rows)),
                                                         Run = rows$Run,
                                                         TrainTime = rows$TrainTime))
  }
}

for (s in 1:length(scenarios.name)) {
  scenario.name <- scenarios.name[s]
  rows1 <- subset(finalTestFit.df, Scenario == scenario.name & Algo == algo.names[1])
  rows2 <- subset(finalTestFit.df, Scenario == scenario.name & Algo == algo.names[2])
  #rows3 <- subset(finalTestFit.df, Scenario == scenario.name & Algo == algo.names[3])

  cat(sprintf("%s
  & %.2f - %.2f(%.2f) & %.2f - %.2f(%.2f)\\\\\n",
              scenarios.name[s],
              min(rows1$TrainTime), mean(rows1$TrainTime), sd(rows1$TrainTime),
              min(rows2$TrainTime), mean(rows2$TrainTime), sd(rows2$TrainTime)))
              #min(rows3$TrainTime), mean(rows3$TrainTime), sd(rows3$TrainTime)))
}

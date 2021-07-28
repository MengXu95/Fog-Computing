library(ggplot2)

working_dir <- "D:/xumeng/PhdMainCode/Paper 3 - MCTS/"
setwd(working_dir)

sprintf("------------------------Start------------------------------")
algos <- c("pBenchMTGPDv1", "pNichMTGPDv1", "pOneMTGPD")
algo.names <- c("pBenchMTGPDv1", "pNichMTGPDv1", "pOneMTGPD")

objectives <- rep(c("max-flowtime", "mean-flowtime", "mean-weighted-flowtime"))
utils <- c(rep(0.85, 3))
ddfactors <- rep(1.5, 3)

scenarios.name <- c("<Fmax, 0.85, 1.5>", "<Fmean, 0.85, 1.5>", "<WFmean, 0.85, 1.5>")

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
                        #AveSeqRulesize = integer(),
                        #AveRouRuleSize = integer(),
                        #AveRuleSize = integer()
)

for (s in 1:length(scenarios.name)) {
  scenario.name <- scenarios.name[s]
  scenario <- paste0(objectives[s], "-", utils[s], "-", ddfactors[s])
  testfile <- paste0("missing-", utils[s], "-", ddfactors[s], ".csv")

  for (a in 1:length(algos)) {
    algo <- algos[a]
    df <- read.csv(paste0(algo, "/trainResults/", scenario, "/test/", testfile), header = TRUE)
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
      
      rows.mean <- mean(rows$RoutRuleSize)
      rows.sd <- sd(rows$RoutRuleSize)
      #rows.mean <- mean(rows$SeqRuleUniqueTerminals)
      #rows.sd <- sd(rows$SeqRuleUniqueTerminals)
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

testfit.df$Scenario <- factor(testfit.df$Scenario, levels = scenarios.name) #2020.10.20 order the appearrence of subplots
g <- ggplot(testfit.df, aes(Generation, Mean, colour = factor(Algo), shape = factor(Algo))) +
  geom_ribbon(aes(ymin = Mean, ymax = Mean, fill = factor(Algo)), alpha = 0.3) +
  geom_line() + geom_point(size = 1)
g <- g + facet_wrap(~ Scenario, ncol = 3, scales = "free")

g <- g + theme(legend.title = element_blank())
g <- g + theme(legend.position = "bottom")
g <- g + theme(legend.text = element_text(size = 19))

#g <- g + labs(y = "The Mean Number of Unique Features in Routing Rules")
g <- g + labs(y = "The Mean Number of Routing Rules Size")
#g <- g + labs(y = "The Mean Number of Unique Features in Sequencing Rules")

g <- g + theme(axis.title.x = element_text(size = 17, face = "bold"))
g <- g + theme(axis.title.y = element_text(size = 17, face = "bold"))
g <- g + theme(axis.text.x = element_text(size = 15))
g <- g + theme(axis.text.y = element_text(size = 15))
g <- g + theme(strip.text.x = element_text(size = 17))

ggsave("test-program-size-curve-routing.pdf", width = 9, height = 3)
#ggsave("test-program-size-curve-sequencing.pdf", width = 9, height = 6)
# table showing

finalTestFit.df <- data.frame(Scenario = character(),
                              Algo = character(),
                              Run = integer(),
                              RoutRuleSize = integer())

for (s in 1:length(scenarios.name)) {
  scenario.name <- scenarios.name[s]
  
  for (a in 1:length(algos)) {
    algo <- algo.names[a]
    
    rows <- subset(result.df, Scenario == scenario.name & Algo == algo & Generation == generations)
    
    finalTestFit.df <- rbind(finalTestFit.df, data.frame(Scenario = rep(scenario.name, nrow(rows)),
                                                         Algo = rep(algo, nrow(rows)),
                                                         Run = rows$Run,
                                                         RoutRuleSize = rows$RoutRuleSize))
  }
}

for (s in 1:length(scenarios.name)) {
  scenario.name <- scenarios.name[s]
  rows1 <- subset(finalTestFit.df, Scenario == scenario.name & Algo == algo.names[1])
  rows2 <- subset(finalTestFit.df, Scenario == scenario.name & Algo == algo.names[2])
  rows3 <- subset(finalTestFit.df, Scenario == scenario.name & Algo == algo.names[3])
  cat(sprintf("%s & %.2f(%.2f) & %.2f(%.2f) & %.2f(%.2f)\\\\\n",
              scenarios.name[s],
              mean(rows1$RoutRuleSize), sd(rows1$RoutRuleSize),
              mean(rows2$RoutRuleSize), sd(rows2$RoutRuleSize),
              mean(rows3$RoutRuleSize), sd(rows3$RoutRuleSize)))
}

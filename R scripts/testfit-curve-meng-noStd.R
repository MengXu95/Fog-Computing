library(ggplot2)

working_dir <- "D:/xumeng/ZheJiangLab/ModifiedSimulation/submit to grid/"
setwd(working_dir)

sprintf("------------------------Start------------------------------")
algos <- c("newsmall1MTGP", "newsmall2MTGP")
algo.names <- c("small1", "small2")
scenarios.name <- c("small1", "small2")

#objectives <- rep(c("mean-flowtime"), 1)
#utils <- c(rep(0.85, 1))
#ddfactors <- rep(1.5, 1)
#
#scenarios.name <- c("<Fmean, 0.85>")

#working_dir <- "D:/xumeng/PhdMainCode/Paper 3 - MCTS - new/new grid/"
#setwd(working_dir)
#
#sprintf("------------------------Start------------------------------")
#algos <- c("pBaselineMTGPD", "pCrossTwoMTGPD", "pCrossSixMTGPD")
#algo.names <- c("pBaselineMTGPD", "pCrossTwoMTGPD", "pCrossSixMTGPD")


#algos <- c("pBenchMTGPDv1", "pNichMTGPDv1", "pOneMTGPD", "pTwoMTGPD", "pThreeMTGPD", "pFourMTGPD", "pFiveMTGPD", "pSixMTGPD")
#algo.names <- c("pBenchMTGPDv1", "pNichMTGPDv1", "pOneMTGPD", "pTwoMTGPD", "pThreeMTGPD", "pFourMTGPD", "pFiveMTGPD", "pSixMTGPD")


#objectives <- rep(c("max-flowtime", "max-weighted-flowtime", "max-tardiness"), 2)
#utils <- c(rep(0.85, 3), rep(0.95, 3))
#ddfactors <- rep(1.5, 6)



#objectives <- rep(c("max-flowtime", "max-weighted-flowtime", "max-tardiness"), 2)
#utils <- c(rep(0.85, 3), rep(0.95, 3))
#ddfactors <- rep(1.5, 6)
#
#scenarios.name <- c("<Fmax, 0.85, 1.5>", "<WFmax, 0.85, 1.5>", "<Tmax, 0.85, 1.5>",
#                    "<Fmax, 0.95, 1.5>", "<WFmax, 0.95, 1.5>", "<Tmax, 0.95, 1.5>")


#objectives <- rep(c("max-flowtime", "max-flowtime"))
##objectives <- rep(c("max-flowtime", "max-flowtime"))
#utils <- rep(c(0.85, 0.95))
#ddfactors <- rep(1.5, 2)
#
#scenarios.name <- c("<Fmax, 0.85>", "<Fmax, 0.95>")

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

#result.df <- data.frame(Scenario = character(),
#                        Algo = character(),
#                        Run = integer(),
#                        Generation = integer(),
#                        Size = integer(),
#                        UniqueTerminals = integer(),
#                        Obj = integer(),
#                        TrainFitness = double(),
#                        TestFitness = double(),
#                        Time = double())

for (s in 1:length(scenarios.name)) {
  scenario.name <- scenarios.name[s]
  #scenario <- paste0(objectives[s], "-", utils[s], "-", ddfactors[s])
  testfile <- paste0("result.csv")
  
  for (a in 1:length(algos)) {
    algo <- algos[a]
    df <- read.csv(paste0(algo, "/results/test/", testfile), header = TRUE)
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

      rows.mean <- mean(rows$TestFitness)
      rows.sd <- sd(rows$TestFitness)
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

testfit.df$Algo <- factor(testfit.df$Algo, levels = scenarios.name) #2020.10.20 order the appearrence of subplots
g <- ggplot(testfit.df, aes(Generation, Mean, colour = factor(Algo), shape = factor(Algo))) +
  geom_ribbon(aes(ymin = Mean, ymax = Mean, fill = factor(Algo)), alpha = 0.3) +
  geom_line() + geom_point(size = 1)
#g <- g + facet_wrap(~ Scenario, nrow = 2, scales = "free")
#g <- g + facet_wrap(~ Scenario, ncol = 3, scales = "free")
g <- g + facet_wrap(~ Algo, ncol = 3, scales = "free")

g <- g + theme(legend.title = element_blank())
g <- g + theme(legend.position = "bottom")
g <- g + theme(legend.text = element_text(size = 19))

g <- g + labs(y = "makespan")

g <- g + theme(axis.title.x = element_text(size = 17, face = "bold"))
g <- g + theme(axis.title.y = element_text(size = 17, face = "bold"))
g <- g + theme(axis.text.x = element_text(size = 15))
g <- g + theme(axis.text.y = element_text(size = 15))
g <- g + theme(strip.text.x = element_text(size = 17))

#g <- g + theme(axis.title.x = element_text(size = 12, face = "bold"))
#g <- g + theme(axis.title.y = element_text(size = 12, face = "bold"))
#g <- g + theme(axis.text.x = element_text(size = 10))
#g <- g + theme(axis.text.y = element_text(size = 10))
#g <- g + theme(strip.text.x = element_text(size = 12))

ggsave("testfit-curve-noStd.pdf", width = 6, height = 3.5)
#ggsave("testfit-curve-noStd.pdf", width = 10, height = 5)

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
                                                         TestFitness = rows$TestFitness))
  }
}

for (s in 1:length(scenarios.name)) {
  scenario.name <- scenarios.name[s]
  rows1 <- subset(finalTestFit.df, Scenario == scenario.name & Algo == algo.names[1])
  rows2 <- subset(finalTestFit.df, Scenario == scenario.name & Algo == algo.names[2])
  rows3 <- subset(finalTestFit.df, Scenario == scenario.name & Algo == algo.names[3])

  cat(sprintf("%s
  & %.2f - %.2f(%.2f) & %.2f - %.2f(%.2f) & %.2f - %.2f(%.2f)\\\\\n",
              scenarios.name[s],
              min(rows1$TestFitness), mean(rows1$TestFitness), sd(rows1$TestFitness),
              min(rows2$TestFitness), mean(rows2$TestFitness), sd(rows2$TestFitness),
              min(rows3$TestFitness), mean(rows3$TestFitness), sd(rows3$TestFitness)))
}

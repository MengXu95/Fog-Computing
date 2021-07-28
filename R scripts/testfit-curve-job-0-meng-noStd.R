library(ggplot2)

working_dir <- "D:/xumeng/PhdMainCode/documents for CEC paper/archive_based_idea/"
setwd(working_dir)

sprintf("------------------------Start------------------------------")
algos <- c("iMTAGP","jMTAGP")
algo.names <- c("iMTAGP-0-0","jMTAGP-512-0.015")

#algos <- c("fMTAGP","aMTAGP","bMTAGP","cMTAGP","dMTAGP","eMTAGP","hMTAGP","MTAGP")
#algo.names <- c("MTAGP-0-0","MTAGP-1024-0.02-no-niching","MTAGP-1024-0.05-ISC","MTAGP-1024-0.02","MTAGP-512-0.01","MTAGP-512-0.015-ISC","MTAGP-512-0.01-no-Surrogate","MTAGP-1024-0.1")

#algos <- c("dMTAGP","eMTAGP","hMTAGP","MTAGP")
#algo.names <- c("MTAGP-512-0.01","MTAGP-512-0.015-ISC","MTAGP-512-0.01-no-Surrogate","MTAGP-1024-0.1")

#algos <- c("fMTAGP","aMTAGP","bMTAGP","cMTAGP")
#algo.names <- c("MTAGP-0-0","MTAGP-1024-0.02-no-niching","MTAGP-1024-0.05-ISC","MTAGP-1024-0.02")
# algos <- c("dagp-penalty-1", "dagp-penalty-0.1", "dagp-penalty-0.01")
# algo.names <- c("1", "0.1", "0.01")

#objectives <- rep(c("mean-flowtime", "mean-tardiness", "mean-weighted-tardiness"), 2)
#utils <- c(rep(0.85, 3), rep(0.95, 3))
#ddfactors <- rep(1.5, 6)
#
#scenarios.name <- c("<FTmean, 0.85, 1.5>", "<Tmean, 0.85, 1.5>", "<WTmean, 0.85, 1.5>",
#                    "<FTmean, 0.95, 1.5>", "<Tmean, 0.95, 1.5>", "<WTmean, 0.95, 1.5>")

objectives <- rep(c("max-flowtime", "mean-flowtime", "mean-weighted-flowtime"), 2)
utils <- c(rep(0.85, 3), rep(0.95, 3))
ddfactors <- rep(1.5, 6)

scenarios.name <- c("<FTmax, 0.85, 1.5>", "<FTmean, 0.85, 1.5>", "<WFTmean, 0.85, 1.5>",
                    "<FTmax, 0.95, 1.5>", "<FTmean, 0.95, 1.5>", "<WFTmean, 0.95, 1.5>")

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
                       Algo == algo & Run == 0 & Generation == g)

      if (nrow(rows) == 0)
        next

      rows.mean <- mean(rows$TestFitness)
      #rows.mean <- mean(rows$TestFitness)
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

g <- ggplot(testfit.df, aes(Generation, Mean, colour = factor(Algo), shape = factor(Algo))) +
  geom_ribbon(aes(ymin = Mean, ymax = Mean, fill = factor(Algo)), alpha = 0.3) +
  geom_line() + geom_point(size = 1)
g <- g + facet_wrap(~ Scenario, ncol = 3, scales = "free")

g <- g + theme(legend.title = element_blank())
g <- g + theme(legend.position = "bottom")

g <- g + labs(y = "Test Fitness")

g <- g + theme(axis.title.x = element_text(size = 12, face = "bold"))
g <- g + theme(axis.title.y = element_text(size = 12, face = "bold"))
g <- g + theme(axis.text.x = element_text(size = 10))
g <- g + theme(axis.text.y = element_text(size = 10))
g <- g + theme(strip.text.x = element_text(size = 12))

ggsave("testfit-curve-job-0-noStd.pdf", width = 9, height = 6)

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
  cat(sprintf("%s
  & %.2f - %.2f(%.2f) & %.2f - %.2f(%.2f)\\\\\n",
              scenarios.name[s],
              min(rows1$TestFitness), mean(rows1$TestFitness), sd(rows1$TestFitness),
              min(rows2$TestFitness), mean(rows2$TestFitness), sd(rows2$TestFitness)))
}

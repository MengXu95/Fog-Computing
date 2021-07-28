library(ggplot2)

working_dir <- "D:/xumeng/PhdMainCode/Paper 4 - LexicaseSelection/oneInstanceMultiCase/new grid 2/"
setwd(working_dir)

sprintf("------------------------Start------------------------------")
algos <- c("pBenchMTGPDv1", "pLSv1MTGPmc", "pLSv2MTGPmc", "pLSv3MTGPmc", "pLSv4MTGPmc", "pLSv5MTGPmc")
algo.names <- c("pBenchMTGPDv1", "pLSv1MTGPmc", "pLSv2MTGPmc", "pLSv3MTGPmc", "pLSv4MTGPmc", "pLSv5MTGPmc")


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

testfit.df$Scenario <- factor(testfit.df$Scenario, levels = scenarios.name) #2020.10.20 order the appearrence of subplots
g <- ggplot(testfit.df, aes(Generation, Mean, colour = factor(Algo), shape = factor(Algo))) +
  geom_violin()
#g <- g + facet_wrap(~ Scenario, nrow = 2, scales = "free")
g <- g + facet_wrap(~ Scenario, ncol = 3, scales = "free")

g <- g + theme(legend.title = element_blank())
g <- g + theme(legend.position = "bottom")
g <- g + theme(legend.text = element_text(size = 19))

g <- g + labs(y = "Test Fitness")

g <- g + theme(axis.title.x = element_text(size = 17, face = "bold"))
g <- g + theme(axis.title.y = element_text(size = 17, face = "bold"))
g <- g + theme(axis.text.x = element_text(size = 15))
g <- g + theme(axis.text.y = element_text(size = 15))
g <- g + theme(strip.text.x = element_text(size = 17))

ggsave("testfit-violin.pdf", width = 9, height = 6)
sprintf("------------------------End------------------------------")


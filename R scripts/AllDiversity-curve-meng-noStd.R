library(ggplot2)


working_dir <- "D:/xumeng/PhdMainCode/Paper 3 - MCTS/"
setwd(working_dir)

sprintf("------------------------Start------------------------------")
algos <- c("pOneMTGPD")
algo.names <- c("pOneMTGPD")

#algos <- c("pBenchMTGPDv1", "pNichMTGPDv1", "pOneMTGPD")
#algo.names <- c("pBenchMTGPDv1", "pNichMTGPDv1", "pOneMTGPD")


#objectives <- rep(c("max-flowtime", "mean-flowtime", "mean-weighted-flowtime"), 2)
#utils <- c(rep(0.85, 3), rep(0.95, 3))
#ddfactors <- rep(1.5, 6)
#
#scenarios.name <- c("<Fmax, 0.85, 1.5>", "<Fmean, 0.85, 1.5>", "<WFmean, 0.85, 1.5>",
#                    "<Fmax, 0.95, 1.5>", "<Fmean, 0.95, 1.5>", "<WFmean, 0.95, 1.5>")

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
                        TrainTime = double(),
                        GenotypeDiversity = double(),
                        PhenotypeDiversity = double(),
                        EntropyDiversity = double(),
                        PseudoIsomorphsDiversity = double(),
                        EditOneDiversity = double(),
                        EditTwoDiversity = double()
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
                         MeanGeno = double(),
                         MeanPheno = double(),
                         MeanEntropy = double(),
                         MeanPseudo = double(),
                         MeanEditOne = double(),
                         MeanEditTwo = double())

for (s in 1:length(scenarios.name)) {
  scenario.name <- scenarios.name[s]

  for (a in 1:length(algos)) {
    algo <- algo.names[a]

    for (g in 1:generations) {
      rows <- subset(result.df, Scenario == scenario.name &
                       Algo == algo & Generation == g)

      if (nrow(rows) == 0)
        next

      rows.mean <- mean(rows$EditTwoDiversity)
      rows.sd <- sd(rows$EditTwoDiversity)
      rows.se <- rows.sd / sqrt(nrow(rows))
      rows.ci <- 1.96 * rows.sd

      testfit.df <- rbind(testfit.df, data.frame(Scenario = scenario.name,
                                                 Algo = algo,
                                                 Generation = g,
                                                 MeanGeno = mean(rows$GenotypeDiversity),
                                                 MeanPheno = mean(rows$PhenotypeDiversity),
                                                 MeanEntropy = mean(rows$EntropyDiversity),
                                                 MeanPseudo = mean(rows$PseudoIsomorphsDiversity),
                                                 MeanEditOne = mean(rows$EditOneDiversity),
                                                 MeanEditTwo = mean(rows$EditTwoDiversity)))
    }
  }
}

testfit.df$Scenario <- factor(testfit.df$Scenario, levels = scenarios.name) #2020.10.20 order the appearrence of subplots
g <- ggplot(testfit.df) +
  geom_line(aes(x = Generation, y = MeanGeno, color = "green")) +
  geom_line(aes(x = Generation, y = MeanPheno, color = "red")) +
  geom_line(aes(x = Generation, y = MeanEntropy, color = "yellow")) +
  geom_line(aes(x = Generation, y = MeanPseudo, color = "black")) +
  geom_line(aes(x = Generation, y = MeanEditOne, color = "blue")) +
  geom_line(aes(x = Generation, y = MeanEditTwo, color = "grey")) +
  scale_colour_discrete("diversity",
                        labels=c("Geno","Pheno","Entropy","Pseudo","EditOne","EditTwo"))
#g <- g + facet_wrap(~ Scenario, nrow = 2, scales = "free")
g <- g + facet_wrap(~ Scenario, ncol = 3, scales = "free")

g <- g + theme(legend.title = element_blank())
g <- g + theme(legend.position = "bottom")
g <- g + theme(legend.text = element_text(size = 14))

g <- g + labs(y = "Diversity")

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

#ggsave("testfit-curve-noStd.pdf", width = 9, height = 6)
ggsave("AllDiversity-curve-noStd.pdf", width = 9, height = 3)

# table showing


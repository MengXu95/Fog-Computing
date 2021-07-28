library(ggplot2)

working_dir <- "D:/xumeng/PhdMainCode/documents for CEC paper/archive_based_idea/"
setwd(working_dir)

algo <- "3mMTAGP"
algo.name <- "mMTAGP-512-0.015"
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

scenarios.name <- c("<Fmax, 0.85, 1.5>", "<Fmean, 0.85, 1.5>", "<WFmean, 0.85, 1.5>",
                    "<Fmax, 0.95, 1.5>", "<Fmean, 0.95, 1.5>", "<WFmean, 0.95, 1.5>")

result.df <- data.frame(Scenario = character(),
                        Algo = character(),
                        indIndex = integer(),
                        fromGen = integer(),
                        Rank = integer()
                        )

for (s in 1:length(scenarios.name)) {
  scenario.name <- scenarios.name[s]
  scenario <- paste0(objectives[s], "-", utils[s], "-", ddfactors[s])
  testfile <- paste0("job.", 0, ".indsInPoolGenRank.csv")

  df <- read.csv(paste0(algo, "/trainResults/", scenario, "/", testfile), header = TRUE)
  result.df <- rbind(result.df,
                     cbind(Scenario = rep(scenario.name, nrow(df)),
                           Algo = rep(algo.name, nrow(df)),
                           df))
}

#runs <- unique(result.df$Run)
#archiveSizes <- 1024 #changed here
#
#testfit.df <- data.frame(Scenario = character(),
#                         Algo = character(),
#                         indIndex = integer(),
#                         fromGen = integer(),
#                         Rank = double())
#
#for (s in 1:length(scenarios.name)) {
#  scenario.name <- scenarios.name[s]
#
#  for (a in 1:length(algos)) {
#    algo <- algo.names[a]
#
#    for (g in 1:archiveSizes) {
#      rows <- subset(result.df, Scenario == scenario.name &
#                       Algo == algo & indIndex == g)
#
#      if (nrow(rows) == 0)
#        next
#
#      rows.mean <- mean(rows$Rank)
#
#      testfit.df <- rbind(testfit.df, data.frame(Scenario = scenario.name,
#                                                 Algo = algo,
#                                                 indIndex = g,
#                                                 fromGen = rows$fromGen,
#                                                 Rank = rows.mean))
#    }
#  }
#}

result.df$Scenario <- factor(result.df$Scenario, levels = scenarios.name) #2020.10.20 order the appearrence of subplots
g <- ggplot(result.df, aes(fromGen, Rank, colour = factor(Rank>5), shape = factor(Rank>5))) + geom_point(size = 1)
g <- g + facet_wrap(~ Scenario, ncol = 3, scales = "free")

#g <- g + theme(legend.title = element_blank())
g <- g + theme(legend.title = element_text())
g <- g + theme(legend.position = "bottom")
g <- g + theme(legend.text = element_text(size = 19))
g <- g + theme(legend.title = element_text(size = 19))

#g <- g + labs(y = "Rank versus Generation in archive.")
g <- g + labs(x = "Generation")
g <- g + labs(y = "Rank")

g <- g + theme(axis.title.x = element_text(size = 17, face = "bold"))
g <- g + theme(axis.title.y = element_text(size = 17, face = "bold"))
g <- g + theme(axis.text.x = element_text(size = 15))
g <- g + theme(axis.text.y = element_text(size = 15))
g <- g + theme(strip.text.x = element_text(size = 17))

ggsave("archive-point-0.pdf", width = 9, height = 6)

sprintf("Save successfully!")
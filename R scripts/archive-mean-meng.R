library(ggplot2)

working_dir <- "D:/xumeng/PhdMainCode/documents for CEC paper/archive_based_idea/"
setwd(working_dir)

algo <- "MTAGP"
algo.name <- "MTAGP"
# algos <- c("dagp-penalty-1", "dagp-penalty-0.1", "dagp-penalty-0.01")
# algo.names <- c("1", "0.1", "0.01")
objectives <- rep(c("mean-flowtime", "mean-tardiness", "mean-weighted-tardiness"), 2)
utils <- c(rep(0.85, 3), rep(0.95, 3))
ddfactors <- rep(1.5, 6)

scenarios.name <- c("<FTmean, 0.85, 1.5>", "<Tmean, 0.85, 1.5>", "<WTmean, 0.85, 1.5>",
                    "<FTmean, 0.95, 1.5>", "<Tmean, 0.95, 1.5>", "<WTmean, 0.95, 1.5>")

result.df <- data.frame(Scenario = character(),
                        Algo = character(),
                        indIndex = integer(),
                        fromGen = integer(),
                        Rank = integer()
                        )

for (s in 1:length(scenarios.name)) {
  scenario.name <- scenarios.name[s]
  scenario <- paste0(objectives[s], "-", utils[s], "-", ddfactors[s])
  for (r in 0:29) {
    testfile <- paste0("job.", r, ".indsInPoolGenRank.csv")

    df <- read.csv(paste0(algo, "/trainResults/", scenario, "/", testfile), header = TRUE)
    result.df <- rbind(result.df,
                       cbind(Scenario = rep(scenario.name, nrow(df)),
                             Algo = rep(algo.name, nrow(df)),
                             df))
  }
}

#runs <- unique(result.df$Run)
archiveSizes <- 1023 #changed here

testfit.df <- data.frame(Scenario = character(),
                         Algo = character(),
                         indIndex = integer(),
                         fromGen = integer(),
                         Rank = double())

for (s in 1:length(scenarios.name)) {
  scenario.name <- scenarios.name[s]


  for (g in 0:archiveSizes) {
    rows <- subset(result.df, Scenario == scenario.name &
                     Algo == algo & indIndex == g)

    if (nrow(rows) == 0)
      next

    rows.mean <- mean(rows$Rank)

    testfit.df <- rbind(testfit.df, data.frame(Scenario = scenario.name,
                                               Algo = algo,
                                               indIndex = g,
                                               fromGen = rows$fromGen,
                                               Rank = rows.mean))
  }
}

g <- ggplot(testfit.df, aes(fromGen, Rank, colour = factor(Rank<10), shape = factor(Rank<10))) + geom_point(size = 1)
g <- g + facet_wrap(~ Scenario, ncol = 2, scales = "free")

g <- g + theme(legend.title = element_blank())
g <- g + theme(legend.position = "bottom")

g <- g + labs(y = "Rank versus fromGen in archive.")

g <- g + theme(axis.title.x = element_text(size = 12, face = "bold"))
g <- g + theme(axis.title.y = element_text(size = 12, face = "bold"))
g <- g + theme(axis.text.x = element_text(size = 10))
g <- g + theme(axis.text.y = element_text(size = 10))
g <- g + theme(strip.text.x = element_text(size = 12))

ggsave("archive-mean.pdf", width = 6, height = 9)

sprintf("Save successfully!")
library(ggplot2)

setwd("/Users/mengxu/Desktop/XUMENG/ZheJiangLab/ModifiedSimulation/submitToGrid/modified/")

algos <- c("small", "large")

scenarios <- c("Nsmall1MTGP", "Nsmall2MTGP", "Nsmall3MTGP",
               "Nlarge1MTGP", "Nlarge2MTGP", "Nlarge3MTGP")
#
scenarios.name <- c("small, devices = 1", "small, devices = 2", "small, devices = 3",
                    "large, devices = 1", "large, devices = 2", "large, devices = 3")

terminals <- c("NIQ", "WIQ", "MRT", "UT", "DT", "PT", "TTIQ", "TIS", "TWT", "NTR")

terminals.df <- data.frame(Scenario = character(),
                           Terminal = character(),
                           Times = integer())

algo = algos[1]
for (i in 1:length(scenarios)) {
  if(i<=3){
    algo = algos[1]
  }
  else{
    algo = algos[2]
  }
  scenario <- scenarios[i]
  scenario.name <- scenarios.name[i]

  for(m in 1:length(terminals)){
    terminal <- terminals[m]
    filename <- paste0(algo, "/", scenario, "/results/test/feature-freq-seqRule.csv")
    df <- tryCatch(read.csv(filename, header = TRUE), error=function(e) NULL)
    rows <- subset(df, Feature == terminal)

    terminals.df <- rbind(terminals.df, data.frame(Scenario = scenario.name,
                                                   Terminal = terminal,
                                                   Times = sum(rows$Freq)))

  }

}

terminals.df$Scenario <- factor(terminals.df$Scenario, levels = scenarios.name)
g <- ggplot(terminals.df, aes(Terminal, Times, fill = factor(Terminal))) + geom_bar(stat="identity")
g <- g + facet_wrap(~ Scenario, ncol = 3, scales = "free")

g <- g + theme(legend.title = element_blank())
g <- g + theme(legend.position = "bottom")
g <- g + labs(y = "Times")

# g <- g + scale_fill_discrete(labels = c("Unselected", "Selected"))
g <- g + theme(axis.text.x = element_text(size = 10, angle = 70, vjust = 0.55))
g <- g + theme(axis.text.y = element_text(size = 10))
g <- g + theme(strip.text.x = element_text(size = 12))
g <- g + theme(axis.title.x = element_text(size = 12, face = "bold"))
g <- g + theme(axis.title.y = element_text(size = 12, face = "bold"))

ggsave("terminals-time-seqRule.pdf", width = 9, height = 6.5)




library(ggplot2)

setwd("/Users/dyska/Desktop/Uni/COMP489/GPJSS/grid_results/dynamic/raw/")
algo <- "coevolution_feature_selection" #"simple_feature_selection
ruletype <- "ROUTING"

scenarios <- c("0.85-max-flowtime", "0.85-mean-flowtime", "0.85-mean-weighted-flowtime",
               "0.95-max-flowtime", "0.95-mean-flowtime", "0.95-mean-weighted-flowtime")

scenarios.name <- c("<TmaxFT, 0.85>", "<TmeanFT, 0.85>", "<TmeanWFT, 0.85>",
                    "<TmaxFT, 0.95>", "<TmeanFT, 0.95>", "<TmeanWFT, 0.95>")

terminals <- c("NIQ", "WIQ", "MWT", "PT", "NPT", "OWT", "WKR", "NOR", "rDD", "W", "TIS", "SL")

terminals.df <- data.frame(Scenario = character(),
                           Run = integer(),
                           Terminal = character(),
                           Select = logical())
runs = 0:29

for (i in 1:length(scenarios)) {
  scenario <- scenarios[i]
  scenario.name <- scenarios.name[i]
  
  for (run in runs) {
    filename <- paste0(algo, "/", scenario, "/job.", run, " - ",ruletype,".terminals.csv")
    df <- tryCatch(read.csv(filename, header = FALSE), error=function(e) NULL)
    if (is.null(df)) {
      ddfactors <- rep(FALSE, length(terminals))
    } else {
      select <- (terminals %in% df$V1)
    }

    terminals.df <- rbind(terminals.df, data.frame(Scenario = rep(scenario.name, length(terminals)),
                                                   Run = rep(run + 1, length(terminals)),
                                                   Terminal = terminals,
                                                   Select = select))
  }
}

g <- ggplot(terminals.df, aes(Terminal, Run, fill = Select)) +
  geom_tile()
g <- g + facet_wrap(~ Scenario, ncol = 3, scales = "free")

g <- g + theme(legend.title = element_blank())
g <- g + theme(legend.position = "bottom")

g <- g + scale_fill_discrete(labels = c("Unselected", "Selected"))
g <- g + theme(axis.text.x = element_text(size = 10, angle = 70, vjust = 0.55))
g <- g + theme(axis.text.y = element_text(size = 10))
g <- g + theme(strip.text.x = element_text(size = 12))
g <- g + theme(axis.title.x = element_text(size = 12, face = "bold"))
g <- g + theme(axis.title.y = element_text(size = 12, face = "bold"))

ggsave("terminals-tile.png", width = 9, height = 6)




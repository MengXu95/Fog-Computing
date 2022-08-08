library(ggplot2)

working_dir <- "/Users/mengxu/Desktop/newExperimentsForTSCfirstRevision/"
setwd(working_dir)

sprintf("------------------------Start------------------------------")
algos <- c("Medium")
devices <- c("1", "2", "3", "4", "5", "6", "7", "8", "9", "10")
algo.names <- c("Medium")
# algos <- c("small", "middle","large")
# devices <- c("1", "2", "3")
# algo.names <- c("small", "middle","large")
#scenarios.name <- c("Nsmall1MTGP", "Nsmall2MTGP","Nsmall3MTGP","Nsmall4MTGP",
#                    "Nmiddle1MTGP", "Nmiddle2MTGP","Nmiddle3MTGP","Nmiddle4MTGP",
#                    "Nlarge1MTGP", "Nlarge2MTGP","Nlarge3MTGP","Nlarge4MTGP")

result.df <- data.frame(Scenario = character(),
                        Algo = character(),
                        Device = integer(),
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


#for (s in 1:length(scenarios.name)) {
#  scenario.name <- scenarios.name[s]
#  #scenario <- paste0(objectives[s], "-", utils[s], "-", ddfactors[s])
#  testfile <- paste0("result.csv")

for (a in 1:length(algos)) {
  algo <- algos[a]
  for (m in 1:length(devices)){
    scenario.name <- paste0("warm",algo,m,"MTGP")
    #scenario.name <- scenarios.name[(a-1)*4+m]
    #scenario <- paste0(objectives[s], "-", utils[s], "-", ddfactors[s])
    testfile <- paste0("result.csv")
    df <- read.csv(paste0(algo, "/", scenario.name, "/results/makespan/test/", testfile), header = TRUE)
    result.df <- rbind(result.df,
                       cbind(Algo = rep(algo.names[a], nrow(df)),
                             Device = rep(devices[m], nrow(df)),
                             df))
  }
}
#}

runs <- unique(result.df$Run)
generations <- max(result.df$Generation)

#for (s in 1:length(scenarios.name)) {
#  scenario.name <- scenarios.name[s]

finalTestFit.df <- data.frame(Algo = character(),
                              Device = integer(),
                              Run = integer(),
                              TestFitness = double())

for (a in 1:length(algos)) {
  algo <- algo.names[a]
  for (m in 1:length(devices)){
    device <- devices[m]

    rows <- subset(result.df, Algo == algo & Device == device & Generation == generations)

    finalTestFit.df <- rbind(finalTestFit.df, data.frame(Algo = rep(algo, nrow(rows)),
                                                         Device = rep(device, nrow(rows)),
                                                         Run = rows$Run,
                                                         TestFitness = rows$TestFitness))
  }
}

testline.df <- data.frame(Algo = character(),
                          Device = integer(),
                          MeanTestFitness = double())

for (m in 1:length(devices)){
  device <- devices[m]

    rowsall <- subset(finalTestFit.df, Device == device)

    testline.df <- rbind(testline.df, data.frame(Algo = rowsall$Algo,
                                                 Device = device,
                                                 MeanTestFitness = mean(rowsall$TestFitness)))
}

finalTestFit.df$Algo <- factor(finalTestFit.df$Algo, levels = algos)
finalTestFit.df$Device <- factor(finalTestFit.df$Device, levels = devices)#2020.10.20 order the appearrence of subplots
# g <- ggplot(finalTestFit.df, aes(Device, TestFitness, colour = factor(Device), shape = factor(Device))) + geom_boxplot()

g <- ggplot() +
  geom_boxplot(data = finalTestFit.df, mapping = aes(Device, TestFitness, colour = factor(Device))) +
  geom_line(data = testline.df, mapping =  aes(x = Device, y = MeanTestFitness, group = Algo)) +
  geom_point(data = testline.df, mapping =  aes(x = Device, y = MeanTestFitness, group = Algo))
# + geom_line(finalTestFit.df, aes(Device, mean(TestFitness)))
#g <- g + facet_wrap(~ Scenario, nrow = 2, scales = "free")
#g <- g + facet_wrap(~ Scenario, ncol = 3, scales = "free")
g <- g + facet_wrap(~ Algo, ncol = 1, scales = "free")

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

ggsave("testfit-box-middle.pdf", width = 9, height = 4.2)
#ggsave("testfit-curve-noStd.pdf", width = 10, height = 5)

# table showing



for (s in 1:length(algos)) {
  rows1 <- subset(finalTestFit.df, Algo == algos[s] & Device == devices[1])
  rows2 <- subset(finalTestFit.df, Algo == algos[s] & Device == devices[2])
  rows3 <- subset(finalTestFit.df, Algo == algos[s] & Device == devices[3])
  rows4 <- subset(finalTestFit.df, Algo == algos[s] & Device == devices[4])

  cat(sprintf("%s
  & %.2f - %.2f(%.2f) & %.2f - %.2f(%.2f) & %.2f - %.2f(%.2f) & %.2f - %.2f(%.2f)\\\\\n",
              algos[s],
              min(rows1$TestFitness), mean(rows1$TestFitness), sd(rows1$TestFitness),
              min(rows2$TestFitness), mean(rows2$TestFitness), sd(rows2$TestFitness),
              min(rows3$TestFitness), mean(rows3$TestFitness), sd(rows3$TestFitness),
              min(rows4$TestFitness), mean(rows4$TestFitness), sd(rows4$TestFitness)))
}


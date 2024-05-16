library(ggplot2)

working_dir <- "/Users/mengxu/IdeaProjects/Fog-Computing/src/mengxu"
setwd(working_dir)

sprintf("------------------------Start------------------------------")

result.df <- data.frame(jobID = integer(),
                        taskID = integer(),
                        processorType = character(),
                        processor = integer(),
                        startTime = double(),
                        completeTime = double()
)

testfile <- paste0("scheduling.csv")
result.df <- read.csv(paste0(testfile), header = TRUE)

# par(pin = c(10,5))
pdf('scheduling.pdf', width=12,height=3)
plot(1:5,1:5,xlim = c(-70,1180), ylim = c(-0.5,5.5), type = "n", cex.axis = 1, font.axis = 1, yaxt = "n", ann = FALSE)
par(mai=c(0,1.7,0,0)) #下，左，上，右留白
# plot("Time","Processor",xlim = c(0,950), ylim = c(-0.5,5.5), type = "n", cex.axis = 2, font.axis = 1)

# plot("time", "processor", xlim = c(0,950), ylim = c(-0.5,5.5), type = "n")


for(pross in 1:6){
  pross <- pross - 1
  rect(xleft = 0, ybottom = pross-0.32, xright = 1200, ytop = pross+0.32, lwd = 1, lty = 2)
}
color <- c("#FFD700", "#E3E3E3")
c = color[1]

for(pos in 1:nrow(result.df)){
  row <- result.df[pos,]
  txt <- paste0(row$jobID, ".", row$taskID);

  if(row$jobID == 0){
    c = color[1]
  }
  else{
    c = color[2]
  }
  rect(xleft = row$startTime, ybottom = row$processorID-0.32, xright = row$completeTime, ytop = row$processorID+0.32, col = c, lwd = 1)
  text((row$startTime+row$completeTime)/2, row$processorID+0.01, txt, cex = 0.6)
}

axis(side = 2, at = c(0, 1, 2, 3, 4, 5), labels = paste(c("Device 0","Device 1", "Edge 0", "Edge 1", "Cloud 0","Cloud 1")), las =2, cex.axis = 0.8, xpd = TRUE)
title(xlab= 'Time', cex.lab = 2)
# axis(side = 2, at = c(0, 1, 2, 3, 4, 5), labels = c("Device 0","Device 1", "Fog 0", "Fog 1", "Cloud 0","Cloud 1"))
# scale_y_continuous(limits=c(-0.5,5.5),breaks = c(0, 1, 2, 3, 4, 5),labels=c("Device 0", "Device 1", "Fog 0", "Fog 1", "Cloud 0","Cloud 1"))


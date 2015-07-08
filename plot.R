library(ggplot2)

#setwd("~/project_dir")

df <- read.csv("solutions.txt", header = FALSE, stringsAsFactors = FALSE)
head(df)
colnames(df) <- c("board", "uniques", "non_uniques", "completions")

# total number of completions for left sides
nr_totals <- sum(df$completions)
nr_totals # = 397279200

# total number of completions with nonunique solution
nr_nonuniques <- sum(df$non_uniques)
nr_nonuniques # = 114222082

# total number of completions with a unique solution?
nr_uniques <- sum(df$uniques)
nr_uniques # = 0

# total number of completions with no solution
nr_no_sols <- nr_totals - nr_nonuniques - nr_uniques
nr_no_sols  # = 283057118

# Number of completions is 2400 or 3600. Make it into a factor variable 
unique(df$completions)
df$completions <- as.factor(df$completions)

# Sort boards lexicographically. Add row index
df <- df[order(df$completions, df$board), ]
df$index <- 1:nrow(df)

ggplot(df, aes(x=index, y=non_uniques, color=completions)) + 
    geom_point(alpha = 0.8, size=1.5) 

#hist(values$non_uniques, breaks=50)

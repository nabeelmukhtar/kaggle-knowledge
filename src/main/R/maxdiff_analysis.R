# http://surveyanalysis.org/wiki/Max-Diff_Analysis_Case_Study_Using_R
# http://en.wikipedia.org/wiki/MaxDiff

library(BiasedUrn)
library(foreign)
library(mlogit)

itData = read.spss("http://surveyanalysis.org/images/0/06/ItMaxDiff.sav", use.value.labels = FALSE, to.data.frame = TRUE)
# Selecting the variables containing the max-diff data
z = itData[,-1:-5]
# stacking the data (one set per row)
alternativeNames = c("Apple","Microsoft","IBM","Google","Intel","HewlettPackard","Sony","Dell","Yahoo","Nokia")
nAlternatives = length(alternativeNames)
nBlocks = ncol(z) / nAlternatives
nAltsPerSet = 5
n = nrow(z)
nObservations = n * nBlocks 
itMaxDiffData = matrix(as.numeric(t(z)),ncol = nAlternatives,byrow = TRUE, dimnames = list(1:nObservations, alternativeNames))

counts = apply(itMaxDiffData, 2, mean, na.rm = TRUE)
ranks = nAlternatives + 1 - rank(counts)
cbind(Counts = counts, Ranks = ranks)

id = rep(1:n,rep(nBlocks,n))
individualCounts = aggregate(itMaxDiffData,list(id),mean, na.rm = TRUE)[,-1]
round(individualCounts[1:10,],1) #show at data for first 10 respondents

set.seed(0) # setting the random number seed to enhance comparability
indidualCountsNoTies = individualCounts + matrix(runif(n * nAlternatives)/100000, n) #adding random numbers to break ties
ranks = nAlternatives + 1 - apply(indidualCountsNoTies,1,rank) #ranks
rankProportions = t(apply(ranks,1,table) / n * 100)
round(rankProportions,1)

rankCumProportions = t(apply(rankProportions,1,cumsum))
round(rankCumProportions,1)

aveRank = rankProportions %*% (1:10)/100
cbind(aveRank, Rank = rank(aveRank))


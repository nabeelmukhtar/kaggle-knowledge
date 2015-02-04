# http://en.wikipedia.org/wiki/Conjoint_analysis_(marketing)

library(conjoint)
data(herbata)
simutil<-caBTL(hsimp,hpref,hprof)
print("Percentage participation of profiles: ", quote=FALSE)
print(simutil)
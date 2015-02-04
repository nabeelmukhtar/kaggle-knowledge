library("mlogit")
data("Fishing", package = "mlogit")
Fish <- mlogit.data(Fishing, shape = "wide", varying = 2:9, choice = "mode")

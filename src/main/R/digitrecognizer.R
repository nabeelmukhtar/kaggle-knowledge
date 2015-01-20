data.dir    <- '~/Datasets/kaggle/digit-recognizer/'
train.file <- paste0(data.dir, 'train.csv')
test.file  <- paste0(data.dir, 'test.csv')

knn_benchnark <- function() {
	library(FNN)
	
	train <- read.csv(train.file, header=TRUE)
	test <- read.csv(test.file, header=TRUE)
	
	labels <- train[,1]
	train <- train[,-1]
	
	results <- (0:9)[knn(train, test, labels, k = 10, algorithm="cover_tree")]
	
	write(results, file="knn_benchmark.csv", ncolumns=1) 	
}

rf_benchmark <- function() {
# makes the random forest submission
	
	library(randomForest)
	
	train <- read.csv(train.file, header=TRUE)
	test <- read.csv(test.file, header=TRUE)
	
	labels <- as.factor(train[,1])
	train <- train[,-1]
	
	rf <- randomForest(train, labels, xtest=test, ntree=1000)
	predictions <- levels(labels)[rf$test$predicted]
	
	write(predictions, file="rf_benchmark.csv", ncolumns=1) 
}

knn_benchnark()
rf_benchmark()
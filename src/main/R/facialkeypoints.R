library(caret)
## set seed
set.seed(32343)
wle_data <- read.csv("data/pml-training.csv", na.strings = c("", "NA", "#DIV/0!"))

predictors <- colnames(wle_data)
predictors <- predictors[colSums(is.na(wle_data)) == 0]
predictors <- predictors[-(1:7)]
# nsv <- nearZeroVar(wle_data[, predictors])
# predictors <- predictors[-nsv]
classes <- unique(wle_data$classe)
class_colors <- 1 + as.integer(classes)
fitControl <- trainControl(method="repeatedcv",
		number=5,
		repeats=1,
		verboseIter=FALSE)

inBuild <- createDataPartition(y=wle_data$classe,
		p=0.7, list=FALSE)
validation <- wle_data[-inBuild, predictors]
buildData <- wle_data[inBuild, predictors]
inTrain <- createDataPartition(y=buildData$classe,
		p=0.7, list=FALSE)
training <- buildData[inTrain, ]
testing <- buildData[-inTrain, ]
rm(buildData, wle_data, inBuild, inTrain)
clean <- gc(FALSE)
rm(clean)

modeltree <- train(classe ~., data=training, method="rpart", trControl=fitControl)
library(rattle)
fancyRpartPlot(modeltree$finalModel)
predicttree <- predict(modeltree, newdata=testing)
cmtree <- confusionMatrix(predicttree, testing$classe)
plot(cmtree$table, col = class_colors, main = paste("Decision Tree Confusion Matrix: Accuracy=", round(cmtree$overall['Accuracy'], 2)))
kable(cmtree$byClass, digits = 2, caption = "Per Class Metrics")

modellda <- train(classe ~., data=training, method="lda", trControl=fitControl)
predictlda <- predict(modellda, newdata=testing)
cmlda <- confusionMatrix(predictlda, testing$classe)
plot(cmlda$table, col = class_colors, main = paste("LDA Confusion Matrix: Accuracy=", round(cmlda$overall['Accuracy'], 2)))
kable(cmlda$byClass, digits = 2, caption = "Per Class Metrics")

modelgbm <- train(classe ~., data=training, method="gbm", trControl=fitControl, verbose = FALSE)
predictgbm <- predict(modelgbm, newdata=testing)
cmgbm <- confusionMatrix(predictgbm, testing$classe)
plot(cmgbm$table, col = class_colors, main = paste("GBM Confusion Matrix: Accuracy=", round(cmgbm$overall['Accuracy'], 2)))
kable(cmgbm$byClass, digits = 2, caption = "Per Class Metrics")

predicttesting <- data.frame(predicttree, predictgbm, predictlda, classe = testing$classe)
modelensemble <- train(classe ~ ., data = predicttesting, method = "rf")
predictvalidation <- data.frame(predicttree = predict(modeltree, newdata=validation),
		predictgbm = predict(modelgbm, newdata=validation),
		predictlda = predict(modellda, newdata=validation),
		classe = validation$classe)
predictensemble <- predict(modelensemble, predictvalidation)
cmensemble <- confusionMatrix(predictensemble, validation$classe)
plot(cmensemble$table, col = class_colors, main = paste("Ensemble Confusion Matrix: Accuracy=", round(cmensemble$overall['Accuracy'], 2)))
kable(cmensemble$byClass, digits = 2, caption = "Per Class Metrics")

modelrf <- train(classe ~ roll_belt + pitch_forearm + magnet_dumbbell_z + yaw_belt + magnet_dumbbell_y + roll_forearm + pitch_belt, data=training, method="rf", ntree = 100)
predictrf <- predict(modelrf, newdata=testing)
cmrf <- confusionMatrix(predictrf, testing$classe)
plot(cmrf$table, col = class_colors, main = paste("Random Forest Confusion Matrix: Accuracy=", round(cmrf$overall['Accuracy'], 2)))
kable(cmrf$byClass, digits = 2, caption = "Per Class Metrics")


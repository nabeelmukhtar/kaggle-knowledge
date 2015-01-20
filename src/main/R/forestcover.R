library(caret)
## set seed
set.seed(32343)
data <- read.csv(unz("~/Datasets/kaggle/forest-cover-type-prediction/train.csv.zip", "train.csv"))
data$Cover_Type <- as.factor(data$Cover_Type)

predictors <- colnames(data)
# predictors <- predictors[colSums(is.na(data)) == 0]
predictors <- predictors[-1]
nsv <- nearZeroVar(data[, predictors])
predictors <- predictors[-nsv]
Cover_Types <- unique(data$Cover_Type)
class_colors <- 1 + as.integer(Cover_Types)
inTrain <- createDataPartition(y=data$Cover_Type,
                               p=0.7, list=FALSE)
training <- data[inTrain, predictors]
testing <- data[-inTrain, predictors]

modelrf <- train(Cover_Type ~ ., data=training, method="rf", ntree = 100)
predictrf <- predict(modelrf, newdata=testing)
cmrf <- confusionMatrix(predictrf, testing$Cover_Type)
plot(cmrf$table, col = class_colors, main = paste("Random Forest Confusion Matrix: Accuracy=", round(cmrf$overall['Accuracy'], 2)))

modelcf <- train(Cover_Type ~ ., data=training, method="cforest", controls=cforest_unbiased(ntree=100, mtry=3))
predictcf <- predict(modelcf, newdata=testing)
cmcf <- confusionMatrix(predictcf, testing$Cover_Type)
plot(cmcf$table, col = class_colors, main = paste("Conditional Random Forest Confusion Matrix: Accuracy=", round(cmrf$overall['Accuracy'], 2)))

# submission.
training <- data[, predictors]
modelrf <- train(Cover_Type ~ ., data=training, method="rf", ntree = 100)

test <- read.csv(unz("~/Datasets/kaggle/forest-cover-type-prediction/test.csv.zip", "test.csv"))
testing <- test[, predictors]

predictrf <- predict(modelrf, newdata=testing)
write.csv(x = data.frame(Id = test$Id, Cover_Type = as.integer(predictrf)), file = "forestcover_submission_01.csv", row.names = FALSE)

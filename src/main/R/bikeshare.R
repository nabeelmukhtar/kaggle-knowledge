library(caret)
library(lubridate)
## set seed
set.seed(32343)
data <- read.csv("~/Datasets/kaggle/bike-sharing-demand/train.csv", stringsAsFactors = FALSE)
data$wday <- wday(data$datetime)
data$hour <- hour(data$datetime)

predictors <- colnames(data)
# predictors <- predictors[colSums(is.na(data)) == 0]
predictors <- predictors[c(-1, -10, -11)]
nsv <- nearZeroVar(data[, predictors])
predictors <- predictors[-nsv]


inTrain <- createDataPartition(y=data$count,
                               p=0.7, list=FALSE)
training <- data[inTrain, predictors]
testing <- data[-inTrain, predictors]

modellm <- train(count ~ ., data=training, method="lm")
predictlm <- predict(modellm, newdata=testing)
rmselm <- sqrt(mean((predictlm - testing$count) ^ 2, na.rm = TRUE))

modelrf <- train(count ~ ., data=training, method="rf", ntree = 100)
predictrf <- predict(modelrf, newdata=testing)
rmserf <- sqrt(mean((predictrf - testing$count) ^ 2, na.rm = TRUE))

# submission no. 1.
training <- data[, predictors]
modellm <- train(count ~ ., data=training, method="lm")

test <- read.csv("~/Datasets/kaggle/bike-sharing-demand/test.csv", stringsAsFactors = FALSE)
test$wday <- wday(test$datetime)
test$hour <- hour(test$datetime)
testing <- test[, colnames(test) %in% predictors]

predictlm <- predict(modellm, newdata=testing)

# remove negative
predictlm <- pmax(predictlm, 0)

write.csv(x = data.frame(datetime = test$datetime, count = as.integer(round(predictlm))), file = "bikeshare_submission_01.csv", row.names = FALSE)


# submission no. 2.
training <- data[, predictors]
modelrf <- train(count ~ ., data=training, method="rf", ntree = 100)

test <- read.csv("~/Datasets/kaggle/bike-sharing-demand/test.csv", stringsAsFactors = FALSE)
test$wday <- wday(test$datetime)
test$hour <- hour(test$datetime)
testing <- test[, colnames(test) %in% predictors]

predictrf <- predict(modelrf, newdata=testing)

# remove negative
predictrf <- pmax(predictrf, 0)

write.csv(x = data.frame(datetime = test$datetime, count = as.integer(round(predictrf))), file = "bikeshare_submission_02.csv", row.names = FALSE)

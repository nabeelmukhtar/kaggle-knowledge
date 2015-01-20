import pandas as pd
import numpy as np
import zipfile

def pre_process(df):
    df = df[list((df.var() > 0.05)[((df.var() > 0.05) == True)].index)]
    # df = df.dropna()
    
    return df.values

# For .read_csv, always use header=0 when you know row 0 is the header row
train_df = pd.read_csv(zipfile.ZipFile('/home/namukhtar/Datasets/kaggle/forest-cover-type-prediction/train.csv.zip').open('train.csv'), header=0)
# print train_df.head(10)
train_df['Cover_Type'] = train_df['Cover_Type'].astype("category")
train_data = pre_process(train_df)

test_df = pd.read_csv(zipfile.ZipFile('/home/namukhtar/Datasets/kaggle/forest-cover-type-prediction/test.csv.zip').open('test.csv'), header=0)
# print test_df.head(10)
test_data = pre_process(test_df)

# Import the random forest package
from sklearn.ensemble import RandomForestClassifier 

# Create the random forest object which will include all the parameters
# for the fit
forest = RandomForestClassifier(n_estimators = 100)

# Fit the training data to the Survived labels and create the decision trees
forest = forest.fit(train_data[0::,:-1],train_data[0::,-1])

# Take the same decision trees and run it on the test data
output = forest.predict(test_data)

out_df = pd.DataFrame({'Id' : test_data[0::, 0], 'Cover_Type' : output})
out_df["Id"] = out_df["Id"].astype("int")
out_df["Cover_Type"] = out_df["Cover_Type"].astype("int")

out_df.to_csv('forest-randomforest.csv', index=False)

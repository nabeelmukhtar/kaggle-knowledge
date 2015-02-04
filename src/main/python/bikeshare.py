import pandas as pd
import numpy as np
import time

def pre_process(df, train = True):
    if (train):
        df = df.drop(['casual', 'registered', 'holiday'], axis=1)
    else:
         df = df.drop(['holiday'], axis=1)
    df['datetime'] = pd.to_datetime(df['datetime'])
    df.set_index('datetime', inplace=True)
    df['wday'] = df.index.weekday
    df['hour'] = df.index.hour
    
    if (train):
        return df.drop(['count'], axis=1).values, df['count'].values
    else:
        return df.values

# For .read_csv, always use header=0 when you know row 0 is the header row
train_df = pd.read_csv('/home/namukhtar/Datasets/kaggle/bike-sharing-demand/train.csv', header=0)
# print train_df.head(10)
train_data, label = pre_process(train_df)

test_df = pd.read_csv('/home/namukhtar/Datasets/kaggle/bike-sharing-demand/test.csv', header=0)
# print test_df.head(10)
test_data = pre_process(test_df, train = False)

# Import the random forest package
from sklearn import linear_model

regr = linear_model.LinearRegression()

regr = regr.fit(train_data, label)

# Take the same decision trees and run it on the test data
output = regr.predict(test_data)

out_df = pd.DataFrame({'datetime' : test_df['datetime']})
out_df["count"] = np.round(np.maximum(output,0), decimals=0).astype('int')

out_df.to_csv('bikeshare-linear-regression.csv', index=False)

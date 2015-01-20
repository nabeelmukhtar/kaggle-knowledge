import pandas as pd
import numpy as np

def pre_process(df):
    
    df['Gender'] = df['Sex'].map({'female': 0, 'male': 1}).astype(int)
    
    median_ages = np.zeros((2, 3))
    
    for i in range(0, 2):
        for j in range(0, 3):
            median_ages[i, j] = df[(df['Gender'] == i) & (df['Pclass'] == j + 1)]['Age'].dropna().median()
                                  
    df['AgeFill'] = df['Age']
    
    for i in range(0, 2):
        for j in range(0, 3):
            df.loc[ (df.Age.isnull()) & (df.Gender == i) & (df.Pclass == j + 1), 'AgeFill'] = median_ages[i, j]
                    
    df['AgeIsNull'] = pd.isnull(df.Age).astype(int)
    
    df['FamilySize'] = df['SibSp'] + df['Parch']
    
    df['Age*Class'] = df.AgeFill * df.Pclass
    
    df = df.drop(['Name', 'Sex', 'Ticket', 'Cabin', 'Embarked'], axis=1) 
    
    df = df.drop(['Age'], axis=1)
    
    df.loc[df.Fare.isnull(), 'Fare'] = df['Fare'].dropna().median()
    
    return df.values

# For .read_csv, always use header=0 when you know row 0 is the header row
train_df = pd.read_csv('/home/namukhtar/Datasets/kaggle/titanic/train.csv', header=0)
# print train_df.head(10)
train_data = pre_process(train_df)

test_df = pd.read_csv('/home/namukhtar/Datasets/kaggle/titanic/test.csv', header=0)
# print test_df.head(10)
test_data = pre_process(test_df)

# Import the random forest package
from sklearn.ensemble import RandomForestClassifier 

# Create the random forest object which will include all the parameters
# for the fit
forest = RandomForestClassifier(n_estimators=100)

# Fit the training data to the Survived labels and create the decision trees
forest = forest.fit(train_data[0::, 2::], train_data[0::, 1])

# Take the same decision trees and run it on the test data
output = forest.predict(test_data[0::, 1::])

out_df = pd.DataFrame({'PassengerId' : test_data[0::, 0], 'Survived' : output})
out_df["PassengerId"] = out_df["PassengerId"].astype("int")
out_df["Survived"] = out_df["Survived"].astype("int")

out_df.to_csv('titanic-randomforest.csv', index=False)


from sklearn import svm

svc = svm.SVC(kernel='linear')

svc = svc.fit(train_data[0::, 2::], train_data[0::, 1])

# Take the same decision trees and run it on the test data
output = svc.predict(test_data[0::, 1::])

out_df = pd.DataFrame({'PassengerId' : test_data[0::, 0], 'Survived' : output})
out_df["PassengerId"] = out_df["PassengerId"].astype("int")
out_df["Survived"] = out_df["Survived"].astype("int")

out_df.to_csv('titanic-svm.csv', index=False)

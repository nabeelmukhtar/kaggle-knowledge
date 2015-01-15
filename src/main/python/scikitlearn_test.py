__author__ = 'namukhtar'

"""
import IPython
import sklearn as sk
import numpy as np
import matplotlib
import nltk
import matplotlib.pyplot as plt


from sklearn.datasets import fetch_olivetti_faces

faces = fetch_olivetti_faces()

print faces.DESCR

print faces.keys()
print faces.images.shape
print faces.data.shape
print faces.target.shape

print np.max(faces.data)
print np.min(faces.data)
print np.mean(faces.data)

def print_faces(images, target, top_n):
    # set up the figure size in inches
    fig = plt.figure(figsize=(12, 12))
    fig.subplots_adjust(left=0, right=1, bottom=0, top=1, hspace=0.05, wspace=0.05)
    for i in range(top_n):
        # plot the images in a matrix of 20x20
        p = fig.add_subplot(20, 20, i + 1, xticks=[], yticks=[])
        p.imshow(images[i], cmap=plt.cm.bone)
        
        # label the image with the target value
        p.text(0, 14, str(target[i]))
        p.text(0, 60, str(i))
        
print_faces(faces.images, faces.target, 20)

from sklearn.svm import SVC

svc_1 = SVC(kernel='linear')
print svc_1

from sklearn.cross_validation import train_test_split

X_train, X_test, y_train, y_test = train_test_split(
        faces.data, faces.target, test_size=0.25, random_state=0)

from sklearn.cross_validation import cross_val_score, KFold
from scipy.stats import sem

def evaluate_cross_validation(clf, X, y, K):
    # create a k-fold croos validation iterator
    cv = KFold(len(y), K, shuffle=True, random_state=0)
    # by default the score used is the one returned by score method of the estimator (accuracy)
    scores = cross_val_score(clf, X, y, cv=cv)
    print scores
    print ("Mean score: {0:.3f} (+/-{1:.3f})").format(
        np.mean(scores), sem(scores))
    
evaluate_cross_validation(svc_1, X_train, y_train, 5)

# Pipelining

import pylab as pl

from sklearn import linear_model, decomposition, datasets

logistic = linear_model.LogisticRegression()

pca = decomposition.PCA()
from sklearn.pipeline import Pipeline
pipe = Pipeline(steps=[('pca', pca), ('logistic', logistic)])

digits = datasets.load_digits()
X_digits = digits.data
y_digits = digits.target

###############################################################################
# Plot the PCA spectrum
pca.fit(X_digits)

pl.figure(1, figsize=(4, 3))
pl.clf()
pl.axes([.2, .2, .7, .7])
pl.plot(pca.explained_variance_, linewidth=2)
pl.axis('tight')
pl.xlabel('n_components')
pl.ylabel('explained_variance_')

###############################################################################
# Prediction

from sklearn.grid_search import GridSearchCV

n_components = [20, 40, 64]
Cs = np.logspace(-4, 4, 3)

#Parameters of pipelines can be set using ‘__’ separated parameter names:

estimator = GridSearchCV(pipe,
                         dict(pca__n_components=n_components,
                              logistic__C=Cs))
estimator.fit(X_digits, y_digits)

pl.axvline(estimator.best_estimator_.named_steps['pca'].n_components,
           linestyle=':', label='n_components chosen')
pl.legend(prop=dict(size=12))

"""

from sklearn import datasets
iris = datasets.load_iris()
digits = datasets.load_digits()

print digits.data

from sklearn import svm
clf = svm.SVC(gamma=0.001, C=100.)

clf.fit(digits.data[:-1], digits.target[:-1])

clf.predict(digits.data[-1])

import pickle
s = pickle.dumps(clf)
clf2 = pickle.loads(s)
clf2.predict(X[0])

from sklearn.externals import joblib
joblib.dump(clf, 'filename.pkl')
import skimage.morphology
import skimage.filter
import skimage.feature
import numpy as np
import timeit
import mahotas

from os import path
luispedro_image = path.join(
            path.dirname(mahotas.__file__),
            'demos',
            'data',
            'luispedro.jpg')
f = mahotas.imread(luispedro_image, as_grey=True)
markers = np.zeros_like(f)
markers[100, 100] = 1
markers[200, 200] = 2
f = f.astype(int)
markers = markers.astype(int)
otsu = mahotas.otsu(f.astype(np.uint8))
fbin = f > otsu
fbin8 = fbin.astype(np.uint8)
Bc = np.eye(3)
Bc = Bc.astype(bool)
Bc8 = Bc.astype(np.uint8)

pre = '''
import skimage.filter
import skimage.morphology
import skimage.feature
import numpy as np
import mahotas
import pymorph
import timethings
f = timethings.f
fbin = timethings.fbin
fbin8 = timethings.fbin8
f64 = f.astype(np.float64)
Bc = timethings.Bc
Bc8 = timethings.Bc8
markers = timethings.markers
'''

def t(s):
    return timeit.timeit(s, setup=pre, number=24)

tests = [
    ('erode', [
        'mahotas.erode(fbin, Bc)',
        'pymorph.erode(fbin, Bc)',
        'skimage.morphology.opening(fbin8, Bc8)',
        ]),
    ('dilate', [
        'mahotas.dilate(fbin, Bc)',
        'pymorph.dilate(fbin, Bc)',
        'skimage.morphology.dilation(fbin8, Bc8)',
        ]),
    ('open', [
        'mahotas.open(fbin, Bc)',
        'pymorph.open(fbin, Bc)',
        'skimage.morphology.opening(fbin8, Bc8)',
        ]),
    ('center mass', [
        'mahotas.center_of_mass(f)',
        None,
        None,
        ]),
    ('sobel', [
        'mahotas.sobel(f)',
        None,
        'skimage.filter.sobel(f64)',
        ]),
    ('cwatershed', [
        'mahotas.cwatershed(f, markers)',
        'pymorph.cwatershed(f, markers)',
        'skimage.morphology.watershed(f, markers)',
        ]),
    ('daubechies', [
        'mahotas.daubechies(f, "D4")',
        None,
        None,
        ]),
    ('haralick', [
        'mahotas.features.haralick(f)',
        None,
        'skimage.feature.greycoprops(skimage.feature.greycomatrix(f, [1], [0]))',
        ]),
]
if __name__ == '__main__':
    base = t('np.max(f)')
    for name, statements in tests:
        print r'%-12s&' % name,
        for st in statements:
            if st is None:
                print '      NA &',
            else:
                time = '%.2f' % (t(st) / base)
                print '%8s &' % time,
        print r'\\'



# import the necessary packages
from sklearn.cross_validation import train_test_split
from sklearn.metrics import classification_report
from sklearn import datasets
from nolearn.dbn import DBN
import numpy as np
import cv2

# grab the MNIST dataset (if this is the first time you are running
# this script, this make take a minute -- the 55mb MNIST digit dataset
# will be downloaded)
print "[X] downloading data..."
dataset = datasets.fetch_mldata("MNIST Original")

# scale the data to the range [0, 1] and then construct the training
# and testing splits
(trainX, testX, trainY, testY) = train_test_split(
    dataset.data / 255.0, dataset.target.astype("int0"), test_size = 0.33)

# train the Deep Belief Network with 784 input units (the flattened,
# 28x28 grayscale image), 300 hidden units, 10 output units (one for
# each possible output classification, which are the digits 1-10)
dbn = DBN(
    [trainX.shape[1], 300, 10],
    learn_rates = 0.3,
    learn_rate_decays = 0.9,
    epochs = 10,
    verbose = 1)
dbn.fit(trainX, trainY)

# compute the predictions for the test data and show a classification
# report
preds = dbn.predict(testX)
print classification_report(testY, preds)

# randomly select a few of the test instances
for i in np.random.choice(np.arange(0, len(testY)), size = (10,)):
    # classify the digit
    pred = dbn.predict(np.atleast_2d(testX[i]))

    # reshape the feature vector to be a 28x28 pixel image, then change
    # the data type to be an unsigned 8-bit integer
    image = (testX[i] * 255).reshape((28, 28)).astype("uint8")

    # show the image and prediction
    print "Actual digit is {0}, predicted {1}".format(testY[i], pred[0])
    # cv2.imshow("Digit", image)
    # cv2.waitKey(0)
__author__ = 'namukhtar'

import IPython
import nltk
from nltk.book import *

# text1
# text2

# text1.concordance("monstrous")

# text1.similar("monstrous")
# text2.similar("monstrous")

# text2.common_contexts(["monstrous", "very"])
# text4.dispersion_plot(["citizens", "democracy", "freedom", "duties", "America"])

# text3.generate()

from nltk.corpus import inaugural

inaugural.fileids()
cfd = nltk.ConditionalFreqDist(
                               (target, fileid[:4])
                               for fileid in inaugural.fileids()
                               for w in inaugural.words(fileid)
                               for target in ['america', 'citizen']
                               if w.lower().startswith(target))

cfd.plot()


from nltk.stem.porter import PorterStemmer

porter_stemmer = PorterStemmer()
porter_stemmer.stem('maximum')
porter_stemmer.stem('presumably')

from nltk.stem import SnowballStemmer
snowball_stemmer = SnowballStemmer('english')
snowball_stemmer.stem('maximum')

from nltk.stem import WordNetLemmatizer
wordnet_lemmatizer = WordNetLemmatizer()
wordnet_lemmatizer.lemmatize('dogs')

from nltk.tokenize import sent_tokenize
sent_tokenize("Hello SF Python. This is NLTK. Its a good library.")

from nltk.tokenize import word_tokenize
word_tokenize('This is NLTK.')

from nltk.tokenize import wordpunct_tokenize
wordpunct_tokenize("What's up?")

words = word_tokenize("And now for something completely different")
from nltk.tag import pos_tag
pos_tag(words)

from nltk.chunk import ne_chunk
ne_chunk(pos_tag(word_tokenize('My name is Jacob Perkins.')))

ne_chunk(pos_tag(word_tokenize('San Francisco is foggy.')))

def bag_of_words(words):
    return dict([(word, True) for word in words])

feats = bag_of_words(word_tokenize("great movie"))
import nltk.data

classifier = nltk.data.load('classifiers/movie_reviews_NaiveBayes.pickle')
classifier.classify(feats)

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

__author__ = 'namukhtar'

import pandas as pd
import numpy as np
import matplotlib.pyplot as plt

s = pd.Series([1,3,5,np.nan,6,8])
s

dates = pd.date_range('20130101',periods=6)
dates

df = pd.DataFrame(np.random.randn(6,4),index=dates,columns=list('ABCD'))
print df

df2 = pd.DataFrame({ 'A' : 1.,
                     'B' : pd.Timestamp('20130102'),
                     'C' : pd.Series(1,index=list(range(4)),dtype='float32'),
                     'D' : np.array([3] * 4,dtype='int32'),
                     'E' : pd.Categorical(["test","train","test","train"]),
                     'F' : 'foo' })
print df2

print df.head()
print df.tail(3)

print df.describe()
print df.T

import pandas as pd
import numpy as np
df = pd.DataFrame({'int_col' : [1,2,6,8,-1], 'float_col' : [0.1, 0.2,0.2,10.1,None], 'str_col' : ['a','b',None,'c','a']})

df

# Selecting a subset of columns

df.ix[:,['float_col','int_col']]

df[['float_col','int_col']]

# Conditional indexing

df[df['float_col'] > 0.15]

df[df['float_col'] == 0.1]

df[(df['float_col'] > 0.1) & (df['int_col']>2)]

df[~(df['float_col'] > 0.1)]

# Renaming columns

df2 = df.rename(columns={'int_col' : 'some_other_name'})

df2

df2.rename(columns={'some_other_name' : 'int_col'}, inplace = True)

# Drop missing values
df2.dropna()

# Fill missing values
df3 = df.copy()
mean = df3['float_col'].mean()

df3
df3['float_col'].fillna(mean)


# Map, Apply
df['str_col'].dropna().map(lambda x : 'map_' + x)

df.ix[:,['int_col','float_col']].apply(np.sqrt)

def some_fn(x):
      if type(x) is str:
        return 'applymap_' + x
      elif x:
        return 100 * x
      else:
        return
    
df.applymap(some_fn)

df = pd.DataFrame(data={"A":[1,2], "B":[1.2,1.3]})

df["C"] = df["A"]+df["B"]
df["D"] = df["A"]*3
df["E"] = np.sqrt(df["A"])

df = pd.DataFrame(data={"A":[1,2], "B":[1.2,1.3], "Z":["a","b"]})
df["F"] = df.Z.str.upper()

df = pd.DataFrame({'int_col' : [1,2,6,8,-1], 'float_col' : [0.1, 0.2,0.2,10.1,None], 'str_col' : ['a','b',None,'c','a']})

grouped = df['float_col'].groupby(df['str_col'])
grouped.mean()

# multiple columns as a function of a single column
df4 = df.copy()

def two_three_strings(x):
    return x*2, x*3

df4['twice'],df4['thrice'] = zip(*df4['int_col'].map(two_three_strings))

# single column as a function of multiple columns

df5 = df.copy()
def sum_two_cols(series):
    return series['int_col'] + series['float_col']
df5['sum_col'] = df5.apply(sum_two_cols,axis=1)


# multiple columns as a function of multiple columns
import math
def int_float_squares(series):
    return pd.Series({'int_sq' : series['int_col']**2, 'flt_sq' : series['float_col']**2})

df.apply(int_float_squares, axis = 1)


# The describe method provides quick stats on all suitable columns.
df.describe()

# The cov method provides the covariance between suitable columns.
df.cov()

# The corr method provides the correlation between suitable columns.
df.corr()


# joins
other = DataFrame({'str_col' : ['a','b'], 'some_val' : [1, 2]})

pd.merge(df,other,on='str_col',how='inner')
pd.merge(df,other,on='str_col',how='outer')
pd.merge(df,other,on='str_col',how='left')
pd.merge(df,other,on='str_col',how='right')

plot_df = pd.DataFrame(np.random.randn(1000,2),columns=['x','y'])
plot_df['y'] = plot_df['y'].map(lambda x : x + 1)
plot_df.plot()

plt.show()


df.values[:,:-1]
df.values[:,:-1].astype('float32')



tuples = list(zip(*[['bar', 'bar', 'baz', 'baz',
                         'foo', 'foo', 'qux', 'qux'],
                        ['one', 'two', 'one', 'two',
                         'one', 'two', 'one', 'two']]))

index = pd.MultiIndex.from_tuples(tuples, names=['first', 'second'])
df = pd.DataFrame(np.random.randn(8, 2), index=index, columns=['A', 'B'])

df2 = df[:4]

stacked = df2.stack()
stacked.unstack()
stacked.unstack(1)
stacked.unstack(0)

df = pd.DataFrame({'A' : ['one', 'one', 'two', 'three'] * 3,
                       'B' : ['A', 'B', 'C'] * 4,
                       'C' : ['foo', 'foo', 'foo', 'bar', 'bar', 'bar'] * 2,
                       'D' : np.random.randn(12),
                       'E' : np.random.randn(12)})

pd.pivot_table(df, values='D', index=['A', 'B'], columns=['C'])


# Time Series
rng = pd.date_range('1/1/2012', periods=100, freq='S')
ts = pd.Series(np.random.randint(0, 500, len(rng)), index=rng)
ts.resample('5Min', how='sum')

# Time zone representation
rng = pd.date_range('3/6/2012 00:00', periods=5, freq='D')
ts = pd.Series(np.random.randn(len(rng)), rng)

ts_utc = ts.tz_localize('UTC')
ts_utc
ts_utc.tz_convert('US/Eastern')

# Categoricals
df = pd.DataFrame({"id":[1,2,3,4,5,6], "raw_grade":['a', 'b', 'b', 'a', 'a', 'e']})

df["grade"] = df["raw_grade"].astype("category")

df["grade"].cat.categories = ["very good", "good", "very bad"]
df.to_excel('foo.xlsx', sheet_name='Sheet1')

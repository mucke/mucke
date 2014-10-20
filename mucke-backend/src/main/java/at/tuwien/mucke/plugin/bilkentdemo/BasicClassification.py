
# coding: utf-8

# In[159]:

import os
import sys
import numpy as np
import cv2
from skimage import feature
from sklearn.svm import LinearSVC


# In[24]:

# Required functions

# Reads all file paths with the given extesion recursively
def get_data_paths(root_path, ext = '*'):
	import os
	import fnmatch
	matches = []
	for root, dirnames, filenames in os.walk(root_path):
	  for filename in fnmatch.filter(filenames, ext):
	      matches.append(os.path.join(root, filename))
	print "There are ",len(matches), " images re found!!"
	return matches


# In[44]:   

# Define Parameters
DATA_ROOT_PATH        = os.path.join(os.path.dirname(os.path.realpath(__file__)),'data/basic/train_images')
TARGET_CONCEPT_FOLDER = 'red'
num_color_bins        = 10
#os.chdir(os.path.dirname(os.path(__file__))[0])
print 'THIS IS THE WORKING DIRECTORY ' +  os.getcwd() + '\n'

# In[102]:

# Read Positive Data and Extract RGB Histogram
X_pos = np.zeros([0, num_color_bins**3+1]) # feature size + label

TARGET_CONCEPT_PATH = os.path.join(DATA_ROOT_PATH, TARGET_CONCEPT_FOLDER)
#TARGET_CONCEPT_PATH = TARGET_CONCEPT_FOLDER
print 'TARGET CONCEPT PATH is '+ TARGET_CONCEPT_PATH
image_paths = get_data_paths(TARGET_CONCEPT_PATH)

for image_path in image_paths:
    img = cv2.imread(image_path)
    hist = cv2.calcHist(img, [0,1,2], None, [num_color_bins,num_color_bins,num_color_bins], [0, 256, 0, 256, 0, 256])
    hist = cv2.normalize(hist)
    hist = hist.flatten()
    X_pos =  np.vstack([X_pos, np.hstack([hist,1])]); # stack the class label at the end of feature vector


# In[104]:

# Read Negative Data and Extract RGB Histogram
X_neg = np.ones([0, num_color_bins**3+1]) # feature size + label

# find other category folders
OTHER_CONCEPT_FOLDERS = os.listdir(DATA_ROOT_PATH)
OTHER_CONCEPT_FOLDERS.remove(TARGET_CONCEPT_FOLDER)
OTHER_CONCEPT_PATHS = map(lambda x: os.path.join(DATA_ROOT_PATH, x),OTHER_CONCEPT_FOLDERS)

# read images
image_paths = []
for OTHER_CONCEPT_PATH in OTHER_CONCEPT_PATHS:
    print OTHER_CONCEPT_PATH
    image_paths = image_paths + get_data_paths(OTHER_CONCEPT_PATH)

for image_path in image_paths:
    img = cv2.imread(image_path)
    hist = cv2.calcHist(img, [0,1,2], None, [num_color_bins,num_color_bins,num_color_bins], [0, 256, 0, 256, 0, 256])
    hist = cv2.normalize(hist)
    hist = hist.flatten()
    X_neg = np.vstack([X_neg,np.hstack([hist,0])])  # stack the class label at the end of feature vector


# In[162]:

# Preprocess Data Matrix
X = np.vstack([X_pos, X_neg])
np.random.shuffle(X)

Y = X[:,-1] # get labels
X = X[:,0:-1] # get instances

# Normalize data to unit ball
ROW_MEANS = X.mean(axis=1)   # You need to keep ROW_MEANS for novel data
ROW_STDS = X.std(axis=0)+10  # You need to keep ROW_STDS for novel data
X = X - ROW_MEANS[:,None] / (ROW_STDS+10)


# In[163]:

SVM = LinearSVC(C=100, fit_intercept=False, dual=False)
SVM.fit(X, Y)
print 'Training accuracy ---> ',SVM.score(X, Y)


RESULT_FILE = os.path.join(os.path.dirname(os.path.realpath(__file__)),'result/basic/output.txt')
f = open(RESULT_FILE,'w')
f.write(str(SVM.score(X, Y)))
f.close()

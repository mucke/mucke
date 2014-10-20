# coding: utf-8
'''
    Author : Eren Golge
    erengolge@gmail.com

    TODO: Seelct random negatives if you have many negative instances
'''
# IMPORT LIBRARIES
import os
import sys
import numpy as np
import cv2
from skimage import feature
from sklearn.svm import LinearSVC, SVC


###########################################
# in-use functions
###########################################

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

def create_ID_file_name_map_file(file_path, images_path):
    image_paths = get_data_paths(images_path)
    with open(file_path,'w') as map_file :
        for count, image_path in enumerate(image_paths):
            temp = image_path.split('/')
            image_name = os.path.join(temp[-2], temp[-1])
            line  = str(count) + ' ' + image_name
            map_file.write(line+'\n')
    return image_paths

# def f_log(write_line, end=0):
#     file_name = 'Full_Classification_LOG.txt'
#     f = open(file_name, 'w')
#     f.write(write_line)
#     f.flush()
#     if end:
#         f.close()


#############################################
## MAIN CALL
#############################################
if __name__ == '__main__':

    # Create a LOG file
    file_name = 'Full_Classification_LOG.txt'
    f_log = open(file_name,'w')

    # Define Parameters
    PROJECT_PATH = os.path.join(os.path.dirname(os.path.realpath(__file__)))
    TRAIN_DATA_ROOT_PATH  = os.path.join(PROJECT_PATH , sys.argv[1]) # THE ROOT PATH of the training images
    TEST_DATA_ROOT_PATH   = os.path.join(PROJECT_PATH , sys.argv[2])  # THE ROOT PATH of the test images
    IMG_ID_NAME_MAP_FILE  = os.path.join(PROJECT_PATH , sys.argv[3])   # File maps file ids to file names including concept folder
    IMG_CONF_FOLDER       = os.path.join(PROJECT_PATH , sys.argv[4])   # Folder that keeps image confidences, below OUTPUT_ROOT_PATH
    num_color_bins        = 20
    CONCEPT_MODEL_DICT    = {}                                                  # It'll keep all the trained models for each concept
    
    # Write parameter logs
    f_log.write('PROJECT PATH :' + PROJECT_PATH + '\n')
    f_log.write('TRAIN_DATA_ROOT_PATH : ' + TRAIN_DATA_ROOT_PATH + '\n')
    f_log.write('TEST_DATA_ROOT_PATH : '+ TEST_DATA_ROOT_PATH + '\n')
    f_log.write('IMG_ID_NAME_MAP_FILE : ' + IMG_ID_NAME_MAP_FILE + '\n')
    f_log.write('RESULTS FOLDER : ' + IMG_CONF_FOLDER + '\n')
    f_log.write('\n')

    f_log.flush()

    # Find all concepts names
    print TRAIN_DATA_ROOT_PATH
    all_concept_paths = os.listdir(TRAIN_DATA_ROOT_PATH)
    ALL_CONCEPTS = map(lambda name: name.split(os.sep)[-1], all_concept_paths)

    # Write log
    f_log.write('ALL THE CONCEPTS : ' + str(ALL_CONCEPTS) + '\n')
    f_log.flush()

    # Create Image ID to Image PATH MAP file
    # image_paths = create_ID_file_name_map_file('train_images_id_name_map.txt',TRAIN_DATA_ROOT_PATH, OUTPUT_ROOT_PATH)
    image_paths = get_data_paths(TRAIN_DATA_ROOT_PATH)
    # Write log
    f_log.write('image_paths: ' + str(image_paths) + '\n')
    f_log.flush()


    ###############################################
    ## TRAINING TIME
    # from image_paths find the image paths related 
    # with the next concept and use these images as 
    # positive as the others are negatives. Keep
    # models in a dictionary with keyed by the concept
    # name that it belongs to.
    ###############################################

    for TARGET_CONCEPT in ALL_CONCEPTS:

        print ' '
        print 'TARGET CONCEPT --------> ', TARGET_CONCEPT
        f_log.write('TARGET CONCEPT --------> ' + TARGET_CONCEPT + '\n')
        f_log.flush()
        
        # Read Positive Data and Extract RGB Histogram
        X_pos = np.zeros([0, num_color_bins**3+1]) # feature size + label

        # TARGET_CONCEPT_PATH = os.path.join(TRAIN_DATA_ROOT_PATH, TARGET_CONCEPT)
        # image_paths = get_data_paths(TARGET_CONCEPT_PATH)
        image_paths_pos = [image_path for image_path in image_paths if image_path.split(os.sep)[-2] == TARGET_CONCEPT]
        print "Number of images for the concept --------> ", str(len(image_paths_pos))
        for image_path in image_paths_pos:
            f_log.write('Next image path ----> ' + image_path + '\n')
            f_log.flush()

            img = cv2.imread(image_path)
            f_log.write('Image is read\n')
            f_log.flush()
            try:
                hist = cv2.calcHist(img, [0,1,2], None, [num_color_bins,num_color_bins,num_color_bins], [0, 256, 0, 256, 0, 256])
                f_log.write('Image feature extracted\n')
                f_log.flush()
            except:
                print 'Image Read Problem in OpenCV. Most probably image file format is not suitable!!'
                f_log.write('Image Read Problem in OpenCV. Most probably image file format is not suitable!! ---> ' + image_path + '\n')
                f_log.flush()
                
            hist = cv2.normalize(hist)
            hist = hist.flatten()
            X_pos = np.vstack([X_pos, np.hstack([hist,1])]); # stack the class label at the end of feature vector

        f_log.write('Positive data is ready\n')
        f_log.flush()

        # Read Negative Data and Extract RGB Histogram
        X_neg = np.ones([0, num_color_bins**3+1]) # feature size + label

        # find other category folders
        OTHER_CONCEPT_FOLDERS = os.listdir(TRAIN_DATA_ROOT_PATH)
        OTHER_CONCEPT_FOLDERS.remove(TARGET_CONCEPT)
        OTHER_CONCEPT_PATHS = map(lambda x: os.path.join(TRAIN_DATA_ROOT_PATH, x),OTHER_CONCEPT_FOLDERS)

        # read images
        image_paths_neg = [image_path for image_path in image_paths if image_path.split(os.sep)[-2] != TARGET_CONCEPT]
        f_log.write('Negative set is read for TARGET_CONCEPT --> '+ TARGET_CONCEPT + '\n')
        f_log.flush()
        for image_path in image_paths_neg:
            f_log.write('Next image path ----> ' + image_path + '\n')
            f_log.flush()

            img = cv2.imread(image_path)

            f_log.write('Image is read\n')
            f_log.flush()
            try:
                hist = cv2.calcHist(img, [0,1,2], None, [num_color_bins,num_color_bins,num_color_bins], [0, 256, 0, 256, 0, 256])
            except:
                print 'Image Read Problem in OpenCV. Most probably image file format is not suitable!!'
                f_log.write('Image Read Problem in OpenCV. Most probably image file format is not suitable!! ---> ' + image_path + '\n')
                f_log.flush()
            hist = cv2.normalize(hist)
            hist = hist.flatten()
            X_neg = np.vstack([X_neg,np.hstack([hist,-1])])  # stack the class label at the end of feature vector

        f_log.write('Negative data is ready\n')
        f_log.flush()

        # Preprocess Data Matrix
        X = np.vstack([X_pos, X_neg])
        np.random.shuffle(X)

        Y = X[:,-1].astype(int) # get labels
        X = X[:,0:-1] # get instances

        print 'Number of labels ---------> ', len(np.unique(Y))
        # Normalize data to unit ball
        ROW_MEANS = X.mean(axis=1)   # You need to keep ROW_MEANS for novel data
        ROW_STDS = X.std(axis=0)+10  # You need to keep ROW_STDS for novel data
        X = X - ROW_MEANS[:,None] / (ROW_STDS+10)

        f_log.write('Model training has been start ------ \n')
        f_log.flush()
        SVM = LinearSVC(C=10000, fit_intercept=True, dual=False, loss='l2', penalty='l1', class_weight='auto', verbose=15)
        #SVM = SVC(C=100, class_weight='auto', kernel='linear', probability=True, verbose= 15)
        SVM.fit(X, Y)
        f_log.write('Model is trained ------ \n')
        f_log.flush()
        print 'Training accuracy ---> ',SVM.score(X, Y)

        CONCEPT_MODEL_DICT[TARGET_CONCEPT] = SVM

    f_log.write('ALL MODELS ARE TRAINED --------> \n')
    f_log.flush()



    ###################################################
    ## PREDICTION TIME
    # read each file from test folder, exract its feature
    # and get the confidence value onto confidence file
    # that is particular for the model's concept
    ###################################################
    print ' '
    print 'TESTING  TIME .... '
    f_log.write('TESTING TIME ....\n')
    f_log.flush()

    test_image_paths = create_ID_file_name_map_file(IMG_ID_NAME_MAP_FILE ,TEST_DATA_ROOT_PATH)
    f_log.write('Test image paths: ' + str(test_image_paths) + '\n')
    f_log.flush()

    for model_name in CONCEPT_MODEL_DICT.keys():
        SVM = CONCEPT_MODEL_DICT[model_name];
        conf_files_path = os.path.join(IMG_CONF_FOLDER,model_name)+'.txt'

        f_log.write('conf_files_path: ' + conf_files_path + '\n')
        f_log.flush()

        print model_name, ' confidence file will be written to -------> ', conf_files_path

        f_log.write('model name: ' + model_name + '\n')
        f_log.flush()

        with open(conf_files_path,'w') as f:
            for count, test_image in enumerate(test_image_paths):

                f_log.write('test_image: ' + test_image + '\n')
                f_log.flush()

                img = cv2.imread(test_image)
                try:
                    hist = cv2.calcHist(img, [0,1,2], None, [num_color_bins,num_color_bins,num_color_bins], [0, 256, 0, 256, 0, 256])
                except:
                    os.remove(test_image)
                    print 'Image Read Problem in OpenCV at testing time. Most probably image file format is not suitable!!'
                    f_log.write('Image Read Problem in OpenCV. Most probably image file format is not suitable!! ---> ' + image_path + '\n')
                    f_log.flush()
                    
                hist = cv2.normalize(hist)
                hist = hist.flatten()
                hist = hist - ROW_MEANS[:,None] / (ROW_STDS+10)
                conf = SVM.decision_function(hist)
                f.write("%s, %.3f \n" % (str(count), conf[0]))    
    f_log.close()      

    
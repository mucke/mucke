In this folder there are two different runs in IMG_CLASSIFICATION folder;

'BasicClassification.py' is a single concept run given the target concept folder in the
data/train_images and it gives a final accuracy for the sanity check
        
'FullClassification.py' is the full run that takes all the concepts from data/full/train_images
and trains separate models for each concept by using other concept images as their negative. After
training test data from data/full/test_images are proceeded, their concept confidence values are
written to separate files in the result/full folder.

Test images are proceeded with unique ids and these ids are mapped to image names in data/full/mappings.txt

CAUTION: File paths are provided by parametres to the Python scripts.

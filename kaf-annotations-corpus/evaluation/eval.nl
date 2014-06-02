-------------------------------------------------------------------------------
LABELLED DATA
-------------------------------------------------------------------------------
/home/VICOMTECH/aazpeitia/Desktop/opeNER/domain-adaptation-tool/kaf-annotations-corpus/svm/nl/kaf-annotations.no-chunk.balanced.tf-idf.nl.Time.train
/home/VICOMTECH/aazpeitia/Desktop/opeNER/domain-adaptation-tool/kaf-annotations-corpus/svm/nl/kaf-annotations.no-chunk.balanced.tf-idf.nl.POI.train
/home/VICOMTECH/aazpeitia/Desktop/opeNER/domain-adaptation-tool/kaf-annotations-corpus/svm/nl/kaf-annotations.no-chunk.balanced.tf-idf.nl.Person.train
/home/VICOMTECH/aazpeitia/Desktop/opeNER/domain-adaptation-tool/kaf-annotations-corpus/svm/nl/kaf-annotations.no-chunk.balanced.tf-idf.nl.Percent.train
/home/VICOMTECH/aazpeitia/Desktop/opeNER/domain-adaptation-tool/kaf-annotations-corpus/svm/nl/kaf-annotations.no-chunk.balanced.tf-idf.nl.Money.train
/home/VICOMTECH/aazpeitia/Desktop/opeNER/domain-adaptation-tool/kaf-annotations-corpus/svm/nl/kaf-annotations.no-chunk.balanced.tf-idf.nl.Location.train
/home/VICOMTECH/aazpeitia/Desktop/opeNER/domain-adaptation-tool/kaf-annotations-corpus/svm/nl/kaf-annotations.no-chunk.balanced.tf-idf.nl.Hotel.train
/home/VICOMTECH/aazpeitia/Desktop/opeNER/domain-adaptation-tool/kaf-annotations-corpus/svm/nl/kaf-annotations.no-chunk.balanced.tf-idf.nl.Date.train
/home/VICOMTECH/aazpeitia/Desktop/opeNER/domain-adaptation-tool/kaf-annotations-corpus/svm/nl/kaf-annotations.no-chunk.balanced.tf-idf.nl.Attraction.train
/home/VICOMTECH/aazpeitia/Desktop/opeNER/domain-adaptation-tool/kaf-annotations-corpus/svm/nl/kaf-annotations.no-chunk.balanced.tf-idf.nl.Restaurant.train

-------------------------------------------------------------------------------
UNLABELLED DATA
-------------------------------------------------------------------------------
/home/VICOMTECH/aazpeitia/Desktop/opeNER/domain-adaptation-tool/kaf-annotations-corpus/svm/nl/kaf-annotations.no-chunk.balanced.tf-idf.nl.train

-------------------------------------------------------------------------------
TEST DATA
-------------------------------------------------------------------------------
/home/VICOMTECH/aazpeitia/Desktop/opeNER/domain-adaptation-tool/kaf-annotations-corpus/svm/nl/kaf-annotations.no-chunk.balanced.tf-idf.nl.test

-------------------------------------------------------------------------------
PARAMETERS
-------------------------------------------------------------------------------
THRESHOLD:         0.95
THRESHOLD_MIN:     0.1
THRESHOLD_STEP:    0.05
MIN INSTANCES:     100
RATIO:             2:1
TF-IDF THRESHOLD:  0.55

-------------------------------------------------------------------------------
FIRST CLASSIFIERS
-------------------------------------------------------------------------------
Training classifiers..........
Evaluation.

| Category | Precision | Recall | F-Score |    TP    |    FP    |  TOTAL  |
|      3.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |       5 | 
|      6.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |       3 | 
|      7.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |       1 | 
|      2.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |       1 | 
|     -1.0 |   20.29   | 100.0  |   33.73 |     28.0 |    110.0 |      28 | 
|      1.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |      11 | 
|      10.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |      38 | 
|      4.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |       5 | 
|      9.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |      17 | 
|      8.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |      27 | 
|      5.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |       2 | 
|  AVERAGE |    1.84   |   9.09 |    3.07 |     2.55 |     10.0 |     138 | 
 
-------------------------------------------------------------------------------
-------------------------------------------------------------------------------
BOOTSTRAPPING
-------------------------------------------------------------------------------
ITERATION: 1.

| Category | Added | Correct |  Total |
|      3.0 |     0 |       0 |      0 |
|      6.0 |     0 |       0 |      0 |
|      7.0 |     0 |       0 |      0 |
|      2.0 |     0 |       0 |      0 |
|      1.0 |     0 |       0 |      0 |
|      10.0 |     0 |       0 |      0 |
|      4.0 |     0 |       0 |      0 |
|      9.0 |     0 |       0 |      0 |
|      8.0 |     0 |       0 |      0 |
|      5.0 |     0 |       0 |      0 |
|    TOTAL |     0 |       0 |    187 |
New threshold: 0.9

ITERATION: 2.

| Category | Added | Correct |  Total |
|      3.0 |     0 |       0 |      0 |
|      6.0 |     0 |       0 |      0 |
|      7.0 |     0 |       0 |      0 |
|      2.0 |     0 |       0 |      0 |
|      1.0 |     0 |       0 |      0 |
|      10.0 |     0 |       0 |      0 |
|      4.0 |     0 |       0 |      0 |
|      9.0 |     0 |       0 |      0 |
|      8.0 |     0 |       0 |      0 |
|      5.0 |     0 |       0 |      0 |
|    TOTAL |     0 |       0 |    187 |
New threshold: 0.85

ITERATION: 3.

| Category | Added | Correct |  Total |
|      3.0 |     0 |       0 |      0 |
|      6.0 |     0 |       0 |      0 |
|      7.0 |     0 |       0 |      0 |
|      2.0 |     0 |       0 |      0 |
|      1.0 |     0 |       0 |      0 |
|      10.0 |     0 |       0 |      0 |
|      4.0 |     0 |       0 |      0 |
|      9.0 |     0 |       0 |      0 |
|      8.0 |     0 |       0 |      0 |
|      5.0 |     0 |       0 |      0 |
|    TOTAL |     0 |       0 |    187 |
New threshold: 0.8

ITERATION: 4.

| Category | Added | Correct |  Total |
|      3.0 |     0 |       0 |      0 |
|      6.0 |     0 |       0 |      0 |
|      7.0 |     0 |       0 |      0 |
|      2.0 |     0 |       0 |      0 |
|      1.0 |     0 |       0 |      0 |
|      10.0 |     0 |       0 |      0 |
|      4.0 |     0 |       0 |      0 |
|      9.0 |     0 |       0 |      0 |
|      8.0 |     0 |       0 |      0 |
|      5.0 |     0 |       0 |      0 |
|    TOTAL |     0 |       0 |    187 |
New threshold: 0.75

ITERATION: 5.

| Category | Added | Correct |  Total |
|      3.0 |     0 |       0 |      0 |
|      6.0 |     0 |       0 |      0 |
|      7.0 |     0 |       0 |      0 |
|      2.0 |     0 |       0 |      0 |
|      1.0 |     0 |       0 |      0 |
|      10.0 |     0 |       0 |      0 |
|      4.0 |     0 |       0 |      0 |
|      9.0 |     0 |       0 |      0 |
|      8.0 |     0 |       0 |      0 |
|      5.0 |     0 |       0 |      0 |
|    TOTAL |     0 |       0 |    187 |
New threshold: 0.7

ITERATION: 6.

| Category | Added | Correct |  Total |
|      3.0 |     0 |       0 |      0 |
|      6.0 |     0 |       0 |      0 |
|      7.0 |     0 |       0 |      0 |
|      2.0 |     0 |       0 |      0 |
|      1.0 |     0 |       0 |      0 |
|      10.0 |     0 |       0 |      0 |
|      4.0 |     0 |       0 |      0 |
|      9.0 |     0 |       0 |      0 |
|      8.0 |     0 |       0 |      0 |
|      5.0 |     0 |       0 |      0 |
|    TOTAL |     0 |       0 |    187 |
New threshold: 0.65

ITERATION: 7.

| Category | Added | Correct |  Total |
|      3.0 |     0 |       0 |      0 |
|      6.0 |     0 |       0 |      0 |
|      7.0 |     0 |       0 |      0 |
|      2.0 |     0 |       0 |      0 |
|      1.0 |     0 |       0 |      0 |
|      10.0 |     0 |       0 |      0 |
|      4.0 |     0 |       0 |      0 |
|      9.0 |     0 |       0 |      0 |
|      8.0 |     0 |       0 |      0 |
|      5.0 |     0 |       0 |      0 |
|    TOTAL |     0 |       0 |    187 |
New threshold: 0.6

ITERATION: 8.

| Category | Added | Correct |  Total |
|      3.0 |     0 |       0 |      0 |
|      6.0 |     0 |       0 |      0 |
|      7.0 |     0 |       0 |      0 |
|      2.0 |     0 |       0 |      0 |
|      1.0 |     0 |       0 |      0 |
|      10.0 |     0 |       0 |      0 |
|      4.0 |     0 |       0 |      0 |
|      9.0 |     0 |       0 |      0 |
|      8.0 |     0 |       0 |      0 |
|      5.0 |     0 |       0 |      0 |
|    TOTAL |     0 |       0 |    187 |
New threshold: 0.55

ITERATION: 9.

| Category | Added | Correct |  Total |
|      3.0 |     0 |       0 |      0 |
|      6.0 |     0 |       0 |      0 |
|      7.0 |     0 |       0 |      0 |
|      2.0 |     0 |       0 |      0 |
|      1.0 |     0 |       0 |      0 |
|      10.0 |     0 |       0 |      0 |
|      4.0 |     0 |       0 |      0 |
|      9.0 |     0 |       0 |      0 |
|      8.0 |     0 |       0 |      0 |
|      5.0 |     0 |       0 |      0 |
|    TOTAL |     0 |       0 |    187 |
New threshold: 0.5

ITERATION: 10.

| Category | Added | Correct |  Total |
|      3.0 |     0 |       0 |      0 |
|      6.0 |     0 |       0 |      0 |
|      7.0 |     0 |       0 |      0 |
|      2.0 |     0 |       0 |      0 |
|      1.0 |     0 |       0 |      0 |
|      10.0 |     0 |       0 |      0 |
|      4.0 |     0 |       0 |      0 |
|      9.0 |     0 |       0 |      0 |
|      8.0 |     0 |       0 |      0 |
|      5.0 |     0 |       0 |      0 |
|    TOTAL |     0 |       0 |    187 |
New threshold: 0.45

ITERATION: 11.

| Category | Added | Correct |  Total |
|      3.0 |     0 |       0 |      0 |
|      6.0 |     0 |       0 |      0 |
|      7.0 |     0 |       0 |      0 |
|      2.0 |     0 |       0 |      0 |
|      1.0 |     0 |       0 |      0 |
|      10.0 |     0 |       0 |      0 |
|      4.0 |     0 |       0 |      0 |
|      9.0 |     0 |       0 |      0 |
|      8.0 |     0 |       0 |      0 |
|      5.0 |     0 |       0 |      0 |
|    TOTAL |     0 |       0 |    187 |
New threshold: 0.4

ITERATION: 12.

| Category | Added | Correct |  Total |
|      3.0 |     0 |       0 |      0 |
|      6.0 |     0 |       0 |      0 |
|      7.0 |     0 |       0 |      0 |
|      2.0 |     0 |       0 |      0 |
|      1.0 |     0 |       0 |      0 |
|      10.0 |     0 |       0 |      0 |
|      4.0 |     0 |       0 |      0 |
|      9.0 |     0 |       0 |      0 |
|      8.0 |     0 |       0 |      0 |
|      5.0 |     0 |       0 |      0 |
|    TOTAL |     0 |       0 |    187 |
New threshold: 0.35

ITERATION: 13.

| Category | Added | Correct |  Total |
|      3.0 |     0 |       0 |      0 |
|      6.0 |     0 |       0 |      0 |
|      7.0 |     0 |       0 |      0 |
|      2.0 |     0 |       0 |      0 |
|      1.0 |     0 |       0 |      0 |
|      10.0 |     0 |       0 |      0 |
|      4.0 |     0 |       0 |      0 |
|      9.0 |     0 |       0 |      0 |
|      8.0 |     0 |       0 |      0 |
|      5.0 |     0 |       0 |      0 |
|    TOTAL |     0 |       0 |    187 |
New threshold: 0.3

ITERATION: 14.

| Category | Added | Correct |  Total |
|      3.0 |     0 |       0 |      0 |
|      6.0 |     0 |       0 |      0 |
|      7.0 |     0 |       0 |      0 |
|      2.0 |     0 |       0 |      0 |
|      1.0 |     0 |       0 |      0 |
|      10.0 |     0 |       0 |      0 |
|      4.0 |     0 |       0 |      0 |
|      9.0 |     0 |       0 |      0 |
|      8.0 |     0 |       0 |      0 |
|      5.0 |     0 |       0 |      0 |
|    TOTAL |     0 |       0 |    187 |
New threshold: 0.25

ITERATION: 15.

| Category | Added | Correct |  Total |
|      3.0 |     0 |       0 |      0 |
|      6.0 |     0 |       0 |      0 |
|      7.0 |     0 |       0 |      0 |
|      2.0 |     0 |       0 |      0 |
|      1.0 |     0 |       0 |      0 |
|      10.0 |     0 |       0 |      0 |
|      4.0 |     0 |       0 |      0 |
|      9.0 |     0 |       0 |      0 |
|      8.0 |     0 |       0 |      0 |
|      5.0 |     0 |       0 |      0 |
|    TOTAL |     0 |       0 |    187 |
New threshold: 0.2

ITERATION: 16.

| Category | Added | Correct |  Total |
|      3.0 |     0 |       0 |      0 |
|      6.0 |     0 |       0 |      0 |
|      7.0 |     0 |       0 |      0 |
|      2.0 |     0 |       0 |      0 |
|      1.0 |     0 |       0 |      0 |
|      10.0 |     0 |       0 |      0 |
|      4.0 |     0 |       0 |      0 |
|      9.0 |     0 |       0 |      0 |
|      8.0 |     0 |       0 |      0 |
|      5.0 |     0 |       0 |      0 |
|    TOTAL |     0 |       0 |    187 |
New threshold: 0.15

ITERATION: 17.

| Category | Added | Correct |  Total |
|      3.0 |     0 |       0 |      0 |
|      6.0 |     0 |       0 |      0 |
|      7.0 |     0 |       0 |      0 |
|      2.0 |     0 |       0 |      0 |
|      1.0 |     0 |       0 |      0 |
|      10.0 |     0 |       0 |      0 |
|      4.0 |     0 |       0 |      0 |
|      9.0 |     0 |       0 |      0 |
|      8.0 |     1 |       0 |      0 |
|      5.0 |     0 |       0 |      0 |
|    TOTAL |     1 |       0 |    187 |
New threshold: 0.1

Training classifiers..........
Evaluation.

| Category | Precision | Recall | F-Score |    TP    |    FP    |  TOTAL  |
|      3.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |       5 | 
|      6.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |       3 | 
|      7.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |       1 | 
|      2.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |       1 | 
|     -1.0 |   20.29   | 100.0  |   33.73 |     28.0 |    110.0 |      28 | 
|      1.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |      11 | 
|      10.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |      38 | 
|      4.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |       5 | 
|      9.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |      17 | 
|      8.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |      27 | 
|      5.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |       2 | 
|  AVERAGE |    1.84   |   9.09 |    3.07 |     2.55 |     10.0 |     138 | 
 
-------------------------------------------------------------------------------
ITERATION: 18.

| Category | Added | Correct |  Total |
|      3.0 |     0 |       0 |      0 |
|      6.0 |     0 |       0 |      0 |
|      7.0 |     0 |       0 |      0 |
|      2.0 |     0 |       0 |      0 |
|      1.0 |     0 |       0 |      0 |
|      10.0 |    18 |       3 |      0 |
|      4.0 |     0 |       0 |      0 |
|      9.0 |     0 |       0 |      0 |
|      8.0 |    67 |      23 |      0 |
|      5.0 |     0 |       0 |      0 |
|    TOTAL |    85 |      26 |    186 |
New threshold: 0.1

Training classifiers..........
Evaluation.

| Category | Precision | Recall | F-Score |    TP    |    FP    |  TOTAL  |
|      3.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |       5 | 
|      6.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |       3 | 
|      7.0 |    0.0    |   0.0  |    0.0  |      0.0 |      1.0 |       1 | 
|      2.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |       1 | 
|     -1.0 |   24.14   |  75.0  |   36.52 |     21.0 |     66.0 |      28 | 
|      1.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |      11 | 
|      10.0 |   50.0    |  10.53 |   17.39 |      4.0 |      4.0 |      38 | 
|      4.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |       5 | 
|      9.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |      17 | 
|      8.0 |   33.33   |  51.85 |   40.58 |     14.0 |     28.0 |      27 | 
|      5.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |       2 | 
|  AVERAGE |    9.77   |  12.49 |    8.59 |     3.55 |      9.0 |     138 | 
 
-------------------------------------------------------------------------------
ITERATION: 19..

| Category | Added | Correct |  Total |
|      3.0 |     0 |       0 |      0 |
|      6.0 |     0 |       0 |      0 |
|      7.0 |     0 |       0 |      0 |
|      2.0 |     0 |       0 |      0 |
|      1.0 |     0 |       0 |      0 |
|      10.0 |    28 |       7 |      0 |
|      4.0 |     0 |       0 |      0 |
|      9.0 |     0 |       0 |      0 |
|      8.0 |    57 |      20 |      0 |
|      5.0 |     0 |       0 |      0 |
|    TOTAL |    85 |      27 |    101 |
New threshold: 0.1

Training classifiers..........
Evaluation.

| Category | Precision | Recall | F-Score |    TP    |    FP    |  TOTAL  |
|      3.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |       5 | 
|      6.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |       3 | 
|      7.0 |    0.0    |   0.0  |    0.0  |      0.0 |      1.0 |       1 | 
|      2.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |       1 | 
|     -1.0 |   22.11   |  75.0  |   34.15 |     21.0 |     74.0 |      28 | 
|      1.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |      11 | 
|      10.0 |   60.0    |   7.89 |   13.95 |      3.0 |      2.0 |      38 | 
|      4.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |       5 | 
|      9.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |      17 | 
|      8.0 |   32.43   |  44.44 |   37.5  |     12.0 |     25.0 |      27 | 
|      5.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |       2 | 
|  AVERAGE |   10.41   |  11.58 |    7.78 |     3.27 |     9.27 |     138 | 
 
-------------------------------------------------------------------------------
ITERATION: 20.

| Category | Added | Correct |  Total |
|      3.0 |     0 |       0 |      0 |
|      6.0 |     0 |       0 |      0 |
|      7.0 |     1 |       0 |      0 |
|      2.0 |     0 |       0 |      0 |
|      1.0 |     0 |       0 |      0 |
|      10.0 |     2 |       0 |      0 |
|      4.0 |     0 |       0 |      0 |
|      9.0 |     0 |       0 |      0 |
|      8.0 |     0 |       0 |      0 |
|      5.0 |     0 |       0 |      0 |
|    TOTAL |     3 |       0 |     16 |
New threshold: 0.1

Training classifiers..........
Evaluation.

| Category | Precision | Recall | F-Score |    TP    |    FP    |  TOTAL  |
|      3.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |       5 | 
|      6.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |       3 | 
|      7.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |       1 | 
|      2.0 |    0.0    |   0.0  |    0.0  |      0.0 |      1.0 |       1 | 
|     -1.0 |   25.93   |  50.0  |   34.15 |     14.0 |     40.0 |      28 | 
|      1.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |      11 | 
|      10.0 |   52.38   |  28.95 |   37.29 |     11.0 |     10.0 |      38 | 
|      4.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |       5 | 
|      9.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |      17 | 
|      8.0 |   30.65   |  70.37 |   42.7  |     19.0 |     43.0 |      27 | 
|      5.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |       2 | 
|  AVERAGE |    9.91   |  13.57 |   10.38 |      4.0 |     8.55 |     138 | 
 
-------------------------------------------------------------------------------
ITERATION: 21..

| Category | Added | Correct |  Total |
|      3.0 |     0 |       0 |      0 |
|      6.0 |     0 |       0 |      0 |
|      7.0 |     0 |       0 |      0 |
|      2.0 |     1 |       0 |      0 |
|      1.0 |     0 |       0 |      0 |
|      10.0 |     4 |       0 |      0 |
|      4.0 |     0 |       0 |      0 |
|      9.0 |     0 |       0 |      0 |
|      8.0 |     1 |       0 |      0 |
|      5.0 |     0 |       0 |      0 |
|    TOTAL |     6 |       0 |     13 |
New threshold: 0.1

Training classifiers..........
Evaluation.

| Category | Precision | Recall | F-Score |    TP    |    FP    |  TOTAL  |
|      3.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |       5 | 
|      6.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |       3 | 
|      7.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |       1 | 
|      2.0 |    0.0    |   0.0  |    0.0  |      0.0 |      2.0 |       1 | 
|     -1.0 |   18.18   |  21.43 |   19.67 |      6.0 |     27.0 |      28 | 
|      1.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |      11 | 
|      10.0 |   43.59   |  44.74 |   44.16 |     17.0 |     22.0 |      38 | 
|      4.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |       5 | 
|      9.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |      17 | 
|      8.0 |   31.25   |  74.07 |   43.96 |     20.0 |     44.0 |      27 | 
|      5.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |       2 | 
|  AVERAGE |    8.46   |  12.75 |    9.8  |     3.91 |     8.64 |     138 | 
 
-------------------------------------------------------------------------------
ITERATION: 22..

| Category | Added | Correct |  Total |
|      3.0 |     0 |       0 |      0 |
|      6.0 |     0 |       0 |      0 |
|      7.0 |     0 |       0 |      0 |
|      2.0 |     0 |       0 |      0 |
|      1.0 |     0 |       0 |      0 |
|      10.0 |     4 |       0 |      0 |
|      4.0 |     0 |       0 |      0 |
|      9.0 |     0 |       0 |      0 |
|      8.0 |     0 |       0 |      0 |
|      5.0 |     0 |       0 |      0 |
|    TOTAL |     4 |       0 |      7 |
New threshold: 0.1

Training classifiers..........
Evaluation.

| Category | Precision | Recall | F-Score |    TP    |    FP    |  TOTAL  |
|      3.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |       5 | 
|      6.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |       3 | 
|      7.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |       1 | 
|      2.0 |    0.0    |   0.0  |    0.0  |      0.0 |      1.0 |       1 | 
|     -1.0 |   17.39   |  28.57 |   21.62 |      8.0 |     38.0 |      28 | 
|      1.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |      11 | 
|      10.0 |   48.39   |  39.47 |   43.48 |     15.0 |     16.0 |      38 | 
|      4.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |       5 | 
|      9.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |      17 | 
|      8.0 |   31.67   |  70.37 |   43.68 |     19.0 |     41.0 |      27 | 
|      5.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |       2 | 
|  AVERAGE |    8.86   |  12.58 |    9.89 |     3.82 |     8.73 |     138 | 
 
-------------------------------------------------------------------------------
ITERATION: 23

| Category | Added | Correct |  Total |
|      3.0 |     0 |       0 |      0 |
|      6.0 |     0 |       0 |      0 |
|      7.0 |     0 |       0 |      0 |
|      2.0 |     0 |       0 |      0 |
|      1.0 |     0 |       0 |      0 |
|      10.0 |     3 |       0 |      0 |
|      4.0 |     0 |       0 |      0 |
|      9.0 |     0 |       0 |      0 |
|      8.0 |     0 |       0 |      0 |
|      5.0 |     0 |       0 |      0 |
|    TOTAL |     3 |       0 |      3 |
New threshold: 0.1

Training classifiers..........
Evaluation.

| Category | Precision | Recall | F-Score |    TP    |    FP    |  TOTAL  |
|      3.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |       5 | 
|      6.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |       3 | 
|      7.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |       1 | 
|      2.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |       1 | 
|     -1.0 |   16.67   |  35.71 |   22.73 |     10.0 |     50.0 |      28 | 
|      1.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |      11 | 
|      10.0 |   50.0    |  36.84 |   42.42 |     14.0 |     14.0 |      38 | 
|      4.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |       5 | 
|      9.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |      17 | 
|      8.0 |   34.0    |  62.96 |   44.16 |     17.0 |     33.0 |      27 | 
|      5.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |       2 | 
|  AVERAGE |    9.15   |  12.32 |    9.94 |     3.73 |     8.82 |     138 | 
 
-------------------------------------------------------------------------------
ITERATION: 24

| Category | Added | Correct |  Total |
|      3.0 |     0 |       0 |      0 |
|      6.0 |     0 |       0 |      0 |
|      7.0 |     0 |       0 |      0 |
|      2.0 |     0 |       0 |      0 |
|      1.0 |     0 |       0 |      0 |
|      10.0 |     0 |       0 |      0 |
|      4.0 |     0 |       0 |      0 |
|      9.0 |     0 |       0 |      0 |
|      8.0 |     0 |       0 |      0 |
|      5.0 |     0 |       0 |      0 |
|    TOTAL |     0 |       0 |      0 |
-------------------------------------------------------------------------------
SAVING CLASSIFIERS
-------------------------------------------------------------------------------
Classifier '/home/VICOMTECH/aazpeitia/Desktop/opeNER/domain-adaptation-tool/kaf-annotations-corpus/svm/nl/kaf-annotations.no-chunk.balanced.tf-idf.nl.Time.bin' wrote
Classifier '/home/VICOMTECH/aazpeitia/Desktop/opeNER/domain-adaptation-tool/kaf-annotations-corpus/svm/nl/kaf-annotations.no-chunk.balanced.tf-idf.nl.POI.bin' wrote
Classifier '/home/VICOMTECH/aazpeitia/Desktop/opeNER/domain-adaptation-tool/kaf-annotations-corpus/svm/nl/kaf-annotations.no-chunk.balanced.tf-idf.nl.Person.bin' wrote
Classifier '/home/VICOMTECH/aazpeitia/Desktop/opeNER/domain-adaptation-tool/kaf-annotations-corpus/svm/nl/kaf-annotations.no-chunk.balanced.tf-idf.nl.Percent.bin' wrote
Classifier '/home/VICOMTECH/aazpeitia/Desktop/opeNER/domain-adaptation-tool/kaf-annotations-corpus/svm/nl/kaf-annotations.no-chunk.balanced.tf-idf.nl.Money.bin' wrote
Classifier '/home/VICOMTECH/aazpeitia/Desktop/opeNER/domain-adaptation-tool/kaf-annotations-corpus/svm/nl/kaf-annotations.no-chunk.balanced.tf-idf.nl.Location.bin' wrote
Classifier '/home/VICOMTECH/aazpeitia/Desktop/opeNER/domain-adaptation-tool/kaf-annotations-corpus/svm/nl/kaf-annotations.no-chunk.balanced.tf-idf.nl.Hotel.bin' wrote
Classifier '/home/VICOMTECH/aazpeitia/Desktop/opeNER/domain-adaptation-tool/kaf-annotations-corpus/svm/nl/kaf-annotations.no-chunk.balanced.tf-idf.nl.Date.bin' wrote
Classifier '/home/VICOMTECH/aazpeitia/Desktop/opeNER/domain-adaptation-tool/kaf-annotations-corpus/svm/nl/kaf-annotations.no-chunk.balanced.tf-idf.nl.Attraction.bin' wrote
Classifier '/home/VICOMTECH/aazpeitia/Desktop/opeNER/domain-adaptation-tool/kaf-annotations-corpus/svm/nl/kaf-annotations.no-chunk.balanced.tf-idf.nl.Restaurant.bin' wrote
-------------------------------------------------------------------------------
SAVING TF-IDF DICTIONARY
-------------------------------------------------------------------------------
TF-IDF dictionary '/home/VICOMTECH/aazpeitia/Desktop/opeNER/domain-adaptation-tool/kaf-annotations-corpus/svm/nl/kaf-annotations.no-chunk.balanced.tf-idf.nl.tfidf.dic' wrote
TF-IDF binarized dictionary '/home/VICOMTECH/aazpeitia/Desktop/opeNER/domain-adaptation-tool/kaf-annotations-corpus/svm/nl/kaf-annotations.no-chunk.balanced.tf-idf.nl.tfidf.dbin' wrote

-------------------------------------------------------------------------------
LABELLED DATA
-------------------------------------------------------------------------------
/home/VICOMTECH/aazpeitia/Desktop/opeNER/domain-adaptation-tool/kaf-annotations-corpus/svm/nl/kaf-annotations.no-chunk.balanced.tf-idf.nl.Attraction.train
/home/VICOMTECH/aazpeitia/Desktop/opeNER/domain-adaptation-tool/kaf-annotations-corpus/svm/nl/kaf-annotations.no-chunk.balanced.tf-idf.nl.Date.train
/home/VICOMTECH/aazpeitia/Desktop/opeNER/domain-adaptation-tool/kaf-annotations-corpus/svm/nl/kaf-annotations.no-chunk.balanced.tf-idf.nl.Hotel.train
/home/VICOMTECH/aazpeitia/Desktop/opeNER/domain-adaptation-tool/kaf-annotations-corpus/svm/nl/kaf-annotations.no-chunk.balanced.tf-idf.nl.Location.train
/home/VICOMTECH/aazpeitia/Desktop/opeNER/domain-adaptation-tool/kaf-annotations-corpus/svm/nl/kaf-annotations.no-chunk.balanced.tf-idf.nl.Money.train
/home/VICOMTECH/aazpeitia/Desktop/opeNER/domain-adaptation-tool/kaf-annotations-corpus/svm/nl/kaf-annotations.no-chunk.balanced.tf-idf.nl.Percent.train
/home/VICOMTECH/aazpeitia/Desktop/opeNER/domain-adaptation-tool/kaf-annotations-corpus/svm/nl/kaf-annotations.no-chunk.balanced.tf-idf.nl.Person.train
/home/VICOMTECH/aazpeitia/Desktop/opeNER/domain-adaptation-tool/kaf-annotations-corpus/svm/nl/kaf-annotations.no-chunk.balanced.tf-idf.nl.POI.train
/home/VICOMTECH/aazpeitia/Desktop/opeNER/domain-adaptation-tool/kaf-annotations-corpus/svm/nl/kaf-annotations.no-chunk.balanced.tf-idf.nl.Restaurant.train
/home/VICOMTECH/aazpeitia/Desktop/opeNER/domain-adaptation-tool/kaf-annotations-corpus/svm/nl/kaf-annotations.no-chunk.balanced.tf-idf.nl.Time.train

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
|     -1.0 |    8.4    | 100.0  |   15.5  |     10.0 |    109.0 |      10 | 
|      1.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |      11 | 
|      10.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |      37 | 
|      4.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |       5 | 
|      9.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |      17 | 
|      8.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |      27 | 
|      5.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |       2 | 
|  AVERAGE |    0.76   |   9.09 |    1.41 |     0.91 |     9.91 |     119 | 
 
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
|    TOTAL |     0 |       0 |    160 |
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
|    TOTAL |     0 |       0 |    160 |
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
|    TOTAL |     0 |       0 |    160 |
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
|    TOTAL |     0 |       0 |    160 |
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
|    TOTAL |     0 |       0 |    160 |
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
|    TOTAL |     0 |       0 |    160 |
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
|    TOTAL |     0 |       0 |    160 |
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
|    TOTAL |     0 |       0 |    160 |
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
|    TOTAL |     0 |       0 |    160 |
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
|    TOTAL |     0 |       0 |    160 |
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
|    TOTAL |     0 |       0 |    160 |
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
|    TOTAL |     0 |       0 |    160 |
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
|    TOTAL |     0 |       0 |    160 |
New threshold: 0.3

ITERATION: 14.

| Category | Added | Correct |  Total |
|      3.0 |     0 |       0 |      0 |
|      6.0 |     0 |       0 |      0 |
|      7.0 |     0 |       0 |      0 |
|      2.0 |     0 |       0 |      0 |
|      1.0 |     0 |       0 |      0 |
|      10.0 |     1 |       0 |      0 |
|      4.0 |     0 |       0 |      0 |
|      9.0 |     0 |       0 |      0 |
|      8.0 |     0 |       0 |      0 |
|      5.0 |     0 |       0 |      0 |
|    TOTAL |     1 |       0 |    160 |
New threshold: 0.25

Training classifiers..........
Evaluation.

| Category | Precision | Recall | F-Score |    TP    |    FP    |  TOTAL  |
|      3.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |       5 | 
|      6.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |       3 | 
|      7.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |       1 | 
|      2.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |       1 | 
|     -1.0 |    8.4    | 100.0  |   15.5  |     10.0 |    109.0 |      10 | 
|      1.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |      11 | 
|      10.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |      37 | 
|      4.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |       5 | 
|      9.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |      17 | 
|      8.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |      27 | 
|      5.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |       2 | 
|  AVERAGE |    0.76   |   9.09 |    1.41 |     0.91 |     9.91 |     119 | 
 
-------------------------------------------------------------------------------
ITERATION: 15.

| Category | Added | Correct |  Total |
|      3.0 |     0 |       0 |      0 |
|      6.0 |     0 |       0 |      0 |
|      7.0 |     0 |       0 |      0 |
|      2.0 |     0 |       0 |      0 |
|      1.0 |     0 |       0 |      0 |
|      10.0 |     4 |       2 |      0 |
|      4.0 |     0 |       0 |      0 |
|      9.0 |     0 |       0 |      0 |
|      8.0 |     5 |       3 |      0 |
|      5.0 |     0 |       0 |      0 |
|    TOTAL |     9 |       5 |    159 |
New threshold: 0.2

Training classifiers..........
Evaluation.

| Category | Precision | Recall | F-Score |    TP    |    FP    |  TOTAL  |
|      3.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |       5 | 
|      6.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |       3 | 
|      7.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |       1 | 
|      2.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |       1 | 
|     -1.0 |    8.33   |  90.0  |   15.25 |      9.0 |     99.0 |      10 | 
|      1.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |      11 | 
|      10.0 |  100.0    |   5.41 |   10.26 |      2.0 |      0.0 |      37 | 
|      4.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |       5 | 
|      9.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |      17 | 
|      8.0 |   55.56   |  18.52 |   27.78 |      5.0 |      4.0 |      27 | 
|      5.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |       2 | 
|  AVERAGE |   14.9    |  10.36 |    4.84 |     1.45 |     9.36 |     119 | 
 
-------------------------------------------------------------------------------
ITERATION: 16.

| Category | Added | Correct |  Total |
|      3.0 |     0 |       0 |      0 |
|      6.0 |     0 |       0 |      0 |
|      7.0 |     0 |       0 |      0 |
|      2.0 |     0 |       0 |      0 |
|      1.0 |     0 |       0 |      0 |
|      10.0 |     9 |       2 |      0 |
|      4.0 |     0 |       0 |      0 |
|      9.0 |     0 |       0 |      0 |
|      8.0 |    41 |      13 |      0 |
|      5.0 |     0 |       0 |      0 |
|    TOTAL |    50 |      15 |    150 |
New threshold: 0.15

Training classifiers..........
Evaluation.

| Category | Precision | Recall | F-Score |    TP    |    FP    |  TOTAL  |
|      3.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |       5 | 
|      6.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |       3 | 
|      7.0 |    0.0    |   0.0  |    0.0  |      0.0 |      1.0 |       1 | 
|      2.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |       1 | 
|     -1.0 |    7.5    |  60.0  |   13.33 |      6.0 |     74.0 |      10 | 
|      1.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |      11 | 
|      10.0 |   75.0    |   8.11 |   14.63 |      3.0 |      1.0 |      37 | 
|      4.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |       5 | 
|      9.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |      17 | 
|      8.0 |   35.29   |  44.44 |   39.34 |     12.0 |     22.0 |      27 | 
|      5.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |       2 | 
|  AVERAGE |   10.71   |  10.23 |    6.12 |     1.91 |     8.91 |     119 | 
 
-------------------------------------------------------------------------------
ITERATION: 17......

| Category | Added | Correct |  Total |
|      3.0 |     0 |       0 |      0 |
|      6.0 |     0 |       0 |      0 |
|      7.0 |     0 |       0 |      0 |
|      2.0 |     0 |       0 |      0 |
|      1.0 |     0 |       0 |      0 |
|      10.0 |    23 |       6 |      0 |
|      4.0 |     0 |       0 |      0 |
|      9.0 |     0 |       0 |      0 |
|      8.0 |    50 |      16 |      0 |
|      5.0 |     0 |       0 |      0 |
|    TOTAL |    73 |      22 |    100 |
New threshold: 0.1

Training classifiers..........
Evaluation.

| Category | Precision | Recall | F-Score |    TP    |    FP    |  TOTAL  |
|      3.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |       5 | 
|      6.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |       3 | 
|      7.0 |    0.0    |   0.0  |    0.0  |      0.0 |      1.0 |       1 | 
|      2.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |       1 | 
|     -1.0 |    7.89   |  60.0  |   13.95 |      6.0 |     70.0 |      10 | 
|      1.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |      11 | 
|      10.0 |   66.67   |  10.81 |   18.6  |      4.0 |      2.0 |      37 | 
|      4.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |       5 | 
|      9.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |      17 | 
|      8.0 |   38.89   |  51.85 |   44.44 |     14.0 |     22.0 |      27 | 
|      5.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |       2 | 
|  AVERAGE |   10.31   |  11.15 |    7.0  |     2.18 |     8.64 |     119 | 
 
-------------------------------------------------------------------------------
ITERATION: 18..

| Category | Added | Correct |  Total |
|      3.0 |     0 |       0 |      0 |
|      6.0 |     0 |       0 |      0 |
|      7.0 |     0 |       0 |      0 |
|      2.0 |     1 |       0 |      0 |
|      1.0 |     0 |       0 |      0 |
|      10.0 |     4 |       1 |      0 |
|      4.0 |     0 |       0 |      0 |
|      9.0 |     0 |       0 |      0 |
|      8.0 |    11 |       4 |      0 |
|      5.0 |     0 |       0 |      0 |
|    TOTAL |    16 |       5 |     27 |
New threshold: 0.1

Training classifiers..........
Evaluation.

| Category | Precision | Recall | F-Score |    TP    |    FP    |  TOTAL  |
|      3.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |       5 | 
|      6.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |       3 | 
|      7.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |       1 | 
|      2.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |       1 | 
|     -1.0 |    8.82   |  60.0  |   15.38 |      6.0 |     62.0 |      10 | 
|      1.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |      11 | 
|      10.0 |   50.0    |   5.41 |    9.76 |      2.0 |      2.0 |      37 | 
|      4.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |       5 | 
|      9.0 |  100.0    |   5.88 |   11.11 |      1.0 |      0.0 |      17 | 
|      8.0 |   34.78   |  59.26 |   43.84 |     16.0 |     30.0 |      27 | 
|      5.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |       2 | 
|  AVERAGE |   17.6    |  11.87 |    7.28 |     2.27 |     8.55 |     119 | 
 
-------------------------------------------------------------------------------
ITERATION: 19.

| Category | Added | Correct |  Total |
|      3.0 |     0 |       0 |      0 |
|      6.0 |     0 |       0 |      0 |
|      7.0 |     1 |       0 |      0 |
|      2.0 |     0 |       0 |      0 |
|      1.0 |     0 |       0 |      0 |
|      10.0 |     0 |       0 |      0 |
|      4.0 |     0 |       0 |      0 |
|      9.0 |     0 |       0 |      0 |
|      8.0 |     5 |       3 |      0 |
|      5.0 |     0 |       0 |      0 |
|    TOTAL |     6 |       3 |     11 |
New threshold: 0.1

Training classifiers..........
Evaluation.

| Category | Precision | Recall | F-Score |    TP    |    FP    |  TOTAL  |
|      3.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |       5 | 
|      6.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |       3 | 
|      7.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |       1 | 
|      2.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |       1 | 
|     -1.0 |    7.94   |  50.0  |   13.7  |      5.0 |     58.0 |      10 | 
|      1.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |      11 | 
|      10.0 |   50.0    |  16.22 |   24.49 |      6.0 |      6.0 |      37 | 
|      4.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |       5 | 
|      9.0 |  100.0    |   5.88 |   11.11 |      1.0 |      0.0 |      17 | 
|      8.0 |   37.21   |  59.26 |   45.71 |     16.0 |     27.0 |      27 | 
|      5.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |       2 | 
|  AVERAGE |   17.74   |  11.94 |    8.64 |     2.55 |     8.27 |     119 | 
 
-------------------------------------------------------------------------------
ITERATION: 20.

| Category | Added | Correct |  Total |
|      3.0 |     0 |       0 |      0 |
|      6.0 |     0 |       0 |      0 |
|      7.0 |     0 |       0 |      0 |
|      2.0 |     0 |       0 |      0 |
|      1.0 |     0 |       0 |      0 |
|      10.0 |     1 |       0 |      0 |
|      4.0 |     0 |       0 |      0 |
|      9.0 |     2 |       0 |      0 |
|      8.0 |     0 |       0 |      0 |
|      5.0 |     0 |       0 |      0 |
|    TOTAL |     3 |       0 |      5 |
New threshold: 0.1

Training classifiers..........
Evaluation.

| Category | Precision | Recall | F-Score |    TP    |    FP    |  TOTAL  |
|      3.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |       5 | 
|      6.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |       3 | 
|      7.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |       1 | 
|      2.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |       1 | 
|     -1.0 |    3.12   |  10.0  |    4.76 |      1.0 |     31.0 |      10 | 
|      1.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |      11 | 
|      10.0 |   59.09   |  35.14 |   44.07 |     13.0 |      9.0 |      37 | 
|      4.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |       5 | 
|      9.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |      17 | 
|      8.0 |   30.77   |  74.07 |   43.48 |     20.0 |     45.0 |      27 | 
|      5.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |       2 | 
|  AVERAGE |    8.45   |  10.84 |    8.39 |     3.09 |     7.73 |     119 | 
 
-------------------------------------------------------------------------------
ITERATION: 21.

| Category | Added | Correct |  Total |
|      3.0 |     0 |       0 |      0 |
|      6.0 |     0 |       0 |      0 |
|      7.0 |     0 |       0 |      0 |
|      2.0 |     0 |       0 |      0 |
|      1.0 |     0 |       0 |      0 |
|      10.0 |     0 |       0 |      0 |
|      4.0 |     0 |       0 |      0 |
|      9.0 |     0 |       0 |      0 |
|      8.0 |     1 |       1 |      0 |
|      5.0 |     0 |       0 |      0 |
|    TOTAL |     1 |       1 |      2 |
New threshold: 0.1

Training classifiers..........
Evaluation.

| Category | Precision | Recall | F-Score |    TP    |    FP    |  TOTAL  |
|      3.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |       5 | 
|      6.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |       3 | 
|      7.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |       1 | 
|      2.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |       1 | 
|     -1.0 |    3.03   |  10.0  |    4.65 |      1.0 |     32.0 |      10 | 
|      1.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |      11 | 
|      10.0 |   57.14   |  32.43 |   41.38 |     12.0 |      9.0 |      37 | 
|      4.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |       5 | 
|      9.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |      17 | 
|      8.0 |   32.31   |  77.78 |   45.65 |     21.0 |     44.0 |      27 | 
|      5.0 |    0.0    |   0.0  |    0.0  |      0.0 |      0.0 |       2 | 
|  AVERAGE |    8.41   |  10.93 |    8.33 |     3.09 |     7.73 |     119 | 
 
-------------------------------------------------------------------------------
ITERATION: 22.

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
|    TOTAL |     0 |       0 |      1 |
-------------------------------------------------------------------------------
SAVING CLASSIFIERS
-------------------------------------------------------------------------------
Classifier '/home/VICOMTECH/aazpeitia/Desktop/opeNER/domain-adaptation-tool/kaf-annotations-corpus/svm/nl/kaf-annotations.no-chunk.balanced.tf-idf.nl.Attraction.bin' wrote
Classifier '/home/VICOMTECH/aazpeitia/Desktop/opeNER/domain-adaptation-tool/kaf-annotations-corpus/svm/nl/kaf-annotations.no-chunk.balanced.tf-idf.nl.Date.bin' wrote
Classifier '/home/VICOMTECH/aazpeitia/Desktop/opeNER/domain-adaptation-tool/kaf-annotations-corpus/svm/nl/kaf-annotations.no-chunk.balanced.tf-idf.nl.Hotel.bin' wrote
Classifier '/home/VICOMTECH/aazpeitia/Desktop/opeNER/domain-adaptation-tool/kaf-annotations-corpus/svm/nl/kaf-annotations.no-chunk.balanced.tf-idf.nl.Location.bin' wrote
Classifier '/home/VICOMTECH/aazpeitia/Desktop/opeNER/domain-adaptation-tool/kaf-annotations-corpus/svm/nl/kaf-annotations.no-chunk.balanced.tf-idf.nl.Money.bin' wrote
Classifier '/home/VICOMTECH/aazpeitia/Desktop/opeNER/domain-adaptation-tool/kaf-annotations-corpus/svm/nl/kaf-annotations.no-chunk.balanced.tf-idf.nl.Percent.bin' wrote
Classifier '/home/VICOMTECH/aazpeitia/Desktop/opeNER/domain-adaptation-tool/kaf-annotations-corpus/svm/nl/kaf-annotations.no-chunk.balanced.tf-idf.nl.Person.bin' wrote
Classifier '/home/VICOMTECH/aazpeitia/Desktop/opeNER/domain-adaptation-tool/kaf-annotations-corpus/svm/nl/kaf-annotations.no-chunk.balanced.tf-idf.nl.POI.bin' wrote
Classifier '/home/VICOMTECH/aazpeitia/Desktop/opeNER/domain-adaptation-tool/kaf-annotations-corpus/svm/nl/kaf-annotations.no-chunk.balanced.tf-idf.nl.Restaurant.bin' wrote
Classifier '/home/VICOMTECH/aazpeitia/Desktop/opeNER/domain-adaptation-tool/kaf-annotations-corpus/svm/nl/kaf-annotations.no-chunk.balanced.tf-idf.nl.Time.bin' wrote
-------------------------------------------------------------------------------
SAVING TF-IDF DICTIONARY
-------------------------------------------------------------------------------
TF-IDF dictionary '/home/VICOMTECH/aazpeitia/Desktop/opeNER/domain-adaptation-tool/kaf-annotations-corpus/svm/nl/kaf-annotations.no-chunk.balanced.tf-idf.nl.tfidf.dic' wrote
TF-IDF binarized dictionary '/home/VICOMTECH/aazpeitia/Desktop/opeNER/domain-adaptation-tool/kaf-annotations-corpus/svm/nl/kaf-annotations.no-chunk.balanced.tf-idf.nl.tfidf.dbin' wrote

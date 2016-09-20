// Sentence clustering 
#include <stdio.h>
#include <mysql.h>

void clusteringStart(unsigned char*, MYSQL*);
void countInClust(unsigned char*, MYSQL*);
void newSenClustRegister( MYSQL*, int);

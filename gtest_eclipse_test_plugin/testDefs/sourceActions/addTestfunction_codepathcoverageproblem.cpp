//test checkNameExist thelist!=null && thelist instanceof IASTExpressionList
#include "cute.h"
#include "ide_listener.h"
#include "cute_runner.h"
void thisIsATest() {
	ASSERTM("start writing tests", false);	
}
void anotherTest(){
	ASSERTM^("start writing tests", false);
}
void runSuite(){
	cute::suite s;
	s.push_back(CUTE(thisIsATest));
	s.push_back(CUTE(anotherTest));
	cute::ide_listener lis;
	cute::makeRunner(lis)(s, "The Suite");
}
int main(){
    runSuite();
}
//expected
#include "cute.h"
#include "ide_listener.h"
#include "cute_runner.h"
void thisIsATest() {
	ASSERTM("start writing tests", false);	
}
void anotherTest(){
	ASSERTM("start writing tests", false);
}
void runSuite(){
	cute::suite s;
	s.push_back(CUTE(thisIsATest));
	s.push_back(CUTE(anotherTest));
	cute::ide_listener lis;
	cute::makeRunner(lis)(s, "The Suite");
}
int main(){
    runSuite();
}
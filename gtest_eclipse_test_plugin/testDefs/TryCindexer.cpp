//test
#include "cute.h"
#include "ide_listener.h"
#include "cute_runner.h"
void thisIsATest() {
	ASSERTM("start writing tests", false);	
}
void anotherTest(){
	std::string s,s2,expected;
	s="Hello";
	s2="World";
	expected="Hello World";
	ASSERT_EQUAL(expected, s.append("  \n    \t  "+s2));
}
void runSuite(){
	cute::suite s;
	s.push_back(CUTE(thisIsATest));
	s.push_back(CUTE(anotherTest));
	cute::ide_listener lis;
	cute::makeRunner(lis)(s, "The Suite");
	cute::makeRunner(lis)(s, "The Suite");
	cute::makeRunner(lis)(s, "The Suite");
}
int main(){
    runSuite();
}
//expected
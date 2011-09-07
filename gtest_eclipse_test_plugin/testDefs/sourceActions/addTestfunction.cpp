//test typical case cursor at functiondeclarator
void thisIsAl^soATest22(){
	int cx=1,dx=1;
	ASSERT(cx==dx);
}
void runTest(){
	cute::suite s=make_suite_s();
	cute::ide_listener lis;
	cute::makeRunner(lis)(s, "The Suite");
}
//expected
void thisIsAlsoATest22(){
	int cx=1,dx=1;
	ASSERT(cx==dx);
}
void runTest(){
	cute::suite s=make_suite_s();
	s.push_back(CUTE(thisIsAlsoATest22));
	cute::ide_listener lis;
	cute::makeRunner(lis)(s, "The Suite");
}
//test typical case cursor at function body
void thisIsAlsoATest22(){
	int cx=1,^dx=1;
	ASSERT(cx==dx);
}
void runTest(){
	cute::suite s=make_suite_s();
	cute::ide_listener lis;
	cute::makeRunner(lis)(s, "The Suite");
}
//expected
void thisIsAlsoATest22(){
	int cx=1,dx=1;
	ASSERT(cx==dx);
}
void runTest(){
	cute::suite s=make_suite_s();
	s.push_back(CUTE(thisIsAlsoATest22));
	cute::ide_listener lis;
	cute::makeRunner(lis)(s, "The Suite");
}
//test already added 
void thisIsAl^soATest22(){
	int cx=1,dx=1;
	ASSERT(cx==dx);
}
void runTest(){
	cute::suite s=make_suite_s();
	s.push_back(CUTE(thisIsAlsoATest22));
	cute::ide_listener lis;
	cute::makeRunner(lis)(s, "The Suite");
}
//expected
void thisIsAlsoATest22(){
	int cx=1,dx=1;
	ASSERT(cx==dx);
}
void runTest(){
	cute::suite s=make_suite_s();
	s.push_back(CUTE(thisIsAlsoATest22));
	cute::ide_listener lis;
	cute::makeRunner(lis)(s, "The Suite");
}
//test function with var arg
void thisIsAlso^ATest24(...){
	int cx=1,dx=1;
	ASSERT(cx==dx);
}
void runTest(){
	cute::suite s=make_suite_s();
	cute::ide_listener lis;
	cute::makeRunner(lis)(s, "The Suite");
}
//expected
void thisIsAlsoATest24(...){
	int cx=1,dx=1;
	ASSERT(cx==dx);
}
void runTest(){
	cute::suite s=make_suite_s();
	cute::ide_listener lis;
	cute::makeRunner(lis)(s, "The Suite");
}
//test function with parameter
void thisIsAlsoATest23(int x, double y){
	int cx=1,dx=1;
	ASSERT(cx==dx);
}
void runTest(){
	cute::suite s=make_suite_s();
	cute::ide_listener lis;
	cute::makeRunner(lis)(s, "The Suite");
}
//expected
void thisIsAlsoATest23(int x, double y){
	int cx=1,dx=1;
	ASSERT(cx==dx);
}
void runTest(){
	cute::suite s=make_suite_s();
	cute::ide_listener lis;
	cute::makeRunner(lis)(s, "The Suite");
}
//test recursive
void run^Test(){
	cute::suite s=make_suite_s();
	cute::ide_listener lis;
	cute::makeRunner(lis)(s, "The Suite");
}
//expected
void runTest(){
	cute::suite s=make_suite_s();
	cute::ide_listener lis;
	cute::makeRunner(lis)(s, "The Suite");
}
//test checkNameExist thelist!=null && thelist instanceof IASTExpressionList
void anotherTest(){
	ASSERTM^("start writing tests", false);
}
void runSuite(){
	cute::suite s;
	s.push_back(CUTE(anotherTest));
	cute::ide_listener lis;
	cute::makeRunner(lis)(s, "The Suite");
}
//expected
void anotherTest(){
	ASSERTM("start writing tests", false);
}
void runSuite(){
	cute::suite s;
	s.push_back(CUTE(anotherTest));
	cute::ide_listener lis;
	cute::makeRunner(lis)(s, "The Suite");
}
//test pushback
void anotherTest(){
	ASSERTM^("start writing tests", false);
}
void runSuite(){
	cute::suite s;
	s.push_back(CUTE(myTest));
	cute::ide_listener lis;
	cute::makeRunner(lis)(s, "The Suite");
}
//expected
void anotherTest(){
	ASSERTM("start writing tests", false);
}
void runSuite(){
	cute::suite s;
	s.push_back(CUTE(myTest));
	s.push_back(CUTE(anotherTest));
	cute::ide_listener lis;
	cute::makeRunner(lis)(s, "The Suite");
}
//test pushback with functor taking in one parameter
#include "cute.h"
#include "ide_listener.h"
#include "cute_runner.h"
void anotherTest(){
	ASSERTM^("start writing tests", false);
}
void runSuite(){
	cute::suite s;
	s.push_back(CUTE(myTest));
	s.push_back(functor(2));
	cute::ide_listener lis;
	cute::makeRunner(lis)(s, "The Suite");
}
//expected
#include "cute.h"
#include "ide_listener.h"
#include "cute_runner.h"
void anotherTest(){
	ASSERTM("start writing tests", false);
}
void runSuite(){
	cute::suite s;
	s.push_back(CUTE(myTest));
	s.push_back(functor(2));
	s.push_back(CUTE(anotherTest));
	cute::ide_listener lis;
	cute::makeRunner(lis)(s, "The Suite");
}
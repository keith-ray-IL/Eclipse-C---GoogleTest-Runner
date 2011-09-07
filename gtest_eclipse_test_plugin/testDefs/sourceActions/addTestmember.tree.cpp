//test Tree
#include "cute.h"
#include "ide_listener.h"
#include "cute_runner.h"

double pie;
class foo{
	int dd;
protected: 
	static void bar();
	//void boo();
public:
	foo(){std::cout<<"constructor empty\n";}
	foo(int d){dd=d;std::cout<<"constructor(int d)\n";}
	foo(double d,bool a);
	void func();
	void operator() (){std::cout<<"operator()\n";
		ASSERTM("operator()", false);
	}
	double operator() (int d){std::cout<<"operator(int d)\n";
		ASSERTM("operator()(int d)", false);
	}
	double cow(){return 0.2;};
	static double cow2(){return 0.1;};
	static void cow3(){ASSERTM("cow3()", false);};
	void cow4(){
		std::cout<<"cow4\n";
		ASSERTM("cow4()", false);
	};
	int poulet(){
		ASSERTM("poulet()", false);
		return 1;};
	double poulet(int d){return 0.1;};
};
struct aStruct{
	void helo(){ASSERTM("helo()", false);};
private:
	void myprivate(){ASSERTM("myprivate()", false);}
};
foo boo;
foo coo(5);
aStruct thatStruct;
union NumericType
{
    int         iValue;
    long        lValue;  
    double      dValue;  
};void newTestFunction(){
	ASSERTM("start writing tests", false);
}

void runSuite(){
	foo abc;	//constructor empty called
	cute::suite s;
	s.push_back(foo());
	s.push_back(foo(2.0));
	s.push_back(CUTE_SMEMFUN(foo,cow4));
	s.push_back(CUTE_MEMFUN(abc,foo,operator ()));
	s.push_back(CUTE_MEMFUN(thatStruct,aStruct,helo));
	s.push_back(CUTE_SMEMFUN(aStruct,helo));
	s.push_back(CUTE(newTestFunction));
	cute::ide_listener lis;
	cute::makeRunner(lis)(s, "The Suite");
}

int main(){
    runSuite();
    std::cout<<"complete";
}
//expected
[foo(func, operator (), cow, cow4, poulet), aStruct(helo), boo(func, operator (), cow4), coo(func, operator (), cow4), thatStruct(helo), abc(func, operator (), cow4)]
//test Tree instances without class struct
#include "cute.h"
#include "ide_listener.h"
#include "cute_runner.h"
#include "HJsuite.h"
class functor{
public:
	void operator()(){
		ASSERTM("start writing tests", false);
	}
};
functor myfunctor;
class jj{
};
void runTest(){
	cute::suite s=make_suite_HJsuite();
	s.push_back(functor());
	cute::ide_listener lis;
	cute::makeRunner(lis)(s, "The Suite");
}

int main(){
    runTest();
}
//expected
[functor(operator ()), myfunctor(operator ())]
//test Tree Empty
#include <iostream>
#include "cute.h"
#include "ide_listener.h"
#include "cute_runner.h"

void thisIsATest23a() {
	//int c=0;
	std::string str2="Now is the time1..." ;
	std::string str1( "Now is the\ttime..." );
	ASSERT_EQUALM("msg from asseration", str1,str2);
}
void thisIsAlsoATest22(){
	int cx=1,dx=1;
	ASSERT(cx==dx);
}


int testT(){
	return 1;
}

void runSuite(){
	cute::suite s;
	s.push_back(CUTE(thisIsAlsoATest22));
	s.push_back(CUTE(thisIsATest23a));
	
	
	//TODO add your test here



	cute::ide_listener lis;
	cute::makeRunner(lis)(s, "The Suite1112233");
}



int main(){
	std::cout<< "Testing the pathing"<<std::endl;
	runSuite();
}

//expected
[]
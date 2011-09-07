//test at end2 with pushback duplicated (checknameExist test)
//parameter 3
void runTest(){
	cute::suite s=make_suite_s();
	s.push_back(CUTE(newTestFunction));
	cute::ide_listener lis;
	cute::makeRunner(lis)(s, "The Suite");
}

int main(){
    runTest();
}
^
//expected results
void runTest(){
	cute::suite s=make_suite_s();
	s.push_back(CUTE(newTestFunction));
	cute::ide_listener lis;
	cute::makeRunner(lis)(s, "The Suite");
}

int main(){
    runTest();
}
void newTestFunction(){
	ASSERTM("start writing tests", false);
}


//test AddTest
//parameter 4
void newTestFunctio^n(){}
void runTest(){
	cute::suite s=make_suite_s();
	s.push_back(CUTE(newTestFunction));
	cute::ide_listener lis;
	cute::makeRunner(lis)(s, "The Suite");
}

int main(){
    runTest();
}

//expected results
void newTestFunction(){}
void runTest(){
	cute::suite s=make_suite_s();
	s.push_back(CUTE(newTestFunction));
	cute::ide_listener lis;
	cute::makeRunner(lis)(s, "The Suite");
}

int main(){
    runTest();
}

//test struct functor
//parameter 7
struct aStru^ctWithoutVisiblity{
	bool operator() (){ASSERTM("start writing tests", false);return false;}
};

void runTest(){
	cute::suite s=make_suite_s();
	s.push_back(aStructWithoutVisiblity());
	cute::ide_listener lis;
	cute::makeRunner(lis)(s, "The Suite");
}
//expected 
struct aStructWithoutVisiblity{
	bool operator() (){ASSERTM("start writing tests", false);return false;}
};

void runTest(){
	cute::suite s=make_suite_s();
	s.push_back(aStructWithoutVisiblity());
	cute::ide_listener lis;
	cute::makeRunner(lis)(s, "The Suite");
}
///////////////////////////////////////
//test addmember SMEMFUN pushback dup 
//parameter foo operator() C 11 
class foo{
public:
	void operator()(){
		std::cout<<"operator()\n";
		ASSERTM("operator()", false);
	};
};
void runSuite(){
	foo abc;
	cute::suite s;
	s.push_back(CUTE_SMEMFUN(foo,operator()));
	cute::ide_listener lis;
	cute::makeRunner(lis)(s, "The Suite");
}
//expected 
class foo{
public:
	void operator()(){
		std::cout<<"operator()\n";
		ASSERTM("operator()", false);
	};
};
void runSuite(){
	foo abc;
	cute::suite s;
	s.push_back(CUTE_SMEMFUN(foo,operator()));
	cute::ide_listener lis;
	cute::makeRunner(lis)(s, "The Suite");
}
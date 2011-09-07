///////////////////////////////////////
// notice all of class/struct doesnt end with semicolon, might be an issue
///////////////////////////////////////
//test addmember SMEMFUN 
//parameter foo cow4 C
class foo{
public:
	void cow4(){
		std::cout<<"cow4\n";
		ASSERTM("cow4()", false);
	};
}
void runSuite(){
	cute::suite s;
	cute::ide_listener lis;
	cute::makeRunner(lis)(s, "The Suite");
}
//expected 
class foo{
public:
	void cow4(){
		std::cout<<"cow4\n";
		ASSERTM("cow4()", false);
	};
}
void runSuite(){
	cute::suite s;
	s.push_back(CUTE_SMEMFUN(foo,cow4));
	cute::ide_listener lis;
	cute::makeRunner(lis)(s, "The Suite");
}
///////////////////////////////////////
//test addmember MEMFUN 
//parameter abc cow4 I
class foo{
public:
	void cow4(){
		std::cout<<"cow4\n";
		ASSERTM("cow4()", false);
	};
}
void runSuite(){
	foo abc;
	cute::suite s;
	cute::ide_listener lis;
	cute::makeRunner(lis)(s, "The Suite");
}
//expected 
class foo{
public:
	void cow4(){
		std::cout<<"cow4\n";
		ASSERTM("cow4()", false);
	};
}
void runSuite(){
	foo abc;
	cute::suite s;
	s.push_back(CUTE_MEMFUN(abc,foo,cow4));
	cute::ide_listener lis;
	cute::makeRunner(lis)(s, "The Suite");
}
///////////////////////////////////////
//test addmember MEMFUN operator 
//parameter abc operator() I
class foo{
public:
	void operator()(){
		std::cout<<"operator()\n";
		ASSERTM("operator()", false);
	};
}
void runSuite(){
	foo abc;
	cute::suite s;
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
}
void runSuite(){
	foo abc;
	cute::suite s;
	s.push_back(CUTE_MEMFUN(abc,foo,operator()));
	cute::ide_listener lis;
	cute::makeRunner(lis)(s, "The Suite");
}
///////////////////////////////////////
//test addmember SMEMFUN operator 
//parameter foo operator() C
class foo{
public:
	void operator()(){
		std::cout<<"operator()\n";
		ASSERTM("operator()", false);
	};
}
void runSuite(){
	foo abc;
	cute::suite s;
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
}
void runSuite(){
	foo abc;
	cute::suite s;
	s.push_back(CUTE_SMEMFUN(foo,operator()));
	cute::ide_listener lis;
	cute::makeRunner(lis)(s, "The Suite");
}
///////////////////////////////////////
//test addmember MEMFUN pushback dup 
//parameter abc operator() I
class foo{
public:
	void operator()(){
		std::cout<<"operator()\n";
		ASSERTM("operator()", false);
	};
}
void runSuite(){
	foo abc;
	cute::suite s;
	s.push_back(CUTE_MEMFUN(abc,foo,operator()));
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
}
void runSuite(){
	foo abc;
	cute::suite s;
	s.push_back(CUTE_MEMFUN(abc,foo,operator()));
	cute::ide_listener lis;
	cute::makeRunner(lis)(s, "The Suite");
}
///////////////////////////////////////
//test addmember SMEMFUN pushback dup 
//parameter foo operator() C
class foo{
public:
	void operator()(){
		std::cout<<"operator()\n";
		ASSERTM("operator()", false);
	};
}
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
}
void runSuite(){
	foo abc;
	cute::suite s;
	s.push_back(CUTE_SMEMFUN(foo,operator()));
	cute::ide_listener lis;
	cute::makeRunner(lis)(s, "The Suite");
}
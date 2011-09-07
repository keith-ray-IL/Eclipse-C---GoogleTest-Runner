#include "cute.h"
#include "ide_listener.h"
#include "cute_runner.h"
#include "$suitename$.h"

void thisIsATest() {
	ASSERTM("start writing tests", false);	
}

cute::suite make_suite_$suitename$(){
	cute::suite s;
	s.push_back(CUTE(thisIsATest));
	return s;
}




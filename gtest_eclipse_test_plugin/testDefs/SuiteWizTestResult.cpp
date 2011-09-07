#include "cute.h"
#include "ide_listener.h"
#include "cute_runner.h"
#include "theSuiteName.h"

void runSuite(){
	cute::suite s=make_suite_theSuiteName();
	
	cute::ide_listener lis;
	cute::makeRunner(lis)(s, "The Suite");
}

int main(){
    runSuite();
}




//test badlocation exception
#include "cute.h"
#include "ide_listener.h"
#include "cute_runner.h"
void runSuite(){
	cute::suite s;
	cute::ide_listener lis;
	cute::makeRunner(lis)(s, "The Suite");
}

int main(){
    runSuite();
}
^


^

//expected results


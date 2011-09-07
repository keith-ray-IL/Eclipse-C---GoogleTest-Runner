/*********************************************************************************
 * This file is part of CUTE.
 *
 * CUTE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * CUTE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with CUTE.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright 2007 Peter Sommerlad
 *
 *********************************************************************************/

#ifndef CUTE_EQUALS_H_
#define CUTE_EQUALS_H_

#include "cute_base.h"
#include <sstream>
#include <cmath>
#include <limits>

namespace cute {
// overload the following for your purpose of presenting a difference
// TODO: should I provide overloads for ExpectedValue == ActualValue?
	static inline std::string backslashQuoteTabNewline(std::string const &input){
		std::string result;
		result.reserve(input.size());
		for (std::string::size_type i=0; i < input.length() ; ++i){
			switch(input[i]) {
				case '\n': result += "\\n"; break;
				case '\t': result += "\\t"; break;
				case '\\': result += "\\\\"; break;
				case '\r': result += "\\r"; break;
				default: result += input[i];
			}
		}
		return result;

	}
	template <typename ExpectedValue, typename ActualValue>
	std::string diff_values(ExpectedValue const &expected
						,ActualValue const & actual){
		// construct a simple message...to be parsed by IDE support
		std::ostringstream exp;
		exp << expected;
		std::ostringstream act;
		act << actual;
		std::ostringstream os;
		os << " expected:\t" << cute::backslashQuoteTabNewline(exp.str())<<"\tbut was:\t"<<cute::backslashQuoteTabNewline(act.str())<<"\t";
		return os.str();
	}


// TODO: some magic might be possible with boost::mpl... leave that for the moment
	template <typename ExpectedValue, typename ActualValue>
	void assert_equal(ExpectedValue const &expected
				,ActualValue const &actual
				,char const *msg
				,char const *file
				,int line) {
					// should get rid of signed-unsigned warning below...
					// but this requires trickery or more overloading
		if (expected == actual) return;
		throw test_failure(cute::backslashQuoteTabNewline(msg) + diff_values(expected,actual),file,line);
	}
	template <typename ExpectedValue, typename ActualValue, typename DeltaValue>
	void assert_equal_delta(ExpectedValue const &expected
				,ActualValue const &actual
				,DeltaValue const &delta
				,char const *msg
				,char const *file
				,int line) {
		if (!(std::abs(delta)  < std::abs(expected-actual))) return;
		throw test_failure(cute::backslashQuoteTabNewline(msg) + diff_values(expected,actual),file,line);
	}
// TODO: provide this for float as well. (and combinations?)
	template <>
	inline
	void assert_equal_delta<double,double,double>(double const &expected
				,double const &actual
				,double const &delta
				,char const *msg
				,char const *file
				,int line) {
		if (!(std::abs(delta) < std::abs(expected-actual))  ) return;
		throw test_failure(cute::backslashQuoteTabNewline(msg) + diff_values(expected,actual),file,line);
	}

	template <>
	inline
	void assert_equal<double,double>(double const &expected
				,double const &actual
				,char const *msg
				,char const *file
				,int line) {
		// allow for one digit error on equality, apps might need different...
		// and should use assert_equal_delta
		const double delta=10*std::numeric_limits<double>::epsilon()*expected;
		assert_equal_delta(expected,actual,delta,msg,file,line);
	}
}

#define ASSERT_EQUALM(msg,expected,actual) cute::assert_equal((expected),(actual),msg,__FILE__,__LINE__)
#define ASSERT_EQUAL(expected,actual) ASSERT_EQUALM(#expected " == " #actual, expected,actual)
#define ASSERT_EQUAL_DELTAM(msg,expected,actual,delta) cute::assert_equal_delta((expected),(actual),(delta),msg,__FILE__,__LINE__)
#define ASSERT_EQUAL_DELTA(expected,actual,delta) ASSERT_EQUAL_DELTAM(#expected " == " #actual " with error " #delta  ,expected,actual,delta)
#endif /*CUTE_EQUALS_H_*/

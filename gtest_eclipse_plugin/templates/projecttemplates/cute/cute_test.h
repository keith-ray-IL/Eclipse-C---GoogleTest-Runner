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
 * Copyright 2008 Peter Sommerlad
 *
 *********************************************************************************/

#ifndef CUTE_TEST_H_
#define CUTE_TEST_H_
#include <boost/function.hpp>
// make plain functions as tests more 'cute':
namespace cute {
	struct test{
		void operator()()const{ theTest(); }
		std::string name()const{ return name_;}

		// this shouldn't belong here, but where?
		// needs adaptation for different compilers
		// dependency to demangle is a given,
		// otherwise we have to use macros everywhere
		static std::string demangle(char const *name);

		// (real) functor types can (almost) spell their name
		// but a name can also be given explicitely, e.g. for CUTE() macro
		// for simple test functions
		template <typename VoidFunctor>
		test(VoidFunctor const &t, std::string name = demangle(typeid(VoidFunctor).name()))
		:theTest(t),name_(name){}

	private:
		boost::function<void()> theTest;
		std::string name_;
	};
#define CUTE(name) cute::test((&name),(#name))

	// TODO: provide platform independent means of demangling,
	// or at least support for different compilers
	// this is platform dependant for gnu compilers
#ifdef __GNUG__
#include <cxxabi.h> // __cxa_demangle
	inline std::string test::demangle(char const *name){
		if (!name) return "unknown";
		char *toBeFreed = __cxxabiv1::__cxa_demangle(name,0,0,0);
		std::string result(toBeFreed?toBeFreed:name);
		::free(toBeFreed);
		return result;
	}

#else
	// this default works reasonably with MSVC71 and 8
	inline std::string test::demangle(char const *name){
		return std::string(name?name:"unknown");
	}
#endif

}
#endif /*CUTE_TEST_H_*/

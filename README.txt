GTest Result Viewer

	This is a plugin for Eclipse to run C++ unit tests written with 
	the Google C++ test framework (http://code.google.com/p/googletest/).
	
	Industrial Logic (http://industriallogic.com) makes this plugin available
	to students doing exercises in IL's C++ eLearning albums. 

History:

	This code was branched off from the Cute Eclipse Plugin several years
	ago. http://cute-test.com/projects/cute/wiki

Compatibility: 

	This version of the plugin is currently compatible with 
	Helios (Eclipse 3.6) and Galileo (Eclipse 3.5).
	
	TODO: revise plugin for compatibility with Indigo (Eclipse 3.7).

Build Instructions:

	As per other Eclipse plugins (TODO: explain further).

Installation Instructions:

	As per other Eclipse plugins (TODO: explain further).

Running Tests:

	TODO: explain further

Running Instructions:

	Given a "C++ makefile" project in Eclipse, do the following:
	
	1. In Eclipse, change to the C/C++ Perspective by selecting 
		Window | Open Perspective | C/C++
	
	2. Build the project by selecting the project and clicking the 
		toolbar "Build" icon.
	
	3. Then right-click the project in the navigator and choose 
		"Run As… / Run Configurations…".
	
	4. In the dialog that appears, double-click on "GTest" to create a 
		new Run Configuration. (If you don't see GTest in the list of possible 
		Run Configurations, you must have some installation or compatibility 
		problem.)
	
	5. The new "Run Configuration" should appear that has the same name as 
		the project.
	
	6. In the "Main" tab in the dialog, fill out the relative pathname of the 
		executable. For example "bin/YourProjectNameTests" or (on Windows)
		"bin/YourProjectNameTests.exe". When the correct name is entered, the 
		"Apply" button will become enabled. But don't click it yet!
	
	7. Click the "Common" tab, and in the "Display Favorites Menu" list, 
		click the checkbox for "Run".
	
	8. Click "Apply" and "Run". If "Run" button is disabled because of a 
		"not a recognized executable" warning message, click "Apply" and 
		then "Close".
	
	Now you can build the project and run the tests when you pop-up the 
		Run toolbar menu item.
	
	You may also, in the GTest Viewer pane toolbar, click on "Rerun Test" 
		icon, to build the exercise and run its tests.
	

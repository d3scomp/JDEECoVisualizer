----------To run the application:----------

1) 	Make sure the following software is installed:
	- JRE (Java Runtime), version 7 or later 
	- JavaFX 2.2 or later (included in the JRE since version 7 update 6 
	by default, for earlier updates of JRE7 it must be installed separately)
	
2)	Make sure that you have the JavaFX library (jfxrt.jar) on the class path. 
	In JRE8 it is done automatically, but in JRE7 it has to be done manually.
	In that case, just append the full path to jfxrt.jar to the CLASSPATH
	system variable.

3) 	On Windows, run start_windows.bat , located in the bin/ directory.
	On Linux, run start_linux.sh , located in the bin/ directory.
	
----------To re-build the application:----------

Note: 
Re-building is not required unless you have changed the source code and want
the changes to reflect in the application.

1)	Make sure the following software is installed: 
	- JDK (Java Development Kit), version 7 or later
	- Apache Ant 1.8.2 or later
	- JavaFX 2.2 or later (included in the JRE since version 7 update 6 
	by default, for earlier updates of JRE7 it must be installed separately)
	
2)	Make sure that you have the JavaFX library (jfxrt.jar) on the class path. 
	In JRE8 it is done automatically, but in JRE7 it has to be done manually.
	In that case, just append the full path to jfxrt.jar to the CLASSPATH
	system variable.

3)	In the application direcotory, run the following command: ant
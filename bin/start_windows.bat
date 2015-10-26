cd %~dp0
cd ..
if Not Exist dist/JDEECoVisualizer.jar (
	call ant -buildfile build.xml just_dist
)
call java -cp "dist/JDEECoVisualizer.jar;plugins/*;%CLASSPATH%" cz.filipekt.jdcv.Visualizer
basedir=$(dirname $0)
cd $basedir
cd ..
if [ ! -f dist/JDEECoVisualizer.jar ]; then
    ant -buildfile build.xml just_dist
fi
java -cp "dist/JDEECoVisualizer.jar:plugins/*:$CLASSPATH" cz.filipekt.jdcv.Visualizer
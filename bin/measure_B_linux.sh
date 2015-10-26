basedir=$(dirname $0)
cd $basedir
cd ..
if [ ! -f dist/JDEECoVisualizer.jar ]; then
    ant -buildfile build.xml just_dist
fi
java -cp "dist/JDEECoVisualizer.jar:plugins/*:$CLASSPATH" -Xmx1200M cz.filipekt.jdcv.measuring.MeasureInputProcessing
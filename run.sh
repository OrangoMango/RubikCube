rm -r bin/*
cd src
echo "compiling..."
javac -d ../bin --module-path $1 --add-modules javafx.controls com/orangomango/rubik/MainApplication.java
cd ../bin
echo "executing..."
java -Dprism.forceGPU=true --module-path $1 --add-modules javafx.controls com.orangomango.rubik.MainApplication

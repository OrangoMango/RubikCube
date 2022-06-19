rm -r bin/*
cd src
javac -d ../bin --module-path $1 --add-modules javafx.controls com/orangomango/rubik/MainApplication.java
cd ../bin
java -Dprism.forceGPU=true --module-path $1 --add-modules javafx.controls com.orangomango.rubik.MainApplication

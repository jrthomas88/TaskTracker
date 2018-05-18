NODE=./src/dataStructure/TaskNode.java
GUI=./src/Controller/TaskTrackerGUI.java

all: tasknode gui jar

gui: tasknode
	javac ${NODE} ${GUI}
	cp ./src/Controller/*.class ./src/class/

tasknode:
	javac ${NODE}
	cp ./src/dataStructure/*.class ./src/class/

jar: gui tasknode
	cp ./src/Controller/*.class ./Controller/
	cp ./src/dataStructure/*.class ./dataStructure/
	jar cvfm TaskTracker.jar manifest.txt ./dataStructure/*.class ./Controller/*.class
	chmod +x TaskTracker.jar

clean:
	rm -f src/class/* TaskTracker.jar ./dataStructure/* ./Controller/* *.class

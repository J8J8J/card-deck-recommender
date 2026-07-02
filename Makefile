JAVAC=javac
JAVA=java

SOURCES=Main.java DeckData.java DeckRecommender.java Test.java

build:
	$(JAVAC) $(SOURCES)

run: build
	$(JAVA) Main

test: build
	$(JAVA) Test

clean:
	rm -f *.class

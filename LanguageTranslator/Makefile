JLEX = ./tools/jlex.jar
LEXER_DIR = lexer
# the input file for jlex
LEXER_INPUT = lexer/XMLLexer
# The generated lexer source file
LEXER_SOURCE = lexer/XMLLexer.java

# Driver program
DRIVER = LTDriver.java

# Directory which stores executable files
EXE_DIR = ./bin

all: LTDriver.class

LTDriver.class : XMLLexer.java $(DRIVER)
	javac $(LEXER_SOURCE) $(DRIVER)

XMLLexer.java : $(LEXER_INPUT)
	java -jar $(JLEX) $(LEXER_INPUT)

clean:
	rm $(LEXER_DIR)/*.class $(LEXER_DIR)/symbol/*.class \
	$(LEXER_SOURCE)

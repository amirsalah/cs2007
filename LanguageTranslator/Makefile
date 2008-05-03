JLEX = ./tools/jlex.jar
LEXER_DIR = src/lexer
# the input file for jlex
LEXER_INPUT = src/lexer/XMLLexer
# The generated lexer source file
LEXER_SOURCE = src/lexer/XMLLexer.java

# the Yytoken class
YYTOKEN = src/lexer/Yytoken.java
# symbol class
SYMBOL = src/lexer/symbol/sym.java
# exception class
EXCEPTION = src/exception

# parser
PARSER = src/parser/XMLParser.java

# Driver program
DRIVER = src/LTDriver.java

# Directory which stores executable files
EXE_DIR = ./bin

all: LTDriver.class

LTDriver.class : XMLLexer.java $(DRIVER)
	javac $(LEXER_SOURCE) $(DRIVER) $(YYTOKEN) $(SYMBOL) $(PARSER) $(EXCEPTION)/RecognitionException.java

XMLLexer.java : $(LEXER_INPUT)
	java -jar $(JLEX) $(LEXER_INPUT)

clean:
	rm *.class $(LEXER_DIR)/*.class $(LEXER_DIR)/symbol/*.class \
	$(LEXER_SOURCE)

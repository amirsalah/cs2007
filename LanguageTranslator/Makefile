JLEX = ./tools/jlex.jar
LEXER_DIR = lexer
# the input file for jlex
LEXER_INPUT = lexer/XMLLexer.lex
# The generated lexer source file
LEXER_SOURCE = lexer/XMLLexer.lex.java

# Directory which stores executable files
EXE_DIR = ./bin

all: XMLLexer.class

XMLLexer.class : XMLLexer.lex.java
	javac $(LEXER_SOURCE)

XMLLexer.lex.java : $(LEXER_INPUT)
	java -jar $(JLEX) $(LEXER_INPUT)

clean:
	rm $(LEXER_DIR)/*.class $(LEXER_DIR)/symbol/*.class \
	$(LEXER_SOURCE)

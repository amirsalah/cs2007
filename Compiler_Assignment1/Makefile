XMLLexer.class : XMLLexer.lex.java
	javac XMLLexer.lex.java

XMLLexer.lex.java : XMLLexer.lex
	java -jar jlex.jar XMLLexer.lex

clean:
	rm Yylex.class Yytoken.class Utility.class XMLLexer.lex.java \
	XMLLexer.class

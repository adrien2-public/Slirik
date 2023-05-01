package core;

import statements.*;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Parser {

    // List of the Lexer generated tokens from the source code
    private final List<Lexer.Token> tokens;

    // Queue of generated bytecode statements
    private Queue<Statement> statements = new LinkedList<>();

    // Current index of the given tokens
    private int index = 0;

    protected Parser(List<Lexer.Token> tokens) {
        this.tokens = tokens;
    }

    /**
     * @return a new Queue of generated bytecode statements based on the tokens given to the Parser object
     */
    protected Queue<Statement> getStatements() {
        statements.add(new Directory("global"));
        statements.add(new Type("int"));
        statements.add(new Operation("+"));

        // Loop through tokens and decide which operation should happen to generate a bytecode statement for each token
        for (; index < tokens.size(); index++) identifyToken();
        return new Validator(statements).getValidatedStatements();
    }

    // Executes an action for adding statements based on token
    private void identifyToken() {
        switch (tokens.get(index).tokenType()) {
            case TYPE -> whenType();
            case IDENTIFIER -> whenIdentifier();
            case EQUALS -> whenEquals();
            case NUMBER -> whenNumber();
            case BINARY_OPERATOR -> whenBinaryOperation();
            case OPEN_PAREN -> whenOpenParen();
            case CLOSE_PAREN -> whenCloseParen();
            case END -> whenEnd();
            case KEYWORD -> whenKeyWord();
        }
    }

    private void whenType() {
        List<Lexer.Token> expressionTokens = new LinkedList<>();
        for (; tokens.get(index).tokenType() != Lexer.TokenType.END; index++) {
            expressionTokens.add(tokens.get(index));
        }
        expressionTokens.add(tokens.get(index));

        statements.addAll(Trees.typeTree(expressionTokens.iterator()));
    }

    private void whenIdentifier() {
        List<Lexer.Token> expressionTokens = new LinkedList<>();
        for (; tokens.get(index).tokenType() != Lexer.TokenType.END; index++) {
            expressionTokens.add(tokens.get(index));
        }
        expressionTokens.add(tokens.get(index));

        statements.addAll(Trees.identifierTree(expressionTokens.iterator()));
    }

    private void whenEquals() {
        throw new IllegalArgumentException("Illegal start of statement");
    }

    private void whenNumber() {
        throw new IllegalArgumentException("Illegal start of statement");
    }

    private void whenBinaryOperation() {
        throw new IllegalArgumentException("Illegal start of statement");
    }

    private void whenOpenParen() {
        throw new IllegalArgumentException("Illegal start of statement");
    }

    private void whenCloseParen() {
        throw new IllegalArgumentException("Illegal start of statement");
    }

    private void whenEnd() {
        throw new IllegalArgumentException("Illegal start of statement");
    }

    private void whenKeyWord() {
        List<Lexer.Token> expressionTokens = new LinkedList<>();

        boolean foundBody = false;
        int closedParensAvailable = 0;
        for (; closedParensAvailable != 0 || closedParensAvailable == 0 && !foundBody; index++) {
            Lexer.Token token = tokens.get(index);
            expressionTokens.add(token);

            if (token.tokenType() == Lexer.TokenType.OPEN_PAREN) {
                closedParensAvailable++;
                foundBody = true;
            } else if (token.tokenType() == Lexer.TokenType.CLOSE_PAREN)
                closedParensAvailable--;
        }
        boolean haveElse = false;
        if (index < tokens.size() && tokens.get(index).value().equals("else")) {
            haveElse = true;
        }

        index--;

        statements.addAll(Trees.keywordTree(expressionTokens.iterator()));

        if (haveElse) {
            Deque<Statement> deque = new LinkedList<>(statements);
            deque.removeLast();
            deque.addLast(new End("next"));
            statements = new LinkedList<>(deque);
        }
    }
}
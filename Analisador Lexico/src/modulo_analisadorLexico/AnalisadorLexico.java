package modulo_analisadorLexico;

import java.util.ArrayList;

/**
 * Classe destinada à análise léxica do código fonte.
 *
 * @author Lucas Carneiro
 * @author Oto Lopes
 *
 * @see Token
 * @see AnalisadorLexico
 */
public class AnalisadorLexico {

    /**
     *
     */
    private static final char EOF = '\0';
    /**
     *
     */
    private final EstruturaLexica estruturaLexica;
    /**
     *
     */
    private final ArrayList<Token> tokens;
    /**
     *
     */
    private final ArrayList<String> erros;
    /**
     *
     */
    private int linha;
    /**
     *
     */
    private int coluna;
    /**
     *
     */
    private ArrayList<String> codigo;
    /**
     *
     */
    private boolean linhaVazia;

    /**
     *
     */
    public AnalisadorLexico() {

        this.estruturaLexica = new EstruturaLexica();
        this.tokens = new ArrayList<>();
        this.erros = new ArrayList<>();
        this.coluna = 0;
        this.linha = 0;

        this.linhaVazia = false;
    }

    /**
     *
     * @return
     */
    public ArrayList<Token> getTokens() {
        return this.tokens;
    }

    /**
     *
     * @return
     */
    public ArrayList<String> getErros() {
        return this.erros;
    }

    /**
     *
     * @param tipo
     */
    public void novoErro(String tipo) {

        this.erros.add("\nCódigo com erro, " + tipo + " na linha " + (linha + 1) + " coluna " + (coluna + 1));
    }

    /**
     *
     * @return
     */
    public char novoChar() {

        char c[] = this.codigo.get(linha).toCharArray();
        if (c.length == this.coluna) {
            return ' ';
        } else if (c.length > this.coluna) {
            return c[coluna];
        } else if (this.codigo.size() > (this.linha + 1)) {
            this.linha++;
            c = this.codigo.get(this.linha).toCharArray();
            this.coluna = 0;
            // Caso uma linha não tenha absolutamente nada, apenas um "enter".
            if (c.length == 0) {
                this.linhaVazia = true;
                return '©'; // Enviar qualquer, tanto faz, não vai ser lido mesmo.
            }
            //
            return c[this.coluna];
        } else {
            //fim de arquivo
            return EOF;
        }
    }

    /**
     *
     * @param codigo
     */
    public void analise(ArrayList<String> codigo) {

        String token;
        this.codigo = codigo;

        char ch = this.novoChar();
        while (ch != EOF) {
            if (!this.linhaVazia) {
                token = "";
                if (Character.isSpaceChar(ch)) {
                    this.coluna++;
                } else if (Character.isLetter(ch)) { // Verifica se é um identificador.
                    this.identificador(token, ch);
                } else if (ch == '\'') { // Verifica se é uma cadeia constante.
                    this.caracterConstante(token, ch);
                } else if (Character.isDigit(ch)) { // Verifica se é número
                    this.numero(token, ch);
                } else if (estruturaLexica.ehOperador(ch)) { // Verifica se é operador
                    this.operador(token, ch);
                } else if (this.estruturaLexica.ehDelimitador(ch)) { // Verifica se é delimitador.
                    token = token + ch;
                    Token tk = new Token(token, "Delimitador", this.linha, this.coluna);
                    this.tokens.add(tk);
                    this.coluna++;
                } else { //Simbolos invalidos
                    this.novoErro("Simbolo invalido");
                    while (!(ch == EOF || Character.isSpaceChar(ch) || this.estruturaLexica.ehDelimitador(ch) || this.estruturaLexica.ehOperador(ch))) {
                        token = token + ch;
                        this.coluna++;
                        ch = this.novoChar();
                    }
                }
            } else {
                this.linhaVazia = false;
                this.linha++;
            }
            ch = this.novoChar();
        }
    }

    public void identificador(String token, char ch) {

        token = token + ch;
        boolean error = false;
        this.coluna++;
        ch = this.novoChar();
        System.out.println(ch + token + this.coluna + " " + this.linha);
        //percorre enquanto houver letras, digitos ou _
        while (!(ch == EOF || Character.isSpaceChar(ch) || this.estruturaLexica.ehDelimitador(ch) || this.estruturaLexica.ehOperador(ch))) {
            if (!(Character.isLetter(ch) || Character.isDigit(ch) || ch == '_')) {
                error = true;
            }
            token = token + ch;
            this.coluna++;
            ch = this.novoChar();
        }
        //Apos consumir letras digitos e simbolos verifica se o token esta correto
        if (!error) {
            Token tk;
            //verifica se eh uma palavra reservada
            if (this.estruturaLexica.ehPalavraReservada(token)) {
                tk = new Token(token, "Palavra Reservada", this.linha, this.coluna);
            } else {
                tk = new Token(token, "Identificador", this.linha, this.coluna);
            }
            this.tokens.add(tk);
        } else {
            this.novoErro("identificador mal formado");
        }
        System.out.println(" token :" + token);

    }

    public void numero(String lexema, char ch) {

        lexema += ch;
        boolean error = false;
        this.coluna++;
        int qtdPonto = 0;
        ch = this.novoChar();
        while (!(ch == EOF || Character.isSpaceChar(ch) || this.estruturaLexica.ehDelimitador(ch) || (this.estruturaLexica.ehOperador(ch) && ch != '.'))) {
            if (ch == '.') {
                qtdPonto++;
            }
            if (!Character.isDigit(ch) || qtdPonto > 1) {
                error = true;
            }
            lexema += ch;
            this.coluna++;
            ch = this.novoChar();
        }
        if (!error && !(lexema.charAt(lexema.length()-1) == '.')) {
            Token tk;
            tk = new Token(lexema, "Numero", this.linha, this.coluna);
            this.tokens.add(tk);
        } else {
            this.novoErro("Numero mal formado");
        }

        System.out.println("tok atu: " + lexema);
    }

    public void cadeiaConstante(String token, char ch) {

    }

    public void caracterConstante(String token, char ch) {

        token = token + ch;
        boolean error = false;
        this.coluna++;
        int cont = 0;
        ch = this.novoChar();
        while (ch != '\'' && ch != EOF) {
            if (!(Character.isLetter(ch) || Character.isDigit(ch)) || cont > 0) {
                error = true;
            }
            cont++;
            token = token + ch;
            this.coluna++;
            ch = this.novoChar();
        }
        if (!error && cont != 0) {
            Token tk;
            token = token + ch;
            this.coluna++;
            tk = new Token(token, "Caracter Constante", this.linha, this.coluna);
            this.tokens.add(tk);
        } else {
            this.novoErro("caracter constante mal formado");
        }
    }

    public void operador(String lexema, char ch) {

        int linhaInicial = this.linha - 1;
        int colunaInicial = this.coluna - 1;

        lexema += ch;
        boolean error = false;
        this.coluna++;
        int qtdOpe = 0;
        ch = this.novoChar();
        while (estruturaLexica.ehOperador(ch)) {
            this.coluna++;
            qtdOpe++;
            if (lexema.equals(".")) {
                error = true;
                lexema += ch;
            } else if (lexema.equals("+")) {
                if (ch != '+') {
                    error = true;
                }
                lexema += ch;
            } else if (lexema.equals("-")) {
                if (ch != '-') {
                    error = true;
                }
                lexema += ch;
            } else if (lexema.equals("*")) {
                error = true;
                lexema += ch;
            } else if (lexema.equals("/")) {
                if (ch == '/' || ch == '*') {
                    //lexema.substring(0, lexema.length()-1);
                    //this.comentario(ch);
                } else {
                    error = true;
                }
            } else if (!lexema.equals("/") && (ch == '/' || ch == '*')) {
                //ch = novoChar();
                //lexema.substring(0, lexema.length()-1);
                //this.comentario(ch);
                
            } else if (lexema.equals("=") || lexema.equals("!") || lexema.equals("<") || lexema.equals(">")) {
                if (ch != '=') {
                    error = true;
                }
                lexema += ch;
            } else if (lexema.equals("&")) {
                if (ch != '&') {
                    error = true;
                }
                lexema += ch;
            } else if (lexema.equals("|")) {
                if (ch != '|') {
                    error = true;
                }
                lexema += ch;
            }

            ch = this.novoChar();
        }

        if (Character.isDigit(ch) && lexema.equals("-")) {
            this.numero(lexema, ch);
            return;
        }
        if (!error && qtdOpe <= 2) {
            Token tk;
            tk = new Token(lexema, "Operador", linhaInicial, colunaInicial);
            this.tokens.add(tk);
        } else {
            this.novoErro("Operador Inexistente");
        }
    }

    public void comentario(char ch) {

        linha = this.linha;
        boolean saiuBloco = false;

        if (ch == '/') {
            while (linha == this.linha || ch != EOF) {
                this.coluna++;
                ch = novoChar();
            }
        } else if (ch == '*') {
            while (ch != EOF && !saiuBloco) {
                this.coluna++;
                ch = novoChar();
                if (ch == '*') {
                    this.coluna++;
                    ch = novoChar();
                    if (ch == '/') {
                        this.coluna++;
                        saiuBloco = true;
                    }
                }
            }
        }
    }
}

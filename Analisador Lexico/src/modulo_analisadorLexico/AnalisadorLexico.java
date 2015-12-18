package modulo_analisadorLexico;

import java.util.ArrayList;
import modulo_completo.TabelaSimbolos;

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
    private TabelaSimbolos tabelaSimbolos;
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
    private boolean podeSerNumero;

    public AnalisadorLexico() {

        this.estruturaLexica = new EstruturaLexica();
        this.tabelaSimbolos = new TabelaSimbolos();
        this.tokens = new ArrayList<>();
        this.erros = new ArrayList<>();
        this.coluna = 0;
        this.linha = 0;

        this.linhaVazia = false;
        this.podeSerNumero = true;
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
     * Método destinado a padronização dos erros.
     *
     * @param tipo
     */
    public void novoErro(String tipo) {

        this.erros.add("\nCódigo com erro, " + tipo + " na linha " + (linha + 1) + " coluna " + coluna);
    }

    /**
     *
     * @return
     */
    public char novoChar() {

        if (!codigo.isEmpty()) {
            char c[] = this.codigo.get(linha).toCharArray();
            if (c.length == this.coluna) {
                return ' ';
            } else if (c.length > this.coluna) {
                return c[coluna];
            } else if (this.codigo.size() > (this.linha + 1)) {
                this.linha++;
                c = this.codigo.get(this.linha).toCharArray();
                this.coluna = 0;

                if (c.length == 0) { // Caso uma linha não tenha absolutamente nada, apenas um "enter".
                    this.linhaVazia = true;
                    return '©'; // Enviar qualquer coisa, tanto faz, não vai ser lido mesmo.
                }
                //
                return c[this.coluna];
            } else {
                //fim de arquivo
                return EOF;
            }
        } else {
            return EOF;
        }
    }

    /**
     *
     * @param codigo
     */
    public void analise(ArrayList<String> codigo) {

        this.codigo = codigo;
        String lexema;

        char ch = this.novoChar();
        while (ch != EOF) {
            if (!this.linhaVazia) {
                lexema = "";

                if (Character.isSpaceChar(ch)) {
                    this.coluna++;
                } else if (estruturaLexica.ehLetra(ch)) { // Verifica se é um identificador.
                    this.identificador(lexema, ch);
                } else if (ch == '\'') { // Verifica se é um caracter constante.
                    this.caracterConstante(lexema, ch);
                } else if (ch == '"') { //Verifica se é cadeia constante
                    this.cadeiaConstante(lexema, ch);
                } else if (estruturaLexica.ehDigito(ch)) { // Verifica se é número
                    this.numero(lexema, ch);
                } else if (estruturaLexica.ehOperador(ch)) { // Verifica se é operador
                    this.operador(lexema, ch);
                } else if (this.estruturaLexica.ehDelimitador(ch)) { // Verifica se é delimitador.
                    lexema = lexema + ch;
                    Token tk = new Token(lexema, "Delimitador", this.linha, this.coluna);
                    this.tokens.add(tk);
                    this.coluna++;
                } else { //Simbolos invalidos
                    this.simboloInvalido(lexema, ch);
                }
            } else {
                this.linhaVazia = false;
                this.linha++;
            }

            if (podeSerNumero && (estruturaLexica.ehOperador(ch) || estruturaLexica.ehDelimitador(ch))) {
                podeSerNumero = true;
            } else if (estruturaLexica.ehLetra(ch) || estruturaLexica.ehDigito(ch)) {
                podeSerNumero = false;
            }

            ch = this.novoChar();
        }
    }

    public void identificador(String lexema, char ch) {

        int linhaInicial = this.linha;
        int colunaInicial = this.coluna;

        lexema = lexema + ch;
        boolean error = false;
        this.coluna++;
        ch = this.novoChar();
        System.out.println(ch + lexema + this.coluna + " " + this.linha);
        //percorre enquanto houver letras, digitos ou _
        while (!(ch == EOF || Character.isSpaceChar(ch) || this.estruturaLexica.ehDelimitador(ch) || this.estruturaLexica.ehOperador(ch))) {
            if (!(estruturaLexica.ehLetra(ch) || estruturaLexica.ehDigito(ch) || ch == '_')) {
                error = true;
            }
            lexema = lexema + ch;
            this.coluna++;
            ch = this.novoChar();
        }
        //Apos consumir letras digitos e simbolos verifica se o token esta correto
        if (!error) {
            Token tk;
            //verifica se eh uma palavra reservada
            if (this.estruturaLexica.ehPalavraReservada(lexema)) {
                tk = new Token(lexema, "Palavra Reservada", linhaInicial, colunaInicial);
            } else {
                tk = new Token(lexema, "Identificador", linhaInicial, colunaInicial);
            }
            this.tokens.add(tk);
        } else {
            this.novoErro("identificador mal formado");
        }
        System.out.println(" token :" + lexema);

    }

    public void numero(String lexema, char ch) {

        int linhaInicial = this.linha;
        int colunaInicial = this.coluna;

        lexema += ch;
        boolean error = false;
        this.coluna++;
        int qtdPonto = 0;
        ch = this.novoChar();
        while (!(ch == EOF || Character.isSpaceChar(ch) || this.estruturaLexica.ehDelimitador(ch) || (this.estruturaLexica.ehOperador(ch) && ch != '.'))) {
            if (ch == '.') {
                qtdPonto++;
            }
            if (!estruturaLexica.ehDigito(ch) || qtdPonto > 1) {
                error = true;
            }
            lexema += ch;
            this.coluna++;
            ch = this.novoChar();
        }
        if (!error && !(lexema.charAt(lexema.length() - 1) == '.')) {
            Token tk;
            tk = new Token(lexema, "Numero", linhaInicial, colunaInicial);
            this.tokens.add(tk);
        } else {
            this.novoErro("Numero mal formado");
        }

        System.out.println("tok atu: " + lexema);
    }

    public void cadeiaConstante(String lexema, char ch) {

        int linhaInicial = this.linha;
        int colunaInicial = this.coluna;

        lexema = lexema + ch;
        boolean error = false;
        this.coluna++;
        int l = linha;
        ch = this.novoChar();
        while (ch != '"' && ch != EOF && l == linha) {
            if (!estruturaLexica.ehSimbolo(ch)) {
                error = true;
            }
            lexema = lexema + ch;
            this.coluna++;
            ch = this.novoChar();
        }
        if (!error && l == linha) {
            Token tk;
            lexema = lexema + ch;
            this.coluna++;
            tk = new Token(lexema, "Cadeia Constante", linhaInicial, colunaInicial);
            this.tokens.add(tk);
        } else {
            this.novoErro("cadeia constante mal formado");
        }
    }

    public void caracterConstante(String lexema, char ch) {

        int linhaInicial = this.linha;
        int colunaInicial = this.coluna;

        lexema = lexema + ch;
        boolean error = false;
        this.coluna++;
        int cont = 0, l = linha;
        ch = this.novoChar();
        while (ch != '\'' && ch != EOF && l == linha) {
            if (!(estruturaLexica.ehLetra(ch) || estruturaLexica.ehDigito(ch)) || cont > 0) {
                error = true;
            }
            cont++;
            lexema = lexema + ch;
            this.coluna++;
            ch = this.novoChar();
        }
        if (!error && cont != 0 && l == linha) {
            Token tk;
            lexema = lexema + ch;
            this.coluna++;
            tk = new Token(lexema, "Caracter Constante", linhaInicial, colunaInicial);
            this.tokens.add(tk);
        } else {
            this.novoErro("caracter constante mal formado");
        }
    }

    public void operador(String lexema, char ch) {

        int linhaInicial = this.linha;
        int colunaInicial = this.coluna;
        boolean error = false;

        lexema += ch;
        this.coluna++;
        if (ch == '.' || ch == '*') {
            //kbou faz mais nada.
        } else if (ch == '+') {
            ch = this.novoChar();
            if (ch == '+') {
                lexema += ch;
                this.coluna++;
            }

        } else if (ch == '-') {
            ch = novoChar();
            if (ch == '-') {
                lexema += ch;
                this.coluna++;
            } else if (this.podeSerNumero) {
                while (Character.isSpaceChar(ch) && linhaInicial == this.linha) {
                    this.coluna++;
                    ch = novoChar();
                }
                if (estruturaLexica.ehDigito(ch)) {
                    this.podeSerNumero = false;
                    this.numero(lexema, ch);
                    return;
                }
            }

        } else if (ch == '/') {
            ch = this.novoChar();
            if (ch == '/' || ch == '*') {
                this.comentario(lexema + ch);
                return;
            }

        } else if (ch == '=' || ch == '>' || ch == '<') {
            ch = novoChar();
            if (ch == '=') {
                lexema += ch;
                this.coluna++;
            }

        } else if (ch == '!') {
            ch = this.novoChar();
            if (ch == '=') {
                lexema += ch;
                this.coluna++;
            } else {
                if (!Character.isSpaceChar(ch)) {
                    lexema += ch;
                    this.coluna++;
                }
                error = true;
            }

        } else if (ch == '&' || ch == '|') {
            ch = this.novoChar();
            if (ch == lexema.charAt(0)) {
                lexema += ch;
                this.coluna++;
            } else {
                if (!Character.isSpaceChar(ch)) {
                    lexema += ch;
                    this.coluna++;
                }
                error = true;
            }

        }
        if (lexema.equals("++") || lexema.equals("--")) {
            this.podeSerNumero = false;
        } else {
            this.podeSerNumero = true;
        }

        if (!error) {
            Token tk;
            tk = new Token(lexema, "Operador", linhaInicial, colunaInicial);
            this.tokens.add(tk);
        } else {
            this.novoErro("Operador Inexistente");
        }
    }

    public void comentario(String coment) {

        int linhaA = this.linha;
        boolean saiuBloco = false;
        this.coluna++;
        char ch = novoChar();

        if (coment.equals("//")) {
            while (linhaA == this.linha && ch != EOF) {
                this.coluna++;
                ch = novoChar();
            }
        } else if (coment.equals("/*")) {
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

    private void simboloInvalido(String lexema, char ch) {

        this.novoErro("Simbolo invalido");
        while (!(ch == EOF || Character.isSpaceChar(ch) || this.estruturaLexica.ehDelimitador(ch) || this.estruturaLexica.ehOperador(ch))) {
            lexema = lexema + ch;
            this.coluna++;
            ch = this.novoChar();
        }
    }
}

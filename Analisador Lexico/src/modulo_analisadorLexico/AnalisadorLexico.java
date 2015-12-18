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
    private ArrayList<Token> tabelaSimbolos;

    private boolean linhaVazia;
    private boolean ultimoFoiDecremento;
    private boolean ultimoFoiMenos;

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
        this.ultimoFoiDecremento = false;
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

        this.erros.add("\nCódigo com erro, " + tipo + " na linha " + (linha + 1) + " coluna " + (coluna + 1));
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
                //....
                this.ultimoFoiDecremento = false;
                this.ultimoFoiMenos = false;

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
        Character ch_anterior = null;

        char ch = this.novoChar();
        while (ch != EOF) {
            if (!this.linhaVazia) {
                lexema = "";
                if (Character.isSpaceChar(ch)) {
                    this.coluna++;
                } else if (estruturaLexica.ehLetra(ch)) { // Verifica se é um identificador.
                    this.identificador(lexema, ch);
                } else if (ch == '\'') { // Verifica se é uma cadeia constante.
                    this.caracterConstante(lexema, ch);
                } else if (ch == '"') {
                    this.cadeiaConstante(lexema, ch);
                } else if (estruturaLexica.ehDigito(ch)) { // Verifica se é número
                    this.numero(lexema, ch);
                } else if (estruturaLexica.ehOperador(ch)) { // Verifica se é operador
                    this.operador(lexema, ch, ch_anterior);
                } else if (this.estruturaLexica.ehDelimitador(ch)) { // Verifica se é delimitador.
                    lexema = lexema + ch;
                    Token tk = new Token(lexema, "Delimitador", this.linha, this.coluna);
                    this.tokens.add(tk);
                    this.coluna++;
                } else { //Simbolos invalidos
                    this.novoErro("Simbolo invalido");
                    while (!(ch == EOF || Character.isSpaceChar(ch) || this.estruturaLexica.ehDelimitador(ch) || this.estruturaLexica.ehOperador(ch))) {
                        lexema = lexema + ch;
                        this.coluna++;
                        ch = this.novoChar();
                    }
                }
            } else {
                this.linhaVazia = false;
                this.linha++;
            }

            ch_anterior = ch;
            ch = this.novoChar();
        }
    }

    public void identificador(String lexema, char ch) {

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
                tk = new Token(lexema, "Palavra Reservada", this.linha, this.coluna);
            } else {
                tk = new Token(lexema, "Identificador", this.linha, this.coluna);
            }
            this.tokens.add(tk);
            this.tabelaSimbolos.add(tk);
        } else {
            this.novoErro("identificador mal formado");
        }
        System.out.println(" token :" + lexema);

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
            if (!estruturaLexica.ehDigito(ch) || qtdPonto > 1) {
                error = true;
            }
            lexema += ch;
            this.coluna++;
            ch = this.novoChar();
        }
        if (!error && !(lexema.charAt(lexema.length() - 1) == '.')) {
            Token tk;
            tk = new Token(lexema, "Numero", this.linha, this.coluna);
            this.tokens.add(tk);
        } else {
            this.novoErro("Numero mal formado");
        }

        System.out.println("tok atu: " + lexema);
    }

    public void cadeiaConstante(String lexema, char ch) {
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
            tk = new Token(lexema, "Cadeia Constante", this.linha, this.coluna);
            this.tokens.add(tk);
        } else {
            this.novoErro("cadeia constante mal formado");
        }
    }

    public void caracterConstante(String lexema, char ch) {

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
            tk = new Token(lexema, "Caracter Constante", this.linha, this.coluna);
            this.tokens.add(tk);
        } else {
            this.novoErro("caracter constante mal formado");
        }
    }

    public void operador(String lexema, char ch, Character ch_anterior) {

        int linhaInicial = this.linha;
        int colunaInicial = this.coluna - 1;
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

        } else if (ch == '-' && this.ultimoFoiMenos) {  
            ch = novoChar();
            while (Character.isSpaceChar(ch) && linhaInicial == this.linha) {
                this.coluna++;
                ch = novoChar();
            }
            if (estruturaLexica.ehDigito(ch)) {
                this.ultimoFoiMenos = false;
                this.numero(lexema, ch);
                return;
            }

        } else if (ch == '-' && !this.ultimoFoiMenos) {
            ch = novoChar();
            if (ch_anterior == null) {
                if (ch == '-') {
                    lexema += ch;
                    this.coluna++;
                    this.ultimoFoiDecremento = true;
                } else if (estruturaLexica.ehDigito(ch)) {
                    this.numero(lexema, ch);
                    return;
                }
            } else if (ultimoFoiDecremento) {
                this.ultimoFoiDecremento = false;
                while (Character.isSpaceChar(ch)) {
                    this.coluna++;
                    ch = novoChar();
                }
                if (estruturaLexica.ehDigito(ch)) {
                    this.numero(lexema, ch);
                    return;
                }

            } else if () {

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
        if (!error) {
            Token tk;
            tk = new Token(lexema, "Operador", linhaInicial, colunaInicial);
            this.tokens.add(tk);
        } else {
            this.novoErro("Operador Inexistente");
        }
    }

    public void comentario(String coment) {

        int linha = this.linha;
        boolean saiuBloco = false;
        this.coluna++;
        char ch = novoChar();

        if (coment.equals("//")) {
            while (linha == this.linha || ch != EOF) {
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
}

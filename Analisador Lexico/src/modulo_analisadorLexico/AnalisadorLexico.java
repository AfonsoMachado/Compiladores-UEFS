package modulo_analisadorLexico;

import java.util.ArrayList;
import javax.swing.JOptionPane;
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
    private static final char QUEBRA_LINHA = ' ';
    private static final char LINHA_VAZIA = '@';
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
     * @param erro
     * @param linhaInicial
     * @param colunaInicial
     */
    public void novoErro(String tipo, String erro, int linhaInicial, int colunaInicial) {

        this.erros.add("\nCódigo com erro, " + erro + " " + tipo +  " na linha " + (linhaInicial+1) + " coluna " + (colunaInicial+1));
    }

    /**
     *
     * @return
     */
    public char novoChar() {

        if (!this.codigo.isEmpty()) {
            char c[] = this.codigo.get(this.linha).toCharArray();
            if (c.length == this.coluna) { 
                return QUEBRA_LINHA;
            } else if (c.length > this.coluna) {
                return c[coluna];
            } else if (this.codigo.size() > (this.linha + 1)) {
                this.linha++;
                podeSerNumero=true;
                c = this.codigo.get(this.linha).toCharArray();
                this.coluna = 0;

                if (c.length == 0) { // Caso uma linha não tenha absolutamente nada, apenas um "enter".
                    this.linhaVazia = true;
                    return LINHA_VAZIA; // Enviar qualquer coisa, tanto faz, não vai ser lido mesmo.
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
            this.novoErro("Identificador mal formado", lexema, linhaInicial, colunaInicial);
        }
        System.out.println(" token :" + lexema);

    }

    public void numero(String lexema, char ch) {

        int linhaInicial = this.linha;
        int colunaInicial = this.coluna;

        if (lexema.equals("-")) {
            colunaInicial--;
        }
            
        lexema += ch;
        boolean error = false;
        this.coluna++;
        ch = this.novoChar();

        while (estruturaLexica.ehDigito(ch) || estruturaLexica.ehLetra(ch)) {
            if (estruturaLexica.ehLetra(ch)) {
                error = true;
            }
            lexema += ch;
            this.coluna++;
            ch = this.novoChar();
        }
        if (ch == '.') {
            this.coluna++;
            ch = this.novoChar();
            if (!estruturaLexica.ehDigito(ch)) {
                if (!error) {
                    Token tk;
                    tk = new Token(lexema, "Número", linhaInicial, colunaInicial);
                    this.tokens.add(tk);
                } else {
                    this.novoErro("Número mal formado", lexema, linhaInicial, colunaInicial);
                }
                
                this.podeSerNumero = true;
                Token tk2;
                tk2 = new Token(".", "Operador", linhaInicial, this.coluna);
                this.tokens.add(tk2);
                return;

            } else { // Tem número depois do ponto.
                if (!error) {
                    lexema += "."+ch;
                    this.coluna++;
                    ch = this.novoChar();
                    while (estruturaLexica.ehDigito(ch) || estruturaLexica.ehLetra(ch)) {
                        if (estruturaLexica.ehLetra(ch)) {
                            error = true;
                        }
                        lexema += ch;
                        this.coluna++;
                        ch = this.novoChar();
                    }
                } else {
                    this.novoErro("Número mal formado", lexema, linhaInicial, colunaInicial);
                 
                    this.podeSerNumero = true;
                    Token tk2;
                    tk2 = new Token(".", "Operador", linhaInicial, this.coluna);
                    this.tokens.add(tk2);
                    return;
                }
            }
        }
        if (!error) {
            Token tk;
            tk = new Token(lexema, "Número", linhaInicial, colunaInicial);
            this.tokens.add(tk);
        } else {
            this.novoErro("Número mal formado", lexema, linhaInicial, colunaInicial);
        }
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
            tk = new Token(lexema, "Cadeia constante", linhaInicial, colunaInicial);
            this.tokens.add(tk);
        } else {
            this.novoErro("Cadeia constante mal formada", lexema, linhaInicial, colunaInicial);
        }
    }

    public void caracterConstante(String lexema, char ch) {

        int linhaInicial = this.linha;
        int colunaInicial = this.coluna;

        lexema = lexema + ch;
        boolean error = false;
        this.coluna++;
        int cont = 0, l = this.linha;
        ch = this.novoChar();
        while (ch != '\'' && ch != EOF && l == this.linha) {
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
            tk = new Token(lexema, "Caracter constante", linhaInicial, colunaInicial);
            this.tokens.add(tk);
        } else {
            if(ch == '\''){
                lexema = lexema + ch;
                this.coluna++;
            
            }
            this.novoErro("Caracter constante mal formado", lexema, linhaInicial, colunaInicial);
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
            this.novoErro("Operador Inexistente", lexema, linhaInicial, colunaInicial);
        }
    }

    public void comentario(String coment) {

        int colunaInicial = this.coluna;
        int linhaInicial = this.linha;
        
        boolean saiuBloco = false;
        this.coluna++;
        char ch = novoChar();

        if (coment.equals("//")) {
            while (linhaInicial == this.linha && ch != EOF) {
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
            
            if (!saiuBloco) {
                this.novoErro("Comentário não finalizado", "###Comentário não impresso###", linhaInicial, colunaInicial);
            }
        }
    }

    private void simboloInvalido(String lexema, char ch) {

        int linhaInicial = this.linha;
        int colunaInicial = this.coluna;
///////////////////////////////////////
        if (ch == 9) {
            System.out.println("TAB");
            this.coluna++;
            return ;
        }
///////////////////////////////////////        
        while (!(ch == EOF || Character.isSpaceChar(ch) || this.estruturaLexica.ehDelimitador(ch) || this.estruturaLexica.ehOperador(ch))) {
            lexema = lexema + ch;
            this.coluna++;
            ch = this.novoChar();
        }

        this.novoErro("Símbolo inválido", lexema, linhaInicial, colunaInicial);
    }
}

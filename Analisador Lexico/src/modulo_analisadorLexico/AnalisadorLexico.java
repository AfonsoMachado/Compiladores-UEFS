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

    private static final char EOF = '\0';

    private final EstruturaLexica estruturaLexica;
    private final ArrayList<Token> tokens;
    private final ArrayList<String> erros;
    private int linha, coluna;
    private ArrayList<String> codigo;

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

    public ArrayList<Token> getTokens() {
        return this.tokens;
    }

    public ArrayList<String> getErros() {
        return this.erros;
    }

    public void novoErro(String tipo) {

        this.erros.add("\nCódigo com erro, " + tipo + " na linha " + (linha + 1) + " coluna " + (coluna + 1));
    }

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
            System.out.println("aq");
            return EOF;
        }
    }

    public void analise(ArrayList<String> codigo) {

        String token;
        this.codigo = codigo;

        char ch = novoChar();
        while (ch != EOF) {
            if (!linhaVazia) {
                token = "";
                if (Character.isSpaceChar(ch)) {
                    this.coluna++;
                } //Verifica se eh um identificador
                else if (Character.isLetter(ch)) {
                    identificador(token, ch);
                } else if (this.estruturaLexica.ehDelimitador(ch)) {
                    token = token + ch;
                    Token tk = new Token(token, "Delimitador", this.linha, this.coluna);
                    this.tokens.add(tk);
                    this.coluna++;
                } //Simbolos invalidos
                else {
                    novoErro("Simbolo invalido");
                }
            } else {
                linhaVazia = false;
                linha++;
            }
            ch = novoChar();
        }
    }

    public void identificador(String token, char ch) {

        token = token + ch;
        boolean error = false;
        this.coluna++;
        ch = novoChar();
        System.out.println(ch + token + this.coluna + " " + this.linha);
        //percorre enquanto houver letras digitos ou _
        while (!(ch == EOF || Character.isSpaceChar(ch) || this.estruturaLexica.ehDelimitador(ch) || this.estruturaLexica.ehOperador(ch))) {
            if (!(Character.isLetter(ch) || Character.isDigit(ch) || ch == '_')) {
                error = true;
            }
            token = token + ch;
            this.coluna++;
            ch = novoChar();
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
        } //indentificador com erro
        else {
            novoErro("identificador mal formado");
        }
        System.out.println(" token :" + token);

    }

}

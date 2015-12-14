package modulo_analisadorLexico;

import java.util.ArrayList;


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author UCHIHA
 */
public class AnalisadorLexico {
    
    private static final char EOF = '\0';

    private final EstruturaLexica estruturaLexica = new EstruturaLexica();
    private final ArrayList<Token> tokens;
    private final ArrayList<String> erros;
    private int linha, coluna;
    private ArrayList<String> codigo;

    /**
     *
     */
    public AnalisadorLexico() {
        tokens = new ArrayList<>();
        erros = new ArrayList<>();
        coluna = 0;
        linha = 0;
    }

    public ArrayList<Token> getTokens() {
        return tokens;
    }

    public ArrayList<String> getErros() {
        return erros;
    }

    public void novoErro(String tipo) {
        erros.add("\nCódigo com erro, " + tipo + " na linha " + (linha+1) + " coluna " + (coluna+1));
    }

    public char novoChar() {
        char c[] = codigo.get(linha).toCharArray();
        if (c.length == coluna) {
            return ' ';
        } else if (c.length > coluna) {
            return c[coluna];
        } else if (codigo.size() > linha + 1) {
            linha++;
            c = codigo.get(linha).toCharArray();
            coluna = 0;
            return c[coluna];
        } else {
            //fim de arquivo
            System.out.println("aq");
            return EOF;
        }

    }
    
    public void identificador(String token, char ch){
        
                token = token + ch;
                boolean error = false;
                coluna++;
                ch = novoChar();
                System.out.println(ch + token + coluna + " " + linha);
                //percorre enquanto houver letras digitos ou _
                while(!(ch == EOF || Character.isSpaceChar(ch) || estruturaLexica.ehDelimitador(ch) || estruturaLexica.ehOperador(ch))){
                    if(!(Character.isLetter(ch) || Character.isDigit(ch) || ch == '_')){
                        error = true;
                    }
                    token = token + ch;
                    coluna++;
                    ch = novoChar();
                }
                //Apos consumir letras digitos e simbolos verifica se o token esta correto
                if(!error){
                    Token tk;
                    //verifica se eh uma palavra reservada
                    if (estruturaLexica.ehPalavraReservada(token)) {
                        tk = new Token(token, "Palavra Reservada", linha);
                    } else {
                        tk = new Token(token, "Identificador", linha);
                    }
                    tokens.add(tk);
                } //indentificador com erro
                else {
                    novoErro("identificador mal formado");
                }
                System.out.println(" token :" + token);
    
    }
    
    public void analise(ArrayList<String> codigo) {

        String token;
        this.codigo = codigo;

        char ch = novoChar();
        while (ch != EOF) {
            token = "";
            if (Character.isSpaceChar(ch)) {
                coluna++;
            } //Verifica se eh um identificador
            else if (Character.isLetter(ch)) {
               identificador(token, ch);
            } else if (estruturaLexica.ehDelimitador(ch)) {
                token = token + ch;
                Token tk = new Token(token, "Delimitador", linha);
                tokens.add(tk);
                coluna++;
            }
            //Simbolos invalidos
            else {
                novoErro("Simbolo invalido");
            }
            ch = novoChar();
        }

    }
}

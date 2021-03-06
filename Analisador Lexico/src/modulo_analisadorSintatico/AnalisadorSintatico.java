/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modulo_analisadorSintatico;

import java.util.ArrayList;
import modulo_analisadorLexico.Token;
import modulo_completo.Compilador;
import modulo_completo.Simbolos;

/**
 * Classe responsável pela análise sintatica dos códigos fontes. Os metodos de
 * reconhecimento sao baseados nas produçoes da gramatica. A gramatica utilizada
 * foi a versao 5.4.
 *
 * @author Lucas Carneiro
 * @author Oto Lopes
 *
 * @see Token
 * @see Compilador
 */
public class AnalisadorSintatico {

    private Token proximo;              //token atual em análise
    private ArrayList<Token> tokens;    //lista com os tokens recebidos
    private ArrayList<String> erros;    //lista com os erros encontrados na análise.
    private ArrayList<String> errosSemanticos;    //lista com os erros encontrados na análise.
    private int contTokens = 0;         //contador que aponta para o proximo token da lista
    private Simbolos escopo;            //salvar o escopo atual da tabela de simbolos
    private Simbolos atual;             //simbolo atual
    private final Simbolos globalEscopo;             //simbolo global
    private Simbolos classeEscopo;             //simbolo classe
    private boolean eBool = false;

    /**
     * Construtor do analisador Sintatico.
     * @param escopo recebe a tabela de simbolos do compilador
     */
    public AnalisadorSintatico(Simbolos escopo) {
        this.escopo = escopo; //recebe a tabela de simbolos do compilador
        this.globalEscopo = escopo;
    }

    /**
     * Metodo responsavel pela analise sintatica dos codigos fontes.
     * @param tokens lista com os tokens vindos do lexico.
     */
    public void analise(ArrayList<Token> tokens) {
        this.tokens = tokens; //recebe os tokens vindos do lexico.
        proximo = proximo();  //recebe o primeiro token da lista
        erros = new ArrayList<>(); //cria a lista de erros
        errosSemanticos = new ArrayList<>(); //cria a lista de erros
        recArquivo();   //inicia a analise do arquivo
    }

    /**
     * Método que retorna os erros encontrados durante a análise sintatica.
     *
     * @return lista de erros sintaticos encontrados.
     */
    public ArrayList<String> getErros() {
        return erros;
    }

    /**
     * Metodo para capturar o proximo token para ser analisado.
     *
     * @return o proximo token da lista de tokens.
     *
     */
    private Token proximo() {
        if (contTokens < tokens.size()) { //verifica se ainda possuem tokens para a analise.
            return tokens.get(contTokens++);
        } else {
            return new Token("EOF", "EOF", 0, 0);  //cria um token de fim de arquivo. 
        }
    }

    /**
     * Metodo para normalizaçao dos erros encontrados.
     */
    private void erroSintatico(String erro) {
        if (!proximo.getValor().equals("EOF")) {
            erros.add(proximo.getLinha() + " " + erro + "\n"); //gera o erro normalizado e adiciona na lista de erros.
        } else {
            erros.add(erro);
        }
    }

    /**
     * Metodo para normalizaçao dos erros semanticos encontrados.
     */
    private void erroSemantico(String erro) {
        if (!proximo.getValor().equals("EOF")) {
            errosSemanticos.add(proximo.getLinha() + " " + erro + "\n"); //gera o erro normalizado e adiciona na lista de erros.
        } else {
            errosSemanticos.add(erro);
        }
    }

    /**
     * Metodo para normalizaçao dos erros semanticos encontrados.
     */
    private void erroSemantico(String erro, int linha) {
        if (!proximo.getValor().equals("EOF")) {
            errosSemanticos.add(linha + " " + erro + "\n"); //gera o erro normalizado e adiciona na lista de erros.
        } else {
            errosSemanticos.add(erro);
        }
    }

    /**
     * Metodo para verificar terminais.
     *
     * @param esperado valor do terminal esperado no proximo token.
     */
    private void terminal(String esperado) {
        if ((!proximo.getValor().equals("EOF")) && proximo.getValor().equals(esperado)) { //verifica se o token atual e o que era esperado
            proximo = proximo();
        } else {
            erroSintatico("falta " + esperado); //gera o erro se o token nao e o esperado 
        }
    }

    /**
     * Metodo para verificar os tipos.
     *
     * @param esperado valor do tipo esperado no proximo token.
     */
    private void Tipo(String esperado) {
        if (!proximo.getValor().equals("EOF") && proximo.getTipo().equals(esperado)) { //verifica se o tipo do token atual e o que era esperado
            proximo = proximo();
        } else {
            erroSintatico("falta " + esperado); //gera o erro se o tipo do token nao e o esperado 
        }
    }

    /**
     * Metodo para o reconhecimento do arquivo.
     */
    private void recArquivo() {
        recConstantes(); //reconhece as constante
        recVariaveis(); //reconhece as variaveis
        recPreMain();  //reconhece as classes e o metodo main
    }

    /**
     * Metodo para reconhecimento de classes antes do metodo main e do metodo
     * main.
     */
    private void recPreMain() {
        if (!proximo.getValor().equals("EOF")) {
            switch (proximo.getValor()) {
                case "void":  //verifica se e uma classe ou o metodo main
                    recMain();
                    recClasses();
                    break;
                case "class":
                    recClasse();
                    recPreMain();
                    break;
                default:
                    while (!proximo.getValor().equals("void") && !proximo.getValor().equals("class")) { //recuperaçao de erro, busca uma classe ou main no arquivo
                        erroSintatico("falta palavra reservada: class, void");
                        proximo = proximo();
                    }
                    recPreMain();
                    break;
            }
        } else {
            erroSintatico("falta palavra reservada: class, void");
        }
    }

    /**
     * Metodo para reconhecimento de varias classes.
     */
    private void recClasses() {
        switch (proximo.getValor()) {
            case "class":
                recClasse();
                recClasses();
                break;
            default:
                break;
        }
    }

    /**
     * Metodo para verificar varias constantes.
     */
    private void recConstantes() {
        switch (proximo.getValor()) {
            case "const":
                recConst();
                recConstantes();
                break;
            default:
                break;
        }
    }

    /**
     * Metodo para reconhecimento de variaveis.
     */
    private void recVariaveis() {
        atual = new Simbolos();  //criaçao de um simbolo para adicionar na tabela de simbolos.
        atual.setCategoria(Simbolos.VAR); //salva a categoria do simbolo.
        switch (proximo.getValor()) {
            case "char":
                atual.setTipo(Simbolos.CHAR);  //salva o tipo do simbolo.
                recDeclaracaoVariavel();
                recVariaveis();
                break;
            case "int":
                atual.setTipo(Simbolos.INT); //salva o tipo do simbolo.
                recDeclaracaoVariavel();
                recVariaveis();
                break;
            case "bool":
                atual.setTipo(Simbolos.BOOL); //salva o tipo do simbolo.
                recDeclaracaoVariavel();
                recVariaveis();
                break;
            case "string":
                atual.setTipo(Simbolos.STRING); //salva o tipo do simbolo.
                recDeclaracaoVariavel();
                recVariaveis();
                break;
            case "float":
                atual.setTipo(Simbolos.FLOAT); //salva o tipo do simbolo.
                recDeclaracaoVariavel();
                recVariaveis();
                break;
            default:
                break;
        }
    }

    /**
     * Metodo para reconhecimento da main.
     */
    private void recMain() {
        atual = new Simbolos(); //cria um simbolo.
        atual.setCategoria(Simbolos.MAIN); //salva a categoria
        atual.setNome("MAIN");
        if (escopo.contains(atual.getNome())) {
            erroSemantico("main ja declarada");
        } else {
            escopo.addFilho(atual); //adiciona a main na tabela de simbolos
        }
        Simbolos anterior = escopo; //salva o escopo atual para retorna ao sair do metodo
        escopo = atual;
        terminal("void");
        terminal("main");
        terminal("(");
        terminal(")");
        terminal("{");
        recConteudoMetodo();
        terminal("}");
        escopo = anterior;
    }

    /**
     * Metodo para reconhecimento de classe.
     */
    private void recClasse() {
        switch (proximo.getValor()) {
            case "class":
                atual = new Simbolos();
                atual.setCategoria(Simbolos.CLASS);
                terminal("class");
                atual.setNome(proximo.getValor());
                if (escopo.contains(atual.getNome())) {
                    erroSemantico("identificador ja declarado");
                } else {
                    escopo.addFilho(atual);
                }
                Simbolos anterior = escopo; //salva o antigo escopo
                Tipo("id");
                recExpressaoHerenca();
                classeEscopo = atual;
                escopo = atual; //o novo escopo e a classe atual
                terminal("{");
                recConteudoClasse();
                terminal("}");
                escopo = anterior; //volta o escopo para o pai da classe.
                break;
            default:
                erroSintatico("Classe com erro.");
                break;
        }
    }

    /**
     * Metodo para reconhecimento de herança.
     */
    private void recExpressaoHerenca() {
        switch (proximo.getValor()) {
            case ">":
                terminal(">");
                if (proximo.getValor().equals(atual.getNome())) {
                    erroSemantico("uma classe nao pode herdar dela mesma");
                } else if (!escopo.contains(proximo.getValor())) {
                    erroSemantico("uma classe so pode herdar de outra declarada anteriormente");
                } else if (escopo.contains(proximo.getValor()) && escopo.getFilho(proximo.getValor()) != null && escopo.getFilho(proximo.getValor()).getCategoria() != Simbolos.CLASS) {
                    erroSemantico("uma classe so pode herdar de outra classe");
                } else {
                    atual.setPai(escopo.getFilho(proximo.getValor()));
                }
                Tipo("id");
                break;
            default:
                break;
        }

    }

    /**
     * Metodo para reconhecimento de conteudo de classe.
     */
    private void recConteudoClasse() {
        switch (proximo.getValor()) {
            case "const":
                recConst();
                recConteudoClasse();
                break;
            case "void":
                recIdDeclaracao();
                recConteudoClasse();
                break;
            default:
                if (proximo.getTipo().equals("palavra_reservada") || proximo.getTipo().equals("id")) {
                    recIdDeclaracao();
                    recConteudoClasse();
                    break;
                } else if (!proximo.getValor().equals("}") && !proximo.getValor().equals("class")) { //recuperaçao do erro, verifica se acabou o bloco, ou surgiu outra classe
                    erroSintatico("falta declaraçao de variavel ou de metodo");
                    proximo = proximo();
                    recConteudoClasse();
                }
                break;
        }
    }

    private void recConst() {
        switch (proximo.getValor()) {
            case "const":
                terminal("const");
                terminal("{");
                recBlocoConstantes();
                terminal("}");
                break;
            default:
                erroSintatico("Esperava um bloco de contantes");
                break;
        }
    }

    private void recBlocoConstantes() {
        atual = new Simbolos();
        atual.setCategoria(Simbolos.CONST);
        switch (proximo.getValor()) {
            case "char":
                terminal("char");
                atual.setTipo(Simbolos.CHAR);
                recListaConst();
                break;
            case "int":
                terminal("int");
                atual.setTipo(Simbolos.INT);
                recListaConst();
                break;
            case "bool":
                terminal("bool");
                atual.setTipo(Simbolos.BOOL);
                recListaConst();
                break;
            case "string":
                terminal("string");
                atual.setTipo(Simbolos.STRING);
                recListaConst();
                break;
            case "float":
                terminal("float");
                atual.setTipo(Simbolos.FLOAT);
                recListaConst();
                break;
            default:
                if (!proximo.getValor().equals("}")) {
                    erroSintatico("falta palavra reservada: int, char, bool, string, float");
                    proximo = proximo();
                    recBlocoConstantes();
                }
                break;
        }
    }

    private void recListaConst() {
        atual.setNome(proximo.getValor());
        Tipo("id");
        terminal("=");
        recAtribuicaoConstante();
        recAuxiliarDeclaracao();

    }

    private void recAtribuicaoConstante() {
        switch (proximo.getTipo()) {
            case "numero":
                if (atual.getTipo() != Simbolos.INT && atual.getTipo() != Simbolos.FLOAT) {
                    erroSemantico("tipos incompatives");
                } else if (atual.getTipo() == Simbolos.INT && proximo.getValor().contains(".")) {
                    erroSemantico("tipos incompatives, nao pode converter int para float");
                } else if (atual.getTipo() == Simbolos.FLOAT && !proximo.getValor().contains(".")) {
                    erroSemantico("tipos incompatives, nao pode converter float para int");
                }

                Tipo("numero");
                break;
            case "cadeia_constante":
                if (atual.getTipo() != Simbolos.STRING) {
                    erroSemantico("tipos incompatives");
                }
                Tipo("cadeia_constante");
                break;
            case "caractere_constante":
                if (atual.getTipo() != Simbolos.CHAR) {
                    erroSemantico("tipos incompatives");
                }
                Tipo("caractere_constante");
                break;
            default:
                if (atual.getTipo() != Simbolos.BOOL) {
                    erroSemantico("tipos incompatives");
                }
                switch (proximo.getValor()) {
                    case "true":
                        terminal("true");
                        break;
                    case "false":
                        terminal("false");
                        break;
                    default:
                        erroSintatico("falta numero, cadeia constante, caracter constante ou boolean");
                        break;
                }
                break;
        }
    }

    private void recAuxiliarDeclaracao() {
        switch (proximo.getValor()) {
            case ",":
                terminal(",");
                int aux = atual.getTipo();
                String obj = atual.getObjectNome();
                if (escopo.contains(atual.getNome())) {
                    erroSemantico("identificador ja declarado");
                } else if (globalEscopo.containsConst(atual.getNome()) || (classeEscopo != null && classeEscopo.containsConst(atual.getNome()))) {
                    erroSemantico("constante nao pode ser alterada");
                } else {
                    escopo.addFilho(atual);
                }
                atual = new Simbolos();
                atual.setCategoria(Simbolos.CONST);
                atual.setTipo(aux);
                atual.setObjectNome(obj);
                recListaConst();
                break;
            case ";":
                if (escopo.contains(atual.getNome())) {
                    erroSemantico("identificador ja declarado");
                } else if (globalEscopo.containsConst(atual.getNome()) || (classeEscopo != null && classeEscopo.containsConst(atual.getNome()))) {
                    erroSemantico("constante nao pode ser alterada");
                }else {
                    escopo.addFilho(atual);
                }
                terminal(";");
                recBlocoConstantes();
                break;
            default:
                erroSintatico("falta , ou ;");
                break;
        }
    }

    private void recDeclaracaoVariavel() {
        switch (proximo.getValor()) {
            case "char":
                terminal("char");
                atual.setNome(proximo.getValor());
                Tipo("id");
                recListaVariavel();
                break;
            case "int":
                terminal("int");
                atual.setNome(proximo.getValor());
                Tipo("id");
                recListaVariavel();
                break;
            case "bool":
                terminal("bool");
                atual.setNome(proximo.getValor());
                Tipo("id");
                recListaVariavel();
                break;
            case "string":
                terminal("string");
                atual.setNome(proximo.getValor());
                Tipo("id");
                recListaVariavel();
                break;
            case "float":
                terminal("float");
                atual.setNome(proximo.getValor());
                Tipo("id");
                recListaVariavel();
                break;
            default:
                break;
        }
    }

    private void recIdDeclaracao() {
        atual = new Simbolos();
        switch (proximo.getValor()) {
            case "void":
                terminal("void");
                atual.setCategoria(Simbolos.MET);
                atual.setTipo(Simbolos.VOID);
                atual.setNome(proximo.getValor());
                Simbolos anterior = escopo;
                escopo = atual;
                int linha = proximo.getLinha();
                Tipo("id");
                terminal("(");
                recDeclParametros();
                terminal(")");
                if (anterior.contains(escopo.getNome())) {
                    if (!anterior.isOverload(escopo)) {
                        erroSemantico("identificador ja declarado", linha);
                    } else {
                        anterior.addFilho(escopo);
                    }
                } else if (globalEscopo.containsConst(escopo.getNome())) {
                    erroSemantico("constante nao pode ser alterada", linha);
                } else {
                    anterior.addFilho(escopo);
                }
                terminal("{");
                recConteudoMetodo();
                terminal("}");
                escopo = anterior;
                break;
            case "char":
                terminal("char");
                atual.setTipo(Simbolos.CHAR);
                atual.setNome(proximo.getValor());
                if(escopo.contains(atual.getNome())){
                    erroSemantico("identificador ja declarado");
                }else if (globalEscopo.containsConst(atual.getNome()) || (classeEscopo != null && classeEscopo.containsConst(atual.getNome()))) {
                    erroSemantico("constante nao pode ser alterada");
                }
                Tipo("id");
                recCompId();
                break;
            case "int":
                terminal("int");
                atual.setTipo(Simbolos.INT);
                atual.setNome(proximo.getValor());
                if(escopo.contains(atual.getNome())){
                    erroSemantico("identificador ja declarado");
                }else if (globalEscopo.containsConst(atual.getNome()) || (classeEscopo != null && classeEscopo.containsConst(atual.getNome()))) {
                    erroSemantico("constante nao pode ser alterada");
                }
                Tipo("id");
                recCompId();
                break;
            case "bool":
                terminal("bool");
                atual.setTipo(Simbolos.BOOL);
                atual.setNome(proximo.getValor());
                if(escopo.contains(atual.getNome())){
                    erroSemantico("identificador ja declarado");
                }else if (globalEscopo.containsConst(atual.getNome()) || (classeEscopo != null && classeEscopo.containsConst(atual.getNome()))) {
                    erroSemantico("constante nao pode ser alterada");
                }
                Tipo("id");
                recCompId();
                break;
            case "string":
                terminal("string");
                atual.setTipo(Simbolos.STRING);
                atual.setNome(proximo.getValor());
                if(escopo.contains(atual.getNome())){
                    erroSemantico("identificador ja declarado");
                }else if (globalEscopo.containsConst(atual.getNome()) || (classeEscopo != null && classeEscopo.containsConst(atual.getNome()))) {
                    erroSemantico("constante nao pode ser alterada");
                }
                Tipo("id");
                recCompId();
                break;
            case "float":
                terminal("float");
                atual.setTipo(Simbolos.FLOAT);
                atual.setNome(proximo.getValor());
                if(escopo.contains(atual.getNome())){
                    erroSemantico("identificador ja declarado");
                }else if (globalEscopo.containsConst(atual.getNome()) || (classeEscopo != null && classeEscopo.containsConst(atual.getNome()))) {
                    erroSemantico("constante nao pode ser alterada");
                }
                Tipo("id");
                recCompId();
                break;
            default:
                if (proximo.getTipo().equals("id")) {
                    atual.setTipo(Simbolos.OBJECT);
                    atual.setObjectNome(proximo.getValor());
                    if (globalEscopo.contains(proximo.getValor())) {
                        if (globalEscopo.getFilho(proximo.getValor()).getCategoria() != Simbolos.CLASS) {
                            erroSemantico("classe nao declarada");
                        }
                    } else {
                        erroSemantico("classe nao declarada");
                    }
                    Tipo("id");
                    atual.setNome(proximo.getValor());
                    Tipo("id");
                    recCompId();
                    break;
                } else {
                    erroSintatico("espera um tipo: id, int, float, char, string, bool, void");
                }
                break;
        }
    }

    private void recCompId() {
        switch (proximo.getValor()) {
            case "[":
                atual.setCategoria(Simbolos.VET);
                terminal("[");
                recIndice();
                terminal("]");
                recListaVetor();
                break;
            case "(":
                atual.setCategoria(Simbolos.MET);
                Simbolos anterior = escopo;
                escopo = atual;
                int linha = proximo.getLinha();
                terminal("(");
                recDeclParametros();
                terminal(")");
                if (anterior.contains(escopo.getNome())) {
                    if (!anterior.isOverload(escopo)) {
                        erroSemantico("identificador ja declarado", linha);
                    } else {
                        anterior.addFilho(escopo);
                    }
                } else if (globalEscopo.containsConst(escopo.getNome())) {
                    erroSemantico("constante nao pode ser alterada", linha);
                } else {
                    anterior.addFilho(escopo);
                }
                terminal("{");
                recConteudoMetodo();
                terminal("return");
                recRetorno();
                terminal("}");
                escopo = anterior;
                break;
            case ",":
                atual.setCategoria(Simbolos.VAR);
                recListaVariavel();
                break;
            case ";":
                atual.setCategoria(Simbolos.VAR);
                if (escopo.contains(atual.getNome())) {
                    erroSemantico("identificador ja declarado");
                } else if (globalEscopo.containsConst(atual.getNome()) || (classeEscopo != null && classeEscopo.containsConst(atual.getNome()))) {
                    erroSemantico("constante nao pode ser alterada");
                }else {
                    escopo.addFilho(atual);
                }
                terminal(";");
                break;
            default:
                erroSintatico("falta ; ou , ou [ ou (");
                break;
        }
    }

    private void recListaVariavel() {
        switch (proximo.getValor()) {
            case ",":
                atual.setCategoria(Simbolos.VAR);
                terminal(",");
                if (escopo.contains(atual.getNome())) {
                    erroSemantico("identificador ja declarado");
                } else if (globalEscopo.containsConst(atual.getNome()) || (classeEscopo != null && classeEscopo.containsConst(atual.getNome()))) {
                    erroSemantico("constante nao pode ser alterada");
                } else {
                    escopo.addFilho(atual);
                }
                int aux = atual.getTipo();
                String obj = atual.getObjectNome();
                atual = new Simbolos();
                atual.setObjectNome(obj);
                atual.setCategoria(Simbolos.VAR);
                atual.setTipo(aux);
                atual.setNome(proximo.getValor());
                Tipo("id");
                recListaVariavel();
                break;
            case ";":
                if (escopo.contains(atual.getNome())) {
                    erroSemantico("identificador ja declarado");
                } else if (globalEscopo.containsConst(atual.getNome()) || (classeEscopo != null && classeEscopo.containsConst(atual.getNome()))) {
                    erroSemantico("constante nao pode ser alterada");
                } else {
                    escopo.addFilho(atual);
                }
                atual.setCategoria(Simbolos.VAR);
                terminal(";");
                break;
            default:
                while (!proximo.getValor().equals(",") && !proximo.getValor().equals(";") && !proximo.getTipo().equals("palavra_reservada")) {
                    erroSintatico("falta , ou ;");
                    proximo = proximo();
                }
                break;
        }
    }

    private void recListaVetor() {
        switch (proximo.getValor()) {
            case ",":
                terminal(",");
                int aux = atual.getTipo();
                String obj = atual.getObjectNome();
                if (escopo.contains(atual.getNome())) {
                    erroSemantico("identificador ja declarado");
                } else if (globalEscopo.containsConst(atual.getNome()) || (classeEscopo != null && classeEscopo.containsConst(atual.getNome()))) {
                    erroSemantico("constante nao pode ser alterada");
                }else {
                    escopo.addFilho(atual);
                }
                atual = new Simbolos();
                atual.setCategoria(Simbolos.VET);
                atual.setTipo(aux);
                atual.setObjectNome(obj);
                atual.setNome(proximo.getValor());
                Tipo("id");
                terminal("[");
                recIndice();
                terminal("]");
                recListaVetor();
                break;
            case ";":
                if (escopo.contains(atual.getNome())) {
                    erroSemantico("identificador ja declarado");
                }else if (globalEscopo.containsConst(atual.getNome()) || (classeEscopo != null && classeEscopo.containsConst(atual.getNome()))) {
                    erroSemantico("constante nao pode ser alterada");
                } else {
                    escopo.addFilho(atual);
                }
                terminal(";");
                break;
            default:
                while (!proximo.getValor().equals(",") && !proximo.getValor().equals(";") && !proximo.getTipo().equals("palavra_reservada")) {
                    erroSintatico("falta , ou ;");
                    proximo = proximo();
                }
                break;
        }
    }

    private void recIndice() {
        switch (proximo.getTipo()) {
            case "id":
                if (!escopo.contains(proximo.getValor()) && !globalEscopo.contains(proximo.getValor()) && (classeEscopo != null && !classeEscopo.contains(proximo.getValor()))) {
                    erroSemantico("variavel do indice nao declarada");
                } else if (escopo.contains(proximo.getValor()) ? escopo.getFilho(proximo.getValor()).getTipo() != Simbolos.INT : (globalEscopo.contains(proximo.getValor()) ? globalEscopo.getFilho(proximo.getValor()).getTipo() != Simbolos.INT : (classeEscopo.contains(proximo.getValor())) && classeEscopo.getFilho(proximo.getValor()).getTipo() != Simbolos.INT)) {
                    erroSemantico("indice do vetor deve ser inteiro");
                }
                Tipo("id");
                break;
            case "numero":
                if (proximo.getValor().contains(".")) {
                    erroSemantico("indice do vetor deve ser inteiro");
                }
                Tipo("numero");
                break;
            default:
                erroSintatico("falta identificador ou numero");
                break;
        }
    }

    private void recDeclParametros() {
        atual = new Simbolos();
        switch (proximo.getValor()) {
            case "char":
                terminal("char");
                atual.setTipo(Simbolos.CHAR);
                atual.setNome(proximo.getValor());
                Tipo("id");
                recVarVet();
                recListaParametros();
                break;
            case "int":
                terminal("int");
                atual.setTipo(Simbolos.INT);
                atual.setNome(proximo.getValor());
                Tipo("id");
                recVarVet();
                recListaParametros();
                break;
            case "bool":
                terminal("bool");
                atual.setTipo(Simbolos.BOOL);
                atual.setNome(proximo.getValor());
                Tipo("id");
                recVarVet();
                recListaParametros();
                break;
            case "string":
                terminal("string");
                atual.setTipo(Simbolos.STRING);
                atual.setNome(proximo.getValor());
                Tipo("id");
                recVarVet();
                recListaParametros();
                break;
            case "float":
                terminal("float");
                atual.setTipo(Simbolos.FLOAT);
                atual.setNome(proximo.getValor());
                Tipo("id");
                recVarVet();
                recListaParametros();
                break;
            default:
                if (proximo.getTipo().equals("id")) {

                    atual.setTipo(Simbolos.OBJECT);
                    atual.setObjectNome(proximo.getValor());
                    if (globalEscopo.contains(proximo.getValor())) {
                        if (globalEscopo.getFilho(proximo.getValor()).getCategoria() != Simbolos.CLASS) {
                            erroSemantico("classe nao declarada");
                        }
                    } else {
                        erroSemantico("classe nao declarada");
                    }
                    Tipo("id");
                    atual.setNome(proximo.getValor());
                    Tipo("id");
                    recVarVet();
                    recListaParametros();
                }
                break;
        }
    }

    private void recListaParametros() {
        switch (proximo.getValor()) {
            case ",":
                terminal(",");
                escopo.addParametro(atual.getTipo());
                if (escopo.contains(atual.getNome())) {
                    erroSemantico("identificador ja declarado");
                }else if (globalEscopo.containsConst(atual.getNome()) || (classeEscopo != null && classeEscopo.containsConst(atual.getNome()))) {
                    erroSemantico("constante nao pode ser alterada");
                } else {
                    escopo.addFilho(atual);
                }
                atual = new Simbolos();
                recTipo();
                atual.setNome(proximo.getValor());
                Tipo("id");
                recVarVet();
                recListaParametros();
                break;
            default:
                escopo.addParametro(atual.getTipo());
                if (escopo.contains(atual.getNome())) {
                    erroSemantico("identificador ja declarado");
                } else if (globalEscopo.containsConst(atual.getNome()) || (classeEscopo != null && classeEscopo.containsConst(atual.getNome()))) {
                    erroSemantico("constante nao pode ser alterada");
                }else {
                    escopo.addFilho(atual);
                }
                break;
        }
    }

    private void recTipo() {
        switch (proximo.getValor()) {
            case "char":
                atual.setTipo(Simbolos.CHAR);
                terminal("char");
                break;
            case "int":
                atual.setTipo(Simbolos.INT);
                terminal("int");
                break;
            case "bool":
                atual.setTipo(Simbolos.BOOL);
                terminal("bool");
                break;
            case "string":
                atual.setTipo(Simbolos.STRING);
                terminal("string");
                break;
            case "float":
                atual.setTipo(Simbolos.FLOAT);
                terminal("float");
                break;
            default:
                if (proximo.getTipo().equals("id")) {
                    atual.setTipo(Simbolos.OBJECT);
                    atual.setObjectNome(proximo.getValor());
                    if (globalEscopo.contains(proximo.getValor())) {
                        if (globalEscopo.getFilho(proximo.getValor()).getCategoria() != Simbolos.CLASS) {
                            erroSemantico("classe nao declarada");
                        }
                    } else {
                        erroSemantico("classe nao declarada");
                    }
                    Tipo("id");
                } else {
                    erroSintatico("falta um tipo: id, int, float, char, string, bool");
                }
                break;
        }
    }

    private void recVarVet() {
        switch (proximo.getValor()) {
            case "[":
                atual.setCategoria(Simbolos.VET);
                terminal("[");
                recIndice();
                terminal("]");
                break;
            default:
                atual.setCategoria(Simbolos.VAR);
                break;
        }
    }

    private void recConteudoMetodo() {
        switch (proximo.getTipo()) {
            case "palavra_reservada":
                if (proximo.getValor().equals("return")) {
                    break;
                }
                recComando();
                recConteudoMetodo();
                break;
            case "id":
                recComando();
                recConteudoMetodo();
                break;
            default:
                if (!proximo.getValor().equals("}")) {
                    erroSintatico("Conteudo de médoto inválido, espera um comando.");
                    proximo = proximo();
                    recConteudoMetodo();
                }
                break;
        }
    }

    private void recComando() {
        atual = new Simbolos();
        switch (proximo.getValor()) {
            case "read":
                recRead();
                break;
            case "write":
                recWrite();
                break;
            case "new":
                recInicializaObjeto();
                break;
            case "if":
                recIf();
                break;
            case "while":
                recWhile();
                break;
            case "char":
                terminal("char");
                atual.setTipo(Simbolos.CHAR);
                atual.setNome(proximo.getValor());
                Tipo("id");
                recIdDecl();
                break;
            case "int":
                terminal("int");
                atual.setTipo(Simbolos.INT);
                atual.setNome(proximo.getValor());
                Tipo("id");
                recIdDecl();
                break;
            case "bool":
                terminal("bool");
                atual.setTipo(Simbolos.BOOL);
                atual.setNome(proximo.getValor());
                Tipo("id");
                recIdDecl();
                break;
            case "string":
                terminal("string");
                atual.setTipo(Simbolos.STRING);
                atual.setNome(proximo.getValor());
                Tipo("id");
                recIdDecl();
                break;
            case "float":
                terminal("float");
                atual.setTipo(Simbolos.FLOAT);
                atual.setNome(proximo.getValor());
                Tipo("id");
                recIdDecl();
                break;
            default:
                if (proximo.getTipo().equals("id")) {
                    String aux = proximo.getValor();
                    Tipo("id");
                    if (proximo.getTipo().equals("id")) {
                        atual.setTipo(Simbolos.OBJECT);
                        atual.setCategoria(Simbolos.VAR);
                        atual.setObjectNome(aux);
                        if (globalEscopo.contains(aux)) {
                            if (globalEscopo.getFilho(aux).getCategoria() != Simbolos.CLASS) {
                                erroSemantico("classe nao declarada");
                            }
                        } else {
                            erroSemantico("classe nao declarada");
                        }
                        atual.setNome(proximo.getValor());
                        Tipo("id");
                        recIdDecl();
                    } else {
                        atual = this.getFilho(aux);
                        if (atual.getTipo() == Simbolos.ERRO) {
                            erroSemantico("identificador nao declarado");
                        }
                        recIdComando();
                    }

                } else {
                    erroSintatico("falta identificador ou palavra reservada: read, write, new, if, while, char, int, float, string, bool");
                }
                break;

        }
    }

    private void recIdDecl() {
        switch (proximo.getValor()) {
            case ",":
                recListaVariavel();
                break;
            case ";":
                atual.setCategoria(Simbolos.VAR);
                
                recListaVariavel();
                break;
            case "[":
                atual.setCategoria(Simbolos.VET);
                terminal("[");
                recIndice();
                terminal("]");
                recListaVetor();
                break;
            default:
                erroSintatico("falta ; ou , ou [");
                break;
        }
    }

    private void recIdComando() {
        switch (proximo.getValor()) {
            case "(":
                if (atual.getCategoria() != Simbolos.MET) {
                    erroSemantico("esta identificador nao e um metodo");
                }
                terminal("(");
                recParametros();
                terminal(")");
                terminal(";");
                break;
            case ".":
                if (atual.getCategoria() != Simbolos.OBJECT) {
                    erroSemantico("esta identificador nao e um objeto");
                }
                terminal(".");
                atual = escopo.getFilho(proximo.getValor());
                if (atual.getCategoria() == Simbolos.ERRO) {
                    erroSemantico("o objeto nao possue este atributo");
                }
                Tipo("id");
                recAcessoObjeto();
                terminal(";");
                break;
            case "=":
                if (atual.getCategoria() != Simbolos.VAR && atual.getCategoria() != Simbolos.CONST){
                    erroSemantico("este identificador nao eh uma variavel");
                }
                terminal("=");
                if (eBool && atual.getTipo() != Simbolos.BOOL) {
                    erroSemantico("atribuicao invalida, tipos incompativeis");
                }else if(atual.getTipo() != recAtribuicao()){
                    erroSemantico("atribuicao invalida, tipos incompativeis");
                }
                eBool=false;
                terminal(";");
                break;
            case "[":
                if (atual.getCategoria() != Simbolos.VET) {
                    erroSemantico("esta identificador nao eh um vetor");
                }
                terminal("[");
                recIndice();
                terminal("]");
                terminal("=");
                if (atual.getTipo() != recAtribuicao()) {
                    erroSemantico("atribuicao invalida, tipos incompativeis");
                }
                terminal(";");
                break;
            default:
                erroSintatico("falta: [ ou = ou . ou (");
                break;
        }
    }

    private void recAcessoObjeto() {
        switch (proximo.getValor()) {
            case "(":
                if (atual.getCategoria() != Simbolos.MET) {
                    erroSemantico("este atributo nao e um metodo");
                }
                terminal("(");
                recParametros();
                terminal(")");
                break;
            case "=":
                if (atual.getCategoria() != Simbolos.VAR) {
                    erroSemantico("este atributo nao e uma variavel");
                }
                terminal("=");
                if (atual.getTipo() != recAtribuicao()) {
                    erroSemantico("atribuicao invalida, tipos incompativeis");
                }
                break;
            default:
                erroSintatico("falta ( ou =");
                break;
        }
    }

    private int recAtribuicao() {
        int aux2;
        switch (proximo.getValor()) {
            case "(":
                terminal("(");
                aux2 = recAtribuicao();
                terminal(")");
                recOperacao();
                return aux2;
            case "++":
                return recIdAcesso();
            case "--":
                return recIdAcesso();
            case "true":
                if (atual.getTipo() != Simbolos.BOOL) {
                    erroSemantico("a variavel nao e do tipo booleana");
                }
                terminal("true");
                recOpLogico();
                return Simbolos.BOOL;
            case "false":
                if (atual.getTipo() != Simbolos.BOOL) {
                    erroSemantico("a variavel nao e do tipo booleana");
                }
                terminal("false");
                recOpLogico();
                return Simbolos.BOOL;
            case "-":
                terminal("-");
                return recNegativo();
            default:
                switch (proximo.getTipo()) {
                    case "id":
                        return recIdAcesso();
                    case "numero":
                        aux2 = proximo.getValor().contains(".")?Simbolos.FLOAT:Simbolos.INT;
                        Tipo("numero");
                        int aux3 = recOperadorNumero(); 
                        return aux2;
                    case "cadeia_constante":
                        if (atual.getTipo() != Simbolos.STRING) {
                            erroSemantico("a variavel nao e do tipo string");
                        }
                        Tipo("cadeia_constante");
                        return Simbolos.STRING;
                    case "caractere_constante":
                        if (atual.getTipo() != Simbolos.CHAR) {
                            erroSemantico("a variavel nao e do tipo char");
                        }
                        Tipo("caractere_constante");
                        return Simbolos.CHAR;
                    default:
                        erroSintatico("falta booleano, numero, identificador, cadeia constante, caracter constante,  ( ou operadores: ++ ou -- ou - ");
                        while (!proximo.getTipo().equals("palavra_reservada") && !proximo.getValor().equals(")") && !proximo.getValor().equals("{") && !proximo.getValor().equals("}")) {
                            proximo = proximo();
                        }
                        return Simbolos.ERRO;
                }
        }
    }

    private int recOperadorNumero() {
        switch (proximo.getValor()) {
            case ">":
                terminal(">");
                recExpAritmetica();
                recOpLogico();
                return Simbolos.BOOL;
            case "<":
                terminal("<");
                recExpAritmetica();
                recOpLogico();
                return Simbolos.BOOL;
            case ">=":
                terminal(">=");
                recExpAritmetica();
                recOpLogico();
                return Simbolos.BOOL;
            case "<=":
                terminal("<=");
                recExpAritmetica();
                recOpLogico();
                return Simbolos.BOOL;
            case "+":
                terminal("+");
                int aux = recExpAritmetica();
                recExpRelacionalOpcional();
                return aux;
            case "-":
                terminal("-");
                recExpAritmetica();
                return recExpRelacionalOpcional();
            case "*":
                terminal("*");
                recExpAritmetica();
                return recExpRelacionalOpcional();
            case "/":
                terminal("/");
                recExpAritmetica();
                return recExpRelacionalOpcional();
            case "==":
                terminal("==");
                recExpAritmetica();
                recOpLogico();
                return Simbolos.BOOL;
            case "!=":
                terminal("!=");
                recExpAritmetica();
                recOpLogico();
                return Simbolos.BOOL;
            default:
                return Simbolos.ERRO;
        }
    }

    private int recNegativo() {
        int aux2;
        switch (proximo.getValor()) {
            case "(":
                terminal("(");
                aux2 = recNegativo();
                terminal(")");
                return aux2;
            case "++":
                return recIdAcesso();
            case "--":
                return recIdAcesso();
            default:
                switch (proximo.getTipo()) {
                    case "numero":
                        Tipo("numero");
                        return recOperadorNumero();
                    case "id":
                        return recIdAcesso();
                    default:
                        erroSintatico("falta: ++, --, (, numero ou identificador");
                        return -1;
                }

        }
    }

    private int recExpRelacionalOpcional() {
        switch (proximo.getValor()) {
            case ">":
                terminal(">");
                recExpAritmetica();
                recOpLogico();
                return Simbolos.BOOL;
            case "<":
                terminal("<");
                recExpAritmetica();
                recOpLogico();
                return Simbolos.BOOL;
            case ">=":
                terminal(">=");
                recExpAritmetica();
                recOpLogico();
                return Simbolos.BOOL;
            case "<=":
                terminal("<=");
                recExpAritmetica();
                recOpLogico();
                return Simbolos.BOOL;
            default:
                return -1;
        }
    }

    private void recOpLogico() {
        switch (proximo.getValor()) {
            case "==":
                terminal("==");
                recExpLogica();
                break;
            case "!=":
                terminal("!=");
                recExpLogica();
                break;
            case "&&":
                terminal("&&");
                recExp();
                break;
            case "||":
                terminal("||");
                recExp();
                break;
            default:
                break;
        }
    }

    private void recRetorno() {
        if (escopo.getTipo() != recAtribuicao()) {
            erroSemantico("tipo de retorno incompativel");
        }
        terminal(";");
    }

    private int recIdAcesso() {
        switch (proximo.getValor()) {
            case "++":
                terminal("++");
                if (this.getFilho(proximo.getValor()).getCategoria() == Simbolos.ERRO) {
                    erroSemantico("identificador nao declarado");
                } else if (this.getFilho(proximo.getValor()).getTipo() != Simbolos.INT && this.getFilho(proximo.getValor()).getTipo() != Simbolos.FLOAT) {
                    erroSemantico("somentes numeros podem ser incrementados");
                }
                Tipo("id");
                return recOperacao();
            case "--":
                terminal("--");
                if (this.getFilho(proximo.getValor()).getCategoria() == Simbolos.ERRO) {
                    erroSemantico("identificador nao declarado");
                } else if (this.getFilho(proximo.getValor()).getTipo() != Simbolos.INT && this.getFilho(proximo.getValor()).getTipo() != Simbolos.FLOAT) {
                    erroSemantico("somentes numeros podem ser incrementados");
                }
                Tipo("id");
                return recOperacao();
            default:
                switch (proximo.getTipo()) {
                    case "id":
                        atual = this.getFilho(proximo.getValor());
                        int aux2 = atual.getTipo();
                        Tipo("id");
                        recAcesso();
                        recOperacao();
                        return aux2;
                    default:
                        erroSintatico("falta: ++, --, identificador");
                        return -1;
                }
        }
    }

    private void recAcesso() {
        switch (proximo.getValor()) {
            case "[":
                if (atual.getCategoria() != Simbolos.VET) {
                    erroSemantico("este identificador nao e um vetor");
                }
                terminal("[");
                recIndice();
                terminal("]");
                break;
            case "(":
                if (atual.getCategoria() != Simbolos.MET) {
                    erroSemantico("este identificador nao e um metodo");
                }
                terminal("(");
                recParametros();
                terminal(")");
                break;
            case ".":
                if (atual.getCategoria() != Simbolos.OBJECT) {
                    erroSemantico("este identificador nao e um objeto");
                }
                terminal(".");
                atual = getFilho(proximo.getValor());
                if (atual.getCategoria() == Simbolos.ERRO) {
                    erroSemantico("a classe nao possue este atributo");
                }
                Tipo("id");
                recChamadaMetodo();
                break;
            case "++":
                terminal("++");
                break;
            case "--":
                terminal("--");
                break;
            default:
                if (atual.getCategoria() != Simbolos.VAR && atual.getCategoria() != Simbolos.CONST) {
                    erroSemantico("este identificador nao e um variavel");
                }
                break;
        }
    }

    private int recOperacao() {
        switch (proximo.getValor()) {
            case ">":
                recOperador();
                return Simbolos.BOOL;
            case "<":
                recOperador();
                return Simbolos.BOOL;
            case ">=":
                recOperador();
                return Simbolos.BOOL;
            case "<=":
                recOperador();
                return Simbolos.BOOL;
            case "+":
                return recOperador();
            case "-":
                return recOperador();
            case "*":
                return recOperador();
            case "/":
                return recOperador();
            case "==":
                recOperador();
                return Simbolos.BOOL;
            case "!=":
                recOperador();
                return Simbolos.BOOL;
            case "&&":
                recOperador();
                return Simbolos.BOOL;
            case "||":
                recOperador();
                return Simbolos.BOOL;
            default:
                return -1;
        }
    }

    private void recChamadaMetodo() {
        switch (proximo.getValor()) {
            case "(":
                if (atual.getCategoria() != Simbolos.MET) {
                    erroSemantico("este identificador nao e um metodo");
                }
                terminal("(");
                recParametros();
                terminal(")");
                break;
            default:
                break;

        }
    }

    private int recOperador() {
        switch (proximo.getValor()) {
            case ">":
                terminal(">");
                recExpAritmetica();
                recOpLogico();
                return Simbolos.BOOL;
            case "<":
                terminal("<");
                recExpAritmetica();
                recOpLogico();
                return Simbolos.BOOL;
            case ">=":
                terminal(">=");
                recExpAritmetica();
                recOpLogico();
                return Simbolos.BOOL;
            case "<=":
                terminal("<=");
                recExpAritmetica();
                recOpLogico();
                return Simbolos.BOOL;
            case "+":
                terminal("+");
                recExpAritmetica();
                return recExpRelacionalOpcional();
            case "-":
                terminal("-");
                recExpAritmetica();
                return recExpRelacionalOpcional();
            case "*":
                terminal("*");
                recExpAritmetica();
                return recExpRelacionalOpcional();
            case "/":
                terminal("/");
                recExpAritmetica();
                return recExpRelacionalOpcional();
            case "==":
                terminal("==");
                if (atual.getTipo() != recAtribuicao()) {
                    erroSemantico("operacao invalida, tipos incompativeis");
                }
                return Simbolos.BOOL;
            case "!=":
                terminal("!=");
                if (atual.getTipo() != recAtribuicao()) {
                    erroSemantico("operacao invalida, tipos incompativeis");
                }
                return Simbolos.BOOL;
            case "&&":
                terminal("&&");
                recExp();
                return Simbolos.BOOL;
            case "||":
                terminal("||");
                recExp();
                return Simbolos.BOOL;
            default:
                erroSintatico("falta operador: >, <, >=, <=, ==, !=, +, -, *, /, &&, ||");
                return -1;
        }
    }

    private void recParametros() {
        switch (proximo.getValor()) {
            case "(":
                recAtribuicao();
                recNovoParametro();
                break;
            case "true":
                recAtribuicao();
                recNovoParametro();
                break;
            case "false":
                recAtribuicao();
                recNovoParametro();
                break;
            case "-":
                recAtribuicao();
                recNovoParametro();
                break;
            case "++":
                recAtribuicao();
                recNovoParametro();
                break;
            case "--":
                recAtribuicao();
                recNovoParametro();
                break;
            default:
                switch (proximo.getTipo()) {
                    case "id":
                        recAtribuicao();
                        recNovoParametro();
                        break;
                    case "numero":
                        recAtribuicao();
                        recNovoParametro();
                        break;
                    case "cadeia_constante":
                        recAtribuicao();
                        recNovoParametro();
                        break;
                    case "caractere_constante":
                        recAtribuicao();
                        recNovoParametro();
                        break;
                    default:
                        break;
                }
                break;
        }
    }

    private void recNovoParametro() {
        switch (proximo.getValor()) {
            case ",":
                terminal(",");
                recParametros();
                break;
            default:
                break;
        }
    }

    private void recInicializaObjeto() {
        terminal("new");
        if (this.getFilho(proximo.getValor()).getCategoria() == Simbolos.ERRO) {
            erroSemantico("objeto nao declarado");
        } else if (this.getFilho(proximo.getValor()).getCategoria() != Simbolos.OBJECT) {
            erroSemantico("este identificador nao e um objeto");
        }
        Tipo("id");
        terminal(";");
    }

    private void recWhile() {
        terminal("while");
        terminal("(");
        recExp();
        terminal(")");
        terminal("{");
        recConteudoEstrutura();
        terminal("}");
    }

    private void recRead() {
        terminal("read");
        terminal("(");
        if (this.getFilho(proximo.getValor()).getCategoria() == Simbolos.ERRO) {
            erroSemantico("identificador nao declarado");
        }
        Tipo("id");
        recListaRead();
        terminal(")");
        terminal(";");
    }

    private void recListaRead() {
        switch (proximo.getValor()) {
            case ",":
                terminal(",");
                if (this.getFilho(proximo.getValor()).getCategoria() == Simbolos.ERRO) {
                    erroSemantico("identificador nao declarado");
                }
                Tipo("id");
                recListaRead();
                break;
            default:
                break;
        }
    }

    private void recWrite() {
        terminal("write");
        terminal("(");
        recParametrosWrite();
        terminal(")");
        terminal(";");
    }

    private void recParametrosWrite() {
        recImprimiveis();
        recNovoParametroWrite();
    }

    private void recNovoParametroWrite() {

        switch (proximo.getValor()) {
            case ",":
                terminal(",");
                recParametrosWrite();
                break;
            default:
                break;
        }
    }

    private void recImprimiveis() {
        switch (proximo.getTipo()) {
            case "id":
                if (this.getFilho(proximo.getValor()).getCategoria() == Simbolos.ERRO) {
                    erroSemantico("identificador nao declarado");
                }
                Tipo("id");
                recOpWrite();
                break;
            case "numero":
                Tipo("numero");
                recOpWrite();
                break;
            case "cadeia_constante":
                Tipo("cadeia_constante");
                break;
            case "caractere_constante":
                Tipo("caractere_constante");
                break;
            default:
                switch (proximo.getValor()) {
                    case "(":
                        terminal("(");
                        recImprimiveis();
                        terminal(")");
                        break;
                    default:
                        erroSintatico("falta identificador, numero, cadeia constante, caracter consatante ou (");
                        break;
                }

        }
    }

    private void recOpWrite() {
        switch (proximo.getValor()) {
            case "+":
                terminal("+");
                recExpAritmetica();
                break;
            case "-":
                terminal("-");
                recExpAritmetica();
                break;
            case "*":
                terminal("*");
                recExpAritmetica();
                break;
            case "/":
                terminal("");
                recExpAritmetica();
                break;
            default:
                break;
        }
    }

    private void recIf() {
        terminal("if");
        terminal("(");
        recExp();
        terminal(")");
        terminal("{");
        recConteudoEstrutura();
        terminal("}");
        recComplementoIf();
    }

    private void recComplementoIf() {
        switch (proximo.getValor()) {
            case "else":
                terminal("else");
                terminal("{");
                recConteudoEstrutura();
                terminal("}");
                break;
            default:
                break;
        }
    }

    private void recConteudoEstrutura() {
        switch (proximo.getTipo()) {
            case "palavra_reservada":
                recComandoEstrutura();
                recConteudoEstrutura();
                break;
            case "id":
                recComandoEstrutura();
                recConteudoEstrutura();
                break;
            default:
                if (!proximo.getValor().equals("}")) {
                    erroSintatico("falta um comando: identificador ou palavra reservada");
                    proximo = proximo();
                    recConteudoEstrutura();
                }
                break;
        }
    }

    private void recComandoEstrutura() {
        switch (proximo.getValor()) {
            case "read":
                recRead();
                break;
            case "write":
                recWrite();
                break;
            case "new":
                recInicializaObjeto();
                break;
            case "if":
                recIf();
                break;
            case "while":
                recWhile();
                break;
            case "char":
                terminal("char");
                Tipo("id");
                recIdDecl();
                break;
            case "int":
                terminal("int");
                Tipo("id");
                recIdDecl();
                break;
            case "bool":
                terminal("bool");
                Tipo("id");
                recIdDecl();
                break;
            case "string":
                terminal("string");
                Tipo("id");
                recIdDecl();
                break;
            case "float":
                terminal("float");
                Tipo("id");
                recIdDecl();
                break;
            default:
                if (proximo.getTipo().equals("id")) {
                    atual = this.getFilho(proximo.getValor());
                    Tipo("id");
                    recIdComando();

                } else {
                    erroSintatico("falta um comando: identificador ou palavra reservada");
                }
                break;

        }
    }

    private void recExp() {
        switch (proximo.getValor()) {
            case "true":
                terminal("true");
                recComplementoLogico();
                break;
            case "false":
                terminal("false");
                recComplementoLogico();
                break;
            case "++":
                terminal("++");
                if (this.getFilho(proximo.getValor()).getCategoria() == Simbolos.ERRO) {
                    erroSemantico("identificador nao declarado");
                } else if (this.getFilho(proximo.getValor()).getTipo() != Simbolos.INT && this.getFilho(proximo.getValor()).getTipo() != Simbolos.FLOAT) {
                    erroSemantico("somente numeros podem ser incremetados");
                }
                Tipo("id");
                recIdExp();
                recComplementoAritmetico1();
                break;
            case "--":
                terminal("--");
                if (this.getFilho(proximo.getValor()).getCategoria() == Simbolos.ERRO) {
                    erroSemantico("identificador nao declarado");
                } else if (this.getFilho(proximo.getValor()).getTipo() != Simbolos.INT && this.getFilho(proximo.getValor()).getTipo() != Simbolos.FLOAT) {
                    erroSemantico("somente numeros podem ser incremetados");
                }
                Tipo("id");
                recIdExp();
                recComplementoAritmetico1();
                break;
            case "(":
                terminal("(");
                recExp();
                terminal(")");
                break;
            default:
                switch (proximo.getTipo()) {
                    case "id":
                        if (this.getFilho(proximo.getValor()).getCategoria() == Simbolos.ERRO) {
                            erroSemantico("identificador nao declarado");
                        }
                        Tipo("id");
                        recIdExpArit();
                        recComplementoAritmetico1();
                        break;
                    case "numero":
                        Tipo("numero");
                        recComplementoAritmetico();
                        recOpRelacional();
                        break;
                    default:
                        erroSintatico("falta identificador, numero, boolean, (, ou operador: ++, --");
                        while (!proximo.getTipo().equals("palavra_reservada") && !proximo.getValor().equals(")") && !proximo.getValor().equals("{") && !proximo.getValor().equals("}")) {
                            proximo = proximo();
                        }
                        break;
                }

        }
    }

    private void recComplementoAritmetico1() {
        switch (proximo.getValor()) {
            case "+":
                terminal("+");
                recFatorAritmetico();
                recOpIdRelacional();
                break;
            case "-":
                terminal("-");
                recFatorAritmetico();
                recOpIdRelacional();
                break;
            case "*":
                terminal("*");
                recFatorAritmetico();
                recOpIdRelacional();
                break;
            case "/":
                terminal("/");
                recFatorAritmetico();
                recOpIdRelacional();
                break;
            default:
                recOpIdLogico();
                break;

        }
    }

    private void recExpLogica() {
        switch (proximo.getValor()) {
            case "true":
                terminal("true");
                recComplementoLogico();
                break;
            case "false":
                terminal("false");
                recComplementoLogico();
                break;
            case "++":
                terminal("++");
                Tipo("id");
                recIdExp();
                recComplementoAritmetico();
                recOpIdLogico();
                break;
            case "--":
                terminal("--");
                Tipo("id");
                recIdExp();
                recComplementoAritmetico();
                recOpIdLogico();
                break;
            case "(":
                terminal("(");
                recExpLogica();
                terminal(")");
                recComplementoExpLogica();
                break;
            default:
                switch (proximo.getTipo()) {
                    case "id":
                        if (this.getFilho(proximo.getValor()).getCategoria() == Simbolos.ERRO) {
                            erroSemantico("identificador nao declarado");
                        }
                        Tipo("id");
                        recIdExpArit();
                        recComplementoAritmetico();
                        recOpIdLogico();
                        break;
                    case "numero":
                        Tipo("numero");
                        recComplementoAritmetico();
                        recCoOpRelacional();
                        break;
                    default:
                        erroSintatico("falta identificador, numero, boolean, (, ou operador: ++, --");
                        while (!proximo.getTipo().equals("palavra_reservada") && !proximo.getValor().equals(")") && !proximo.getValor().equals("{") && !proximo.getValor().equals("}")) {
                            proximo = proximo();
                        }
                        break;
                }

        }
    }

    private void recCoOpRelacional() {
        switch (proximo.getValor()) {
            case ">":
                recOpRelacional();
                break;
            case "<":
                recOpRelacional();
                break;
            case ">=":
                recOpRelacional();
                break;
            case "<=":
                recOpRelacional();
                break;
            case "==":
                recOpRelacional();
                break;
            case "!=":
                recOpRelacional();
                break;
            case "&&":
                terminal("&&");
                recExp();
                break;
            case "||":
                terminal("||");
                recExp();
                break;
            default:
                break;
        }
    }

    private void recComplementoExpLogica() {
        switch (proximo.getValor()) {
            case ">":
                terminal(">");
                recFatorAritmetico();
                recComplementoLogico();
                break;
            case "<":
                terminal("<");
                recFatorAritmetico();
                recComplementoLogico();
                break;
            case ">=":
                terminal(">=");
                recFatorAritmetico();
                recComplementoLogico();
                break;
            case "<=":
                terminal("<=");
                recFatorAritmetico();
                recComplementoLogico();
                break;
            case "==":
                terminal("==");
                recExpLogica();
                break;
            case "!=":
                terminal("!=");
                recExpLogica();
                break;
            case "&&":
                terminal("&&");
                recExp();
                break;
            case "||":
                terminal("||");
                recExp();
                break;
            case "+":
                terminal("+");
                recFatorAritmetico();
                recComplementoLogico();
                break;
            case "-":
                terminal("-");
                recFatorAritmetico();
                recComplementoLogico();
                break;
            case "*":
                terminal("*");
                recFatorAritmetico();
                recComplementoLogico();
                break;
            case "/":
                terminal("/");
                recFatorAritmetico();
                recComplementoLogico();
                break;
            default:
                break;
        }
    }

    private int recIdExp() {
        switch (proximo.getValor()) {
            case "(":
                terminal("(");
                recParametros();
                terminal(")");
                return -1;
            case ".":
                terminal(".");
                if (this.getFilho(proximo.getValor()).getCategoria() == Simbolos.ERRO) {
                    erroSemantico("identificador nao declarado");
                }
                Tipo("id");
                recChamadaMetodo();
                return -1;
            case "[":
                terminal("[");
                recIndice();
                terminal("]");
                return -1;
            default:
                return -1;
        }
    }

    private void recOpIdLogico() {
        switch (proximo.getValor()) {
            case ">":
                recOpIdRelacional();
                break;
            case "<":
                recOpIdRelacional();
                break;
            case ">=":
                recOpIdRelacional();
                break;
            case "<=":
                recOpIdRelacional();
                break;
            case "==":
                recOpIdRelacional();
                break;
            case "!=":
                recOpIdRelacional();
                break;
            case "&&":
                terminal("&&");
                recExp();
                break;
            case "||":
                terminal("||");
                recExp();
                break;
            default:
                break;
        }
    }

    private void recComplementoLogico() {
        switch (proximo.getValor()) {
            case "==":
                terminal("==");
                recExpLogica();
                break;
            case "!=":
                terminal("!=");
                recExpLogica();
                break;
            case "&&":
                terminal("&&");
                recExp();
                break;
            case "||":
                terminal("||");
                recExp();
                break;
            default:
                break;
        }
    }

    private void recOpRelacional() {
        switch (proximo.getValor()) {
            case ">":
                terminal(">");
                recExpAritmetica();
                recOpLogico();
                break;
            case "<":
                terminal("<");
                recExpAritmetica();
                recOpLogico();
                break;
            case ">=":
                terminal(">=");
                recExpAritmetica();
                recOpLogico();
                break;
            case "<=":
                terminal("<=");
                recExpAritmetica();
                recOpLogico();
                break;
            case "==":
                terminal("==");
                recExpAritmetica();
                recOpLogico();
                break;
            case "!=":
                terminal("!=");
                recExpAritmetica();
                recOpLogico();
                break;
            default:
                erroSintatico("falta operador: >, <, >=, <=, ==, !=");
                break;
        }
    }

    private void recOpIdRelacional() {
        switch (proximo.getValor()) {
            case ">":
                terminal(">");
                recExpAritmetica();
                recOpLogico();
                break;
            case "<":
                terminal("<");
                recExpAritmetica();
                recOpLogico();
                break;
            case ">=":
                terminal(">=");
                recExpAritmetica();
                recOpLogico();
                break;
            case "<=":
                terminal("<=");
                recExpAritmetica();
                recOpLogico();
                break;
            case "==":
                terminal("==");
                recExpLogica();
                break;
            case "!=":
                terminal("!=");
                recExpLogica();
                break;
            default:
                erroSintatico("falta operador: >, <, >=, <=, ==, !=");
                break;
        }
    }

    private int recExpAritmetica() {
        switch (proximo.getValor()) {
            case "++":
                return recFatorAritmetico();
            case "--":
                return recFatorAritmetico();
            case "-":
                terminal("-");
                return recExpAritmetica();
            case "(":
                return recFatorAritmetico();
            default:
                if (proximo.getTipo().equals("id") || proximo.getTipo().equals("numero")) {
                    return recFatorAritmetico();
                }
                erroSintatico("falta identificar, numero, ( ou operador: ++, --, -");
                while (!proximo.getTipo().equals("id") && !proximo.getTipo().equals("palavra_reservada") && !proximo.getValor().equals(")") && !proximo.getValor().equals("{") && !proximo.getValor().equals("}")) {
                    proximo = proximo();
                }
                return -1;
        }

    }

    private int recFatorAritmetico() {
        switch (proximo.getValor()) {
            case "++":
                recIdAritmetico();
                return recComplementoAritmetico();
            case "--":
                recIdAritmetico();
                return recComplementoAritmetico();
            case "-":
                terminal("-");
                return recExpAritmetica();
            case "(":
                recFatorAritmetico();
                return recComplementoAritmetico();
            default:
                if (proximo.getTipo().equals("id")) {
                    recIdAritmetico();
                    return recComplementoAritmetico();
                } else if (proximo.getTipo().equals("numero")) {
                    Tipo("numero");
                    return recComplementoAritmetico();
                }
                erroSintatico("falta numero, identificador, (, ou operador: ++, --, -");
                while (!proximo.getTipo().equals("palavra_reservada") && !proximo.getValor().equals(")") && !proximo.getValor().equals("{") && !proximo.getValor().equals("}") && !proximo.getValor().equals(";")) {
                    proximo = proximo();
                }
                return -1;
        }
    }

    private int recIdAritmetico() {
        Simbolos aux;
        int aux2;
        switch (proximo.getValor()) {
            case "++":
                terminal("++");
                aux = this.getFilho(proximo.getValor());
                if (aux.getCategoria() == Simbolos.ERRO) {
                    erroSemantico("identificador nao declarado");
                } else if (aux.getTipo() != Simbolos.INT && aux.getTipo() != Simbolos.FLOAT) {
                    erroSemantico("somente numeros podem ser incremetados");
                }
                aux2 = this.getFilho(proximo.getValor()).getTipo();
                Tipo("id");
                return aux2;
            case "--":
                terminal("--");
                aux = this.getFilho(proximo.getValor());
                if (aux.getCategoria() == Simbolos.ERRO) {
                    erroSemantico("identificador não declarado");
                } else if (aux.getTipo() != Simbolos.INT && aux.getTipo() != Simbolos.FLOAT) {
                    erroSemantico("somente numeros podem ser incremetados");
                }
                aux2 = this.getFilho(proximo.getValor()).getTipo();
                Tipo("id");
                return aux2;
            default:
                if (proximo.getTipo().equals("id")) {
                    aux = this.getFilho(proximo.getValor());
                    if (aux.getCategoria() == Simbolos.ERRO) {
                        erroSemantico("identificador nao declarado");
                    } else if (aux.getTipo() != Simbolos.INT && aux.getTipo() != Simbolos.FLOAT) {
                        erroSemantico("somente numeros podem ser incremetados");
                    }
                    Tipo("id");
                    return recIdExpArit();
                } else {
                    erroSintatico("falta identificador ou operador: ++, --");
                    return -1;
                }
        }
    }

    private int recIdExpArit() {
        switch (proximo.getValor()) {
            case "(":
                if (atual.getCategoria() != Simbolos.MET) {
                    erroSemantico("esta variavel nao e um metodo");
                }
                return recIdExp();
            case ".":
                if (atual.getCategoria() != Simbolos.OBJECT) {
                    erroSemantico("esta variavel nao e um objeto");
                }
                return recIdExp();
            case "[":
                if (atual.getCategoria() != Simbolos.VET) {
                    erroSemantico("esta variavel nao e um vetor");
                }
                return recIdExp();
            case "++":
                terminal("++");
                return -1;
            case "--":
                terminal("--");
                return -1;
            default:
                return -1;
        }
    }

    private int recComplementoAritmetico() {
        switch (proximo.getValor()) {
            case "+":
                terminal("+");
                return recFatorAritmetico();
            case "-":
                terminal("-");
                return recFatorAritmetico();
            case "*":
                terminal("*");
                return recFatorAritmetico();
            case "/":
                terminal("/");
                return recFatorAritmetico();
            default:
                return -1;
        }
    }

    /**
     * Método para retornar os erros semanticos encontrados na analise.
     *
     * @return lista com erros semanticos
     */
    public ArrayList<String> getErrosSemanticos() {
        return errosSemanticos;
    }

    private Simbolos getFilho(String valor) {
        return escopo.contains(valor) ? escopo.getFilho(valor) : (classeEscopo.contains(valor) ? classeEscopo.getFilho(valor) : (globalEscopo.getFilho(valor)));
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modulo_completo;

import java.util.ArrayList;

/**
 * @author Lucas Carneiro
 * @author Oto Lopes
 */
public class Simbolos {

    public static final int INT = 0;
    public static final int CHAR = 1;
    public static final int STRING = 2;
    public static final int FLOAT = 3;
    public static final int BOOL = 4;
    public static final int OBJECT = 5;
    public static final int VOID = 6;

    public static final int VAR = 10;
    public static final int CONST = 11;
    public static final int MET = 12;
    public static final int CLASS = 13;
    public static final int VET = 14;
    public static final int MAIN = 15;
    public static final int PARA = 16;

    private String nome;
    private int categoria;
    private int tipo;
    private ArrayList<Integer> parametros;
    private Simbolos pai;
    private ArrayList<Simbolos> filhos;

    public Simbolos() {
        filhos = new ArrayList<>();
        parametros = new ArrayList<>();
    }

    /**
     * Método para adicionar um novo simbolo a tabela de simbolos.
     *
     * @param filho
     */
    public void addFilho(Simbolos filho) {
        filhos.add(filho);
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getCategoria() {
        return categoria;
    }

    public void setCategoria(int categoria) {
        this.categoria = categoria;
    }

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }

    public ArrayList getFilhos() {
        return filhos;
    }

    public void setFilhos(ArrayList filhos) {
        this.filhos = filhos;
    }

    @Override
    public String toString() {
        return " categoria: " + categoria + " tipo: " + tipo + " nome: " + nome + " filhos: " + filhos.toString()
                + " pai: " + pai + " parametros: " + parametros + "\n"; //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Metodo para verificar se o identificador ja esta em uso.
     *
     * @param simbolo simbolo para testar se o nome ja esta em uso
     * @return retorna true se ja existe um simbolo com o mesmo nome e false
     * caso contrario
     */
    public boolean contains(String simbolo) {
        boolean var = false;
        if (pai != null) {                    //Verifica se a classe tem herança.
            return pai.contains(simbolo); //Verifica se o identificador já foi decarado no pai. 
        }
        for (Simbolos next : filhos) {
            if (next.getNome().equals(simbolo)) {
                var = true;
                break;
            }
        }
        return var;
    }
    /**
     * Método para verificar se já existe uma constante declarada.
     * @param simbolo Nome do simbolo a ser identificado. 
     * @return retorna verdadeiro se existe uma constante com esse nome ou false se não existe.
     */
    public boolean containsConst(String simbolo) {
        boolean var = false;
        if (this.contains(simbolo)) {
            if (this.getFilho(simbolo).getCategoria() == Simbolos.CONST) {
                var = true;
            }
        }
        return var;
    }
    
    /**
     * Método para verificar se um método está fazendo overload de um método do pai. 
     * @param simbolo Método para verificar o overload
     * @return retorna true se existe um método no pai igual ao do filho ou false caso contrario.
     */
    public boolean isOverload(Simbolos simbolo) {
        boolean var = false;
        if (pai != null && pai.contains(simbolo.getNome())) {
            Simbolos aux = pai.getFilho(simbolo.getNome());
            if (aux.getCategoria() == Simbolos.MET) {
                if (simbolo.getTipo()== aux.getTipo() && simbolo.getParametros().size() == aux.getParametros().size()) {
                    for (int i = 0; i < aux.getParametros().size(); i++) {
                        if (simbolo.getParametros().get(i).equals(aux.getParametros().get(i))) {
                            var = true;
                        } else {
                            return false;
                        }
                    }
                }
            }
        }
        return var;
    }

    public ArrayList<Integer> getParametros() {
        return parametros;
    }

    public void setParametros(ArrayList<Integer> parametros) {
        this.parametros = parametros;
    }

    public Simbolos getPai() {
        return pai;
    }

    public void setPai(Simbolos pai) {
        this.pai = pai;
    }
    
    /**
     * Método que retorna um filho específico a partir do nome.
     * @param valor nome do filho desejado
     * @return retorna o simbolo com o nome desejado
     */
    public Simbolos getFilho(String valor) {
        for (Simbolos next : filhos) {
            if (next.getNome().equals(valor)) {
                return next;
            }
        }
        return null;
    }

    /**
     * Método para salvar os parametros de funções.
     * @param tipo adiciona o tipo do parametro.
     */
    public void addParametro(int tipo) {
        parametros.add(tipo);
    }
}

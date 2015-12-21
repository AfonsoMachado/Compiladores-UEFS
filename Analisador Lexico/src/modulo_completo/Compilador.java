package modulo_completo;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import manipulação_arquivosIO.Arquivo;
import modulo_analisadorLexico.AnalisadorLexico;

/**
 * Classe responsável por executar os modulos do compilador, assim capturando os
 * códigos fonte e gerando suas respectivas saídas.
 *
 * @author Lucas Carneiro
 * @author Oto Lopes
 *
 * @see AnalisadorLexico
 * @see Arquivo
 */
public class Compilador {

    /**
     * Manipulador dos documentos de entrada e saída.
     */
    private final Arquivo arquivo;

    /**
     * Modulo da analise léxica do código.
     */
    private AnalisadorLexico analisadorLexico;

    /**
     * Construtor da Classe.
     */
    public Compilador() {

        arquivo = new Arquivo(); // Criação do manipulador de entrada e saída.
    }

    /**
     * Ler todos os códigos fonte da pasta <i>/src/testes/in/</i> e envia cada
     * um ao modulo léxico, sendo que ao fim do processo deste modulo, são
     * gerados os arquivos de cada código fonte
     *
     * @throws FileNotFoundException Se não encontrar o arquivo do código
     * @throws IOException Arquivo de saida não foi gerado com sucesso
     */
    public void compilar() throws FileNotFoundException, IOException {

        ArrayList<String> localFiles = arquivo.lerCodigos(); // Recebe a lista com todos os códigos da pasta.
        if (localFiles.isEmpty()) { // Pasta de códigos de entrada vazia.
            System.out.println("Sem Códigos para Compilar");
            System.exit(0);
        }
        for (String lF : localFiles) { // Para cada arquivo fonte, o analisador léxico gera as listas de tokens e erros (se houver).
            ArrayList<String> codigoFonte = arquivo.lerCodigoFonte(lF);
            analisadorLexico = new AnalisadorLexico();
            analisadorLexico.analise(codigoFonte);
            arquivo.escreverSaidaLexico(analisadorLexico.getTokens(), analisadorLexico.getErros());
        }
    }

    /**
     * Inicializa os modulos do compilador
     * @param args
     */
    public static void main(String args[]) {

        try {
            Compilador compilador = new Compilador(); // Cria o compilador.
            compilador.compilar(); // Executa o compilador.
        } catch (FileNotFoundException error1) {
            System.out.println("Arquivo Não Encontrado");
            System.exit(0);
        } catch (IOException ex) {
            System.out.println("Arquivo de saida não foi gerado com sucesso");
            System.exit(0);
        }
        
        System.out.println("COMPILADO !");
    }
}
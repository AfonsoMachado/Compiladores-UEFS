package manipulação_arquivosIO;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;
import modulo_analisadorLexico.Token;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author MUDAR DEPOIS ESSES DADOS DO JAVADOC. na verdade é bom começar com o
 * javadoc tbm
 */
public class Arquivo {

    /**
     *
     */
    private Scanner scanner;
    private File file;

    /**
     * 
     * Lexico\src\manipulação_arquivosIO\Arquivo.java
     *
     * @param file
     * @return
     * @throws FileNotFoundException
     */
    public ArrayList<String> lerCodigoFonte(File file) throws FileNotFoundException {

        scanner = new Scanner(new FileReader(file)).useDelimiter("\n");
        this.file = file;
        ArrayList<String> codigo = new ArrayList();
        while (scanner.hasNext()) {
            codigo.add(scanner.next());
        }

        return codigo;
    }

    public void escreverSaidaLexico(ArrayList<Token> tokens, ArrayList<String> erros) {
        FileWriter arq;
        try {
            arq = new FileWriter(file.getAbsolutePath() + ".out", false);

            PrintWriter gravar = new PrintWriter(arq);
            for (Token token : tokens) {
                gravar.printf(token.getTipo() + "#" + token.getValor() + "#" + token.getLinha() + "\n");
            }
            if (erros.isEmpty()) {
                gravar.printf("\nParabens, código compilado com successo\n");
            } else {

                for (String erro : erros) {
                    gravar.printf(erro + "\n");
                }
            }
            arq.close();
        } catch (IOException ex) {
            System.out.println("Arquivo de saida não foi gerado com sucesso.");
        }
    }

}

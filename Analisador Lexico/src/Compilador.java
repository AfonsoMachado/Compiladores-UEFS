
import java.util.ArrayList;
import javax.swing.JOptionPane;
import util.Arquivo;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author jkdrangel
 */
public class Compilador {
    public static void main(String[] args){
        Arquivo arquivo = new Arquivo();
        ArrayList<String> codigoFonte = arquivo.lerCodigoFonte();
        JOptionPane.showMessageDialog(null, codigoFonte);
    }
}

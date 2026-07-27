package cgti;// pertenece al paquete 

import cgti.vistas.InicioFrame; // importando clase InicioFrame
import javax.swing.SwingUtilities; // libreria para trabajar con swing ( graficos)

public class Main { // se crea la clase principal
    public static void main(String[] args) { 
        SwingUtilities.invokeLater(() -> { // se manda a llamar 
            InicioFrame frame = new InicioFrame(); // crea InicioFrame
            frame.setVisible(true);// muestra la pantalla
        });
    }
}

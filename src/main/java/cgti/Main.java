package cgti;// pertenece al paquete 

import cgti.vistas.AlumnoAccesoFrame;
import javax.swing.SwingUtilities; // libreria para trabajar con swing ( graficos)
import com.formdev.flatlaf.FlatLightLaf;

public class Main { // se crea la clase principal
    public static void main(String[] args) {
        // Activar FlatLaf
        FlatLightLaf.setup();

        SwingUtilities.invokeLater(() -> new AlumnoAccesoFrame().setVisible(true));
    }
}

package cgti.vistas;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Estilos {

    public static final Color COLOR_AZUL = new Color(0, 37, 154);
    public static final Color COLOR_GRIS = new Color(139, 141, 141);
    public static final Color COLOR_VERDE = new Color(0, 168, 135);
    public static final Color COLOR_BLANCO = new Color(255, 255, 255);
    public static final Color COLOR_NEGRO = new Color(0, 0, 0);
    public static final Color COLOR_VERDE_HOVER = new Color(0, 145, 118);
    public static final Color COLOR_ERROR = new Color(242, 128, 128);
    public static final Color COLOR_AZUL_HOVER = new Color(30, 65, 180);
    public static final Color COLOR_BORDE = new Color(220, 224, 230);

    // Nombre del usuario
    public static final Font GRANDE = new Font("BakerSignet BT", Font.BOLD, 28);

    public static final Font TITULO = new Font("Arial", Font.BOLD, 24);

    public static final Font Nombre = new Font("Arial", Font.BOLD, 18);
    public static final Font PEQUEÑO = new Font("Arial", Font.PLAIN, 12);

    public static void estilizarBotonRedondo(JButton boton, Color fondo, Color texto, int redondeo) {
        // 1. Quitamos el diseño viejo de Java
        boton.setContentAreaFilled(false);
        boton.setBorderPainted(false);
        boton.setFocusPainted(false);

        // 2. Aplicamos los colores que le pases por parámetro
        boton.setBackground(fondo);
        boton.setForeground(texto);

        // 3. Le cambiamos el comportamiento para que se pinte redondo
        boton.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g;
                // Suavizado de bordes
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Pintamos el fondo redondo
                g2.setColor(c.getBackground());
                g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), redondeo, redondeo);
                // Pintamos el texto encima
                super.paint(g, c);
            }
        });
    }

    public static JLabel crearLabelCentrado(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(Estilos.TITULO.deriveFont(Font.PLAIN, 14));
        label.setForeground(Estilos.COLOR_NEGRO);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    public static void bloquearCierreConX(JFrame frame, String mensaje) {
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                JPanel panel = new JPanel(new BorderLayout());
                panel.setBackground(COLOR_BLANCO);

                JLabel lblMensaje = new JLabel(mensaje);
                lblMensaje.setFont(PEQUEÑO);
                lblMensaje.setForeground(COLOR_NEGRO);
                lblMensaje.setHorizontalAlignment(SwingConstants.CENTER);

                panel.add(lblMensaje);

                JOptionPane.showMessageDialog(
                        frame,
                        panel,
                        "Atencion",
                        JOptionPane.PLAIN_MESSAGE);
            }
        });

    }
}

package cgti.vistas;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * Clase de estilos visuales de la aplicación
 * 
 * Colores institucionales UTNG:
 * - Azul UTNG: #003B9A (institucional primario)
 * - Verde UTNG: #00A887 (institucional secundario)
 * - Dorado UTNG: #C4A12E (detalles y acentos)
 * - Blanco: #FFFFFF (fondos)
 * - Gris UTNG: #8B8D8D (textos secundarios)
 * 
 * Paleta completa basada en el manual de identidad UTNG
 */
public class Estilos {

    // ==================== COLORES INSTITUCIONALES UTNG ====================

    /** Azul institucional primario - Representa confianza y tecnología */
    public static final Color COLOR_AZUL = new Color(0, 59, 154); // #003B9A

    /** Verde institucional secundario - Representa crecimiento e innovación */
    public static final Color COLOR_VERDE = new Color(0, 168, 135); // #00A887

    /** Dorado institucional - Para detalles, acentos y elementos destacados */
    public static final Color COLOR_DORADO = new Color(196, 161, 46); // #C4A12E

    // ==================== COLORES NEUTROS ====================

    /** Blanco puro - Fondos principales */
    public static final Color COLOR_BLANCO = new Color(255, 255, 255); // #FFFFFF

    /** Negro puro - Textos principales */
    public static final Color COLOR_NEGRO = new Color(0, 0, 0); // #000000

    /** Gris institucional - Textos secundarios y elementos deshabilitados */
    public static final Color COLOR_GRIS = new Color(139, 141, 141); // #8B8D8D

    /** Gris claro - Fondos alternativos y separadores */
    public static final Color COLOR_GRIS_CLARO = new Color(245, 245, 245); // #F5F5F5

    /** Borde sutil - Para campos de texto y separadores */
    public static final Color COLOR_BORDE = new Color(220, 224, 230); // #DCE0E6

    // ==================== COLORES DE ESTADO (Derivados de institucionales)
    // ====================

    /** Verde éxito - Derivado más claro del verde institucional */
    public static final Color COLOR_EXITO = new Color(46, 204, 113); // #2ECC71

    /** Rojo error - Para mensajes de error y eliminaciones */
    public static final Color COLOR_ERROR = new Color(231, 76, 60); // #E74C3C

    /** Amarillo advertencia - Para mensajes de precaución */
    public static final Color COLOR_ADVERTENCIA = new Color(241, 196, 15); // #F1C40F

    /** Azul información - Derivado más claro del azul institucional */
    public static final Color COLOR_INFO = new Color(52, 152, 219); // #3498DB

    // ==================== COLORES HOVER (Versiones oscurecidas)
    // ====================

    /** Azul hover - Para efectos al pasar el mouse */
    public static final Color COLOR_AZUL_HOVER = new Color(0, 45, 120); // #002D78

    /** Verde hover - Para efectos al pasar el mouse */
    public static final Color COLOR_VERDE_HOVER = new Color(0, 145, 118); // #009176

    /** Dorado hover - Para efectos al pasar el mouse */
    public static final Color COLOR_DORADO_HOVER = new Color(170, 140, 35); // #AA8C23

    // ==================== COLORES PARA FONDOS ESPECIALES ====================

    /** Fondo para modo edición - Amarillo muy claro */
    public static final Color COLOR_FONDO_EDICION = new Color(255, 251, 235); // #FFFBEB

    /** Fondo para tarjetas - Blanco roto */
    public static final Color COLOR_FONDO_TARJETA = new Color(250, 250, 250); // #FAFAFA

    // ==================== TIPOGRAFÍA INSTITUCIONAL ====================

    /** Título principal - BakerSignet BT (fuente institucional) */
    public static final Font GRANDE = new Font("BakerSignet BT", Font.BOLD, 28);

    /** Subtítulos - Arial Bold */
    public static final Font TITULO = new Font("Arial", Font.BOLD, 24);

    /** Nombres y etiquetas destacadas */
    public static final Font NOMBRE = new Font("Arial", Font.BOLD, 18);

    /** Texto secundario y notas */
    public static final Font PEQUEÑO = new Font("Arial", Font.PLAIN, 12);

    /** Texto de botones */
    public static final Font BOTON = new Font("Arial", Font.BOLD, 14);

    // ==================== MÉTODOS DE ESTILIZACIÓN ====================

    /**
     * Aplica estilo redondeado moderno a un botón
     * Usa colores institucionales con efecto hover automático
     * 
     * @param boton    Botón a estilizar
     * @param fondo    Color de fondo del botón
     * @param texto    Color del texto del botón
     * @param redondeo Radio de redondeo de las esquinas
     */
    public static void estilizarBotonRedondo(JButton boton, Color fondo, Color texto, int redondeo) {
        // Quitar decoraciones por defecto de Java
        boton.setContentAreaFilled(false);
        boton.setBorderPainted(false);
        boton.setFocusPainted(false);
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        boton.setFont(BOTON);

        // Aplicar colores
        boton.setBackground(fondo);
        boton.setForeground(texto);

        // Color hover automático (15% más oscuro)
        Color hover = oscurecer(fondo, 0.85f);

        // Personalizar el pintado del botón
        boton.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Sombra sutil debajo del botón
                g2.setColor(new Color(0, 0, 0, 25));
                g2.fillRoundRect(0, 2, c.getWidth(), c.getHeight() - 2, redondeo, redondeo);

                // Color según estado del botón
                ButtonModel modelo = ((JButton) c).getModel();
                if (modelo.isPressed()) {
                    g2.setColor(oscurecer(fondo, 0.75f)); // Más oscuro al presionar
                } else if (modelo.isRollover()) {
                    g2.setColor(hover); // Hover normal
                } else {
                    g2.setColor(fondo); // Estado normal
                }

                g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight() - 2, redondeo, redondeo);

                // Dibujar el texto centrado
                super.paint(g, c);
                g2.dispose();
            }
        });

        // Repintar al pasar el mouse
        boton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                boton.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                boton.repaint();
            }
        });
    }

    /**
     * Aplica estilo moderno a un campo de texto
     * Incluye borde redondeado y efecto focus
     * 
     * @param campo Campo de texto a estilizar
     */
    public static void estilizarCampoTexto(JTextField campo) {
        campo.setFont(new Font("Arial", Font.PLAIN, 14));
        campo.setForeground(COLOR_NEGRO);
        campo.setBackground(COLOR_BLANCO);
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDE, 1, true),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));

        // Efecto al ganar foco: borde verde institucional
        campo.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                campo.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(COLOR_VERDE, 2, true),
                        BorderFactory.createEmptyBorder(7, 11, 7, 11)));
            }

            @Override
            public void focusLost(FocusEvent e) {
                campo.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(COLOR_BORDE, 1, true),
                        BorderFactory.createEmptyBorder(8, 12, 8, 12)));
            }
        });
    }

    /**
     * Crea un panel tipo "tarjeta" con sombra y bordes redondeados
     * Ideal para formularios y secciones destacadas
     * 
     * @return JPanel estilizado como tarjeta
     */
    public static JPanel crearTarjeta() {
        JPanel tarjeta = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Sombra de la tarjeta
                g2.setColor(new Color(0, 0, 0, 18));
                g2.fillRoundRect(4, 6, getWidth() - 8, getHeight() - 8, 22, 22);

                // Fondo blanco de la tarjeta
                g2.setColor(COLOR_BLANCO);
                g2.fillRoundRect(0, 0, getWidth() - 4, getHeight() - 8, 22, 22);

                g2.dispose();
            }
        };
        tarjeta.setOpaque(false);
        return tarjeta;
    }

    /**
     * Crea una etiqueta centrada con estilo estándar
     * 
     * @param texto Texto de la etiqueta
     * @return JLabel centrado y estilizado
     */
    public static JLabel crearLabelCentrado(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(TITULO.deriveFont(Font.PLAIN, 14));
        label.setForeground(COLOR_NEGRO);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    /**
     * Bloquea el cierre con la X de la ventana
     * Muestra un mensaje indicando que use el botón regresar
     * 
     * @param frame   Ventana a bloquear
     * @param mensaje Mensaje a mostrar
     */
    public static void bloquearCierreConX(JFrame frame, String mensaje) {
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                JPanel panel = new JPanel(new BorderLayout());
                panel.setBackground(COLOR_BLANCO);

                JLabel lblMensaje = new JLabel(mensaje, SwingConstants.CENTER);
                lblMensaje.setFont(PEQUEÑO);
                lblMensaje.setForeground(COLOR_NEGRO);

                panel.add(lblMensaje, BorderLayout.CENTER);

                JOptionPane.showMessageDialog(
                        frame,
                        panel,
                        "Atención",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });
    }

    // ==================== MÉTODOS AUXILIARES ====================

    /**
     * Oscurece un color por un factor dado
     * 
     * @param color  Color original
     * @param factor Factor de oscurecimiento (1.0 = igual, 0.0 = negro)
     * @return Color oscurecido
     */
    private static Color oscurecer(Color color, float factor) {
        return new Color(
                Math.max((int) (color.getRed() * factor), 0),
                Math.max((int) (color.getGreen() * factor), 0),
                Math.max((int) (color.getBlue() * factor), 0));
    }

    /**
     * Aclara un color por un factor dado
     * Útil para crear variantes más claras de colores institucionales
     * 
     * @param color  Color original
     * @param factor Factor de aclarado (1.0 = igual, >1.0 = más claro)
     * @return Color aclarado
     */
    public static Color aclarar(Color color, float factor) {
        return new Color(
                Math.min((int) (color.getRed() * factor), 255),
                Math.min((int) (color.getGreen() * factor), 255),
                Math.min((int) (color.getBlue() * factor), 255));
    }

    /**
     * Obtiene el color institucional según el tipo
     * Útil para asignar colores dinámicamente
     * 
     * @param tipo "primario", "secundario", "acento"
     * @return Color correspondiente
     */
    public static Color getColorInstitucional(String tipo) {
        return switch (tipo.toLowerCase()) {
            case "primario", "azul" -> COLOR_AZUL;
            case "secundario", "verde" -> COLOR_VERDE;
            case "acento", "dorado" -> COLOR_DORADO;
            default -> COLOR_AZUL; // Por defecto, azul institucional
        };
    }
}
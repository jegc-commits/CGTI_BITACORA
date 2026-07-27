package cgti.vistas;

import cgti.clase.*;
import cgti.funcion.ApartadoFuncion;
import cgti.funcion.EquipoFuncion;
import cgti.funcion.RegistroFuncion;
import java.awt.*;
import java.util.List;
import javax.swing.*;

public class RegistroFrame extends JFrame {

    private RegistroFuncion registroFuncion = new RegistroFuncion();
    private ApartadoFuncion apartadoFuncion = new ApartadoFuncion();
    private EquipoFuncion equipoFuncion;
    private JComboBox<Equipo> cmbEquipo;
    private JComboBox<Laboratorio> cmbLab;
    private JTextField txtProposito;
    private JButton btnEntrada, btnSalida;
    private JLabel lblMensaje;
    private Usuario usuarioActual;

    public RegistroFrame(Usuario usuario) {
        this.usuarioActual = usuario;
        // Inyectar RegistroFuncion en EquipoFuncion para consultas de disponibilidad
        equipoFuncion = new EquipoFuncion(registroFuncion);

        setTitle("Registro de Laboratorio - CGTI");
        setSize(500, 650); // Aumentado para mejor espacio
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        Estilos.bloquearCierreConX(this, "usar boton regresar");
        setLayout(new BorderLayout());

        JPanel barra = new JPanel(null) {
            private Image imagenFondo;
            {
                try {
                    imagenFondo = new ImageIcon(getClass().getResource("/imagenes/d2.jpg")).getImage();
                    System.out.println("Imagen cargada correctamente");
                } catch (Exception e) {
                    System.err.println("Error al cargar imagen: " + e.getMessage());
                    imagenFondo = null;
                }
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (imagenFondo != null) {
                    g.drawImage(imagenFondo, 0, 0, getWidth(), getHeight(), this);
                } else {
                    g.setColor(Estilos.COLOR_AZUL);
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };

        barra.setPreferredSize(new Dimension(500, 100));
        add(barra, BorderLayout.NORTH);

        // ----- Panel del formulario -----
        JPanel panelFormulario = new JPanel();
        panelFormulario.setLayout(new BoxLayout(panelFormulario, BoxLayout.Y_AXIS));
        panelFormulario.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        panelFormulario.setBackground(Estilos.COLOR_BLANCO);

        JLabel lblTitulo = new JLabel("Registro de Laboratorio");
        lblTitulo.setFont(Estilos.GRANDE);
        lblTitulo.setForeground(Estilos.COLOR_NEGRO);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblUsuario = new JLabel("Usuario: " + usuario.getNombre() + " " + usuario.getApellidoPaterno());
        lblUsuario.setFont(Estilos.TITULO.deriveFont(Font.PLAIN, 14));
        lblUsuario.setForeground(Estilos.COLOR_NEGRO);
        lblUsuario.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Inicializar el JTextField para el propósito
        txtProposito = new JTextField();
        txtProposito.setFont(Estilos.TITULO.deriveFont(Font.PLAIN, 14));
        txtProposito.setForeground(Estilos.COLOR_NEGRO);
        txtProposito.setAlignmentX(Component.CENTER_ALIGNMENT);

        cmbLab = new JComboBox<>(Laboratorio.values());

        // MODIFICACIÓN: Configurar el renderer del JComboBox de equipos
        cmbEquipo = new JComboBox<>();
        configurarRendererEquipos(); // Llamar al nuevo método

        actualizarComboEquipos((Laboratorio) cmbLab.getSelectedItem());

        // Configurar tamaño máximo
        Dimension comboSize = new Dimension(400, 30);
        cmbLab.setMaximumSize(comboSize);
        txtProposito.setMaximumSize(comboSize);
        cmbEquipo.setMaximumSize(comboSize);

        // MODIFICACIÓN: Agregar etiqueta para mostrar detalles del equipo
        JLabel lblInfoEquipo = new JLabel(" ");
        lblInfoEquipo.setFont(new Font("Arial", Font.ITALIC, 12));
        lblInfoEquipo.setForeground(Estilos.COLOR_GRIS);
        lblInfoEquipo.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Eventos de cambio
        cmbLab.addActionListener(e -> {
            Laboratorio lab = (Laboratorio) cmbLab.getSelectedItem();
            actualizarComboEquipos(lab);
            validarEstado();
            actualizarInfoEquipo(lblInfoEquipo); // MODIFICACIÓN
        });

        // MODIFICACIÓN: Agregar listener para actualizar información del equipo
        cmbEquipo.addActionListener(e -> {
            validarEstado();
            actualizarInfoEquipo(lblInfoEquipo);
        });

        lblMensaje = new JLabel(" ");
        lblMensaje.setForeground(Color.RED);
        lblMensaje.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblMensaje.setFont(new Font("Arial", Font.PLAIN, 12));

        // Botones
        btnEntrada = new JButton("Registrar Entrada");
        btnSalida = new JButton("Registrar Salida");
        JButton btnRegresar = new JButton("Regresar");

        // Estilizar botones
        Estilos.estilizarBotonRedondo(btnEntrada, Estilos.COLOR_VERDE, Estilos.COLOR_BLANCO, 30);
        Estilos.estilizarBotonRedondo(btnSalida, Estilos.COLOR_VERDE, Estilos.COLOR_BLANCO, 30);
        Estilos.estilizarBotonRedondo(btnRegresar, Estilos.COLOR_VERDE, Estilos.COLOR_BLANCO, 30);

        // Configurar tamaño de botones
        Dimension botonSize = new Dimension(200, 40);
        btnEntrada.setMaximumSize(botonSize);
        btnSalida.setMaximumSize(botonSize);
        btnRegresar.setMaximumSize(botonSize);

        btnEntrada.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnSalida.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnRegresar.setAlignmentX(Component.CENTER_ALIGNMENT);

        validarEstado(); // estado inicial

        if (usuario.getRol() == Rol.OBSERVADOR) {
            cmbLab.setEnabled(false);
            txtProposito.setEnabled(false);
            cmbEquipo.setEnabled(false);
            btnEntrada.setEnabled(false);
            btnSalida.setEnabled(false);
            lblMensaje.setForeground(Estilos.COLOR_GRIS);
            lblMensaje.setText("Modo de solo lectura: el observador no puede registrar entradas/salidas");
        }

        // --- Acciones de los botones ---
        btnEntrada.addActionListener(e -> {
            Equipo equipo = (Equipo) cmbEquipo.getSelectedItem();
            Laboratorio laboratorio = (Laboratorio) cmbLab.getSelectedItem();
            String proposito = txtProposito.getText().trim();

            if (equipo == null) {
                lblMensaje.setForeground(Color.RED);
                lblMensaje.setText("Seleccione un equipo válido.");
                return;
            }

            if (proposito.isEmpty()) {
                lblMensaje.setForeground(Color.RED);
                lblMensaje.setText("Por favor, escriba el propósito.");
                return;
            }

            if (apartadoFuncion.laboratorioBloqueadoAhora(laboratorio)) {
                lblMensaje.setForeground(Color.RED);
                lblMensaje.setText("El laboratorio " + laboratorio + " está bloqueado en este horario.");
                return;
            }

            if (!equipoFuncion.estaDisponible(equipo.getId())) {
                lblMensaje.setForeground(Color.RED);
                lblMensaje.setText("El equipo " + equipo.getNombreEqp() + " ya está en uso.");
                return;
            }

            if (registroFuncion.tieneSesionActiva(usuarioActual.getMatricula())) {
                lblMensaje.setForeground(Color.RED);
                lblMensaje.setText("Ya tienes una entrada activa. Registra tu salida primero.");
                return;
            }

            // MODIFICACIÓN: Mensaje de confirmación con nombre del equipo
            String resultado = registroFuncion.registrarEntrada(
                    usuarioActual,
                    equipo,
                    laboratorio,
                    proposito);

            if (resultado.contains("correctamente") || resultado.contains("éxito")) {
                lblMensaje.setForeground(new Color(0, 128, 0));
                lblMensaje.setText("✓ Entrada registrada - Equipo: " + equipo.getNombreEqp() +
                        " - Lab: " + laboratorio);
                txtProposito.setText("");
                validarEstado();
                actualizarComboEquipos(laboratorio);
                actualizarInfoEquipo(lblInfoEquipo);
            } else {
                lblMensaje.setForeground(Color.RED);
                lblMensaje.setText(resultado);
            }
        });

        btnSalida.addActionListener(e -> {
            // MODIFICACIÓN: Obtener información del equipo antes de registrar salida
            Equipo equipoActual = (Equipo) cmbEquipo.getSelectedItem();
            String resultado = registroFuncion.registrarSalida(usuarioActual.getMatricula());

            if (resultado.contains("correctamente") || resultado.contains("éxito")) {
                lblMensaje.setForeground(new Color(0, 128, 0));
                if (equipoActual != null) {
                    lblMensaje.setText("✓ Salida registrada - Equipo: " + equipoActual.getNombreEqp() + " liberado");
                } else {
                    lblMensaje.setText("✓ Salida registrada correctamente");
                }
                actualizarComboEquipos((Laboratorio) cmbLab.getSelectedItem());
                validarEstado();
                actualizarInfoEquipo(lblInfoEquipo);
            } else {
                lblMensaje.setForeground(Color.RED);
                lblMensaje.setText(resultado);
            }
        });

        btnRegresar.addActionListener(e -> {
            dispose();
        });

        // Ensamblar panel
        panelFormulario.add(lblTitulo);
        panelFormulario.add(Box.createVerticalStrut(10));
        panelFormulario.add(lblUsuario);
        panelFormulario.add(Box.createVerticalStrut(20));
        panelFormulario.add(Estilos.crearLabelCentrado("Laboratorio:"));
        panelFormulario.add(cmbLab);
        panelFormulario.add(Box.createVerticalStrut(10));
        panelFormulario.add(Estilos.crearLabelCentrado("Equipo:"));
        panelFormulario.add(cmbEquipo);
        panelFormulario.add(Box.createVerticalStrut(5));
        panelFormulario.add(lblInfoEquipo); // MODIFICACIÓN: Agregar etiqueta de info
        panelFormulario.add(Box.createVerticalStrut(10));
        panelFormulario.add(Estilos.crearLabelCentrado("Propósito:"));
        panelFormulario.add(txtProposito);
        panelFormulario.add(Box.createVerticalStrut(20));
        panelFormulario.add(btnEntrada);
        panelFormulario.add(Box.createVerticalStrut(8));
        panelFormulario.add(btnSalida);
        panelFormulario.add(Box.createVerticalStrut(8));
        panelFormulario.add(btnRegresar);
        panelFormulario.add(Box.createVerticalStrut(15));
        panelFormulario.add(lblMensaje);

        add(panelFormulario, BorderLayout.CENTER);
    }

    // ---------- MÉTODOS NUEVOS ----------

    /**
     * Configura el renderer del JComboBox para mostrar el nombre del equipo
     * en lugar de la representación por defecto del objeto
     */
    private void configurarRendererEquipos() {
        cmbEquipo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                // Llamar al método padre para mantener el estilo
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                if (value instanceof Equipo) {
                    Equipo equipo = (Equipo) value;
                    // Mostrar el nombre del equipo y opcionalmente el ID
                    setText(equipo.getNombreEqp());

                    // Opcional: Mostrar también el ID para equipos con el mismo nombre
                    // setText(equipo.getNombreEqp() + " (ID: " + equipo.getId() + ")");

                    // Cambiar color si el equipo no está disponible
                    if (!equipoFuncion.estaDisponible(equipo.getId())) {
                        setForeground(Color.RED);
                    } else {
                        setForeground(isSelected ? list.getSelectionForeground() : Estilos.COLOR_NEGRO);
                    }
                }
                return this;
            }
        });
    }

    /**
     * Actualiza la etiqueta de información del equipo seleccionado
     */
    private void actualizarInfoEquipo(JLabel lblInfo) {
        Equipo equipo = (Equipo) cmbEquipo.getSelectedItem();
        if (equipo != null) {
            StringBuilder info = new StringBuilder();
            info.append("Equipo seleccionado: ").append(equipo.getNombreEqp());
            info.append(" | ID: ").append(equipo.getId());

            // Agregar estado de disponibilidad
            if (equipoFuncion.estaDisponible(equipo.getId())) {
                info.append(" | Estado: Disponible ✓");
                lblInfo.setForeground(new Color(0, 128, 0));
            } else {
                info.append(" | Estado: Ocupado ✗");
                lblInfo.setForeground(Color.RED);
            }

            lblInfo.setText(info.toString());
        } else {
            lblInfo.setText("No hay equipo seleccionado");
            lblInfo.setForeground(Estilos.COLOR_GRIS);
        }
    }

    // ---------- Métodos auxiliares (MODIFICADOS) ----------

    private void actualizarComboEquipos(Laboratorio lab) {
        cmbEquipo.removeAllItems();
        // Se muestran todos los equipos del laboratorio
        List<Equipo> equipos = equipoFuncion.listarPorLaboratorio(lab.name());
        for (Equipo equipo : equipos) {
            cmbEquipo.addItem(equipo);
        }

        // MODIFICACIÓN: Seleccionar el primer equipo disponible si existe
        if (cmbEquipo.getItemCount() > 0) {
            for (int i = 0; i < cmbEquipo.getItemCount(); i++) {
                Equipo eq = cmbEquipo.getItemAt(i);
                if (equipoFuncion.estaDisponible(eq.getId())) {
                    cmbEquipo.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    /**
     * Valida tanto la existencia de equipos como la disponibilidad del
     * seleccionado. Habilita/deshabilita los botones según corresponda.
     */
    private void validarEstado() {
        // 1. ¿Hay equipos registrados en este laboratorio?
        if (cmbEquipo.getItemCount() == 0) {
            btnEntrada.setEnabled(false);
            btnSalida.setEnabled(false);
            lblMensaje.setForeground(Color.RED);
            lblMensaje.setText("No hay equipos registrados en este laboratorio.");
            return;
        }

        Equipo seleccionado = (Equipo) cmbEquipo.getSelectedItem();
        if (seleccionado == null) {
            btnEntrada.setEnabled(false);
            btnSalida.setEnabled(false);
            lblMensaje.setForeground(Color.RED);
            lblMensaje.setText("Seleccione un equipo.");
            return;
        }

        // 2. Verificar si el usuario ya tiene una sesión activa
        if (registroFuncion.tieneSesionActiva(usuarioActual.getMatricula())) {
            btnEntrada.setEnabled(false);
            btnSalida.setEnabled(true);
            lblMensaje.setForeground(Color.RED);
            lblMensaje.setText("Ya tienes una sesión activa. Debes registrar tu salida.");
            return;
        }

        // 3. Verificar si el laboratorio está bloqueado en este horario
        Laboratorio lab = (Laboratorio) cmbLab.getSelectedItem();
        if (apartadoFuncion.laboratorioBloqueadoAhora(lab)) {
            btnEntrada.setEnabled(false);
            btnSalida.setEnabled(false);
            lblMensaje.setForeground(Color.RED);
            lblMensaje.setText("El laboratorio " + lab + " está bloqueado en este horario.");
            return;
        }

        // 4. Verificar disponibilidad física del equipo seleccionado
        boolean disponible = equipoFuncion.estaDisponible(seleccionado.getId());

        if (disponible) {
            btnEntrada.setEnabled(true);
            btnSalida.setEnabled(false);
            lblMensaje.setForeground(new Color(0, 128, 0));
            lblMensaje.setText("✓ " + seleccionado.getNombreEqp() + " disponible para uso.");
        } else {
            btnEntrada.setEnabled(false);
            btnSalida.setEnabled(false);
            lblMensaje.setForeground(Color.RED);
            lblMensaje.setText("✗ " + seleccionado.getNombreEqp() + " está ocupado actualmente.");
        }
    }
}
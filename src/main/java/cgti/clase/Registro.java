package cgti.clase;

import java.time.LocalDateTime;

public class Registro {

    private Long id;
    private LocalDateTime horaEntrada;
    private LocalDateTime horaSalida;
    private String proposito;
    private Laboratorio laboratorio;
    private Usuario usuario;
    private Long equipoId;
    private String equipoNombre;
    private Long apartadoId;
    private boolean incidencia; // true si la salida se marco automaticamente por exceso de tiempo
    private String notaIncidencia;

    // Getters y Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getHoraEntrada() {
        return horaEntrada;
    }

    public void setHoraEntrada(LocalDateTime horaEntrada) {
        this.horaEntrada = horaEntrada;
    }

    public LocalDateTime getHoraSalida() {
        return horaSalida;
    }

    public void setHoraSalida(LocalDateTime horaSalida) {
        this.horaSalida = horaSalida;
    }

    public String getProposito() {
        return proposito;
    }

    public void setProposito(String proposito) {
        this.proposito = proposito;
    }

    public Laboratorio getLaboratorio() {
        return laboratorio;
    }

    public void setLaboratorio(Laboratorio laboratorio) {
        this.laboratorio = laboratorio;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Long getEquipoId() {
        return equipoId;
    }

    public void setEquipoId(Long equipoId) {
        this.equipoId = equipoId;
    }

    public Long getApartadoId() {
        return apartadoId;
    }

    public void setApartadoId(Long apartadoId) {
        this.apartadoId = apartadoId;
    }

    public boolean isIncidencia() {
        return incidencia;
    }

    public void setIncidencia(boolean incidencia) {
        this.incidencia = incidencia;
    }

    public String getNotaIncidencia() {
        return notaIncidencia;
    }

    public void setNotaIncidencia(String notaIncidencia) {
        this.notaIncidencia = notaIncidencia;
    }

    public String getEquipoNombre() {
        return equipoNombre;
    }

    public void setEquipoNombre(String equipoNombre) {
        this.equipoNombre = equipoNombre;
    }
}

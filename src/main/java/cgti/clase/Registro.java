
package cgti.clase;

import java.time.LocalDateTime;

public class Registro {
    private Long id;
    private LocalDateTime horaEntrada;
    private LocalDateTime horaSalida;
    private String proposito;
    private String laboratorio;
    private Usuario usuario;
    private Long equipoId;
    private String equipoNombre;
    private boolean incidencia;

    public Registro() {
    }

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

    public String getLaboratorio() {
        return laboratorio;
    }

    public void setLaboratorio(String laboratorio) {
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

    public String getEquipoNombre() {
        return equipoNombre;
    }

    public void setEquipoNombre(String equipoNombre) {
        this.equipoNombre = equipoNombre;
    }

    public boolean isIncidencia() {
        return incidencia;
    }

    public void setIncidencia(boolean incidencia) {
        this.incidencia = incidencia;
    }

    @Override
    public String toString() {
        return "Registro #" + id + " - " + laboratorio;
    }
}
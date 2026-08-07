package cgti.clase; // pertenece a pacquete

import java.time.LocalDateTime; // libreria importada para las fechas

public class Apartado {

    private Long id;
    private Usuario usuario;
    private String laboratorio;
    private Equipo equipo;
    private DiaSemana diaSemana;
    private LocalDateTime horaInicio;
    private LocalDateTime horaFin;
    private String proposito;
    private EstadoApartado estado;

    // geters y seters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getLaboratorio() {
        return laboratorio;
    }

    public void setLaboratorio(String laboratorio) {
        this.laboratorio = laboratorio;
    }

    public Equipo getEquipo() {
        return equipo;
    }

    public void setEquipo(Equipo equipo) {
        this.equipo = equipo;
    }

    public DiaSemana getDiaSemana() {
        return diaSemana;
    }

    public void setDiaSemana(DiaSemana diaSemana) {
        this.diaSemana = diaSemana;
    }

    public LocalDateTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalDateTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public LocalDateTime getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(LocalDateTime horaFin) {
        this.horaFin = horaFin;
    }

    public String getProposito() {
        return proposito;
    }

    public void setProposito(String proposito) {
        this.proposito = proposito;
    }

    public EstadoApartado getEstado() {
        return estado;
    }

    public void setEstado(EstadoApartado estado) {
        this.estado = estado;
    }
}

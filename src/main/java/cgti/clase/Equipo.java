package cgti.clase; // pertenece al paquete 

public class Equipo {

  private Long id;
  private String nombreEqp;
  private Laboratorio laboratorio;
  private String software;

  // setters y geters
  public String getSoftware() {
    return software;
  }

  public void setSoftware(String software) {
    this.software = software;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getNombreEqp() {
    return nombreEqp;
  }

  public void setNombreEqp(String nombreEqp) {
    this.nombreEqp = nombreEqp;
  }

  public Laboratorio getLaboratorio() {
    return laboratorio;
  }

  public void setLaboratorio(Laboratorio laboratorio) {
    this.laboratorio = laboratorio;
  }

  @Override
  public String toString() {
    return nombreEqp;
  }

}

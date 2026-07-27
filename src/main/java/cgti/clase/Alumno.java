package cgti.clase; // pertenece al paquete 

public class Alumno extends Usuario { // alumno hereda atributos de Usuario

  private String cuatrimestre;
  private String carrera;
  private String grupo;

  public String getGrupo() {
    return grupo;
  }

  public void setGrupo(String grupo) {
    this.grupo = grupo;
  }

  public String getCuatrimestre() {
    return cuatrimestre;
  }

  public void setCuatrimestre(String cuatrimestre) {
    this.cuatrimestre = cuatrimestre;
  }

  public String getCarrera() {
    return carrera;
  }

  public void setCarrera(String carrera) {
    this.carrera = carrera;
  }

}

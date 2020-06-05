package model;

/**
 * Clase que modelara la informacion y utilidades de un usuario en general de la aplicacion.
 * */
public abstract class User {
	public static final int ADMIN = 1;			//Constante para identificar el tipo de usuario administrador
	public static final int CLIENT = 2;			//Constante para identificar el tipo de usuario cliente
	public static final int NOT_VALID = 3;		//Constante para identificar que no se ha establecido el identificador
	
	private String rut;							//Rut del usuario.
	private int idUser;							//Identificador del identificador del usuario, 1 -> Admin, 2-> Cliente.
	
	/**
	 * Constructor por defecto, establece los valores iniciales para los atributos de la clase.
	 * */
	public User() {
		this.rut = "00000000-0";
		this.idUser = NOT_VALID;
	}
	
	/**
	 * Contructor que establece todos los atributos de la clase.
	 * @param rut rut inicial del usuario.
	 * @param idUser identificador inicial del usuario.
	 * */
	public User(String rut, int idUser) {
		this.rut = rut;
		this.idUser = idUser;
	}
	
	/**
	 * Metodo que establece un nuevo rut para el usuario.
	 * @param rut nuevo rut del usuario.
	 * */
	public void setRut(String rut) {
		this.rut = rut;
	}
	
	/**
	 * Metodo que establece el identificador del usuario.
	 * @param idUser nuevo identificador del usuario.
	 * */
	public void setIdUser(int idUser) {
		this.idUser = idUser;
	}
	
	/**
	 * @return rut actual del usuario.
	 * */
	public String getRut() {
		return this.rut;
	}
	
	/**
	 * @return identificador actual del usuario.
	 * */
	public int idUser() {
		return this.idUser;
	}
}

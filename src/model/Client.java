package model;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Clase que guardara la informacion de un cliente del banco (nombre, rut, cuentas activas, etc).
 * @author DanSantos
 * @version 12-04-2020 
 * */

public class Client {
	
	private String name;								//Nombre del cliente.
	private String rut;									//Rut del cliente.
	private Account rutAccount;							//Informacion de la cuenta rut del cliente.
	private Account savingAccount;						//Informacion de la cuenta de ahorro del cliente.
	private HashMap<String, Addressee> addressees;		//Destinatarios del cliente.
	
	/**
	 * Constructor predeterminado, establece los valores iniciales de los atributos, e instancia
	 * los atributos objeto de la clase.
	 * */
	public Client() {
		this("none", "00000000-0");
	}
	
	/**
	 * Constructor, establece el nombre y el rut del cliente, ademas instancia los atributos
	 * objeto de la clase.
	 * @param name, nombre del cliente.
	 * @param rut, rut del cliente, se asume que es valido.
	 * */
	public Client(String name, String rut) {
		this.name = name;
		this.rut = rut;
		this.addressees = new HashMap<String, Addressee>();
		this.rutAccount = new Account();
		this.savingAccount = new Account();
	}
	
	/**
	 * Agrega un nuevo destinatario, valida que no haya un destinatario con el mismo numero de
	 * cuenta ya ingresado, si es asi lo agrega, sino no hace nada.
	 * @param newAddressee destinatario a agregar, se asume que se han inicializado los datos.
	 * */
	public void addAddressee(Addressee newAddressee) {
		if(!this.addressees.containsKey(newAddressee.getAccountNumber()))
			this.addressees.put(newAddressee.getAccountNumber(), newAddressee);
	}
	
	/**
	 * Elimina un destinatario del mapa, valida que exista un destinatario guardado con el
	 * numero de cuenta ingresado, si es asi lo elimina, sino no hace nada.
	 * @param accountNumber numero de cuenta asociado al destinatario a eliminar.
	 * */
	public void removeAddressee(String accountNumber) {
		if(this.addressees.containsKey(accountNumber))
			this.addressees.remove(accountNumber);
	}
	
	/**
	 * Comprueba que exista un destinatario guardado con el numero de cuenta ingresado.
	 * @param accountNumber numero de cuenta del destinatario a buscar.
	 * @return true si ya existe un destinatario guardado con ese numero de cuenta, false en caso contrario.
	 * */
	public boolean existsAddressee(String accountNumber) {
		return this.addressees.containsKey(accountNumber);
	}
	
	/**
	 * @return nombre actual del cliente.
	 * */
	public String getName() {
		return this.name;
	}
	
	/**
	 * @return rut actual del cliente.
	 * */
	public String getRut() {
		return this.rut;
	}
	
	/**
	 * Obtiene la cuenta correspondiente al tipo de cuenta ingresado.
	 * @param typeAccount tipo de cuenta que se quiere obtener.
	 * @return cuenta solicitada.
	 * */
	public Account getAccount(TypeAccount typeAccount) {
		return (typeAccount == TypeAccount.RUT_ACCOUNT) ? this.rutAccount : this.savingAccount;
	}
	
	/**
	 * @return lista con los destinatarios guardados.
	 * */
	public ArrayList<Addressee> getAddressees() {
		//Lista que almacenara los destinatarios del cliente y sera retornada.
		ArrayList<Addressee> list;
		
		//Se instancia la lista y se le establece como largo la cantidad de destinatarios que tiene el cliente.
		list = new ArrayList<Addressee>(this.addressees.size());
		
		//Se obtiene el iterador del mapa de destinatarios del cliente.
		Iterator<String> iterator = this.addressees.keySet().iterator();
		
		while(iterator.hasNext()) {
			//Se obtiene la clave (numero de cuenta del destinatario actual)
			String accountNumber = iterator.next();
			
			//Se guarda el destinatario actual en la lista
			list.add(this.addressees.get(accountNumber));
		}
		
		return list;
	}
	
	/**
	 * Establece el nombre del cliente.
	 * @param newName nuevo nombre del cliente.
	 * */
	public void setName(String newName) { 
		this.name = newName;
	}
	
	/**
	 * Establece le rut del cliente, se asume que es un rut valido.
	 * @param newRut nuevo rut del cliente.
	 * */
	public void setRut(String newRut) {
		this.rut = newRut;
	}
	
	/**
	 * Establece una cuenta del cliente, la cuenta dependera del tipo de cuenta ingresada.
	 * @param account cuenta que se asociara al usuario.
	 * */
	public void setAccount(Account account) {
		TypeAccount typeAccount = Account.getTypeAccount(account.getAccountNumber());
		
		if(typeAccount == TypeAccount.RUT_ACCOUNT)
			this.rutAccount = account;
		else if(typeAccount == TypeAccount.SAVING_ACCOUNT)
			this.savingAccount = account;
			
	}
}

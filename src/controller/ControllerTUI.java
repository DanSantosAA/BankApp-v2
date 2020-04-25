package controller;
import view.*;
import model.*;
import java.util.ArrayList;

/**
 * Clase correspondiente al controlador del patron MVC
 * */

public class ControllerTUI {

	/**
	 * Contructor, establece los atributos de la clase
	 * @param bank intancia de la clase modelo
	 * */
	public 	ControllerTUI(BankApp bank) {
		this.bank = bank;
	}

	/**
	 * Contructor predeterminado
	 * */
	public 	ControllerTUI() {
	}

	/**
	 * Establece la instacia del atributo de la clase vista a ser controlada.
	 * */
	public void setView(ViewTUI viewTUI) {
		this.viewTUI = viewTUI;
	}
	
	/**
	 * Establece la instancia del atributo de la clase modelo.
	 * */
	public void setModel(BankApp bank) {
		this.bank = bank;
	}

	/**
	 * Obtiene la informacion del nuevo destinatario a guardar y valida que no exista y en 
	 * los destinatarios guardados del cliente.
	 * */
	public void addAddressee() {
		String name;
		String accountNumber;
		boolean isFavorite;

		this.viewTUI.setOutput("Nombre: ");
		name = this.viewTUI.getInput();

		this.viewTUI.setOutput("Numero de cuenta: ");
		accountNumber = this.viewTUI.getInput();

		this.viewTUI.setOutput("Guardarlo como favorito (1 - si, 2 - no): ");
		isFavorite = (this.viewTUI.getInput().equals("1")) ? true : false;
		
		Client client = this.bank.getClient();
		if(!Account.isValid(accountNumber)) {
			this.viewTUI.setOutput("Numero de cuenta ingresado no valido\n");
		}
		else if(client.existsAddressee(accountNumber)) {
			this.viewTUI.setOutput("Ya existe un destinatario con el numero de cuenta ingresado\n");
		}
		else {
			client.addAddressee(new Addressee(accountNumber, name, isFavorite));
			this.viewTUI.setOutput("Destinatario guardado correctamente\n");
		}
	}
	
	/**
	 * Muestra los destinatarios guardados por el cliente.
	 * */
	public void showAddressees() {
		ArrayList<Addressee> addressees = this.bank.getClient().getAddressees();
		
		for(Addressee addressee : addressees) {
			this.viewTUI.setOutput("Nombre: " + addressee.getName() + " - Numero de cuenta: " + addressee.getAccountNumber() + '\n');
		}
	}
	
	/**
	 * Muestra los destinatarios guardados como favoritos por el cliente.
	 * */
	public void showFavoritesAddressees() {
		ArrayList<Addressee> addressees = this.bank.getClient().getAddressees();
		
		for(Addressee addressee : addressees) {
			if(addressee.isFavorite())
				this.viewTUI.setOutput("Nombre: " + addressee.getName() + " - Numero de cuenta: " + addressee.getAccountNumber() + '\n');
		}
	}
	
	/**
	 * Obtiene los datos del destinatario a eliminar, valida que exista y lo elimina.
	 * */
	public void removeAddressee() {
		String accountNumber;

		this.viewTUI.setOutput("Numero de cuenta del destinatario a eliminar: ");
		accountNumber = this.viewTUI.getInput();

		Client client = this.bank.getClient();
		if(!Account.isValid(accountNumber)) {
			this.viewTUI.setOutput("Numero de cuenta ingresado no valido\n");
		}
		else if(client.existsAddressee(accountNumber)) {
			client.removeAddressee(accountNumber);
			this.viewTUI.setOutput("Destinatario eliminado correctamente\n");
		}
		else {
			this.viewTUI.setOutput("No existe un destinatario con el numero de cuenta ingresado\\n");
		}
	}
	
	/**
	 * Realiza una transferencia de la cuenta origen elegida por el usuario, a la  cuenta destino.
	 * */
	public void makeTransfer() {
		Account originAccount = chooseMyAccount();
		
		if(originAccount != null) {
			Account destinyAccount = chooseDestinyAccount();
			
			this.viewTUI.setOutput("Cuenta origen: \n\n");
			originAccount.showInfo();
			
			this.viewTUI.setOutput("\n\nCuenta destino: \n\n");
			destinyAccount.showInfo();
		}
	}
	
	/**
	 * Muestra la cuenta seleccionada por el usuario
	 * */
	public void showAccount() {
		Account account = chooseMyAccount();

		if(account != null)
			account.showInfo();
	}
	
	/**
	 * El usuario selecciona la cuenta propia en la que quiere operar.
	 * */
	public Account chooseMyAccount() {
		Client client = this.bank.getClient();
		Account account;
		int option;
		
		try {
			this.viewTUI.setOutput("Seleccione cuenta (1 - cuenta rut, 2 - cuenta ahorro): ");
			option = Integer.parseInt(this.viewTUI.getInput());
		}
		catch (NumberFormatException e) {
			option = -1;
		}
		
		if(option == 1) {
			account = client.getAccount(TypeAccount.RUT_ACCOUNT);
		}
		else if(option == 2){
			account = client.getAccount(TypeAccount.SAVING_ACCOUNT);
		}
		else {
			this.viewTUI.setOutput("Opcion ingresada no valida\n");
			account = null;
		}
		
		return account;
	}
	
	/**
	 * Obtiene la cuenta seleccionada por el usuario dentro de las cuentas del sistema.
	 * */
	public Account chooseDestinyAccount() {
		String accountNumber;
		Account account = null;
		
		this.viewTUI.setOutput("Ingrese el numero de cuenta: ");
		accountNumber = this.viewTUI.getInput();
		
		if(this.bank.existsAccount(accountNumber))
			account = this.bank.getAccount(accountNumber);
		else
			this.viewTUI.setOutput("No se encontro una cuenta asociado al numero de cuenta ingresado");
		
		
		return account;
	}

	//Atributos
	private ViewTUI viewTUI;
	private BankApp bank;
}
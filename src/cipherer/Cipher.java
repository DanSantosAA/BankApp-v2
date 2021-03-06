package cipherer;

/**
 * Clase que modela un cifrador usando el algoritmo AES, implementa el patron singleton.
 * Se puede cifrar/decifrar otorgando una clave, o usando una clave anteriormente establecida, por lo tanto
 * si se va a ocupar una sola clave, hay que preocuparse de que se haya establecido almenos una vez usando el metodo
 * setKey o con las versiones de los metodos cifrate y decifrate que reciben una clave por parametro.
 * La clave debe tener almenos 16 caracteres que son los unicos que se tomaran en cuenta como clave.
 * @author DanSantosAA
 * @version 25-05-2020
 * */

public class Cipher {

	//Atributos
	private char[][] key;									//Clave de cifrado y decifrado actual
	private static final Cipher cipher = new Cipher();		//Unica instancia de la clase

	/**
	 * Funcion encargada de obtener la unica instancia del cipher puesto que la clas aplica el patron singleton.
	 * @return unica instancia de la clase.
	 * */
	public static Cipher getInstance() {
		return cipher;
	}

	/**
	 * Constructor por defecto de la clase, dimensiona la matriz que guardara tanto la clave como las subclaves.
	 * */
	private Cipher() {
		this.key = new char[MATRIX_ORDER * (CANTS_ROUNDS + 1)][MATRIX_ORDER];
	}

	/**
	 * Metodo que establece una nueva clave, valida que no sea null y que contenga almenos 16 caracteres.
	 * @param new_key la nueva clave que se usara y sobre la cual se calcularan las demas subclaves.
	 * */
	public void setKey(String new_key) {
		if((new_key != null) && (new_key.length() >= 16)) {

			for(int i = 0; i < MATRIX_ORDER; ++i) {
				for(int j = 0; j < MATRIX_ORDER; ++j) {
					this.key[i][j] = new_key.charAt((i * MATRIX_ORDER) + j);
				}
			}

			calculateSubKeys();
		}
	}

	/**
	 * Metodo que cifra el texto usando la clave ingresada, y devuelve el texto cifrado con AES.
	 * @param text texto a cifrar.
	 * @param key clave del cifrado.
	 * @return texto cifrado con la clave ingresada.
	 * */
	public String cifrate(String text, String key) {
		if(key != null)
			setKey(key);

		char states[][][] = expandBlocks(text, CIFRATE_MODE);

		for(char state[][] : states) {
			addRoundKey(state, 0);

			for(int round = 1; round < CANTS_ROUNDS; ++round) {
				subBytes(state);
				shiftRows(state);
				mixColumns(state);
				addRoundKey(state, round);
			}

			subBytes(state);
			shiftRows(state);
			addRoundKey(state, CANTS_ROUNDS);
		}

		return getText(states, CIFRATE_MODE);
	}

	/**
	 * Metodo que cifra el texto ingresado usando la clave establecida anteriormente, y devuelve el texto cifrado con AES.
	 * @param text texto a cifrar.
	 * @return texto cifrado con la clave ingresada.
	 * */
	public String cifrate(String text) {
		return cifrate(text, null);
	}

	/**
	 * Metodo que decifra el texto cifrado usando la clave ingresada, y devuelve el texto decifrado.
	 * @param text texto a decifrar.
	 * @param key clave del decifrado.
	 * @return texto decifrado con la clave ingresada.
	 * */
	public String decifrate(String text, String key) {
		if(key != null)
			setKey(key);

		char states[][][] = expandBlocks(text, DECIFRATE_MODE);

		for(char state[][] : states) {
			addRoundKey(state, CANTS_ROUNDS);
			invShiftRows(state);
			invSubBytes(state);

			for(int round = CANTS_ROUNDS - 1; round > 0; --round) {
				addRoundKey(state, round);
				invMixColumns(state);
				invShiftRows(state);
				invSubBytes(state);
			}

			addRoundKey(state, 0);
		}

		return getText(states, DECIFRATE_MODE);
	}

	/**
	 * Metodo que decifra el texto cifrado ingresado usando la clave establecida anteriormente, y devuelve el texto decifrado.
	 * @param text texto a decifrar.
	 * @return texto decifrado con la clave ingresada.
	 * */
	public String decifrate(String text) {
		return decifrate(text, null);
	}

	/* ------------------------- Funciones auxiliares -------------------------- */
	/**
	 * Metodo que calcula las 10 subclaves a partir de la clave actual.
	 * */
	private void calculateSubKeys() {
		for(int currentKey = 1; currentKey <= CANTS_ROUNDS; ++currentKey) {
			rotWord(this.key[(currentKey * MATRIX_ORDER) - 1], this.key[currentKey * MATRIX_ORDER]);

			for(int j = 0; j < MATRIX_ORDER; ++j) {
				this.key[currentKey * MATRIX_ORDER][j] = Tables.sbox[this.key[currentKey * MATRIX_ORDER][j]];

				this.key[currentKey * MATRIX_ORDER][j] ^= this.key[(currentKey - 1) * MATRIX_ORDER][j];
			}

			this.key[currentKey * MATRIX_ORDER][0] ^= Tables.rcon[currentKey - 1];

			for(int j = 1; j < MATRIX_ORDER; ++j) {
				xorBtweenVector(
					this.key[(currentKey * MATRIX_ORDER) + j - 1],
					this.key[((currentKey - 1) * MATRIX_ORDER) + j],
					this.key[(currentKey * MATRIX_ORDER) + j]);
			}
		}
	}

	/**
	 * Metodo que expandira el texto ingresado en bloques de 128 bits, donde cada uno sera cifrado, dependiendo del modo
	 * le agregara un bloque extra de relleno si esta en modo de cifrado.
	 * */
	private char[][][] expandBlocks(String text, byte mode) {
		//Indica cuantos bloques van a ser cifrados o decifrados
		int cants_blocks = (text.length() / RADIX);

		//Si esta en modo cifrado, se le agrega el bloque de relleno
		if(CIFRATE_MODE == mode)
			cants_blocks++;

		//Se dimensiona la matriz de bloques con el tamaño establecido previamente
		char states[][][] = new char[cants_blocks][MATRIX_ORDER][MATRIX_ORDER];

		//Guardara la posicion del caracter actual del texto a expandir
		int currentChar = 0;

		for(int i = 0; i < cants_blocks; ++i) {
			for(int j = 0; j < MATRIX_ORDER; ++j) {
				for(int k = 0; k < MATRIX_ORDER; ++k) {

					//Si todavia quedan caracteres en el texto se ingresa en el estado actual sino se guarda el relleno
					if(currentChar < text.length()) {
						states[i][j][k] = text.charAt(currentChar);
						currentChar++;
					}
					else {
						states[i][j][k] = (char) ((RADIX * cants_blocks) - currentChar);
					}
				}
			}
		}

		return states;
	}

	/**
	 * Metodo que obtiene el texto a partir de la matriz de estados, dependiendo del modo ingresado, si es modo de cifrado
	 * le agraga todos los caracteres, sino le agrega los caracteres validos.
	 * */
	private String getText(char states[][][], int mode) {
		String text = "";

		for(char state[][] : states)
		{
			for(char column[] : state)
			{
				for(char character : column)
				{
					if(DECIFRATE_MODE == mode) {
						if(character > 16)
							text += character;
					}
					else {
						text += character;
					}
				}
			}
		}

		return text;
	}

	/**
	 * Metodo que realiza la funcion rotword, osea que rota una columna de la clave.
	 * @param columnOrigin columna de donde se sacaran los bytes a rotar.
	 * @param columnDestiny columna en donde se guardaran los bytes resultantes de la rotacion.
	 * */
	private void rotWord(char columnOrigin[], char columnDestiny[]) {
		columnDestiny[MATRIX_ORDER - 1] = columnOrigin[0];

		for(int i = 1; i < MATRIX_ORDER; ++i) {
			columnDestiny[i - 1] = columnOrigin[i];
		}
	}

	/**
	 * Metodo que rota la columna "column" de la matriz "matrix" una posicion hacia la izquierda.
	 * @param matrix matriz que en donde se ubica la columna a rotar.
	 * @param column numero de la columna a rotar.
	 * */
	private void rotColumnLeft(char matrix[][], int column)
	{
		char aux = matrix[0][column];

		for(int i = 1; i < MATRIX_ORDER; ++i) {
			matrix[i - 1][column] = matrix[i][column];
		}

		matrix[MATRIX_ORDER - 1][column] = aux;
	}

	/**
	 * Metodo que rota la columna "column" de la matriz "matrix" una posicion hacia la derecha.
	 * @param matrix matriz que en donde se ubica la columna a rotar.
	 * @param column numero de la columna a rotar.
	 * */
	private void rotColumnRight(char matrix[][], int column)
	{
		char aux = matrix[MATRIX_ORDER - 1][column];

		for(int i = MATRIX_ORDER - 1; i > 0; --i) {
			matrix[i][column] = matrix[i - 1][column];
		}

		matrix[0][column] = aux;
	}

	/**
	 * Metodo que realiza un XOR entre cada byte de los vectores "vector1" y "vector2" y el resultado
	 * lo guarda en "vectorResult".
	 * @param vector1 primer vector operando de la operacion XOR.
	 * @param vector2 segundo vector operando de la operacion XOR.
	 * @param vectorResult vector resultante de la operacion XOR entre "vector1" y "vector2".
	 * */
	private void xorBtweenVector(char vector1[], char vector2[], char vectorRestult[]) {
		for(int i = 0; i < MATRIX_ORDER; ++i) {
			vectorRestult[i] = (char) (vector1[i] ^ vector2[i]);
		}
	}

	/* --------------------------------- Funciones del algoritmo AES ---------------------------------- */
	private void addRoundKey(char state[][], int currentRound) {
		for(int i = 0; i < MATRIX_ORDER; ++i) {
			xorBtweenVector(state[i], this.key[(currentRound * MATRIX_ORDER) + i], state[i]);
		}
	}

	private void shiftRows(char state[][])
	{
		for(int i = 1; i < MATRIX_ORDER; ++i) {
			for(int j = 0; j < i; ++j) {
				rotColumnLeft(state, i);
			}
		}
	}

	private void subBytes(char state[][])
	{
		for(char column[] : state) {
			for(char character : column) {
				character = Tables.sbox[character];
			}
		}
	}

	private void mixColumns(char state[][])
	{
		char aux[] = new char[MATRIX_ORDER];
		char temp[] = new char[MATRIX_ORDER];

		for (int j = 0; j < MATRIX_ORDER; j++)
		{
			for(int i = 0; i < MATRIX_ORDER; ++i) {
				aux[i] = state[j][i];
			}

			temp[0] = (char) (Tables.mul2[aux[0]] ^ Tables.mul3[aux[1]] ^ aux[2] ^ aux[3]);
			temp[1] = (char) (aux[0] ^ Tables.mul2[aux[1]] ^ Tables.mul3[aux[2]] ^ aux[3]);
			temp[2] = (char) (aux[0] ^ aux[1] ^ Tables.mul2[aux[2]] ^ Tables.mul3[aux[3]]);
			temp[3] = (char) (Tables.mul3[aux[0]] ^ aux[1] ^ aux[2] ^ Tables.mul2[aux[3]]);

			for(int i = 0; i < MATRIX_ORDER; ++i) {
				state[j][i] = temp[i];
			}
		}
	}

	private void invShiftRows(char state[][])
	{
		for(int i = 1; i < MATRIX_ORDER; ++i) {
			for(int j = 0; j < i; ++j) {
				rotColumnRight(state, i);
			}
		}
	}

	private void invSubBytes(char state[][])
	{
		for(char column[] : state) {
			for(char character : column) {
				character = Tables.inv_sbox[character];
			}
		}
	}

	private void invMixColumns(char state[][])
	{
		char aux[] = new char[MATRIX_ORDER];
		char temp[] = new char[MATRIX_ORDER];

		for (int j = 0; j < MATRIX_ORDER; j++)
		{
			for(int i = 0; i < MATRIX_ORDER; ++i) {
				aux[i] = state[j][i];
			}

			temp[0] = (char) (Tables.mul14[aux[0]] ^ Tables.mul11[aux[1]] ^ Tables.mul13[aux[2]] ^ Tables.mul9[aux[3]]);
			temp[1] = (char) (Tables.mul9[aux[0]] ^ Tables.mul14[aux[1]] ^ Tables.mul11[aux[2]] ^Tables.mul13[aux[3]]);
			temp[2] = (char) (Tables.mul13[aux[0]] ^ Tables.mul9[aux[1]] ^ Tables.mul14[aux[2]] ^ Tables.mul11[aux[3]]);
			temp[3] = (char) (Tables.mul11[aux[0]] ^ Tables.mul13[aux[1]] ^ Tables.mul9[aux[2]] ^ Tables.mul14[aux[3]]);

			for(int i = 0; i < MATRIX_ORDER; ++i) {
				state[j][i] = temp[i];
			}
		}
	}


	private static final byte MATRIX_ORDER = 4;			//Guarda el orden de las matrices estandar de la clase.
	private static final byte CANTS_ROUNDS = 10;		//Guarda la cantidad de rounds que realizara el algoritmo.
	private static final byte RADIX = 16;				//Guarda la base de la representacion de los numeros.
	public static final byte CIFRATE_MODE = 1;			//Indica que el modo es de cifrado.
	public static final byte DECIFRATE_MODE = 2;		//Indica que el modo es de decifrado.
}
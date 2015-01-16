
package lexer;


/**
 * This class represents the rows found within the table of {@link State} Enum. It contains the possible values for the
 * rows as well as the state transition table. All States have an index corresponding to their position in the table and
 * a String description corresponding to the state.
 * 
 * @author Jan Christian
 * @see ~/CSE340_Lexer/Resources/CSE340_A1_StateTables.xlsx
 */
public enum State
{
	/*
	 * Equal index parameter's implies that their state transitions are equal. E.g. $0b & $binary, $0x & $hexaDecimal,
	 * $stringQuote && $string The String parameter should be used to name valid states. Specifically, states that imply
	 * valid syntax.
	 */
	$stop(-2, "STOP"),
	$error(0, "ERROR"),
	$initial(1),
	$0(2, "INTEGER"),
	$0b(3),
	$binary(3, "BINARY"),
	$integer(4, "INTEGER"),
	$octal(5, "OCTAL"),
	$0x(6),
	$hexaDecimal(6, "HEXADECIMAL"),
	$identifier(7, "IDENTIFIER"),
	$dot(8, "FLOAT"),
	$dotFloat(9, "FLOAT"),
	$exponent(10),
	$signedExponent(11),
	$exponentFloat(12, "FLOAT"),
	$openQuote(13),
	$character(13),
	$charEscape(14),
	$closingQuote(15),
	$openQuotes(16),
	$string(16),
	$stringEscape(17),
	$closingQuotes(15);

	public int getIndex()
	{
		return this.index;
	}


	public String getDescription()
	{
		return this.description;
	}


	/**
	 * Returns the {@link State} found within the indexes of currentState and the {@link Input} column determined from
	 * currentCharacter.
	 * 
	 * @param currentState
	 * @param currentCharacter
	 * @return {@link State}
	 */
	public static State getNextState(State currentState, char currentCharacter, boolean isBetweenQuotes)
	{
		Input input = Input.getInputFrom(currentState, currentCharacter, isBetweenQuotes);
		return table[currentState.getIndex()][input.getIndex()];
	}
	
	public static boolean inPreValidString(State currentState)
	{
		return currentState == State.$openQuotes || currentState == State.$stringEscape || currentState == State.$string; 
	}
	
	public static boolean inPreValidChar(State currentState)
	{
		return currentState == State.$openQuote || currentState == State.$charEscape || currentState == State.$character;  
	}

	/**
	 * This variable contains the state table which is used by getNextState() to determine the next state for the Lexer.
	 * Its columns correspond to the Input Enumerator and its rows correspond to the State Enumerator. Every array
	 * represents a row.
	 */
	public static final State[][] table = {
		
			/**
			 * @formatter:off
			 */
		
			// 0 - $error
			{ $error, $error, $error, $error, $error, $error, $error, $error, $error, $error, $error, $error, $stop, $stop, $stop, $error, $stop },
			// 1 - $initial
			{ $0, $integer, $integer, $integer, $identifier, $identifier, $identifier, $identifier, $identifier, $dot, $identifier, $error, $openQuote, $openQuotes, $stop, $error, $stop },
			// 2 - $0
			{ $octal, $octal, $octal, $error, $0b, $exponent, $0x, $error, $error, $dot, $error, $error, $stop, $stop, $stop, $error, $stop },
			// 3 - $0b & binary
			{ $binary, $binary, $error, $error, $error, $error, $error, $error, $error, $error, $error, $error, $stop, $stop, $stop, $error, $stop },
			// 4 - $integer
			{ $integer, $integer, $integer, $integer, $error, $exponent, $error, $error, $error, $dotFloat, $error, $error, $stop, $stop, $stop, $error, $stop },
			// 5 - $octal
			{ $octal, $octal, $octal, $error, $error, $error, $error, $error, $error, $error, $error, $error, $stop, $stop, $stop, $error, $stop },
			// 6 - $0x & $hexaDecimal
			{ $hexaDecimal, $hexaDecimal, $hexaDecimal, $hexaDecimal, $hexaDecimal, $hexaDecimal, $error, $hexaDecimal, $error, $error, $error, $error, $stop, $stop, $stop, $error, $stop },
			// 7 - $identifier
			{ $identifier, $identifier, $identifier, $identifier, $identifier, $identifier, $identifier, $identifier, $identifier, $error, $identifier, $error, $stop, $stop, $stop, $error, $stop },
			// 8 - $dot
			{ $dotFloat, $dotFloat, $dotFloat, $dotFloat, $error, $error, $error, $error, $error, $error, $error, $error, $stop, $stop, $stop, $error, $stop },
			// 9 - $dotFloat
			{ $dotFloat, $dotFloat, $dotFloat, $dotFloat, $error, $exponent, $error, $error, $error, $error, $error, $error, $stop, $stop, $stop, $error, $stop },
			// 10 - $exponent
			{ $exponentFloat, $exponentFloat, $exponentFloat, $exponentFloat, $error, $error, $error, $error, $error, $error, $error, $stop, $stop, $stop, $signedExponent, $error, $stop },
			// 11 - $signedExponent
			{ $exponentFloat, $exponentFloat, $exponentFloat, $exponentFloat, $error, $error, $error, $error, $error, $error, $error, $error, $stop, $stop, $stop, $error, $stop },
			// 12 - $exponentFloat
			{ $exponentFloat, $exponentFloat, $exponentFloat, $exponentFloat, $error, $error, $error, $error, $error, $error, $error, $error, $stop, $stop, $stop, $error, $stop },
			// 13 - $openQuote & $character
			{ $character, $character, $character, $character, $character, $character, $character, $character, $character, $character, $character, $charEscape, $closingQuote, $character, $character, $character, $character },
			// 14 - $charEscape
			{ $character, $character, $character, $character, $character, $character, $character, $character, $character, $character, $character, $character, $character, $character, $character, $character, $character },
			// 15 - $closingQuote && $closingQuotes
			{ $error, $error, $error, $error, $error, $error, $error, $error, $error, $error, $error, $error, $stop, $stop, $stop, $error, $stop },
			// 16 - $openQuotes & $string
			{ $string, $string, $string, $string, $string, $string, $string, $string, $string, $string, $string, $stringEscape, $string, $closingQuotes, $string, $string, $string },
			// 17 - $stringEscape
			{ $string, $string, $string, $string, $string, $string, $string, $string, $string, $string, $string, $string, $string, $string, $string, $string, $string }
			
			/**
			 * @formatter:on
			 */
	};


	/**
	 * Constructor. {@code description} will default to "ERROR".
	 * 
	 * @param index
	 */
	private State(int index)
	{
		this.index = index;
		this.description = "ERROR";
	}


	/**
	 * Constructor. {@code index} refers to the position in an array and {@code description} describes the state.
	 * 
	 * @param index
	 * @param description
	 */
	private State(int index, String description)
	{
		this.index = index;
		this.description = description;
	}

	private int index;
	private String description;
}

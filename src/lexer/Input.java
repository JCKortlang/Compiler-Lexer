
package lexer;


/**
 * This class represents the columns found within the table of {@link State} Enum. It contains the possible values for
 * the columns as well as the functions used to map user input to the appropriate column. All Inputs are assigned an
 * index that corresponds to their position in the table, as well a character or range of characters that it represents.
 * 
 * @see ~/CSE340_Lexer/Resources/CSE340_A1_StateTables.xlsx
 * @author Jan Christian Chavez-Kortlang
 */
public enum Input
{
	/*
	 * Enums with the same index parameter implies that their columns are identical. Note that while the input ranges do
	 * not appear deterministic, the conditional clauses will make them so. For example, we will check $0 and $1 before
	 * $octalDigits. Therefore if the program checks $octalDigits then it implies that $octalDigits really only
	 * encompasses [2-7].
	 */
	$0(0, '0'),
	$1(1, '1'),
	$octalDigits(2, '0', '7'),
	$digits(3, '0', '9'),
	$B(4, 'b'),
	$E(5, 'e'),
	$X(6, 'x'),
	$hexLetters(7, 'a', 'f'),
	$alphabet(8, 'a', 'z'),
	$period(9, '.'),
	$dollarSign(10, '$'),
	$underScore(10, '_'),
	$backslash(11, '\\'),
	$charQuote(12, '\''),
	$stringQuote(13, '"'),
	$plus(14, '+'),
	$minus(14, '-'),
	$delimeter(16),
	$other(15);

	/**
	 * Iterates through all of this Enumerator's values and checks the parameter against the char values passed in
	 * through its corresponding parameter.
	 * 
	 * @param currentState
	 * @param currentCharacter
	 * @return State | null
	 */
	public static Input getInputFrom(State currentState, char currentCharacter, boolean isBetweenQuotes)
	{
		// Iterates through all the values checks to see if the input matches
		// it.
		for (Input input : Input.values())
		{
			if (input.isInCharRange(currentState, currentCharacter, isBetweenQuotes))
			{
				return input;
			}
		}
		return null;
	}


	/**
	 * Compares the input to an array of predetermined delimiters.
	 * 
	 * @param input
	 * @return boolean
	 */
	public static boolean isDelimiter(char input)
	{
		char[] delimiters = { ':', ';', '}', '{', '[', ']', '(', ')', ',' };
		for (char delimiter : delimiters)
		{
			if (input == delimiter)
			{
				return true;
			}
		}
		return false;
	}


	/**
	 * Compares the input to an array of predetermined identifiers.
	 * 
	 * @param input
	 * @return boolean
	 */
	public static boolean isOperator(char input)
	{
		// == and != should be handled in splitLine
		char[] operators = { '+', '-', '*', '/', '<', '>', '=', '!', '&', '|' };
		for (char operator : operators)
		{
			if (input == operator)
			{
				return true;
			}
		}
		return false;
	}


	/**
	 * This is called whenever Lever.java ends in the identifier state because all keywords are also identifiers.
	 * 
	 * @param input
	 * @return boolean
	 */
	public static boolean isKeyword(String input)
	{
		String[] keywords = { "if", "else", "while", "switch", "case", "return", "int", "float", "void", "char", "string", "boolean", "true", "false", "print" };

		for (String keyword : keywords)
		{
			if (input.equals(keyword))
			{
				return true;
			}
		}
		return false;
	}


	/**
	 * Checks if the parameter corresponds to the blank space character.
	 * 
	 * @param input
	 * @return boolean
	 */
	public static boolean isSpace(char input)
	{
		return input == ' ';
	}


	/**
	 * Returns true if {@code input} is equal to {@code '\'' || '"'}
	 * 
	 * @param input
	 * @return boolean
	 */
	public static boolean isQuotationMark(char input)
	{
		return input == '\'' || input == '"';
	}


	/**
	 * Returns true if the {@code input} is equal to: {@code ">" || "<" || "!" || "="}.
	 * 
	 * @param input
	 * @return boolean
	 */
	public static boolean hasComplexEquality(String input)
	{
		return input.equalsIgnoreCase(">") || input.equalsIgnoreCase("<") || input.equalsIgnoreCase("!") || input.equalsIgnoreCase("=");
	}


	/**
	 * Simple getter.
	 * 
	 * @return {@code index}
	 */
	public int getIndex()
	{
		return this.index;
	}


	/**
	 * Constructor. {@code index} refers to the enumerator's column position in {@code State}'s table.
	 * 
	 * @param index
	 */
	private Input(int index)
	{
		this.index = index;
	}


	/**
	 * Constructor. {@code index} refers to the enumerator's column position in {@code State}'s table and
	 * {@code startingCharacter} refers to the character represented by the {@code Input}.
	 * 
	 * @param index
	 * @param startingCharacter
	 */
	private Input(int index, char startingCharacter)
	{
		this.index = index;
		this.startingCharacter = startingCharacter;
		this.endingCharacter = startingCharacter;
	}


	/**
	 * Constructor. {@code index} refers to the enumerator's column position in {@code State}'s table.
	 * {@code startingCharacter} refers to the first character of the range represented by {@code Input} and
	 * {@code endingCharacter} represents the last character in the range.
	 * 
	 * @param index
	 * @param startingCharacter
	 * @param endingCharacter
	 */
	private Input(int index, char startingCharacter, char endingCharacter)
	{
		this.index = index;
		this.startingCharacter = startingCharacter;
		this.endingCharacter = endingCharacter;
	}


	/**
	 * Uses the current state to and input to determine which Input Enumerator corresponds to input.
	 * 
	 * @param currentState
	 * @param input
	 * @return boolean
	 */
	private boolean isInCharRange(State currentState, char input, boolean isBetweenQuotes)
	{
		//If the 
		if (this == Input.$delimeter && currentState == State.$error && isBetweenQuotes)
		{
			return Input.isDelimiter(input) || Input.isOperator(input);
		}
		// We avoid escaping during strings and characters because they can contain delimiters, operators, and
		// whitespace.
		else if (this == Input.$delimeter && currentState != State.$openQuote && currentState != State.$openQuotes && currentState != State.$stringEscape
				&& currentState != State.$string)
		{
			return Input.isDelimiter(input) || Input.isSpace(input) || Input.isOperator(input);
		}
		// Letters must be checked for both upper and lower case values.
		else if (Character.isLetter(input))
		{
			return (input >= this.startingCharacter && input <= this.endingCharacter)
					|| (input >= Character.toUpperCase(this.startingCharacter) && input <= Character.toUpperCase(this.endingCharacter));
		}
		// We exclude $other it implies values that are not specified.
		else if (this != $other)
		{
			// If it is not a letter then there are not multiple versions of the
			// same char.
			return (input >= this.startingCharacter && input <= this.endingCharacter);
		}
		// $other is true for all values.
		else if (this == $other)
		{
			return true;
		}
		// All other states should return false for 'other' values.
		else
		{
			return false;
		}
	}

	private int index;
	private char startingCharacter;
	private char endingCharacter;
}

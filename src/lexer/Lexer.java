
package lexer;


import java.util.Vector;


public class Lexer
{
	private String text;
	private Vector<Token> tokens;

	private Token previousToken;


	public Lexer(String text)
	{
		this.text = text;
	}


	public void run()
	{
		tokens = new Vector<Token>();
		String line;
		int lineNumber = 1;
		// split lines
		do
		{
			int eolAt = text.indexOf('\n');
			if (eolAt >= 0)
			{
				line = text.substring(0, eolAt);
				if (text.length() > 0)
				{
					text = text.substring(eolAt + 1);
				}
			}
			else
			{
				line = text;
				text = "";
			}
			parseLine(lineNumber, line);
			lineNumber++;
		} while (!text.equals(""));
	}


	// slit line
	private void parseLine(int lineNumber, String line)
	{
		if (line.equals(""))
		{
			return;
		}

		int index = 0;
		State nextState;
		State currentState = State.$initial;
		Token currentToken = null;

		char currentCharacter;
		String processedString = "";
		boolean isBetweenQuotes = false;

		System.out.println("\n" + line + "\n");
		do
		{
			// Store the character that determines the current state.
			currentCharacter = line.charAt(index);

			// Holds the state as determined by the input.
			nextState = State.getNextState(currentState, currentCharacter, isBetweenQuotes);
			
			if (nextState == State.$openQuote || nextState == State.$openQuotes)
			{
				isBetweenQuotes = true;
			}
			else if (nextState == State.$closingQuote || nextState == State.$closingQuotes)
			{
				isBetweenQuotes = false;
			}

			// FOR: Debugging
			//System.out.println(index + "\t" + currentCharacter + "\t" + nextState + "\t" + currentState);

			// If the currentState is STOP then the character that stopped it is not part of the processesedToken.
			if (nextState != State.$stop)
			{
				processedString += currentCharacter;
				currentState = nextState;
			}
			index++;

			// We iterate over the line until we reach a character that returns STOP state or completes a string / char.
		} while (index < line.length() && nextState != State.$stop && currentState != State.$closingQuote && currentState != State.$closingQuotes);

		// System.out.println("Length : " + processedString.length());
		// if(processedString.length() >= 2)
		// {
		// System.out.println("Char : " + processedString.charAt(1));
		// System.out.println("Equality : " + (processedString.charAt(1) == '\\'));
		// }

		// Initial state does not have a valid token.
		if (currentState != State.$initial)
		{
			// If the processed string is an identifier then it might be a keyword.
			if (currentState == State.$identifier && Input.isKeyword(processedString))
			{
				this.tokens.add(new Token(processedString + "", "KEYWORD", lineNumber));
			}
			// Implies we have a complete but unknown char sequence.
			else if (nextState == State.$closingQuote)
			{
				// Implies '.' || '\.'
				if (processedString.length() == 4 && processedString.charAt(1) == '\\')
				{
					this.tokens.add(new Token(processedString + "", "CHARACTER", lineNumber));
				}
				else if (processedString.length() == 3)
				{
					this.tokens.add(new Token(processedString + "", "CHARACTER", lineNumber));
				}
				else
				{
					this.tokens.add(new Token(processedString + "", "ERROR", lineNumber));
				}
			}
			// Implies we have a complete but unknown string sequence.
			else if (nextState == State.$closingQuotes)
			{
				this.tokens.add(new Token(processedString + "", "STRING", lineNumber));
			}
			else
			{
				this.tokens.add(new Token(processedString + "", currentState.getDescription(), lineNumber));
			}
		}

		if (currentCharacter != ' ')
		{
			// This conditional statement edits the previous token if the current character would append to create != or
			// ==. This is a bit hackish but it works. Ideally this should be added to the state table.
			if (previousToken != null && (Input.hasComplexEquality(previousToken.getWord())) && currentCharacter == '=')
			{
				previousToken.setWord(previousToken.getWord() + currentCharacter);
			}
			else if (Input.isDelimiter(currentCharacter) && nextState == State.$stop)
			{
				currentToken = new Token(currentCharacter + "", "DELIMITER", lineNumber);
				this.tokens.add(currentToken);
			}
			else if (Input.isOperator(currentCharacter) && nextState == State.$stop)
			{
				currentToken = new Token(currentCharacter + "", "OPERATOR", lineNumber);
				this.tokens.add(currentToken);
			}
			else if (Input.isQuotationMark(currentCharacter))
			{
				// Implies that we have reaches a quotation outside string. E.g. foo"string"
				if (nextState == State.$stop)
				{
					//In this case we want to parse the string including the quotation delimiter. Thus we decrement the counter.
					index--;
				}
			}
		}

		// Recursion will end once we have processed the entire line.
		if (index < line.length())
		{
			// Before we make a recursive call, we want to save the currentToken. This is used to detect != and ==
			this.previousToken = currentToken;
			parseLine(lineNumber, line.substring(index));
		}
	}


	// getTokens
	public Vector<Token> getTokens()
	{
		return tokens;
	}

}

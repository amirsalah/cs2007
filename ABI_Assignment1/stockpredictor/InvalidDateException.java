package stockpredictor;


public class InvalidDateException extends Exception{

	public InvalidDateException()
	{
		super();
	}

	/**
	 * the preferred Constructs with the given reason.
	 * @param reason the reason
	 */
	public InvalidDateException(String reason){
		super(reason);
	}
}
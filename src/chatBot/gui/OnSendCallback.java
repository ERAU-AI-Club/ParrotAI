package chatBot.gui;

/**
 * The <code>OnSendCallback</code> interface is a functional interface used by {@link ChatBox} to handle what
 * happens when the user presses the Send button.
 * 
 * @see {@link ChatBox}
 * @author Mohammad Alali (Sanavesa)
 */
public interface OnSendCallback
{
	/**
	 * Fired when the Send button has been clicked.
	 * @param text the text in the input field
	 */
	void onSend(String text);
}
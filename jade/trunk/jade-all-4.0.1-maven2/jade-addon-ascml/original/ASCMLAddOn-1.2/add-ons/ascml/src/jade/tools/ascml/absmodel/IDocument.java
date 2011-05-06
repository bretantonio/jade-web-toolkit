package jade.tools.ascml.absmodel;

/**
 * 
 */
public interface IDocument
{
	String SOURCE_UNKNOWN = "Source Unknown";

	/**
	 *  Set the source (path + file-name).
	 *  @param source  The source-object.
	 */
	void setSource(String source);

	/**
	 *  Get the source (path + file-name).
	 *  @return  The source-object.
	 */
	String getSource();

	/**
	 * Get the source-path.
	 * If the source is a file, than the path within the file-system is returned.
	 * @return  The source-path.
	 */
	String getSourcePath();

	/**
	 * Get the source-name.
	 * If the source is a file, than the file-name (without it's path) is returned.
	 * @return  The source-path.
	 */
	String getSourceName();

	/**
	 * Set the source-path.
	 * @param path  The new source-path.
	 */
	void setSourcePath(String path);

	/**
	 * Set the source-name (i.e. file-name - must end with '.agent.xml' or '.society.xml').
	 * @param name  The new source-name (must end with '.agent.xml' or '.society.xml')
	 */
	void setSourceName(String name);

	/**
	 * Return whether the document is saved or not.
	 * @return true, if this document is saved as it is (without any pending changes), false otherwise.
	 */
	public boolean isSaved();

	/**
	 * Set whether the document is saved or not.
	 * @param isSaved  true, if this document is saved as it is (without any pending changes), false otherwise.
	 */
	public void setSaved(boolean isSaved);
}

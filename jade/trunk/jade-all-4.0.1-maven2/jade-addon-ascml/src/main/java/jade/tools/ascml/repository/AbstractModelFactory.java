package jade.tools.ascml.repository;

import jade.tools.ascml.exceptions.ResourceNotFoundException;
import jade.tools.ascml.exceptions.ModelException;
import jade.tools.ascml.model.jibx.SocietyType;
import jade.tools.ascml.model.jibx.AgentType;

import java.io.*;
import java.util.jar.JarFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipEntry;
import java.net.URL;

/**
 * 
 */
public abstract class AbstractModelFactory
{
	protected String sourceName;

    /**
	 *  Load an AgentType-model.
	 *  @param xmlFile  The agent-description file.
	 *  @param repository  The Repository-object
	 *  @return The AgentType-model.
	 */
	public abstract AgentType createAgentTypeModel(String xmlFile, Repository repository) throws ModelException, ResourceNotFoundException;

	/**
	 *  Load a SocietyType-model.
	 *  @param xmlFile  The society-description file.
	 *  @param repository  The Repository-object
	 *  @return The SocietyType-model.
	 */
	public abstract SocietyType createSocietyTypeModel(String xmlFile, Repository repository) throws ModelException, ResourceNotFoundException;

	/**
	 *  Save a SocietyType to the file-system.
	 *  @param model  The SocietyType to save.
	 */
	public abstract void saveSocietyTypeModel(SocietyType model) throws ModelException;

	/**
	 *  Save an AgentType to the file-system.
	 *  @param model  The AgentType to save.
	 */
	public abstract void saveAgentTypeModel(AgentType model) throws ModelException;

	/**
	 *  Get an input stream for whatever provided.
	 *  1. It is tried to load the resource as file.
	 *  2. It is tried to load the resource via the ClassLoader.
	 *  3. It is tried to load the resource as URL.
	 *  @param name The resource description.
	 *  @return The input stream for the resource.
	 *  @throws ResourceNotFoundException  when the resource was not found.
	 */
	protected InputStream getModelInputStream(String name) throws ResourceNotFoundException
	{
		InputStream is = getResource0(name);
		if(is==null)
			is = getResource0(name.replace(".", "/"));
		if(is==null)
			is = getResource0(name + ".agent.xml");
		if(is==null)
			is = getResource0(name.replace(".", "/") + ".agent.xml");
		if(is==null)
			is = getResource0(name + ".society.xml");
		if(is==null)
			is = getResource0(name.replace(".", "/") + ".society.xml");
		if(is==null)
			throw new ResourceNotFoundException("Could not load model: " + name);

		return is;
	}

	/**
	 * This method returns an inputStream for the xml-File, which is
	 * contained within a jar-archiv.
	 *
	 * @param modelName   fileName of the model-description-file.
	 * @param jarFileName fileName of the jar-file, that contains the xml-file.
	 * @return ByteArrayInputStream containing the xml-Files content.
	 */
	protected InputStream getModelInputStreamFromJar(String modelName, String jarFileName)
	{
		InputStream returnStream = null;
		try
		{
			JarFile jarFile = new JarFile(jarFileName);

			FileInputStream fis = new FileInputStream(jarFileName);
			ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
			ZipEntry entry = null;

			// iterate through all entries of the jar-File.
			while((entry = zis.getNextEntry())!=null)
			{
				// if the matching entry is found, start loading the data
				// and create an ByteArrayInputStream with the loaded data.
				if(entry.getName().equals(modelName))
				{
					int count = 0;
					byte data[] = new byte[512];

					ByteArrayOutputStream bos = new ByteArrayOutputStream();

					while((count = zis.read(data, 0, 512))!=-1)
					{
						bos.write(data, 0, count);
					}

					bos.flush();
					bos.close();

					// the inputStream is used by the ModelRepository to read and parse the
					// xml-file.
					returnStream = new ByteArrayInputStream(bos.toByteArray());
				}
			}
			zis.close();
			jarFile.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return returnStream;
	}

	/**
	 *  Get an input stream for whatever provided.
	 *  1. It is tried to load the resource as file.
	 *  2. It is tried to load the resource via the ClassLoader.
	 *  3. It is tried to load the resource as URL.
	 *  @param name The resource description.
	 *  @return The input stream for the resource or null when the resource was not found.
	 */
	private InputStream getResource0(String name)
	{
		InputStream is = null;
		File file;

		// File...
		// Hack!!! Might throw exception in applet / webstart.
		try
		{
			file	= new File(name);
			if(file.exists())
			{
				try
				{
					is	= new FileInputStream(file);
					sourceName = file.getCanonicalPath();
				}
				catch(FileNotFoundException e)
				{
					// File is directory, or maybe locked...
				}
				catch(IOException e)
				{
					// File is directory, or maybe locked...
				}
			}
		}
		catch(SecurityException e){}

		// Classpath...
		if(is==null)
		{
			is = ClassLoader.getSystemClassLoader().getResourceAsStream(name.startsWith("/") ? name.substring(1) : name);
			if (is != null)
				sourceName = ClassLoader.getSystemClassLoader().getResource(name.startsWith("/") ? name.substring(1) : name).getPath();
		}

		// URL...
		if(is==null)
		{
			try
			{
				is = new URL(name).openStream();
				if (is != null)
					sourceName = name;
			}
			catch(IOException le)
			{
			}
		}

		return is;
	}

}

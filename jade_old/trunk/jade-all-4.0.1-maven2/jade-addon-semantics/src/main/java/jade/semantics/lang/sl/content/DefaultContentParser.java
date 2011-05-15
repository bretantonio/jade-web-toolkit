/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop 
multi-agent systems in compliance with the FIPA specifications.
JSA - JADE Semantics Add-on is a framework to develop cognitive
agents in compliance with the FIPA-ACL formal specifications.

Copyright (C) 2007 France Telecom

GNU Lesser General Public License

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation, 
version 2.1 of the License. 

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the
Free Software Foundation, Inc., 59 Temple Place - Suite 330,
Boston, MA  02111-1307, USA.
*****************************************************************/

package jade.semantics.lang.sl.content;

import jade.semantics.lang.sl.grammar.Content;
import jade.semantics.lang.sl.parser.ParseException;
import jade.semantics.lang.sl.parser.SLParser;
import jade.semantics.lang.sl.parser.SLUnparser;

import java.io.StringWriter;

public class DefaultContentParser implements ContentParser {

	/**
	 * @return the language handled by this content manager
	 */
	public String getLanguage() {return "fipa-sl";}

	/**
	 * @param message the message the content of which to extract
	 * @return the SL content extracted from the message.
	 * @throws ExctractContentException
	 */
	public Content parseContent(String foreignContent) 
	  	throws ParseContentException {
		try {
			return SLParser.getParser().parseContent(foreignContent);
		} catch (ParseException e) {
			throw new ParseContentException();
		}
	}

	/**
	 * @param message the message the content of which is filled
	 * @param c the content to fill the message
	 * @throws UnparseContentException
	 */
	public String unparseContent(Content content)
		throws UnparseContentException {
		try {
			StringWriter writer = new StringWriter();
			new SLUnparser(writer).unparseTrueSL(content);
			return writer.toString();
		}
		catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}

/*****************************************************************
"DistilledStateChartBehaviour" is a work based on the library "HSMBehaviour"
(authors: G. Caire, R. Delucchi, M. Griss, R. Kessler, B. Remick).
Changed files: "HSMBehaviour.java", "HSMEvent.java", "HSMPerformativeTransition.java",
"HSMTemplateTransition.java", "HSMTransition.java".
Last change date: 18/06/2010
Copyright (C) 2010 G. Fortino, F. Rango

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation;
version 2.1 of the License.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
*****************************************************************/

package examples.meeting_dsc_example;

import jade.core.*;

/**
 * @author G. Fortino, F. Rango
 */
public class ELDAEvent implements java.io.Serializable {

	private AID source;
	private java.util.List<AID> target;
	private String language;
	public static final String IN = "ELDA_IN";
	public static final String OUT = "ELDA_OUT";

	public ELDAEvent(AID source, java.util.List<AID> target){
		this.source = source;
		this.target = target;
	}

	public AID getSource() {
		return source;
	}

	public void setSource(AID source) {
		this.source = source;
	}

	public java.util.List<AID> getTarget() {
		return target;
	}

	public void setTarget(java.util.List<AID> target) {
		this.target = target;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

}
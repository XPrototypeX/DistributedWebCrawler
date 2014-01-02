package serializer;

import java.io.Serializable;

/**
 * Important note isn't an implementation of suns URL Class! 
 * Its more like a helper class.
 * 
 * TODO the serilizer from hazelcast doesnt work yet
 * FIX FIX FIX 
 * 
 * 
 * 
 * @author root
 *
 */

public final class Url implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2037283520465671483L;
	private final String link;
	
	public Url(String link) {
		this.link = link;
	}

	public String getUrl() {
		return link;
	}
}

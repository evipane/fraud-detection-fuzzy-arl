package org.processmining.framework.models.epcpack;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;

/**
 * <p>
 * Title:
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author not attributable
 * @version 1.0
 */
interface DOTCodeWriter {
	void writeDOTCode(Writer bw, HashMap nodeMapping) throws IOException;

	public int getId();
}

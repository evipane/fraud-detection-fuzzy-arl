/* Copyright (C) 2005, University of Massachusetts, Multi-Agent Systems Lab
 * See LICENSE for license information
 */

/************************************************************
 * SafeEnumeration.java
 ************************************************************/

package org.processmining.plugins.compliance.rules.select.constraints;

/* Global imports */
import java.util.Enumeration;
import java.util.Vector;


/**
 * Grabs all the elements in an enumeration and stores them
 * locally before enumerating over them.  This allows you to
 * safely modify the original structure you are enumerating
 * while you are enumerating.
 */
public class SafeEnumeration implements Enumeration {
  Enumeration enumeration;

  public SafeEnumeration(Enumeration e) {
    enumeration = prime(e);
  }

  private Enumeration prime(Enumeration e) {
    Vector elements = new Vector();
    while(e.hasMoreElements()) {
      elements.addElement(e.nextElement());
    }
    return elements.elements();
  }
  
  public boolean hasMoreElements() {
    return (enumeration.hasMoreElements());
  }
  
  public Object nextElement() {
    return (enumeration.nextElement());
  }
}

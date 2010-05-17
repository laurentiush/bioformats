//
// ImportStep.java
//

/*
LOCI Plugins for ImageJ: a collection of ImageJ plugins including the
Bio-Formats Importer, Bio-Formats Exporter, Bio-Formats Macro Extensions,
Data Browser, Stack Colorizer and Stack Slicer. Copyright (C) 2005-@year@
Melissa Linkert, Curtis Rueden and Christopher Peterson.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package loci.plugins.in;

import java.util.HashMap;

/**
 * An enumeration of the steps in the import preparation process.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/loci-plugins/src/loci/plugins/in/ImportStep.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/loci-plugins/src/loci/plugins/in/ImportStep.java">SVN</a></dd></dl>
 */
public enum ImportStep {

  // -- Enumeration --

  READER    (0, "Initializing base reader"),
  FILE      (1, "Reading file header"),
  STACK     (2, "Building reader stack"),
  SERIES    (3, "Choosing series"),
  DIM_ORDER (4, "Confirming dimension order"),
  RANGE     (5, "Confirming planar ranges"),
  CROP      (6, "Confirming crop region"),
  COLORS    (7, "Confirming colorization"),
  METADATA  (8, "Initializing metadata"),
  COMPLETE  (9, "Import preparations complete");

  // -- Static fields --

  private static final HashMap<Integer, ImportStep> STEP_TABLE;
  static {
    STEP_TABLE = new HashMap<Integer, ImportStep>();
    for (ImportStep step : values()) {
      STEP_TABLE.put(step.getStep(), step);
    }
  }
  public static ImportStep getStep(int step) {
    return STEP_TABLE.get(step);
  }

  // -- Fields --

  private int step;
  private String message;

  // -- Constructor --

  private ImportStep(int step, String message) {
    this.step = step;
    this.message = message;
  }

  // -- ImportStep methods --

  public int getStep() { return step; }
  public String getMessage() { return message; }

}

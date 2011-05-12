//
// OverlayArrow.java
//

/*
VisBio application for visualization of multidimensional biological
image data. Copyright (C) 2002-@year@ Curtis Rueden and Abraham Sorber.

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

package loci.visbio.overlays;

import java.awt.Color;
import java.rmi.RemoteException;
import java.util.Arrays;

import loci.visbio.util.MathUtil;
import visad.DataImpl;
import visad.FlatField;
import visad.FunctionType;
import visad.Gridded2DSet;
import visad.GriddedSet;
import visad.RealTupleType;
import visad.TupleType;
import visad.VisADException;

/**
 * OverlayArrow is an arrow wedge overlay.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/visbio/src/loci/visbio/overlays/OverlayArrow.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/visbio/src/loci/visbio/overlays/OverlayArrow.java">SVN</a></dd></dl>
 */
public class OverlayArrow extends OverlayObject {

  // -- Static Fields --

  /** The names of the statistics this object reports. */
  protected static final String TIP = "Tip Coordinates";
  protected static final String ANGLE = "Angle";
  protected static final String LENGTH = "Length";
  protected static final String[] STAT_TYPES = {TIP, ANGLE, LENGTH};

  // -- Constructors --

  /** Constructs an uninitialized arrow wedge overlay. */
  public OverlayArrow(OverlayTransform overlay) { super(overlay); }

  /** Constructs an arrow wedge overlay. */
  public OverlayArrow(OverlayTransform overlay,
    float x1, float y1, float x2, float y2)
  {
    super(overlay);
    this.x1 = x1;
    this.y1 = y1;
    this.x2 = x2;
    this.y2 = y2;
  }

  // -- Static methods --

  /** Returns the names of the statistics this object reports. */
  public static String[] getStatTypes() { return STAT_TYPES; }

  // -- OverlayObject API methods --

  /** Returns whether this object is drawable, i.e., is of nonzero
   *  size, area, length, etc.
   */
  public boolean hasData() { return (x1 != x2 || y1 != y2); }

  /** Gets VisAD data object representing this overlay. */
  public DataImpl getData() {
    if (!hasData()) return null; // dont render zero length arrows
    double xx = x2 - x1;
    double yy = y2 - y1;
    double dist = Math.sqrt(xx * xx + yy * yy);
    double mult = 0.1; // something like aspect ratio

    float qx = (float) (mult * xx);
    float qy = (float) (mult * yy);

    RealTupleType domain = overlay.getDomainType();
    TupleType range = overlay.getRangeType();

    float[][] setSamples = {
      {x1, x2 - qy, x2 + qy, x1}, {y1, y2 + qx, y2 - qx, y1}
    };

    GriddedSet fieldSet = null;
    try {
      if (filled) {
        fieldSet = new Gridded2DSet(domain,
          setSamples, 2, 2, null, null, null, false, false);
      }
      else {
        fieldSet = new Gridded2DSet(domain,
          setSamples, setSamples[0].length, null, null, null, false);
      }
    }
    catch (VisADException exc) { exc.printStackTrace(); }

    Color col = selected ? GLOW_COLOR : color;
    float r = col.getRed() / 255f;
    float g = col.getGreen() / 255f;
    float b = col.getBlue() / 255f;

    float[][] rangeSamples = new float[4][setSamples[0].length];
    Arrays.fill(rangeSamples[0], r);
    Arrays.fill(rangeSamples[1], g);
    Arrays.fill(rangeSamples[2], b);
    Arrays.fill(rangeSamples[3], 1.0f);

    FlatField field = null;
    try {
      FunctionType fieldType = new FunctionType(domain, range);
      field = new FlatField(fieldType, fieldSet);
      field.setSamples(rangeSamples);
    }
    catch (VisADException exc) { exc.printStackTrace(); }
    catch (RemoteException exc) { exc.printStackTrace(); }
    return field;
  }

  /** Computes the shortest distance from this object to the given point. */
  public double getDistance(double x, double y) {
    return MathUtil.getDistance(new double[] {x1, y1},
      new double[] {x2, y2}, new double[] {x, y}, true);
  }

  /** Returns a specific statistic of this object. */
  public String getStat(String name) {
    if (name.equals(TIP)) {
      return "(" + x1 + ", " + y1 + ")";
    }
    else if (name.equals(ANGLE)) {
      return "" + getAngle();
    }
    else if (name.equals(LENGTH)) {
      return "" + getLength();
    }
    else return "No such statistic for this overlay type";
  }

  /** Retrieves useful statistics about this overlay. */
  public String getStatistics() {
    return "Arrow " + TIP + " = (" + x1 + ", " + y1 + ")\n" +
      ANGLE + " = " + getAngle() + "; " + LENGTH + " = " + getLength();
  }

  /** True iff this overlay has an endpoint coordinate pair. */
  public boolean hasEndpoint() { return true; }

  /** True iff this overlay has a second endpoint coordinate pair. */
  public boolean hasEndpoint2() { return true; }

  /** True iff this overlay supports the filled parameter. */
  public boolean canBeFilled() { return true; }

  // -- Helper methods --

  /** Computes the angle of this arrow. */
  protected float getAngle() {
    float xx = x2 - x1;
    float yy = y2 - y1;
    float angle = (float) (180 * Math.atan(xx / yy) / Math.PI);
    if (yy < 0) angle += 180;
    if (angle < 0) angle += 360;
    return angle;
  }

  /** Computes the length of this arrow. */
  protected float getLength() {
    float xx = x2 - x1;
    float yy = y2 - y1;
    float length = (float) Math.sqrt(xx * xx + yy * yy);
    return length;
  }

  // -- Object API methods --

  /** Gets a short string representation of this overlay arrow. */
  public String toString() { return "Arrow"; }

}
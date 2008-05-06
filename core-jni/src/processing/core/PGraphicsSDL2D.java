/* -*- mode: java; c-basic-offset: 2; indent-tabs-mode: nil -*- */

package processing.core;

public class PGraphicsSDL2D extends PGraphics {

  /* put native functions here */
  static { System.loadLibrary("processing-core"); }
  native void native_init();
  native void native_unload();
  native void native_line(int x1, int y1, int x2, int y2);
  native void native_point(int x, int y);
  native void native_update_rect(int x1, int y1, int x2, int y2);
  /* end of native functions */

  protected boolean transformed;

  public PGraphicsSDL2D(int iwidth, int iheight, PApplet parent) {
    super(iwidth, iheight, parent);
    transformed = false;
    native_init();
  }

  protected void allocate() {
  }

  public void applyMatrix(float n00, float n01, float n02,
                          float n10, float n11, float n12) {
    super.applyMatrix(n00, n01, n02, n10, n11, n12);
    transformed = true;
    if (m00 == 1 && m01 == 0 && m02 == 0 && m10 == 0 && m11 == 1 && m12 == 0)
      transformed = false;
  }

  public void beginDraw() {
  }

  public void beginShape(int kind) {
    shape = kind;
    vertexCount = 0;
  }

  protected void clear() {
  }

  public void dispose() {
    native_unload();
  }

  public void endDraw() {
    updatePixels();
  }

  public void endShape(int mode) {
  }

  public void line(float x1, float y1, float x2, float y2) {
    native_line((int) x1, (int) y1, (int) x2, (int) y2);
  }

  public void point(float x, float y) {
    if (transformed != true) {
      native_point((int) x, (int) y);
      return;
    }
    native_point((int) screenX(x, y), (int) screenY(x, y));
  }

  public void resetMatrix() {
    transformed = false;
    super.resetMatrix();
  }

  public void resize(int iwidth, int iheight) {
    width = iwidth;
    height = iheight;
    width1 = width - 1;
    height1 = height - 1;
  }

  public void updatePixels() {
    updatePixels(0, 0, width, height);
  }

  public void updatePixels(int x1, int y1, int x2, int y2) {
    native_update_rect(x1, y1, x2, y2);
  }

  public void vertex(float x, float y) {
  }

  public void vertex(float x, float y, float z) {
  }

  public void vertex(float x, float y, float u, float v) {
  }

  public void vertex(float x, float y, float z, float u, float v) {
  }
}

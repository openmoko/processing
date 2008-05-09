/* -*- mode: java; c-basic-offset: 2; indent-tabs-mode: nil -*- */

/* TODO: add the color support */

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
    if (mode == CLOSE && shape == POLYGON) {
      /* connect the last point to the starting point */
      line(vertices[vertexCount - 1][MX], vertices[vertexCount - 1][MY],
           vertices[0][MX], vertices[0][MY]);
    }
  }

  public void line(float x1, float y1, float x2, float y2) {
    if (!transformed) {
      native_line((int) x1, (int) y1, (int) x2, (int) y2);
      return;
    }
    native_line((int) screenX(x1, y1), (int) screenY(x1, y1),
                (int) screenX(x2, y2), (int) screenY(x2, y2));
  }

  public void point(float x, float y) {
    if (!transformed) {
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

    vertices[vertexCount][MX] = x;
    vertices[vertexCount][MY] = y;
    vertexCount++;

    switch (shape) {
    case POINTS:
      point(x, y);
      break;
    case LINES:
      if (vertexCount % 2 == 0) {
        line(vertices[vertexCount - 2][MX], vertices[vertexCount - 2][MY], x, y);
      }
      break;
    case TRIANGLES:
      if (vertexCount % 3 == 0) {
        line(vertices[vertexCount - 3][MX], vertices[vertexCount - 3][MY],
             vertices[vertexCount - 2][MX], vertices[vertexCount - 2][MY]);
        line(vertices[vertexCount - 3][MX], vertices[vertexCount - 3][MY], x, y);
        line(vertices[vertexCount - 2][MX], vertices[vertexCount - 2][MY], x, y);
      }
      break;
    case TRIANGLE_STRIP:
      if (vertexCount >= 3) {
        line(vertices[vertexCount - 3][MX], vertices[vertexCount - 3][MY], x, y);
        line(vertices[vertexCount - 2][MX], vertices[vertexCount - 2][MY], x, y);
      }
      if (vertexCount == 3) {
        line(vertices[0][MX], vertices[0][MY],
             vertices[1][MX], vertices[1][MY]);
      }
      break;
    case TRIANGLE_FAN:
      if (vertexCount >= 3) {
        line(vertices[vertexCount - 2][MX], vertices[vertexCount - 2][MY], x, y);
        line(vertices[0][MX], vertices[0][MY], x, y);
      }
      if (vertexCount == 3) {
        line(vertices[0][MX], vertices[0][MY], vertices[1][MX], vertices[1][MY]);
      }
      break;
    case QUADS:
      if (vertexCount % 4 == 0) {
        line(vertices[vertexCount - 4][MX], vertices[vertexCount - 4][MY],
             vertices[vertexCount - 3][MX], vertices[vertexCount - 3][MY]);
        line(vertices[vertexCount - 3][MX], vertices[vertexCount - 3][MY],
             vertices[vertexCount - 2][MX], vertices[vertexCount - 2][MY]);
        line(vertices[vertexCount - 2][MX], vertices[vertexCount - 2][MY], x, y);
        line(vertices[vertexCount - 4][MX], vertices[vertexCount - 4][MY], x, y);
      }
      break;
    case QUAD_STRIP:
      if (vertexCount >= 4 && vertexCount % 2 == 0) {
        line(vertices[vertexCount - 4][MX], vertices[vertexCount - 4][MY],
             vertices[vertexCount - 2][MX], vertices[vertexCount - 2][MY]);
        line(vertices[vertexCount - 2][MX], vertices[vertexCount - 2][MY], x, y);
        line(vertices[vertexCount - 3][MX], vertices[vertexCount - 3][MY], x, y);
      }
      if (vertexCount == 4) {
        line(vertices[0][MX], vertices[0][MY], vertices[1][MX], vertices[1][MY]);
      }
      break;
    case POLYGON:
      if (vertexCount > 1) {
        line(vertices[vertexCount - 2][MX], vertices[vertexCount - 2][MY], x, y);
      }
      break;
    }
  }

  public void vertex(float x, float y, float z) {
    throw new UnsupportedOperationException();
  }

  public void vertex(float x, float y, float u, float v) {
    throw new UnsupportedOperationException();
  }

  public void vertex(float x, float y, float z, float u, float v) {
    throw new UnsupportedOperationException();
  }
}

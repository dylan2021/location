package com.sfmap.api.maps.model;

import android.text.TextUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

/**
 * 部分实现TileProvider类，只需要一个URL指向的图像。 注意：使用这个类，要求所有的图像都具有相同的尺寸。
 */
public abstract class UrlTileProvider
  implements TileProvider
{
  private final int width;
  private final int height;

    /**
     * 构造一个UrlTileProvider对象。
     * @param width -用于Tile的图片宽度。
     * @param height - 用于Tile的图片高度。
     */
  public UrlTileProvider(int width, int height)
  {
    this.width = width;
    this.height = height;
  }

    /**
     * 返回指定tile坐标对应图片的URL。
     * @param x - tile的横坐标，范围为[0, 2的zoom次方 - 1]。
     * @param y - tile的纵坐标，范围为[0, 2的zoom次方 - 1]。
     * @param zoom - tile的缩放级别，范围通过类MapController的getMinZoomLevel()和getMaxZoomLevel()获得。
     * @return 指定tile坐标对应图片的URL。
     */
  public abstract URL getTileUrl(int x, int y, int zoom);

    /**
     * 返回指定tile坐标的tile对象。
     * @param x - tile的横坐标，范围为[0, 2的zoom次方 - 1]。
     * @param y - tile的纵坐标，范围为[0, 2的zoom次方 - 1]。
     * @param zoom  - tile的缩放级别，范围通过类MapController的getMinZoomLevel()和getMaxZoomLevel()获得。
     * @return 指定tile坐标的tile对象。
     */
    public final Tile getTile(int x, int y, int zoom) {
      URL tileUrl = getTileUrl(x, y, zoom);
      if (tileUrl == null) {
        return NO_TILE;
      }
      Tile tile;
      try {
        Map<String, String> urlProperties = getUrlRequestProperties();
        InputStream tileUrlInputStream;
        if (urlProperties == null || urlProperties.isEmpty()) {
          tileUrlInputStream = tileUrl.openStream();
        } else {
          URLConnection urlConnection = tileUrl.openConnection();
          for(Map.Entry<String, String> property : urlProperties.entrySet()) {
            if(!TextUtils.isEmpty(property.getKey()) && !TextUtils.isEmpty(property.getValue())) {
              urlConnection.setRequestProperty(property.getKey(), property.getValue());
            }
          }
          tileUrlInputStream = urlConnection.getInputStream();
        }
        tile = new Tile(this.width, this.height, readByteStream(tileUrlInputStream));
      } catch (IOException localIOException) {
        tile = NO_TILE;
      }
      return tile;
    }

  private static byte[] readByteStream(InputStream inputStream)
          throws IOException {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    copyByteStream(inputStream, outputStream);
    return outputStream.toByteArray();
  }

  private static long copyByteStream(InputStream paramInputStream, OutputStream paramOutputStream)
          throws IOException {
    byte[] arrayOfByte = new byte[4096];

    long length = 0L;
    while (true) {
      int read = paramInputStream.read(arrayOfByte);
      if (read == -1)
        break;
      paramOutputStream.write(arrayOfByte, 0, read);

      length += read;
    }

    return length;
  }

  public Map<String, String> getUrlRequestProperties() {
      return null;
  }

    /**
     * 返回指定tile的图片宽度。
     * @return 指定tile的图片宽度，单位像素pixel。
     */
  public int getTileWidth()
  {
    return this.width;
  }
    /**
     * 返回指定tile的图片高度。
     * @return 指定tile的图片高度，单位像素pixel。
     */
  public int getTileHeight()
  {
    return this.height;
  }
}

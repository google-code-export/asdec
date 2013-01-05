/*
 *  Copyright (C) 2010-2011 JPEXS
 * 
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.jpexs.asdec;

import SevenZip.Compression.LZMA.Encoder;
import com.jpexs.asdec.action.TagNode;
import com.jpexs.asdec.helpers.Highlighting;
import com.jpexs.asdec.tags.DefineBitsJPEG2Tag;
import com.jpexs.asdec.tags.DefineBitsJPEG3Tag;
import com.jpexs.asdec.tags.DefineBitsJPEG4Tag;
import com.jpexs.asdec.tags.DefineBitsLossless2Tag;
import com.jpexs.asdec.tags.DefineBitsLosslessTag;
import com.jpexs.asdec.tags.DefineBitsTag;
import com.jpexs.asdec.tags.DoABCTag;
import com.jpexs.asdec.tags.JPEGTablesTag;
import com.jpexs.asdec.tags.Tag;
import com.jpexs.asdec.tags.base.ASMSource;
import com.jpexs.asdec.tags.base.TagName;
import com.jpexs.asdec.types.ALPHABITMAPDATA;
import com.jpexs.asdec.types.ALPHACOLORMAPDATA;
import com.jpexs.asdec.types.BITMAPDATA;
import com.jpexs.asdec.types.COLORMAPDATA;
import com.jpexs.asdec.types.RECT;
import com.jpexs.asdec.types.RGB;
import com.jpexs.asdec.types.RGBA;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;
import javax.imageio.ImageIO;

/**
 * Class representing SWF file
 *
 * @author JPEXS
 */
public class SWF {

   /**
    * Default version of SWF file format
    */
   public static final int DEFAULT_VERSION = 10;
   /**
    * Tags inside of file
    */
   public List<Tag> tags = new ArrayList<Tag>();
   /**
    * Rectangle for the display
    */
   public RECT displayRect;
   /**
    * Movie frame rate
    */
   public int frameRate;
   /**
    * Number of frames in movie
    */
   public int frameCount;
   /**
    * Version of SWF
    */
   public int version;
   /**
    * Size of the file
    */
   public long fileSize;
   /**
    * Use compression
    */
   public boolean compressed = false;
   /**
    * Use LZMA compression
    */
   public boolean lzma = false;
   /**
    * Compressed size of the file (LZMA)
    */
   public long compressedSize;
   /**
    * LZMA Properties
    */
   public byte lzmaProperties[];

   /**
    * Gets all tags with specified id
    *
    * @param tagId Identificator of tag type
    * @return List of tags
    */
   public List<Tag> getTagData(int tagId) {
      List<Tag> ret = new ArrayList<Tag>();
      for (Tag tag : tags) {
         if (tag.getId() == tagId) {
            ret.add(tag);
         }
      }
      return ret;
   }

   /**
    * Saves this SWF into new file
    *
    * @param os OutputStream to save SWF in
    * @throws IOException
    */
   public void saveTo(OutputStream os) throws IOException {
      try {
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         SWFOutputStream sos = new SWFOutputStream(baos, version);
         sos.writeRECT(displayRect);
         sos.writeUI8(0);
         sos.writeUI8(frameRate);
         sos.writeUI16(frameCount);

         sos.writeTags(tags);
         sos.writeUI16(0);
         sos.close();
         if (compressed && lzma) {
            os.write('Z');
         } else if (compressed) {
            os.write('C');
         } else {
            os.write('F');
         }
         os.write('W');
         os.write('S');
         os.write(version);
         byte data[] = baos.toByteArray();
         sos = new SWFOutputStream(os, version);
         sos.writeUI32(data.length + 8);

         if (compressed) {
            if (lzma) {
               Encoder enc = new Encoder();
               int val = lzmaProperties[0] & 0xFF;
               int lc = val % 9;
               int remainder = val / 9;
               int lp = remainder % 5;
               int pb = remainder / 5;
               int dictionarySize = 0;
               for (int i = 0; i < 4; i++) {
                  dictionarySize += ((int) (lzmaProperties[1 + i]) & 0xFF) << (i * 8);
               }
               enc.SetDictionarySize(dictionarySize);
               enc.SetLcLpPb(lc, lp, pb);
               baos = new ByteArrayOutputStream();
               enc.SetEndMarkerMode(true);
               enc.Code(new ByteArrayInputStream(data), baos, -1, -1, null);
               data = baos.toByteArray();
               byte udata[] = new byte[4];
               udata[0] = (byte) (data.length & 0xFF);
               udata[1] = (byte) ((data.length >> 8) & 0xFF);
               udata[2] = (byte) ((data.length >> 16) & 0xFF);
               udata[3] = (byte) ((data.length >> 24) & 0xFF);
               os.write(udata);
               os.write(lzmaProperties);
            } else {
               os = new DeflaterOutputStream(os);
            }
         }
         os.write(data);
      } finally {
         if (os != null) {
            os.close();
         }
      }

   }

   /**
    * Construct SWF from stream
    *
    * @param is Stream to read SWF from
    * @throws IOException
    */
   public SWF(InputStream is) throws IOException {
      byte hdr[] = new byte[3];
      is.read(hdr);
      String shdr = new String(hdr);
      if ((!shdr.equals("FWS")) && (!shdr.equals("CWS")) && (!shdr.equals("ZWS"))) {
         throw new IOException("Invalid SWF file");
      }
      version = is.read();
      SWFInputStream sis = new SWFInputStream(is, version, 4);
      fileSize = sis.readUI32();

      if (hdr[0] == 'C') {
         sis = new SWFInputStream(new InflaterInputStream(is), version, 8);
         compressed = true;
      }

      if (hdr[0] == 'Z') {
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         long outSize = sis.readUI32();
         int propertiesSize = 5;
         lzmaProperties = new byte[propertiesSize];
         if (sis.read(lzmaProperties, 0, propertiesSize) != propertiesSize) {
            throw new IOException("LZMA:input .lzma file is too short");
         }
         SevenZip.Compression.LZMA.Decoder decoder = new SevenZip.Compression.LZMA.Decoder();
         if (!decoder.SetDecoderProperties(lzmaProperties)) {
            throw new IOException("LZMA:Incorrect stream properties");
         }

         if (!decoder.Code(sis, baos, fileSize - 8)) {
            throw new IOException("LZMA:Error in data stream");
         }
         sis = new SWFInputStream(new ByteArrayInputStream(baos.toByteArray()), version, 8);
         compressed = true;
         lzma = true;
      }




      displayRect = sis.readRECT();
      // FIXED8 (16 bit fixed point) frameRate
      int tmpFirstByetOfFrameRate = sis.readUI8();
      frameRate = sis.readUI8();
      frameCount = sis.readUI16();
      tags = sis.readTagList(0);
   }

   /**
    * Compress SWF file
    *
    * @param fis Input stream
    * @param fos Output stream
    */
   public static boolean fws2cws(InputStream fis, OutputStream fos) {
      try {
         byte swfHead[] = new byte[8];
         fis.read(swfHead);

         if (swfHead[0] != 'F') {
            fis.close();
            return false;
         }
         swfHead[0] = 'C';
         fos.write(swfHead);
         fos = new DeflaterOutputStream(fos);
         int i;
         while ((i = fis.read()) != -1) {
            fos.write(i);
         }

         fis.close();
         fos.close();
      } catch (FileNotFoundException ex) {
         return false;
      } catch (IOException ex) {
         return false;
      }
      return true;
   }

   /**
    * Decompress SWF file
    *
    * @param fis Input stream
    * @param fos Output stream
    */
   public static boolean cws2fws(InputStream fis, OutputStream fos) {
      try {
         byte swfHead[] = new byte[8];
         fis.read(swfHead);
         InflaterInputStream iis = new InflaterInputStream(fis);
         if (swfHead[0] != 'C') {
            fis.close();
            return false;
         }
         swfHead[0] = 'F';
         fos.write(swfHead);
         int i;
         while ((i = iis.read()) != -1) {
            fos.write(i);
         }

         fis.close();
         fos.close();
      } catch (FileNotFoundException ex) {
         return false;
      } catch (IOException ex) {
         return false;
      }
      return true;
   }

   /**
    * Decompress LZMA compressed SWF file
    *
    * @param fis Input stream
    * @param fos Output stream
    */
   public static boolean zws2fws(InputStream fis, OutputStream fos) {
      try {
         byte hdr[] = new byte[3];
         fis.read(hdr);
         String shdr = new String(hdr);
         if (!shdr.equals("ZWS")) {
            return false;
         }
         int version = fis.read();
         SWFInputStream sis = new SWFInputStream(fis, version, 4);
         long fileSize = sis.readUI32();

         if (hdr[0] == 'Z') {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            long outSize = sis.readUI32();
            int propertiesSize = 5;
            byte lzmaProperties[] = new byte[propertiesSize];
            if (sis.read(lzmaProperties, 0, propertiesSize) != propertiesSize) {
               throw new IOException("LZMA:input .lzma file is too short");
            }
            SevenZip.Compression.LZMA.Decoder decoder = new SevenZip.Compression.LZMA.Decoder();
            if (!decoder.SetDecoderProperties(lzmaProperties)) {
               throw new IOException("LZMA:Incorrect stream properties");
            }

            if (!decoder.Code(sis, baos, fileSize - 8)) {
               throw new IOException("LZMA:Error in data stream");
            }
            SWFOutputStream sos = new SWFOutputStream(fos, version);
            sos.write("FWS".getBytes());
            sos.write(version);
            sos.writeUI32(fileSize);
            sos.write(baos.toByteArray());
            sos.close();
         } else {
            return false;
         }
      } catch (FileNotFoundException ex) {
         return false;
      } catch (IOException ex) {
         return false;
      }
      return true;
   }

   public boolean exportActionScript(String outdir, boolean isPcode) throws Exception {
      boolean asV3Found = false;
      final EventListener evl = new EventListener() {
         public void handleEvent(String event, Object data) {
            if (event.equals("export")) {
               informListeners(event, data);
            }
         }
      };
      List<DoABCTag> abcTags = new ArrayList<DoABCTag>();
      for (Tag t : tags) {
         if (t instanceof DoABCTag) {
            abcTags.add((DoABCTag) t);
            asV3Found = true;
         }
      }
      for (int i = 0; i < abcTags.size(); i++) {
         DoABCTag t = abcTags.get(i);
         t.abc.addEventListener(evl);
         t.abc.export(outdir, isPcode, abcTags, "tag " + (i + 1) + "/" + abcTags.size() + " ");
      }
      for (DoABCTag t : abcTags) {
      }

      if (!asV3Found) {
         List<Object> list2 = new ArrayList<Object>();
         list2.addAll(tags);
         return exportNode(TagNode.createTagList(list2), outdir, isPcode);
      }
      return asV3Found;
   }

   private boolean exportNode(List<TagNode> nodeList, String outdir, boolean isPcode) {
      File dir = new File(outdir);
      if (!dir.exists()) {
         dir.mkdirs();
      }
      List<String> existingNames = new ArrayList<String>();
      for (TagNode node : nodeList) {
         String name;
         if (node.tag instanceof TagName) {
            name = ((TagName) node.tag).getName();
         } else {
            name = node.tag.toString();
         }
         int i = 1;
         String baseName = name;
         while (existingNames.contains(name)) {
            i++;
            name = baseName + "_" + i;
         }
         existingNames.add(name);
         if (node.subItems.isEmpty()) {
            if (node.tag instanceof ASMSource) {
               try {
                  String f = outdir + File.separatorChar + name + ".as";
                  informListeners("export", "Exporting " + f + " ...");
                  String ret;
                  if (isPcode) {
                     ret = ((ASMSource) node.tag).getASMSource(SWF.DEFAULT_VERSION);
                  } else {
                     List<com.jpexs.asdec.action.Action> as = ((ASMSource) node.tag).getActions(SWF.DEFAULT_VERSION);
                     com.jpexs.asdec.action.Action.setActionsAddresses(as, 0, SWF.DEFAULT_VERSION);
                     ret = (Highlighting.stripHilights(com.jpexs.asdec.action.Action.actionsToSource(as, SWF.DEFAULT_VERSION)));
                  }


                  FileOutputStream fos = new FileOutputStream(f);
                  fos.write(ret.getBytes());
                  fos.close();
               } catch (Exception ex) {
               }
            }
         } else {
            exportNode(node.subItems, outdir + File.separatorChar + name, isPcode);
         }

      }
      return true;
   }
   protected HashSet<EventListener> listeners = new HashSet<EventListener>();

   public void addEventListener(EventListener listener) {
      listeners.add(listener);
   }

   public void removeEventListener(EventListener listener) {
      listeners.remove(listener);
   }

   protected void informListeners(String event, Object data) {
      for (EventListener listener : listeners) {
         listener.handleEvent(event, data);
      }
   }

   private String getImageFormat(byte data[]) {
      if (hasErrorHeader(data)) {
         return "jpg";
      }
      if (data.length > 2 && ((data[0] & 0xff) == 0xff) && ((data[1] & 0xff) == 0xd8)) {
         return "jpg";
      }
      if (data.length > 6 && ((data[0] & 0xff) == 0x47) && ((data[1] & 0xff) == 0x49) && ((data[2] & 0xff) == 0x46) && ((data[3] & 0xff) == 0x38) && ((data[4] & 0xff) == 0x39) && ((data[5] & 0xff) == 0x61)) {
         return "gif";
      }

      if (data.length > 8 && ((data[0] & 0xff) == 0x89) && ((data[1] & 0xff) == 0x50) && ((data[2] & 0xff) == 0x4e) && ((data[3] & 0xff) == 0x47) && ((data[4] & 0xff) == 0x0d) && ((data[5] & 0xff) == 0x0a) && ((data[6] & 0xff) == 0x1a) && ((data[7] & 0xff) == 0x0a)) {
         return "png";
      }

      return "unk";
   }

   private boolean hasErrorHeader(byte data[]) {
      if (data.length > 4) {
         if ((data[0] & 0xff) == 0xff) {
            if ((data[1] & 0xff) == 0xd9) {
               if ((data[2] & 0xff) == 0xff) {
                  if ((data[3] & 0xff) == 0xd8) {
                     return true;
                  }
               }
            }
         }
      }
      return false;
   }

   public void exportImages(String outdir) throws IOException {
      JPEGTablesTag jtt = null;
      for (Tag t : tags) {
         if (t instanceof JPEGTablesTag) {
            jtt = (JPEGTablesTag) t;
         }
      }

      for (Tag t : tags) {
         if ((t instanceof DefineBitsJPEG2Tag) || (t instanceof DefineBitsJPEG3Tag) || (t instanceof DefineBitsJPEG4Tag)) {
            byte imageData[] = null;
            int characterID = 0;
            if (t instanceof DefineBitsJPEG2Tag) {
               imageData = ((DefineBitsJPEG2Tag) t).imageData;
               characterID = ((DefineBitsJPEG2Tag) t).characterID;
            }
            if (t instanceof DefineBitsJPEG3Tag) {
               imageData = ((DefineBitsJPEG3Tag) t).imageData;
               characterID = ((DefineBitsJPEG3Tag) t).characterID;
            }
            if (t instanceof DefineBitsJPEG4Tag) {
               imageData = ((DefineBitsJPEG4Tag) t).imageData;
               characterID = ((DefineBitsJPEG4Tag) t).characterID;
            }

            FileOutputStream fos = null;
            try {
               fos = new FileOutputStream(outdir + File.separator + characterID + "." + getImageFormat(imageData));
               if (hasErrorHeader(imageData)) {
                  fos.write(imageData, 4, imageData.length - 4);
               } else {
                  fos.write(imageData);
               }
            } finally {
               if (fos != null) {
                  try {
                     fos.close();
                  } catch (Exception ex) {
                     //ignore
                  }
               }
            }
         }
         if (t instanceof DefineBitsLosslessTag) {
            DefineBitsLosslessTag dbl = (DefineBitsLosslessTag) t;
            BufferedImage bi = new BufferedImage(dbl.bitmapWidth, dbl.bitmapHeight, BufferedImage.TYPE_INT_RGB);
            Graphics g = bi.getGraphics();
            COLORMAPDATA colorMapData = null;
            BITMAPDATA bitmapData = null;
            if (dbl.bitmapFormat == DefineBitsLosslessTag.FORMAT_8BIT_COLORMAPPED) {
               colorMapData = dbl.getColorMapData();
            }
            if ((dbl.bitmapFormat == DefineBitsLosslessTag.FORMAT_15BIT_RGB) || (dbl.bitmapFormat == DefineBitsLosslessTag.FORMAT_24BIT_RGB)) {
               bitmapData = dbl.getBitmapData();
            }
            int pos32aligned = 0;
            int pos = 0;
            for (int y = 0; y < dbl.bitmapHeight; y++) {
               for (int x = 0; x < dbl.bitmapWidth; x++) {
                  if (dbl.bitmapFormat == DefineBitsLosslessTag.FORMAT_8BIT_COLORMAPPED) {
                     RGB color = colorMapData.colorTableRGB[colorMapData.colorMapPixelData[pos32aligned]];
                     g.setColor(new Color(color.red, color.green, color.blue));
                  }
                  if (dbl.bitmapFormat == DefineBitsLosslessTag.FORMAT_15BIT_RGB) {
                     g.setColor(new Color(bitmapData.bitmapPixelDataPix15[pos].red * 8, bitmapData.bitmapPixelDataPix15[pos].green * 8, bitmapData.bitmapPixelDataPix15[pos].blue * 8));
                  }
                  if (dbl.bitmapFormat == DefineBitsLosslessTag.FORMAT_24BIT_RGB) {
                     g.setColor(new Color(bitmapData.bitmapPixelDataPix24[pos].red, bitmapData.bitmapPixelDataPix24[pos].green, bitmapData.bitmapPixelDataPix24[pos].blue));
                  }
                  g.fillRect(x, y, 1, 1);
                  pos32aligned++;
                  pos++;
               }
               while ((pos32aligned % 4 != 0)) {
                  pos32aligned++;
               }
            }
            ImageIO.write(bi, "PNG", new File(outdir + File.separator + dbl.characterID + ".png"));
         }
         if (t instanceof DefineBitsLossless2Tag) {
            DefineBitsLossless2Tag dbl = (DefineBitsLossless2Tag) t;
            BufferedImage bi = new BufferedImage(dbl.bitmapWidth, dbl.bitmapHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics g = bi.getGraphics();
            ALPHACOLORMAPDATA colorMapData = null;
            ALPHABITMAPDATA bitmapData = null;
            if (dbl.bitmapFormat == DefineBitsLossless2Tag.FORMAT_8BIT_COLORMAPPED) {
               colorMapData = dbl.getColorMapData();
            }
            if ((dbl.bitmapFormat == DefineBitsLossless2Tag.FORMAT_15BIT_RGB) || (dbl.bitmapFormat == DefineBitsLossless2Tag.FORMAT_24BIT_RGB)) {
               bitmapData = dbl.getBitmapData();
            }
            int pos32aligned = 0;
            int pos = 0;
            for (int y = 0; y < dbl.bitmapHeight; y++) {
               for (int x = 0; x < dbl.bitmapWidth; x++) {
                  if ((dbl.bitmapFormat == DefineBitsLossless2Tag.FORMAT_8BIT_COLORMAPPED)) {
                     RGBA color = colorMapData.colorTableRGB[colorMapData.colorMapPixelData[pos32aligned]];
                     g.setColor(new Color(color.red, color.green, color.blue, color.alpha));
                  }
                  if ((dbl.bitmapFormat == DefineBitsLossless2Tag.FORMAT_15BIT_RGB) || (dbl.bitmapFormat == DefineBitsLossless2Tag.FORMAT_24BIT_RGB)) {
                     g.setColor(new Color(bitmapData.bitmapPixelData[pos].red, bitmapData.bitmapPixelData[pos].green, bitmapData.bitmapPixelData[pos].blue, bitmapData.bitmapPixelData[pos].alpha));
                  }
                  g.fillRect(x, y, 1, 1);
                  pos32aligned++;
                  pos++;
               }
               while ((pos32aligned % 4 != 0)) {
                  pos32aligned++;
               }
            }
            ImageIO.write(bi, "PNG", new File(outdir + File.separator + dbl.characterID + ".png"));
         }
         if ((jtt != null) && (t instanceof DefineBitsTag)) {
            DefineBitsTag dbt = (DefineBitsTag) t;
            FileOutputStream fos = null;
            try {
               fos = new FileOutputStream(outdir + File.separator + dbt.characterID + ".jpg");
               byte data[] = jtt.getData(10);
               fos.write(data, hasErrorHeader(data) ? 4 : 0, data.length - (hasErrorHeader(data) ? 6 : 2));
               fos.write(dbt.jpegData, hasErrorHeader(dbt.jpegData) ? 6 : 2, dbt.jpegData.length - (hasErrorHeader(data) ? 6 : 2));
            } finally {
               if (fos != null) {
                  try {
                     fos.close();
                  } catch (Exception ex) {
                     //ignore
                  }
               }
            }
         }
      }
   }
}

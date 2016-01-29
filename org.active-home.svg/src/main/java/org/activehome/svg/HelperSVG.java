package org.activehome.svg;

/*
 * #%L
 * Active Home :: SVG
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2016 org.active-home
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */


import org.apache.batik.dom.svg.SAXSVGDocumentFactory;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.transcoder.Transcoder;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.util.XMLResourceDescriptor;
import org.apache.fop.svg.PDFTranscoder;
import org.kevoree.log.Log;
import org.w3c.dom.Document;

import java.io.*;

/**
 * Some tools to facilitate SVG management
 *
 * @author Jacky Bourgeois
 * @version %I%, %G%
 */
public class HelperSVG {

    public static Document loadSVG(String file, ClassLoader loader) {
        try {
            String parser = XMLResourceDescriptor.getXMLParserClassName();
            SAXSVGDocumentFactory f = new SAXSVGDocumentFactory(parser);
            return f.createDocument(null, loader.getResourceAsStream(file));
        } catch (IOException exception) {
            Log.error("IO Exception: " + exception.getMessage());
        } catch (Exception exception) {
            Log.error("Exception: " + exception.getMessage());
        }
        return null;
    }

    public static void export2pdf(Document doc, String fileName) {
        Transcoder transcoder = new PDFTranscoder();
        // Set the transcoder input and output.
        TranscoderInput input = new TranscoderInput(doc);
        OutputStream ostream = null;
        try {
            ostream = new FileOutputStream(fileName);
            TranscoderOutput output = new TranscoderOutput(ostream);
            // Perform the transcoding.
            transcoder.transcode(input, output);
            ostream.flush();
            ostream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (TranscoderException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void export2svg(Document doc, String fileName) {
        SVGGraphics2D svgGenerator = new SVGGraphics2D(doc);
        try {
            boolean useCSS = true; // we want to use CSS style attributes
            Writer out = new OutputStreamWriter(new FileOutputStream(fileName), "UTF-8");
            svgGenerator.stream(out, useCSS);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

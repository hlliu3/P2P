package com.bjpowernode;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.datamatrix.DataMatrixWriter;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws WriterException, IOException {
        Map<EncodeHintType, Object> map = new HashMap<>();
        map.put(EncodeHintType.CHARACTER_SET,"UTF-8");

        BitMatrix hello_word = new MultiFormatWriter().encode("Hello Word", BarcodeFormat.QR_CODE, 100, 100, map);
        Path path = FileSystems.getDefault().getPath("D://", "qrCode.jpg");
        MatrixToImageWriter.writeToPath(hello_word,"jpg",path);
        System.out.println( "Hello World!" );
    }
}

package com.zingit.restaurant;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.DisplayMetrics;

import com.dantsu.escposprinter.EscPosPrinter;
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections;
import com.dantsu.escposprinter.exceptions.EscPosBarcodeException;
import com.dantsu.escposprinter.exceptions.EscPosConnectionException;
import com.dantsu.escposprinter.exceptions.EscPosEncodingException;
import com.dantsu.escposprinter.exceptions.EscPosParserException;
import com.dantsu.escposprinter.textparser.PrinterTextParserImg;


public class PrintSlip extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_slip);

        EscPosPrinter printer = null;

        try {
            printer = new EscPosPrinter(BluetoothPrintersConnections.selectFirstPaired(), 203, 72f, 32);
        } catch (EscPosConnectionException e) {
            e.printStackTrace();
        }
        try {
            printer
                    .printFormattedText(
                            "[C]<img>" + PrinterTextParserImg.bitmapToHexadecimalString(printer, this.getApplicationContext().getResources().getDrawableForDensity(R.drawable.zing_business_logo, DisplayMetrics.DENSITY_MEDIUM))+"</img>\n" +
                                    "[L]\n" +
                                    "[C]<u><font size='big'>ORDER N°045</font></u>\n" +
                                    "[L]\n" +
                                    "[C]================================\n" +
                                    "[L]\n" +
                                    "[L]<b>BEAUTIFUL SHIRT</b>[R]9.99e\n" +
                                    "[L]  + Size : S\n" +
                                    "[L]\n" +
                                    "[L]<b>AWESOME HAT</b>[R]24.99e\n" +
                                    "[L]  + Size : 57/58\n" +
                                    "[L]\n" +
                                    "[C]--------------------------------\n" +
                                    "[R]TOTAL PRICE :[R]34.98e\n" +
                                    "[R]TAX :[R]4.23e\n" +
                                    "[L]\n" +
                                    "[C]================================\n" +
                                    "[L]\n" +
                                    "[L]<font size='tall'>Customer :</font>\n" +
                                    "[L]Raymond DUPONT\n" +
                                    "[L]5 rue des girafes\n" +
                                    "[L]31547 PERPETES\n" +
                                    "[L]Tel : +33801201456\n" +
                                    "[L]\n" +
                                    "[C]<barcode type='ean13' height='10'>831254784551</barcode>\n" +
                                    "[C]<qrcode size='20'>http://www.developpeur-web.dantsu.com/</qrcode>"
                    );



        } catch (EscPosConnectionException e) {
            e.printStackTrace();
        } catch (EscPosParserException e) {
            e.printStackTrace();
        } catch (EscPosEncodingException e) {
            e.printStackTrace();
        } catch (EscPosBarcodeException e) {
            e.printStackTrace();
        }


    }
}
package net.netzgut.integral.tfa.components;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Base64;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

public class QRCodeComponent {

    private static final Logger log = LoggerFactory.getLogger(QRCodeComponent.class);

    @Parameter(required = true, allowNull = false, defaultPrefix = BindingConstants.PROP)
    @Property
    private URI                 uri;

    @Parameter(required = true, allowNull = false, defaultPrefix = BindingConstants.LITERAL)
    @Property
    private int                 width;

    @Parameter(required = true, allowNull = false, defaultPrefix = BindingConstants.LITERAL)
    @Property
    private int                 height;

    @Parameter(required = false, allowNull = false, defaultPrefix = BindingConstants.LITERAL)
    @Property
    private String              alt;

    String getDataURI() {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        try {
            BitMatrix matrix = qrCodeWriter.encode(this.uri.toString(), BarcodeFormat.QR_CODE, this.width, this.height);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(matrix, "PNG", out);

            String image = Base64.getEncoder().encodeToString(out.toByteArray());

            return String.format("data:image/png;base64,%s", image);
        }
        catch (WriterException | IOException e) {
            log.error("Couldn't generate QR Code"); // TODO: Find a better way to log without logging sensitive information
        }
        return "";
    }
}

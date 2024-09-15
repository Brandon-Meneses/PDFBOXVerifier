import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.PDSignature;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.cms.Attribute;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.asn1.cms.CMSAttributes;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.cms.SignedData;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.cms.CMSSignedData;

import java.io.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.List;

public class PDFBoxVerifier {

    public static boolean verifyTimestampInPDF(String pdfFilePath) throws Exception {
        PDDocument document = PDDocument.load(new File(pdfFilePath));
        List<PDSignature> signatures = document.getSignatureDictionaries();

        for (PDSignature signature : signatures) {
            byte[] content = signature.getContents(new FileInputStream(pdfFilePath));
            CMSSignedData signedData = new CMSSignedData(content);
            AttributeTable signedAttributes = signedData.getSignerInfos().iterator().next().getSignedAttributes();
            if (signedAttributes != null) {
                Attribute tsAttr = signedAttributes.get(CMSAttributes.signingTime);
                if (tsAttr != null) {
                    System.out.println("Timestamp attribute found: " + tsAttr.toString());
                    return true;
                }
            }
        }

        return false;
    }

    public static void main(String[] args) {
        try {
            boolean hasTimestamp = verifyTimestampInPDF("/Users/brandonluismenesessolorzano/Downloads/F_SEPARATA 5 - METODO DE HOGG SIMPLIFICADO(179).pdf");
            if (hasTimestamp) {
                System.out.println("El documento PDF contiene un sello de tiempo.");
            } else {
                System.out.println("El documento PDF NO contiene un sello de tiempo.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

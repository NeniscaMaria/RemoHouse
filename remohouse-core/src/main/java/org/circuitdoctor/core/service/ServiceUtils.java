package org.circuitdoctor.core.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.circuitdoctor.core.model.ActionLogGSM;
import org.circuitdoctor.core.model.GSMController;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.imageio.ImageIO;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.*;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

@Service
public class ServiceUtils {

    private static final Logger log = LoggerFactory.getLogger(ServiceUtils.class);
    public ServiceUtils() {
    }
    public String sendMessage(String message,String phoneNumber) {
        log.trace("sendMessage - method entered");
        String user="579042";
        String parola="ca00ddee9a532243928f16c22b49002b";
        String telefon=phoneNumber;


        StringBuilder command = new StringBuilder("curl -X POST https://www.clickphone.ro/api/sms");

        StringBuilder param=new StringBuilder();
        param.append(" --data ").append("user=").append(user);
        param.append(" --data ").append("parola=").append(parola);
        param.append(" --data ").append("telefon=").append(telefon);
        param.append(" --data ").append("text=").append(message);
        command.append(param);
        System.out.println(command);


        Process process = null;
        try {
            process = Runtime.getRuntime().exec(command.toString());
            process.waitFor();
            BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = null;
            StringBuilder response=new StringBuilder();
            try {
                while ((line = input.readLine()) != null)
                    response.append(line).append("\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(response.toString().contains("<result>success</result>")){
                return "ok";
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();

        }



        log.trace("sendMessage API- something is wrong");
        return "something went wrong";
    }


    public byte[] getQRCode(GSMController gsmController){
        /*
        DESCR: generates a QRCode from the GSMController {gsmController}
        PARAM: gsmController - GSMController
        PRE: GSMContrller has to be an already existing gsmController
        POST: returns the QR code in form of a byte array
                throws error if there is a problem when generating the QR code
         */
        try {
            StringBuilder text=new StringBuilder("");
            text.append("ID : ").append(gsmController.getId()).append("\n");
            text.append("Phone Number : ").append(gsmController.getPhoneNumber()).append("\n");
            text.append("Type : ").append(gsmController.getType()).append("\n");
            text.append("City : ").append(gsmController.getRoom().getLocation().getCity());
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(text.toString(), BarcodeFormat.QR_CODE, 200, 200);

            //creates the image
            //Path path = FileSystems.getDefault().getPath("./myQRCode.jpg");
            //MatrixToImageWriter.writeToPath(bitMatrix, "JPG", path);

            //changing the image into a byte array
            ByteArrayOutputStream jpgOutputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "JPG", jpgOutputStream);


            return jpgOutputStream.toByteArray();
        } catch (WriterException e) {
            log.trace("method getQrCode -Could not generate QR Code, WriterException :: " + e.getMessage());
        } catch (IOException e) {
            log.trace("method getQrCode -Could not generate QR Code, IOException :: " + e.getMessage());
        }
        return null;
    }


    public void sendEmailWithAttachment(String from,String to,String password,String emailMessage,String subject,String fileName){
        /*
        DESCR:sends an email from an email to another one with an attachmentFile
        PARAM:from         - string: the email address from which the email is sent
              to           - string: the email address to which the email is sent
              password     - string: the password of the {from} email
              emailMessage - string : the content of the email
              subject      - string: the subject of the email
        PRE:none of the parameters are null.
            {password} is the correct password for {from}
            {from} and {to} are existing emails
        POST:-
         */
        to="andrei.bangau99@gmail.com";
        Properties props = System.getProperties();
        props.setProperty("mail.transport.protocol", "smtp");
        props.setProperty("mail.host", "mail.circuitdoctor.ro");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.auth", "true");

        // Get the default Session object.
        try {
            Session session = Session.getDefaultInstance(props, null);
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.addRecipients(Message.RecipientType.TO, to);
            message.setSubject(subject);
            message.setText(emailMessage);

            //attachment
            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText("These are the action logs");
            // Create a multipar message
            Multipart multipart = new MimeMultipart();

            // Set text message part
            multipart.addBodyPart(messageBodyPart);


            messageBodyPart = new MimeBodyPart();

            DataSource source = new FileDataSource(fileName);
            messageBodyPart.setDataHandler(new DataHandler(source));
            messageBodyPart.setFileName(fileName);
            multipart.addBodyPart(messageBodyPart);

            // Send the complete message parts
            message.setContent(multipart );
            Transport transport = session.getTransport("smtp");
            transport.connect("mail.circuitdoctor.ro",26, from, password);//CAUSES EXCEPTION
            transport.sendMessage(message,message.getAllRecipients());
            log.trace("Sent message successfully....");
        } catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }
    public void writeToPDFFile(List<ActionLogGSM> actionLogGSMList,String fileName,boolean takeAll,String startDate,String endDate){
        try{
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream("logFile.pdf"));

            document.open();
            Font bold =  new Font(com.itextpdf.text.Font.FontFamily.TIMES_ROMAN, 20, Font.BOLD);
            String title="";
            if(takeAll){
                title="All time actions table from the gsm controllers selected";
            }
            else{
                title="Actions table from the gsm controllers selected that happened between "+startDate+" and "+endDate;
            }
            Paragraph p=new Paragraph(title,bold);

            p.setAlignment(Element.ALIGN_CENTER);
            document.add(p);
            document.add(Chunk.NEWLINE);
            PdfPTable table = new PdfPTable(4);
            addTableHeader(table);
            for (ActionLogGSM action:actionLogGSMList) {
                addCustomRows(table,action);
            }

            document.add(table);
            document.close();
        }
        catch (Exception e){
            log.trace(e.getMessage());
        }


    }
    private void addTableHeader(PdfPTable table) {
        Stream.of("operation type", "Date", "Gsm Id","User name")
                .forEach(columnTitle -> {
                    PdfPCell header = new PdfPCell();
                    header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    header.setBorderWidth(2);
                    header.setPhrase(new Phrase(columnTitle));
                    header.setHorizontalAlignment(Element.ALIGN_CENTER);
                    header.setVerticalAlignment(Element.ALIGN_CENTER);
                    table.addCell(header);
                });
    }

    private  void addCustomRows(PdfPTable table,ActionLogGSM actionLogGSM){


        PdfPCell operationType = new PdfPCell(new Phrase(actionLogGSM.getOperationType()));
        operationType.setHorizontalAlignment(Element.ALIGN_CENTER);
        operationType.setVerticalAlignment(Element.ALIGN_CENTER);
        table.addCell(operationType);



        PdfPCell date = new PdfPCell(new Phrase(actionLogGSM.getDateTime().withNano(0).toString().replace("T"," ")));
        date.setHorizontalAlignment(Element.ALIGN_CENTER);
        date.setVerticalAlignment(Element.ALIGN_CENTER);
        table.addCell(date);

        PdfPCell gsmId = new PdfPCell(new Phrase(actionLogGSM.getGsmController().getId().toString()));
        gsmId.setHorizontalAlignment(Element.ALIGN_CENTER);
        gsmId.setVerticalAlignment(Element.ALIGN_CENTER);
        table.addCell(gsmId);

        PdfPCell username = new PdfPCell(new Phrase(actionLogGSM.getUser().getName()));
        username.setHorizontalAlignment(Element.ALIGN_CENTER);
        username.setVerticalAlignment(Element.ALIGN_CENTER);
        table.addCell(username);


    }
    public void writeToCSVFile(List<ActionLogGSM> actionLogGSMList,String fileName) throws IOException {
        List<List<String>> rows=new ArrayList<>();
        for (ActionLogGSM action:actionLogGSMList) {
            rows.add(Arrays.asList(action.getOperationType(), action.getDateTime().toString(), action.getGsmController().getId().toString()
                    ,action.getUser().getName()));

        }

        FileWriter csvWriter = new FileWriter(fileName);
        csvWriter.append("OperationType").append(",");
        csvWriter.append("Date").append(",");
        csvWriter.append("GSM ID").append(",");
        csvWriter.append("User Name");
        csvWriter.append("\n");

        for (List<String> rowData : rows) {
            csvWriter.append(String.join(",", rowData));
            csvWriter.append("\n");
        }

        csvWriter.flush();
        csvWriter.close();
    }

    public String hashPassword(String password){

        Integer salt=ThreadLocalRandom.current().nextInt(5, 15 + 1);
        return BCrypt.hashpw(password, BCrypt.gensalt(salt));
    }
    public boolean checkPassword(String dumbPassword,String hashedPassword){
        return BCrypt.checkpw(dumbPassword, hashedPassword);
    }

}

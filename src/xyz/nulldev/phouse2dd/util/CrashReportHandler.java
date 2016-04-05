package xyz.nulldev.phouse2dd.util;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import xyz.nulldev.phouse2dd.Phouse2DD;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;

/**
 * Project: Phouse2DD (Extracted from: OtakuCentral)
 * Created: 11/08/15
 * Author: nulldev
 */

/**
 * This class handles crashes in a user-friendly way. When the application crashes, it notifies the end-user by opening a simple dialog, while adding a button
 * for developers.
 */
public class CrashReportHandler implements Thread.UncaughtExceptionHandler {
    @Override
    public void uncaughtException(Thread t, Throwable e) {
        CrashReportHandler.reportCrash(t, e);
    }

    public static void reportCrash(Thread t, Throwable e) {
        reportCrash(t, e, null);
    }

    public static void reportCrash(Thread t, Throwable e, String details) {
        Thread.setDefaultUncaughtExceptionHandler(null); //This line prevents a disastrous crash loop :P
        System.out.println("Phouse2DD has crashed!\n" +
                "The stack trace follows:\n");
        e.printStackTrace();

        if(Phouse2DD.PRIMARY_STAGE != null) {
            Phouse2DD.PRIMARY_STAGE.close();
        }

        if(details != null) {
            System.out.println("A message is also included:\n" + details);
        }

        Alert dlg = new Alert(Alert.AlertType.ERROR);
        dlg.setTitle("Crash - Phouse2DD");
        dlg.setHeaderText("Phouse2DD has crashed!");
        //Add the exit, details and report buttons
        ButtonType detailsButton = new ButtonType("Details");
        ButtonType reportButton = new ButtonType("Report");
        ButtonType exitButton = new ButtonType("Exit");
        dlg.getButtonTypes().clear();
        dlg.getButtonTypes().add(detailsButton);
//        dlg.getButtonTypes().add(reportButton);
        dlg.getButtonTypes().add(exitButton);
        //Add the error details
        dlg.setContentText("Phouse2DD has experienced an unrecoverable error!\n\n" +
//                "We would love for you to report the error to us by pressing the 'Report' button so we can fix it!\n\n" +
                "If you are a developer, you can view more details by pressing the 'Details' button below.");

        //If there is a message, include it
        if(details != null) {
            dlg.setContentText(dlg.getContentText() + "\n\nMessage: " + details);
        }

        dlg.setOnHidden(event -> {
            if (dlg.getResult() != null) {
                ButtonType result = dlg.getResult();

                if (result.equals(detailsButton)) {
                    Alert detailsDialog = new Alert(Alert.AlertType.INFORMATION);
                    dlg.close();
                    detailsDialog.setTitle("Crash Details - Phouse2DD");
                    detailsDialog.setHeaderText("Crash Stacktrace");
                    detailsDialog.setContentText(stackTraceToString(e));
                    detailsDialog.show();
                    detailsDialog.setOnHidden(v -> Platform.exit());
                    detailsDialog.setHeight(500);
                    detailsDialog.setWidth(900);
                } else if (result.equals(reportButton)) {
                    /*Alert dialog = new Alert(Alert.AlertType.NONE);
                    if(reportCrashToCloud(t, e, details)) {
                        dialog.setAlertType(Alert.AlertType.INFORMATION);
                        dialog.setTitle("Crash Reported - OtakuCentral");
                        dialog.setHeaderText("Crash Reported");
                        dialog.setContentText("Thanks! We have been notified of the crash, we'll fix it soon!");
                    } else {
                        dialog.setAlertType(Alert.AlertType.ERROR);
                        dialog.setTitle("Crash Report Failed - OtakuCentral");
                        dialog.setHeaderText("Failed to Report Crash");
                        dialog.setContentText("Sorry, it looks like something went wrong reporting your crash, you can just ignore this.");
                    }
                    dialog.show();
                    dialog.setWidth(600);*/
                } else if (result.equals(exitButton)) {
                    Platform.exit();
                }
            }
        });

        dlg.show();
        dlg.setHeight(300);
        dlg.setWidth(dlg.getWidth() + 50);
    }

    static String stackTraceToString(Throwable t) {
        StringWriter sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }

    /**
     * @author Crunchify.com
     *
     * Referenced from: http://crunchify.com/how-to-generate-java-thread-dump-programmatically/
     */
    static String crunchifyGenerateThreadDump() {
        final StringBuilder dump = new StringBuilder();
        final ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        final ThreadInfo[] threadInfos = threadMXBean.getThreadInfo(threadMXBean.getAllThreadIds(), 100);
        for (ThreadInfo threadInfo : threadInfos) {
            dump.append('"');
            dump.append(threadInfo.getThreadName());
            dump.append("\" ");
            final Thread.State state = threadInfo.getThreadState();
            dump.append("\n   java.lang.Thread.State: ");
            dump.append(state);
            final StackTraceElement[] stackTraceElements = threadInfo.getStackTrace();
            for (final StackTraceElement stackTraceElement : stackTraceElements) {
                dump.append("\n        at ");
                dump.append(stackTraceElement);
            }
            dump.append("\n\n");
        }
        return dump.toString();
    }

    /*
    This is so messy I want to cri :(
     */

    /**
     * Reports a crash to Google Docs (Responses can be viewed here: https://docs.google.com/spreadsheets/d/1rckNywxRL4zI6ty2Lijdmz5CLbqkb7cwSOQi23IcSJw/edit?usp=sharing)
     * @param t The thread in which the crash occured in.
     * @param e The exception thrown.
     * @param message The message by the developer.
     * @return Whether or not the crash was successfully reported.
     */
    /*static boolean reportCrashToCloud(Thread t, Throwable e, String message) {
        String version = "";
        try {
            version = URLEncoder.encode(String.valueOf(OtakuCentral.VERSION), "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            System.out.println("Could not encode version!");
            e1.printStackTrace();
        }
        String md5 = "";
        try {
            byte[] b = Files.readAllBytes(Paths.get(CrashReportHandler.class.getProtectionDomain().getCodeSource().getLocation().toURI()));
            byte[] hash = MessageDigest.getInstance("MD5").digest(b);
            md5 = URLEncoder.encode(DatatypeConverter.printHexBinary(hash), "UTF-8");
        } catch (IOException | URISyntaxException | NoSuchAlgorithmException e1) {
            System.out.println("Could not determine MD5 of JAR file!");
            e1.printStackTrace();
        }
        String prgMessage = "";
        try {
            prgMessage = URLEncoder.encode(message != null ? message : "", "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            System.out.println("Could not encode program message!");
            e1.printStackTrace();
        }
        String exceptMessage = "";
        try {
            exceptMessage = URLEncoder.encode(e.getMessage(), "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            System.out.println("Could not encode exception message!");
            e1.printStackTrace();
        }
        String stackTrace = "";
        try {
            stackTrace = URLEncoder.encode(stackTraceToString(e), "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            System.out.println("Could not encode stack trace!");
            e1.printStackTrace();
        }
        String threadDmp = "";
        try {
            threadDmp = URLEncoder.encode(crunchifyGenerateThreadDump(), "UTF-8");
        } catch(Exception idk) {
            System.out.println("Could not create thread dump!");
            idk.printStackTrace();
        }
        String ram = "";
        try {
            com.sun.management.OperatingSystemMXBean bean =
                    (com.sun.management.OperatingSystemMXBean)
                            java.lang.management.ManagementFactory.getOperatingSystemMXBean();
            ram = URLEncoder.encode(String.valueOf(bean.getTotalPhysicalMemorySize()), "UTF-8");
        } catch(Exception idk) {
            System.out.println("Could not determine total system RAM!");
            idk.printStackTrace();
        }
        String os = "";
        try {
            os = URLEncoder.encode(System.getProperty("os.name"), "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            System.out.println("Could not encode os!");
            e1.printStackTrace();
        }
        String vm = "";
        try {
            vm = URLEncoder.encode(System.getProperty("java.vm.name") + " VERSION: " + System.getProperty("java.version"), "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            System.out.println("Could not encode JVM details!");
            e1.printStackTrace();
        }
        String proc = "";
        try {
            proc = URLEncoder.encode(System.getProperty("os.arch"), "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            System.out.println("Could not encode processor architecture!");
            e1.printStackTrace();
        }
        String dateTime = "";
        try {
            dateTime = URLEncoder.encode(LocalDateTime.now().toString(), "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            System.out.println("Could not encode date time!");
            e1.printStackTrace();
        }

        String urlParameters = "entry.188134433=" + version + "&entry.1736267158=" + md5 + "&entry.844501749=" + prgMessage + "&entry.1716057352=" + exceptMessage + "&entry.1707058498=" + stackTrace + "&entry.553938186=" + threadDmp + "&entry.729005134=" + ram + "&entry.1103697259=" + os + "&entry.1537566326=" + vm + "&entry.1423120096=" + proc + "&entry.468653237=" + dateTime;
        byte[] postData = urlParameters.getBytes( StandardCharsets.UTF_8 );
        int postDataLength = postData.length;
        URL url;
        try {
            url = new URL("https://docs.google.com/forms/d/1SKZcfVQoiLnqlZBbJzPG_n6BqYa91W_QpeF16uJp59o/formResponse");
        } catch (MalformedURLException e1) {
            System.out.println("Could not construct URL!");
            e1.printStackTrace();
            return false;
        }
        HttpURLConnection conn;
        try {
            conn = (HttpURLConnection) url.openConnection();
        } catch (IOException e1) {
            System.out.println("Could not connect to crash report URL!");
            e1.printStackTrace();
            return false;
        }
        conn.setDoOutput( true );
        conn.setInstanceFollowRedirects(false);
        try {
            conn.setRequestMethod( "POST" );
        } catch (ProtocolException e1) {
            System.out.println("Could not set protocol!");
            e1.printStackTrace();
            return false;
        }
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("charset", "utf-8");
        conn.setRequestProperty("Content-Length", Integer.toString(postDataLength));
        conn.setUseCaches(false);
        try( DataOutputStream wr = new DataOutputStream( conn.getOutputStream())) {
            wr.write( postData );
        } catch (IOException e1) {
            System.out.println("Could not write data!");
            e1.printStackTrace();
            return false;
        }
        return true;
    }*/
}
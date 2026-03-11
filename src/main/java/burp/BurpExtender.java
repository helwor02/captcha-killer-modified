package burp;

import ui.GUI;
import ui.Menu;
import utils.Util;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

/* loaded from: captcha-killer-modified-0.21-jdk8.jar:burp/BurpExtender.class */
public class BurpExtender implements IBurpExtender, ITab, IIntruderPayloadGeneratorFactory, IIntruderPayloadGenerator, IHttpListener ,IIntruderPayloadProcessor{
    public static IBurpExtenderCallbacks callbacks;
    public static IExtensionHelpers helpers;
    public static boolean isShowIntruderResult = true;
    public static PrintWriter stdout;
    public static PrintWriter stderr;
    public static GUI gui;
    private String extensionName = "captcha-killer-modified";
    private String version = "0.24.1";
    public String processname = "captcha-killer-modified";
    public static Boolean Isreplace = false;

    @Override // burp.IBurpExtender
    public void registerExtenderCallbacks(IBurpExtenderCallbacks calllbacks) {
        callbacks = calllbacks;
        helpers = calllbacks.getHelpers();


        stdout = new PrintWriter(calllbacks.getStdout(), true);
        stderr = new PrintWriter(calllbacks.getStderr(), true);
        gui = new GUI();
        callbacks.setExtensionName(String.format("%s %s", this.extensionName, this.version));
        calllbacks.registerContextMenuFactory(new Menu());
        calllbacks.registerIntruderPayloadGeneratorFactory(this);
        callbacks.registerIntruderPayloadProcessor(this);
        callbacks.registerHttpListener(this);
        stdout = new PrintWriter(callbacks.getStdout(), true);
        stderr = new PrintWriter(callbacks.getStderr(), true);
        SwingUtilities.invokeLater(new Runnable() { // from class: burp.BurpExtender.1
            @Override // java.lang.Runnable
            public void run() {
                BurpExtender burpExtender = BurpExtender.this;
                BurpExtender.callbacks.addSuiteTab(BurpExtender.this);
            }
        });
        stdout.println(Util.getBanner(this.extensionName, this.version));
    }


    private void refreshCaptchaBeforeAttack() {
        try {
            stdout.println("[captcha-killer-modified] refreshing captcha before attack request");
            GUI.GetCaptchaThread thread = new GUI.GetCaptchaThread(gui.tfURL.getText(), gui.taRequest.getText(), false);
            thread.start();
            thread.join();
            if (gui.byteImg == null) {
                stdout.println("[captcha-killer-modified] refresh finished but captcha image is null");
            } else {
                stdout.println("[captcha-killer-modified] refresh finished, captcha bytes=" + gui.byteImg.length);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            stderr.println("[captcha-killer-modified] refresh interrupted: " + e.getMessage());
        }
    }

    @Override // burp.IHttpListener
    public void processHttpMessage(int toolFlag, boolean messageIsRequest, IHttpRequestResponse messageInfo) {
        if (!(toolFlag == 4 || toolFlag == 32 || toolFlag == 64)) {
            return;
        } else if (toolFlag == IBurpExtenderCallbacks.TOOL_INTRUDER || toolFlag == IBurpExtenderCallbacks.TOOL_REPEATER) {
            if (messageIsRequest) {
                byte[] request = messageInfo.getRequest();
                IRequestInfo iRequestInfo = helpers.analyzeRequest(messageInfo);
                List<String> headersList = iRequestInfo.getHeaders();
                int bodyOffset = iRequestInfo.getBodyOffset();
                byte[] body = Arrays.copyOfRange(request, bodyOffset, request.length);
                String bodyStr = new String(body);

                boolean containsCaptchaTag = bodyStr.contains("@captcha@");
                boolean containsTokenTag = bodyStr.contains("@captcha-killer-modified@");
                for (String header : headersList) {
                    if (header.contains("@captcha@")) {
                        containsCaptchaTag = true;
                    }
                    if (header.contains("@captcha-killer-modified@")) {
                        containsTokenTag = true;
                    }
                }
                boolean shouldHandle = gui.getUsebutton() && (containsCaptchaTag || containsTokenTag);

                if (shouldHandle) {
                    stdout.println("[captcha-killer-modified][AUTO] process request in tool=" + toolFlag + ", refresh->identify flow start");
                    refreshCaptchaBeforeAttack();

                    try {
                        if (gui.byteImg != null) {
                            BurpExtender.gui.cap = GUI.identifyCaptchas(gui.getInterfaceURL().getText(), gui.getTaInterfaceTmplReq().getText(), BurpExtender.gui.byteImg, gui.getCbmRuleType().getSelectedIndex(), gui.getRegular().getText());
                            BurpExtender.gui.tfcapex.setText(gui.cap);
                            stdout.println("[captcha-killer-modified] identify success, captcha=" + gui.cap);
                        } else {
                            stderr.println("[captcha-killer-modified] identify skipped: captcha image is null after refresh");
                        }
                    } catch (IOException e) {
                        stderr.println("[captcha-killer-modified] identify failed: " + e.getMessage());
                    }

                    int i = 0;
                    for (String singleheader : headersList) {
                        headersList.set(i, singleheader.replace("@captcha-killer-modified@", gui.tokenwords));
                        i++;
                    }

                    byte[] httpmsgresp = helpers.buildHttpMessage(headersList, bodyStr.replace("@captcha-killer-modified@", gui.tokenwords).replace("@captcha@", BurpExtender.gui.cap).getBytes());
                    messageInfo.setRequest(httpmsgresp);
                    Isreplace = true;
                }
            } else {
                Isreplace = false;
            }
        }
    }


    @Override // burp.ITab
    public String getTabCaption() {
        return this.extensionName;
    }

    @Override // burp.ITab
    public Component getUiComponent() {
        return gui.getComponet();
    }

    @Override // burp.IIntruderPayloadGenerator
    public boolean hasMorePayloads() {
        return true;
    }

    @Override // burp.IIntruderPayloadGenerator
    public byte[] getNextPayload(byte[] bytes) {

        GeneratePayloadSwingWorker gpsw = new GeneratePayloadSwingWorker();
        gpsw.execute();
        try {
            Object result = gpsw.get();
            return (byte[]) result;
        } catch (Exception e) {
            e.printStackTrace();
            return String.format("Erro: %s", e.getMessage()).getBytes();
        }
    }

    @Override // burp.IIntruderPayloadGenerator
    public void reset() {
    }

    @Override // burp.IIntruderPayloadGeneratorFactory
    public String getGeneratorName() {
        return this.processname;
    }

    @Override // burp.IIntruderPayloadGeneratorFactory
    public IIntruderPayloadGenerator createNewInstance(IIntruderAttack iIntruderAttack) {
        return this;
    }

    @Override
    public String getProcessorName() {
        return "captcha";
    }

    @Override
    public byte[] processPayload(byte[] currentPayload, byte[] originalPayload, byte[] baseValue) {
        return new byte[0];
    }
}

package burp;

import ui.GUI;
import utils.Util;

import javax.swing.*;

import static burp.BurpExtender.gui;

public class GeneratePayloadSwingWorker extends SwingWorker {
    @Override
    protected Object doInBackground() throws Exception {
        if(!Util.isURL(gui.getInterfaceURL().getText())){
            return "Interface URL format invalid".getBytes();
        }

        if(gui.getCaptchaURL() == null || gui.getCaptchaURL().trim().equals("")){
            return "Captcha URL is empty".getBytes();
        }
        if(gui.getCaptchaReqRaw() == null || gui.getCaptchaReqRaw().trim().equals("")){
            return "Captcha request is empty".getBytes();
        }

        int count = 0;
        try {
            BurpExtender.stdout.println("[captcha-killer-modified][INTRUDER] payload generation start: refresh->identify");
            GUI.GetCaptchaThread refreshThread = new GUI.GetCaptchaThread(gui.getCaptchaURL(), gui.getCaptchaReqRaw(), false);
            refreshThread.start();
            refreshThread.join();

            if(gui.byteImg == null){
                BurpExtender.stderr.println("[captcha-killer-modified][INTRUDER] captcha refresh failed: image is null");
                return "".getBytes();
            }

            BurpExtender.gui.cap = GUI.identifyCaptchas(
                    BurpExtender.gui.getInterfaceURL().getText(),
                    BurpExtender.gui.getTaInterfaceTmplReq().getText(),
                    BurpExtender.gui.byteImg,
                    BurpExtender.gui.getCbmRuleType().getSelectedIndex(),
                    BurpExtender.gui.getRegular().getText()
            );

            while (count < 3 ) {
                if(gui.cap == null || gui.cap.trim().equals("")){
                    Thread.sleep(1500);
                    count += 1;
                }else{
                    break;
                }
            }

            if(gui.cap == null){
                gui.cap = "";
            }
            BurpExtender.stdout.println("[captcha-killer-modified][INTRUDER] payload generation done, result=" + gui.cap);
        } catch (Exception e) {
            BurpExtender.stderr.println("[captcha-killer-modified][INTRUDER] payload generation failed: " + e.getMessage());
            if(gui.cap == null){
                gui.cap = "";
            }
        }
        return gui.cap.getBytes();
    }
}

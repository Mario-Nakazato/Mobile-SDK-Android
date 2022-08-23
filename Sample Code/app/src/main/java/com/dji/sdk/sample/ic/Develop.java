package com.dji.sdk.sample.ic;

import android.content.Context;

import com.dji.sdk.sample.R;
import com.dji.sdk.sample.internal.utils.Helper;
import com.dji.sdk.sample.internal.utils.ToastUtils;
import com.dji.sdk.sample.internal.view.BasePushDataView;

import java.text.SimpleDateFormat;

/**
 * Classe para realizar desenvolvimento do sdk mobile da dji.
 */
public class Develop extends BasePushDataView {
    boolean isStopThread;

    public Develop(Context context) {
        super(context);
    }

    /**
     * Chamado quando a view é anexada a uma janela.
     * Realize configurações ou inicializações adicionais aqui.
     */
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        try {
            isStopThread = false;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try{
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS");

                        long startTime = System.currentTimeMillis();
                        long endTime = startTime + (1 * 60 * 1000);// Tempo em milissegundos.
                        while(!isStopThread && System.currentTimeMillis() < endTime){
                            stringBuffer.delete(0, stringBuffer.length());

                            stringBuffer.append("Desenvolvimento.");
                            stringBuffer.append("\nData inicial: " + dateFormat.format(startTime));
                            stringBuffer.append("\nData final: " + dateFormat.format(endTime));
                            stringBuffer.append("\nData atual: " + Helper.timeStamp2Date("dd/MM/yyyy HH:mm:ss.SSS"));

                            showStringBufferResult();

                            Thread.sleep(100);// Simular 10 Hz.
                        }
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (Exception ignored) {
            ToastUtils.setResultToToast("Catch: " + ignored);
            stringBuffer.delete(0, stringBuffer.length());

            stringBuffer.append("Catch: ").append(ignored).append("\n");

            showStringBufferResult();
        }
    }

    /**
     * Chamado quando a view é removida da janela.
     * Realize limpezas ou encerre recursos relacionados aqui.
     */
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        try {
            isStopThread = true;
        } catch (Exception ignored) {

        }
    }

    @Override
    public int getDescription() {
        return R.string.desenvolver;
    }
}

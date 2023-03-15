package com.dji.sdk.sample.ic;

import android.content.Context;

import com.dji.sdk.sample.R;
import com.dji.sdk.sample.internal.utils.Helper;
import com.dji.sdk.sample.internal.utils.ToastUtils;
import com.dji.sdk.sample.internal.view.BasePushDataView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;

/**
 * Classe para realizar desenvolvimento do sdk mobile da dji e firebase realtime.
 */
public class DevelopFirebase extends BasePushDataView {
    boolean isStopThread;

    public DevelopFirebase(Context context) {
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
                        DatabaseReference DataRef = FirebaseDatabase.getInstance().getReference("Informação");

                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS");
                        long startDataTime = System.currentTimeMillis();
                        long endDataTime = startDataTime + (1 * 60 *1000);// Tempo em milissegundos.
                        String currentDataTime;

                        String DataKey = DataRef.push().getKey();
                        DataRef.child(DataKey).child("startDataTime").setValue(dateFormat.format(startDataTime));
                        DataRef.child(DataKey).child("endDataTime").setValue(dateFormat.format(endDataTime));
                        DatabaseReference Data = DataRef.child(DataKey).child("currentDataTime");
                        while(!isStopThread && System.currentTimeMillis() < endDataTime){
                            currentDataTime = Helper.timeStamp2Date("dd/MM/yyyy HH:mm:ss.SSS");

                            Data.push().setValue(currentDataTime);

                            stringBuffer.delete(0, stringBuffer.length());

                            stringBuffer.append("Desenvolvimento.");
                            stringBuffer.append("\nData inicial: " + dateFormat.format(startDataTime));
                            stringBuffer.append("\nData final: " + dateFormat.format(endDataTime));
                            stringBuffer.append("\nData atual: " + currentDataTime);

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

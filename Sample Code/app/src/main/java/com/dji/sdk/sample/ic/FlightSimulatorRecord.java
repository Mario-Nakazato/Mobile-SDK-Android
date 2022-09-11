package com.dji.sdk.sample.ic;

import android.content.Context;

import androidx.annotation.NonNull;

import com.dji.sdk.sample.R;
import com.dji.sdk.sample.internal.utils.Helper;
import com.dji.sdk.sample.internal.utils.ModuleVerificationUtil;
import com.dji.sdk.sample.internal.utils.ToastUtils;
import com.dji.sdk.sample.internal.view.BasePushDataView;

import java.text.DecimalFormat;

import dji.common.error.DJIError;
import dji.common.flightcontroller.simulator.InitializationData;
import dji.common.flightcontroller.simulator.SimulatorState;
import dji.common.model.LocationCoordinate2D;
import dji.common.util.CommonCallbacks;
import dji.sdk.flightcontroller.Simulator;

/**
 * Classe para realizar o registro de voo do drone no simulador.
 */
public class FlightSimulatorRecord extends BasePushDataView {
    Simulator flightSimulatorController;

    public FlightSimulatorRecord(Context context) {
        super(context);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        try {
            flightSimulatorController = ModuleVerificationUtil.getSimulator();
            if (flightSimulatorController == null) {
                stringBuffer.delete(0, stringBuffer.length());

                stringBuffer.append("Não foi possível obter o controlador de simulador de voo. Verifique se a aeronave está conectada e tente novamente.");

                showStringBufferResult();
                return;
            }

            flightSimulatorController.start(InitializationData.createInstance(new LocationCoordinate2D(23, 113), 10, 10),
                    new CommonCallbacks.CompletionCallback() {
                        @Override
                        public void onResult(DJIError djiError) {

                        }
                    });

            // Na versão da branch ic estava declarando dentro da FlightControllerState.Callback
            DecimalFormat decimalFormatter = new DecimalFormat("0.00");
            flightSimulatorController.setStateCallback(new SimulatorState.Callback() {
                LocationCoordinate2D location;

                @Override
                public void onUpdate(@NonNull SimulatorState simulatorState) {
                    location = simulatorState.getLocation();

                    stringBuffer.delete(0, stringBuffer.length());

                    stringBuffer.append("Data atual: ").append(Helper.timeStamp2Date("dd/MM/yyyy HH:mm:ss.SSS"));
                    stringBuffer.append("\nMotores ligados: ").append(simulatorState.areMotorsOn());
                    stringBuffer.append("\nEstá voando: ").append(simulatorState.isFlying());
                    stringBuffer.append("\nPosição X: ").append(decimalFormatter.format(simulatorState.getPositionX()));
                    stringBuffer.append("\nPosição Y: ").append(decimalFormatter.format(simulatorState.getPositionY()));
                    stringBuffer.append("\nPosição Z: ").append(decimalFormatter.format(simulatorState.getPositionZ()));
                    stringBuffer.append("\nRoll: ").append(decimalFormatter.format(simulatorState.getRoll()));
                    stringBuffer.append("\nPitch: ").append(decimalFormatter.format(simulatorState.getPitch()));
                    stringBuffer.append("\nYaw: ").append(decimalFormatter.format(simulatorState.getYaw()));
                    stringBuffer.append("\nLatitude: ").append(location.getLatitude());
                    stringBuffer.append("\nLongitude: ").append(location.getLongitude());

                    showStringBufferResult();
                }
            });
        } catch (Exception ignored) {
            ToastUtils.setResultToToast("Catch: " + ignored);
            stringBuffer.delete(0, stringBuffer.length());

            stringBuffer.append("Catch: ").append(ignored).append("\n");

            showStringBufferResult();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        try {
            flightSimulatorController.stop(new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {

                }
            });
            flightSimulatorController.setStateCallback(null);
        } catch (Exception ignored) {

        }
    }

    @Override
    public int getDescription() {
        return R.string.registro_simulador_voo;
    }
}

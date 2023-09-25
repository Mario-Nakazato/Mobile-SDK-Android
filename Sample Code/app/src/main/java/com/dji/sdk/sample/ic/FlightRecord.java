package com.dji.sdk.sample.ic;

import android.content.Context;

import androidx.annotation.NonNull;

import com.dji.sdk.sample.R;
import com.dji.sdk.sample.internal.utils.Helper;
import com.dji.sdk.sample.internal.utils.ModuleVerificationUtil;
import com.dji.sdk.sample.internal.utils.ToastUtils;
import com.dji.sdk.sample.internal.view.BasePushDataView;

import java.text.DecimalFormat;

import dji.common.flightcontroller.Attitude;
import dji.common.flightcontroller.FlightControllerState;
import dji.common.flightcontroller.LocationCoordinate3D;
import dji.sdk.flightcontroller.FlightController;

/**
 * Classe para realizar o registro de voo do drone.
 */
public class FlightRecord extends BasePushDataView {
    FlightController flightController;

    public FlightRecord(Context context) {
        super(context);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        try {
            flightController = ModuleVerificationUtil.getFlightController();
            if (flightController == null) {
                stringBuffer.delete(0, stringBuffer.length());

                stringBuffer.append("Não foi possível obter o controlador de voo. Verifique se a aeronave está conectada e tente novamente.");

                showStringBufferResult();
                return;
            }

            // Na versão da branch ic estava declarando dentro da FlightControllerState.Callback
            DecimalFormat decimalFormatter = new DecimalFormat("0.00");
            flightController.setStateCallback(new FlightControllerState.Callback() {
                Attitude attitude;
                LocationCoordinate3D aircraftLocation;

                @Override
                public void onUpdate(@NonNull FlightControllerState flightControllerState) {
                    attitude = flightControllerState.getAttitude();
                    aircraftLocation = flightControllerState.getAircraftLocation();

                    stringBuffer.delete(0, stringBuffer.length());

                    stringBuffer.append("Data atual: ").append(Helper.timeStamp2Date("dd/MM/yyyy HH:mm:ss.SSS"));
                    stringBuffer.append("\nMotores ligados: ").append(flightControllerState.areMotorsOn());
                    stringBuffer.append("\nAeronave voando: ").append(flightControllerState.isFlying());
                    stringBuffer.append("\nModo de voo: ").append(flightControllerState.getFlightMode().name());
                    stringBuffer.append("\nSatélites conectados: ").append(flightControllerState.getSatelliteCount());
                    stringBuffer.append("\nVelocidade X: ").append(decimalFormatter.format(flightControllerState.getVelocityX()));
                    stringBuffer.append("\nVelocidade Y: ").append(decimalFormatter.format(flightControllerState.getVelocityY()));
                    stringBuffer.append("\nVelocidade Z: ").append(decimalFormatter.format(flightControllerState.getVelocityZ()));
                    stringBuffer.append("\nRoll: ").append(decimalFormatter.format(attitude.roll));
                    stringBuffer.append("\nPitch: ").append(decimalFormatter.format(attitude.pitch));
                    stringBuffer.append("\nYaw: ").append(decimalFormatter.format(attitude.yaw));
                    stringBuffer.append("\nAltura ultrassônica: ").append(decimalFormatter.format(flightControllerState.getUltrasonicHeightInMeters()));
                    stringBuffer.append("\nDireção da aeronave: ").append(flightControllerState.getAircraftHeadDirection());
                    stringBuffer.append("\nTempo de voo (segundos): ").append(flightControllerState.getFlightTimeInSeconds());
                    stringBuffer.append("\nLatitude: ").append(aircraftLocation.getLatitude());
                    stringBuffer.append("\nLongitude: ").append(aircraftLocation.getLongitude());
                    stringBuffer.append("\nAltitude: ").append(aircraftLocation.getAltitude());
                    stringBuffer.append("\nContagem de voos: ").append(flightControllerState.getFlightCount());

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
            flightController.setStateCallback(null);
        } catch (Exception ignored) {

        }
    }

    @Override
    public int getDescription() {
        return R.string.registro_voo;
    }
}

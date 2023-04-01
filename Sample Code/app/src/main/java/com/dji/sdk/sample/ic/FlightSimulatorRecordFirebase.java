package com.dji.sdk.sample.ic;

import android.content.Context;

import androidx.annotation.NonNull;

import com.dji.sdk.sample.R;
import com.dji.sdk.sample.internal.utils.Helper;
import com.dji.sdk.sample.internal.utils.ModuleVerificationUtil;
import com.dji.sdk.sample.internal.utils.ToastUtils;
import com.dji.sdk.sample.internal.view.BasePushDataView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
public class FlightSimulatorRecordFirebase extends BasePushDataView {
    Simulator flightSimulatorController;

    public FlightSimulatorRecordFirebase(Context context) {
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
            DatabaseReference DataRef = FirebaseDatabase.getInstance().getReference("simulatorStateData");
            flightSimulatorController.setStateCallback(new SimulatorState.Callback() {
                String currentDateTime;
                boolean areMotorsOn;
                boolean isFlying;
                float positionX;
                float positionY;
                float positionZ;
                float roll;
                float pitch;
                float yaw;
                LocationCoordinate2D location;
                double latitude;
                double longitude;

                @Override
                public void onUpdate(@NonNull SimulatorState simulatorState) {
                    currentDateTime = Helper.timeStamp2Date("dd/MM/yyyy HH:mm:ss.SSS");
                    areMotorsOn = simulatorState.areMotorsOn();
                    isFlying = simulatorState.isFlying();
                    positionX = simulatorState.getPositionX();
                    positionY = simulatorState.getPositionY();
                    positionZ = simulatorState.getPositionZ();
                    roll = simulatorState.getRoll();
                    pitch = simulatorState.getPitch();
                    yaw = simulatorState.getYaw();
                    location = simulatorState.getLocation();
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();

                    if (areMotorsOn) {
                        String dataKey = DataRef.push().getKey();
                        DatabaseReference data = DataRef.child(dataKey);
                        data.child("currentDateTime").setValue(currentDateTime);
                        data.child("areMotorsOn").setValue(areMotorsOn);
                        data.child("isFlying").setValue(isFlying);
                        data.child("positionX").setValue(positionX);
                        data.child("positionY").setValue(positionY);
                        data.child("positionZ").setValue(positionZ);
                        data.child("roll").setValue(roll);
                        data.child("pitch").setValue(pitch);
                        data.child("yaw").setValue(yaw);
                        data.child("latitude").setValue(latitude);
                        data.child("longitude").setValue(longitude);
                    }

                    stringBuffer.delete(0, stringBuffer.length());

                    stringBuffer.append("Data atual: ").append(currentDateTime);
                    stringBuffer.append("\nMotores ligados: ").append(areMotorsOn);
                    stringBuffer.append("\nEstá voando: ").append(isFlying);
                    stringBuffer.append("\nPosição X: ").append(decimalFormatter.format(positionX));
                    stringBuffer.append("\nPosição Y: ").append(decimalFormatter.format(positionY));
                    stringBuffer.append("\nPosição Z: ").append(decimalFormatter.format(positionZ));
                    stringBuffer.append("\nRoll: ").append(decimalFormatter.format(roll));
                    stringBuffer.append("\nPitch: ").append(decimalFormatter.format(pitch));
                    stringBuffer.append("\nYaw: ").append(decimalFormatter.format(yaw));
                    stringBuffer.append("\nLatitude: ").append(latitude);
                    stringBuffer.append("\nLongitude: ").append(longitude);

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

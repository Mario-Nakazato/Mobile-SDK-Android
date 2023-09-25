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

import dji.common.flightcontroller.Attitude;
import dji.common.flightcontroller.FlightControllerState;
import dji.common.flightcontroller.LocationCoordinate3D;
import dji.sdk.flightcontroller.FlightController;

/**
 * Classe para realizar o registro de voo do drone.
 */
public class FlightRecordFirebase extends BasePushDataView {
    FlightController flightController;

    public FlightRecordFirebase(Context context) {
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
            DatabaseReference DataRef = FirebaseDatabase.getInstance().getReference("flightControllerStateData");
            flightController.setStateCallback(new FlightControllerState.Callback() {
                String currentDateTime = Helper.timeStamp2Date("dd/MM/yyyy HH:mm:ss.SSS");
                boolean areMotorsOn;
                boolean isFlying;
                String flightMode;
                int satelliteCount;
                float velocityX;
                float velocityY;
                float velocityZ;
                Attitude attitude;
                double roll;
                double pitch;
                double yaw;
                float ultrasonicHeight;
                String aircraftHeadDirection;
                int flightTimeInSeconds;
                LocationCoordinate3D aircraftLocation;
                double latitude;
                double longitude;
                double altitude;
                int flightCount;

                @Override
                public void onUpdate(@NonNull FlightControllerState flightControllerState) {
                    currentDateTime = Helper.timeStamp2Date("dd/MM/yyyy HH:mm:ss.SSS");
                    areMotorsOn = flightControllerState.areMotorsOn();
                    isFlying = flightControllerState.isFlying();
                    flightMode = flightControllerState.getFlightMode().name();
                    satelliteCount = flightControllerState.getSatelliteCount();
                    velocityX = flightControllerState.getVelocityX();
                    velocityY = flightControllerState.getVelocityY();
                    velocityZ = flightControllerState.getVelocityZ();
                    attitude = flightControllerState.getAttitude();
                    roll = attitude.roll;
                    pitch = attitude.pitch;
                    yaw = attitude.yaw;
                    ultrasonicHeight = flightControllerState.getUltrasonicHeightInMeters();
                    aircraftHeadDirection = String.valueOf(flightControllerState.getAircraftHeadDirection());
                    flightTimeInSeconds = flightControllerState.getFlightTimeInSeconds();
                    aircraftLocation = flightControllerState.getAircraftLocation();
                    latitude = aircraftLocation.getLatitude();
                    longitude = aircraftLocation.getLongitude();
                    altitude = aircraftLocation.getAltitude();
                    flightCount = flightControllerState.getFlightCount();

                    if (areMotorsOn) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                String DataKey = DataRef.push().getKey();
                                DatabaseReference Data = DataRef.child(DataKey);
                                Data.child("currentDateTime").setValue(currentDateTime);
                                Data.child("areMotorsOn").setValue(areMotorsOn);
                                Data.child("isFlying").setValue(isFlying);
                                Data.child("flightMode").setValue(flightMode);
                                Data.child("satelliteCount").setValue(satelliteCount);
                                Data.child("velocityX").setValue(velocityX);
                                Data.child("velocityY").setValue(velocityY);
                                Data.child("velocityZ").setValue(velocityZ);
                                Data.child("roll").setValue(roll);
                                Data.child("pitch").setValue(pitch);
                                Data.child("yaw").setValue(yaw);
                                Data.child("ultrasonicHeight").setValue(ultrasonicHeight);
                                Data.child("aircraftHeadDirection").setValue(aircraftHeadDirection);
                                Data.child("flightTimeInSeconds").setValue(flightTimeInSeconds);
                                Data.child("latitude").setValue(Double.isNaN(latitude) ? Double.toString(latitude) : latitude);
                                Data.child("longitude").setValue(Double.isNaN(longitude) ? Double.toString(longitude) : longitude);
                                Data.child("altitude").setValue(altitude);
                                Data.child("flightCount").setValue(flightCount);
                            }
                        }).start();
                    }

                    stringBuffer.delete(0, stringBuffer.length());

                    stringBuffer.append("Data atual: ").append(currentDateTime);
                    stringBuffer.append("\nMotores ligados: ").append(areMotorsOn);
                    stringBuffer.append("\nAeronave voando: ").append(isFlying);
                    stringBuffer.append("\nModo de voo: ").append(flightMode);
                    stringBuffer.append("\nSatélites conectados: ").append(satelliteCount);
                    stringBuffer.append("\nVelocidade X: ").append(decimalFormatter.format(velocityX));
                    stringBuffer.append("\nVelocidade Y: ").append(decimalFormatter.format(velocityY));
                    stringBuffer.append("\nVelocidade Z: ").append(decimalFormatter.format(velocityZ));
                    stringBuffer.append("\nRoll: ").append(decimalFormatter.format(roll));
                    stringBuffer.append("\nPitch: ").append(decimalFormatter.format(pitch));
                    stringBuffer.append("\nYaw: ").append(decimalFormatter.format(yaw));
                    stringBuffer.append("\nAltura ultrassônica: ").append(decimalFormatter.format(ultrasonicHeight));
                    stringBuffer.append("\nDireção da aeronave: ").append(aircraftHeadDirection);
                    stringBuffer.append("\nTempo de voo (segundos): ").append(flightTimeInSeconds);
                    stringBuffer.append("\nLatitude: ").append(latitude);
                    stringBuffer.append("\nLongitude: ").append(longitude);
                    stringBuffer.append("\nAltitude: ").append(altitude);
                    stringBuffer.append("\nContagem de voos: ").append(flightCount);

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

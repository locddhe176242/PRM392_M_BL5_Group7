package com.example.smartalamclock.fragment;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.smartalamclock.R;
import com.example.smartalamclock.databinding.FragmentShakeMissionBinding;
import com.example.smartalamclock.mission.MissionHost;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

public class ShakeMissionFragment extends Fragment implements MissionHost, SensorEventListener {

    private FragmentShakeMissionBinding binding;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private int requiredShakes;
    private int shakeCount = 0;
    private float shakeThreshold;
    private long shakeDelay;
    private long lastShakeTime = 0;
    private MissionHost missionHost;

    public static ShakeMissionFragment newInstance(String configJson) {
        ShakeMissionFragment fragment = new ShakeMissionFragment();
        Bundle args = new Bundle();
        args.putString("config", configJson);
        fragment.setArguments(args);
        return fragment;
    }

    public ShakeMissionFragment() {
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof MissionHost) {
            missionHost = (MissionHost) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentShakeMissionBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        shakeThreshold = getResources().getDimension(R.dimen.shake_threshold);
        shakeDelay = getResources().getInteger(R.integer.shake_delay);
        Random random = new Random();
        requiredShakes = random.nextInt(26) + 5;

        Bundle args = getArguments();
        if (args != null && args.containsKey("config")) {
            try {
                String configStr = args.getString("config");
                if (configStr != null && !configStr.isEmpty()) {
                    JSONObject config = new JSONObject(configStr);
                    if (config.has("requiredShakes")) {
                        requiredShakes = config.getInt("requiredShakes");
                        requiredShakes = Math.max(1, requiredShakes); // Ensure at least 1
                    }
                    if (config.has("shakeThreshold")) {
                        shakeThreshold = (float) config.getDouble("shakeThreshold");
                        shakeThreshold = Math.max(0.0f, shakeThreshold); // Ensure non-negative
                    }
                    if (config.has("shakeDelay")) {
                        shakeDelay = config.getLong("shakeDelay");
                        shakeDelay = Math.max(0L, shakeDelay); // Ensure non-negative
                    }
                }
            } catch (JSONException e) {
            }
        }

        // Initialize sensor
        Context context = getContext();
        if (context != null) {
            sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
            if (sensorManager != null) {
                accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            }
            if (accelerometer == null) {
                Toast.makeText(context, "Accelerometer not available!", Toast.LENGTH_LONG).show();
                if (missionHost != null) {
                    missionHost.onMissionFailed("Device lacks accelerometer");
                }
                return;
            }
        }

        binding.tvShakeCount.setText("0 / " + requiredShakes);
        binding.pbShakeProgress.setMax(requiredShakes);
        binding.pbShakeProgress.setProgress(0);
        binding.tvShakeHint.setText(getString(R.string.shake_to_dismiss));

        updateProgress();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (accelerometer != null && sensorManager != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            double acceleration = Math.sqrt(x * x + y * y + z * z) - SensorManager.GRAVITY_EARTH;

            long currentTime = System.currentTimeMillis();

            if (acceleration > shakeThreshold && (currentTime - lastShakeTime) > shakeDelay) {
                lastShakeTime = currentTime;
                shakeCount++;

                binding.tvShakeCount.setText(shakeCount + " / " + requiredShakes);
                binding.pbShakeProgress.setProgress(shakeCount);
                updateProgress();

                if (shakeCount >= requiredShakes) {
                    binding.tvShakeHint.setText(getString(R.string.mission_completed));
                    sensorManager.unregisterListener(this);
                    if (missionHost != null) {
                        missionHost.onMissionCompleted();
                    }
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // No action needed
    }

    private void updateProgress() {
        if (missionHost != null) {
            int progress = (int) ((shakeCount / (float) requiredShakes) * 100);
            missionHost.onMissionProgress(progress);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onMissionProgress(int progress) {
    }

    @Override
    public void onMissionCompleted() {
    }

    @Override
    public void onMissionFailed(String reason) {
    }
}
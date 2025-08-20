package com.example.smartalamclock.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.smartalamclock.R;
import com.example.smartalamclock.databinding.FragmentMathMissionBinding;
import com.example.smartalamclock.mission.MissionHost;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

public class MathMissionFragment extends Fragment implements MissionHost {

    private FragmentMathMissionBinding binding;
    private int correctAnswer;
    private int correctAnswersCount = 0;
    private int requiredAnswers = 3; // Default number of correct answers needed
    private MissionHost missionHost;
    private Random random = new Random();

    public static MathMissionFragment newInstance(String configJson) {
        MathMissionFragment fragment = new MathMissionFragment();
        Bundle args = new Bundle();
        args.putString("config", configJson);
        fragment.setArguments(args);
        return fragment;
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
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentMathMissionBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Parse configJson for requiredAnswers
        Bundle args = getArguments();
        if (args != null && args.containsKey("config")) {
            try {
                String configStr = args.getString("config");
                if (configStr != null && !configStr.isEmpty()) {
                    JSONObject config = new JSONObject(configStr);
                    if (config.has("requiredAnswers")) {
                        requiredAnswers = config.getInt("requiredAnswers");
                        // Ensure requiredAnswers is at least 1
                        requiredAnswers = Math.max(1, requiredAnswers);
                    }
                }
            } catch (JSONException e) {
                // Use default requiredAnswers if JSON parsing fails
            }
        }

        generateQuestion();
        setupActions();
        updateProgress();
    }

    private void setupActions() {
        binding.btnDone.setOnClickListener(v -> {
            Context context = getContext();
            if (context == null) return;

            String answerStr = binding.edtAnswer.getText().toString().trim();
            if (answerStr.isEmpty()) {
                Toast.makeText(context, "Please enter your answer!", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                int userAnswer = Integer.parseInt(answerStr);
                if (userAnswer == correctAnswer) {
                    correctAnswersCount++;
                    updateProgress();
                    if (correctAnswersCount >= requiredAnswers) {
                        Toast.makeText(context, "Correct! Mission Completed 🎉", Toast.LENGTH_SHORT).show();
                        if (missionHost != null) {
                            missionHost.onMissionCompleted();
                        }
                    } else {
                        Toast.makeText(context, "Correct! Next question.", Toast.LENGTH_SHORT).show();
                        binding.edtAnswer.setText("");
                        generateQuestion();
                    }
                } else {
                    Toast.makeText(context, "Wrong! Try again.", Toast.LENGTH_SHORT).show();
                    binding.edtAnswer.setText("");
                    if (missionHost != null) {
                        missionHost.onMissionFailed("Incorrect answer");
                    }
                }
            } catch (NumberFormatException e) {
                Toast.makeText(context, "Invalid number!", Toast.LENGTH_SHORT).show();
                binding.edtAnswer.setText("");
            }
        });
    }

    private void generateQuestion() {
        int a, b;
        char operator;

        int op = random.nextInt(4);
        switch (op) {
            case 0: // Addition
                a = random.nextInt(50) + 1;
                b = random.nextInt(50) + 1;
                correctAnswer = a + b;
                operator = '+';
                break;
            case 1: // Subtraction
                a = random.nextInt(50) + 1;
                b = random.nextInt(a) + 1;
                correctAnswer = a - b;
                operator = '-';
                break;
            case 2: // Multiplication
                a = random.nextInt(12) + 1;
                b = random.nextInt(12) + 1;
                correctAnswer = a * b;
                operator = '×';
                break;
            case 3: // Division
                b = random.nextInt(12) + 1;
                correctAnswer = random.nextInt(12) + 1;
                a = correctAnswer * b;
                operator = '/';
                break;
            default:
                a = 1;
                b = 1;
                correctAnswer = a + b;
                operator = '+';
        }

        binding.tvQuestion.setText(a + " " + operator + " " + b + " = ?");
    }

    private void updateProgress() {
        if (missionHost != null) {
            int progress = (int) ((correctAnswersCount / (float) requiredAnswers) * 100);
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
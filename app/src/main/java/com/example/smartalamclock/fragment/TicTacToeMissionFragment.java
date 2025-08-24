package com.example.smartalamclock.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.smartalamclock.R;
import com.example.smartalamclock.databinding.FragmentTicTacToeMissionBinding;
import com.example.smartalamclock.mission.MissionHost;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TicTacToeMissionFragment extends Fragment implements MissionHost {

    private FragmentTicTacToeMissionBinding binding;
    private Button[][] board = new Button[3][3];
    private boolean playerTurn = true; // true = player, false = computer
    private Random random = new Random();
    private float randomMoveProbability = 0.3f;
    private int moveCount = 0;
    private MissionHost missionHost;

    public static TicTacToeMissionFragment newInstance(String configJson) {
        TicTacToeMissionFragment fragment = new TicTacToeMissionFragment();
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
        binding = FragmentTicTacToeMissionBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();
        if (args != null && args.containsKey("config")) {
            try {
                String configStr = args.getString("config");
                if (configStr != null && !configStr.isEmpty()) {
                    JSONObject config = new JSONObject(configStr);
                    if (config.has("randomMoveProbability")) {
                        randomMoveProbability = (float) config.getDouble("randomMoveProbability");
                        // Ensure probability is between 0 and 1
                        randomMoveProbability = Math.max(0.0f, Math.min(1.0f, randomMoveProbability));
                    }
                }
            } catch (JSONException e) {
            }
        }

        setupBoard();

        binding.btnResetTicTacToe.setOnClickListener(v -> resetGame());
        updateProgress();
    }

    private void setupBoard() {
        GridLayout gridLayout = binding.gridTicTacToe;
        gridLayout.removeAllViews();

        int size = getResources().getDimensionPixelSize(R.dimen.tic_tac_toe_cell_size);
        int textSize = getResources().getDimensionPixelSize(R.dimen.tic_tac_toe_text_size);

        Context context = getContext();
        if (context == null) return;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                Button button = new Button(context);
                button.setText("");
                button.setTextSize(pxToSp(textSize));
                button.setAllCaps(false);

                button.setBackgroundResource(R.drawable.bg_ttt_cell);
                button.setTextColor(getResources().getColor(android.R.color.black));

                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.rowSpec = GridLayout.spec(i, 1f);
                params.columnSpec = GridLayout.spec(j, 1f);
                params.width = size;
                params.height = size;
                params.setMargins(4, 4, 4, 4);
                button.setLayoutParams(params);

                int finalI = i;
                int finalJ = j;
                button.setOnClickListener(v -> onCellClicked(finalI, finalJ));

                gridLayout.addView(button);
                board[i][j] = button;
            }
        }
    }

    private void onCellClicked(int i, int j) {
        if (!playerTurn || !board[i][j].getText().toString().isEmpty()) return;

        board[i][j].setText("X");
        board[i][j].setTextColor(getResources().getColor(R.color.ttt_x));
        moveCount++;
        updateProgress();

        if (checkWin("X")) {
            binding.tvGameStatus.setText(getString(R.string.you_win));
            disableBoard();
            if (missionHost != null) {
                missionHost.onMissionCompleted();
            }
            return;
        }

        if (isBoardFull()) {
            binding.tvGameStatus.setText(getString(R.string.draw));
            if (missionHost != null) {
                missionHost.onMissionFailed("Game ended in a draw");
            }
            return;
        }

        playerTurn = false;
        binding.tvGameStatus.setText(getString(R.string.computer_turn));

        int[] move = minimaxAI();
        board[move[0]][move[1]].setText("O");
        moveCount++;
        updateProgress();

        if (checkWin("O")) {
            binding.tvGameStatus.setText(getString(R.string.computer_wins));
            disableBoard();
            if (missionHost != null) {
                missionHost.onMissionFailed("Computer won the game");
            }
        } else if (isBoardFull()) {
            binding.tvGameStatus.setText(getString(R.string.draw));
            if (missionHost != null) {
                missionHost.onMissionFailed("Game ended in a draw");
            }
        } else {
            playerTurn = true;
            binding.tvGameStatus.setText(getString(R.string.your_turn));
        }
    }

    private int[] minimaxAI() {
        if (random.nextFloat() < randomMoveProbability) {
            int[] randomMove = randomMove();
            if (randomMove != null) {
                return randomMove;
            }
        }

        int bestScore = Integer.MIN_VALUE;
        int[] bestMove = new int[]{-1, -1};

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j].getText().toString().isEmpty()) {
                    board[i][j].setText("O");
                    int score = minimax(false, Integer.MIN_VALUE, Integer.MAX_VALUE);
                    board[i][j].setText("");
                    if (score > bestScore) {
                        bestScore = score;
                        bestMove[0] = i;
                        bestMove[1] = j;
                    }
                }
            }
        }
        return bestMove[0] != -1 ? bestMove : randomMove();
    }

    private int[] randomMove() {
        List<int[]> emptyCells = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j].getText().toString().isEmpty()) {
                    emptyCells.add(new int[]{i, j});
                }
            }
        }
        return emptyCells.isEmpty() ? null : emptyCells.get(random.nextInt(emptyCells.size()));
    }

    private int minimax(boolean isMaximizing, int alpha, int beta) {
        if (checkWin("X")) return -10;
        if (checkWin("O")) return 10;
        if (isBoardFull()) return 0;

        if (isMaximizing) {
            int bestScore = Integer.MIN_VALUE;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (board[i][j].getText().toString().isEmpty()) {
                        board[i][j].setText("O");
                        int score = minimax(false, alpha, beta);
                        board[i][j].setText("");
                        bestScore = Math.max(score, bestScore);
                        alpha = Math.max(alpha, bestScore);
                        if (beta <= alpha) break;
                    }
                }
            }
            return bestScore;
        } else {
            int bestScore = Integer.MAX_VALUE;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (board[i][j].getText().toString().isEmpty()) {
                        board[i][j].setText("X");
                        int score = minimax(true, alpha, beta);
                        board[i][j].setText("");
                        bestScore = Math.min(score, bestScore);
                        beta = Math.min(beta, bestScore);
                        if (beta <= alpha) break;
                    }
                }
            }
            return bestScore;
        }
    }

    private boolean checkWin(String symbol) {
        for (int i = 0; i < 3; i++) {
            if (board[i][0].getText().toString().equals(symbol)
                    && board[i][1].getText().toString().equals(symbol)
                    && board[i][2].getText().toString().equals(symbol)) return true;

            if (board[0][i].getText().toString().equals(symbol)
                    && board[1][i].getText().toString().equals(symbol)
                    && board[2][i].getText().toString().equals(symbol)) return true;
        }
        return board[0][0].getText().toString().equals(symbol)
                && board[1][1].getText().toString().equals(symbol)
                && board[2][2].getText().toString().equals(symbol)
                || board[0][2].getText().toString().equals(symbol)
                && board[1][1].getText().toString().equals(symbol)
                && board[2][0].getText().toString().equals(symbol);
    }

    private boolean isBoardFull() {
        for (Button[] row : board) {
            for (Button cell : row) {
                if (cell.getText().toString().isEmpty()) return false;
            }
        }
        return true;
    }

    private void disableBoard() {
        for (Button[] row : board) {
            for (Button cell : row) {
                if (cell != null) {
                    cell.setEnabled(false);
                }
            }
        }
    }

    private void resetGame() {
        setupBoard();
        playerTurn = true;
        moveCount = 0;
        updateProgress();
        binding.tvGameStatus.setText(getString(R.string.your_turn));
    }

    private void updateProgress() {
        if (missionHost != null) {
            int progress = (int) ((moveCount / 9.0f) * 100);
            missionHost.onMissionProgress(progress);
        }
    }

    private float pxToSp(float px) {
        return px / getResources().getDisplayMetrics().scaledDensity;
    }

    @Override
    public void onMissionProgress(int progress) {}

    @Override
    public void onMissionCompleted() {}

    @Override
    public void onMissionFailed(String reason) {}
}
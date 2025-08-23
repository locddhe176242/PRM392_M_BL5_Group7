package com.example.smartalamclock.mission;

import androidx.fragment.app.Fragment;

import com.example.smartalamclock.fragment.MathMissionFragment;
import com.example.smartalamclock.fragment.ShakeMissionFragment;
import com.example.smartalamclock.fragment.TicTacToeMissionFragment;

public final class MissionRunner {
    public static Fragment create(MissionType type, String configJson) {
        switch (type) {
            case MATH:
                return new MathMissionFragment().newInstance(configJson);
            case SHAKE:
                return new ShakeMissionFragment().newInstance(configJson);
            case TIC_TAC_TOE:
                return TicTacToeMissionFragment.newInstance(configJson);
            default:
                return MathMissionFragment.newInstance(configJson);
        }
    }
}

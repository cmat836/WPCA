package com.cmat.wpca.fragments;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.cmat.wpca.R;
import com.cmat.wpca.data.DataStore;
import com.cmat.wpca.data.IGameEvent;
import com.cmat.wpca.data.PlayerEntry;
import com.cmat.wpca.data.RulesetEntry;
import com.cmat.wpca.data.ShotEvent;
import com.cmat.wpca.data.TeamEntry;

import java.util.ArrayList;

public class GameRecordFragment extends Fragment {
    DataStore<PlayerEntry> Players = new DataStore<>("players", PlayerEntry.class);
    DataStore<TeamEntry> Teams = new DataStore<>("teams", TeamEntry.class);
    DataStore<RulesetEntry> Rulesets = new DataStore<>("rulesets", RulesetEntry.class);

    ArrayList<IGameEvent> Game = new ArrayList<>();

    PopupWindow eventMenu = new PopupWindow(null, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, true);
    PopupWindow shotMenu = new PopupWindow(null, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, true);
    PopupWindow extraMenu = new PopupWindow(null, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, true);
    PopupWindow substitutionMenu = new PopupWindow(null, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, true);
    PopupWindow misconductMenu = new PopupWindow(null, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, true);
    PopupWindow swimoffMenu = new PopupWindow(null, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, true);

    String rulesetName;
    String teamName;
    String[] selectedPlayers = new String[7];

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.game_record, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Players.load(getContext());
        Teams.load(getContext());
        Rulesets.load(getContext());

        eventMenu.setAnimationStyle(android.R.style.Animation);
        eventMenu.setBackgroundDrawable(new ColorDrawable((Color.WHITE)));
        eventMenu.setElevation(20);
        shotMenu.setAnimationStyle(android.R.style.Animation);
        shotMenu.setBackgroundDrawable(new ColorDrawable((Color.WHITE)));
        shotMenu.setElevation(20);
        extraMenu.setAnimationStyle(android.R.style.Animation);
        extraMenu.setBackgroundDrawable(new ColorDrawable((Color.WHITE)));
        extraMenu.setElevation(20);
        substitutionMenu.setAnimationStyle(android.R.style.Animation);
        substitutionMenu.setBackgroundDrawable(new ColorDrawable((Color.WHITE)));
        substitutionMenu.setElevation(20);
        misconductMenu.setAnimationStyle(android.R.style.Animation);
        misconductMenu.setBackgroundDrawable(new ColorDrawable((Color.WHITE)));
        misconductMenu.setElevation(20);
        swimoffMenu.setAnimationStyle(android.R.style.Animation);
        swimoffMenu.setBackgroundDrawable(new ColorDrawable((Color.WHITE)));
        swimoffMenu.setElevation(20);

        rulesetName = getArguments().getString("rulesetName");
        selectedPlayers = getArguments().getStringArray("selectedPlayers");
        teamName = getArguments().getString("teamName");

        View p1 = view.findViewById(R.id.player1_button);
        p1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return playerButton_Pressed(0, v, event);
            }
        });
        ((Button)p1).setText(Players.getEntry(selectedPlayers[0]).getName());

        View p2 = view.findViewById(R.id.player2_button);
        p2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return playerButton_Pressed(1, v, event);
            }
        });
        ((Button)p2).setText(Players.getEntry(selectedPlayers[1]).getName());

        View p3 = view.findViewById(R.id.player3_button);
        p3.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return playerButton_Pressed(2, v, event);
            }
        });
        ((Button)p3).setText(Players.getEntry(selectedPlayers[2]).getName());

        View p4 = view.findViewById(R.id.player4_button);
        p4.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return playerButton_Pressed(3, v, event);
            }
        });
        ((Button)p4).setText(Players.getEntry(selectedPlayers[3]).getName());

        View p5 = view.findViewById(R.id.player5_button);
        p5.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return playerButton_Pressed(4, v, event);
            }
        });
        ((Button)p5).setText(Players.getEntry(selectedPlayers[4]).getName());

        View p6 = view.findViewById(R.id.player6_button);
        p6.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return playerButton_Pressed(5, v, event);
            }
        });
        ((Button)p6).setText(Players.getEntry(selectedPlayers[5]).getName());

        View p7 = view.findViewById(R.id.player7_button);
        p7.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return playerButton_Pressed(6, v, event);
            }
        });
        ((Button)p7).setText(Players.getEntry(selectedPlayers[6]).getName());
    }

    private boolean playerButton_Pressed(int buttonNumber, View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View content = this.getLayoutInflater().inflate(R.layout.popupwindow_event_menu, null);
            eventMenu.setContentView(content);
            eventMenu.showAtLocation(v, Gravity.CENTER, 0, 0);
            v.performClick();
            return true;
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (checkEventWithinView(eventMenu.getContentView().findViewById(R.id.shot_button), event)) {
                eventMenu_shotButton_Pressed(buttonNumber, v);
                return true;
            }
            if (checkEventWithinView(eventMenu.getContentView().findViewById(R.id.blank_button), event)) {
                eventMenu.dismiss();
                return true;
            }
            if (checkEventWithinView(eventMenu.getContentView().findViewById(R.id.turnover_button), event)) {
                eventMenu_turnoverButton_Pressed(buttonNumber, v);
                return true;
            }
            if (checkEventWithinView(eventMenu.getContentView().findViewById(R.id.exclusion_button), event)) {
                eventMenu_exclusionButton_Pressed(buttonNumber, v);
                return true;
            }
            if (checkEventWithinView(eventMenu.getContentView().findViewById(R.id.block_button), event)) {
                eventMenu_blockButton_Pressed(buttonNumber, v);
                return true;
            }
            if (checkEventWithinView(eventMenu.getContentView().findViewById(R.id.badpass_button), event)) {
                eventMenu_badpassButton_Pressed(buttonNumber, v);
                return true;
            }
            if (checkEventWithinView(eventMenu.getContentView().findViewById(R.id.extra_button), event)) {
                eventMenu_extraButton_Pressed(buttonNumber, v);
                return true;
            }
        }
        return false;

    }

    public boolean checkEventWithinView(View v, MotionEvent event) {
        int x = (int) event.getRawX();
        int y = (int) event.getRawY();
        int[] loc = new int[2];
        v.getLocationOnScreen(loc);
        int vx = loc[0];
        int vy = loc[1];
        int w = v.getWidth();
        int h = v.getHeight();
        if (x > vx && x < vx + w && y > vy && y < vy + h) {
            return true;
        }
        return false;
    }

    public void AddGameEvent(IGameEvent event) {
        Game.add(event);
        refreshReplayTable();
    }

    private void refreshReplayTable() {
        ((TextView)getView().findViewById(R.id.replay_playernumber_textview)).setText(Game.get(Game.size() - 1).getPlayer().getNumber());
        ((TextView)getView().findViewById(R.id.replay_event_textview)).setText(Game.get(Game.size() - 1).getEventText());
        ((TextView)getView().findViewById(R.id.replay_timestamp_textview)).setText(String.valueOf(Game.get(Game.size() - 1).getTime()));
    }

    private void eventMenu_shotButton_Pressed(int buttonNumber, View v) {
        eventMenu.dismiss();
        View content = this.getLayoutInflater().inflate(R.layout.popupwindow_shot_menu, null);
        shotMenu.setContentView(content);
        shotMenu.showAtLocation(v, Gravity.CENTER, 0, 0);

        content.findViewById(R.id.goal_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddGameEvent(new ShotEvent(ShotEvent.ShotType.GOAL, Players.getEntry(selectedPlayers[buttonNumber])));
                shotMenu.dismiss();
            }
        });
        content.findViewById(R.id.blocked_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddGameEvent(new ShotEvent(ShotEvent.ShotType.BLOCKED, Players.getEntry(selectedPlayers[buttonNumber])));
                shotMenu.dismiss();
            }
        });
        content.findViewById(R.id.miss_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddGameEvent(new ShotEvent(ShotEvent.ShotType.MISS, Players.getEntry(selectedPlayers[buttonNumber])));
                shotMenu.dismiss();
            }
        });
    }

    private void eventMenu_turnoverButton_Pressed(int buttonNumber, View v) {
        eventMenu.dismiss();
    }

    private void eventMenu_exclusionButton_Pressed(int buttonNumber, View v) {
        eventMenu.dismiss();
    }
    private void eventMenu_blockButton_Pressed(int buttonNumber, View v) {
        eventMenu.dismiss();
    }

    private void eventMenu_badpassButton_Pressed(int buttonNumber, View v) {
        eventMenu.dismiss();
    }

    private void eventMenu_extraButton_Pressed(int buttonNumber, View v) {
        eventMenu.dismiss();
        View content = this.getLayoutInflater().inflate(R.layout.popupwindow_extra_menu, null);
        extraMenu.setContentView(content);
        extraMenu.showAtLocation(v, Gravity.CENTER, 0, 0);

        content.findViewById(R.id.sub_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                extraMenu.dismiss();
                extraMenu_subButton_Pressed(buttonNumber, v);
            }
        });
        content.findViewById(R.id.card_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                extraMenu.dismiss();
                extraMenu_cardButton_Pressed(buttonNumber, v);
            }
        });
        content.findViewById(R.id.swimoff_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                extraMenu.dismiss();
                extraMenu_swimoffButton_Pressed(buttonNumber, v);
            }
        });
        content.findViewById(R.id.penalty_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                extraMenu.dismiss();
                extraMenu_penaltyButton_Pressed(buttonNumber, v);
            }
        });
    }

    private void extraMenu_subButton_Pressed(int buttonNumber, View v) {
        View content = this.getLayoutInflater().inflate(R.layout.popupwindow_substitution, null);
        substitutionMenu.setContentView(content);
        substitutionMenu.showAtLocation(v, Gravity.CENTER, 0, 0);
    }

    private void extraMenu_cardButton_Pressed(int buttonNumber, View v) {
        View content = this.getLayoutInflater().inflate(R.layout.popupwindow_misconduct_menu, null);
        misconductMenu.setContentView(content);
        misconductMenu.showAtLocation(v, Gravity.CENTER, 0, 0);
    }

    private void extraMenu_swimoffButton_Pressed(int buttonNumber, View v) {
        View content = this.getLayoutInflater().inflate(R.layout.popupwindow_swimoff_menu, null);
        swimoffMenu.setContentView(content);
        swimoffMenu.showAtLocation(v, Gravity.CENTER, 0, 0);
    }

    private void extraMenu_penaltyButton_Pressed(int buttonNumber, View v) {
    }

}
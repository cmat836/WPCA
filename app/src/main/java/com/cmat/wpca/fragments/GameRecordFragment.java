package com.cmat.wpca.fragments;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.core.util.Consumer;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cmat.wpca.R;
import com.cmat.wpca.data.DataStore;
import com.cmat.wpca.data.Game;
import com.cmat.wpca.data.TaskTimer;
import com.cmat.wpca.data.event.GenericPlayerEvent;
import com.cmat.wpca.data.event.IGameEvent;
import com.cmat.wpca.data.event.MisconductEvent;
import com.cmat.wpca.data.event.NonPlayerEvent;
import com.cmat.wpca.data.entry.PlayerEntry;
import com.cmat.wpca.data.entry.RulesetEntry;
import com.cmat.wpca.data.event.ShotEvent;
import com.cmat.wpca.data.event.SubEvent;
import com.cmat.wpca.data.event.SwimoffEvent;
import com.cmat.wpca.data.entry.TeamEntry;

import java.util.ArrayList;
import java.util.Arrays;

/*
-//opp goal//
-review
-//uneven -on/off//
-//scoreboard//
-//noteable//
-//Timer stored in game//
-//Timer starts on swimoff//
-//Timer pauses on quarter//
-//Displayed timer during timeouts displays relative to event start//
-uneven and timeout button, flash every 5 seconds
-//time show when uneven or timeout//
-//possession toggle//
-brutality, misconduct and exclusion, trigger uneven
-track exclusions on players
-uneven triggerd = self, manual triggerd = them
-Turnover submenu
-automatically turn's over ball

quarter button
-//trigger quarter mode//
-mass sub
-//pause time//
-//swimoff starts quarter//
-after final quarter close button

-Goalie always bottom

-BIG UNDO ON LONG PRESS
    -Set up gameevents to have apply and undo functions in interface
    -Functions recieve a Game instance and need to use only that to perform actions
    -Timers must be moved into Game and abstraction provided
 */


public class GameRecordFragment extends Fragment {
    DataStore<PlayerEntry> Players = new DataStore<>("players", PlayerEntry.class);
    DataStore<TeamEntry> Teams = new DataStore<>("teams", TeamEntry.class);
    DataStore<RulesetEntry> Rulesets = new DataStore<>("rulesets", RulesetEntry.class);

    Game game = new Game();

    TaskTimer gameTime = new TaskTimer();
    TaskTimer timeoutTime = new TaskTimer();

    PopupWindow eventMenu = new PopupWindow(null, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, true);
    PopupWindow shotMenu = new PopupWindow(null, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, true);
    PopupWindow extraMenu = new PopupWindow(null, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, true);
    PopupWindow substitutionMenu = new PopupWindow(null, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, true);
    PopupWindow misconductMenu = new PopupWindow(null, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, true);
    PopupWindow swimoffMenu = new PopupWindow(null, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, true);

    String rulesetName;
    String teamName;
    ArrayList<String> selectedPlayers = new ArrayList<>();
    ArrayList<Button> playerButtons = new ArrayList<>();

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
        selectedPlayers = new ArrayList<>(Arrays.asList(getArguments().getStringArray("selectedPlayers")));
        teamName = getArguments().getString("teamName");

        Teams.setSelected(teamName);

        game.quarterbreak = true;
        game.paused = true;

        refreshScoreboard();

        ((ToggleButton)getView().findViewById(R.id.possesion_toggle)).setOnCheckedChangeListener(this::possessionButton_Toggled);

        getButton(R.id.oppgoal_button).setOnClickListener(this::oppGoalButton_Pressed);
        getButton(R.id.notableplay_button).setOnClickListener(this::notablePlayButton_Pressed);
        getButton(R.id.uneven_button).setOnClickListener(this::unevenButton_Pressed);
        getButton(R.id.quarter_button).setOnLongClickListener(this::quarterButton_Pressed);
        getButton(R.id.timeout_button).setOnClickListener(this::timeoutButton_Pressed);
        getButton(R.id.review_button).setOnClickListener(this::reviewButton_Pressed);

        getView().findViewById(R.id.replay_container).setOnLongClickListener(this::replayContainer_LongPressed);

        playerButtons.add(getButton(R.id.player1_button));
        playerButtons.add(getButton(R.id.player2_button));
        playerButtons.add(getButton(R.id.player3_button));
        playerButtons.add(getButton(R.id.player4_button));
        playerButtons.add(getButton(R.id.player5_button));
        playerButtons.add(getButton(R.id.player6_button));
        playerButtons.add(getButton(R.id.player7_button));

        for (int i = 0; i < playerButtons.size(); i++) {
            int finalI = i;
            playerButtons.get(i).setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return playerButton_Pressed(finalI, v, event);
                }
            });
            playerButtons.get(i).setText(Players.getEntry(selectedPlayers.get(i)).getName());
        }


    }

    private boolean replayContainer_LongPressed(View view) {
        game.GameEvents.remove(getMostRecentEvent());
        refreshReplayTable();
        return false;
    }

    private void reviewButton_Pressed(View view) {

    }

    private void timeoutButton_Pressed(View view) {
        Button timeoutButton = getButton(R.id.timeout_button);
        if (!game.timeout && !game.quarterbreak) {
            game.paused = true;
            game.timeout = true;
            AddGameEvent(new NonPlayerEvent(NonPlayerEvent.NonPlayerEventType.TIMEOUTSTART));
            gameTime.pause();
            timeoutTime.start();
            timeoutButton.setBackgroundColor(Color.RED);
            timeoutTime.addTask("timeouttimer", false, new Consumer<Long>() {
                @Override
                public void accept(Long aLong) {
                    timeoutButton.setText(timeoutTime.getString());
                }
            }, 200);
        } else {
            game.timeout = false;
            AddGameEvent(new NonPlayerEvent(NonPlayerEvent.NonPlayerEventType.TIMEOUTEND));
            if (!game.quarterbreak) {
                game.paused = false;
                gameTime.resume();
            }
            timeoutTime.stop();
            timeoutButton.setBackgroundColor(Color.BLUE);
            timeoutButton.setText("timeout");
            timeoutTime.removeTask("timeouttimer");
        }
    }

    private void possessionButton_Toggled(CompoundButton compoundButton, boolean b) {
        if (b) {
            AddGameEvent(new NonPlayerEvent(NonPlayerEvent.NonPlayerEventType.POSSESSIONCHANGEOPP));
            game.homePossession = false;
        } else {
            AddGameEvent(new NonPlayerEvent(NonPlayerEvent.NonPlayerEventType.POSSESSIONCHANGEHOME));
            game.homePossession = true;
        }
    }

    private boolean quarterButton_Pressed(View view) {
        if (!game.quarterbreak) {
            game.paused = true;
            game.quarterbreak = true;
            Button qb = getButton(R.id.quarter_button);
            qb.setText("Between Q's");
            qb.setBackgroundColor(Color.RED);
            gameTime.pause();
            // Other quarter time things
        }
        return false;
    }


    private void refreshPlayerButtons() {
        for (int i = 0; i < playerButtons.size(); i++) {
            playerButtons.get(i).setText(Players.getEntry(selectedPlayers.get(i)).getName());
        }
    }

    public void refreshScoreboard() {
        ((TextView)getView().findViewById(R.id.score_textview)).setText(game.homeGoals + ":" + game.oppGoals);
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
        event.setTime(gameTime.getMillis());
        game.GameEvents.add(event);
        refreshReplayTable();
    }

    private IGameEvent getMostRecentEvent() {
        return game.GameEvents.get(game.GameEvents.size() - 1);
    }

    private void refreshReplayTable() {
        ((TextView)getView().findViewById(R.id.replay_playernumber_textview)).setText(getMostRecentEvent().getPlayer().getNumber());
        ((TextView)getView().findViewById(R.id.replay_event_textview)).setText(getMostRecentEvent().getEventText());
        ((TextView)getView().findViewById(R.id.replay_timestamp_textview)).setText(TaskTimer.millisToString(getMostRecentEvent().getTime()));
    }

    private void oppGoalButton_Pressed(View view) {
        game.oppGoals++;
        AddGameEvent(new NonPlayerEvent(NonPlayerEvent.NonPlayerEventType.OPPGOAL));
        refreshScoreboard();
    }
    
    private void unevenButton_Pressed(View view) {
        Button unevenButton = getButton(R.id.uneven_button);
        if (!game.unevenOpp) {
            game.unevenOpp = true;
            AddGameEvent(new NonPlayerEvent(NonPlayerEvent.NonPlayerEventType.UNEVENOPPSTART));
            unevenButton.setBackgroundColor(Color.RED);
            gameTime.addTask("unevenopp", true, new Consumer<Long>() {
                @Override
                public void accept(Long aLong) {
                    unevenButton.setText("m:" + aLong);
                }
            }, 200);
        } else {
            game.unevenOpp = false;
            AddGameEvent(new NonPlayerEvent(NonPlayerEvent.NonPlayerEventType.UNEVENOPPEND));
            unevenButton.setBackgroundColor(Color.BLUE);
            gameTime.removeTask("unevenopp");
            unevenButton.setText("uneven");
        }
    }

    private void notablePlayButton_Pressed(View view) {
        getMostRecentEvent().setNotable(true);
    }

    private void eventMenu_shotButton_Pressed(int buttonNumber, View v) {
        eventMenu.dismiss();
        View content = this.getLayoutInflater().inflate(R.layout.popupwindow_shot_menu, null);
        shotMenu.setContentView(content);
        shotMenu.showAtLocation(v, Gravity.CENTER, 0, 0);

        content.findViewById(R.id.goal_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddGameEvent(new ShotEvent(ShotEvent.ShotType.GOAL, Players.getEntry(selectedPlayers.get(buttonNumber))));
                game.homeGoals++;
                refreshScoreboard();
                shotMenu.dismiss();
            }
        });
        content.findViewById(R.id.blocked_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddGameEvent(new ShotEvent(ShotEvent.ShotType.BLOCKED, Players.getEntry(selectedPlayers.get(buttonNumber))));
                shotMenu.dismiss();
            }
        });
        content.findViewById(R.id.miss_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddGameEvent(new ShotEvent(ShotEvent.ShotType.MISS, Players.getEntry(selectedPlayers.get(buttonNumber))));
                shotMenu.dismiss();
            }
        });
    }

    private void eventMenu_turnoverButton_Pressed(int buttonNumber, View v) {
        AddGameEvent(new GenericPlayerEvent(GenericPlayerEvent.GenericEventType.TURNOVER, Players.getEntry(selectedPlayers.get(buttonNumber))));
        eventMenu.dismiss();
    }

    private void eventMenu_exclusionButton_Pressed(int buttonNumber, View v) {
        AddGameEvent(new GenericPlayerEvent(GenericPlayerEvent.GenericEventType.EXCLUSION, Players.getEntry(selectedPlayers.get(buttonNumber))));
        game.unevenHome = true;
        int finalButtonNumber = buttonNumber;
        playerButtons.get(buttonNumber).setBackgroundColor(Color.RED);
        gameTime.addTask("exclusiontimer" + buttonNumber, true, new Consumer<Long>() {
            @Override
            public void accept(Long aLong) {
                playerButtons.get(finalButtonNumber).setText(Players.getEntry(selectedPlayers.get(finalButtonNumber)).getNumber() + " " + TaskTimer.millisToString(aLong));
            }
        }, 200, 20000, new Consumer<Long>() {
            @Override
            public void accept(Long aLong) {
                playerButtons.get(finalButtonNumber).setText(Players.getEntry(selectedPlayers.get(finalButtonNumber)).getName());
                playerButtons.get(finalButtonNumber).setBackgroundColor(Color.GRAY);
            }
        });
        playerButtons.get(buttonNumber).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
        playerButtons.get(buttonNumber).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                playerButtons.get(finalButtonNumber).setText(Players.getEntry(selectedPlayers.get(finalButtonNumber)).getName());
                playerButtons.get(finalButtonNumber).setBackground(getButton(R.id.oppgoal_button).getBackground());
                RippleDrawable r = ((RippleDrawable) getButton(R.id.oppgoal_button).getBackground());
                gameTime.removeTask("exclusiontimer" + finalButtonNumber);
                game.unevenHome = false;
                playerButtons.get(finalButtonNumber).setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return playerButton_Pressed(finalButtonNumber, v, event);
                    }
                });
                return false;
            }
        });
        eventMenu.dismiss();
    }

    private void eventMenu_blockButton_Pressed(int buttonNumber, View v) {
        AddGameEvent(new GenericPlayerEvent(GenericPlayerEvent.GenericEventType.BLOCK, Players.getEntry(selectedPlayers.get(buttonNumber))));
        eventMenu.dismiss();
    }

    private void eventMenu_badpassButton_Pressed(int buttonNumber, View v) {
        AddGameEvent(new GenericPlayerEvent(GenericPlayerEvent.GenericEventType.BADPASS, Players.getEntry(selectedPlayers.get(buttonNumber))));
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
        RecyclerView r = ((RecyclerView)content.findViewById(R.id.subsitution_player_recycleview));
        r.setLayoutManager(new LinearLayoutManager(getContext()));
        PlayerSubAdapter adapter = new PlayerSubAdapter(this, Players.getEntry(selectedPlayers.get(buttonNumber)).getName());
        r.setAdapter(adapter);
        content.findViewById(R.id.substitution_goback_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                substitutionMenu.dismiss();
            }
        });

    }

    private void extraMenu_cardButton_Pressed(int buttonNumber, View v) {
        View content = this.getLayoutInflater().inflate(R.layout.popupwindow_misconduct_menu, null);
        misconductMenu.setContentView(content);
        misconductMenu.showAtLocation(v, Gravity.CENTER, 0, 0);
        content.findViewById(R.id.brutality_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                misconductMenu_Button_Pressed(buttonNumber, v, MisconductEvent.MisconductType.BRUTALITY);
                misconductMenu.dismiss();
            }
        });
        content.findViewById(R.id.misconduct_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                misconductMenu_Button_Pressed(buttonNumber, v, MisconductEvent.MisconductType.MISCONDUCT);
                misconductMenu.dismiss();
            }
        });
    }

    private void extraMenu_swimoffButton_Pressed(int buttonNumber, View v) {
        View content = this.getLayoutInflater().inflate(R.layout.popupwindow_swimoff_menu, null);
        swimoffMenu.setContentView(content);
        swimoffMenu.showAtLocation(v, Gravity.CENTER, 0, 0);
        content.findViewById(R.id.swimoff_win_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swimoffMenu_Button_Pressed(buttonNumber, v, SwimoffEvent.SwimoffResult.WIN);
                game.homePossession = true;
                if (game.quarterbreak) {
                    gameTime.resume();
                    game.advanceQuarter();
                    game.quarterbreak = false;
                    game.paused = false;
                    Button qb = getButton(R.id.quarter_button);
                    qb.setText(game.quarter.toString());
                    qb.setBackgroundColor(Color.BLUE);
                }
                swimoffMenu.dismiss();
            }
        });
        content.findViewById(R.id.swimoff_lose_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swimoffMenu_Button_Pressed(buttonNumber, v, SwimoffEvent.SwimoffResult.LOSE);
                game.homePossession = false;
                if (game.quarterbreak) {
                    gameTime.resume();
                    game.advanceQuarter();
                    game.quarterbreak = false;
                    game.paused = false;
                    Button qb = getButton(R.id.quarter_button);
                    qb.setText(game.quarter.toString());
                    qb.setBackgroundColor(Color.BLUE);
                }
                swimoffMenu.dismiss();
            }
        });
    }

    private void extraMenu_penaltyButton_Pressed(int buttonNumber, View v) {
        AddGameEvent(new ShotEvent(ShotEvent.ShotType.GOAL, Players.getEntry(selectedPlayers.get(buttonNumber))));
    }

    private void misconductMenu_Button_Pressed(int buttonNumber, View v, MisconductEvent.MisconductType type) {
        String note = ((EditText)misconductMenu.getContentView().findViewById(R.id.cardnote_edittext)).getText().toString();
        AddGameEvent(new MisconductEvent(Players.getEntry(selectedPlayers.get(buttonNumber)), type, note));
    }

    private void swimoffMenu_Button_Pressed(int buttonNumber, View v, SwimoffEvent.SwimoffResult result) {
        AddGameEvent(new SwimoffEvent(Players.getEntry(selectedPlayers.get(buttonNumber)), result));
    }

    private Button getButton(int id) {
        return (Button)(getView().findViewById(id));
    }

    private TextView getTextView(int id) {
        return (TextView)(getView().findViewById(id));
    }

    private EditText getEditText(int id) {
        return (EditText)(getView().findViewById(id));
    }

    public class PlayerSubAdapter extends RecyclerView.Adapter<InfoViewHolder>
    {
        GameRecordFragment context;
        String playerName;
        ArrayList<String> playerNames = new ArrayList<>();

        public PlayerSubAdapter(GameRecordFragment gameRecordFragment, String playerName) {
            super();
            context = gameRecordFragment;
            this.playerName = playerName;
            for (String s : context.Teams.getSelected().getNameList()) {
                if (!context.selectedPlayers.contains(s)) {
                    playerNames.add(s);
                }
            }
        }

        @Override
        public InfoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.selectable_item, parent, false);
            return new InfoViewHolder(v);
        }

        @Override
        public void onBindViewHolder(InfoViewHolder holder, int position) {
            TeamEntry t = context.Teams.getSelected();
            String name = playerNames.get(position);
            holder.getNameText().setText(name);
            holder.getInfoText().setText(context.Players.getEntry(name).getNumber());
            holder.itemView.setLongClickable(true);
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    substitutionMenu.dismiss();
                    for (int i = 0; i < context.selectedPlayers.size(); i++) {
                        if (context.selectedPlayers.get(i).equals(playerName)) {
                            context.selectedPlayers.set(i, name);
                        }
                    }
                    refreshPlayerButtons();
                    AddGameEvent(new SubEvent(Players.getEntry(name), Players.getEntry(playerName)));
                    return false;
                }
            });
        }

        @Override
        public int getItemCount() {
            return playerNames.size();
        }
    }

}
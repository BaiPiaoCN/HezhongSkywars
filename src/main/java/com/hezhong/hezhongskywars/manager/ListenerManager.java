package com.hezhong.hezhongskywars.manager;

import com.hezhong.hezhongskywars.game.GameListener;
import com.hezhong.hezhongskywars.game.gui.GUIListener;
import com.hezhong.hezhongskywars.listeners.JoinQuitListener;
import com.hezhong.hezhongskywars.setup.SetupListener;

public class ListenerManager {
    public static GameListener gameListener;
    public static JoinQuitListener joinQuitListener;
    public static SetupListener setupListener;
    public static GUIListener guiListener;
}

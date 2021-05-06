package v;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class receiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case "com.mayank.vaishnavasongs.playpause1":
                h.callBacks.playPause();
                break;
            case "com.mayank.vaishnavasongs.next1":
                h.callBacks.playNext();
                break;
            case "com.mayank.vaishnavasongs.prev1":
                h.callBacks.playPrevious();
                break;
            case "com.mayank.vaishnavasongs.like1":
                h.callBacks.setLike(-1);
                break;
            case "com.mayank.vaishnavasongs.share1":
                h.callBacks.shareSong(-1);
                break;
            case "com.mayank.vaishnavasongs.play_pause_chant_with_sp1":
                h.callBacks.playPauseChantWithSP();
                break;
            case "com.mayank.vaishnavasongs.pause_chant_with_sp1":
                h.callBacks.stopChantWithSP();
                break;
            case "com.mayank.vaishnavasongs.sp_chant_vol_down1":
                h.callBacks.adjustSPChantVol(false);
                break;
            case "com.mayank.vaishnavasongs.sp_chant_vol_up1":
                h.callBacks.adjustSPChantVol(true);
                break;
        }
    }
}
